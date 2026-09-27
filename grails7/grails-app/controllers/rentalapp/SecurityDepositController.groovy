package rentalapp

import grails.gorm.transactions.Transactional
import static org.springframework.http.HttpStatus.*

/**
 * Standalone, independent-of-a-specific-tenancy ledger screen for
 * `security_deposits` (browsable/auditable across all tenancies). This is
 * separate from — and doesn't affect — the inline, AJAX-refreshed
 * installment widget on TenancyController's show/edit screen, which edits
 * the same table in the context of one tenancy.
 *
 * Originally `static scaffold = SecurityDeposit` (Grails' dynamic
 * scaffolding). Under Grails 7, dynamic scaffolding's view resolution
 * produces a malformed internal forward path and recurses infinitely
 * (StackOverflowError) — a framework-level regression discovered while
 * migrating this project, not something specific to this domain class.
 * Converted to a real, generated-style controller + views (same pattern as
 * TenantController) to sidestep it entirely.
 */
class SecurityDepositController {

    static allowedMethods = [save: 'POST', update: 'PUT', delete: 'DELETE']

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def securityDepositList = SecurityDeposit.list(params)
        def securityDepositCount = SecurityDeposit.count()
        respond securityDepositList, model: [securityDepositList: securityDepositList, securityDepositCount: securityDepositCount]
    }

    def show(SecurityDeposit securityDeposit) {
        if (securityDeposit == null) {
            notFound()
            return
        }
        respond securityDeposit
    }

    def create() {
        respond new SecurityDeposit(params)
    }

    @Transactional
    def save(SecurityDeposit securityDeposit) {
        if (securityDeposit == null) {
            notFound()
            return
        }

        if (securityDeposit.hasErrors()) {
            respond securityDeposit.errors, view: 'create'
            return
        }

        securityDeposit.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'securityDeposit.label', default: 'SecurityDeposit'), securityDeposit.id])
                redirect securityDeposit
            }
            '*' { respond securityDeposit, [status: CREATED] }
        }
    }

    def edit(SecurityDeposit securityDeposit) {
        if (securityDeposit == null) {
            notFound()
            return
        }
        respond securityDeposit
    }

    @Transactional
    def update(SecurityDeposit securityDeposit) {
        if (securityDeposit == null) {
            notFound()
            return
        }

        if (securityDeposit.hasErrors()) {
            respond securityDeposit.errors, view: 'edit'
            return
        }

        securityDeposit.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'securityDeposit.label', default: 'SecurityDeposit'), securityDeposit.id])
                redirect securityDeposit
            }
            '*' { respond securityDeposit, [status: OK] }
        }
    }

    @Transactional
    def delete(SecurityDeposit securityDeposit) {
        if (securityDeposit == null) {
            notFound()
            return
        }

        securityDeposit.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'securityDeposit.label', default: 'SecurityDeposit'), securityDeposit.id])
                redirect action: 'index', method: 'GET'
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'securityDeposit.label', default: 'SecurityDeposit'), params.id])
                redirect action: 'index', method: 'GET'
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
