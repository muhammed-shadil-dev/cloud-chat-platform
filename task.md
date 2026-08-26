# Task Board — Cloud Chat Platform

Single source of truth for implementation progress. Process rules live in `CLAUDE.md` — this file
only tracks **what is done** and **what is next**.

Rules for this file:

- A task is marked ✅ only after implementation **and** relevant tests were actually executed and
  passed. Never check a box from memory or intent.
- Every completed task gets an entry under its phase's **Implementation Notes**: what was built,
  which tests ran, and the engineering decisions made.
- Work top-to-bottom within a phase. Do not skip ahead across phases.
- Before implementing, verify against the repository — a task may already be done.

**Status legend:** ✅ Done · 🚧 In progress · ☐ Not started · ⏸ Deferred · ⛔ Blocked

**⏸ Deferred** means still in the roadmap and still intended — consciously postponed, not dropped
and not done. A deferred task is skipped by `/next` until the developer reactivates it.

## Active priority (set 2026-08-26)

The developer's priority is **Backend SDE placement and backend engineering depth**. The active
line of work is therefore the backend authentication sequence, not the remaining frontend
scaffolding:

```
P1-5  Registration WebMvcTest coverage      ✅ done 2026-08-26
  ↓
P1-6  Database-backed authentication        ← next
  ↓
P1-7  Login endpoint and JWT issuance       (needs 3 decisions first — see task)
```

This is a **deliberate deviation** from strict top-to-bottom phase order: **P0-8** (Next.js
frontend initialization) is deferred, and P0-9 was already dependency-blocked on P1-7. Phase 0 is
otherwise complete, so `/next` should proceed into Phase 1 rather than treating Phase 0 as a
barrier. Recorded here so the board does not silently drift from the order it documents.

---

## Status note (2026-08-26) — verified repository state

Established by direct inspection, not assumption:

- **Backend builds and tests pass.** `apps/api/mvnw -B test` → `BUILD SUCCESS`,
  `Tests run: 4, Failures: 0, Errors: 0, Skipped: 0`.
- **Registration works end-to-end** (`POST /api/auth/register`) with BCrypt hashing, duplicate
  username/email conflicts, bean validation, and a global exception handler.
- **Login does not exist.** `LoginRequest`/`LoginResponse` DTOs are present but nothing consumes
  them. There is no login endpoint, no JWT, and no `UserDetailsService` bean — the boot log shows
  Spring Security still falling back to `inMemoryUserDetailsManager`, so **no database user can
  currently authenticate**. `CustomUserDetails` is written but unused.
- **`apps/web` is not initialized** and is now **⏸ deferred** (P0-8). `package.json`,
  `next.config.ts`, and `tsconfig.json` are 0-byte files; the feature directories are empty; there
  is no `node_modules`.
- **`docker/`, `deployment/`, `monitoring/`, `scripts/` are empty.** No Docker CLI on this machine.
- ~~**Uncommitted work is in the tree.**~~ Resolved 2026-08-26: the `auth` → `users` module
  extraction, its tests, `.env.example`, `application-dev.properties`, and the two `AGENTS.md`
  files were committed as `65e73fd` and pushed. Local `main` and `origin/main` are in sync. See
  P0-6.
- **Known risk:** `application.properties` sets `spring.profiles.default=dev`, so a deployment that
  forgets to set a profile silently runs with `ddl-auto=update`. Tracked as P12-1.

---

## PHASE 0 — Foundation

#### ✅ P0-1 · Repository structure — *repo*
- **Goal:** A monorepo layout that separates backend, frontend, and (future) infrastructure.
- **Depends on:** —
- **Scope:** `apps/api`, `apps/web`, plus placeholder `docker/`, `deployment/`, `monitoring/`,
  `docs/`, `scripts/`.
- **Tests:** N/A (structural).
- **Docs:** Layout documented in `CLAUDE.md` §2.
- **Interview concepts:** Monorepo vs polyrepo; modular monolith vs microservices; why deferring
  infrastructure directories beats scaffolding empty ones.
- **Status:** ✅ Verified 2026-08-26.

#### ✅ P0-2 · Spring Boot backend bootstrap — *api*
- **Goal:** A buildable Spring Boot 4 / Java 21 application.
- **Depends on:** P0-1
- **Scope:** `pom.xml` (Web MVC, Security, Data JPA, Validation, PostgreSQL, Lombok, test
  starters), Maven wrapper, `ChatAppApplication`.
- **Tests:** `ChatAppApplicationTests.contextLoads` — passing.
- **Docs:** Stack recorded in `CLAUDE.md` §3.
- **Interview concepts:** Spring Boot auto-configuration; starter dependencies; the application
  context; why the context test excludes datasource auto-configuration.
- **Status:** ✅ Verified 2026-08-26 (`mvnw -B test` → BUILD SUCCESS).

#### ✅ P0-3 · PostgreSQL + profile configuration — *api*
- **Goal:** Environment-driven datasource configuration with no secrets in Git.
- **Depends on:** P0-2
- **Scope:** `application.properties`, `application-dev.properties` with `${DB_URL:...}`-style
  indirection, `ddl-auto` and `show-sql` behind env vars.
- **Tests:** N/A (configuration; exercised by P1 integration tests later).
- **Docs:** `CLAUDE.md` §8.
- **Interview concepts:** Spring profiles; externalized configuration precedence; why `ddl-auto=update`
  is a development-only convenience.
- **Status:** ✅ Verified 2026-08-26.

#### ✅ P0-4 · Environment variable documentation — *repo*
- **Goal:** Required environment variables discoverable without leaking secrets.
- **Depends on:** P0-3
- **Scope:** `apps/api/.env.example`; `.gitignore` ignores `.env` and `.env.*` but keeps
  `.env.example`.
- **Tests:** N/A.
- **Docs:** `.env.example` is self-documenting.
- **Interview concepts:** Twelve-factor config; secret handling; why `.env` never enters version
  control.
