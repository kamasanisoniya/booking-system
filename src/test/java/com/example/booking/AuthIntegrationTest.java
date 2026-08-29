package com.example.booking;

import com.example.booking.dto.LoginRequest;
import com.example.booking.dto.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void loginWithValidCredentialsReturnsToken() {
        LoginRequest req = new LoginRequest("admin", "Admin@123");
        ResponseEntity<LoginResponse> resp = restTemplate.postForEntity("/auth/login", req, LoginResponse.class);
        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertNotNull(resp.getBody().getToken());
        assertFalse(resp.getBody().getToken().isBlank());
    }
}
