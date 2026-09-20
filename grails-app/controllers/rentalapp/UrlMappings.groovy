package rentalapp

class UrlMappings {

    static mappings = {
        // Root deliberately does NOT map straight to the `auth` action.
        // `auth()` calls render(view: 'auth'), and Grails' relative view
        // resolution gets confused when that render happens from a request
        // whose own resolved URI is the bare "/" (zero path segments) —
        // under WAR deployment specifically, it asks Spring to render a
        // literal view named "/login/auth" instead of resolving it
        // relative to the login controller, which fails and manifests as
        // an infinite redirect loop via Spring Boot's ErrorPageFilter.
        // Routing through `index` (a plain redirect, no rendering) sidesteps
        // it entirely — by the time `auth()` actually renders, the request
        // is a real /login/auth hit with real path segments to resolve
        // against.
        "/"(controller: 'login', action: 'index')

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
