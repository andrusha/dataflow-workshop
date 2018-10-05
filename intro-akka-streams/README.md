# Akka Streams

## Prerequsites

- [IntelliJIDEA](https://www.jetbrains.com/idea/download/) CommunityEdition + Scala plugin
- [Java 10](https://www.oracle.com/technetwork/java/javase/downloads/jdk10-downloads-4416644.html)
- Scala & Sbt `brew install scala sbt`
- [Docker](https://www.docker.com/products/docker-desktop)

## Goals

- [Multi-stage builds](https://docs.docker.com/develop/develop-images/multistage-build/)
- Running and building Scala apps with Sbt and Docker
- Learning about Actors and Streams
- Hands-on experience building Akka-Streams app
- Learn how to decode JSON

## Actors

### Benefits

- Multi-threading without atomics or locks (hard to manage, inefficient, hard to distribute, volatile)
- Transparent network communication (data propagation not through the variables, but through the messages)
- Simplicity of handling failures, fault recovery (shared memory failure handling is complicated, no state is lost due to exception unwinding)

### Internals

- Encapsulated local state
- Non-blocking communication via message passing
- Error handling through supervisor or pushing messages back to sender
- Backpressure support

## Building & running

Building locally:

- `sbt stage`

Building with Docker:

- `docker build -t intro-akka-streams .`
- `docker run --rm -it --init intro-akka-streams`
