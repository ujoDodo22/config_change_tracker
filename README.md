# Config Change Tracker

## 📌 Overview

Config Change Tracker is a Spring Boot application designed to track and manage changes to domain-specific configuration rules such as:

* Credit limits
* Approval policies

The system supports creating, storing, and querying configuration changes while ensuring proper validation, observability, and resilience.

---

## ⚙️ Tech Stack

* Java 21
* Spring Boot 3.x
* Maven
* Spring Boot Actuator (metrics & health)
* Spring Retry (resilience)
* JUnit 5 + Mockito
* In-memory storage (no database)

---

## 🚀 Features

### Core Functionality

* Create configuration changes (ADD / UPDATE / DELETE)
* Retrieve:

    * All changes
    * Changes by type
    * Changes by time range
    * Change by ID

---

### Validation & Error Handling

* Strong input validation using `@Valid`
* Meaningful error responses (HTTP 400)
* Validation of **valid state transitions**

    * ADD must not contain `oldValue`
    * UPDATE requires both `oldValue` and `newValue`
    * DELETE requires `oldValue` only

---

### Observability

#### 📊 Metrics (Micrometer + Actuator)

* `/actuator/metrics`
* Custom metrics:

    * `config.changes.total`
    * `config.changes.critical`

#### 🏥 Health Check

```http
GET /actuator/health
```

---

### 🧵 Tracing (Correlation ID)

* Each request has `X-Correlation-ID`
* If not provided → generated automatically
* Included in:

    * Response headers
    * Logs (via MDC)

---

### 🔁 Retry Logic (External Integration)

* Critical changes trigger notification
* Uses Spring Retry:

    * max attempts: 3
    * backoff delay: 300 ms
* Recovery handler logs failure if all retries fail

---

## 🧠 Domain Logic

### Change Types

| Type            | Value Type                |
| --------------- | ------------------------- |
| CREDIT_LIMIT    | Integer                   |
| APPROVAL_POLICY | String (`AUTO`, `MANUAL`) |

---

### Critical Changes

A change is considered critical when:

* CREDIT_LIMIT:

    * difference > 3000

* APPROVAL_POLICY:

    * change from `MANUAL` → `AUTO`

---

## ▶️ How to Run

```bash
mvn spring-boot:run
```

App runs on:

```text
http://localhost:8080
```

---

## 📡 API Endpoints

---

### 🔹 Create Config Change

```http
POST /api/config-changes
```

#### Examples

##### ADD

```json
{
  "changeType": "CREDIT_LIMIT",
  "actionType": "ADD",
  "newValue": 5000
}
```

##### UPDATE

```json
{
  "changeType": "CREDIT_LIMIT",
  "actionType": "UPDATE",
  "oldValue": 2000,
  "newValue": 6000
}
```

##### DELETE

```json
{
  "changeType": "CREDIT_LIMIT",
  "actionType": "DELETE",
  "oldValue": 5000
}
```

##### Approval Policy

```json
{
  "changeType": "APPROVAL_POLICY",
  "actionType": "UPDATE",
  "oldValue": "MANUAL",
  "newValue": "AUTO"
}
```

---

#### Response

```json
{
  "id": "uuid",
  "type": "CREDIT_LIMIT",
  "action": "ADD",
  "oldValue": null,
  "newValue": 5000,
  "timestamp": "2026-04-20T10:00:00Z",
  "critical": false
}
```

---

### 🔹 Get All / Filtered

```http
GET /api/config-changes
```

#### Query params

| Param | Description    |
| ----- | -------------- |
| type  | Filter by type |
| from  | Start time     |
| to    | End time       |

---

### 🔹 Get by ID

```http
GET /api/config-changes/{id}
```

---

## ❌ Error Handling

Example:

```json
{
  "timestamp": "2026-04-20T10:00:00Z",
  "status": 400,
  "error": "Validation failed"
}
```

---

## 🧪 Testing

Run tests:

```bash
mvn test
```

Includes:

* Unit tests (service logic)
* Integration tests (REST API)

---

## 📊 Metrics Examples

```http
GET /actuator/metrics/config.changes.total
GET /actuator/metrics/config.changes.critical
```

---

## 🧵 Correlation ID Example

Request:

```http
POST /api/config-changes
X-Correlation-ID: test-123
```

Response:

```http
X-Correlation-ID: test-123
```