package rentalapp

/**
 * Maps onto the EXISTING `tenancies` table (added in V6; refund fields moved
 * here from security_deposits in V10, since a refund is one event per
 * tenancy, not per installment). `endDate == null` means the tenancy is
 * still active — see the `current_tenancies` view / CurrentTenancy.groovy.
 */
class Tenancy {

    Tenant tenant
    Room room

    Date startDate
    Date endDate                 // null == still active

    Date depositRefundDate
    BigDecimal depositRefundAmount

    Date createdAt
    Date updatedAt

    static hasMany = [
        securityDeposits: SecurityDeposit
    ]

    static constraints = {
        tenant                nullable: false
        room                  nullable: false
        startDate             nullable: false
        endDate               nullable: true, validator: { Date end, Tenancy instance ->
            if (end && instance.startDate && end < instance.startDate) {
                return ['endDate.beforeStartDate']
            }
        }
        depositRefundDate     nullable: true
        depositRefundAmount   nullable: true, scale: 2, min: 0.0G
        createdAt             nullable: true, editable: false, display: false
        updatedAt             nullable: true, editable: false, display: false
    }

    static mapping = {
        table 'tenancies'
        version false
        id                  column: 'id'
        tenant              column: 'tenant_id'
        room                column: 'room_id'
        startDate           column: 'start_date'
        endDate             column: 'end_date'
        depositRefundDate   column: 'deposit_refund_date'
        depositRefundAmount column: 'deposit_refund_amount'
        createdAt           column: 'created_at', insertable: false, updateable: false
        updatedAt           column: 'updated_at', insertable: false, updateable: false
        securityDeposits    sort: 'paidDate', order: 'asc'
    }

    boolean isActive() {
        endDate == null
    }

    BigDecimal getTotalDepositPaid() {
        securityDeposits?.sum { it.amount } ?: 0.0G
    }

    String toString() {
        "${tenant} @ ${room?.name} (${isActive() ? 'active' : 'ended'})"
    }
}
