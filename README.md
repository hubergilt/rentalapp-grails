# rentalapp

A Grails 6.x admin/CRUD dashboard over the **existing** `rentaldb` database
(tenants, rooms, tenancies, rent_payments, security_deposits, plus the
`current_tenancies` view). See https://github.com/hubergilt/rentaldb for
the database project itself.

**This app never creates or alters the schema.** `rentaldb`'s tables are
owned and version-controlled by **Flyway**, run from the separate `rentaldb`
project (`flyway.conf`, `sql/V1..V13`). rentalapp only ever *reads and writes
rows* through GORM against tables that already exist — every domain class is
pinned with `dbCreate: none` (permanent, in `application.yml`) and
`static mapping = { table '...'; version false }`.

## Stack

- Grails 6.2.2 / **Groovy 3.0.23** / Java 17+ — see note below on the Groovy version
- **Spring Boot 2.7.18** (`javax.servlet`) — see note below
- Gradle (standard Grails build), no Maven
- **MySQL, PostgreSQL, Oracle, or SQL Server** — all four JDBC drivers ship
  in `build.gradle`; which one is active is picked in `application.yml`
  (see "Switching database engines" below), not hardcoded
- GORM/Hibernate 5, mapped onto the live schema (`dbCreate: none`)
- Grails scaffolding (dynamic + generated) for every CRUD screen — there is
  no separate hand-built frontend
- A simple, dependency-free form-based login (BCrypt-hashed password from an
  env var) gates the whole app; there's no GORM-backed user table, since
  auth doesn't belong in Flyway's schema
- Runs standalone (`bootRun`/`bootJar`, embedded Tomcat) **or** deployed as a
  WAR (`bootWar`) into an external servlet container — see "Deploying to an
  external servlet container" below

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
> both land together if you do that upgrade later. This is also why WAR
> deployment only targets `javax.servlet`-generation containers (Tomcat 9,
> JBoss EAP 7.4) — see the deployment section below.

## Project layout

```
rentalapp/
├── build.gradle                     Grails 6 web+gsp app; embedded Tomcat
│                                     for bootRun/bootJar, `war` plugin +
│                                     providedRuntime for bootWar
├── Makefile                         war / deploy / undeploy / redeploy
│                                     targets for the external-Tomcat workflow
├── .gitignore                       .env, build/, *.back, logs/, IDE files
├── settings.gradle
├── gradle.properties
├── .env.example                     copy to .env, fill in real values
├── grails-app/
│   ├── init/rentalapp/
│   │   └── Application.groovy       loads .env (static initializer — runs
│   │                                 under bootRun/bootJar AND WAR deployment)
│   ├── conf/
│   │   ├── application.yml          branding; datasource (one commented
│   │   │                             block per DB engine — see below);
│   │   │                             dbCreate: none
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
└── src/main/groovy/rentalapp/       HashPassword helper (./gradlew hashPassword),
                                      WebConfig (static resource handlers)
```

> **Note on asset-pipeline:** Grails apps conventionally use the
> `asset-pipeline` plugin for `grails-app/assets`. This project deliberately
> doesn't use it — for three small static files (CSS/JS/logo), plain Spring
> Boot static resource serving is simpler and is one less plugin/version to
> track — they're served directly from `src/main/resources/static/`. Every
> reference to these in a GSP (`main.gsp`, `login/auth.gsp`) is prefixed
> with `${request.contextPath}` rather than a bare `/css/...` — required so
> assets resolve correctly whether the app is running at the root (`bootRun`)
> or under a subpath like `/rentalapp` (WAR deployment).

## 1. Prerequisites

- Java 17+
- A running MySQL 8, PostgreSQL, Oracle, or SQL Server instance with the
  `rentaldb` schema already migrated by Flyway (see the `rentaldb` project's
  own README/Makefile — `flyway migrate` against `flyway.conf`). **Run that
  first; rentalapp assumes the tables already exist**, regardless of engine.
- Gradle itself is *not* required to be pre-installed if you generate the
  wrapper (see below), but you do need network access the first time.
