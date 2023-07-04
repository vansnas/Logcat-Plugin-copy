package org.apache.cordova.logcat;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MicrosoftAzureStorageConnection {

    private static final String TAG = "MicrosoftAzureStorageConnection";
    private static final String grantType = "client_credentials";
    private static String clientId;
    private static String clientSecret;
    private static String scope;
    private static final String tennantId = "0c0d142b-bb72-4eb8-a9c5-49b7cc8466af";
    private static BufferedReader reader;

    public void uploadZipFile(String filepath){
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(filepath);
                if (file.exists()) {
                    String token = getAccessToken();
                    Gson gson = new Gson();
                    TokenResponse response = gson.fromJson(token, TokenResponse.class);
                    String accessBlobToken = response.getAccessToken();
                    uploadFileToBlobStorage(accessBlobToken, file);
                    file.delete();
                } else {
                    Log.e(TAG, "File " + file.getName() + " not found (upload to Blob storage ");
                }
            }
        }).start();
    }

    class TokenResponse {
        private String access_token;

        public String getAccessToken() {
            return access_token;
        }
    }

    public static void uploadFileToBlobStorage(String bearerToken, File file) {
        RequestBody body = RequestBody.create(file, MediaType.parse("application/zip"));

        OkHttpClient client = new OkHttpClient();

        int contentLength = 0;

        Request request = new Request.Builder()
                .url("https://stdaflogs.blob.core.windows.net/logcat/" + file.getName())
                .addHeader("Authorization", "Bearer " + bearerToken)
                .addHeader("x-ms-version", "2020-12-06")
                .addHeader("x-ms-date", getXMsDate())
                .addHeader("ContentLength", String.valueOf(contentLength))
                .addHeader("x-ms-blob-type", "BlockBlob")
                .put(body)
                .build();

        try {
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                Log.e(TAG, "Logact zip file was not uploaded, something went wrong");
            }
        } catch (IOException e) {
            Log.e(TAG, "Something went wrong while uploading the logcat zip file to the Azure", e);
        }
    }

    private static String getAccessToken(){
        String url = "https://login.microsoftonline.com/" + tennantId + "/oauth2/V2.0/token";

        clientId = "92c08464-e708-4d91-b5e4-aa97f66cf92c";
        clientSecret = "b9N8Q~kFBnsPv2vsJmjVjyLvaQbeJJBqUZYaXbgV";
        scope = "https://stdaflogs.blob.core.windows.net/.default";

        String encodedCredentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        // HTTP Connection
        HttpURLConnection connection = connectToHTTP(url, encodedCredentials);

        String parameters = "grant_type=" + grantType + "&scope=" + scope;

        sendRequest(connection, parameters);
        reader = readResponse(connection);
        StringBuilder response = processResponse();
        closeReader();

        Log.e("AZURE CONNECTION JAVA", response.toString());

        return response.toString();
    }

    //Creates connection to HTTP
    private static HttpURLConnection connectToHTTP(String url, String encodedCredentials){
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
        } catch (IOException e) {
            Log.e(TAG, "Error creating HTTP connection", e);
        }
        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            Log.e(TAG, "Error creating request method of HTTP connection", e);
        }
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);
        connection.setDoOutput(true);
        return connection;
    }

    //Sends the request
    private static void sendRequest(HttpURLConnection connection, String parameters) {
        try {
            if (connection != null) {
                try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                    outputStream.writeBytes(parameters);
                    outputStream.flush();
                }
            } else {
                Log.e(TAG, "Connection is null");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error sending the request of HTTP connection", e);
        }
    }

    private static BufferedReader readResponse(HttpURLConnection connection){
        int responseCode = 0;
        try {
            responseCode = connection.getResponseCode();
        } catch (IOException e) {
            Log.e(TAG, "Error getting response of HTTP connection", e);
        }
        BufferedReader reader = null;
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } catch (IOException e) {
                Log.e(TAG, "Error initializing HTTP connection response reader", e);
            }
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }
        return reader;
    }

    private static StringBuilder processResponse(){
        StringBuilder response = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to read response from reader", e);
        }
        return response;
    }

    private static void closeReader(){
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to close HTTP connection reader", e);
            }
            reader = null;
        }
    }

    private static String getXMsDate(){
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId zoneId = ZoneId.of("GMT");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");
        String xmsdate = zonedDateTime.format(formatter);

        return xmsdate;
    }

}

