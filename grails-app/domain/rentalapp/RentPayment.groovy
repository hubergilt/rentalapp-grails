package rentalapp

/**
 * Maps onto the EXISTING `rent_payments` table. Verified directly against
 * the real schema dump (rentaldb.sql).
 *
 * `tenant_id`/`room_id` are genuinely nullable at the DB level (DEFAULT
 * NULL, no NOT NULL) — kept nullable here to match, even though in
 * practice most rows will have both set.
 *
 * `period`      - first day of the month the payment COVERS (billing period)
 * `deposit_date`- the actual payment timestamp (legacy column name, kept
 *                 as-is since renaming it is a job for a future Flyway
 *                 migration, not for this app)
 * `remarks`     - free-text note, required (NOT NULL) at the DB level
 */
class RentPayment {

    Tenant tenant
    Room room

    Date period
    BigDecimal amount
    Date paymentDate
    String remarks

    Date createdAt
    Date updatedAt

    static constraints = {
        tenant      nullable: true
        room        nullable: true
        period      nullable: false
        amount      scale: 2, min: 0.01G
        paymentDate nullable: false
        remarks     blank: false, maxSize: 100
        createdAt   nullable: true, editable: false, display: false
        updatedAt   nullable: true, editable: false, display: false
    }

    static mapping = {
        table 'rent_payments'
        version false
        id          column: 'id'
        tenant      column: 'tenant_id'
        room        column: 'room_id'
        period      column: 'period'
        amount      column: 'amount'
        paymentDate column: 'deposit_date'
        remarks     column: 'remarks'
        createdAt   column: 'created_at', insertable: false, updateable: false
        updatedAt   column: 'updated_at', insertable: false, updateable: false
    }

    String toString() {
        "${tenant} / ${room} — ${period} : ${amount}"
    }
}
