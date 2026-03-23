# Smart Order Management System

A production-style, portfolio-grade backend built with **Spring Boot Microservices**.  
The system allows customers to place orders, validates stock, processes payments, and sends notifications — all secured with JWT and fully containerized with Docker.

---

## Architecture Overview
```
                        ┌─────────────────┐
                        │   API Gateway   │  :8080
                        │  (JWT Filter)   │
                        └────────┬────────┘
                                 │
          ┌──────────────────────┼──────────────────────┐
          │                      │                      │
   ┌──────▼──────┐      ┌────────▼───────┐    ┌────────▼───────┐
   │Auth Service │      │Customer Service│    │Inventory Service│
   │   :8086     │      │    :8081       │    │    :8082        │
   └─────────────┘      └────────────────┘    └────────────────┘
          │                      │                      │
   ┌──────▼──────────────────────▼──────────────────────▼───────┐
   │                      Order Service                          │
   │                         :8083                               │
   │         (Orchestrates the full order flow)                  │
   └──────────────────────────┬──────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              │                               │
     ┌────────▼────────┐           ┌──────────▼──────────┐
     │ Payment Service │           │ Notification Service │
     │    :8084        │           │      :8085           │
     └─────────────────┘           └─────────────────────┘

Supporting Services:
- Eureka Server     :8761  (Service Registry)
- Config Server     :8888  (Centralized Configuration)
- PostgreSQL        :5432  (One database per service)
```

---

## Tech Stack

| Category                    | Technology                             |
|-----------------------------|----------------------------------------|
| Language                    | Java 21                                |
| Framework                   | Spring Boot 3.3.5                      |
| Service Discovery           | Spring Cloud Netflix Eureka            |
| API Gateway                 | Spring Cloud Gateway                   |
| Config Management           | Spring Cloud Config Server             |
| Inter-service Communication | OpenFeign                              |
| Fault Tolerance             | Resilience4j (Circuit Breaker + Retry) |
| Security                    | Spring Security + JWT (JJWT 0.12.6)    |
| Database                    | PostgreSQL 16 (one per service)        |
| ORM                         | Spring Data JPA + Hibernate            |
| Containerization            | Docker + Docker Compose                |
| API Documentation           | SpringDoc OpenAPI (Swagger UI)         |
| Build Tool                  | Maven                                  |

---

## Services

| Service              | Port   | Database        | Responsibility                   |
|----------------------|--------|-----------------|----------------------------------|
| Eureka Server        | 8761   | —               | Service registry                 |
| Config Server        | 8888   | —               | Centralized configuration        |
| API Gateway          | 8080   | —               | Request routing + JWT validation |
| Auth Service         | 8086   | auth_db         | Register, login, JWT generation  |
| Customer Service     | 8081   | customer_db     | Customer CRUD + validation       |
| Inventory Service    | 8082   | inventory_db    | Product CRUD + stock management  |
| Order Service        | 8083   | order_db        | Order orchestration              |
| Payment Service      | 8084   | payment_db      | Payment processing               |
| Notification Service | 8085   | notification_db | Notification logging             |

---

## Core Business Flow
```
1.  Customer registers / logs in → receives JWT token
2.  Customer places an order via API Gateway
3.  Gateway validates JWT → forwards request to Order Service
4.  Order Service validates customer → Customer Service
5.  Order Service fetches products + checks stock → Inventory Service
6.  Order Service creates order with PENDING status
7.  Inventory Service reserves stock
8.  Order Service calls Payment Service
9a. Payment SUCCESS → order becomes CONFIRMED
                    → stock reservation becomes CONSUMED
                    → confirmation notification sent
9b. Payment FAILED  → order becomes FAILED
                    → stock reservation RELEASED
                    → failure notification sent
```

---

## Order & Payment Statuses

**OrderStatus:** `PENDING` → `CONFIRMED` / `FAILED` / `CANCELLED`

**PaymentStatus:** `PENDING` → `SUCCESS` / `FAILED`

**ReservationStatus:** `RESERVED` → `CONSUMED` / `RELEASED`

---

## Prerequisites

- Java 21
- Maven 3.9+
- Docker Desktop
- PostgreSQL (only needed for local run without Docker)

---

