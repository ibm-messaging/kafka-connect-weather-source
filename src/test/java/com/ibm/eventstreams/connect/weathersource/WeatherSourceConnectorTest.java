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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class WeatherSourceConnectorTest {

    @Test
    public void testTaskConfigs1Task1Location() {
        WeatherSourceConnector connector = new WeatherSourceConnector();
        Map<String, String> props = new HashMap<>();
        props.put(WeatherSourceConnectorConfig.URL, "");
        props.put(WeatherSourceConnectorConfig.USERNAME, "");
        props.put(WeatherSourceConnectorConfig.PASSWORD, "");
        props.put(WeatherSourceConnectorConfig.LOCATIONS, "Atlanta/Atlanta");
        connector.start(props);
        List<Map<String, String>> configs = connector.taskConfigs(1);
        assertEquals(1, configs.size());
        checkStringList(1, configs.get(0).get(WeatherSourceConnectorConfig.LOCATIONS));
    }

    @Test
    public void testTaskConfigs1Task2Locations() {
        WeatherSourceConnector connector = new WeatherSourceConnector();
        Map<String, String> props = new HashMap<>();
        props.put(WeatherSourceConnectorConfig.URL, "");
        props.put(WeatherSourceConnectorConfig.USERNAME, "");
        props.put(WeatherSourceConnectorConfig.PASSWORD, "");
        props.put(WeatherSourceConnectorConfig.LOCATIONS, "Atlanta/Atlanta,Paris/Paris");
        connector.start(props);
        List<Map<String, String>> configs = connector.taskConfigs(1);
        assertEquals(1, configs.size());
        checkStringList(2, configs.get(0).get(WeatherSourceConnectorConfig.LOCATIONS));
    }

    @Test
    public void testTaskConfigs2Tasks2Locations() {
        WeatherSourceConnector connector = new WeatherSourceConnector();
        Map<String, String> props = new HashMap<>();
        props.put(WeatherSourceConnectorConfig.URL, "");
        props.put(WeatherSourceConnectorConfig.USERNAME, "");
        props.put(WeatherSourceConnectorConfig.PASSWORD, "");
        props.put(WeatherSourceConnectorConfig.LOCATIONS, "Atlanta/Atlanta,Paris/Paris");
        connector.start(props);
        List<Map<String, String>> configs = connector.taskConfigs(2);
        assertEquals(2, configs.size());
        for (int i = 0; i < 2; i++) {
            checkStringList(1, configs.get(i).get(WeatherSourceConnectorConfig.LOCATIONS));
        }
    }

    @Test
    public void testTaskConfigs3Tasks2Locations() {
        WeatherSourceConnector connector = new WeatherSourceConnector();
        Map<String, String> props = new HashMap<>();
        props.put(WeatherSourceConnectorConfig.URL, "");
        props.put(WeatherSourceConnectorConfig.USERNAME, "");
        props.put(WeatherSourceConnectorConfig.PASSWORD, "");
        props.put(WeatherSourceConnectorConfig.LOCATIONS, "Atlanta/Atlanta,Paris/Paris");
        connector.start(props);
        List<Map<String, String>> configs = connector.taskConfigs(3);
        assertEquals(2, configs.size());
        for (int i = 0; i < 2; i++) {
            checkStringList(1, configs.get(i).get(WeatherSourceConnectorConfig.LOCATIONS));
        }
    }

    private static void checkStringList(int expectedSize, String locations) {
        List<String> list = Arrays.asList(locations.split(","));
        assertEquals(expectedSize, list.size());
    }

    @Test
    public void testPartition() {
        List<String> list = Arrays.asList("a", "b", "c", "d", "e", "f");
        Collection<List<String>> partitions1 = WeatherSourceConnector.partition(list, 2);
        assertEquals(3, partitions1.size());
        checkNestedLists(2, partitions1);
        Collection<List<String>> partitions2 = WeatherSourceConnector.partition(list, 3);
        assertEquals(2, partitions2.size());
        checkNestedLists(3, partitions2);
        Collection<List<String>> partitions3 = WeatherSourceConnector.partition(list, 6);
        assertEquals(1, partitions3.size());
        checkNestedLists(6, partitions3);
        Collection<List<String>> partitions4 = WeatherSourceConnector.partition(list, 1);
        assertEquals(6, partitions4.size());
        checkNestedLists(1, partitions4);
    }

    private static void checkNestedLists(int expectedSize, Collection<List<String>> list) {
        for (List<String> partition : list) {
            assertEquals(expectedSize, partition.size());
        }
    }
}
