FROM adoptopenjdk/openjdk14-openj9 as build0

ARG SBT_VERSION=1.3.13

# Install sbt
RUN \
  apt update && \
  apt install -y curl && \
  curl -L -o sbt-$SBT_VERSION.deb https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install sbt && \
  sbt sbtVersion

WORKDIR /tmp
COPY . /
RUN sbt docker:publishLocal

FROM k8ty-api:0.0.1-SNAPSHOT as mainstage
