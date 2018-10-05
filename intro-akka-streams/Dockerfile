### Scala build environment
FROM openjdk:10-jdk AS sbt

ENV SBT_VERSION=1.1.6

# Install sbt
RUN curl -L -o "sbt-${SBT_VERSION}.deb" "https://dl.bintray.com/sbt/debian/sbt-${SBT_VERSION}.deb" && \
    dpkg -i "sbt-${SBT_VERSION}.deb" && \
    rm sbt-${SBT_VERSION}.deb && \
    apt-get update && \
    apt-get install sbt

### Cacher
FROM sbt AS cacher

WORKDIR /build
ADD build.sbt /build/
ADD project /build/project
RUN sbt update

### Builder
FROM cacher AS builder

WORKDIR /build
ADD . /build
RUN sbt stage

### Execution environment
FROM openjdk:10 AS runner

WORKDIR /app
COPY --from=builder /build/target/universal/stage /app

CMD "/app/bin/wikipedia-changes"