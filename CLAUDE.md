# Cloud Chat Platform — Project Instructions

This file is the permanent engineering contract for this repository. It governs every Claude Code
session here and overrides default behavior.

Before doing any work: read `task.md` (single source of truth for progress), then **inspect the
actual repository state**. Never assume a feature exists because it appears in the stack, and never
assume it is missing because you don't remember it. Verify against code.

This is a flagship Backend/SDE portfolio project on the path:
**Backend Software Engineering → Distributed Systems → AI Infrastructure Engineering.**
The goal is production-quality backend engineering, not a CRUD chat app. Equally important: the
developer must *understand* every line that ships here — this repo doubles as placement
preparation, so implementation is never complete without learning material (§16, §17).

The developer drives this project with short prompts: `/next`, `continue`, `explain current
feature`, `fix tests`, `prepare interview`. Each must be resolvable from this file + `task.md` +
the repo alone, with no prior conversation context.

---

## 1. Project Purpose

A cloud-based, real-time chat platform: authenticated users join rooms and exchange messages over
WebSocket, with presence, notifications, media, and search layered on a durable PostgreSQL core and
a Redis coordination layer.

What this project is trying to prove, in order:

1. Clean modular backend design that survives feature growth.
2. Correct authentication and authorization.
3. Real-time messaging with well-understood delivery semantics.
4. Distributed-systems reasoning: state, caching, fan-out, failure, idempotency.
5. Operability: observability, containerization, CI/CD.

---

## 2. Architecture

```
                 +--------------------------+
   Browser ----->|  apps/web  (Next.js)     |
                 +------------+-------------+
                              | REST + WebSocket/STOMP
                 +------------v-------------+
                 |  apps/api (Spring Boot)  |
                 |   modular monolith       |
                 |   package-by-feature     |
                 +---+------------------+---+
                     |                  |
             +-------v------+    +------v------+
             |  PostgreSQL  |    |    Redis    |
             |  durable     |    | supplemental|
             +--------------+    +-------------+
```

Repository layout (only `apps/` currently contains code):

```
cloud-chat-platform/
├── apps/
│   ├── api/          Spring Boot modular monolith  (has code)
│   └── web/          Next.js frontend              (scaffold only, not initialized)
├── docker/           empty — populated in Phase 12
├── deployment/       empty — populated in Phase 13
├── monitoring/       empty — populated in Phase 11
├── docs/             repo documentation + templates
├── scripts/          empty — populated when a script is actually needed
├── CLAUDE.md         this file
└── task.md           progress board
```

**The backend stays a modular monolith.** Do not introduce microservices. Do not introduce Clean
Architecture layer packages (`presentation/`, `application/`, `domain/`, `infrastructure/`) unless
one module has grown complex enough to justify it *and* the developer has approved the refactor.

**Empty directories are not architecture.** `docker/`, `deployment/`, `monitoring/`, and `scripts/`
exist but are empty. Do not fill them, and do not create new module packages, until a task in
`task.md` actually requires it. A repo with fifty empty production-looking folders is worse than an
honest small one.

---

## 3. Technology Stack

### Currently in the build (verified — `apps/api/pom.xml`)

Java 21 · Spring Boot 4.0.6 · Maven (wrapper 3.9.16) · Spring Web MVC · Spring Security ·
Spring Data JPA / Hibernate · PostgreSQL driver · Bean Validation · Lombok ·
JUnit 5 + Mockito + AssertJ (via Boot test starters).

### Approved for the roadmap, NOT yet added

Redis · Spring WebSocket + STOMP · JWT · OpenAPI/Swagger · Actuator · Micrometer · Testcontainers ·
Checkstyle · SpotBugs · Docker · Kubernetes · Nginx · GitHub Actions · Prometheus · Grafana ·
Logback + Loki · MinIO or AWS S3 · Firebase Cloud Messaging · PostgreSQL Full-Text Search.

### Frontend

