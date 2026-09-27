package rentalapp

/**
 * Read-only screen over the `current_tenancies` view ("who lives where right
 * now"). Deliberately exposes ONLY index/show — there is no
 * create/save/edit/update/delete action, and no route to reach one, since
 * this is a reporting view, not an editable table.
 */
class CurrentTenancyController {

    static allowedMethods = [index: 'GET', show: 'GET']

    def index(String q, Integer max) {
        params.max = Math.min(max ?: 20, 100)

        def criteria = CurrentTenancy.createCriteria()
        def list = criteria.list(max: params.max, offset: params.offset ?: 0) {
            if (q) {
                or {
                    ilike('name', "%${q}%")
                    ilike('firstNames', "%${q}%")
                    ilike('paternalSurname', "%${q}%")
                    ilike('maternalSurname', "%${q}%")
                }
            }
            order('name', 'asc')
        }

        respond list, model: [currentTenancyList: list, currentTenancyCount: list.totalCount, q: q]
    }

    def show(CurrentTenancy currentTenancy) {
        respond currentTenancy
    }
}
