# Backend Agent Instructions

Apply the repository instructions in `/AGENTS.md` and use the referenced documentation as the authority for architecture, API, database, security, and tests.

- Use Java 21, Spring Boot 4, and Maven. Do not introduce unapproved backend dependencies or frameworks.
- Organize code package-by-feature. The approved modules and ownership boundaries are in `docs/architecture.md`; modules must not access another module's repositories without explicit architectural justification and approval.
- Controllers handle HTTP/WebSocket transport concerns and DTO validation; services own feature use cases and transaction boundaries; repositories own persistence access. Do not expose entities as API contracts.
- Use request/response DTOs at transport boundaries and follow the API/error conventions in `docs/api.md`.
- Keep transactions at appropriate feature-service operations. Use PostgreSQL as durable relational storage; use versioned migrations for production schema changes and do not rely on `ddl-auto=update` for production.
- Redis is supplemental only. Define and approve its purpose, expiry, consistency, and failure behaviour before use.
- Follow Spring Security plus the approved JWT direction. Persisted users must authenticate through the application security system; do not add token lifetime, refresh, role, or authorization details that are not explicitly decided.
- WebSocket/STOMP work must include authenticated connections, destination authorization, and relevant testing; detailed realtime interaction semantics remain subject to feature design.
- Add and run relevant JUnit 5 tests. Use Mockito, Testcontainers, controller/security, repository/integration, and WebSocket tests only as applicable and approved by the stack; never claim tests passed unless executed.