- **Status:** ✅ Verified 2026-08-26.

#### ✅ P0-5 · Engineering + learning workflow — *repo*
- **Goal:** A repeatable `/next` loop: implement → test → document → teach → commit.
- **Depends on:** P0-1
- **Scope:** `CLAUDE.md` (engineering contract), `task.md` (this board),
  `.claude/commands/next.md`, `docs/templates/obsidian-feature-note.md`, root `AGENTS.md` pointer.
- **Tests:** N/A (process).
- **Docs:** Is the documentation.
- **Interview concepts:** Definition of done; why documentation and tests are part of the feature,
  not a follow-up.
- **Status:** ✅ Completed 2026-08-26.

#### ✅ P0-6 · Commit the pending `users` module refactor — *api*
- **Goal:** Get the developer's uncommitted `auth` → `users` module extraction into history so the
  board and the repository agree.
- **Depends on:** P0-5
- **Scope:** Review the working-tree diff, confirm nothing secret is included, commit the moved
  `User`/`UserRepository`, `UserRegistrationService`, the new tests, `application-dev.properties`,
  `.env.example`, and the `AGENTS.md` files as one or more conventional commits. **Developer
  decides** whether to split it — ask before committing someone else's in-progress work.
- **Tests:** `mvnw -B test` must pass before committing (currently does).
- **Docs:** Note the module-boundary rationale in the Phase 0 Obsidian note.
- **Interview concepts:** Module extraction; why `auth` calling `users`' *service* (not its
  repository) is the boundary that matters; keeping commits reviewable.
- **Status:** ✅ Completed 2026-08-26 — commit `65e73fd`, pushed to `origin/main`.

#### ✅ P0-7 · Actuator health check — *api*
- **Goal:** A liveness/readiness endpoint before anything is containerized.
- **Depends on:** P0-2
- **Scope:** Add `spring-boot-starter-actuator`; expose `/actuator/health` only; decide what
  authentication it requires.
- **Tests:** `@WebMvcTest`/integration test asserting the endpoint's status and exposure rules.
- **Docs:** Endpoint + exposure decision in the Phase 0 note.
- **Interview concepts:** Liveness vs readiness; why exposing all actuator endpoints is a
  vulnerability; health checks as a deployment contract.
- **Status:** ✅ Completed 2026-08-26 — 6 tests, all passing.

#### ⏸ P0-8 · Initialize the Next.js frontend — *web*
- **Goal:** Turn the `apps/web` scaffold into a real, buildable Next.js app.
- **Depends on:** P0-1
- **Scope:** Real `package.json`, `next.config.ts`, `tsconfig.json` (strict), Tailwind, shadcn/ui
  init, a minimal root layout and page. Nothing feature-specific.
- **Tests:** `npm run build` must succeed; add the test runner only when the first component needs
  one.
- **Docs:** A short "how to run the frontend" section in `README.md`.
- **Interview concepts:** App Router vs Pages Router; server vs client components; why the frontend
  is a separate deployable.
- **Status:** ⏸ **Deferred 2026-08-26** — not started, not dropped. Postponed by the developer to
  prioritize backend depth for SDE placement; the frontend is not on the critical path for the
  auth, chat, WebSocket, or Redis work that carries the portfolio. Stays in the roadmap.
  **Reactivate when:** the backend has endpoints worth driving from a UI (realistically after
  P1-7 login and P3-2 rooms), or a demo needs a visible client. Still requires `npm install`.
  Nothing else depends on this task except P12-3 (frontend container / Nginx).

#### ☐ P0-9 · OpenAPI / Swagger UI — *api*
- **Goal:** Generated, browsable API documentation that cannot drift from the code.
- **Depends on:** P1-7 (worth doing once there is more than one endpoint)
- **Scope:** Add springdoc-openapi; annotate existing controllers; decide whether the UI is exposed
  outside `dev`.
- **Tests:** Assert the OpenAPI JSON endpoint responds and includes the known paths.
- **Docs:** Link the UI path in `README.md`.
- **Interview concepts:** Contract-first vs code-first; why generated docs beat hand-written ones;
  exposure risk in production.
- **Status:** ☐ Not started.

### Implementation Notes (Phase 0)

**P0-1 … P0-4 (2026-08-26)** — Verified by inspection rather than re-created. The repository already
had the monorepo layout, a building Spring Boot 4.0.6 / Java 21 backend, environment-driven
PostgreSQL configuration, and `.env.example`. Confirmed with `find`, `cat pom.xml`, and a real build:
`apps/api/mvnw -B test` → `BUILD SUCCESS`, `Tests run: 4, Failures: 0, Errors: 0, Skipped: 0`.
Nothing was recreated, and no existing code was modified.

**P0-5 (2026-08-26)** — Established the engineering + learning workflow: `CLAUDE.md` as the
permanent contract (architecture, per-domain conventions, definition of done, autonomy rules),
this board, `.claude/commands/next.md`, and a repo-local copy of the 17-section Obsidian feature
template so the workflow does not depend on the vault being present. Deliberately created **no**
empty infrastructure directories: `docker/`, `deployment/`, `monitoring/`, and `scripts/` stay empty
until a task needs them.

Two findings worth carrying forward:
1. `apps/api/AGENTS.md` and `apps/web/AGENTS.md` reference `docs/architecture.md`, `docs/api.md`,
   `docs/security.md`, `docs/testing.md`, and `docs/task.md` — none of which exist. `CLAUDE.md` §15
   records that this file and `task.md` are authoritative until those are written. The dangling
   references were left in place rather than edited, since those files are the developer's
   uncommitted work.
2. `spring.profiles.default=dev` means a deployment that forgets `SPRING_PROFILES_ACTIVE` silently
   runs with `ddl-auto=update` against its real database. Tracked as P12-1.

