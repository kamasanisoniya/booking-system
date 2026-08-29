package com.example.booking;

import com.example.booking.dto.LoginRequest;
import com.example.booking.dto.LoginResponse;
import com.example.booking.dto.ResourceRequest;
import com.example.booking.dto.ResourceResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ResourceControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String getToken(String username, String password) {
        LoginRequest req = new LoginRequest(username, password);
        ResponseEntity<LoginResponse> resp = restTemplate.postForEntity("/auth/login", req, LoginResponse.class);
        return resp.getBody().getToken();
    }

    @Test
    void adminCanCreateResource_and_userCannot() {
        String adminToken = getToken("admin", "Admin@123");
        String userToken = getToken("user", "User@123");

        ResourceRequest rr = ResourceRequest.builder().name("Room A").type("ROOM").description("desc").available(true).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        HttpEntity<ResourceRequest> entity = new HttpEntity<>(rr, headers);

        ResponseEntity<ResourceResponse> createResp = restTemplate.exchange("/resources", HttpMethod.POST, entity, ResourceResponse.class);
        assertEquals(HttpStatus.CREATED, createResp.getStatusCode());
        assertNotNull(createResp.getBody());
        assertNotNull(createResp.getBody().getId());

        // user tries to create
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        userHeaders.setBearerAuth(userToken);
        HttpEntity<ResourceRequest> userEntity = new HttpEntity<>(rr, userHeaders);

        ResponseEntity<String> userCreate = restTemplate.exchange("/resources", HttpMethod.POST, userEntity, String.class);
        assertEquals(HttpStatus.FORBIDDEN, userCreate.getStatusCode());
    }
}