## Running with Docker (Recommended)

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/smart-order-management.git
cd smart-order-management
```

### 2. Build all JARs locally
```bash
# Windows PowerShell
@("eureka-server","config-server","api-gateway","auth-service",`
  "customer-service","inventory-service","order-service",`
  "payment-service","notification-service") | ForEach-Object {
    Push-Location $_
    mvn clean package -DskipTests
    Pop-Location
}
```
```bash
# Linux / Mac
for service in eureka-server config-server api-gateway auth-service \
  customer-service inventory-service order-service \
  payment-service notification-service; do
  cd $service && mvn clean package -DskipTests && cd ..
done
```

### 3. Start all services
```bash
docker-compose up -d
```

### 4. Wait for services to start

All services take approximately 2-3 minutes to fully start and register with Eureka.
Monitor progress:
```bash
docker-compose logs -f
```

### 5. Verify all services are registered

Open `http://localhost:8761` — you should see all 8 services listed.

### 6. Stop all services
```bash
docker-compose down
```

### 7. Stop and remove all data
```bash
docker-compose down -v
```

---

## Running Locally (Without Docker)

### 1. Create databases in PostgreSQL
```sql
CREATE DATABASE auth_db;
CREATE DATABASE customer_db;
CREATE DATABASE inventory_db;
CREATE DATABASE order_db;
CREATE DATABASE payment_db;
CREATE DATABASE notification_db;
```

### 2. Start services in order
```
1. Eureka Server    (port 8761)
2. Config Server    (port 8888)
3. API Gateway      (port 8080)
4. Auth Service     (port 8086)
5. Customer Service (port 8081)
6. Inventory Service (port 8082)
7. Payment Service  (port 8084)
8. Notification Service (port 8085)
9. Order Service    (port 8083)
```
```bash
cd <service-folder>
mvn spring-boot:run
```

---

## API Documentation

Swagger UI is available for each service:

| Service              | Swagger UI                            |
|----------------------|---------------------------------------|
| Auth Service         | http://localhost:8086/swagger-ui.html |
| Customer Service     | http://localhost:8081/swagger-ui.html |
| Inventory Service    | http://localhost:8082/swagger-ui.html |
| Order Service        | http://localhost:8083/swagger-ui.html |
| Payment Service      | http://localhost:8084/swagger-ui.html |
| Notification Service | http://localhost:8085/swagger-ui.html |

---

## API Quick Reference

All requests go through the API Gateway on port `8080`.  
All endpoints except `/api/v1/auth/**` require a `Bearer` token.

### Authentication

**Register**
```
POST /api/v1/auth/register
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Login**
```
POST /api/v1/auth/login
{
  "email": "john@example.com",
  "password": "password123"
}
```

Response includes:
```json
{
  "data": {
    "token": "eyJhbGci...",
    "tokenType": "Bearer",
    "role": "ROLE_CUSTOMER",
    "expiresIn": 86400000
  }
}
```

---

### Customers

| Method   | Endpoint               | Description        |
|----------|------------------------|--------------------|
| POST     | /api/v1/customers      | Create customer    |
| GET      | /api/v1/customers      | Get all customers  |
| GET      | /api/v1/customers/{id} | Get customer by ID |
| PUT      | /api/v1/customers/{id} | Update customer    |
| DELETE   | /api/v1/customers/{id} | Delete customer    |

---

### Products & Inventory

| Method  | Endpoint                  | Description       |
|---------|---------------------------|-------------------|
| POST    | /api/v1/products          | Create product    |
| GET     | /api/v1/products          | Get all products  |
| GET     | /api/v1/products/{id}     | Get product by ID |
| PUT     | /api/v1/products/{id}     | Update product    |
| DELETE  | /api/v1/products/{id}     | Delete product    |
| POST    | /api/v1/inventory/reserve | Reserve stock     |
| POST    | /api/v1/inventory/release | Release stock     |
| POST    | /api/v1/inventory/consume | Consume stock     |

---

### Orders

| Method   | Endpoint                     | Description            |
|----------|------------------------------|------------------------|
| POST     | /api/v1/orders               | Place an order         |
| GET      | /api/v1/orders               | Get all orders         |
| GET      | /api/v1/orders/{id}          | Get order by ID        |
| GET      | /api/v1/orders/customer/{id} | Get orders by customer |

**Place Order Request:**
```json
{
  "customerId": 1,
  "items": [
    { "productId": 1, "quantity": 2 }
  ],
  "paymentMethod": "CREDIT_CARD",
  "notes": "Optional notes"
}
```

---

### Payments

| Method  | Endpoint                         | Description          |
|---------|----------------------------------|----------------------|
| POST    | /api/v1/payments                 | Process payment      |
| GET     | /api/v1/payments/{id}            | Get payment by ID    |
| GET     | /api/v1/payments/order/{orderId} | Get payment by order |

---

### Notifications

| Method   | Endpoint                              | Description                    |
|----------|---------------------------------------|--------------------------------|
| GET      | /api/v1/notifications/order/{orderId} | Get notifications for order    |
| GET      | /api/v1/notifications/customer/{id}   | Get notifications for customer |

---

## Unified Response Format

**Success:**
```json
{
  "success": true,
  "message": "Order created successfully",
  "data": { "..." },
  "timestamp": "2026-03-21T14:30:00"
}
```

**Error:**
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "email": "Email must be valid"
  },
  "timestamp": "2026-03-21T14:30:00",
  "path": "/api/v1/customers"
}
```

