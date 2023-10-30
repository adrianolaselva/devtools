# DevTools

CLI tool developed to make routine tasks of manipulating data streams, exporting, importing, among others, easier.

[![Java CI with Maven](https://github.com/adrianolaselva/devtools/actions/workflows/build-and-run-unit-tests.yml/badge.svg?branch=master)](https://github.com/adrianolaselva/devtools/actions/workflows/build-and-run-unit-tests.yml)

## How To Use

Below are some usage examples from the source code downloaded from the repository.

## Build Application

```shell
make build-application-uber-jar
```
> Run this command to generate an `uber-jar` from the CLI tool, that is, a `.jar` file with its respective dependencies (in other words a fat jar).

```shell
java -jar target/*-runner.jar -h
```
> Run this command after building the `.jar` file.

## Build Application Using `JPackage`

```shell
make build-package
```
> Run this command to generate the CLI tool binary, the data will be exported in the directory `./devtools/*`.

```shell
./build/devtools/bin/devtools -h
```
> Run this command after building the package.

## Command structure

Example of output after execution passing `-h` (helper) as a parameter

```shell
    ____                 __                         ______            __
   / __ \___ _   _____  / /___  ____  ___  _____   /_  __/___  ____  / /____
  / / / / _ \ | / / _ \/ / __ \/ __ \/ _ \/ ___/    / / / __ \/ __ \/ / ___/
 / /_/ /  __/ |/ /  __/ / /_/ / /_/ /  __/ /       / / / /_/ / /_/ / (__  )
/_____/\___/|___/\___/_/\____/ .___/\___/_/       /_/  \____/\____/_/____/
                            /_/

Usage: devtools [-hV] [COMMAND]

DevTools is a command-line tool (CLI) designed to simplify routine tasks faced
by developers in their
day-to-day lives. This tool was created to group a series of essential
commands, providing developers with
a more productive and efficient.

  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  kafka-consumer
                  Command responsible for enabling access to an Apache Kafka
                    instance to consume events
```
> Example of helper printed after executing the command.

## Examples

Below are some examples of using the tool.

```shell
./build/devtools/bin/devtools kafka-consumer -b localhost:9092 -t event-stream
```
> In this example we just consult and print the information from a topic

```shell
./build/devtools/bin/devtools kafka-consumer -b localhost:9092 -t event-stream \
  -o "2023-01-30T20:03:00.000-0300"
```
> In this example we consult and print the information of a topic passing the offset as date

```shell
./build/devtools/bin/devtools kafka-consumer -b localhost:9092 -t event-stream \
  --transform-method=GROOVY \
  --transform-path=./src/test/resources/scripts/groovy/transform_example_01.groovy
```
> In this example we consult and print the information of a topic, but we process a transformation first using a groovy script

```shell
./build/devtools/bin/devtools kafka-consumer -b localhost:9092 -t event-stream \
  --output-type=JSON_LINE \
  --output-path=./out/export.jsonl
```
> In this example we query and export data from a topic to a file

```shell
./build/devtools/bin/devtools kafka-consumer -b localhost:9092 -t event-stream \
  --output-type=KAFKA \
  --output-topic=event-stream-output
```
> In this example we query and export data from one topic to another

## References

- [Tool used for integrated testing](https://java.testcontainers.org/supported_docker_environment/)
- [Configuration to run integrated tests using colima](https://golang.testcontainers.org/system_requirements/using_colima/)
