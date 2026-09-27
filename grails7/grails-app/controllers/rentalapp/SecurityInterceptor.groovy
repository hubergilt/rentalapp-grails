package rentalapp

/**
 * Applies to every controller/action in the app EXCEPT LoginController.
 * Unauthenticated requests are bounced to the login form — this is what
 * makes the login screen double as the gateway into all the scaffolded
 * CRUD/admin screens, per the requirements.
 */
class SecurityInterceptor {

    SecurityInterceptor() {
        // Excluding by controller name (`excludes(controller: 'login')`)
        // turned out to be unreliable specifically under WAR deployment —
        // it worked fine under bootRun, but under Tomcat the exclusion
        // silently failed to match, causing this interceptor to fire on
        // /login/auth itself and redirect it back to... /login/auth,
        // an infinite loop. Matching on the literal URI instead sidesteps
        // whatever's different about Grails' controller-name resolution
        // between the two entry points, since it only looks at the raw
        // request path (already context-path-relative, same as UrlMappings).
        matchAll()
            .excludes(uri: '/login/**')
            // Static assets must load on the login page itself (a logged-out
            // page) — URI-based matching, unlike the old controller-name
            // exclusion, apparently does catch requests served by Spring's
            // static ResourceHttpRequestHandler (WebConfig.groovy), so these
            // need their own explicit exclusion or every CSS/JS/image
            // request gets redirected to /login/auth instead of the asset.
            .excludes(uri: '/css/**')
            .excludes(uri: '/js/**')
            .excludes(uri: '/images/**')
    }

    boolean before() {
        if (session.rentalappUser) {
            return true
        }
        flash.message = 'Please log in to continue.'
        redirect controller: 'login', action: 'auth'
        false
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
