# Apache Pulsar TableView Demo 

This repo contains several code examples of the ow to use Pulsar's new TableView consumer

## Setup

In order to use this project, you will need a developer environment.

### Toolchain setup

#### Java

For Java, the main tools needed are:

- A JDK, any version above java 11 should work
- Maven


### Options for running a Pulsar Cluster

There are two different ways you can run a standalone Pulsar cluster which will be used to complete the exercises.

#### Using Docker

Pulsar ships docker images which can be used. Theses images should work for both Mac, Linux, and Docker for Windows

See this document for instructions on running Pulsar with docker:

https://pulsar.apache.org/docs/en/standalone-docker/#start-pulsar-in-docker

#### Using Pulsar Distribution

On Mac and Linux, you can also just directly use the Pulsar distribution.

See these two documents for details:

https://pulsar.apache.org/docs/en/standalone/#install-pulsar-standalone (skip the optional tiered storage and connectors)
https://pulsar.apache.org/docs/en/standalone/#start-pulsar-standalone


For best compatibility, use the Java 8 distribution from Oracle, however, more recent versions should work, as well as different Java distributions, but it may result in some warning messages.

## Demo Details

This repo contains three 3 different demos that each show a different usage pattern for the TableView API.

## Building

`mvn compile`

## Running

1. In order to run the demos, you first need to start the `StockTradeGenerator` class that publishes data to multiple 
topics used by the rest of the demos. You can achieve this by running `mvn compile exec:java@generate_data`
2. You can run the first demo class, `StockQuoteStreamJoinStockPositionTable` by running the `mvn compile exec:java@demo1` command
3. You can run the second demo class, `StockQuoteTableJoinStockStatsTable` using the `mvn compile exec:java@demo2` command
4. Likewise, the third demo class, `StockPositionTableJoinStockQuoteTable` can be run using the `mvn compile exec:java@demo3` command

## Monitoring
1. To monitor the stock quote topic, you can run the `mvn compile exec:java@monitor-quotes` command
2. To monitor the stock trade topic, you can run the `mvn compile exec:java@monitor-trades` command

## Docs

The javadoc for the TableView API can be found at https://pulsar.apache.org/api/client/2.10.1/org/apache/pulsar/client/api/TableView.html
