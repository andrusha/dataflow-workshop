# Docker

## Prerequisites

- [Docker](https://www.docker.com/products/docker-desktop)

## [Architecture](https://docs.docker.com/engine/docker-overview/#docker-architecture)

- Client -(builds)-> Image -(published to)-> Registry
- Client -(pulls from)-> Registry, Image -(is ran inside)-> Container

## Benefits

- Reproducible environment (shared for production, development, etc)
- Isolation (containers don't pollute host system)
- Simpler deploys, rollbacks, scaling
- Efficient resource sharing (container \*-1 node)

## Tasks

- Run docker container
  - `docker run --rm -it supertest2014/nyan`
- Build container and run it
  - `docker build -t intro-docker .`
  - `docker run --rm --init -it -p 8080:8080 intro-docker`
  - `open http://localhost:8080`
