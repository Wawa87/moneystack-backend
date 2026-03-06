package com.wawa87.moneystack.service.auth;

import com.wawa87.moneystack.App;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthenticationTest {
    @Test
    public void testAuthentication() throws Exception {
//        String[] args = new String[1];
//        App.main(args);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/authentication/login?username=testuser&password=testpassword"))
                .GET()
                .header("User-Agent", "Java Test App")
                .build();

        HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(send.body());
        System.out.println("Print stuff...");
    }
}
