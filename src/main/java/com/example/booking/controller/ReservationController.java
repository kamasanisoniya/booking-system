package com.example.booking.controller;

import com.example.booking.dto.ReservationRequest;
import com.example.booking.dto.ReservationResponse;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.entity.Role;
import com.example.booking.entity.User;
import com.example.booking.service.CurrentUserService;
import com.example.booking.service.ReservationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final CurrentUserService currentUserService;

    public ReservationController(ReservationService reservationService, CurrentUserService currentUserService) {
        this.reservationService = reservationService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ReservationResponse> create(@Valid @RequestBody ReservationRequest req) {
        return ResponseEntity.status(201).body(reservationService.create(req));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<ReservationResponse>> list(
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        User current = currentUserService.getCurrentUser();
        boolean onlyOwner = !current.getRole().equals(Role.ADMIN);
        Page<ReservationResponse> page = reservationService.findReservations(status, minPrice, maxPrice, pageable, onlyOwner);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ReservationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ReservationResponse> update(@PathVariable Long id, @Valid @RequestBody ReservationRequest req) {
        return ResponseEntity.ok(reservationService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
