# Syzenx Pay - Wallet Transaction API 🚀

A robust RESTful API designed to process digital wallet charges securely. This project was built following **Hexagonal Architecture (Ports and Adapters)** principles and is heavily optimized for high-concurrency environments, preventing Race Conditions and duplicate transaction charges.

## Architecture & Design Decisions

The system is strictly divided into three main layers to guarantee low coupling and high cohesion (Dependency Inversion Principle):

1. **Domain Layer:** Contains the pure business model (`Wallet`). It has absolutely no framework dependencies (neither Spring nor JPA). Strict business rules and mathematical validations (e.g., insufficient funds) reside here.
2. **Application Layer:** Contains the Use Cases (`ChargeWalletUseCase`). It orchestrates the flow between the inbound (Web) and outbound (Database) ports, keeping the domain completely isolated.
3. **Infrastructure Layer:** Contains the technological adapters:
    *   **Inbound (Web):** REST Controllers (`WalletController`).
    *   **Outbound (DB/Cache):** PostgreSQL and Redis adapters (`SpringDataWalletRepository`).

### Resilience & Concurrency (Key Features)

*   **Race Condition Prevention (Pessimistic Locking):** Utilizes database row-level locking in PostgreSQL (`SELECT ... FOR UPDATE`) to safely queue simultaneous transactions attempting to modify the same wallet's balance at the exact same millisecond, ensuring mathematical consistency.
*   **Idempotency (Redis Cache):** Implements a protective shield using Redis to intercept duplicated transactions caused by network retries or frontend glitches. It requires a unique UUID in the `Idempotency-Key` HTTP header.
*   **Isolated Testing (Testcontainers):** Comprehensive integration testing strategy that spins up ephemeral PostgreSQL and Redis Docker containers on the fly, guaranteeing that the system's behavior under stress is identical to production.

---

## Tech Stack

*   **Language:** Java 21
*   **Framework:** Spring Boot 3.3.x
*   **Database:** PostgreSQL 15
*   **Cache / Idempotency:** Redis 7
*   **Local Infrastructure:** Docker Compose
*   **Testing:** JUnit 5, Mockito 5, Testcontainers

---

## Getting Started

### Prerequisites
*   [Java 21](https://jdk.java.net/21/)
*   [Maven](https://maven.apache.org/)
*   [Docker](https://www.docker.com/) and Docker Compose

### 1. Spin up the Infrastructure (Database & Cache)
The project includes a ready-to-use `compose.yaml` file.
```bash
docker compose up -d
