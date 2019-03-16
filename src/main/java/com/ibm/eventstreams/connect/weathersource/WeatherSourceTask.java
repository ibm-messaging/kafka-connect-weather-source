/*
 * Copyright 2019 IBM Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.ibm.eventstreams.connect.weathersource;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.eventstreams.connect.weatherapi.LocationCoordinates;
import com.ibm.eventstreams.connect.weatherapi.WeatherClient;
import com.ibm.eventstreams.connect.weatherapi.WeatherData;

/**
 * WeatherSourceTask reads weather data from IBM Weather service
 */
public class WeatherSourceTask extends SourceTask {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherSourceTask.class);
    private static final Map<String, ?> NO_SOURCE = Collections.emptyMap();

    private List<LocationCoordinates> locations;
    private String topic;
    private long pollIntervalMs;
    private WeatherClient client;
    private long lastPollTime = 0L;

    @Override
    public String version() {
        return new WeatherSourceConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {
        WeatherSourceConnectorConfig connectorConfig = new WeatherSourceConnectorConfig(props);
        String url = connectorConfig.getString(WeatherSourceConnectorConfig.URL);
        String username = connectorConfig.getString(WeatherSourceConnectorConfig.USERNAME);
        String password = connectorConfig.getString(WeatherSourceConnectorConfig.PASSWORD);
        String units = connectorConfig.getString(WeatherSourceConnectorConfig.UNITS);
        client = new WeatherClient(url, username, password, units);
        locations = parseLocations(connectorConfig.getList(WeatherSourceConnectorConfig.LOCATIONS));
        topic = connectorConfig.getString(WeatherSourceConnectorConfig.TOPIC);
        pollIntervalMs = Duration.ofMinutes(connectorConfig.getLong(WeatherSourceConnectorConfig.POLL_INTERVAL)).toMillis();
        LOG.info("starting");
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        long millis = pollIntervalMs - (System.currentTimeMillis() - lastPollTime);
        if (millis > 0L) {
            LOG.info("Waiting {}ms before polling the Weather Service", millis);
            Thread.sleep(millis);
        }
        List<SourceRecord> records = new ArrayList<>();
        try {
            for (LocationCoordinates location : locations) {
                try {
                    WeatherData weatherData = client.getWeatherData(location);
                    SourceRecord sr = new SourceRecord(
                            NO_SOURCE, NO_SOURCE, topic,
                            Schema.STRING_SCHEMA, location.name(),
                            WeatherData.SCHEMA, weatherData.toStruct(location.name()));
                    records.add(sr);
                    LOG.info("Added a message: {}", weatherData);
                } catch (Exception exc) {
                    LOG.error("Failed getting weather for {}: {}", location, exc.getMessage(), exc);
                } 
            }
        } finally {
            lastPollTime = System.currentTimeMillis();
        }
        return records;
    }

    @Override
    public void stop() {
        LOG.trace("Stopping");
    }

    List<LocationCoordinates> parseLocations(List<String> locations) {
        List<LocationCoordinates> locationCoordinates = new ArrayList<>();
        for (String location : locations) {
            String[] sections = location.split("/");
            if (sections.length != 2) {
                throw new IllegalArgumentException("Invalid location: " + location);
            }
            try {
                locationCoordinates.add(toCoordinates(sections[0], sections[1]));
            } catch (Exception exc) {
                LOG.error("Failed parsing location {}: {}", location, exc.getMessage(), exc);
            }
        }
        LOG.info("Parsed locations {}", locationCoordinates);
        return locationCoordinates;
    }

    LocationCoordinates toCoordinates(String name, String location) throws Exception {
        String[] locationParts = location.split(":");
        if (locationParts.length == 2) {
            Double lat = Double.valueOf(locationParts[0]);
            Double lon = Double.valueOf(locationParts[1]);
            return new LocationCoordinates(name, lat, lon);
        } else {
            return client.getLocationCoordinates(name, location);
        }
    }

}
