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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.ibm.eventstreams.connect.weatherapi.LocationCoordinates;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class WeatherSourceTaskTest {

    MockWebServer server;
    HttpUrl url;

    @Before
    public void before() throws Exception {
        server = new MockWebServer();
        server.start();
        url = server.url("/");
    }

    @Test
    public void testToCoordinates() throws Exception {
        WeatherSourceTask task = new WeatherSourceTask();
        LocationCoordinates newYorkCoordinates = new LocationCoordinates("New York", 40.77, -73.98);
        assertEquals(newYorkCoordinates, task.toCoordinates("New York", "40.77:-73.98"));
        assertEquals(newYorkCoordinates, task.toCoordinates("New York", " 40.77 : -73.98 "));
        try {
            task.toCoordinates("New York", " 40.77 : abc");
            fail("Should have thrown");
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    public void testToCoordinatesWithLocation() throws Exception {
        LocationCoordinates atlantaCoordinates = new LocationCoordinates("Atlanta", 33.749, -84.39);
        String location = new String(Files.readAllBytes(Paths.get("./src/test/resources/location.json")));
        server.enqueue(new MockResponse().setBody(location));

        WeatherSourceTask task = new WeatherSourceTask();
        Map<String, String> props = new HashMap<>();
        props.put(WeatherSourceConnectorConfig.URL, url.toString());
        props.put(WeatherSourceConnectorConfig.USERNAME, "");
        props.put(WeatherSourceConnectorConfig.PASSWORD, "");
        props.put(WeatherSourceConnectorConfig.LOCATIONS, "Atlanta/Atlanta");
        task.start(props);

        // From location name
        server.enqueue(new MockResponse().setBody(location));
        assertEquals(atlantaCoordinates, task.toCoordinates("Atlanta", "Atlanta"));
    }

    @Test
    public void testVersion() {
        assertEquals(new WeatherSourceConnector().version(), new WeatherSourceTask().version());
    }
}
