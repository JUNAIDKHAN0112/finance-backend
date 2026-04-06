# Finance Data Processing and Access Control Backend

A production-grade backend system for a **Finance Dashboard** built with Java 17, Spring Boot 3.x, PostgreSQL, and JWT-based Role-Based Access Control (RBAC). Built as part of a backend engineering assessment for Zorvyn FinTech.

---

## Live Demo

| Resource | Link |
|---|---|

| **Swagger UI / Live  URL** | https://finance-backend-sl5i.onrender.com/swagger-ui/index.html |

| **GitHub** | https://github.com/JUNAIDKHAN0112/finance-backend |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Database | PostgreSQL 16 |
| Authentication | JWT (jjwt 0.12.3) |
| Security | Spring Security 6 |
| ORM | Spring Data JPA + Hibernate 6 |
| Validation | Bean Validation (Jakarta) |
| Documentation | Springdoc OpenAPI (Swagger UI) |
| Build Tool | Maven |
| Deployment | Render (PostgreSQL + App) |
| Containerization | Docker + Docker Compose |

---

## Project Structure

```
src/main/java/com/zorvyn/finance/
├── common/
│   ├── exception/          # GlobalExceptionHandler + Custom Exceptions
│   └── response/           # ApiResponse<T> wrapper
├── config/                 # SwaggerConfig
├── controller/             # AuthController, UserController, TransactionController, DashboardController
├── dto/
│   ├── request/            # RegisterRequest, LoginRequest, TransactionRequest, etc.
│   └── response/           # AuthResponse, UserResponse, TransactionResponse
├── entity/                 # User, Transaction, IdempotencyRecord
├── enums/                  # Role (ADMIN/ANALYST/VIEWER), TransactionType (INCOME/EXPENSE)
├── repository/             # UserRepository, TransactionRepository, IdempotencyRepository
├── security/               # JwtUtil, JwtAuthFilter, SecurityConfig, RateLimitFilter
└── service/                # AuthService, UserService, TransactionService, DashboardService

src/test/java/com/zorvyn/finance/
├── controller/             # AuthControllerTest, TransactionControllerTest
├── repository/             # TransactionRepositoryTest
└── service/                # AuthServiceTest, TransactionServiceTest, DashboardServiceTest
```

---

## Role Matrix

| Action | VIEWER | ANALYST | ADMIN |
|---|---|---|---|
| Register / Login | ✅ | ✅ | ✅ |
| View own profile | ✅ | ✅ | ✅ |
| View transactions | ✅ | ✅ | ✅ |
| Search transactions | ✅ | ✅ | ✅ |
| View recent activity | ✅ | ✅ | ✅ |
| View dashboard summary | ❌ | ✅ | ✅ |
| View category breakdown | ❌ | ✅ | ✅ |
| View monthly/weekly trend | ❌ | ✅ | ✅ |
| Create transaction | ❌ | ❌ | ✅ |
| Update transaction | ❌ | ❌ | ✅ |
| Delete transaction | ❌ | ❌ | ✅ |
| Manage users | ❌ | ❌ | ✅ |
| Update user role | ❌ | ❌ | ✅ |
| Update user status | ❌ | ❌ | ✅ |

---

## API Endpoints

### Authentication (Public)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and get JWT token |

### Users (Authenticated)

| Method | Endpoint | Role | Description |
|---|---|---|---|
| GET | `/api/users` | ADMIN | Get all users |
| GET | `/api/users/me` | ALL | Get own profile |
| GET | `/api/users/{id}` | ADMIN | Get user by ID |
| PATCH | `/api/users/{id}/role` | ADMIN | Update user role |
| PATCH | `/api/users/{id}/status` | ADMIN | Activate/deactivate user |

### Transactions (Authenticated)

| Method | Endpoint | Role | Description |
|---|---|---|---|
| POST | `/api/transactions` | ADMIN | Create transaction |
| GET | `/api/transactions` | ALL | List with filters + pagination |
| GET | `/api/transactions/{id}` | ALL | Get by ID |
| PUT | `/api/transactions/{id}` | ADMIN | Update transaction |
| DELETE | `/api/transactions/{id}` | ADMIN | Soft delete |
| GET | `/api/transactions/search` | ALL | Search by keyword |

### Dashboard (Authenticated)

| Method | Endpoint | Role | Description |
|---|---|---|---|
| GET | `/api/dashboard/summary` | ANALYST, ADMIN | Total income, expense, net balance |
| GET | `/api/dashboard/category-breakdown` | ANALYST, ADMIN | Category-wise totals |
| GET | `/api/dashboard/monthly-trend` | ANALYST, ADMIN | Last 6 months trend |
| GET | `/api/dashboard/weekly-trend` | ANALYST, ADMIN | Last 7 days trend |
| GET | `/api/dashboard/recent` | ALL | Last 10 transactions |

---

## Sample Requests

### Register
```bash
curl -X POST https://finance-backend-sl5i.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Admin User",
    "email": "admin@zorvyn.com",
    "password": "admin123"
  }'
```

### Login
```bash
curl -X POST https://finance-backend-sl5i.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@zorvyn.com",
    "password": "admin123"
  }'
```

### Create Transaction (Admin only)
```bash
curl -X POST https://finance-backend-sl5i.onrender.com/api/transactions \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: unique-key-123" \
  -d '{
    "amount": 75000.00,
    "type": "INCOME",
    "category": "Salary",
    "date": "2026-04-01",
    "notes": "April salary"
  }'
```

