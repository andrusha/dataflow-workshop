# Kafka

## Prerequisites

- [Docker](https://www.docker.com/products/docker-desktop)
- kafkacat `brew install kafkacat`

## [Architecture](https://kafka.apache.org/documentation/#design)

- Cluster 1-\* Topic 1-\* Partition
- Producer \*-1 Topic 1-\* Subscribers

## Benefits

- Decoupling producer and consumer
- Buffer between services (rpc, scale with load, process at optimal speed)
- Immutable, replayable (robustness for service failures)

## Kafka hands-on

- Run containerized Kafka locally
  - `docker run --rm -e ADVERTISED_HOST=localhost -e ADVERTISED_PORT=9092 -p 9092:9092 spotify/kafka`
- Connect producer
  - `kafkacat -P -b localhost:9092 -t testtopic -p 0`
- Connect a few consumer
  - `kafkacat -b localhost:9092 -G testgroup testtopic`
  - `kafkacat -b localhost:9092 -G secondtestgroup testtopic`
- Publish a bunch of messages in the producer
