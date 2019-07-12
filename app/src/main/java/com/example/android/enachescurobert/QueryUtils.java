/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.enachescurobert;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving parkingSpot data from USGS.
 */
public final class QueryUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the USGS dataset and return a list of {@link Parking} objects.
     */
    public static List<Parking> fetchParkingData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Parking}s
        List<Parking> parkingSpots = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Parking}s
        return parkingSpots;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the parkingSpot JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Parking} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Parking> extractFeatureFromJson(String parkingSpotJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(parkingSpotJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding parkingSpots to
        List<Parking> parkingSpots = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(parkingSpotJSON);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or parkingSpots).
            //JSONArray parkingSpotArray = baseJsonResponse.getJSONArray("features");
            JSONArray parkingSpotArray = baseJsonResponse.getJSONArray("feeds");


            // For each parkingSpot in the parkingSpotArray, create an {@link Parking} object
//            for (int i = 0; i < parkingSpotArray.length(); i++) {

                // Get a single parkingSpot at position i within the list of parkingSpots

                int lastEntry = parkingSpotArray.length();

                JSONObject currentParking = parkingSpotArray.getJSONObject(lastEntry - 1);

                // For a given parkingSpot, extract the JSONObject associated with the
                // key called "properties", which represents a list of all properties
                // for that parkingSpot.
                //JSONObject properties = currentParking.getJSONObject("properties");

                // Extract the value for the key called "mag"
                double magnitude = currentParking.getDouble("field1");

                double magnitude2 = currentParking.getDouble("field2");

                double magnitude3 = currentParking.getDouble("field3");

                String dataPostarii = currentParking.getString("created_at");

                // Extract the value for the key called "place"
                //String location = properties.getString("place");
                String location = currentParking.getString("entry_id");
                String locUnu = "Loc de parcare 1";
                String locDoi = "Loc de parcare 2";
                String locTrei = "Loc de parcare 3";

                // Extract the value for the key called "time"
                //long time = properties.getLong("time");
                long time = 1554205570;


                // Extract the value for the key called "url"
                //String url = properties.getString("url");
                String url = "https://play.google.com/store/apps/developer?id=Enachescu+Robert";

                // Create a new {@link Parking} object with the magnitude, location, time,
                // and url from the JSON response.
                Parking parkingSpot = new Parking(magnitude, locUnu, time, url);
                Parking parkingSpot2 = new Parking(magnitude2, locDoi, time, url);
                Parking parkingSpot3 = new Parking(magnitude3, locTrei, time, url);


            // Add the new {@link Parking} to the list of parkingSpots.
                parkingSpots.add(parkingSpot);
                parkingSpots.add(parkingSpot2);
                parkingSpots.add(parkingSpot3);

//            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the parkingSpot JSON results", e);
        }

        // Return the list of parkingSpots
        return parkingSpots;
    }

}