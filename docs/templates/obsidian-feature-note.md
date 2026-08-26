# {{Feature Name}}

> Module: {{Module Name}}
> Feature: {{Feature Name}}
> Status: 🚧 In Progress / ✅ Completed
> Version: v1.0
> Last Updated: {{YYYY-MM-DD}}

<!--
Canonical template for feature notes in
C:\Users\Admin\Documents\vault\note\03-Projects\cloud-based-chat-app\backend\module\{module}\

Rules (CLAUDE.md §16):
- Use all 17 sections. If one does not apply, write "Not applicable — {reason}". Never delete it.
- Describe the ACTUAL implementation, not this template's generic flow.
- §10.1 Automated Tests reports only real, executed results. Never invent a passing test.
- §10.2-10.11 Manual Verification is written as a GUIDE for the developer to run. Claude never
  fills in an "Actual Result" and never sets "Fully Verified" on its own - see the rule in §10.
- Before writing any manual verification guide, INSPECT the real implementation (controllers,
  services, repositories, entities, DTOs, security config, schema, Redis/WebSocket config,
  actuator config, application*.properties). Never invent endpoints, ports, tables, columns,
  Redis keys, STOMP destinations, fields, auth flows, or log messages.
- §17 questions must be grounded in this feature's code, with concise answers.
Delete this comment block in the real note.
-->

---

# 1. Purpose

## Problem

Describe the problem this feature solves.

## Goal

Explain what this feature is expected to achieve.

## Business Rules

List the business rules or constraints.

---

# 2. Feature Flow

```text
Client
  ↓
Request
  ↓
Controller
  ↓
Service
  ↓
Business Logic
  ↓
Repository / External Service
  ↓
Database / Cache / Queue
  ↓
Response
```

---

# 3. API

## Endpoint

```http
METHOD /endpoint
```

## Headers

```text
Content-Type: application/json
```

## Request

```json
{
}
```

## Success Response

```json
{
}
```

HTTP Status

```text
200 OK
```

## Error Responses

Document only the errors this feature can actually produce.

---

# 4. Request Flow

Document the actual implementation flow, step by step.

Do not copy the generic diagram from §2 if this feature behaves differently — draw what the code
really does.

---

# 5. Implementation (Class Walkthrough)

## Controller

### Class

### Responsibilities

---

## Service

### Class

### Responsibilities

---

## Repository

### Class

### Methods Used

---

## Entity / Model

### Class

### Fields

---

## DTO

### Request DTO

### Response DTO

### Validation

---

## Configuration

Only document configuration actually used by this feature.

---

# 6. Database

## Tables

## Columns

## Relationships

## Indexes

Only document applicable database structures.

---

# 7. Security & Access Control

## Current

## Future Improvements

---

# 8. Validation

## Objective

## Validation Rules

## Validation Flow

---

# 9. Exception Handling

## Custom Exceptions

## Global Exception Handler

## Exception Flow

---

# 10. Testing & Verification

Two independent layers. **Automated tests (10.1)** are run by Claude and report real executed
results. **Manual verification (10.2–10.11)** is performed by *the developer* — Claude writes the
guide, never the results.

> **Hard rule.** Claude must never claim that Postman, PostgreSQL, Redis, WebSocket, storage, log,
> or Actuator verification was performed. Those fields stay `[To be filled after manual
> verification]` until the developer reports the outcome. A fabricated manual result is worse than
> a blank one — it destroys the reason the section exists.

Subsections that do not apply to this feature get `Not applicable — {reason}`. Do not delete them,
and **never invent** an endpoint, HTTP method, port, table, column, Redis key, STOMP destination,
request/response field, auth flow, or log message to fill one. If the environment cannot support a
check, write exactly: `Manual verification required — environment unavailable.`

---

## 10.1 Automated Tests

- **Framework:**
- **Test class(es):**
- **Purpose:**
- **Important assertions:**

Real, executed results only — paste the actual command and its output:

```text
Command: apps/api/mvnw -B test

Tests Run:
Passed:
Failed:
Errors:
Skipped:
```

---

## 10.2 Manual API Testing (Postman)

Postman is the primary manual HTTP testing tool. Repeat this block once per endpoint the feature
introduces or modifies.

### Endpoint: `METHOD /api/...`

**Purpose** — what this request is testing.

**Prerequisites**

```text
[ ] Backend running        (apps/api/mvnw spring-boot:run)
[ ] PostgreSQL running and reachable
[ ] Redis running          (only if this feature uses Redis)
[ ] Environment variables: DB_URL, DB_USERNAME, DB_PASSWORD
[ ] Authentication:        (none / Bearer token obtained from ...)
[ ] Test data required:    (...)
```

**Request**

```http
METHOD /api/...
```

**URL**

```text
http://localhost:8080/api/...
```

**Headers** — include `Authorization` only if the endpoint actually requires it.

```text
Content-Type: application/json
```

**Request Body** — the actual request DTO fields, not a guess.

```json
{
}
```

**Expected Response**

```text
HTTP 200 OK
```

**Expected Response Body** — the actual response DTO shape.

```json
{
}
```

**Actual Result**

```text
[To be filled after manual verification]
```

---

## 10.3 Negative API Testing

Cover the failure modes this feature can actually produce — missing required field, invalid input,
invalid email, invalid credentials, missing/invalid/expired JWT, unauthorized, forbidden, not found,
duplicate resource, invalid state, malformed request. **Omit cases the feature cannot reach.**

```text
Test Case:
Request:
Expected Status:
Expected Response:
Why:
Actual Result:        [To be filled after manual verification]
```

---

## 10.4 PostgreSQL Verification