React · Next.js · TypeScript · Tailwind CSS · shadcn/ui.
`apps/web` is currently **not initialized** — `package.json`, `next.config.ts`, and `tsconfig.json`
are 0-byte placeholders and there is no `node_modules`.

> **Critical rule.** A technology appearing in the stack is *permission*, not *instruction*. Never
> add a dependency, config file, or infrastructure component because it is "in the stack." Add it
> only when a specific `task.md` task requires it, and record in the task notes why it was needed.
> Every added dependency must be defensible in an interview.

---

## 4. Package-by-Feature Rule

Backend code is organized **by feature, not by technical layer**. Base package:
`com.example.chat_app`.

```
com.example.chat_app
├── ChatAppApplication.java
├── common/                     cross-cutting only
│   ├── config/
│   ├── dto/
│   └── exception/
├── auth/
│   ├── controller/
│   ├── dto/
│   ├── security/
│   └── service/
└── users/
    ├── entity/
    ├── repository/
    └── service/
```

Rules:

- A feature module owns its controllers, services, repositories, entities, and DTOs.
- **A module contains only the packages it actually needs.** Never create an empty package for
  symmetry or appearance.
- Never create a top-level layer package (`controllers/`, `services/`, `repositories/`) spanning
  features.
- `common/` is for genuinely cross-cutting concerns only (global exception handling, shared error
  DTOs, framework configuration no single feature owns). If something is used by exactly one
  feature, it belongs in that feature — not in `common/`.

Planned module set — create a module's package **only when its first task is implemented**:
`auth`, `users`, `chat`, `rooms`, `presence`, `notifications`, `media`, `search`.

---

## 5. Module Boundaries

- Modules communicate **service-to-service**, never repository-to-repository. A module may inject
  another module's `@Service`; it must not inject another module's `@Repository` or reach into its
  entities' persistence.
  *Existing example:* `auth.service.AuthService` calls `users.service.UserRegistrationService`; it
  never touches `users.repository.UserRepository` directly. Preserve this pattern — it is the
  single most important structural decision in the codebase so far.
- A module owns its entities. Cross-module references prefer IDs over JPA associations unless a
  real relational constraint is needed; when an association is used, document why.
- No circular module dependencies. If A needs B and B needs A, extract the shared concept or invert
  one direction with an event — and record the decision in `task.md`.
- DTOs do not cross module boundaries. Each module defines its own request/response DTOs.
- If a boundary must be broken, stop and ask. Do not silently couple modules.

---

## 6. Backend Conventions

- **Controllers** handle transport only: HTTP/WebSocket mapping, DTO binding, `@Valid`, status
  codes. No business logic, no repository access.
- **Services** own use cases, business rules, and transaction boundaries — `@Transactional` on the
  service method that defines the unit of work, never on the controller.
- **Repositories** own persistence access only. Spring Data JPA interfaces; custom queries when a
  derived query name becomes unreadable.
- **Never expose entities in API contracts.** Requests and responses are always DTOs. Never return a
  `User` from a controller; never accept one as a request body.
- Use **constructor injection**. No `@Autowired` fields, no field injection.
- Lombok is allowed for boilerplate (`@Getter`, `@Builder`, `@Data` on DTOs). Do not use Lombok to
  hide behavior; write real methods when logic is involved.
- Exceptions: throw a specific domain exception, handle it in
  `common/exception/GlobalExceptionHandler`, map it to a status code and an `ErrorResponse`. Never
  return raw stack traces or framework exception messages to clients.
- Validation lives on the request DTO via Bean Validation annotations with human-readable `message`
  values. `MethodArgumentNotValidException` is handled globally.
- Logging: SLF4J. Log decisions and failures — never credentials, tokens, password hashes, or
  message bodies.
- Configuration values come from `application*.properties` with environment-variable indirection
  (`${DB_PASSWORD:}`). No secrets hard-coded, ever.

---

## 7. Frontend Conventions

`apps/web` is a scaffold and must be initialized as a real Next.js project (Phase 2 or later) before
any frontend feature work.

