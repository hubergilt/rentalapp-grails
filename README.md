# rentalapp

A Grails 6.x admin/CRUD dashboard over the **existing** `rentaldb` MySQL 8
database (tenants, rooms, tenancies, rent_payments, security_deposits, plus
the `current_tenancies` view). See https://github.com/hubergilt/rentaldb for
the database project itself.

**This app never creates or alters the schema.** `rentaldb`'s tables are
owned and version-controlled by **Flyway**, run from the separate `rentaldb`
project (`flyway.conf`, `sql/V1..V13`). rentalapp only ever *reads and writes
rows* through GORM against tables that already exist — every domain class is
pinned with `dbCreate: none` (permanent, in `application.yml`) and
`static mapping = { table '...'; version false }`.

## Stack

- Grails 6.2.2 / **Groovy 3.0.23** / Java 17+ — see note below on the Groovy version
- **Spring Boot 2.7.18** (embedded Tomcat 9, `javax.servlet`) — see note below
- Gradle (standard Grails build), no Maven
- MySQL Connector/J 8.4.x
- GORM/Hibernate 5, mapped onto the live schema (`dbCreate: none`)
- Grails scaffolding (dynamic + generated) for every CRUD screen — there is
  no separate hand-built frontend
- A simple, dependency-free form-based login (BCrypt-hashed password from an
  env var) gates the whole app; there's no GORM-backed user table, since
  auth doesn't belong in Flyway's schema

> **Note on Groovy 4.x:** the original brief called for Groovy 4.x. In
> practice, the Grails 6.2.x line's own dependency management still pins
> Groovy 3.0.23 (`org.codehaus.groovy` group) — Groovy 4/5 moved to the
> `org.apache.groovy` group and is only cleanly supported starting with
> **Grails 7.x** (the Apache-governed release line). Groovy 3.0.23 on Java
> 17 is a fully supported, stable combination, so this is what's shipped
> here to get you running. If genuine Groovy 4.x is a hard requirement,
> the path is upgrading `grailsVersion`/`grailsGradlePluginVersion` to a
> Grails 7.x release and re-testing the scaffolded views/controllers
> against it (the domain/controller/view code in this project doesn't use
> anything Groovy-3-specific, so that migration should mostly be a version
> bump plus re-testing, not a rewrite).

> **Note on Spring Boot 2.7 vs 3.x:** this project originally forced Spring
> Boot 3.3.2. That doesn't actually work on Grails 6.2.x: core Grails
> modules (e.g. `grails-plugin-codecs`) still depend directly on
> `javax.servlet-api`, i.e. Grails 6.2.x's real baseline is Spring Boot 2.x,
> not 3.x. Forcing Boot 3 caused Grails' auto-generated servlet
> initializer (`ApplicationLoader`) to try to override a `javax.servlet`
> method against Boot 3's `jakarta.servlet`-only base class — a mismatch
> that can never compile. Pinning to **Spring Boot 2.7.18** (last release
> of the 2.x line, still fully Java 17-compatible) matches what Grails
> 6.2.x actually expects. As with Groovy, genuine Spring Boot 3/jakarta
> support is a **Grails 7.x** upgrade, not a Grails 6.2.x config tweak —
> both land together if you do that upgrade later.

## Project layout

```
rentalapp/
├── build.gradle                     Grails 6 web+gsp app (Spring Boot 2.7 / embedded Tomcat)
├── settings.gradle
├── gradle.properties
├── .env.example                     copy to .env, fill in real values
├── grails-app/
│   ├── conf/
│   │   ├── application.yml          branding, datasource, dbCreate: none
│   │   └── logback.xml
│   ├── domain/rentalapp/            Tenant, Room, Tenancy, RentPayment,
│   │                                 SecurityDeposit, CurrentTenancy (view)
│   ├── controllers/rentalapp/       Tenant/Tenancy custom controllers,
│   │                                 Room/RentPayment/SecurityDeposit
│   │                                 dynamic-scaffolded, CurrentTenancy
│   │                                 (read-only), Login + SecurityInterceptor
│   ├── views/                       branded layout, login screen, full
│   │                                 Tenant + Tenancy CRUD views (incl.
│   │                                 inline deposit installments), read-only
│   │                                 CurrentTenancy views
│   └── i18n/messages.properties
├── src/main/resources/static/       logo, stylesheet (theme vars), JS for
│                                     the inline deposit-installments widget
│                                     — plain Spring Boot static resources,
│                                     no asset-pipeline plugin (see note below)
└── src/main/groovy/rentalapp/       HashPassword helper (./gradlew hashPassword)
```

