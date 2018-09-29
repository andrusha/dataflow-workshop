# Eteherum-workshop

## Goals

- Learn docker, kafka, kuberenets
- Develop dataflow microservices
- Have fun with Ethereum

## Prerequisites

- [IntelliJIDEA](https://www.jetbrains.com/idea/download/) CommunityEdition + Scala plugin
- [Docker](https://www.docker.com/products/docker-desktop)
- kafkacat `brew install kafkacat`
- scala `brew install scala`

## Publishing to Kafka

In order to run analytics on Ethereum blockchain we need to ingest data into our pipeline. Kafka will act as a bus between Ethereum node and services which will process the data.

geth (rpc) -> service -> kafka

1. Learn how geth RPC works https://github.com/ethereum/wiki/wiki/JSON-RPC
2. Learn how producer API works https://kafka.apache.org/20/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html
