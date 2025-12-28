
# 💳 Payment Processing System

## 💸 PaymentService

* A project that allows working with domain models (`EntriePayment` — an internal accounting record, and `RegularPayment` — a user record and their data).
* For easier time setting, a parser was created to convert your "1m", "2d", "3h" into internal time units.
* The controller level does not accept direct database entities; therefore, even if new rows are added to an entity, the controller will not return more than a DTO :)
* Includes a Swagger specification, making it convenient to review functionality and test it without additional tools.

## ⏰ Reglament

* A module that creates a "Job" to generate an `EntriePayment` in the `PaymentService`.
* The service runs in the background and interacts with the `Payment Service` via HTTP requests.

### ⚙️ Configurable

* The entire project is configured in Docker Compose according to your needs (ports, database settings, job repetition time).
* The project is launched with a single command — `docker-compose up`, so no preliminary setups, databases, or downloading of additional files are required.
* When the containers are started, `postgresDB`, `Reglament`, and `PaymentService` are created.

### 📖 Open API

* After starting the project, the Swagger UI is available at: [API](http://localhost:8080/swagger-ui/index.html)