**P0-6 (2026-08-26)** — Committed the developer's pending `auth` → `users` extraction as `65e73fd`
after re-running the tests on the exact tree being committed (`BUILD SUCCESS`, 4 tests, 0 failures)
and scanning the staged diff for secrets (only the `.env.example` placeholder and `${DB_PASSWORD:}`
indirection — no literals). Git recorded both file moves as **renames** (R052/R057), so history
follows `User.java` and `UserRepository.java` into `users/`.

`origin/main` had meanwhile gained `cbaea97 Update README.md`, so `main` was 1 ahead / 1 behind.
Resolved with `git pull --rebase` — zero path overlap, no conflicts — then a normal fast-forward
push. No force push, no reset, nothing stashed or discarded. The two local commits took new hashes
(`26e7ef3`→`992ed3e`, `fa09d95`→`65e73fd`), which is expected for a rebase and harmless since
neither had been pushed.

**P0-7 (2026-08-26)** — Added `spring-boot-starter-actuator` and exposed **only** `/actuator/health`.

Decisions made:
- **`management.endpoints.web.exposure.include=health`** — nothing else reaches the web surface.
  `env`, `beans`, `configprops`, and `heapdump` leak configuration and internals; each one gets
  exposed only when a task needs it.
- **Health is public** (`permitAll` in `SecurityConfig`). Probes run before any credential exists —
  container health checks, load balancers, and Kubernetes liveness/readiness cannot authenticate.
  Leaving it behind `anyRequest().authenticated()` would have made it useless for P12-2/P13-1.
- **`show-details=never`** — the public response is UP/DOWN and nothing more, so making it public
  costs no information. `when-authorized` would be better once a role model exists, but
  `CLAUDE.md` §11 forbids inventing one before a task defines it. Revisit then.
- **`probes.enabled=true`** — `/actuator/health/liveness` and `/actuator/health/readiness` now exist,
  which is what P13-1 will wire to Kubernetes probes. Enabled now because P0-7's stated goal is a
  liveness/readiness endpoint, not simply "a health URL".

Tests: `HealthEndpointTest` — 6 tests, all passing. Health reachable anonymously and reports UP;
no `components` key in the body; both probes reachable anonymously; `/actuator/env` and
`/actuator/beans` return 401 anonymously **and 404 even when authenticated** (`@WithMockUser`) —
the second assertion is the one that actually proves non-exposure rather than merely proving the
security rule. Full suite: `Tests run: 10, Failures: 0, Errors: 0, Skipped: 0`, `BUILD SUCCESS`.

Two notes for later:
- The test excludes datasource auto-configuration, so there is no `db` health indicator in it.
  Against a real database, health correctly reports **DOWN with 503** when PostgreSQL is
  unreachable — that is the point of the check, and P12-2's container healthcheck will depend on it.
- `SecurityConfig` lives in `auth/security/` but now governs an operational path. It is the single
  filter chain for the whole application, so it arguably belongs in `common/config/`. Not moved —
  out of scope for this task, and worth doing deliberately rather than as a drive-by.

Verified against Spring Boot 4's repackaged test annotations before writing the test:
`@AutoConfigureMockMvc` is `org.springframework.boot.webmvc.test.autoconfigure` in Boot 4, not the
Boot 3 `org.springframework.boot.test.autoconfigure.web.servlet`.

**P0-8 (2026-08-26) — deferred, not dropped.** The developer's priority is Backend SDE placement, so
the Next.js initialization is postponed and the active line moves to the Phase 1 authentication
sequence (P1-5 → P1-6 → P1-7). Rationale: the frontend is not on the critical path for the auth,
chat, WebSocket, and Redis work that carries this portfolio, and an uninitialized `apps/web` costs
nothing until there is an API worth driving from a UI. Consequences recorded rather than left
implicit: **P12-3** (frontend container + Nginx) is transitively blocked while this stands, and
`apps/web` remains 0-byte placeholder files, so any claim about the frontend building is false until
P0-8 is reactivated. With P0-8 deferred and P0-9 dependency-blocked on P1-7, **Phase 0 is otherwise
complete** and `/next` proceeds into Phase 1.

---

## PHASE 1 — Authentication  *(module: `auth`, `users`)*

#### ✅ P1-1 · User entity and repository — *users*
- **Goal:** Durable user identity with database-enforced uniqueness.
- **Depends on:** P0-3
- **Scope:** `users/entity/User` (id, username, email, password hash, createdAt, enabled; unique
  constraints on username and email), `users/repository/UserRepository` with `findByEmail`,
  `findByUsername`, `existsByEmail`, `existsByUsername`.
- **Tests:** Covered indirectly by `UserRegistrationServiceTest`.
- **Docs:** Vault — `backend/module/auth/01-Registration.md` (pre-existing).
- **Interview concepts:** JPA entity lifecycle; `@PrePersist`; `GenerationType.IDENTITY` vs
  `SEQUENCE`; derived query methods; why uniqueness must be a DB constraint, not just a check.
- **Status:** ✅ Implemented (currently uncommitted — see P0-6).

#### ✅ P1-2 · Registration endpoint with BCrypt — *auth*
- **Goal:** `POST /api/auth/register` creates a user with a hashed password.
- **Depends on:** P1-1
- **Scope:** `AuthController`, `AuthService`, `UserRegistrationService`, `RegisterRequest`,
  `RegisterResponse`, `PasswordConfig` (BCrypt), `201 Created`.
- **Tests:** `AuthServiceTest.register_hashesPasswordAndReturnsRegisteredUser`,
  `UserRegistrationServiceTest` (duplicate username, duplicate email) — 3 tests, passing.
- **Docs:** Vault — `backend/module/auth/01-Registration.md`.
- **Interview concepts:** Why BCrypt over SHA-256; salting and work factor; why the password is
  hashed in `AuthService` and the `users` module never sees the raw password; DTOs vs entities at
  the boundary.
- **Status:** ✅ Implemented and tested.

