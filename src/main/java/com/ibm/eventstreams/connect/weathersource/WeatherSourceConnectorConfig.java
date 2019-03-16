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

import java.util.Map;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

public class WeatherSourceConnectorConfig extends AbstractConfig {

    public static final String LOCATIONS = "locations";
    public static final String TOPIC = "topic";
    public static final String URL = "url";
    public static final String POLL_INTERVAL = "poll.interval.minutes";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String UNITS = "units";

    public static final ConfigDef CONFIG_DEF = new ConfigDef()
        .define(LOCATIONS, Type.LIST, Importance.HIGH, "The list of cities to pull weather conditions for")
        .define(TOPIC, Type.STRING, "weather", Importance.HIGH, "The topic to publish data to")
        .define(URL, Type.STRING, Importance.HIGH, "The URL for the IBM Weather service")
        .define(POLL_INTERVAL, Type.LONG, 15, Importance.HIGH, "The refresh interval in minutes")
        .define(USERNAME, Type.STRING, Importance.HIGH, "The username for the IBM Weather service")
        .define(PASSWORD, Type.STRING, Importance.HIGH, "The password for the IBM Weather service")
        .define(UNITS, Type.STRING, "m", Importance.HIGH, "The units of measure to return the data in (for example, e=Imperial(English), m=Metric, h=Hybrid).");

    public WeatherSourceConnectorConfig(ConfigDef definition, Map<?, ?> originals) {
        super(definition, originals);
    }

    public WeatherSourceConnectorConfig(Map<String, String> parsedConfig) {
        this(CONFIG_DEF, parsedConfig);
    }

}
