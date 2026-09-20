package rentalapp

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

/**
 * Simple, dependency-free form-based login. This is the front door for
 * every scaffolded CRUD screen (see SecurityInterceptor) — there is no
 * separate hand-built landing page; logging in IS the entry point, and the
 * post-login layout's nav bar IS the app's navigation (main.gsp).
 *
 * Credentials are never hardcoded: ADMIN_USERNAME / ADMIN_PASSWORD_HASH come
 * from the environment (.env / application.yml), and only a bcrypt hash of
 * the password is ever stored — see README for how to generate one.
 */
class LoginController {

    static defaultAction = 'auth'

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder()

    /**
     * Root ("/") maps here (see UrlMappings) instead of straight to auth().
     * This does nothing but issue a real HTTP redirect — no view rendering
     * — specifically so auth()'s render(view: 'auth') only ever runs from a
     * genuine /login/auth request. See the comment in UrlMappings.groovy
     * for why that distinction matters.
     *
     * `redirect(uri: ...)` is used rather than `redirect(controller:
     * 'login', action: 'auth')` — the latter reverse-maps through
     * UrlMappings, which could just as easily resolve back to "/" itself
     * (since both map to the same controller+action), recreating the exact
     * loop this exists to avoid. An explicit uri sidesteps that ambiguity;
     * Grails still prepends the app's context path automatically.
     */
    def index() {
        redirect uri: '/login/auth'
    }

    def auth() {
        if (session.rentalappUser) {
            redirect controller: 'tenant', action: 'index'
            return
        }
        render(view: 'auth')
    }

    def authenticate(String username, String password) {
        String expectedUser = grailsApplication.config.getProperty('rentalapp.auth.adminUsername')
        String expectedHash = grailsApplication.config.getProperty('rentalapp.auth.adminPasswordHash')

        boolean ok = username && password && expectedHash &&
                     username == expectedUser &&
                     ENCODER.matches(password, expectedHash)

        if (ok) {
            session.rentalappUser = username
            redirect controller: 'tenant', action: 'index'
        } else {
            flash.message = 'Invalid username or password.'
            redirect action: 'auth'
        }
    }

    def logout() {
        session.invalidate()
        redirect action: 'auth'
    }
}
