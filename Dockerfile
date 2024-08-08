FROM clojure AS builder
RUN mkdir -p /usr/src/ideabox
WORKDIR /usr/src/ideabox
COPY project.clj /usr/src/ideabox
RUN lein deps
COPY . /usr/src/ideabox
RUN mv "$(lein ring uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" ideabox-standalone.jar

FROM eclipse-temurin:21-alpine
RUN mkdir -p /usr/lib/ideabox
WORKDIR /usr/lib/ideabox
COPY --from=builder /usr/src/ideabox/ideabox-standalone.jar /usr/lib/ideabox/ideabox-standalone.jar
CMD ["java", "-jar", "ideabox-standalone.jar"]
