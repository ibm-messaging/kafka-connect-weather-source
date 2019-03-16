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
package com.ibm.eventstreams.connect.weatherapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.kafka.connect.data.Struct;
import org.junit.Test;


public class WeatherDataTest {

    @Test
    public void testToString() {
        WeatherData data = new WeatherData();
        data.obs = data.new Observation();
        data.obs.humidity = new Integer(123);
        data.obs.temperature = new Integer(456);
        data.obs.wx_icon = new Integer(789);

        String dataStr = data.toString();
        assertTrue(dataStr.contains("123"));
        assertTrue(dataStr.contains("456"));
        assertTrue(dataStr.contains("789"));
    }

    @Test
    public void testToStruct() {
        WeatherData data = new WeatherData();
        data.obs = data.new Observation();
        data.obs.humidity = new Integer(123);
        data.obs.temperature = new Integer(456);
        data.obs.wx_icon = new Integer(789);

        Struct dataStruct = data.toStruct("test");
        assertEquals(new Integer(123), dataStruct.getInt32("humidity"));
        assertEquals(new Integer(456), dataStruct.getInt32("temperature"));
        assertEquals(new Integer(789), dataStruct.getInt32("wx_icon"));
    }
}
