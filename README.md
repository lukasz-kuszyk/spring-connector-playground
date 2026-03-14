# Spring Connector Playground

Spring Boot application with RabbitMQ integration and an interactive shell.

## Prerequisites

- **Java 21**
- **Docker** and **Docker Compose** (RabbitMQ runs via `compose.yaml`)

## How to Run

### 1. Start the application

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

On startup:

1. Spring Boot starts
2. Docker Compose brings up RabbitMQ (port 5672)
3. RabbitAdmin declares the exchange `connector.exchange` and queue `connector.queue`
4. The Spring Shell prompt appears

### 2. Use the shell

When the shell is ready, run:

```
start-consumer
```

This starts the RabbitMQ consumer and blocks until you press **Ctrl+C**. Messages received on `connector.queue` are printed to the console.

### 3. Send test messages

In another terminal, publish messages to RabbitMQ:

```bash
# Using RabbitMQ Management HTTP API (requires curl)
curl -u guest:guest -X POST -H "Content-Type: application/json" \
  "http://localhost:15672/api/exchanges/%2F/connector.exchange/publish" \
  -d '{"properties":{},"routing_key":"connector.routing.key","payload":"Hello from RabbitMQ","payload_encoding":"string"}'
```

Or use the [RabbitMQ Management UI](http://localhost:15672) (guest/guest) to publish messages to the `connector.exchange` with routing key `connector.routing.key`.

## Running without Docker Compose

If you prefer to run RabbitMQ yourself (e.g. installed locally), start the app with:

```bash
./mvnw spring-boot:run -Dspring.docker.compose.enabled=false
```

Ensure RabbitMQ is available at `localhost:5672` with user `guest` and password `guest`.
