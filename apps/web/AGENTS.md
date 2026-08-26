# Frontend Agent Instructions

Apply the repository instructions in `/AGENTS.md` and use `docs/architecture.md`, `docs/tech-stack.md`, `docs/api.md`, `docs/security.md`, `docs/testing.md`, and `docs/task.md` as the authority.

- Use the approved Next.js, React, TypeScript, Tailwind CSS, and shadcn/ui stack. Do not add unapproved frontend frameworks, libraries, or dependencies.
- Organize UI code by feature. Keep reusable UI primitives distinct from feature behaviour and keep API/service clients separate from components.
- Keep server/API state, local UI state, and authentication state explicit; do not introduce a state-management library or persistence model without approval.
- Implement accessible semantic UI: keyboard operation, visible focus, labels, appropriate contrast, and meaningful loading/error states.
- Treat tokens and sensitive data as security-sensitive. Follow the backend/API security design; do not embed secrets, trust client authorization, or log credentials/tokens.
- Add and run relevant frontend/component/integration tests once the frontend test tooling is approved and present. Never claim unexecuted tests passed.
