package rentalapp

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

/**
 * Entry point for rentalapp.
 *
 * `.env` is loaded and copied into JVM system properties ourselves (see
 * loadDotEnv() below) — deliberately not delegated to a third-party
 * auto-detecting library, since that turned out to be unreliable depending
 * on the working directory `./gradlew bootRun` launches the forked JVM
 * from. This way it's simple, explicit, and easy to debug: the line printed
 * at startup tells you exactly how many values were picked up.
 *
 * Everything else — which database engine is active, its JDBC driver class,
 * Hibernate dialect, and connection URL — is decided entirely in
 * grails-app/conf/application.yml, as a set of commented-out dataSource
 * blocks (one per engine). To switch engines, comment out the active block
 * there and uncomment the one you want; nothing here needs to change.
 * .env only supplies the connection details (host, port, credentials, etc.)
 * that whichever block is active substitutes in via ${DB_HOST:...}-style
 * placeholders.
 *
 * The schema itself is never created or altered by Grails/GORM — see
 * application.yml (dbCreate: none) and the README for how Flyway (external,
 * from the rentaldb project) owns migrations.
 */
class Application extends GrailsAutoConfiguration {

    static void main(String[] args) {
        loadDotEnv()
        GrailsApp.run(Application, args)
    }

    /**
     * Reads a `.env` file (KEY=VALUE per line, '#' comments — full-line or
     * trailing after an unquoted value — and optional single or double
     * quotes around the value) from the current working directory and
     * copies each entry into a JVM system property of the same name —
     * which is exactly where application.yml's `${DB_HOST:...}`-style
     * placeholders look. Real OS environment variables (export FOO=bar)
     * still take priority if both are set, since those are usually a
     * deliberate override (e.g. in CI).
     *
     * Silently does nothing if no `.env` file is found — that's expected
     * for e.g. `./gradlew test`, or a production box using real env vars.
     */
    static void loadDotEnv() {
        File dotEnv = new File(System.getProperty('user.dir'), '.env')
        if (!dotEnv.exists()) {
            println "[rentalapp] No .env file found at ${dotEnv.absolutePath} — relying on real environment variables / -D system properties only."
            return
        }

        int loaded = 0
        dotEnv.eachLine { rawLine ->
            String line = rawLine.trim()
            if (!line || line.startsWith('#')) return

            int eq = line.indexOf('=')
            if (eq <= 0) return

            String key = line.substring(0, eq).trim()
            String value = line.substring(eq + 1).trim()

            if (value.length() >= 2 &&
                ((value.charAt(0) == '"' as char && value.charAt(value.length() - 1) == '"' as char) ||
                 (value.charAt(0) == '\'' as char && value.charAt(value.length() - 1) == '\'' as char))) {
                // Quoted value: strip one layer of matching surrounding
                // quotes. Everything inside — including a literal '#' — is
                // kept as-is (e.g. a password that happens to contain one).
                value = value.substring(1, value.length() - 1)
            } else {
                // Unquoted value: a ' #' (whitespace then hash) starts an
                // inline comment, same convention docker compose/python-dotenv
                // use — e.g. `DB_HOST=ora01.ad.lab   # internal DNS only`
                // must not swallow the comment into the value.
                int commentAt = value.indexOf(' #')
                if (commentAt >= 0) {
                    value = value.substring(0, commentAt).trim()
                }
            }

            if (!value) return

            // Real OS env vars win over .env — treat .env as a dev-only default.
            if (System.getenv(key)) return

            System.setProperty(key, value)
            loaded++
        }
        println "[rentalapp] Loaded ${loaded} value(s) from ${dotEnv.absolutePath}"
    }
}
