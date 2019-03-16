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

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData {

    public static final Schema SCHEMA = SchemaBuilder.struct().name("weatherdata")
            .field("name", Schema.STRING_SCHEMA)
            .field("wx_icon", Schema.OPTIONAL_INT32_SCHEMA)
            .field("temperature", Schema.OPTIONAL_INT32_SCHEMA)
            .field("humidity", Schema.OPTIONAL_INT32_SCHEMA)
            .build();

    @JsonProperty("observation")
    public Observation obs;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Observation {
        public Observation() {}
        @JsonProperty("temp")
        public Integer temperature;
        @JsonProperty("rh")
        public Integer humidity;
        @JsonProperty("wx_icon")
        public Integer wx_icon;
    }

    public String toString() {
        return "{\"wx_icon\": " + obs.wx_icon + ", \"temperature\": " + obs.temperature + ", \"humidity\": " + obs.humidity + "}";
    }

    public Struct toStruct(String name) {
        Struct struct =  new Struct(SCHEMA);
        struct.put(SCHEMA.field("name"), name);
        if (obs.wx_icon != null)
            struct.put(SCHEMA.field("wx_icon"), obs.wx_icon);
        if (obs.temperature != null)
            struct.put(SCHEMA.field("temperature"), obs.temperature);
        if (obs.humidity != null)
            struct.put(SCHEMA.field("humidity"), obs.humidity);
        return struct;
    }
}