- Only needed for WAR deployment: an external servlet container. See
  "Deploying to an external servlet container" for which ones are actually
  compatible — not every Tomcat/JBoss/WildFly version works.

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
| `DB_USERNAME` / `DB_PASSWORD` | DB credentials for rentalapp's own (least-privilege) user |
| `DB_HOST` / `DB_PORT` | plain TCP connection, shared by all engines |
| `DB_SOCKET` | **MySQL only** — use instead of `DB_HOST`/`DB_PORT` if MySQL is only reachable via a non-default unix socket (e.g. started through an environment-modules `module load mysql/8.x` setup rather than the OS package's default socket). Takes priority when set. Connected to via [junixsocket](https://github.com/kohlschutter/junixsocket)'s `AFUNIXDatabaseSocketFactory`. |
| `DB_SERVICE_NAME` | **Oracle only** — falls back to `DB_NAME` if unset |
| `DB_ENCRYPT` / `DB_TRUST_SERVER_CERTIFICATE` | **SQL Server only** — default `true` for both |
| `DB_URL` | escape hatch — a fully-formed JDBC URL, used verbatim if set, overriding everything else |
| `DB_LOG_SQL` | `true` to see Hibernate SQL in dev |
| `APP_NAME` / `APP_TAGLINE` / `APP_THEME` | branding (`slate` \| `ocean` \| `sunset`) |
| `ADMIN_USERNAME` / `ADMIN_PASSWORD_HASH` | login credentials — see below |

`.env` is loaded automatically at startup by `Application.groovy`'s own
`loadDotEnv()` — a small, explicit loader (not a third-party auto-detecting
library), so it's easy to see exactly what got picked up: it prints how
many values it loaded, and from which path, every time. It runs from a
`static { }` initializer rather than `main()`, since a WAR deployed into an
external container never calls this class's `main()` at all — a static
initializer runs regardless of entry point.

**Where it looks for `.env` differs by how you're running it:**
- `bootRun` / `bootJar`: the directory you ran the command from (your
  project root).
- WAR deployed into an external container: `$CATALINA_BASE/.env` (e.g.
  `/opt/tomcat/9.0.113/.env`) — **not** anywhere inside the WAR itself, and
  not your project directory. See the deployment section below for why, and
  for what to actually put there.

Real exported OS environment variables always take priority over `.env` if
both are set. Nothing sensitive is ever hardcoded in `application.yml`.

### Switching database engines

`application.yml`'s `dataSource:` block has one commented-out option per
engine (MySQL over TCP, MySQL over a unix socket, PostgreSQL, Oracle, SQL
Server) — each fully self-contained (`driverClassName` + `dialect` + `url`),
with the `url` built entirely from `${VAR:default}` placeholders reading
`.env`. To switch engines: comment out the currently-active block, uncomment
the one you want, restart. That's the only file that needs touching — no
code change, and `.env`'s connection-detail variables (`DB_HOST`, `DB_PORT`,
etc.) work the same regardless of which block is active.

**Nothing validates that exactly one block is uncommented at a time** — if
two are ever left active by mistake, Spring silently uses whichever
`driverClassName`/`dialect`/`url` key appears *last* in the file, with no
warning. Worth a quick look at the file after editing it.

### Setting the admin password

Only a **BCrypt hash** of the password is ever configured — never the
plaintext. Generate one with the bundled `hashPassword` Gradle task (a
small, self-contained helper — no extra plugin, no database connection
needed):

```bash
./gradlew hashPassword -Ppassword='your-real-password'
```

Copy the printed `$2a$...` string into `.env` as `ADMIN_PASSWORD_HASH`.

> `make pass` runs the same task using the `PASSWD` variable in the
> `Makefile` — convenient, but that means the plaintext password lives in a
> file that's normally committed to git. Prefer `./gradlew hashPassword
> -Ppassword=...` directly, or override on the command line
> (`make pass PASSWD='...'`), rather than editing the Makefile itself.

### Creating rentalapp's own database user (least privilege)

The exact syntax is engine-specific, but the intent is the same everywhere:
grant only `SELECT`/`INSERT`/`UPDATE`/`DELETE` on the already-migrated
`rentaldb` schema — never `CREATE`/`ALTER`/`DROP`/`INDEX`/`REFERENCES`,
since schema changes are Flyway's job only. For MySQL:

```sql
CREATE USER 'rentalapp'@'localhost' IDENTIFIED BY 'changeme';
GRANT SELECT, INSERT, UPDATE, DELETE ON rentaldb.* TO 'rentalapp'@'localhost';
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
entities. If the new view references a static asset, prefix its path with
`${request.contextPath}` (see the asset-pipeline note above) so it still
works under WAR deployment, not just `bootRun`.

