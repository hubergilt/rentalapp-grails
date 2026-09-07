package rentalapp

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

/**
 * Entry point for rentalapp.
 *
 * Two things happen here before Spring Boot's context is created:
 *
 * 1. `.env` is loaded and copied into JVM system properties ourselves (see
 *    loadDotEnv() below) — deliberately not delegated to a third-party
 *    auto-detecting library, since that turned out to be unreliable
 *    depending on the working directory `./gradlew bootRun` launches the
 *    forked JVM from. This way it's simple, explicit, and easy to debug:
 *    every line printed at startup tells you exactly what was picked up.
 *
 * 2. The datasource is resolved for whichever database engine is active —
 *    JDBC URL, driver class, and Hibernate dialect all come from the single
 *    ENGINE_DATASOURCE_DEFAULTS map below, keyed by DB_ENGINE in .env. This
 *    is the one place that knows "postgresql means org.postgresql.Driver +
 *    PostgreSQLDialect" — grails-app/conf/application.yml no longer needs
 *    hand-edited driver/dialect values when you switch engines; its own
 *    values are just a fallback for if Application.main() is ever bypassed.
 *
 *    Engine selection, highest-priority first:
 *      1. DB_ENGINE   - explicit: mysql | postgresql | oracle | sqlserver
 *      2. DB_PORT     - inferred from the port, for .env files predating
 *                       DB_ENGINE (5432→postgresql, 1521→oracle, 1433→sqlserver)
 *      3. mysql        - default if neither is set
 *
 *    URL precedence (highest wins), all overridable without touching this file:
 *      1. DB_URL           - a fully-formed JDBC URL, used as-is if present
 *      2. DB_SOCKET        - (MySQL only) path to a unix socket, via junixsocket
 *      3. DB_HOST/DB_PORT  - plain TCP, the default for most workstations/CI
 *
 * The schema itself is never created or altered by Grails/GORM — see
 * grails-app/conf/application.yml (dbCreate: none) and the README for how
 * Flyway (external, from the rentaldb project) owns migrations.
 */
class Application extends GrailsAutoConfiguration {

    /**
     * Single source of truth for "what does engine X need". Add a new engine
     * here (plus its JDBC driver in build.gradle) and it's usable via
     * DB_ENGINE without touching application.yml or the URL-building logic's
     * callers.
     */
    private static final Map<String, Map<String, String>> ENGINE_DATASOURCE_DEFAULTS = [
        mysql: [
            driverClassName: 'com.mysql.cj.jdbc.Driver',
            dialect: 'org.hibernate.dialect.MySQL8Dialect',
        ],
        postgresql: [
            driverClassName: 'org.postgresql.Driver',
            dialect: 'org.hibernate.dialect.PostgreSQLDialect',
        ],
        oracle: [
            driverClassName: 'oracle.jdbc.OracleDriver',
            dialect: 'org.hibernate.dialect.OracleDialect',
        ],
        sqlserver: [
            driverClassName: 'com.microsoft.sqlserver.jdbc.SQLServerDriver',
            dialect: 'org.hibernate.dialect.SQLServerDialect',
        ],
    ]

    static void main(String[] args) {
        loadDotEnv()

        String engine = resolveEngine()
        Map<String, String> defaults = ENGINE_DATASOURCE_DEFAULTS[engine]

        System.setProperty('dataSource.url', resolveJdbcUrl(engine))
        System.setProperty('dataSource.driverClassName', defaults.driverClassName)
        System.setProperty('dataSource.dialect', defaults.dialect)

        println "[rentalapp] Database engine: ${engine} " +
                "(driverClassName=${defaults.driverClassName}, dialect=${defaults.dialect})"

        GrailsApp.run(Application, args)
    }

