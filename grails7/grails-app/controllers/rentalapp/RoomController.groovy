package rentalapp

import grails.gorm.transactions.Transactional
import static org.springframework.http.HttpStatus.*

/**
 * Full CRUD screen for `rooms`.
 *
 * Originally `static scaffold = Room` (Grails' dynamic scaffolding). Under
 * Grails 7, dynamic scaffolding's view resolution produces a malformed
 * internal forward path and recurses infinitely (StackOverflowError) — a
 * framework-level regression discovered while migrating this project, not
 * something specific to this domain class. Converted to a real, generated-
 * style controller + views (same pattern as TenantController) to sidestep
 * it entirely; behavior is otherwise identical to what the scaffolding
 * provided (list/show/create/edit/delete, sortable/paginated list).
 */
class RoomController {

    static allowedMethods = [save: 'POST', update: 'PUT', delete: 'DELETE']

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def roomList = Room.list(params)
        def roomCount = Room.count()
        respond roomList, model: [roomList: roomList, roomCount: roomCount]
    }

    def show(Room room) {
        if (room == null) {
            notFound()
            return
        }
        respond room
    }

    def create() {
        respond new Room(params)
    }

    @Transactional
    def save(Room room) {
        if (room == null) {
            notFound()
            return
        }

        if (room.hasErrors()) {
            respond room.errors, view: 'create'
            return
        }

        room.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'room.label', default: 'Room'), room.id])
                redirect room
            }
            '*' { respond room, [status: CREATED] }
        }
    }

    def edit(Room room) {
        if (room == null) {
            notFound()
            return
        }
        respond room
    }

    @Transactional
    def update(Room room) {
        if (room == null) {
            notFound()
            return
        }

        if (room.hasErrors()) {
            respond room.errors, view: 'edit'
            return
        }

        room.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'room.label', default: 'Room'), room.id])
                redirect room
            }
            '*' { respond room, [status: OK] }
        }
    }

    @Transactional
    def delete(Room room) {
        if (room == null) {
            notFound()
            return
        }

        // Mirrors the DB-level FK behaviour (ON DELETE RESTRICT on both
        // tenancies.room_id and rent_payments.room_id): fail loudly and
        // helpfully instead of letting a raw SQL exception bubble up.
        if (Tenancy.countByRoom(room) > 0 || RentPayment.countByRoom(room) > 0) {
            flash.message = "Cannot delete ${room} — it still has tenancies or rent payments on file. Remove those first."
            redirect action: 'show', id: room.id
            return
        }

        room.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'room.label', default: 'Room'), room.id])
                redirect action: 'index', method: 'GET'
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'room.label', default: 'Room'), params.id])
                redirect action: 'index', method: 'GET'
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
