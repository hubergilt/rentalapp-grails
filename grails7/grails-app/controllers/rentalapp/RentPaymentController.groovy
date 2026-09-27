package rentalapp

import grails.gorm.transactions.Transactional
import static org.springframework.http.HttpStatus.*

/**
 * Append-only ledger screen for `rent_payments`.
 *
 * Originally `static scaffold = RentPayment` (Grails' dynamic scaffolding).
 * Under Grails 7, dynamic scaffolding's view resolution produces a
 * malformed internal forward path and recurses infinitely
 * (StackOverflowError) — a framework-level regression discovered while
 * migrating this project, not something specific to this domain class.
 * Converted to a real, generated-style controller + views (same pattern as
 * TenantController) to sidestep it entirely; behavior is otherwise
 * identical to what the scaffolding provided — including the
 * column-sorting/query-param filtering the README describes (e.g.
 * ?tenant.id=3), which Grails' list(params) already handles natively.
 */
class RentPaymentController {

    static allowedMethods = [save: 'POST', update: 'PUT', delete: 'DELETE']

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def rentPaymentList = RentPayment.list(params)
        def rentPaymentCount = RentPayment.count()
        respond rentPaymentList, model: [rentPaymentList: rentPaymentList, rentPaymentCount: rentPaymentCount]
    }

    def show(RentPayment rentPayment) {
        if (rentPayment == null) {
            notFound()
            return
        }
        respond rentPayment
    }

    def create() {
        respond new RentPayment(params)
    }

    @Transactional
    def save(RentPayment rentPayment) {
        if (rentPayment == null) {
            notFound()
            return
        }

        if (rentPayment.hasErrors()) {
            respond rentPayment.errors, view: 'create'
            return
        }

        rentPayment.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'rentPayment.label', default: 'RentPayment'), rentPayment.id])
                redirect rentPayment
            }
            '*' { respond rentPayment, [status: CREATED] }
        }
    }

    def edit(RentPayment rentPayment) {
        if (rentPayment == null) {
            notFound()
            return
        }
        respond rentPayment
    }

    @Transactional
    def update(RentPayment rentPayment) {
        if (rentPayment == null) {
            notFound()
            return
        }

        if (rentPayment.hasErrors()) {
            respond rentPayment.errors, view: 'edit'
            return
        }

        rentPayment.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'rentPayment.label', default: 'RentPayment'), rentPayment.id])
                redirect rentPayment
            }
            '*' { respond rentPayment, [status: OK] }
        }
    }

    @Transactional
    def delete(RentPayment rentPayment) {
        if (rentPayment == null) {
            notFound()
            return
        }

        rentPayment.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'rentPayment.label', default: 'RentPayment'), rentPayment.id])
                redirect action: 'index', method: 'GET'
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'rentPayment.label', default: 'RentPayment'), params.id])
                redirect action: 'index', method: 'GET'
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
