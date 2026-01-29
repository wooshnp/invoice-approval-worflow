# Invoice Approval Workflow

A CLI application that simulates an invoice approval workflow. Based on configurable rules, it determines which approver should receive an approval request and via which notification channel (Slack or Email).

---

## Business Context

**The Problem:** Companies receive hundreds of invoices daily. Each needs approval before payment, but routing them manually is slow, error-prone, and doesn't scale.

**The Solution:** An automated workflow that:
- Routes invoices to the right approver based on amount, department, and approval requirements
- Notifies approvers via their preferred channel (Slack for quick response, Email for formal record)
- Creates an audit trail for compliance

**Why This Matters for Light's Customers:**
- **Finance teams** save hours of manual routing
- **Approvers** get notified instantly in their preferred channel
- **Compliance** is built-in with full audit history
- **Flexibility** - each company configures their own rules

---

## Quick Demo

```bash
$ mvn spring-boot:run -Dspring-boot.run.arguments="--amount=15000 --department=MARKETING"

=== Processing Invoice ===
Amount: 15000
Department: MARKETING
Requires Manager Approval: false

Sending approval request via Email to cmo@light.inc (Chief Marketing Officer) for invoice: amount=15000, department=MARKETING

=== Result ===
Status: SUCCESS
Approver: Chief Marketing Officer (CMO)
Channel: EMAIL
```

```bash
$ mvn spring-boot:run -Dspring-boot.run.arguments="--amount=3000 --department=OTHER"


=== Processing Invoice ===
Amount: 3000
Department: OTHER
Requires Manager Approval: false

Sending approval request via Slack to @finance-team (Finance Team) for invoice: amount=3000, department=OTHER

=== Result ===
Status: SUCCESS
Approver: Finance Team (FINANCE_TEAM_MEMBER)
Channel: SLACK
```

---

## Challenge Requirements

This project implements the [Light Backend Challenge](./challenge_readme.md):

