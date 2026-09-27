package rentalapp

/**
 * Maps onto the EXISTING `security_deposits` table — reworked in V10 into a
 * proper installment ledger: multiple rows per `tenancy_id` are normal
 * (partial deposit payments). Refund tracking lives on `Tenancy`, not here.
 *
 * This is the entity edited inline from the Tenancy show/edit screen via the
 * nested/AJAX installment widget — see TenancyController + tenancy/show.gsp.
 */
class SecurityDeposit {

    Tenancy tenancy

    BigDecimal amount
    Date paidDate
    String remarks

    Date createdAt
    Date updatedAt

    static belongsTo = [tenancy: Tenancy]

    static constraints = {
        tenancy   nullable: false
        amount    scale: 2, min: 0.01G
        paidDate  nullable: false
        remarks   nullable: true, maxSize: 100
        createdAt nullable: true, editable: false, display: false
        updatedAt nullable: true, editable: false, display: false
    }

    static mapping = {
        table 'security_deposits'
        version false
        id        column: 'id'
        tenancy   column: 'tenancy_id'
        amount    column: 'amount'
        paidDate  column: 'paid_date'
        remarks   column: 'remarks'
        createdAt column: 'created_at', insertable: false, updateable: false
        updatedAt column: 'updated_at', insertable: false, updateable: false
    }

    String toString() {
        "Installment ${amount} on ${paidDate} (tenancy #${tenancy?.id})"
    }
}
