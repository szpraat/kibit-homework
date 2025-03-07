# Instant Payment Service

## Table of Contents

- [Overview](#overview)
- [Technologies Used](#technologies-used)
- [How to Start the Service with Docker Compose](#how-to-start-the-service-with-docker-compose)
    - [Prerequisites](#prerequisites)
    - [Step 1: Clone the Repository](#step-1-clone-the-repository)
    - [Step 2: Build and Start the Services](#step-2-build-and-start-the-services)
    - [Step 3: Access the Services](#step-3-access-the-services)
- [Future Improvements](#future-improvements)

---

## Overview

The **Instant Payment Service** is a microservice designed to handle real-time transactions between accounts in an event-driven architecture. This service leverages **Kafka** for asynchronous communication, **PostgreSQL** for data storage, **Swagger UI** for API documentation, and **Spring Boot** as the framework for building the microservice. It includes various components like **PgAdmin** for managing the PostgreSQL database and **Kafka UI** for monitoring Kafka topics and consumer groups. The service processes payment notifications and sends updates through Kafka to other microservices or systems in the architecture.

### Key Features:
- **Kafka Messaging**: Sends and receives payment notifications asynchronously.
- **PostgreSQL Database**: Stores transaction and account data.
- **Swagger UI**: Provides interactive API documentation to easily test and explore the service’s endpoints.
- **Real-time Monitoring**: Kafka UI and PgAdmin for managing and monitoring system components.
- **Event-driven Architecture**: Decouples microservices by leveraging Kafka topics for communication.

---

## Technologies Used

- **Spring Boot**: Framework for building the microservice.
- **Kafka**: Distributed streaming platform for event-driven communication.
- **PostgreSQL**: Relational database for storing payment and account data.
- **Docker**: Containerization platform for building and running services.
- **Docker Compose**: Orchestration tool for managing multiple services and containers.
- **PgAdmin**: Web-based interface for managing the PostgreSQL database.
- **Kafka UI**: Web interface to monitor Kafka topics, consumers, and offsets.
- **Swagger UI**: Web-based interface for API documentation, allowing easy testing and exploration of API endpoints.

---

# How to Start the Service with Docker Compose

### Prerequisites

Before starting the service, ensure you have the following installed:

- **Docker**: To run the containerized services.
- **Docker Compose**: To manage multi-container applications.

You can verify your Docker installation by running:

```bash
docker --version
docker-compose --version
```
## Step 1: Clone the Repository

```bash
git clone https://github.com/szpraat/instant-payment-service.git
cd instant-payment-service
```

## Step 2: Build and Start the Services
```bash
docker-compose up --build -d
```
This command will:

* Build the Docker images if they don't already exist.
* Start the following services:
    *  Payment Service (Spring Boot application)
    * PostgreSQL (for storing account and transaction data)
    * PgAdmin (for managing PostgreSQL)
    * Kafka (for event-driven communication)
    * Kafka UI (for monitoring Kafka topics)

## Step 3: Access the Services
Once the containers are up and running, you can access the following services:

### Payment Service (Swagger UI)
Access the API documentation via Swagger UI at:
* http://localhost:8080/swagger-ui.html

This provides interactive API documentation for testing the payment endpoints.


### Kafka UI
Access the Kafka UI at:
* http://localhost:9090

Here you can monitor Kafka topics, view consumer groups, and explore message flows.

### PgAdmin
Access PgAdmin for managing PostgreSQL via:
http://localhost:8888

Log in using the following credentials:
```
Email: admin@admin.com
Password: admin
```
### Add a New Server in PgAdmin

1. In **PgAdmin**, click on **Add New Server** in the Quick Links panel.

#### Server Configuration:

- **General**:
    - **Name**: `Instant Payments DB`

- **Connection**:
    - **Host**: `postgres` (name of the PostgreSQL container from Docker Compose)
    - **Port**: `5432` (default PostgreSQL port)
    - **Maintenance database**: `instant-payments-db`
    - **Username**: `myuser`
    - **Password**: `secret`

2. Click **Save** to establish the connection.

## Future Improvements

Here are some areas to focus on to enhance the service for production readiness:

### 1. Kafka Fine-tuning
- **Partitioning**: Implement partitioning of Kafka topics to allow for better horizontal scalability and parallel processing of payment transactions.
- **Replication**: Set up topic replication across multiple brokers for fault tolerance.
- **Consumer Group Scaling**: Add more consumers in different consumer groups to handle larger volumes of payments efficiently.
- **Message Retention**: Configure proper message retention and cleanup policies for Kafka topics to prevent excessive storage usage.

### 2. Backpressure Handling
- **Rate Limiting**: Implement rate limiting for payment requests to prevent overwhelming the system with high traffic.
- **Backpressure in Kafka Consumers**: Implement backpressure handling in Kafka consumers to ensure that slow consumers do not overwhelm the system when messages accumulate.
- **Timeouts and Retries**: Set appropriate timeouts and retries for external systems like Kafka and PostgreSQL to handle transient failures gracefully.

### 3. Error Handling and Resilience
- **Circuit Breakers**: Integrate resilience patterns like circuit breakers using libraries such as Resilience4j to prevent cascading failures.
- **Graceful Degradation**: Implement fallback mechanisms for critical components like Kafka or PostgreSQL to ensure the service continues to function even if a dependency is temporarily unavailable.
- **Dead Letter Queue**: Use a Dead Letter Queue (DLQ) for Kafka to handle failed message deliveries and retries.

### 4. Monitoring and Logging
- **Prometheus & Grafana**: Integrate Prometheus for monitoring application metrics and Grafana for visualization. Monitor Kafka consumer lag, database performance, and application health.

### 5. Security Enhancements
- **Authentication and Authorization**: Secure the API with JWT or OAuth2 for user authentication. Implement role-based access control (RBAC) for different API endpoints.
- **Secure Communication**: Use SSL/TLS encryption for secure communication between services, especially for Kafka and PostgreSQL.
- **Secrets Management**: Store sensitive credentials in a secure vault like HashiCorp Vault or AWS Secrets Manager instead of using plain environment variables.

### 7. CI/CD Pipeline
- **Automated Testing**: Set up unit, integration, and end-to-end tests to ensure the service works reliably.
- **Continuous Integration & Deployment (CI/CD)**: Set up a CI/CD pipeline using tools like Jenkins, GitHub Actions, or GitLab CI to automate testing, building, and deployment.

### 8. Scalability
- **Horizontal Scaling**: Add more instances of the microservice to handle increased load. Use Kubernetes or Docker Swarm for orchestrating multiple instances.
- **Auto-scaling**: Use cloud-native services to automatically scale the service based on load, ensuring high availability during peak traffic.