#### ✅ P1-3 · Validation and global exception handling — *common*
- **Goal:** Predictable, non-leaky error responses.
- **Depends on:** P1-2
- **Scope:** Bean Validation on `RegisterRequest`; `GlobalExceptionHandler` mapping
  `UsernameAlreadyExistsException` / `EmailAlreadyExistsException` / `DataIntegrityViolationException`
  → `409`, `MethodArgumentNotValidException` → `400` with a field→message map; `ErrorResponse`.
- **Tests:** Service-level duplicate tests passing; **HTTP-layer assertions still missing** → P1-5.
- **Docs:** Vault — `backend/module/auth/01-Registration.md`.
- **Interview concepts:** `@RestControllerAdvice`; why catching `DataIntegrityViolationException`
  matters even with an application-level `existsBy` check (TOCTOU race between two concurrent
  registrations); not leaking internals in error messages.
- **Status:** ✅ Implemented.

#### 🚧 P1-4 · Login request/response DTOs — *auth*
- **Goal:** The transport contract for login.
- **Depends on:** P1-2
- **Scope:** `LoginRequest` (usernameOrEmail, password — both `@NotBlank`), `LoginResponse`
  (accessToken, tokenType).
- **Tests:** None yet — nothing consumes these DTOs.
- **Docs:** Vault — `backend/module/auth/02-Login.md` (pre-existing, marked in-progress).
- **Interview concepts:** Designing a login contract that does not leak which field was wrong;
  why the response carries a token type.
- **Status:** 🚧 DTOs exist; no producer or consumer. Completed by P1-7.

#### ✅ P1-5 · Registration API-layer tests — *auth*
- **Goal:** Close out registration by testing the HTTP contract, not just the service.
- **Depends on:** P1-3
- **Scope:** `@WebMvcTest(AuthController)` with mocked `AuthService` and Spring Security test
  support: `201` on success, `400` with field errors on invalid input (blank username, bad email,
  short password), `409` on duplicate username/email.
- **Tests:** The above, actually executed.
- **Docs:** Update the registration note's §10 Testing with real results.
- **Interview concepts:** Slice tests vs full context tests; why `@WebMvcTest` needs security
  configuration considered; testing validation at the boundary that enforces it.
- **Status:** ✅ Completed 2026-08-26 — 10 tests, all passing. Registration's HTTP contract is now
  covered; the feature is closed out.

#### ☐ P1-6 · Database-backed authentication — *auth*
- **Goal:** Make persisted users actually able to authenticate. Today `CustomUserDetails` exists but
  no `UserDetailsService` bean does, so Spring Security falls back to an in-memory user and every
  registered account is unusable.
- **Depends on:** P1-1, P1-2
- **Scope:** A `UserDetailsService` implementation in `auth/security` that loads a user by username
  **or** email via `users`' service layer (not its repository — `CLAUDE.md` §5) and returns
  `CustomUserDetails`; throw `UsernameNotFoundException` when absent; expose an
  `AuthenticationManager`/`DaoAuthenticationProvider` bean wired to the existing BCrypt
  `PasswordEncoder`; decide and document how `enabled = false` is handled.
- **Tests:** Unit tests for found / not-found / lookup-by-email; an integration or security-slice
  test proving a registered user authenticates and a wrong password is rejected.
- **Docs:** New vault note `backend/module/auth/03-Database-Authentication.md` (17-section template).
- **Interview concepts:** The `AuthenticationManager` → `AuthenticationProvider` →
  `UserDetailsService` → `PasswordEncoder` chain; why `UserDetailsService` returns a user and never
  compares passwords itself; timing attacks and generic failure messages; `SecurityContextHolder`
  and how the principal reaches a controller.
- **Status:** ☐ Not started. **← Next real feature, immediately after P1-5.**

#### ☐ P1-7 · Login endpoint and JWT issuance — *auth*
- **Goal:** `POST /api/auth/login` returns a signed JWT for valid credentials.
- **Depends on:** P1-6
- **Scope:** Add a JWT library; a `JwtService` (issue + validate) reading its secret from the
  environment only; `AuthController.login` delegating to `AuthService`; populate `LoginResponse`;
  one generic `401` for any credential failure.
  **Decisions to make with the developer first:** signing algorithm (HMAC vs RSA), token lifetime,
  and what claims the token carries.
- **Tests:** Unit tests for token creation/validation/expiry/tampering; controller tests for `200`
  with a token, `401` on bad credentials, `400` on a malformed body.
- **Docs:** Complete `backend/module/auth/02-Login.md`; write an ADR for the JWT decisions.
- **Interview concepts:** JWT structure (header/payload/signature); signing vs encryption; why a JWT
  payload is readable and must never hold secrets; stateless auth trade-offs; token lifetime vs
  revocation.
- **Status:** ☐ Not started.

#### ☐ P1-8 · JWT authentication filter and stateless security — *auth*
- **Goal:** Protected endpoints accept a `Bearer` token instead of HTTP Basic.
- **Depends on:** P1-7
- **Scope:** A `OncePerRequestFilter` that extracts and validates the token and populates the
  `SecurityContext`; register it before `UsernamePasswordAuthenticationFilter`; set session
  creation policy to `STATELESS`; remove HTTP Basic; keep `/api/auth/**` public.
- **Tests:** Missing token → `401`; valid token → `200`; expired token → `401`; tampered signature →
  `401`.
- **Docs:** Update the login note's request-flow and security sections.
- **Interview concepts:** Filter chain ordering; `OncePerRequestFilter` and why "once per request"
  matters; stateless sessions; why the filter must not touch the database on every request if it can
  avoid it.
- **Status:** ☐ Not started.

#### ☐ P1-9 · Refresh tokens and logout — *auth*
- **Goal:** Sessions that expire safely without forcing constant re-login.
- **Depends on:** P1-8
- **Scope:** Refresh token issuance, storage, rotation, and revocation. **Storage choice (database
  vs Redis) is an explicit decision** — do not assume.