### Filter Transactions
```bash
curl "https://finance-backend-sl5i.onrender.com/api/transactions?type=INCOME&category=Salary&from=2026-01-01&to=2026-04-30&page=0&size=10" \
  -H "Authorization: Bearer <token>"
```

### Dashboard Summary
```bash
curl https://finance-backend-sl5i.onrender.com/api/dashboard/summary \
  -H "Authorization: Bearer <token>"
```

---

## Local Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 16
- Docker (optional)

### Option 1 — Docker Compose (Recommended)

```bash
git clone https://github.com/JUNAIDKHAN0112/finance-backend.git
cd finance-backend
docker compose up -d
```

App runs at `http://localhost:8080`

### Option 2 — Manual Setup

```bash
# 1. Clone
git clone https://github.com/JUNAIDKHAN0112/finance-backend.git
cd finance-backend

# 2. Create PostgreSQL database
psql -U postgres -c "CREATE DATABASE finance_db;"

# 3. Configure (application.yml already has defaults)
# Edit src/main/resources/application.yml if needed

# 4. Run
mvn spring-boot:run
```

### Environment Variables

Copy `.env.example` to `.env`:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/finance_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
JWT_SECRET=your_jwt_secret_here
JWT_EXPIRATION=86400000
```

---

## Running Tests

```bash
mvn test
```

Test coverage includes:
- `AuthServiceTest` — register, login, validation, deactivated user
- `TransactionServiceTest` — CRUD, idempotency, soft delete
- `DashboardServiceTest` — summary, category breakdown
- `AuthControllerTest` — register/login validation, HTTP status codes
- `TransactionControllerTest` — access control, role enforcement
- `TransactionRepositoryTest` — DB-level queries, filter logic

---

## Architecture Decisions & Assumptions

### 1. BigDecimal for Financial Amounts
Used `BigDecimal` (precision 19, scale 4) instead of `double` or `float` to avoid floating-point precision errors — critical in any financial system where rounding can cause incorrect balances.

### 2. Role Design — Admin Creates Transactions
Only ADMIN can create/modify transactions. This follows the principle of least privilege — in a financial system, data entry should be restricted to authorized personnel only. VIEWER and ANALYST roles exist for transparency and reporting without write access. This mirrors real-world fintech systems where data entry and reporting are separate concerns.

### 3. UUID Primary Keys
Used UUID as primary keys instead of sequential integers to prevent enumeration attacks and improve security — especially important in a financial system where predictable IDs can expose sensitive transaction data.

### 4. JWT Stateless Authentication
Chose JWT over session-based auth for stateless, scalable authentication. Tokens carry role claims, enabling fast authorization checks without hitting the database on every request. Deactivated users are blocked at the filter level — even valid tokens are rejected if the user account is inactive.

### 5. Role-Based Access via @PreAuthorize
Implemented method-level security using Spring Security's `@PreAuthorize` with `@EnableMethodSecurity` instead of URL-based config alone. This keeps access control co-located with the business logic, making it easier to audit and maintain.

### 6. Soft Delete on Transactions
Financial records are never hard deleted. Instead, an `is_deleted` flag marks them inactive. This preserves audit history and reflects real-world financial compliance requirements.

### 7. DB-Level Aggregations for Dashboard
All dashboard metrics (totals, category breakdown, monthly/weekly trends) are computed via `@Query` native queries rather than in-memory Java processing. This keeps aggregation performant as data scales.

### 8. Idempotency for Transaction Creation
Added `Idempotency-Key` header support on POST `/api/transactions`. Duplicate requests with the same key return the cached response without creating duplicate records — critical for financial systems where network retries can cause duplicate charges.

### 9. Rate Limiting on Auth Endpoints
Applied IP-based rate limiting (20 requests/minute) on `/api/auth/**` using Bucket4j to prevent brute-force attacks on login.

### 10. Audit Fields on All Entities
Every entity carries `createdAt` and `updatedAt` fields using Spring Data's `@EntityListeners(AuditingEntityListener.class)`. This provides traceability out of the box.

### 11. DB Indexes
Added indexes on frequently queried columns (`type`, `category`, `date`, `is_deleted`, `created_at` on transactions; `email`, `role`, `is_active` on users) to ensure query performance at scale.

---

## Trade-offs

| Decision | Trade-off |
|---|---|
| Railway over AWS | Faster reviewer access vs full production control |
| `ddl-auto: update` | Simple schema management vs Flyway versioning |
| In-memory rate limiting | Simple setup vs distributed rate limiting with Redis |
| Soft delete | Data safety vs storage growth over time |
| Tests skip on Railway build | CI simplicity vs full test gate in pipeline |

---

## Optional Enhancements Implemented

All optional features from the assignment specification were implemented:

- ✅ JWT Authentication with token-based sessions
- ✅ Pagination on all list endpoints
- ✅ Search support (keyword search on category, notes, type)
- ✅ Soft delete on transactions
- ✅ Rate limiting on auth endpoints (Bucket4j)
- ✅ Unit + integration tests (service, controller, repository layers)
- ✅ API documentation (Swagger UI)
- ✅ Idempotency (bonus — not in spec but critical for fintech)
- ✅ DB Indexes (bonus — performance optimization)
- ✅ Deactivated user token blocking (bonus — security hardening)
- ✅ Docker Compose (one-command local setup)
