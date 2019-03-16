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

import java.io.IOException;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class WeatherClient {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherClient.class);

    private final OkHttpClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String url;
    private final String units;

    public WeatherClient(String url, String username, String password, String units) {
        this.url = url;
        this.client = new OkHttpClient.Builder()
                .authenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        if (response.request().header("Authorization") != null) {
                            return null;
                        }
                        return response.request().newBuilder()
                                .header("Authorization", Credentials.basic(username, password))
                                .build();
                    }
                })
                .build();
        this.units = units;
    }

    public LocationCoordinates getLocationCoordinates(String name, String locationName) throws Exception {
        HttpUrl url = getLocationServiceURL(locationName);
        try (Response response = query(url)) {
            if (response.isSuccessful()) {
                String body = response.body().string();
                LOG.debug("getLocationCoordinates response body: {}", body);
                LocationData data = objectMapper.readValue(body, LocationData.class);
                return data.getLocationCoordinates(name);
            } else {
                throw new Exception("Failed querying Location for " + locationName + ". Response: " + response);
            }
        }
    }

    public WeatherData getWeatherData(LocationCoordinates locationCoordinates) throws Exception {
        HttpUrl url = getCurrentWeatherURL(locationCoordinates);
        try (Response response = query(url)) {
            if (response.isSuccessful()) {
                String body = response.body().string();
                LOG.debug("getWeatherData response body: {}", body);
                return objectMapper.readValue(body, WeatherData.class);
            } else {
                throw new Exception("Failed querying Weather for " + locationCoordinates + ". Response: " + response);
            }
        }
    }

    private HttpUrl getCurrentWeatherURL(LocationCoordinates locationCoordinates) throws URISyntaxException {
        return HttpUrl.parse(url).newBuilder().addPathSegments("api/weather/v1/geocode")
                .addPathSegment(String.valueOf(locationCoordinates.latitude()))
                .addPathSegment(String.valueOf(locationCoordinates.longitude()))
                .addPathSegment("observations.json")
                .addQueryParameter("language", "en-US")
                .addQueryParameter("units", units).build();
    }

    private HttpUrl getLocationServiceURL(String city) throws URISyntaxException {
        return HttpUrl.parse(url).newBuilder().addPathSegments("api/weather/v3/location/search")
                .addQueryParameter("query", city)
                .addQueryParameter("locationType", "city")
                .addQueryParameter("language", "en-US").build();
    }

    private Response query(HttpUrl url) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        LOG.debug("Querying: {}", url);
        Request request = requestBuilder.url(url).build();
        Call call = client.newCall(request);
        Response response = call.execute();
        LOG.debug("Response: {}", response);
        return response;
    }

}