- **Tests:** Refresh succeeds; a rotated/revoked token is rejected; logout invalidates.
- **Docs:** ADR for the refresh strategy; update the login note.
- **Interview concepts:** Access vs refresh token lifetimes; rotation and reuse detection; why
  stateless JWTs make logout hard; where revocation state has to live.
- **Status:** ☐ Not started.

### Implementation Notes (Phase 1)

*(Entries added as tasks complete. P1-1 … P1-3 were implemented by the developer before this
workflow existed; verified against the code and the passing test run on 2026-08-26.)*

**P1-5 (2026-08-26)** — `AuthControllerTest`, 10 tests, all passing. Test-only change: no
production code, dependency, schema, or API behavior was touched. Full suite now
`Tests run: 20, Failures: 0, Errors: 0, Skipped: 0` — `BUILD SUCCESS`.

Covers what only the web layer can prove: `201` with the response DTO, `400` with a field→message
map for blank username / malformed email / short password, `409` for both conflict exceptions, and
`409` for `DataIntegrityViolationException` (the race the `existsBy` checks cannot close). Plus two
assertions that are really guardrails: the response never carries a `password`/`passwordHash` field
(fails immediately if someone ever returns the entity instead of the DTO), and validation rejects a
bad request **without the service being called at all**.

Decisions:
- **Imported `SecurityConfig` into the slice.** `@WebMvcTest` does not pick up plain
  `@Configuration` classes, so without it Spring Security's defaults apply and everything 401s —
  the tests would then pass or fail for reasons unrelated to the real rules. With it imported,
  these unauthenticated requests succeeding *is* the assertion that `/api/auth/**` is public.
- **Asserted fields, not wording, where the message is not deterministic.** A blank username
  violates both `@NotBlank` and `@Size(min = 3)`, and `GlobalExceptionHandler` keeps one message
  per field, so which survives is arbitrary. Same for `@Size` on password, whose default message is
  locale-dependent. Only the custom `"Invalid email format"` message is asserted verbatim.
- **Raw JSON string bodies** rather than serializing the DTO, so the test exercises the real
  deserialization path a client would hit.