- Organize UI **by feature**, mirroring backend module names where sensible (`features/auth/`,
  `features/chat/`). Keep reusable primitives (`components/ui/`) separate from feature behavior.
- API access lives in `services/` — typed clients, one per backend module. Components never call
  `fetch` directly.
- Keep three kinds of state explicit and separate: server/API state, local UI state, auth state. Do
  not add a state-management library without a stated reason.
- TypeScript strict. No `any` in committed code.
- Accessibility is not optional: semantic elements, keyboard operation, visible focus, labels,
  meaningful loading and error states.
- Never store secrets in the frontend, never trust client-side authorization, never log tokens.
- Do not run `npm install` or add frontend dependencies as a side effect of a backend task.

---

## 8. Database Conventions

- PostgreSQL is the durable source of truth. Anything that must survive a restart lives here.
- **Current state:** `spring.jpa.hibernate.ddl-auto=update`, in the `dev` profile only. This is a
  development convenience and is explicitly **not acceptable for production**.
- Before any production/Docker deployment task, versioned migrations (Flyway or Liquibase) must be
  introduced as their own `task.md` task, with `ddl-auto` set to `validate` or `none` outside `dev`.
  Do not pick the migration tool unilaterally — ask.
- Table and column names are `snake_case`; entity fields are `camelCase` with explicit `@Column`
  names where they differ.
- Every uniqueness rule enforced in application code must **also** be a database constraint. An
  application-level `existsBy...` check is a race, not a guarantee — the DB constraint is the real
  one, and `DataIntegrityViolationException` must be handled.
- Add an index whenever a column is used for lookup, join, or ordering on a table expected to grow.
  State the reason in the task notes.
- Timestamps: `LocalDateTime` via `@PrePersist` today. If the platform gains multi-region or
  client-timezone concerns, migrating to UTC `Instant` / `timestamptz` becomes its own task.
- Never write a query inside a controller. Never use `EntityManager` directly without a reason.

---

## 9. Redis Conventions

Redis is **not yet a dependency** and must not be added until a task requires it (Phase 9 for
caching; earlier only if presence or WebSocket scaling genuinely needs it).

When Redis is introduced, every use must answer these five questions **in writing** — in the task
notes and the Obsidian note — before the code is written:

1. **Purpose** — cache, session store, pub/sub fan-out, rate limiter, presence set, or lock?
2. **Key schema** — exact key format and naming convention.
3. **Expiry** — TTL, and why that value.
4. **Consistency** — how it stays in sync with PostgreSQL; who invalidates, and when.
5. **Failure behavior** — what happens when Redis is down. *Redis being unavailable must never lose
   durable data.* Degrade, don't corrupt.

Redis is supplemental. PostgreSQL remains the source of truth. Never make Redis the only place a
piece of durable state exists.

---

## 10. WebSocket Conventions

Not yet implemented — no WebSocket dependency, config, or endpoint exists.

When real-time messaging is implemented (Phase 4):

- Spring WebSocket + STOMP. SockJS fallback only if a real browser-compatibility need exists.
- **The handshake must be authenticated.** A WebSocket connection is not a security-free zone — the
  same identity that authenticates REST calls must authenticate the STOMP CONNECT.
- **Destination authorization is mandatory.** Subscribing to `/topic/room.{id}` must verify the
  principal is a member of that room. Never rely on the client to subscribe only to what it should.
- Messages are persisted to PostgreSQL before or as part of broadcast — define and document the
  ordering. Decide and write down the delivery semantics (at-most-once / at-least-once) rather than
  leaving them accidental.
- A single-node in-memory STOMP broker does not survive horizontal scaling. When multi-instance
  becomes a goal, that is its own task (Redis pub/sub relay or an external broker) with its own
  written rationale.
- Payloads are DTOs, same as REST. Entities never go over the wire.

---

## 11. Security Conventions

