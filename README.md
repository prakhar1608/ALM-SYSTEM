# Asset Liability Management System

A Java 17 / Spring Boot REST application for the supplied ALM case study. It manages positions, calculates interest-rate scenario impacts, presents liquidity and net-position indicators, and separates write access by role.

## Technology

- Oracle Database 23ai/26ai compatible SQL, Spring Boot 3, Java 17, JDBC, JSON REST
- Spring Security HTTP Basic authentication with BCrypt password hashes
- JavaScript responsive UI plus an Oracle JET 10 module in `frontend/oj-alm-ui`, JUnit 5, Selenium dependency, Insomnia collection, and GitHub Actions CI

## Application structure

The backend uses a layered design: REST controllers handle HTTP concerns, services contain business rules and transactions, DAOs own SQL access, and typed models/DTOs define the API contract. Assets, liabilities, and scenarios support create, read, update, and soft-delete operations; deletion changes the record status to `INACTIVE` so financial data remains traceable.

## Run locally

1. In Oracle SQL Developer / SQLcl, run `ALM_5_Table_Oracle26ai.sql`, then `ALM_5_Table_Sample_Data.sql`.
2. Set connection details (PowerShell):
   ```powershell
   $env:ALM_DB_URL='jdbc:oracle:thin:@//localhost:1521/FREEPDB1'
   $env:ALM_DB_USERNAME='alm'
   $env:ALM_DB_PASSWORD='alm'
   ```
3. Run `mvn spring-boot:run`, then browse to `http://localhost:8080/alm/`. The included Maven configuration keeps downloaded dependencies within this project.

The supplied development users all use password `password`; change every seed password before deployment. The API needs HTTP Basic authentication. Import `insomnia/ALM_Insomnia_Collection.json` into Insomnia to exercise it.

### Run a no-Oracle demonstration

Use `mvn spring-boot:run -Dspring-boot.run.profiles=demo` to launch a complete disposable demo with the same sample portfolio at `http://localhost:8080/alm/`. Sign in as `admin@alm.local` / `password`. This profile uses an in-memory H2 database and is only for demonstrations; the default profile uses Oracle.

## API surface

| Endpoint | Purpose | Role |
| --- | --- | --- |
| `GET /api/dashboard` | Asset, liability, net position and liquidity summary | authenticated |
| `GET/POST /api/assets`, `GET/POST /api/liabilities` | List or create portfolio positions | GET: authenticated; POST: analyst/admin |
| `GET/PUT/DELETE /api/assets/{id}`, `GET/PUT/DELETE /api/liabilities/{id}` | Retrieve, update, or archive a position | GET: authenticated; PUT/DELETE: analyst/admin |
| `GET/POST /api/scenarios`, `GET/PUT/DELETE /api/scenarios/{id}` | List, create, retrieve, update, or archive scenarios | GET: authenticated; POST/PUT/DELETE: analyst/admin |
| `POST /api/scenarios/{id}/run` | Calculate interest-rate sensitivity | analyst/admin |
| `GET /api/reports/risk` | Scenario and portfolio report | authenticated |

## Validation

Run `mvn verify`. This executes JUnit calculation tests; the Selenium dependency is included for browser acceptance tests once a running test environment is available. GitHub Actions runs the same verification for each push and pull request.

## Scenario method

For the simplified five-table schema, a positive rate shock reduces assets by `assets × shock / 100` and raises liabilities by `liabilities × shock / 200`; net impact is the difference. Risk bands are based on net impact as a percentage of active assets. This is a transparent training-model calculation, not a production valuation model.
