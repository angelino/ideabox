# Ideabox

A Clojure app designed to save my ideas and learn Clojure.

## Dependencies

- [H2 database](https://h2database.com/html/main.html)
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

## License

Copyright © 2021 Lucas Angelino dos Santos
