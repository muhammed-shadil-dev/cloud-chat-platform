---
description: Implement the next incomplete task from task.md, with tests, docs, learning notes, and a commit
---

Execute the `/next` workflow defined in `CLAUDE.md` §18. Do this now, in order.

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

## 5. Test for real

Run the relevant tests: `apps/api/mvnw -B test` for the backend.

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

## 9. Generate learning material and interview questions

This repository is placement preparation as much as it is a product. The note's §14 (Lessons
Learned), §15 (Common Mistakes), and §17 (Interview Questions) must be grounded in the code you just
wrote — specific, with concise answers. No generic "what is Spring Boot" filler.

## 10. Commit

Conventional commit: `type(scope): description`. **Stage explicitly by path** (`git add <paths>`) so
the developer's unrelated in-progress work is never swept in. Verify no secrets are staged.

## 11. Push

Push to `origin` if it is configured and the push is a normal fast-forward.

**If local and remote history have diverged, STOP and report it.** Do not force-push, do not reset,
do not rewrite history, ever. If the push fails, say so — never claim a push that did not happen.

## 12. Report

Use the exact **Final response format** from `CLAUDE.md` §18: Completed · Files Changed · Tests ·
Architectural Decisions · What You Need to Learn · Interview Questions · Obsidian Note · Next Task.

No extra narration — the full explanation belongs in the Obsidian note, not the terminal.

---

**Implement one meaningful task per run**, even if the next few look small or related. Stop after
the report.
