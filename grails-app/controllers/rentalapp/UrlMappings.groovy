package rentalapp

class UrlMappings {

    static mappings = {
        "/"(controller: 'login', action: 'auth')

        // NOTE: these MUST be double-quoted GStrings, not single-quoted
        // strings. Grails' URL Mappings DSL only recognizes $action/$id as
        // dynamic placeholders inside a GString — in a single-quoted
        // string '$action' is just literal text, which silently produces a
        // dead route (e.g. a login form posting to the literal path
        // "/login/$action" instead of "/login/authenticate").
        "/tenant/$action?/$id?(.$format)?"(controller: 'tenant')
        "/room/$action?/$id?(.$format)?"(controller: 'room')
        "/tenancy/$action?/$id?(.$format)?"(controller: 'tenancy')
        "/rentPayment/$action?/$id?(.$format)?"(controller: 'rentPayment')
        "/securityDeposit/$action?/$id?(.$format)?"(controller: 'securityDeposit')
        "/currentTenancy/$action?/$id?(.$format)?"(controller: 'currentTenancy')
        "/login/$action?"(controller: 'login')

        "404"(view: '/notFound')
        "500"(view: '/error')
    }
}