    /**
     * Reads a `.env` file (KEY=VALUE per line, '#' comments, optional single
     * or double quotes around the value) from the current working directory
     * and copies each entry into a JVM system property of the same name —
     * which is exactly where application.yml's `${DB_USERNAME:...}`-style
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

            // Strip one layer of matching surrounding quotes, if present.
            if (value.length() >= 2) {
                char first = value.charAt(0)
                char last = value.charAt(value.length() - 1)
                if ((first == '"' as char && last == '"' as char) ||
                    (first == '\'' as char && last == '\'' as char)) {
                    value = value.substring(1, value.length() - 1)
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

    /** Reads KEY from a real OS env var first, falling back to whatever
     *  loadDotEnv() copied into system properties. */
    static String env(String key) {
        System.getenv(key) ?: System.getProperty(key)
    }

    /**
     * Picks the active engine. DB_ENGINE is authoritative when set; otherwise
     * falls back to inferring it from DB_PORT (for .env files written before
     * DB_ENGINE existed), and finally to mysql.
     */
    static String resolveEngine() {
        String explicit = env('DB_ENGINE')?.toLowerCase()?.trim()
        if (explicit) {
            if (!ENGINE_DATASOURCE_DEFAULTS.containsKey(explicit)) {
                println "[rentalapp] Unknown DB_ENGINE '${explicit}' — falling back to mysql. " +
                        "Valid values: ${ENGINE_DATASOURCE_DEFAULTS.keySet().join(', ')}"
                return 'mysql'
            }
            return explicit
        }

        switch (env('DB_PORT')) {
            case '5432': return 'postgresql'
            case '1521': return 'oracle'
            case '1433': return 'sqlserver'
            default:     return 'mysql'
        }
    }

    static String resolveJdbcUrl(String engine) {
        String explicitUrl = env('DB_URL')
        if (explicitUrl) {
            return explicitUrl
        }

        String dbName   = env('DB_NAME') ?: 'rentaldb'
        String dbHost   = env('DB_HOST') ?: 'localhost'
        String dbSocket = env('DB_SOCKET')
        String dbPort   = env('DB_PORT')

        switch (engine) {
            case 'postgresql':
                // No extra query params needed for a local/dev connection —
                // psql defaults (SSL negotiated automatically, etc.) just
                // work over this URL.
                return "jdbc:postgresql://${dbHost}:${dbPort ?: '5432'}/${dbName}"

            case 'oracle':
                // DB_SERVICE_NAME defaults to DB_NAME if unset.
                String serviceName = env('DB_SERVICE_NAME') ?: dbName
                return "jdbc:oracle:thin:@//${dbHost}:${dbPort ?: '1521'}/${serviceName}"

            case 'sqlserver':
                boolean encrypt   = (env('DB_ENCRYPT') ?: 'true').toBoolean()
                boolean trustCert = (env('DB_TRUST_SERVER_CERTIFICATE') ?: 'true').toBoolean()
                return "jdbc:sqlserver://${dbHost}:${dbPort ?: '1433'};databaseName=${dbName}" +
                       ";encrypt=${encrypt};trustServerCertificate=${trustCert}"

            default: // mysql — also the fallback when DB_PORT/DB_ENGINE are unset
                // characterEncoding must be a Java charset name (UTF-8), not
                // a MySQL charset name (utf8mb4) — the MySQL server-side
                // table/column charset can still be utf8mb4; this parameter
                // is purely about how the JDBC driver talks to the JVM's
                // Charset APIs.
                String params = 'useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8&useUnicode=true'

                if (dbSocket) {
                    // Route the connection through a specific unix domain
                    // socket via junixsocket's AFUNIXDatabaseSocketFactory
                    // (see build.gradle).
                    return "jdbc:mysql://localhost/${dbName}?${params}" +
                           "&socketFactory=org.newsclub.net.mysql.AFUNIXDatabaseSocketFactory" +
                           "&junixsocket.file=${dbSocket}"
                }

                String port = dbPort ?: '3306'
                return "jdbc:mysql://${dbHost}:${port}/${dbName}?${params}"
        }
    }
}