Current state: Spring Security with HTTP Basic; `/api/auth/**` permitted, everything else
authenticated; CSRF disabled. `CustomUserDetails` exists but **no `UserDetailsService` is wired** —
the app still boots Spring Boot's `inMemoryUserDetailsManager`, so no database user can currently
authenticate. JWT is not implemented.

Rules:

- Passwords are stored **only** as BCrypt hashes (`common/config/PasswordConfig`). Never log,
  return, or compare a raw password outside the encoder.
- Never return a password hash in any DTO.
- Login must not leak which of username/email/password was wrong — one generic "invalid credentials"
  response.
- JWT, when implemented: signing algorithm, secret source (environment only), token lifetime, and
  refresh strategy must be explicit decisions written in the task notes — never defaults chosen by
  accident. Secrets come from the environment, never from a properties file in Git.
- CSRF is currently disabled because the API is token-oriented and stateless. If cookie-based auth is
  ever introduced, CSRF protection must be revisited in the same task.
- Authorization is checked server-side, always. Authorities are currently empty — do not invent a
  role model until a task defines one.
- Never commit `.env`, credentials, private keys, tokens, passwords, or API secrets. Use
  `apps/api/.env.example` to document required variables with placeholder values.

---

## 12. Testing Requirements

- Every behavioral change ships with tests. A task is not complete without them.
- **Unit tests** (JUnit 5 + Mockito) for service logic — the default. Mock collaborators; assert
  behavior and interactions.
- **Controller/security tests** (`@WebMvcTest` + Spring Security test support) for endpoints: status
  codes, validation failures, auth rules.
- **Repository/integration tests** with Testcontainers once PostgreSQL-specific behavior matters
  (constraints, indexes, full-text search). Testcontainers is not yet a dependency — add it with the
  task that first needs it, and note that it requires a running Docker daemon.
- **WebSocket tests** when Phase 4 lands.
- Run tests with `apps/api/mvnw -B test`.
- **Never claim a test passed without running it.** Report real output. If tests fail: stop, fix the
  cause, rerun. Never mark a task complete over a failing test, never add `@Disabled` to make a build
  green, never weaken an assertion to fit broken code.
- If a test cannot run in this environment (no Docker daemon, no database, dependencies not
  installed), say so explicitly and report it as a prerequisite — do not silently skip it, and do not
  pretend.

---

## 13. Docker Rules

Nothing is containerized yet. `docker/` is empty, and there is **no Docker CLI on this machine**.

- Docker work belongs to Phase 12 and must not start early.
- When it happens: multi-stage build for `apps/api` (build layer + slim JRE runtime), non-root user,
  no secrets baked into images, a `.dockerignore` alongside every Dockerfile.
- `docker-compose.yml` for local development (api, postgres — and redis only once Redis is real).
  Compose is a development tool, not a deployment strategy.
- Pin base image tags. Never `:latest` in a committed file.
- Never write a Dockerfile for a service that does not exist yet.

---

## 14. Git Conventions

- **Conventional commits**: `type(scope): description` —
  `feat(auth): implement jwt authentication`, `feat(chat): implement websocket messaging`,
  `fix(auth): prevent duplicate registration`, `test(users): add registration service tests`,
  `docs(auth): document login flow`, `chore(build): add redis dependency`.
  Never `update`, `changes`, `work`, `final`, `fix`.
- Before committing: run `git status`, review `git diff`, run the relevant tests, and confirm no
  secrets are staged.
- **Stage explicitly.** Prefer `git add <paths>` over `git add -A`, so unrelated in-progress work in
  the developer's working tree is never swept into a commit.
- Standing authorization: for a verified, complete task in the `/next` flow, committing and pushing
  to `origin` may be done without asking each time.
- **Absolutely never**: force push, rewrite or delete history, `git reset --hard`, `git clean -fd`
  over the developer's work, or discard uncommitted changes you did not create.
- If local and remote history have diverged, **stop and report**. Do not resolve it by force.
- Remote: `origin` → `github.com/muhammed-shadil-dev/cloud-chat-platform`. If a push fails, say so —
  never claim a push that did not happen.

