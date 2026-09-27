# rentalapp

A Grails admin/CRUD dashboard over the existing `rentaldb` database
(tenants, rooms, tenancies, rent_payments, security_deposits, plus the
`current_tenancies` view). See https://github.com/hubergilt/rentaldb for
the database project itself.

## Overview

This project exists in two parallel versions:

- **grails6** — Grails 6.2.2 / Groovy 3.0.23 / Spring Boot 2.7.18 / `javax.servlet`
- **grails7** — Grails 7.2.3 / Groovy 4.0.33 / Spring Boot 3.5.16 / `jakarta.servlet`

Both versions share the same domain model and database schema but target
different servlet namespace generations. Choose the version that matches
your target deployment container.

## Core Principle

**This app never creates or alters the schema.** `rentaldb`'s tables are
owned and version-controlled by **Flyway**, run from the separate `rentaldb`
project. Rentalapp only ever *reads and writes rows* through GORM against
tables that already exist — every domain class is pinned with
`dbCreate: none` (permanent, in `application.yml`) and
`static mapping = { table '...'; version false }`.

## Stack Comparison

| Aspect | grails6 (Grails 6.2.x) | grails7 (Grails 7.x) |
|---|---|---|
| Grails | 6.2.2 | 7.2.3 |
| Groovy | 3.0.23 | 4.0.33 |
| Spring Boot | 2.7.18 | 3.5.16 |
| Servlet API | `javax.servlet` | `jakarta.servlet` |
| Hibernate | 5 | 6 |
| Target Containers | Tomcat 9, JBoss EAP 7.4 | Tomcat 10/11, JBoss EAP 8.x, WildFly 27+ |

## Project Layout

```
rentalapp/
├── grails6/    Grails 6.x version (embedded Tomcat, javax.servlet)
└── grails7/    Grails 7.x version (embedded Tomcat, jakarta.servlet)
    ├── build.gradle
    ├── Makefile
    ├── .gitignore
    ├── settings.gradle
    ├── gradle.properties
    ├── gradlew, gradlew.bat
    ├── .env.example
    ├── grails-app/
    │   ├── init/rentalapp/Application.groovy
    │   ├── conf/application.yml
    │   ├── domain/rentalapp/
    │   ├── controllers/rentalapp/
    │   ├── views/
    │   └── i18n/messages.properties
    ├── src/main/resources/static/    logo, stylesheet, JS
    └── src/main/groovy/rentalapp/    HashPassword, WebConfig
```

Each subdirectory (`grails6/`, `grails7/`) is a complete, independent
Grails project with its own `gradlew`, `.env`, `application.yml`,
domain classes, controllers, and views.

## Quick Start (choose one version)

### 1. Prerequisites

- Java 17+
- A running MySQL 8, PostgreSQL, Oracle, or SQL Server instance with the
  `rentaldb` schema already migrated by Flyway (see the `rentaldb` project's
  own README/Makefile). **Run Flyway migration first**; rentalapp assumes
  the tables already exist, regardless of engine.
- Gradle wrapper is included — no local Gradle install required.

### 2. Configure your environment

```bash
cd rentalapp/grails6    # or: cd rentalapp/grails7
cp .env.example .env
```

Edit `.env` with your database credentials, admin password hash, and
branding preferences. See each version's `.env.example` for variable
descriptions.

### 3. Generate the Gradle wrapper (one-time, if needed)

```bash
gradle wrapper --gradle-version 8.7
```

After that, use `./gradlew` for all commands below.

### 4. Set the admin password

Only a BCrypt hash is ever configured. Generate one:

```bash
./gradlew hashPassword -Ppassword='your-real-password'
```

Copy the `$2a$...` or `$2b$...` string into `.env` as `ADMIN_PASSWORD_HASH`.

### 5. Run the app

```bash
./gradlew bootRun
```

Open http://localhost:8080 — you'll land on the login screen, which
doubles as the gateway into every scaffolded CRUD screen.

## What's on each screen

| Screen | grails6 Behavior | grails7 Behavior |
|---|---|---|
| **Tenants** | Custom controller: search (name/ID/email), full CRUD, delete blocked if tenancies/rent payments exist | Same, with real controller + views (converted from dynamic scaffolding) |
| **Rooms** | Dynamic scaffolding (`static scaffold = Room`) | Real controller + views (dynamic scaffolding converted due to framework regression) |
| **Tenancies** | Custom controller: filter by status/room/tenant, AJAX-refreshed deposit installments panel | Same |
| **Rent Payments** | Dynamic scaffolding — append-only ledger | Real controller + views (converted from dynamic scaffolding) |
| **Security Deposits** | Dynamic scaffolding — same ledger | Real controller + views (converted from dynamic scaffolding) |
| **Current Occupancy** | Read-only list/show over `current_tenancies` view | Same |

## Database fidelity

Every domain class is verified against a real schema dump (`mysqldump` output
of `rentaldb`). Key things to know:

- `tenants` has three name parts (`first_names`, `paternal_surname`,
  `maternal_surname`) and no email/phone columns
- `rooms`' label column is `name` (plus a separate `floor`), not `room_number`
- `rent_payments.tenant_id`/`room_id` are genuinely nullable at the DB level
- `rent_payments` and `security_deposits` both have a `remarks` column
- `current_tenancies` is a `LEFT JOIN` from `rooms` — one row per room,
  including vacant ones

If your schema diverges from this dump, update the relevant domain class's
`mapping { column '...' }` block and re-run `./gradlew bootRun`. The
`SQLSyntaxErrorException: Unknown column` error always names the exact
column needing adjustment.

## Regenerating scaffolding after a schema change

Whenever Flyway adds a migration that changes a mapped table, update the
domain class's `mapping` block to match — GORM will not detect changes
itself (`dbCreate: none`). For brand-new tables:

```bash
grails generate-controller rentalapp.NewThing
grails generate-views rentalapp.NewThing
```

Then hand-add `static mapping = { table '...'; version false }` and wire
it into `main.gsp`'s nav and `UrlMappings.groovy`, same as existing entities.

## Tests

```bash
./gradlew test
```

Tests run against an in-memory H2 database — they never touch the real,
Flyway-owned `rentaldb`.

## Deploying to an external servlet container

Each version builds a deployable WAR via `./gradlew bootWar`.

### grails6 (javax.servlet containers)

- **Compatible**: Tomcat 9.x, JBoss EAP 7.4
- **Incompatible**: Tomcat 10/11, JBoss EAP 8.x (jakarta.servlet), WildFly

### grails7 (jakarta.servlet containers)

- **Compatible**: Tomcat 10/11, JBoss EAP 8.x, WildFly 27+
- **Incompatible**: Tomcat 9.x, JBoss EAP 7.4 (javax.servlet)

See each version's README.md for detailed container compatibility tables,
service account setup (`tomcat`/`jboss` user), Makefile targets
(`make war`, `make deploy`, `make jboss-deploy`, etc.), and one-time
server setup instructions.

## Migration between versions

Moving from grails6 → grails7 (or vice versa) is not a trivial drop-in
swap. The two versions use different servlet namespaces (`javax.*` vs
`jakarta.*`), different Groovy versions, different Hibernate dialects,
and different Grails plugin IDs. A comprehensive migration guide is
beyond the scope of this README — contact the project maintainers if
you need to upgrade between major Grails release lines.