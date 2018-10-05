# Eteherum-workshop

## Goals

- Learn docker, kafka, kuberenets
- Develop dataflow microservices
- Have fun with Ethereum

## Publishing to Kafka

In order to run analytics on Ethereum blockchain we need to ingest data into our pipeline. Kafka will act as a bus between Ethereum node and services which will process the data.

parity (ws) -> service -> kafka

https://github.com/paritytech/parity-ethereum
https://www.parity.io/
https://github.com/mslinn/web3j-scala
