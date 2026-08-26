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
- §10 Testing reports only real, executed results. Never invent a passing test or a benchmark.
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

# 10. Testing

> Real, executed results only. Paste the actual command and outcome.

## Unit Tests

## Integration Tests

## API Tests

## Expected Results

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
