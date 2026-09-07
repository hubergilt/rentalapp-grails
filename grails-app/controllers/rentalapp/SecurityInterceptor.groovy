package rentalapp

/**
 * Applies to every controller/action in the app EXCEPT LoginController.
 * Unauthenticated requests are bounced to the login form — this is what
 * makes the login screen double as the gateway into all the scaffolded
 * CRUD/admin screens, per the requirements.
 */
class SecurityInterceptor {

    SecurityInterceptor() {
        matchAll().excludes(controller: 'login')
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
