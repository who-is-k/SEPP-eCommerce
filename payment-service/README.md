# Payment Service

Payment service for the SEPPe-commerce project. This microservice manages payment processing, payment records, payment status management, REST API operations, H2 database persistence, and is designed for event-driven integration using Apache Kafka within a microservices architecture.

## Local Kafka Environment (End-to-End Demonstration)

To demonstrate the event-driven communication between the Order Service and the Payment Service, a local Apache Kafka broker is required. 

The repo includes `infra/docker-compose.yml` to quickly start a single-node Kafka broker in KRaft mode (no Zookeeper).

### Kafka Broker Configuration
* **Broker Address:** `localhost:9092`
* **Kafka Topics:** The system automatically uses the following predefined topics:
  * `order.created` (consumed from Order Service)
  * `payment.completed` (published to Order Service)
  * `payment.failed` (published to Order Service)

**Important:** The Order Service configuration remains completely unchanged. Both microservices will connect to this same `localhost:9092` broker instance.

### How to Start Kafka
Run the following command from the repository root:

```bash
docker compose -f infra/docker-compose.yml up -d
```

### How to Stop Kafka
To stop and remove the Kafka container, run:

```bash
docker compose -f infra/docker-compose.yml down
```
