---
description: Implement the next incomplete task from task.md, with tests, docs, learning notes, and a commit
---

Execute the `/next` workflow defined in `CLAUDE.md` §18. Do this now, in order.

```text
Read task.md
      ↓
Understand existing architecture
      ↓
Implement feature
      ↓
Run automated tests
      ↓
Run build
      ↓
Inspect implementation
      ↓
Create/update feature note
      ↓
Create manual verification guide      ← the developer runs this, not Claude
      ↓
Commit
      ↓
Push
```

> **The verification boundary.** Claude runs the *automated* tests and reports their real output.
> **Manual verification — Postman, PostgreSQL, Redis, WebSocket/STOMP, file storage, logs,
> Actuator — is performed by the developer.** Claude writes the guide and stops there. Never claim
> a manual check was performed, never fill in an "Actual Result", and never mark a note
> `✅ Fully Verified` — that happens only after the developer reports the results back.

## 1. Load the contract

Read `CLAUDE.md` and `task.md` **in full**. They are the authority on architecture, conventions,
definition of done, and what is already built.

## 2. Inspect reality

Do not trust `task.md` blindly — a checked box only means someone verified it once. Inspect the
actual repository: the file tree, `apps/api/pom.xml`, the module packages under
`apps/api/src/main/java/com/example/chat_app/`, the existing tests, `git status`, and `git log`.

Confirm the working tree state before touching anything. If there are uncommitted changes you did
not make, they belong to the developer — work around them, never revert or absorb them.

## 3. Choose the task

Find the highest-priority incomplete task in `task.md` whose dependencies are satisfied. Work
top-to-bottom within the current phase; do not skip ahead across phases.

Then, before writing code:

- **Check whether it is already implemented.** If it is, mark it complete with evidence (the code
  that proves it, the tests that cover it) instead of writing duplicate code — and move to the
  genuinely next task.
- **Check its dependencies.** If a dependency is incomplete, say so and implement the correct task
  instead of the numerically next one.
- **Check for a required decision.** If the task's scope says a decision is needed (migration tool,
  JWT strategy, storage backend, delivery semantics), stop and ask the developer. Do not choose
  silently.

## 4. Explain, then implement

State in one or two sentences what you are about to implement and why it is next. Then implement
**only that task's scope**:

- Package-by-feature; respect module boundaries (`CLAUDE.md` §4, §5).
- Controllers do transport, services own use cases and transactions, repositories do persistence.
- DTOs at every boundary — never expose entities.
- Constructor injection. Readable code over clever code.
- No placeholder or fake implementations to make a task look done.
- No unrelated changes, no drive-by refactors, no dependencies the task does not need.

## 5. Test for real, and build

Run the relevant tests: `apps/api/mvnw -B test` for the backend. That runs the build too — if you
changed dependencies or configuration, confirm the build itself succeeded and not just the tests.

If anything fails: **stop, explain the failure, fix the root cause, rerun.** Never mark the task
complete over a failing test. Never disable a test or weaken an assertion to get a green build.
Never report a result you did not observe.

If a test genuinely cannot run in this environment (no Docker daemon, no database, dependencies not
installed), say so explicitly and report it as a prerequisite. Do not pretend it passed and do not
quietly skip it.

## 6. Review your own work

Before committing, re-read the diff as a reviewer would: Is anything left unfinished? Any secret,
`.env`, or unrelated file staged? Any entity leaking through an API? Any boundary violated? Any
error path unhandled? Fix what you find.

## 7. Update `task.md`

Mark the task complete and add an entry under that phase's **Implementation Notes**: what was built,
which tests ran and their real results, and the engineering decisions made (including anything you
deliberately did *not* do).

## 8. Write the Obsidian note

Per `CLAUDE.md` §16. Use `docs/templates/obsidian-feature-note.md` — all 17 sections, writing
"Not applicable — {reason}" rather than deleting one. The note goes in the module folder under:

```
C:\Users\Admin\Documents\vault\note\03-Projects\cloud-based-chat-app\backend\module\{module}\
```

Foundation (Phase 0) tasks are not features — they append a short entry to
`...\cloud-based-chat-app\architecute\Phase 0 — Foundation.md` instead.

Describe the *actual* implementation, not the template's generic flow. Never rewrite or delete
pre-existing vault notes.

Record the automated results in **§10.1** — real, executed output only.

## 9. Write the manual verification guide (§10.2–§10.11)

This is a **guide for the developer to run**, not a report of work you did.

Before writing a single line of it, **re-inspect the real implementation**: controllers, services,
repositories, entities, DTOs, mappers, security configuration, the schema Hibernate actually
generates from the entities, Redis configuration and usage, WebSocket configuration and STOMP
destinations, storage implementation, Actuator configuration, tests, `application.properties`,
`application-dev.properties`, and Docker configuration where relevant.

Fill in only the subsections this feature actually reaches:

| | Applies when |
|---|---|
| 10.2 Manual API Testing (Postman) | the feature adds or changes an HTTP endpoint |
| 10.3 Negative API Testing | any endpoint with reachable failure modes |
| 10.4 PostgreSQL Verification | the feature touches the database |
| 10.5 Redis Verification | the feature uses Redis |
| 10.6 WebSocket / STOMP Verification | the feature is real-time |
| 10.7 Auth / Authorization Verification | the endpoint is secured |
| 10.8 File / Media Verification | the feature uses MinIO/S3 |
| 10.9 Actuator / Health Verification | the feature changes health/readiness behavior |
| 10.10 Log Verification | any feature with meaningful log output |
| 10.11 Checklist + Verification Status | always |

Everything else gets `Not applicable — {reason}`. Never delete a subsection.

**Never invent** an endpoint, HTTP method, port, database table, column, Redis key, WebSocket
destination, request field, response field, authentication flow, or log message. Every one of these
must come from code you just read. If a check cannot be run in the current environment, write
exactly: `Manual verification required — environment unavailable.`

Leave every **Actual Result** as `[To be filled after manual verification]`, and set:

```text
Verification Status:

⏳ Implementation Complete — Manual Verification Pending
```

Only when the developer later reports their manual results do you update those fields and change
the status to `✅ Fully Verified`. Passing automated tests never justify that change.

Then write **§10.12**: what each verification layer proves, why automated and manual testing catch
different bugs, and which bugs each layer is blind to — specific to this feature.

## 10. Generate learning material and interview questions

This repository is placement preparation as much as it is a product. The note's §14 (Lessons
Learned), §15 (Common Mistakes), and §17 (Interview Questions) must be grounded in the code you just
wrote — specific, with concise answers. No generic "what is Spring Boot" filler.

## 11. Commit

Conventional commit: `type(scope): description`. **Stage explicitly by path** (`git add <paths>`) so
the developer's unrelated in-progress work is never swept in. Verify no secrets are staged.

## 12. Push

Push to `origin` if it is configured and the push is a normal fast-forward.

**If local and remote history have diverged, STOP and report it.** Do not force-push, do not reset,
do not rewrite history, ever. If the push fails, say so — never claim a push that did not happen.

## 13. Report

Use the exact **Final response format** from `CLAUDE.md` §18: Completed · Files Changed · Tests ·
Manual Verification · Architectural Decisions · What You Need to Learn · Interview Questions ·
Obsidian Note · Next Task.

Under **Manual Verification**, state which subsections the guide covers and that the status is
`⏳ Implementation Complete — Manual Verification Pending`. Point the developer at the note. Do not
describe manual checks as done.

No extra narration — the full explanation belongs in the Obsidian note, not the terminal.

---

**Implement one meaningful task per run**, even if the next few look small or related. Stop after
the report.
