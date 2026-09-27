package rentalapp

import grails.gorm.transactions.Transactional
import grails.converters.JSON
import static org.springframework.http.HttpStatus.*

/**
 * Full CRUD for `tenancies`, plus inline (AJAX) management of that tenancy's
 * `security_deposits` installments — the nested-form requirement from the
 * spec. The installments panel lives in tenancy/_deposits.gsp and is
 * refreshed in place after add/remove via a small fetch() call, no full
 * page reload and no separate custom frontend framework.
 */
class TenancyController {

    static allowedMethods = [
        save: 'POST', update: 'PUT', delete: 'DELETE',
        addDeposit: 'POST', removeDeposit: 'DELETE'
    ]

    def index(String status, Long roomId, Long tenantId, Integer max) {
        params.max = Math.min(max ?: 10, 100)

        def criteria = Tenancy.createCriteria()
        def tenancyList = criteria.list(max: params.max, offset: params.offset ?: 0) {
            if (status == 'active')  { isNull('endDate') }
            if (status == 'ended')   { isNotNull('endDate') }
            if (roomId)              { eq('room', Room.get(roomId)) }
            if (tenantId)            { eq('tenant', Tenant.get(tenantId)) }
            order('startDate', 'desc')
        }

        respond tenancyList, model: [
            tenancyList: tenancyList,
            tenancyCount: tenancyList.totalCount,
            status: status, roomId: roomId, tenantId: tenantId,
            rooms: Room.list(sort: 'name'),
            tenants: Tenant.list(sort: 'paternalSurname')
        ]
    }

    def show(Tenancy tenancy) {
        respond tenancy, model: [tenancy: tenancy, deposits: tenancy?.securityDeposits ?: []]
    }

    def create() {
        def tenancy = new Tenancy(params)
        respond tenancy, model: [tenancy: tenancy, rooms: Room.list(sort: 'name'), tenants: Tenant.list(sort: 'paternalSurname')]
    }

    @Transactional
    def save(Tenancy tenancy) {
        if (tenancy == null) { notFound(); return }
        if (tenancy.hasErrors()) {
            respond tenancy.errors, view: 'create', model: [tenancy: tenancy, rooms: Room.list(sort: 'name'), tenants: Tenant.list(sort: 'paternalSurname')]
            return
        }
        tenancy.save flush: true
        flash.message = "Tenancy #${tenancy.id} created."
        redirect action: 'show', id: tenancy.id
    }

    def edit(Tenancy tenancy) {
        respond tenancy, model: [tenancy: tenancy, rooms: Room.list(sort: 'name'), tenants: Tenant.list(sort: 'paternalSurname')]
    }

    @Transactional
    def update(Tenancy tenancy) {
        if (tenancy == null) { notFound(); return }
        if (tenancy.hasErrors()) {
            respond tenancy.errors, view: 'edit', model: [tenancy: tenancy, rooms: Room.list(sort: 'name'), tenants: Tenant.list(sort: 'paternalSurname')]
            return
        }
        tenancy.save flush: true
        flash.message = "Tenancy #${tenancy.id} updated."
        redirect action: 'show', id: tenancy.id
    }

    @Transactional
    def delete(Tenancy tenancy) {
        if (tenancy == null) { notFound(); return }

        if (SecurityDeposit.countByTenancy(tenancy) > 0) {
            flash.message = "Cannot delete tenancy #${tenancy.id} — it still has security deposit installments on file. Remove those first."
            redirect action: 'show', id: tenancy.id
            return
        }

        tenancy.delete flush: true
        flash.message = "Tenancy #${tenancy.id} deleted."
        redirect action: 'index'
    }

    // -------------------------------------------------------------------
    // Inline / AJAX: security deposit installments, edited from the
    // Tenancy show screen without navigating away.
    // -------------------------------------------------------------------

    @Transactional
    def addDeposit(Long tenancyId, BigDecimal amount, Date paidDate) {
        Tenancy tenancy = Tenancy.get(tenancyId)
        if (!tenancy) { render(status: NOT_FOUND); return }

        SecurityDeposit deposit = new SecurityDeposit(
            tenancy: tenancy,
            amount: amount,
            paidDate: paidDate ?: new Date()
        )

        if (!deposit.save(flush: true)) {
            render(status: UNPROCESSABLE_ENTITY,
                   text: deposit.errors.allErrors.collect { message(error: it) }.join('; '))
            return
        }

        tenancy.refresh()
        render(view: '_deposits', model: [tenancy: tenancy, deposits: tenancy.securityDeposits])
    }

    @Transactional
    def removeDeposit(Long tenancyId, Long depositId) {
        Tenancy tenancy = Tenancy.get(tenancyId)
        SecurityDeposit deposit = SecurityDeposit.get(depositId)

        if (!tenancy || !deposit || deposit.tenancy?.id != tenancy.id) {
            render(status: NOT_FOUND)
            return
        }

        deposit.delete(flush: true)
        tenancy.refresh()
        render(view: '_deposits', model: [tenancy: tenancy, deposits: tenancy.securityDeposits])
    }

    protected void notFound() {
        flash.message = 'Tenancy not found.'
        redirect action: 'index'
    }
}
