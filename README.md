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
2. Docker Compose brings up RabbitMQ (port 5672) and Azure Service Bus emulator (port 5673)
3. RabbitAdmin declares the exchange `connector.exchange` and queue `connector.queue`
4. The Spring Shell prompt appears

### Azure Service Bus Emulator

The [Azure Service Bus emulator](https://learn.microsoft.com/en-us/azure/service-bus-messaging/test-locally-with-service-bus-emulator) runs alongside RabbitMQ:

- **AMQP port**: 5673 (host) → 5672 (container) — avoids conflict with RabbitMQ on 5672
- **Management API**: http://localhost:5300
- **Pre-configured**: topic `playground-topic` with subscription `playground-subscription` (namespace `sbemulatorns`)

Connection string for local development:

```
Endpoint=sb://localhost:5673;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=SAS_KEY_VALUE;UseDevelopmentEmulator=true;
```

For management operations, use port 5300:

```
Endpoint=sb://localhost:5300;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=SAS_KEY_VALUE;UseDevelopmentEmulator=true;
```

Config: `servicebus/config.json`

Shell commands:

```
servicebus-publish              # Publish test message to playground-topic
servicebus-consumer-start       # Start consumer (background)
servicebus-consumer-stop        # Stop consumer
servicebus-consumer-consume     # Consume until Ctrl+C
```

### 2. Use the shell

When the shell is ready, run:

```
rabbit-consumer-start
```

This starts the RabbitMQ consumer in the background. Messages received on `connector.queue` are printed to the console. Run `rabbit-consumer-stop` to stop it. You can run other commands (e.g. `rabbit-publish`) while the consumer is running.

Alternatively, run `rabbit-consumer-consume` (same as start; use `rabbit-consumer-stop` for graceful stop).

### 3. Send test messages

From the same shell (while the consumer is running), run:

```
rabbit-publish
```

Or from another terminal, publish messages to RabbitMQ:

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
