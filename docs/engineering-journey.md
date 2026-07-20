# Engineering Journey & Architectural Decisions

This document registers the real-world engineering challenges, refactoring cycles, and architectural tradeoffs solved during the development of the Sentinel Vault Core Engine.

## 1. The Git Strategy & Ecosystem Evolution
The Sentinel Vault is designed under an evolutionary architecture pattern. The `main` branch does not contain a coupled end-user interface; instead, it serves as a pure, hyper-optimized **Core Engine**. From this stable, containerized baseline, three specialized product verticals are branched out:
* **Vertical A (Dev Injector):** Automated `.env` local runtime injection.
* **Vertical B (Infra Guardian):** Secure SSH Key and SSL/TLS Certificate metadata management.
* **Vertical C (Compliance Platform):** Extended network telemetry and high-grain immutable auditing.

## 2. Solving the Spring Security Circular Dependency
During the hardening phase of the Authorization Server, a classic circular reference loop occurred between `SecurityConfig` and `UserService` due to the bidirectional requirement of the `PasswordEncoder`.
* **The Solution:** Applied the *Separation of Concerns (SoC)* principle, isolating the cryptographic encoding engine into a dedicated `PasswordEncoderConfig` specialist class. This broken the instantiation loop, allowing Spring to resolve the dependency graph cleanly at boot time.

## 3. Network Unification and Docker Architecture
Initially, the local environment ran the database containerized while the application executed natively on the host OS, forcing manual port mappings (`5439:5432`) and external exposures.
* **The Solution:** Engineered a unified `docker-compose.yml` operating on an isolated internal bridge network. The application now discovers the database natively via the service alias name (`postgres`) on the default internal port `5432`. This preserves network isolation, reduces socket overhead, and enforces *Least Privilege* practices.