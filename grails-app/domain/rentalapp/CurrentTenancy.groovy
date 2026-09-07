package rentalapp

/**
 * Maps onto the `current_tenancies` VIEW. Verified directly against the
 * real schema dump (rentaldb.sql):
 *
 *   CREATE VIEW current_tenancies AS
 *   SELECT r.id AS room_id, r.name, r.floor, r.status,
 *          t.id AS tenant_id, t.first_names, t.paternal_surname, t.maternal_surname,
 *          tc.start_date
 *   FROM rooms r
 *   LEFT JOIN tenancies tc ON tc.room_id = r.id AND tc.end_date IS NULL
 *   LEFT JOIN tenants t ON t.id = tc.tenant_id
 *
 * It's a LEFT JOIN from rooms — meaning there is ALWAYS one row per room,
 * even vacant ones (tenant_id/first_names/.../start_date will simply be
 * null for a vacant room). room_id is therefore the natural, unique key for
 * this view, used as the domain id below.
 *
 * READ ONLY at every level — see CurrentTenancyController, which only
 * exposes index/show (no save/update/delete action exists).
 */
class CurrentTenancy implements Serializable {

    Long roomId
    String name
    String floor
    String status
    Long tenantId
    String firstNames
    String paternalSurname
    String maternalSurname
    Date startDate

    static constraints = {
        roomId          nullable: true
        name            nullable: true
        floor           nullable: true
        status          nullable: true
        tenantId        nullable: true
        firstNames      nullable: true
        paternalSurname nullable: true
        maternalSurname nullable: true
        startDate       nullable: true
    }

    static mapping = {
        table 'current_tenancies'
        version false
        id               name: 'roomId', column: 'room_id', generator: 'assigned'
        name             column: 'name'
        floor            column: 'floor'
        status           column: 'status'
        tenantId         column: 'tenant_id'
        firstNames       column: 'first_names'
        paternalSurname  column: 'paternal_surname'
        maternalSurname  column: 'maternal_surname'
        startDate        column: 'start_date'
        cache false
    }

    boolean isOccupied() {
        tenantId != null
    }

    String getTenantFullName() {
        occupied ? "${firstNames} ${paternalSurname} ${maternalSurname}" : null
    }

    String toString() {
        occupied ? "${tenantFullName} in room ${name} since ${startDate}" : "Room ${name} — vacant"
    }
}
