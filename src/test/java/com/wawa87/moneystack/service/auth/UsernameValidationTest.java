package com.wawa87.moneystack.service.auth;

import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UsernameValidationTest {
    @Test
    public void testUsernameValidation() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        // Test empty username
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/profile/validateUsername"))
                .header("User-Agent", "Java Test App")
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Gson gson = new Gson();
        ValidationBoolean validationBoolean = gson.fromJson(response.body(), ValidationBoolean.class);

        Assertions.assertEquals("false", validationBoolean.result);

        // Test bad-format username
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/profile/validateUsername/BadUs/ernamme/"))
                .header("User-Agent", "Java Test App")
                .header("Content-Type", "application/json")
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        validationBoolean = gson.fromJson(response.body(), ValidationBoolean.class);

        Assertions.assertEquals("false", validationBoolean.result);

        // Test conflicting username w/case mismatch.
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/profile/validateUsername/aWguthrie"))
                .header("User-Agent", "Java Test App")
                .header("Content-Type", "application/json")
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        validationBoolean = gson.fromJson(response.body(), ValidationBoolean.class);

        Assertions.assertEquals("false", validationBoolean.result);

        // Test username success
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/profile/validateUsername/nonExistentUsername"))
                .header("User-Agent", "Java Test App")
                .header("Content-Type", "application/json")
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        validationBoolean = gson.fromJson(response.body(), ValidationBoolean.class);

        Assertions.assertEquals("true", validationBoolean.result);
    }

    private class ValidationBoolean {
        private String result;
    }
}