Two findings recorded, not fixed (out of P1-5's scope):
1. **`GlobalExceptionHandler` collapses multiple errors per field.** `errors.put(field, message)`
   keeps only the last violation for a field, and the validation response shape
   (`{field: message}`) differs from `ErrorResponse` (`{status, message}`) used everywhere else —
   two different error contracts on one endpoint.
2. **No handler for `HttpMessageNotReadableException`.** Malformed JSON falls through to Spring's
   default error body, a third shape. Worth a small task to unify the three.

Also confirmed while writing the verification guide: **no class in `auth`, `users`, or `common`
declares a logger**, so this feature emits no application log lines of its own. The note says so
rather than inventing log output.

---

## PHASE 2 — User Management  *(module: `users`)*

#### ☐ P2-1 · Current-user profile endpoint — *users*
- **Goal:** `GET /api/users/me` returns the authenticated user's profile.
- **Depends on:** P1-8
- **Scope:** `UserController`, a read service method, a `UserProfileResponse` DTO that **never**
  includes the password hash; resolve the principal from the security context.
- **Tests:** Authenticated request returns the right user; unauthenticated returns `401`; the
  response body contains no password field.
- **Docs:** New vault note `backend/module/users/01-User-Profile.md`.
- **Interview concepts:** Resolving the principal; why "me" endpoints beat `/users/{id}` for self
  access; DTO projection as a security control.
- **Status:** ☐ Not started.

#### ☐ P2-2 · Profile update — *users*
- **Goal:** Users can change their own display information.
- **Depends on:** P2-1
- **Scope:** `PATCH`/`PUT` with validation; enforce that a user can only modify themselves;
  uniqueness re-check if username or email is editable.
- **Tests:** Successful update; validation failure; attempting to update another user → `403`.
- **Docs:** Extend the user profile note.
- **Interview concepts:** Idempotency of `PUT` vs `PATCH`; object-level authorization (IDOR);
  optimistic locking if concurrent updates matter.
- **Status:** ☐ Not started.

#### ☐ P2-3 · User search / directory lookup — *users*
- **Goal:** Find other users to start a conversation with.
- **Depends on:** P2-1
- **Scope:** Paginated lookup by username prefix; DB index on the searched column; response DTOs
  exposing only public fields.
- **Tests:** Pagination boundaries; case sensitivity; that private fields never appear.
- **Docs:** Extend the users module note.
- **Interview concepts:** Keyset vs offset pagination; indexing for prefix search; enumeration as a
  privacy risk.
- **Status:** ☐ Not started.

#### ☐ P2-4 · Password change — *auth* / *users*
- **Goal:** Authenticated password rotation.
- **Depends on:** P1-8
- **Scope:** Require the current password; re-hash; decide whether existing tokens are invalidated
  (depends on P1-9).
- **Tests:** Correct current password succeeds; wrong one → `401`; new hash differs and verifies.
- **Docs:** Extend the auth module notes.
- **Interview concepts:** Why the current password is required even when authenticated; session
  invalidation after credential change.
- **Status:** ☐ Not started.

---

## PHASE 3 — Chat and Rooms  *(modules: `rooms`, `chat`)*

#### ☐ P3-1 · Room domain model — *rooms*
- **Goal:** Rooms and membership as first-class persisted concepts.
- **Depends on:** P2-1
- **Scope:** `Room` entity (name, type: direct/group, createdBy, createdAt), `RoomMember` join
  entity with a unique `(room_id, user_id)` constraint and a role/joinedAt; repositories.
- **Tests:** Repository tests for membership uniqueness and lookup by user.
- **Docs:** New vault note `backend/module/rooms/01-Room-Model.md`.
- **Interview concepts:** Modeling many-to-many with an explicit join entity vs `@ManyToMany`;
  direct messages as a two-member room; composite unique constraints.
- **Status:** ☐ Not started.

#### ☐ P3-2 · Room lifecycle API — *rooms*
- **Goal:** Create rooms, list mine, join, leave.
- **Depends on:** P3-1
- **Scope:** REST endpoints with authorization at every step; a direct-message room must be
  idempotent — requesting a DM with the same user twice returns the existing room.
- **Tests:** Creation; listing only the caller's rooms; join/leave; non-member access → `403`;
  duplicate DM creation returns the same room.
- **Docs:** Extend the rooms note.
- **Interview concepts:** Idempotent resource creation; authorization on every read *and* write;
  why "list my rooms" must filter server-side.
- **Status:** ☐ Not started.

#### ☐ P3-3 · Message persistence model — *chat*
- **Goal:** Durable message storage that supports efficient history reads.
- **Depends on:** P3-1
- **Scope:** `Message` entity (roomId, senderId, content, createdAt, optional editedAt/deletedAt);
  index on `(room_id, created_at)`; decide soft vs hard delete.
- **Tests:** Repository tests for insert and ordered retrieval.
- **Docs:** New vault note `backend/module/chat/01-Message-Model.md`.
- **Interview concepts:** Why the history index is composite and ordered; soft delete trade-offs;
  storing sender as an ID rather than a JPA association across modules.
- **Status:** ☐ Not started.

#### ☐ P3-4 · Message history API — *chat*
- **Goal:** `GET` paginated room history for a member.
- **Depends on:** P3-2, P3-3
- **Scope:** Cursor-based pagination (newest-first), membership check before returning anything.
- **Tests:** Pagination correctness at boundaries; non-member → `403`; ordering is stable.
- **Docs:** Extend the chat note.
- **Interview concepts:** Keyset pagination and why `OFFSET` degrades on large tables; stable
  ordering with ties; authorization before data access.
- **Status:** ☐ Not started.

---

## PHASE 4 — Real-Time Messaging  *(module: `chat`)*

#### ☐ P4-1 · WebSocket + STOMP infrastructure — *chat*
- **Goal:** An authenticated real-time transport.
- **Depends on:** P1-8, P3-2
- **Scope:** Add the WebSocket starter; `WebSocketConfig` with an endpoint and a simple broker;
  **authenticate the STOMP CONNECT using the same JWT as REST**; a channel interceptor that
  resolves the principal.
- **Tests:** Connection succeeds with a valid token; is rejected without one and with an expired one.
- **Docs:** New vault note `backend/module/chat/02-WebSocket-Transport.md`.
- **Interview concepts:** HTTP upgrade handshake; why WebSocket bypasses the normal filter chain and
  what that means for security; STOMP as a framing protocol; simple broker vs external broker.
- **Status:** ☐ Not started.

#### ☐ P4-2 · Send and broadcast messages — *chat*
- **Goal:** A member sends a message; every member of that room receives it live.
- **Depends on:** P4-1, P3-3
- **Scope:** A `@MessageMapping` handler that validates membership, persists, then broadcasts to
  `/topic/room.{id}`; define and document the persist-vs-broadcast ordering and the delivery
  semantics.
- **Tests:** Persisted and delivered to subscribers; a non-member's send is rejected; payload is a
  DTO, never an entity.
- **Docs:** Extend the WebSocket note with the real sequence diagram.
- **Interview concepts:** At-most-once vs at-least-once delivery; why persist-then-broadcast and
  broadcast-then-persist fail differently; idempotency keys for client retries; message ordering.
- **Status:** ☐ Not started.

#### ☐ P4-3 · Subscription authorization — *chat*
- **Goal:** A user cannot subscribe to a room they are not in.
- **Depends on:** P4-2
- **Scope:** Enforce destination authorization on SUBSCRIBE, not just on send.
- **Tests:** A non-member's subscription attempt is rejected and receives nothing.
- **Docs:** Extend the WebSocket note's security section.
- **Interview concepts:** Why send-side checks alone leak data; authorizing a subscription is
  authorizing a data stream.
- **Status:** ☐ Not started.

#### ☐ P4-4 · Typing indicators and read receipts — *chat*
- **Goal:** Transient interaction signals.
- **Depends on:** P4-3
- **Scope:** Ephemeral events that are **not** persisted as messages; decide what (if anything) read
  receipts persist.
- **Tests:** Events reach other members and create no message rows.
- **Docs:** Extend the WebSocket note.
- **Interview concepts:** Ephemeral vs durable events; why not everything real-time deserves a
  database row; chattiness and back-pressure.
- **Status:** ☐ Not started.

---

## PHASE 5 — Presence  *(module: `presence`)*

#### ☐ P5-1 · Online/offline tracking — *presence*
- **Goal:** Know who is currently connected.
- **Depends on:** P4-1
- **Scope:** Track presence on STOMP connect/disconnect events. **Storage decision required:**
  in-memory works only for a single instance; Redis is the multi-instance answer. If Redis is chosen
  here it arrives ahead of Phase 9 — that is allowed, but `CLAUDE.md` §9's five questions must be
  answered in writing first.
- **Tests:** Connect marks online; disconnect marks offline; an abrupt drop is handled.
- **Docs:** New vault note `backend/module/presence/01-Presence-Tracking.md`.
- **Interview concepts:** Presence as distributed soft state; why an in-memory map breaks when you
  scale to two instances; heartbeats and TTLs; detecting a disconnect that never sent a DISCONNECT.
- **Status:** ☐ Not started.

#### ☐ P5-2 · Presence broadcast and last-seen — *presence*
- **Goal:** Room members see each other's status change live; offline users show a last-seen time.
- **Depends on:** P5-1
- **Scope:** Broadcast presence changes to relevant rooms; persist `lastSeenAt` durably.
- **Tests:** Status change reaches members; last-seen persists across a restart.
- **Docs:** Extend the presence note.
- **Interview concepts:** Fan-out cost of presence in large rooms; why last-seen is durable while
  online-state is not; debouncing flapping connections.
- **Status:** ☐ Not started.

---

## PHASE 6 — Notifications  *(module: `notifications`)*

#### ☐ P6-1 · In-app notification model and delivery — *notifications*
- **Goal:** Unread indicators and a notification feed for messages received while away.
- **Depends on:** P4-2
- **Scope:** Notification entity, generation on message send to offline/unsubscribed members,
  read/unread state, list and mark-read endpoints.
- **Tests:** Generated only for the right recipients; marking read is idempotent; no notification for
  the sender.
- **Docs:** New vault note `backend/module/notifications/01-In-App-Notifications.md`.
- **Interview concepts:** Write fan-out vs read fan-out; unread counters and how they drift;
  idempotent state transitions.
- **Status:** ☐ Not started.

#### ☐ P6-2 · Push notifications (FCM) — *notifications*
- **Goal:** Reach users who are not in the app at all.
- **Depends on:** P6-1
- **Scope:** Device token registration, FCM integration, send-on-offline, credentials from the
  environment only. Failure to send must never fail the message write.
- **Tests:** Token registration; the send path with a mocked FCM client; a send failure does not roll
  back the message.
- **Docs:** Extend the notifications note; ADR for the async/failure strategy.
- **Interview concepts:** Third-party calls inside a transaction (don't); async dispatch and retry;
  at-least-once delivery to an external system; token invalidation.
- **Status:** ☐ Not started.

---

## PHASE 7 — Media  *(module: `media`)*

#### ☐ P7-1 · Object storage integration — *media*
- **Goal:** Store attachments outside the database.
- **Depends on:** P1-8
- **Scope:** MinIO (local) / S3 (cloud) client, bucket configuration, credentials from the
  environment.
- **Tests:** Upload/retrieve against a local MinIO or a mocked client; document which was used.
- **Docs:** New vault note `backend/module/media/01-Object-Storage.md`.
- **Interview concepts:** Why blobs do not belong in PostgreSQL; object storage semantics;
  bucket/key design.
- **Status:** ☐ Not started.

#### ☐ P7-2 · Attachment upload and message linkage — *media* / *chat*
- **Goal:** Send images and files in a room.
- **Depends on:** P7-1, P4-2
- **Scope:** Upload endpoint with size and content-type validation, metadata persisted and linked to
  a message, presigned URLs for retrieval, authorization on download.
- **Tests:** Oversized and disallowed types rejected; a non-member cannot fetch an attachment;
  presigned URLs expire.
- **Docs:** Extend the media note.
- **Interview concepts:** Presigned URLs and why the app should not proxy every download; validating
  content type by content, not extension; orphaned-object cleanup.
- **Status:** ☐ Not started.

---

## PHASE 8 — Search  *(module: `search`)*

#### ☐ P8-1 · Message full-text search — *search*
- **Goal:** Search message history within rooms the user belongs to.
- **Depends on:** P3-4
- **Scope:** PostgreSQL `tsvector` column + GIN index, ranked queries, **results filtered by
  membership**.
- **Tests:** Relevance ordering; a non-member's results never include that room; index is actually
  used (`EXPLAIN`).
- **Docs:** New vault note `backend/module/search/01-Message-Search.md`.
- **Interview concepts:** `tsvector`/`tsquery`; GIN vs GiST; stemming and language configuration;
  why authorization must be inside the query, not applied after.
- **Status:** ☐ Not started.

---

## PHASE 9 — Redis Optimization  *(cross-cutting)*

#### ☐ P9-1 · Redis integration groundwork — *common*
- **Goal:** A deliberate, documented first use of Redis.
- **Depends on:** the first feature that genuinely needs it
- **Scope:** Add the Redis starter, connection configuration from the environment, serialization
  strategy, and health check. Answer `CLAUDE.md` §9's five questions before writing code.
- **Tests:** Integration test against a Testcontainers Redis; verify graceful behavior when Redis is
  unavailable.
- **Docs:** ADR `docs/decisions/` recording purpose, key schema, TTL, consistency, failure mode.
- **Interview concepts:** Cache-aside vs write-through; TTL selection; serialization pitfalls;
  designing for Redis being down.
- **Status:** ☐ Not started.

#### ☐ P9-2 · Hot-path caching — *chat* / *users*
- **Goal:** Reduce database load on the highest-traffic reads.
- **Depends on:** P9-1
- **Scope:** Cache recent room messages and/or user lookups; explicit invalidation on write.
- **Tests:** Cache hit and miss paths; invalidation on update; correctness when the cache is cold.
- **Docs:** Extend the relevant module notes.
- **Interview concepts:** Cache invalidation; thundering herd and stampede protection; measuring
  before optimizing.
- **Status:** ☐ Not started.

#### ☐ P9-3 · Multi-instance WebSocket fan-out — *chat*
- **Goal:** A message sent on instance A reaches a subscriber connected to instance B.
- **Depends on:** P9-1, P4-2
- **Scope:** Redis pub/sub relay (or an external broker) replacing the single-node simple broker.
- **Tests:** Two application instances; a message crosses between them.
- **Docs:** ADR for the broker decision; extend the WebSocket note.
- **Interview concepts:** Why an in-memory broker cannot scale horizontally; pub/sub vs a durable
  broker; sticky sessions and their costs.
- **Status:** ☐ Not started.

---

## PHASE 10 — Reliability and Distributed-System Improvements

#### ☐ P10-1 · Message idempotency and client retries
Client-supplied message IDs so a retry after a network failure does not duplicate a message.
**Depends on:** P4-2 · **Tests:** duplicate submit creates one row · **Concepts:** idempotency keys,
exactly-once as an illusion, unique constraints as the enforcement point. · **Status:** ☐

#### ☐ P10-2 · Rate limiting
Per-user limits on send and auth endpoints. **Depends on:** P9-1 · **Tests:** limit enforced, resets
correctly · **Concepts:** token bucket vs sliding window, distributed counters, `429` semantics.
· **Status:** ☐

#### ☐ P10-3 · Graceful degradation and timeouts
Explicit timeouts and fallbacks for every external dependency. **Depends on:** P9-1 · **Tests:**
behavior when Redis/FCM/S3 is unavailable · **Concepts:** circuit breakers, bulkheads, why an
unbounded timeout is a latent outage. · **Status:** ☐

#### ☐ P10-4 · Concurrency correctness review
Audit the paths where two requests race: registration, room join, read receipts. **Depends on:**
P3-2 · **Tests:** concurrent-execution tests · **Concepts:** optimistic vs pessimistic locking,
isolation levels, TOCTOU. · **Status:** ☐

---

## PHASE 11 — Observability

#### ☐ P11-1 · Structured logging
Logback with JSON output, correlation/request IDs propagated through REST and WebSocket. **Tests:**
log output shape · **Concepts:** MDC, correlation IDs across async boundaries, never logging
secrets. · **Status:** ☐

#### ☐ P11-2 · Application metrics
Micrometer + Actuator: message throughput, active connections, auth failures, latency histograms.
**Concepts:** counters vs gauges vs histograms, cardinality explosion, RED/USE method. · **Status:** ☐

#### ☐ P11-3 · Prometheus and Grafana
Scrape configuration and dashboards in `monitoring/`. **Depends on:** P11-2, P12-2 · **Concepts:**
pull vs push metrics, alert design, SLIs and SLOs. · **Status:** ☐

#### ☐ P11-4 · Log aggregation with Loki
Ship structured logs; correlate logs with metrics. **Depends on:** P11-1, P11-3 · **Concepts:** log
aggregation vs indexing, label cardinality, retention cost. · **Status:** ☐

---

## PHASE 12 — Docker and Production Deployment

#### ☐ P12-1 · Production configuration hardening
Remove the `spring.profiles.default=dev` foot-gun; introduce versioned migrations (Flyway or
Liquibase — **ask first**); set `ddl-auto=validate`/`none` outside `dev`; a real `prod` profile.
**Depends on:** P0-3 · **Tests:** migrations apply from empty against Testcontainers PostgreSQL ·
**Concepts:** schema migration vs auto-DDL, forward-only migrations, config precedence. · **Status:** ☐

#### ☐ P12-2 · Backend Dockerfile and Compose
Multi-stage build, non-root user, pinned base images, `.dockerignore`; Compose for api + postgres
(+ redis if real). **Depends on:** P12-1 · **Tests:** image builds, container starts, health check
passes · **Concepts:** layer caching, build vs runtime images, why secrets never go in an image.
**Prerequisite:** Docker is not installed on this machine. · **Status:** ☐

#### ☐ P12-3 · Frontend container and Nginx
Production Next.js image; Nginx reverse proxy with WebSocket upgrade handling and TLS termination.
**Depends on:** P0-8 (⏸ deferred), P12-2 · **Concepts:** proxying WebSocket upgrades, TLS
termination, static asset caching. · **Status:** ☐ — transitively blocked while P0-8 is deferred.
The Nginx/WebSocket-upgrade half could be split out and done against the API alone if the frontend
is still deferred by the time Phase 12 is reached.

---

## PHASE 13 — Kubernetes

#### ☐ P13-1 · Base manifests
Deployments, Services, ConfigMaps, Secrets, liveness/readiness probes wired to Actuator.
**Depends on:** P12-2, P0-7 · **Concepts:** probe semantics, rolling updates, requests vs limits.
· **Status:** ☐

#### ☐ P13-2 · Scaling and session affinity
Horizontal scaling with WebSocket connections; Ingress. **Depends on:** P13-1, P9-3 ·
**Concepts:** why WebSocket scaling needs either sticky sessions or a shared broker, HPA, graceful
shutdown and connection draining. · **Status:** ☐

#### ☐ P13-3 · Helm packaging
Chart with environment-specific values. **Depends on:** P13-2 · **Concepts:** templating vs
duplication, release lifecycle. · **Status:** ☐

---

## PHASE 14 — CI/CD

#### ☐ P14-1 · Build and test pipeline
GitHub Actions: build, test, cache dependencies, run on PRs. **Depends on:** P0-2 · **Concepts:**
pipeline as a quality gate, caching, fail-fast. · **Status:** ☐

#### ☐ P14-2 · Code quality gates
Checkstyle + SpotBugs wired into the build and the pipeline. **Depends on:** P14-1 · **Concepts:**
static analysis vs tests, ratcheting rules into a legacy codebase. · **Status:** ☐

#### ☐ P14-3 · Image build and publish
Build and push images on merge, tagged by commit. **Depends on:** P12-2, P14-1 · **Concepts:**
immutable image tags, provenance, registry authentication in CI. · **Status:** ☐

#### ☐ P14-4 · Deployment automation
Automated deploy to a target environment with rollback. **Depends on:** P13-1, P14-3 · **Concepts:**
deployment strategies, rollback as a first-class operation. · **Status:** ☐

---

## PHASE 15 — Final Production Hardening

#### ☐ P15-1 · Security review
Dependency scan, header hardening, CORS policy, input validation audit, secret handling audit.
**Concepts:** OWASP Top 10 as applied to this codebase, defense in depth. · **Status:** ☐

#### ☐ P15-2 · Load and soak testing
Measure real throughput and latency for REST and WebSocket; find the actual bottleneck.
**Depends on:** P11-2 · **Concepts:** open vs closed workload models, percentiles over averages,
connection limits. · **Status:** ☐

#### ☐ P15-3 · Backup, recovery, and runbook
Database backup/restore procedure, documented failure playbooks. **Concepts:** RPO/RTO, testing
restores rather than assuming them. · **Status:** ☐

#### ☐ P15-4 · Documentation and portfolio polish
README with architecture diagram and run instructions, ADR index, an interview-ready walkthrough of
the system. **Concepts:** explaining a system to an audience that has not read the code. · **Status:** ☐