---

## Payment Simulation

Real payment gateway integration is not included.  
The simulation rule is:

| Amount                             | Result    |
|------------------------------------|-----------|
| Any amount **not** ending in `.99` | ✅ SUCCESS |
| Any amount ending in `.99`         | ❌ FAILED  |

Example: price `9.99` → payment fails, stock released, order FAILED.

---

## Security

- All endpoints except `/api/v1/auth/**` require JWT authentication
- JWT is validated at the API Gateway before forwarding to any service
- Downstream services receive user info via headers: `X-User-Id`, `X-User-Email`, `X-User-Role`
- Passwords are BCrypt hashed
- Token expiry: 24 hours (configurable via `jwt.expiration-ms`)
- Roles: `ROLE_ADMIN`, `ROLE_CUSTOMER`

---

## Fault Tolerance

Resilience4j is configured on the Order Service for all downstream calls:

| Pattern         | Services                     | Configuration                              |
|-----------------|------------------------------|--------------------------------------------|
| Circuit Breaker | Customer, Inventory, Payment | Opens after 50% failure rate over 10 calls |
| Retry           | Customer, Inventory          | 3 attempts, 1 second wait                  |
| Fallback        | All                          | Returns null → graceful error response     |

Check circuit breaker states:
```
GET http://localhost:8083/actuator/health
```

---

## Project Structure
```
smart-order-management/
├── docker-compose.yml
├── init-db.sql
├── eureka-server/
├── config-server/
│   └── src/main/resources/configs/    ← per-service config files
├── api-gateway/
├── auth-service/
├── customer-service/
├── inventory-service/
├── order-service/
├── payment-service/
└── notification-service/
```

Each service follows this internal package structure:
```
com.smartorders.<service>/
├── common/          BaseResponse, ErrorResponse
├── config/          Spring configuration
├── controller/      REST controllers
├── client/          Feign clients (order-service only)
├── dto/             Request/Response DTOs
├── entity/          JPA entities
├── enums/           Status enums
├── exception/       Custom exceptions + GlobalExceptionHandler
├── mapper/          Entity ↔ DTO mappers
├── repository/      Spring Data JPA repositories
└── service/         Service interfaces + implementations
```

---

## Environment Variables

All configuration is externalized. Key values:

| Variable                     | Default                                 | Description             |
|------------------------------|-----------------------------------------|-------------------------|
| `jwt.secret`                 | `smartorders-super-secret-jwt-key-2026` | JWT signing secret      |
| `jwt.expiration-ms`          | `86400000`                              | Token expiry (24 hours) |
| `spring.datasource.password` | `postgres`                              | DB password             |
| `SPRING_PROFILES_ACTIVE`     | `docker`                                | Active profile          |

---

## Phases Built

| Phase | What was built |
|---|---|
| Phase 1 | Eureka, Config Server, Gateway, Customer, Inventory, Order services |
| Phase 2 | Payment Service, Notification Service, complete order flow |
| Phase 3 | Auth Service, Spring Security, JWT, secured gateway |
| Phase 4 | Docker Compose, centralized config, CORS, full containerization |
| Phase 5 | Resilience4j circuit breakers, retry logic, fault tolerance |

---

## Future Improvements

- Replace synchronous Feign calls to Notification Service with async messaging (RabbitMQ / Kafka)
- Add role-based access control (ADMIN vs CUSTOMER endpoints)
- Add Flyway database migrations
- Add Spring Boot Admin for service monitoring
- Add distributed tracing with Zipkin / Sleuth
- Add rate limiting at the API Gateway
- Replace simulated payment with real gateway (Stripe / PayPal)
- Add refresh token support

---

## Author

Built as a portfolio project demonstrating production-grade Spring Boot microservices architecture.