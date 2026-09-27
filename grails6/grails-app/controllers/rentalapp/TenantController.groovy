package rentalapp

import grails.gorm.transactions.Transactional
import static org.springframework.http.HttpStatus.*

/**
 * Full CRUD screen for `tenants`, generated against the live schema
 * (grails generate-controller / generate-views target Tenant.groovy), then
 * hand-customized only for search/filter on the list screen — the actual
 * list/show/create/edit fields are still the scaffolded ones.
 */
class TenantController {

    static allowedMethods = [save: 'POST', update: 'PUT', delete: 'DELETE']

    def index(String q, String status, Integer max) {
        params.max = Math.min(max ?: 10, 100)

        def criteria = Tenant.createCriteria()
        def tenantList = criteria.list(max: params.max, offset: params.offset ?: 0) {
            if (q) {
                or {
                    ilike('firstNames', "%${q}%")
                    ilike('paternalSurname', "%${q}%")
                    ilike('maternalSurname', "%${q}%")
                    ilike('nationalId', "%${q}%")
                }
            }
            order('paternalSurname', 'asc')
            order('firstNames', 'asc')
        }

        respond tenantList, model: [tenantList: tenantList, tenantCount: tenantList.totalCount, q: q]
    }

    def show(Tenant tenant) {
        if (tenant == null) {
            notFound()
            return
        }
        respond tenant
    }

    def create() {
        respond new Tenant(params)
    }

    @Transactional
    def save(Tenant tenant) {
        if (tenant == null) {
            notFound()
            return
        }

        if (tenant.hasErrors()) {
            respond tenant.errors, view: 'create'
            return
        }

        tenant.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'tenant.label', default: 'Tenant'), tenant.id])
                redirect tenant
            }
            '*' { respond tenant, [status: CREATED] }
        }
    }

    def edit(Tenant tenant) {
        if (tenant == null) {
            notFound()
            return
        }
        respond tenant
    }

    @Transactional
    def update(Tenant tenant) {
        if (tenant == null) {
            notFound()
            return
        }

        if (tenant.hasErrors()) {
            respond tenant.errors, view: 'edit'
            return
        }

        tenant.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'tenant.label', default: 'Tenant'), tenant.id])
                redirect tenant
            }
            '*' { respond tenant, [status: OK] }
        }
    }

    @Transactional
    def delete(Tenant tenant) {
        if (tenant == null) {
            notFound()
            return
        }

        // Mirrors the DB-level FK behaviour (ON DELETE RESTRICT): fail loudly
        // and helpfully instead of letting a raw SQL exception bubble up.
        if (Tenancy.countByTenant(tenant) > 0 || RentPayment.countByTenant(tenant) > 0) {
            flash.message = "Cannot delete ${tenant} — it still has tenancies or rent payments on file. Remove those first."
            redirect action: 'show', id: tenant.id
            return
        }

        tenant.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'tenant.label', default: 'Tenant'), tenant.id])
                redirect action: 'index', method: 'GET'
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'tenant.label', default: 'Tenant'), params.id])
                redirect action: 'index', method: 'GET'
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