| Requirement | Implementation |
|-------------|----------------|
| Database model for workflow configuration | PostgreSQL with Liquibase migrations (see [ER Diagram](#database-schema)) |
| Simulate workflow execution | `WorkflowService` orchestrates rule evaluation and notification |
| Slack and Email channels (mocked) | Strategy pattern with `println` implementations |
| CLI interface | Spring Boot `CommandLineRunner` with argument parsing |
| Pre-load Fig.1 workflow | Liquibase seed script (`005-seed-initial-data.sql`) |

---

## Architecture

![Architecture](doc/architecture-overview-v1.drawio.png)

### Key Components

| Component | Responsibility |
|-----------|----------------|
| `ApprovalWorkflowApplication` | CLI entry point, argument parsing |
| `WorkflowService` | Orchestrates the approval flow |
| `RuleEvaluationService` | Matches invoice against rules |
| `NotificationService` | Routes to correct notification sender |
| `NotificationSender` | Strategy interface for Slack/Email |

---

## Database Schema

![ER Diagram](doc/ER-diagram.drawio.png)

### Tables

| Table | Purpose |
|-------|---------|
| `workflow` | Workflow definitions with versioning |
| `rule` | Conditions and actions for approval routing |
| `approver` | People who can approve (CFO, CMO, Finance Team, etc.) |
| `approval_request` | Audit log of all sent approval requests |

---

## Pre-loaded Workflow (Fig.1)

The database is seeded with the following rules from the challenge diagram:

| Priority | Condition | Approver | Channel |
|----------|-----------|----------|---------|
| 1 | amount > 10,000 AND MARKETING | CMO | EMAIL |
| 2 | amount > 10,000 (any other dept) | CFO | SLACK |
| 3 | 5,000 < amount ≤ 10,000 AND manager approval required | Finance Manager | EMAIL |
| 4 | 5,000 < amount ≤ 10,000 AND no manager approval | Finance Team | SLACK |
| 5 | amount ≤ 5,000 | Finance Team | SLACK |

---

## Getting Started

### Prerequisites

- Java 21
- Maven
- Docker & Docker Compose

### 1. Start PostgreSQL

```bash
docker compose up -d
```

### 2. Build

```bash
mvn clean package -DskipTests
```

### 3. Run

```bash
# Using Maven
mvn spring-boot:run -Dspring-boot.run.arguments="--amount=15000 --department=MARKETING --managerApproval=false"

# Using JAR
java -jar target/approval-workflow-0.0.1-SNAPSHOT.jar --amount=15000 --department=MARKETING --managerApproval=false
```

> **Note:** On first run, Liquibase automatically creates the database schema and seeds the workflow rules.


### CLI Arguments

| Argument | Required | Description |
|----------|----------|-------------|
| `--amount=<value>` | Yes | Invoice amount in USD |
| `--department=<value>` | Yes | `MARKETING` or `OTHER` |
| `--managerApproval=<value>` | No | `true` or `false` (default: `false`) |

### Example Scenarios

```bash
# High-value Marketing invoice → CMO via Email
--amount=15000 --department=MARKETING

# High-value non-Marketing invoice → CFO via Slack
--amount=15000 --department=OTHER

# Mid-value with manager approval → Finance Manager via Email
--amount=7500 --department=OTHER --managerApproval=true

# Low-value invoice → Finance Team via Slack
--amount=3000 --department=OTHER
```

---

## Design Decisions

### 1. Strategy Pattern for Notifications

**Decision:** Used the Strategy pattern for notification channels.

**Why:**
- **Open/Closed Principle:** Adding a new channel (SMS, Teams, etc.) requires only a new `@Component` - no changes to existing code
- **Testability:** Each sender can be unit tested in isolation
- **Spring Integration:** Spring auto-discovers `NotificationSender` beans and injects them as a list

```java
// Adding a new channel is just:
@Component
public class SmsNotificationSender implements NotificationSender {
   public NotificationChannel getChannel() { return NotificationChannel.SMS; }
   public void send(...) { /* implementation */ }
}
```

### 2. Rule-Based Evaluation with Priority

**Decision:** Rules are evaluated in priority order; first matching rule wins.

**Why:**
- **Predictable:** Clear, deterministic behavior
- **Maintainable:** Easy to understand which rule will match
- **Flexible:** Supports complex overlapping conditions by adjusting priority

**Alternative considered:** Collecting all matching rules and sending multiple notifications. Rejected because the workflow diagram implies single-path execution.

### 3. PostgreSQL + Liquibase vs In-Memory

**Decision:** Implemented full database persistence even though the challenge said in-memory was acceptable.

**Why:**
- **Deterministic setup:** Migrations guarantee the same schema and seed data everywhere
- **Fig.1 alignment:** The seed script makes the workflow explicit and repeatable
- **Audit trail:** `approval_request` table provides complete history

**Trade-off:** Requires Docker, but keeps schema + seed data consistent across machines

### 4. Workflow Versioning

**Decision:** Workflows have a `version` field and `active` flag.

**Why:** The challenge states "A company should be able to modify their workflow at any point, e.g. when the current version of the workflow is in action."

**Implementation:** Create a new workflow version, set it active, and the old version becomes inactive. Historical approval requests still reference the rule that was in effect at the time.

---

## Trade-offs

- **PostgreSQL + Liquibase:** Requires Docker, but keeps schema + seed data consistent across runs.
- **CLI only:** Not user‑friendly for non‑devs, but the prompt asked for CLI and an API can be added later.
- **Synchronous notifications:** No retries or async behavior, but notifications are mocked and async would add noise.
- **First‑match rule:** No multi‑approver flow, but the diagram implies a single path.
- **Department enum:** Limited set, but it matches the diagram and sample rules.

---

## Assumptions

### From the Challenge

1. **One workflow per company**: Each company defines only one workflow
2. **Workflow modification during execution**: New versions can be created while current is active
3. **USD only**: No currency conversion needed

### Technical Assumptions

1. **First matching rule wins**: The workflow diagram shows single-path execution, so we stop at the first match
2. **Approvers are pre-defined**: CFO, CMO, Finance Manager, Finance Team are seeded; in production these would be configurable
3. **Notifications are fire-and-forget**: No tracking of whether the approver actually received/read the notification
4. **Department is a simple enum**: For the challenge it is fixed; in production this would likely be a DB table per company
5. **Manager approval is a boolean**: In production, this might reference a specific manager or approval chain

### Business Assumptions

1. **Single approval is sufficient**: One approver per invoice (not a chain of approvals)
2. **Immediate notification**: No scheduling or batching of notifications
3. **No approval deadlines**: No escalation if approver doesn't respond
4. **Static thresholds**: Amount thresholds (5000, 10000) are hardcoded in rules, not configurable per company
5. **No delegation**: Approvers can't delegate to others when on vacation

### What I Would Clarify with PO/Business

Before building this for production, I would ask:

1. **Can an invoice require multiple approvals?** (e.g., both Finance Manager AND CFO for large amounts)
2. **What happens if no rule matches?** (Currently returns error or should there be a default approver?)
3. **Should notifications be retried?** (What if Slack is down?)
4. **Do approvers need to respond within a timeframe?** (Escalation logic?)
5. **Can rules overlap?** (Should multiple approvers be notified?)
6. **How do customers manage their approvers?** (Separate UI? Admin API?)

---

## How to Extend

### Adding a New Notification Channel (e.g., SMS)

1. Add enum value:
   ```java
   public enum NotificationChannel { EMAIL, SLACK, SMS }
   ```

2. Create sender:
   ```java
   @Component
   public class SmsNotificationSender implements NotificationSender {
       public NotificationChannel getChannel() { return NotificationChannel.SMS; }
       public void send(ApproverEntity approver, InvoiceRequest invoice) {
           // Integrate with something like Twilio e.g.
       }
   }
   ```

3. Done, Spring auto-discovers and registers it.

### Adding a REST API

1. Add `spring-boot-starter-web` dependency
2. Create controller:
   ```java
   @RestController
   @RequestMapping("/api/invoices")
   public class InvoiceController {
       @PostMapping("/process")
       public ApprovalResult process(@RequestBody InvoiceRequest request) {
           return workflowService.processInvoice(request);
       }
   }
   ```

### Adding Async/Queue-Based Notifications

1. Add `spring-kafka` or `spring-amqp` dependency
2. Replace sender implementations:
   ```java
   @Component
   public class SlackNotificationSender implements NotificationSender {
       private final KafkaTemplate<String, ApprovalMessage> kafka;

       public void send(ApproverEntity approver, InvoiceRequest invoice) {
           kafka.send("notifications.slack", new ApprovalMessage(...));
       }
   }
   ```

### Multi-Company Support

1. Add `company_id` to `workflow`, `rule`, `approver` tables
2. Filter all queries by company context
3. Add authentication to determine current company

---

## Production Considerations

Things that would be different in a real deployment:

| Area | Current | Production                                    |
|------|---------|-----------------------------------------------|
| **Notifications** | `println` mock | Real Slack API, SMTP integration              |
| **Resilience** | Synchronous | Message queue with retry, dlq                 |
| **Authentication** | None | OAuth2/JWT for company context                |
| **Approval tracking** | Audit log only | Full approval/rejection flow with webhooks    |
| **Monitoring** | None | Metrics, distributed tracing, alerting        |
| **Deployment** | Local Docker | Kubernetes, managed database (using helm ofc) |

---

## Running Tests

```bash
mvn test
```

Tests cover:
- `RuleEvaluationServiceTest`: Rule matching logic for all scenarios
- `NotificationServiceTest`: Strategy pattern routing
- `WorkflowServiceTest`: E2E workflow processing
