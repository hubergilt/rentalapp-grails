package rentalapp

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

/**
 * Standalone helper for `./gradlew hashPassword -Ppassword=yourRealPassword`.
 * Deliberately has no dependency on Grails/Spring Boot bootstrapping — just
 * the spring-security-crypto jar already on the runtime classpath — so it
 * runs fast and doesn't need a database connection or a booted app.
 *
 * Prints only the resulting BCrypt hash; copy that into .env as
 * ADMIN_PASSWORD_HASH. Never paste the plaintext password anywhere else.
 */
class HashPassword {
    static void main(String[] args) {
        String password = args ? args[0] : ''
        if (!password) {
            System.err.println('Usage: ./gradlew hashPassword -Ppassword=yourRealPassword')
            System.exit(1)
        }
        println new BCryptPasswordEncoder().encode(password)
    }
}
