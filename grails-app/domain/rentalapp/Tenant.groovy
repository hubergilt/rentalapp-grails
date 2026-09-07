package rentalapp

/**
 * Maps onto the EXISTING `tenants` table. Verified directly against the
 * real schema dump (rentaldb.sql) — three separate name parts (common in
 * Latin American naming conventions: first name(s), paternal surname,
 * maternal surname), no email/phone columns at all.
 */
class Tenant {

    String firstNames
    String paternalSurname
    String maternalSurname
    String nationalId

    Date createdAt
    Date updatedAt

    static hasMany = [
        tenancies    : Tenancy,
        rentPayments : RentPayment
    ]

    static constraints = {
        firstNames       blank: false, maxSize: 45
        paternalSurname  blank: false, maxSize: 45
        maternalSurname  blank: false, maxSize: 45
        nationalId       blank: false, unique: true, maxSize: 8
        createdAt        nullable: true, editable: false, display: false
        updatedAt        nullable: true, editable: false, display: false
    }

    static mapping = {
        table 'tenants'
        version false
        id               column: 'id'
        firstNames       column: 'first_names'
        paternalSurname  column: 'paternal_surname'
        maternalSurname  column: 'maternal_surname'
        nationalId       column: 'national_id'
        createdAt        column: 'created_at', insertable: false, updateable: false
        updatedAt        column: 'updated_at', insertable: false, updateable: false
        tenancies        sort: 'startDate', order: 'desc'
    }

    String getFullName() {
        "${firstNames} ${paternalSurname} ${maternalSurname}"
    }

    String getSelectLabel() {
        "${paternalSurname}, ${firstNames}"
    }

    String toString() {
        "${fullName} (${nationalId})"
    }
}