## 8. Tests

```bash
./gradlew test
```

Tests run against an in-memory H2 database (see the `test` environment block
in `application.yml`) — they never touch the real, Flyway-owned `rentaldb`.

## 9. Deploying to an external servlet container

Besides `bootRun`/`bootJar` (embedded Tomcat, for local dev), the project
also builds a deployable WAR via `./gradlew bootWar`, for hosting inside an
external servlet container rather than running standalone.

### Container compatibility

Grails 6.2.x / Spring Boot 2.7 are built on the older `javax.servlet.*` API,
not the newer `jakarta.servlet.*` one (Jakarta EE 9+). Only containers still
on the `javax.*` generation work without modification:

| Container | Servlet namespace | Compatible? |
|---|---|---|
| **Tomcat 9.x** | `javax.*` | ✅ Yes |
| Tomcat 10/11 | `jakarta.*` | ❌ No |
| **JBoss EAP 7.4** | `javax.*` | ✅ Yes |
| JBoss EAP 8.x | `jakarta.*` | ❌ No |
| WildFly (any current version) | `jakarta.*` | ❌ No |

Using an incompatible container would require migrating the whole project
to Spring Boot 3/Grails 7 first (see the Spring Boot note above) — it's not
a WAR-packaging setting.

### One-time server setup (Tomcat 9 example)

Run Tomcat under its own dedicated service account rather than a personal
user account:

```bash
sudo groupadd --system tomcat
sudo useradd --system --gid tomcat --no-create-home --shell /usr/sbin/nologin tomcat
sudo usermod -aG tomcat "$USER"          # log out/in (or `newgrp tomcat`) after this
sudo chown -R tomcat:tomcat /opt/tomcat/9.0.113
sudo chmod 2775 /opt/tomcat/9.0.113/webapps
```

The `2` (setgid) on `webapps/` means new top-level entries inherit the
`tomcat` group, so both you and the Tomcat process can deploy there without
needing `sudo` for every deploy. It does **not** make Tomcat's own extracted
subdirectories (`WEB-INF/`, etc.) group-writable, though — those are created
with the standard `755`, owner-write-only. Deleting an already-deployed
app's exploded directory (not just the `.war` file) does need `sudo` for
that reason; see the Makefile's `undeploy` target.

Create a separate `.env` for the server itself — the WAR never bundles
`.env` (it's gitignored, holds real secrets):

```bash
sudo -u tomcat cp .env /opt/tomcat/9.0.113/.env
sudo chmod 600 /opt/tomcat/9.0.113/.env
```

### Building and deploying

```bash
make war        # ./gradlew clean bootWar, renamed to a fixed rentalapp.war
make deploy      # copies it into Tomcat's webapps/
make undeploy    # removes both the WAR and its exploded directory
make redeploy    # build + deploy in one step
```

The WAR is deliberately renamed from Gradle's default
`rentalapp-<version>.war` to a fixed `rentalapp.war` — Tomcat derives the
deployed context path from the filename, so the versioned name would
otherwise deploy at `/rentalapp-0.1.0` instead of `/rentalapp`.

**The Makefile does not start or stop Tomcat itself.** `tomcat-start` /
`tomcat-stop` are shell aliases provided by the `srv/tomcat` environment
module, and aliases aren't reliably expanded inside `make`'s non-interactive
recipe shell. Wrap the Makefile targets with those yourself:

```bash
tomcat-stop && make deploy && tomcat-start
tomcat-logs   # tails catalina.out
```

Once running, the app is served at `http://localhost:8080/rentalapp/` — not
the root path, unlike `bootRun`.
