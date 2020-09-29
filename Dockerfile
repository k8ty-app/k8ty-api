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
COPY . .
RUN ["sbt", "docker:stage"]

FROM adoptopenjdk/openjdk14-openj9 as stage0
WORKDIR /opt/docker
COPY --from=build0 /tmp/target/docker/stage/1/opt /1/opt
COPY --from=build0 /tmp/target/docker/stage/2/opt /2/opt
USER root
RUN ["chmod", "-R", "u=rX,g=rX", "/1/opt/docker"]
RUN ["chmod", "-R", "u=rX,g=rX", "/2/opt/docker"]
RUN ["chmod", "u+x,g+x", "/1/opt/docker/bin/k8ty-api"]

FROM adoptopenjdk/openjdk14-openj9 as mainstage
USER root
RUN id -u demiourgos728 1>/dev/null 2>&1 || (( getent group 0 1>/dev/null 2>&1 || ( type groupadd 1>/dev/null 2>&1 && groupadd -g 0 root || addgroup -g 0 -S root )) && ( type useradd 1>/dev/null 2>&1 && useradd --system --create-home --uid 1001 --gid 0 demiourgos728 || adduser -S -u 1001 -G root demiourgos728 ))
WORKDIR /opt/docker
COPY --from=stage0 --chown=demiourgos728:root /1/opt/docker /opt/docker
COPY --from=stage0 --chown=demiourgos728:root /2/opt/docker /opt/docker
EXPOSE 9000
USER 1001:0
ENTRYPOINT ["/opt/docker/bin/k8ty-api"]
CMD []