---

## 15. Documentation Requirements

- `task.md` is the progress board and is updated in the same commit as the work it describes.
- `README.md` describes what the project is and how to run it. Keep the "how to run" section true — a
  README documenting a command that does not work is a bug.
- Architecture decisions that constrain future work go in `docs/decisions/NNNN-title.md` (short ADR:
  context, decision, consequences). Write one whenever you make a choice a reviewer would ask "why?"
  about — migration tool, JWT strategy, broker choice, delivery semantics.
- `apps/api/AGENTS.md` and `apps/web/AGENTS.md` are instructions for other AI coding agents. They
  currently reference `docs/architecture.md`, `docs/api.md`, `docs/security.md`, `docs/testing.md`,
  and `docs/task.md`, **none of which exist**. Until those are written, **this file and `task.md` are
  authoritative**. Do not invent those documents to satisfy the references; write them only when a
  task calls for them.
- Document what exists. Never write documentation describing a component that has not been built.

---

## 16. Obsidian Note Requirements

Vault root for backend feature notes:

```
C:\Users\Admin\Documents\vault\note\03-Projects\cloud-based-chat-app\backend\module\
```

One folder per backend module: `auth/`, `users/`, `chat/`, `rooms/`, `presence/`, `notifications/`,
`media/`, `search/`. **Create a module folder only when that module's first feature is implemented.**

- Every completed feature gets a note built from the template at
  `docs/templates/obsidian-feature-note.md` — **the canonical copy**. Use all 17 sections; write
  "Not applicable — {reason}" instead of deleting a section.
  The vault keeps a working copy at `...\cloud-based-chat-app\templates\Feature-Template.md`. The
  two are kept in step but are **not identical**: the vault copy also carries a
  `# 18. References` section and writes its §7 heading as `# 7 Security & Access Control` (no
  period). Neither copy is 19 sections — that number comes from the Reliable Job Queue project's
  template, not this one. Follow the repo copy when they disagree.
- Notes describe the **actual implementation**, not the generic template flow. If the feature's
  request flow differs from the template diagram, draw the real one.
- **§10 is "Testing & Verification" and has two halves with different owners.**
  - **§10.1 Automated Tests** — Claude runs these and reports real, executed results. Never invent
    a benchmark or claim a test passed without running it.
  - **§10.2–§10.11 Manual Verification** (Postman, negative cases, PostgreSQL, Redis,
    WebSocket/STOMP, auth, file storage, Actuator, logs, checklist) — **Claude writes the guide;
    the developer performs the verification.** Claude must never claim a manual check was
    performed, never fill in an "Actual Result", and never set `✅ Fully Verified`. Those fields
    stay `[To be filled after manual verification]` and the status stays
    `⏳ Implementation Complete — Manual Verification Pending` until the developer reports back.
    Passing automated tests are not grounds for changing it.
  - **§10.12** explains what each verification layer proves and which bugs it is blind to.
- **Before writing a manual verification guide, inspect the real implementation** — controllers,
  services, repositories, entities, DTOs, security config, the schema the entities generate, Redis
  and WebSocket configuration, storage, Actuator config, and `application*.properties`. **Never
  invent** an endpoint, HTTP method, port, table, column, Redis key, STOMP destination, request or
  response field, auth flow, or log message. If a check cannot run here, write exactly:
  `Manual verification required — environment unavailable.`
- §17 (Interview Questions) must be grounded in this feature's actual code — no generic "what is
  Spring Boot" filler. Include concise answers.
- **Phase 0 exception:** foundation tasks are not features and do not fit the 17-section template.
  They share one lightweight note at
  `...\cloud-based-chat-app\architecute\Phase 0 — Foundation.md`, with a short entry per task.
- Pre-existing notes (`architecute/`, `techstack/`, `backend/other tech/`, and
  `backend/module/auth/00-Overview.md`, `01-Registration.md`, `02-Login.md`) predate this workflow.
  Treat them as historical: read before touching, extend where they describe current reality,
  **never delete and never bulk-rewrite the vault**. When they conflict with the code, the repository
  and this file win.