Only for features that touch the database. Use the **actual schema** — never invented tables or
columns.

### Before Request

Describe the expected database state before the call (e.g. "no row with this email").

```sql
```

### Execute API Request

Name the exact Postman request from 10.2 to run.

### After Request

```sql
```

**Verify:** state precisely what the developer should look for. Cover whichever apply — record
created / updated / deleted, relationships, foreign keys, constraints, timestamps, status fields,
data integrity, security-sensitive fields.

> For any feature touching credentials, **explicitly verify the password column holds a BCrypt hash
> and not plaintext.** Never print a real secret into this note.

**Actual Result**

```text
[To be filled after manual verification]
```

---

## 10.5 Redis Verification

Only when the feature uses Redis. Use the **actual key structure** from the implementation.

| | |
|---|---|
| Key that should exist | |
| Why it exists | |
| Expected value/state | |
| Expected TTL | |
| What the result proves | |
| Behavior after expiry | |

```text
PING
GET <key>
HGETALL <key>
TTL <key>
SCAN 0 MATCH <pattern>
```

**Actual Result**

```text
[To be filled after manual verification]
```

---

## 10.6 WebSocket / STOMP Verification

Only for real-time features. Use the **actual endpoint and destinations** from the WebSocket
configuration.

**Connection** — how to connect to the WebSocket endpoint.

**Authentication** — how the connection is authenticated.

**Subscription**

```text
SUBSCRIBE
/destination/...
```

**Send Message** — actual destination and payload.

**Expected Behavior** — sender behavior, recipient behavior, delivery, persistence, error behavior.

**Database Verification** — SQL confirming the message persisted, when applicable.

**Redis Verification** — when Redis participates in fan-out or presence.

**Logs** — which log lines demonstrate connection, authentication, subscription, message received,
message persisted, message delivered, and error. Only lines the code actually emits.

**Actual Result**

```text
[To be filled after manual verification]
```

---

## 10.7 Authentication / Authorization Verification

For secured features. Document the expected status for each case that applies:

| Case | Expected | Actual |
|---|---|---|
| Valid JWT | | `[To be filled]` |
| Missing JWT | `401 Unauthorized` | `[To be filled]` |
| Invalid JWT | `401 Unauthorized` | `[To be filled]` |
| Expired JWT | `401 Unauthorized` | `[To be filled]` |
| Insufficient authorization | `403 Forbidden` | `[To be filled]` |
| Valid authorized request | | `[To be filled]` |

Use the real security configuration — if JWT is not implemented yet, say so rather than documenting
a flow that does not exist.

---

## 10.8 File / Media Verification

Only when the feature uses MinIO or S3: upload test, expected API response, object storage
verification, database metadata verification, download test, authorization test, invalid file test.
Use the real bucket and key structure — never invented paths.

**Actual Result**

```text
[To be filled after manual verification]
```

---

## 10.9 Actuator / Health Verification

```http
GET /actuator/health
```

Document expected status, liveness behavior, readiness behavior, and the health indicators actually
configured (PostgreSQL, Redis, others) — per the real Actuator configuration, including which
endpoints are exposed and which require authentication.

**Actual Result**

```text
[To be filled after manual verification]
```

---

## 10.10 Log Verification

Which log lines should appear during manual testing, and what each one proves. Depending on the
feature: authentication, WebSocket connection, subscription, message received, message persisted,
message delivered, Redis interaction, exceptions, disconnect/reconnect.

Only document log output the implementation actually produces.

**Actual Result**

```text
[To be filled after manual verification]
```

---

## 10.11 Manual Verification Checklist

Mark items that do not apply as `n/a` rather than leaving them ambiguous.

```text
[ ] Application starts successfully
[ ] PostgreSQL connection verified
[ ] Redis connection verified
[ ] Happy-path API tested with Postman
[ ] Negative API cases tested
[ ] Authentication tested
[ ] Authorization tested
[ ] PostgreSQL state verified
[ ] Redis state verified
[ ] WebSocket/STOMP behavior verified
[ ] File storage verified (if applicable)
[ ] Logs checked
[ ] Actuator checked
[ ] Automated tests passed
```

```text
Verification Status:

⏳ Implementation Complete — Manual Verification Pending
```

> Claude sets `⏳ Implementation Complete — Manual Verification Pending` when the feature ships.
> It may change this to `✅ Fully Verified` **only after the developer explicitly reports the manual
> results** — never on its own inference, and never because the automated tests passed.

---

## 10.12 What This Verification Proves (Learning)

Short and specific to this feature — not generic testing theory.

1. **What the automated tests prove** —
2. **What the Postman test proves** —
3. **What the PostgreSQL verification proves** —
4. **What the Redis verification proves** —
5. **What the WebSocket verification proves** —
6. **Why automated and manual testing are different** —
7. **What bugs each layer can catch** (and what each is blind to) —
8. **Engineering concepts involved** —
9. **Interview questions this raises** —

---

# 11. Files Modified

List the actual files changed, with a word on what changed in each.

---

# 12. Sequence Diagram

Document the real implementation flow.

---

# 13. Engineering Decisions

Explain:

- Why this design?
- Why this package structure?
- Why these dependencies?
- Why this database design?
- Why Redis, if applicable?
- Why WebSocket, if applicable?
- Why DTOs?
- Why these specific concurrency/security decisions?

---

# 14. Lessons Learned

Explain the concepts to understand from this feature.

---

# 15. Common Mistakes

List realistic mistakes someone would make implementing this.

---

# 16. Future Improvements

List production improvements this feature still needs.

---

# 17. Interview Questions

Questions grounded in this feature's actual implementation, with concise answers.
No generic framework trivia.
