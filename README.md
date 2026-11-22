### MiniBank

A small banking backend: accounts, idempotent money transfers and a double-entry ledger. Built to practise production concerns such as concurrency, idempotency and CI/CD.

### Tech stack

Kotlin · Spring Boot 4 · Java 21 · PostgreSQL 17 · Flyway · Spring Data JPA · JUnit 5 · Testcontainers · Docker · GitHub Actions · GHCR

### Features

- Create and fetch accounts (IBAN and currency validation)
- Transfers between accounts with an Idempotency-Key header
- Double-entry ledger: every transfer writes a debit and a credit entry
- Pessimistic locking with a consistent lock order (no lost updates, no deadlocks)
- Consistent JSON error responses (400 / 404 / 409 / 422)
- Integration and concurrency tests against a real PostgreSQL (Testcontainers)
- CI pipeline: build, test, Docker image, push to GHCR

### Getting started
Requirements: JDK 21, Docker


```
docker compose up -d      # PostgreSQL on localhost:5332
./mvnw spring-boot:run    # API on localhost:8080
./mvnw verify             # run all tests (Docker must be running)
```

### API examples

#### Create an account
```
curl -X POST localhost:8080/accounts \
-H 'Content-Type: application/json' \
-d '{"ownerName":"Alice","iban":"TR330006100519786457841326","currency":"TRY"}'
```

#### Transfer money
```
curl -X POST localhost:8080/transfers \
-H 'Content-Type: application/json' \
-H "Idempotency-Key: $(uuidgen)" \
-d '{"fromAccountId":1,"toAccountId":2,"amount":"30.00","currency":"TRY"}'
```

### Design decisions
**Idempotent transfers.** Every `POST /transfers` requires an `Idempotency-Key` header. If a transfer with that key already exists, the original result is returned instead of moving money again, so clients can safely retry after a timeout. A `UNIQUE` constraint on the key is the final guard against two identical requests racing each other.

**Pessimistic locking.** Two concurrent transfers could both read the same balance and overwrite each other (lost update). The service reads both accounts with `SELECT ... FOR UPDATE`, so concurrent transfers on the same account wait in line instead of failing. A `@Version` column adds optimistic locking as a second safety net for any code path that changes a balance without the lock.

**Deadlock-free lock order.** Accounts are always locked in ascending id order. Without this, A→B and B→A transfers could each hold one lock and wait forever for the other.

**Double-entry ledger.** Each transfer writes two append-only ledger entries (−amount and +amount) that sum to zero. Balances are a fast cache; the ledger is the audit trail and can be used to reconcile them.


### Roadmap

- Deposits endpoint
- Account statement with keyset pagination
- OpenAPI / Swagger UI
- Transactional outbox + Kafka events
- Observability (metrics, tracing)
- Kubernetes deployment (kind)


------