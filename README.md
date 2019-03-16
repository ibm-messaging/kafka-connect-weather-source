# kafka-connect-weather-source

kafka-connect-weather-source is a Kafka Connect source connector for importing data from [IBM Weather Service](https://cloud.ibm.com/catalog/services/weather-company-data) into Apache Kafka.

The connector is supplied as source code which you can easily build into a JAR file.

## Content
- [Building the connector](#building-the-connector)
- [Configuration](#configuration)
- [Data Formats](#data-formats)
- [Provisioning an IBM Weather Service instance](#provisioning-an-ibm-weather-service-instance)
- [Running the connector](#running-the-connector)

## Building the connector

To build the connector, you must have the following installed:

* [git](https://git-scm.com/)
* [Gradle 4.0 or later](https://gradle.org/)
* Java 8 or later

Clone the repository with the following command:

```shell
git clone https://github.com/ibm-messaging/kafka-connect-weather-source
```

Change directory into the `kafka-connect-weather-source` directory:

```shell
cd kafka-connect-weather-source
```

Build the connector using Gradle:

```shell
gradle build
```

Once built, the output is a single JAR called `build/libs/kafka-connect-weather-source-1.0.jar` which contains all of the required dependencies.

## Configuration

| Name                  | Description                                   | Type    | Default | Valid values                           |
| --------------------- | --------------------------------------------- | ------- | ------- | -------------------------------------- |
| locations             | List of locations to retrieve weather for     | List    |         | London/london,New York/40.77:-73.98    |
| poll.interval.minutes | Interval in minutes to retrieve Weather data  | Integer | 15      |                                        |
| url                   | URL for the IBM Weather Service               | String  |         | https://twcservice.eu-gb.mybluemix.net |
| username              | Username for the IBM Weather Service          | String  |         |                                        |
| password              | Password for the IBM Weather Service          | String  |         |                                        |
| units                 | Units used for Weather data                   | String  | m       | `m` for Metric, `e` for Imperial       |
| topic                 | Kafka Topic to produce Weather data to        | String  | weather |                                        |

A templated configuration file called `config/weather-source.properties` is available.

### Format of the `locations` configuration

The `locations` configuration must be a comma separated list. Each element must have the following format: `<NAME>/<LOCATION>` where:
- `<NAME>` will be the key of the records produced to Kafka for this location. It also appears in the value, see [Data Format](#data-formats)
- `<LOCATION>` is either a city name or geographical coordinates. Use the city name for large cities.
    For example if you want data for London, United Kingdom, it's fine using `london` as the location.
    However, for London, Ontario, Canada, use coordinates in this format: `<latitude>:<longitude>`

For example, to retrieve the weather data for both London, UK and London Canada, use:
```
locations=London UK/london,London Canada/42.99:-81.24
```

## Data Formats

For each location, weather data is produced to Kafka with the following format:
- Key: `<NAME>` used for this location
- Value: JSON document with the following fields:
    - `name`: `<NAME>` used for this location
    - `wx_icon`: The two-digit number to represent the observed weather conditions as described by the [World Meteorological Organization](https://worldweather.wmo.int/wx_icon.htm)
    - `temperature`: Current temperature in Celsius for Metric units or Fahrenheit for Imperial units
    - `humidity`: Relative humidity of the air as a percentage

    For example:
    ```json
    {
        "name": "New York",
        "wx_icon": 34,
        "temperature": 9,
        "humidity": 36
    }
    ```

## Provisioning an IBM Weather Service instance

To use this connector you must provision an instance of the [IBM Weather Service](https://cloud.ibm.com/catalog/services/weather-company-data). Once provisioned, navigate to the `Service Credentials` tab in your instance to retrieve the required configurations for the connector.

## Running the connector

To run the connector, you must have:

* The JAR from building the connector
* A properties file containing the configuration for the connector
* Apache Kafka 1.1.0 or later, either standalone or included as part of an offering such as [IBM Event Streams](https://cloud.ibm.com/catalog/services/event-streams)

The connector expects the topic specified by the [`topic` configuration](#configuration) to exist in the Kafka cluster.

The connector can be run in a Kafka Connect worker in either standalone (single process) or distributed mode.

### Standalone Mode

You need two configuration files, one for the configuration that applies to all of the connectors such as the Kafka bootstrap servers, and another for the configuration specific to the IBM Weather source connector such as the connection information for your Weather Service. For the former, the Kafka distribution includes a file called connect-standalone.properties that you can use as a starting point. For the latter, you can use `config/weather-source.properties` in this repository after replacing `<WEATHER_SERVICE_USERNAME>` and `<WEATHER_SERVICE_PASSWORD>` with your Weather Service credentials.

To run the connector in standalone mode from the directory into which you installed Apache Kafka, you use a command like this:

```shell
bin/connect-standalone.sh connect-standalone.properties weather-source.properties
```

### Distributed Mode

You need an instance of Kafka Connect running in distributed mode. To start the connector, you can use `config/weather-source.json` in this repository after replacing `<WEATHER_SERVICE_USERNAME>` and `<WEATHER_SERVICE_PASSWORD>` with your Weather Service credentials.

To run the connector in distributed mode, you use a command like this:

```shell
curl -X POST -H "Content-Type: application/json" http://localhost:8083/connectors \
  --data "@./config/weather-source.json" 
```