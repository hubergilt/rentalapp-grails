package rentalapp

/**
 * Maps onto the EXISTING `rooms` table. Verified directly against the real
 * schema dump (rentaldb.sql) — the room's label column is `name`, plus a
 * separate `floor` column; there is no `room_number` column.
 */
class Room {

    String name
    String floor
    BigDecimal price
    String status   // 'available' | 'occupied' | 'maintenance' — DB-level ENUM

    Date createdAt
    Date updatedAt

    static hasMany = [
        tenancies    : Tenancy,
        rentPayments : RentPayment
    ]

    static constraints = {
        name       blank: false, maxSize: 45
        floor      blank: false, maxSize: 20
        price      scale: 2, min: 0.01G
        status     inList: ['available', 'occupied', 'maintenance']
        createdAt  nullable: true, editable: false, display: false
        updatedAt  nullable: true, editable: false, display: false
    }

    static mapping = {
        table 'rooms'
        version false
        id         column: 'id'
        name       column: 'name'
        floor      column: 'floor'
        price      column: 'price'
        status     column: 'status'
        createdAt  column: 'created_at', insertable: false, updateable: false
        updatedAt  column: 'updated_at', insertable: false, updateable: false
    }

    String toString() {
        "Room ${name} (floor ${floor}, ${status})"
    }
}
