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

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class LocationDataTest {

    @Test
    public void testGetLocationCoordinates() throws Exception {
        LocationData data = new LocationData();
        data.location = data.new Location();
        data.location.latitude = Arrays.asList(1.0);
        data.location.longitude = Arrays.asList(2.0);

        LocationCoordinates coords = new LocationCoordinates("test", 1.0, 2.0);
        assertEquals(coords, data.getLocationCoordinates("test"));
    }

    @Test
    public void testGetLocationCoordinatesInvalid() throws Exception {
        LocationData data = new LocationData();
        data.location = data.new Location();
        data.location.latitude = Arrays.asList(1.0);
        data.location.longitude = Collections.emptyList();

        try {
            new LocationCoordinates("test", 1.0, 2.0);
        } catch (Exception e) {
            // expected
        }
    }

}
