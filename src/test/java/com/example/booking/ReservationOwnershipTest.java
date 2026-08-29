package com.example.booking;

import com.example.booking.dto.LoginRequest;
import com.example.booking.dto.LoginResponse;
import com.example.booking.dto.ReservationRequest;
import com.example.booking.dto.ReservationResponse;
import com.example.booking.dto.ResourceRequest;
import com.example.booking.entity.Role;
import com.example.booking.entity.User;
import com.example.booking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ReservationOwnershipTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String getToken(String username, String password) {
        LoginRequest req = new LoginRequest(username, password);
        ResponseEntity<LoginResponse> resp = restTemplate.postForEntity("/auth/login", req, LoginResponse.class);
        return resp.getBody().getToken();
    }

    @Test
    void userCannotAccessAnotherUsersReservation() {
        // admin creates a resource
        String adminToken = getToken("admin", "Admin@123");
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        adminHeaders.setBearerAuth(adminToken);

        ResourceRequest rr = ResourceRequest.builder().name("Room B").type("ROOM").description("desc").available(true).build();
        HttpEntity<ResourceRequest> createResource = new HttpEntity<>(rr, adminHeaders);
        ResponseEntity<com.example.booking.dto.ResourceResponse> resResp = restTemplate.exchange("/resources", HttpMethod.POST, createResource, com.example.booking.dto.ResourceResponse.class);
        assertEquals(HttpStatus.CREATED, resResp.getStatusCode());
        Long resourceId = resResp.getBody().getId();

        // create another user 'other'
        User other = User.builder()
                .username("other")
                .email("other@example.com")
                .password(passwordEncoder.encode("Other@123"))
                .role(Role.USER)
                .build();
        userRepository.save(other);

        // user creates a reservation
        String userToken = getToken("user", "User@123");
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        userHeaders.setBearerAuth(userToken);

        ReservationRequest rreq = ReservationRequest.builder()
                .resourceId(resourceId)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .price(new BigDecimal("100.00"))
                .build();
        HttpEntity<ReservationRequest> reservationEntity = new HttpEntity<>(rreq, userHeaders);
        ResponseEntity<ReservationResponse> reservationResp = restTemplate.exchange("/reservations", HttpMethod.POST, reservationEntity, ReservationResponse.class);
        assertEquals(HttpStatus.CREATED, reservationResp.getStatusCode());
        Long reservationId = reservationResp.getBody().getId();

        // 'other' tries to get the reservation -> should be FORBIDDEN (403)
        String otherToken = getToken("other", "Other@123");
        HttpHeaders otherHeaders = new HttpHeaders();
        otherHeaders.setBearerAuth(otherToken);
        HttpEntity<Void> otherEntity = new HttpEntity<>(otherHeaders);

        ResponseEntity<String> otherGet = restTemplate.exchange("/reservations/" + reservationId, HttpMethod.GET, otherEntity, String.class);
        assertEquals(HttpStatus.FORBIDDEN, otherGet.getStatusCode());

        // admin can get it
        HttpEntity<Void> admEntity = new HttpEntity<>(adminHeaders);
        ResponseEntity<ReservationResponse> adminGet = restTemplate.exchange("/reservations/" + reservationId, HttpMethod.GET, admEntity, ReservationResponse.class);
        assertEquals(HttpStatus.OK, adminGet.getStatusCode());
    }
}
