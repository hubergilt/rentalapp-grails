package rentalapp

/**
 * Rooms don't need custom search/filter logic beyond what Grails' own
 * dynamic scaffolding already provides (list/show/create/edit/delete with
 * sortable columns and pagination). `static scaffold = true` generates the
 * controller actions AND the views at runtime — nothing here to hand-edit,
 * which is the point: the schema (and therefore the fields) is Flyway's.
 */
class RoomController {
    static scaffold = Room
}
