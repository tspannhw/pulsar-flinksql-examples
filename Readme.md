## Pulsar Flink SQL Examples

> Pulsar is a highly magnetized rotating neutron star

#### Flink SQL's SELECT * is a natural for Selecting Pulsar 😊

### Environment Setup and Instructions
**Spin up Pulsar and Flink Clusters**
```shell
docker-compose up
```

**Create a Pulsar topic and add retention policies to keep data around**
```shell
./setup.sh
```

At this point run the EventsProducer class to generate some event data. You can find the Producer [here](src/main/java/io/ipolyzos/EventsProducer.java)

**Launch Flink SQL Cli**
```shell
docker exec -it jobmanager ./bin/sql-client.sh
```

**Create a Pulsar Catalog**
```shell
CREATE CATALOG pulsar WITH (
        'type' = 'pulsar-catalog', 
        'catalog-admin-url' = 'http://pulsar:8080',
        'catalog-service-url' = 'pulsar://pulsar:6650'
);
```

**Check the available databases and tables**
```shell
USE CATALOGS pulsar;

SHOW DATABASES;

USE `public/default`;

SHOW TABLES;

DESCRIBE events;
```

**Read all the events we have ingested**
```shell
SELECT * FROM events;
```

**Create a new database**
```shell
CREATE DATABASE IF NOT EXISTS processing;

USE processing;
```

**Create a new table**
```shell
CREATE TABLE click_events (
    eventType STRING,
    productId STRING,
    categoryId STRING,
    categoryCode STRING,
    brand STRING,
    price DOUBLE,
    userid STRING,
    userSession STRING,
    `event_time` TIMESTAMP_LTZ(3) METADATA,
    `key` STRING,
    WATERMARK FOR `event_time` AS `event_time` - INTERVAL '1' SECOND
) WITH (
    'connector' = 'pulsar',
    'topics'  = 'persistent://public/default/events',
    'service-url' = 'pulsar://pulsar:6650',
    'admin-url' = 'http://pulsar:8080',
    'source.start.message-id' = 'earliest' ,
    'format'  = 'json'
);
```

```shell
DESCRIBE click_events;
```

```shell
SELECT * FROM click_events;
```