package io.github.dhananjaytrivedi.API_Testing;

import io.github.dhananjaytrivedi.API_Testing.ApiResponse;
import spark.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {
    private String server;                                                              // The address of server

    public ApiClient(String server) {                                                   // Constructor
        this.server = server;                                                           // Initialize server
    }

    public ApiResponse request(String method, String uri) {                             //
        return request(method, uri, null);
    }

    public ApiResponse request(String method, String uri, String requestBody) {
        try {
            URL url = new URL(server + uri);                                       // URL = ServerAddress + URI
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();    // Creating HttpConnection
            connection.setRequestMethod(method);                                        // Setting parameters
            connection.setRequestProperty("Content-Type", "application/json");
            if (requestBody != null) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(requestBody.getBytes("UTF-8"));
                }
            }
            connection.connect();
            InputStream inputStream = connection.getResponseCode() < 400 ?              // Either we get response or error
                    connection.getInputStream() :
                    connection.getErrorStream();
            String body = IOUtils.toString(inputStream);                                // Returning back the API Response
            return new ApiResponse(connection.getResponseCode(), body);
        } catch (IOException e) {                                                       // Catching an exception
            e.printStackTrace();
            throw new RuntimeException("Whoops!  Connection error");
        }
    }
}