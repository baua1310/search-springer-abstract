package de.uniregensburg.springer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ApiClient {
    
    private static String url = "https://api.springernature.com/meta/v2/json";
    private static int maxResults = 10; // maximum is 100 (p query param); 100 results in some crashes -> only querying 10 at a time is better
    // Springer is limiting request withing a certain timeslot (300 hits / minute)
    // configure max requests in a row
    private static int maxRequestsInARow = 300;
    // time to wait in seconds if max requests is reached
    private static int waitTime = 180;
    // counter of requests
    private static int requestCounter = 0;
    private static int totalRequests = 0;
    private static int badRequests = 0;

    private ApplicationProperties applicationProperties;

    public ApiClient() throws IOException {
        applicationProperties = new ApplicationProperties();
    }

    /*
     * Returns JSON array containing records of API call
     * 
     * @return  Json array
     * 
     * @param   query   search query
     */
    public JsonArray getRecords(String query) throws IOException {
        return getRecords(query, 1);
    }

    /*
     * Returns JSON array containing records of API call
     * 
     * @return  json array
     * 
     * @param   query   search query
     * @param   start   start index
     */
    public JsonArray getRecords(String query, int start) throws IOException {

        // Setup url params
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("api_key", applicationProperties.getApiKey());
        requestParams.put("p", String.valueOf(maxResults));
        requestParams.put("q", query);
        requestParams.put("s", String.valueOf(start));

        // put together url and urlencode it
        String encodedURL = requestParams.keySet().stream()
            .map(key -> key + "=" + encodeValue(requestParams.get(key)))
            .collect(Collectors.joining("&", url + "?", ""));

        // make http request
        HttpResponse<String> response = makeHttpRequest(encodedURL);

        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            System.out.println("Bad response");
            System.out.println(response.statusCode());
            System.out.println(response.body());
            System.out.println("Request: " + encodedURL);
            // skip to next request
            return getRecords(query, start + maxResults);
        }

        // convert response body to json
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (jsonObject == null) {
            return null;
        }

        // get next page url
        JsonElement nextPage = jsonObject.get("nextPage");

        // get start index of current response
        JsonElement result = jsonObject.get("result");
        JsonObject resultObject = result.getAsJsonArray().get(0).getAsJsonObject();
        start = resultObject.get("start").getAsInt();

        JsonElement records = jsonObject.get("records");
        JsonArray recordsArray = records.getAsJsonArray();

        if (nextPage != null) {
            recordsArray.addAll(getRecords(query, start + maxResults));
        }

        return recordsArray;

    }

    private HttpResponse<String> makeHttpRequest(String encodedURL) {
        // create new http client
        HttpClient client = HttpClient.newHttpClient();

        // create new request
        HttpRequest request = HttpRequest.newBuilder(
            URI.create(encodedURL))
        .header("accept", "application/json")
        .build();

        requestCounter++;
        // check if max requests is reached
        if (requestCounter > maxRequestsInARow) {
            System.out.println("Request limit reached, waiting " + waitTime + " seconds");
            try {
                // sleep for given wait time in seconds
                Thread.sleep(waitTime * 1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // reset counter
            requestCounter = 1;
        }
        totalRequests++;
        System.out.println("Request " + requestCounter + "/" + maxRequestsInARow + " | Total requests: " + totalRequests);

        try {
            // time-out, try again
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_GATEWAY_TIMEOUT) {
                badRequests++;
                System.out.println("Gateway time out, try again | bad requests: " + badRequests);
                if (badRequests > 3) {
                    badRequests = 0;
                    return response;
                }
                return makeHttpRequest(encodedURL);
            }
            if (response.statusCode() == HttpURLConnection.HTTP_CLIENT_TIMEOUT) {
                badRequests++;
                System.out.println("Request time out, try again | bad requests: " + badRequests);
                if (badRequests > 3) {
                    badRequests = 0;
                    return response;
                }
                return makeHttpRequest(encodedURL);
            }
            badRequests = 0;
            return response;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * Returns encoded value of string
     * 
     * @return  encoded value
     * 
     * @param   value   input string
     */
    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

}
