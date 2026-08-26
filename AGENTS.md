# Agent Instructions — Cloud Chat Platform

**`CLAUDE.md` in this directory is the engineering contract for this repository.** Read it before
doing any work here, whichever AI coding agent you are. `task.md` is the single source of truth for
what is implemented and what comes next.

Short version of the rules that matter most:

- This is an **existing** project. Inspect before you change. Do not recreate the application, do not
  replace the architecture, do not duplicate features that already exist.
- The backend is a **modular monolith, organized package-by-feature**. No microservices. No
  `presentation/application/domain/infrastructure` layer packages.
- A technology being in the stack is permission, not instruction. Add a dependency only when a task
  in `task.md` requires it.
- Do not create empty directories or packages for appearance.
- Tests are part of the work. Never claim a test passed without running it.
- Never force-push, rewrite history, or discard the developer's uncommitted changes.

`apps/api/AGENTS.md` and `apps/web/AGENTS.md` carry backend- and frontend-specific rules. Note that
they currently reference `docs/architecture.md`, `docs/api.md`, `docs/security.md`,
`docs/testing.md`, and `docs/task.md`, **which do not exist yet** — until they are written,
`CLAUDE.md` and `task.md` are authoritative.
