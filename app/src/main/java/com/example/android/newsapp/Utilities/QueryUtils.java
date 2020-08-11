package com.example.android.newsapp.Utilities;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.newsapp.Models.News;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper methods related to requesting and receiving news data from USGS.
 */
public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    public static List<News> fetchNewsData(String requestUrl) {

        final int SLEEPING_TIME = 2000;

        try {
            Thread.sleep(SLEEPING_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}s
        List<News> news = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link News}s
        return news;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        final int READ_TIME = 10000; /* milliseconds */
        final int CONNECT_TIME = 15000; /* milliseconds */

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIME);
            urlConnection.setConnectTimeout(CONNECT_TIME);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
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
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding newses to
        List<News> newses = new ArrayList<>();


        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject testJSON = baseJsonResponse.getJSONObject("response");
            JSONArray jsonArray = testJSON.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject currentNews = jsonArray.getJSONObject(i);

                String dateField;
                if (currentNews.has("webPublicationDate")) {
                    dateField = currentNews.getString("webPublicationDate");
                } else {
                    // assign info about missing info about author
                    dateField = "Unknown date";
                }

                // Switch format date
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateField);
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
                dateField = formattedDate;
                Log.println(Log.INFO, LOG_TAG, String.valueOf("PUBLISHED Date: " + dateField));

                String webTitleObject;
                if (currentNews.has("webTitle")) {
                    webTitleObject = currentNews.getString("webTitle");
                } else {
                    webTitleObject = "Unknown Web Title";
                }

                String sectionNameObject;
                if (currentNews.has("sectionName")) {
                    sectionNameObject = currentNews.getString("sectionName");
                } else {
                    sectionNameObject = "Unknown category";
                }

                String webURLObject;
                if (currentNews.has("webUrl")) {
                    webURLObject = currentNews.getString("webUrl");
                } else {
                    webURLObject = null;
                }

                String imageArticle;
                JSONObject fields = currentNews.getJSONObject("fields");
                if (fields.has("thumbnail")) {
                    imageArticle = fields.getString("thumbnail");
                    Log.println(Log.INFO, LOG_TAG, String.valueOf("IMAGE URL: " + imageArticle));
                } else {
                    continue; // back to begin of this loop without adding an object without thumbnail img to List
                }

                News news = new News(dateField, webTitleObject, sectionNameObject, webURLObject, imageArticle);
                newses.add(news);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Return the list of newses
        return newses;
    }
}