> **Note on asset-pipeline:** Grails apps conventionally use the
> `asset-pipeline` plugin for `grails-app/assets`. This project deliberately
> doesn't use it — for three small static files (CSS/JS/logo), plain Spring
> Boot static resource serving is simpler and is one less plugin/version to
> track — they're served directly from `src/main/resources/static/`.

## 1. Prerequisites

- Java 17+
- A running MySQL 8 instance with the `rentaldb` schema already migrated by
  Flyway (see the `rentaldb` project's own README/Makefile — `flyway migrate`
  against `flyway.conf`). **Run that first; rentalapp assumes the tables
  already exist.**
- Gradle itself is *not* required to be pre-installed if you generate the
  wrapper (see below), but you do need network access the first time.

## 2. Generate the Gradle wrapper (one-time)

This archive ships without the binary `gradle-wrapper.jar` (binaries don't
belong in a source drop). Generate it once, with any Gradle 8.x on your PATH:

```bash
cd rentalapp
gradle wrapper --gradle-version 8.7
```

After that, use `./gradlew` (or `gradlew.bat` on Windows) for everything
below — no local Gradle install required from that point on.

## 3. Configure your environment

```bash
cp .env.example .env
```

Edit `.env`:

| Variable | Purpose |
|---|---|
| `DB_NAME` | schema name, default `rentaldb` |
| `DB_USERNAME` / `DB_PASSWORD` | MySQL credentials for rentalapp's own (least-privilege) DB user |
| `DB_HOST` / `DB_PORT` | plain TCP connection (default path) |
| `DB_SOCKET` | **use instead of** `DB_HOST`/`DB_PORT` if MySQL is only reachable via a non-default unix socket (e.g. started through an environment-modules `module load mysql/8.x` setup rather than the OS package's default socket). Takes priority when set. Connected to via [junixsocket](https://github.com/kohlschutter/junixsocket)'s `AFUNIXDatabaseSocketFactory`. |
| `DB_URL` | escape hatch — a fully-formed JDBC URL, used verbatim if set, overriding everything else |
| `DB_LOG_SQL` | `true` to see Hibernate SQL in dev |
| `APP_NAME` / `APP_TAGLINE` / `APP_THEME` | branding (`slate` \| `ocean` \| `sunset`) |
| `ADMIN_USERNAME` / `ADMIN_PASSWORD_HASH` | login credentials — see below |

`.env` is loaded automatically at startup by `Application.groovy`'s own
`loadDotEnv()` — a small, explicit loader (not a third-party auto-detecting
library), so it's easy to see exactly what got picked up: it prints how
many values it loaded, and from which path, on every `bootRun`. It looks
for `.env` in the directory you run `./gradlew bootRun` from (the project
root). Real exported OS environment variables always take priority over
`.env` if both are set. Nothing sensitive is ever hardcoded in
`application.yml`.

### Setting the admin password

Only a **BCrypt hash** of the password is ever configured — never the
plaintext. Generate one with the bundled `hashPassword` Gradle task (a
small, self-contained helper — no extra plugin, no database connection
needed):

```bash
./gradlew hashPassword -Ppassword='your-real-password'
```

Copy the printed `$2a$...` string into `.env` as `ADMIN_PASSWORD_HASH`.

### Creating rentalapp's own MySQL user (least privilege)

Run once, as a MySQL admin, against the already-Flyway-migrated `rentaldb`:

```sql
CREATE USER 'rentalapp'@'localhost' IDENTIFIED BY 'changeme';
GRANT SELECT, INSERT, UPDATE, DELETE ON rentaldb.* TO 'rentalapp'@'localhost';
-- Deliberately NOT granted: CREATE, ALTER, DROP, INDEX, REFERENCES —
-- schema changes are Flyway's job only.
FLUSH PRIVILEGES;
```

## 4. Run it

```bash
./gradlew bootRun
```

(`./gradlew serve` is provided as a friendlier alias for the same task, if
your team prefers that name.)

Then open http://localhost:8080 — you'll land on the login screen, which
doubles as the gateway into every scaffolded CRUD screen (nav bar across the
top once logged in).

## 5. What's on each screen

| Screen | Behavior |
|---|---|
| **Tenants** | Custom controller: search box (name / national ID / email), full list/show/create/edit, delete blocked (with a friendly message) if the tenant still has tenancies or rent payments — mirrors the DB's `ON DELETE RESTRICT`. |
| **Rooms** | Grails dynamic scaffolding (`static scaffold = Room`) — list/show/create/edit/delete with built-in sortable columns. |
| **Tenancies** | Custom controller: filter by status (active/ended), room, tenant. The show/edit screen includes an **inline, AJAX-refreshed security deposit installments panel** — add or remove an installment without leaving the page or reloading. Delete is blocked if installments still exist. |
| **Rent Payments** | Grails dynamic scaffolding — append-only ledger, use column sorting / query params as filters. |
| **Security Deposits** | Grails dynamic scaffolding — same ledger, browsable/auditable independent of a specific tenancy. |
| **Current Occupancy** | Read-only list/show over the `current_tenancies` **view**. Only `index`/`show` actions exist — there is no create/edit/delete route, in code or in the UI. |

## 6. Schema fidelity

Every domain class here is verified directly against a real schema dump
(`mysqldump` output of `rentaldb`, current as of this writing) — not
guessed from documentation. A few things worth knowing about the actual
shape of the data, since they're easy to miss:

- `tenants` has three separate name parts (`first_names`, `paternal_surname`,
  `maternal_surname`) and no email/phone columns at all.
- `rooms`' label column is `name` (plus a separate `floor`), not
  `room_number`.
- `rent_payments.tenant_id`/`room_id` are genuinely nullable at the DB
  level — the domain class matches that rather than forcing them required.
- `rent_payments` and `security_deposits` both have a `remarks` column
  (required on rent_payments, optional on security_deposits).
- `current_tenancies` is a `LEFT JOIN` from `rooms`, so it has **one row per
  room, including vacant ones** — not one row per active tenancy. Vacant
  rooms show up with null tenant fields; `CurrentTenancy.groovy` exposes an
  `occupied` convenience getter for this, and the views render
  `— vacant —` accordingly.

If your `rentaldb` schema has since diverged from this dump (a newer Flyway
migration changed a column), the fix is the same either way: update the
relevant domain class's `mapping { column '...' }` block to match, then
re-run `./gradlew bootRun` and watch for
`SQLSyntaxErrorException: Unknown column` in the log — that error always
names the exact column that needs adjusting.

## 7. Regenerating scaffolding after a schema change

Whenever Flyway adds a migration that changes a mapped table (new column,
renamed column, etc.), update the domain class's `mapping` block to match —
GORM will not detect or apply the change itself (`dbCreate: none`). For
brand-new tables, generate a fresh domain class + controller from the
existing (already-migrated) table:

```bash
# Use the Grails CLI if installed:
grails generate-controller rentalapp.NewThing
grails generate-views rentalapp.NewThing
```

then hand-add `static mapping = { table '...'; version false }` and wire it
into `main.gsp`'s nav and `UrlMappings.groovy`, same as the existing
entities.

## 8. Tests

```bash
./gradlew test
```

Tests run against an in-memory H2 database (see the `test` environment block
in `application.yml`) — they never touch the real, Flyway-owned `rentaldb`.
