package de.uniregensburg;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ApiClient {
    
    private static String url = "http://api.springernature.com/meta/v2/json";
    private static int maxResults = 100; // maximum is 100 (p query param)

    private ApplicationProperties applicationProperties;

    public ApiClient() throws IOException {
        applicationProperties = new ApplicationProperties();
    }

    /*
     * Returns JSON object containing result of API call
     * 
     * @return  json object
     * 
     * @param   query   search query
     */
    public void performRequest(String query) throws IOException, InterruptedException {
        // create new http client
        HttpClient client = HttpClient.newHttpClient();

        // Setup url params
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("api_key", applicationProperties.getApiKey());
        requestParams.put("p", String.valueOf(maxResults));
        requestParams.put("q", query);

        // put together url and urlencode it
        String encodedURL = requestParams.keySet().stream()
            .map(key -> key + "=" + encodeValue(requestParams.get(key)))
            .collect(Collectors.joining("&", url + "?", ""));

        // create new request
        HttpRequest request = HttpRequest.newBuilder(
            URI.create(encodedURL))
        .header("accept", "application/json")
        .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());

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
