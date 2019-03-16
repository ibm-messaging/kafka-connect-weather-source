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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherSourceConnector extends SourceConnector {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherSourceConnector.class);
    private static final String VERSION = "1.0.0";

    private WeatherSourceConnectorConfig config;

    @Override
    public String version() {
        return VERSION;
    }

    @Override
    public void start(Map<String, String> props) {
        config = new WeatherSourceConnectorConfig(props);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return WeatherSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<String> locations = config.getList(WeatherSourceConnectorConfig.LOCATIONS);
        maxTasks = Integer.min(maxTasks, locations.size());
        int chunkSize = locations.size() / maxTasks;
        List<List<String>> partitions = partition(locations, chunkSize);

        List<Map<String, String>> configs = new ArrayList<>(partitions.size());
        Map<String, String> taskProps = new HashMap<>(config.originalsStrings());
        for (int i = 0; i < partitions.size(); i++) {
            Map<String, String> taskConfigs = new HashMap<>(taskProps);
            taskConfigs.put(WeatherSourceConnectorConfig.LOCATIONS, String.join(",", partitions.get(i)));
            configs.add(taskConfigs);
        }
        LOG.info("taskConfigs: {}", configs);
        return configs;
    }

    @Override
    public ConfigDef config() {
        return WeatherSourceConnectorConfig.CONFIG_DEF;
    }

    @Override
    public void stop() {
    }

    static List<List<String>> partition(List<String> list, int size) {
        final AtomicInteger counter = new AtomicInteger(0);
        return new ArrayList<List<String>>(list.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / size))
                .values());
    }
}
