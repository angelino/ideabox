# Ideabox

A Clojure app designed to save my ideas and learn Clojure.

## Dependencies

- [H2 database](https://h2database.com/html/main.html) for localhost dev
- [Postgres](https://h2database.com/html/main.html)
- [Bulma CSS](https://bulma.io)

## Usage

```shell script
  lein ring server $PORT
```

or 

```shell script
  lein run
```

## Executable JAR

```shell script
  lein ring uberjar

  java -jar ./target/ideabox-standalone.jar
```

## Generate the docs

```shell script
  lein codox
```

## Run on Docker

```shell script
  docker compose up -d
```
and see the logs

```shell script
  docker compose logs
```

## License

Copyright © 2024 Lucas Angelino dos Santos