---

## 17. Definition of Done

A task is done only when **all** of these are true:

1. Implementation matches the task's stated scope — no more, no less.
2. Validation and error handling exist for the paths a real client can hit.
3. Tests are written **and were actually executed and passed** (real output reported).
4. A **manual verification guide** (§10.2–§10.11) exists for every subsection the feature actually
   reaches, built from the real implementation, with results left blank and the status set to
   `⏳ Implementation Complete — Manual Verification Pending`. The guide being *written* is the
   requirement; the guide being *run* is the developer's step, not a blocker on the task.
5. No secrets, credentials, or `.env` files are staged.
6. `task.md` is updated: box checked, implementation notes added (what was built, tests run,
   engineering decisions made).
7. The Obsidian note is created or updated per §16.
8. Learning material and interview questions are produced (§18 report format).
9. Work is committed with a conventional-commit message, and pushed if the remote is reachable.

If any of these cannot be satisfied, the task stays open and the blocker is reported.

---

## 18. Rules for Autonomous Implementation

### Always

- Read `CLAUDE.md` and `task.md`, then verify against the real repository before deciding what to do.
- Implement **exactly one meaningful task** per `/next` run. Stop after the report.
- Check dependencies before starting a task. If a dependency is incomplete, say so and pick the
  correct task instead.
- Check whether the task is *already implemented* before implementing it. If it is, mark it complete
  with evidence rather than writing duplicate code.
- Prefer the smallest correct change. Readable code over clever code. Explicit control flow.
- State assumptions out loud when a task is ambiguous.

### Never

- Force push, delete or rewrite Git history, or destructively reset the repository.
- Overwrite or discard uncommitted changes the developer made.
- Create duplicate features, parallel implementations, or a second class doing what an existing one
  already does.
- Write a placeholder or fake implementation so a task "looks" complete.
- Skip tests silently, or claim tests passed when they were not run.
- Move to the next feature while the current one is incomplete.
- Add a dependency, folder, or config file that no current task requires.
- Introduce microservices, Kubernetes, Terraform, or an observability stack ahead of its phase.
- Modify unrelated code as a side effect of a task.

### Stop and ask when

- Git history has diverged, or a push is rejected.
- A task requires a decision with long-term consequences that `task.md` does not specify (migration
  tool, JWT/refresh strategy, message broker, delivery semantics).
- A module boundary would have to be broken.
- The environment blocks verification (no Docker daemon, no database, dependencies not installed) —
  report it as a prerequisite rather than faking the result.

### Short commands

**`/next` · `continue` · `implement next task`** — the full flow in `.claude/commands/next.md`.

**`explain current feature`** — read-only. Do not modify code. Explain architecture, runtime flow,
important classes, DB/Redis interaction, concurrency, failure modes, design decisions, and give
interview questions.

**`fix tests`** — find the root cause, apply the smallest correct fix, rerun, update the relevant
note if the fix is a real engineering lesson, then commit. Never hide a failing test.

**`prepare interview`** — read the completed features and generate progressively harder questions
(basic → deep) across Java, Spring Boot, REST, security/JWT, PostgreSQL, Redis, WebSocket,
concurrency, distributed systems, reliability, Docker, and system design.

### Final response format (for `/next` / `continue` / `fix tests`)

```
## Completed
- <task id + name>

## Files Changed
- <path> — <what changed>

## Tests
- <command run> — <real result>

## Manual Verification
- Guide written for §<subsections that apply> — ⏳ Implementation Complete — Manual Verification Pending
- Run it from: `<exact note path>`

## Architectural Decisions
- <decision and why>

## What You Need to Learn
- <concept>

## Interview Questions
1. <question>

## Obsidian Note
Created/updated: `<exact note path>`

## Next Task
`<task id + name>` — <one-line why>
```

No extra narration after a task. The full explanation belongs in the Obsidian note, not the terminal.
