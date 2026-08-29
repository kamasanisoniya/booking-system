package com.example.booking.service;

import com.example.booking.dto.ReservationRequest;
import com.example.booking.dto.ReservationResponse;
import com.example.booking.entity.*;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.exception.ReservationNotFoundException;
import com.example.booking.repository.ReservationRepository;
import com.example.booking.repository.ResourceRepository;
import com.example.booking.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public ReservationService(ReservationRepository reservationRepository, ResourceRepository resourceRepository, UserRepository userRepository, CurrentUserService currentUserService) {
        this.reservationRepository = reservationRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public ReservationResponse create(ReservationRequest req) {
        if (req.getEndTime().isBefore(req.getStartTime()) || req.getEndTime().isEqual(req.getStartTime())) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }
        if (req.getPrice() == null || req.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("price must be greater than zero");
        }
        Resource resource = resourceRepository.findById(req.getResourceId()).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        User user = currentUserService.getCurrentUser();

        Reservation r = Reservation.builder()
                .resource(resource)
                .user(user)
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .price(req.getPrice())
                .status(ReservationStatus.PENDING)
                .build();
        r = reservationRepository.save(r);
        return toDto(r);
    }

    public Page<ReservationResponse> findReservations(ReservationStatus status, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable, boolean onlyOwner) {
        User currentUser = onlyOwner ? currentUserService.getCurrentUser() : null;
        Page<Reservation> page = reservationRepository.findWithFilters(currentUser, status, minPrice, maxPrice, pageable, onlyOwner);
        return page.map(this::toDto);
    }

    public ReservationResponse findById(Long id) {
        Reservation r = reservationRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));
        User current = currentUserService.getCurrentUser();
        if (!current.getRole().equals(Role.ADMIN) && !r.getUser().getId().equals(current.getId())) {
            throw new AccessDeniedException("Access denied");
        }
        return toDto(r);
    }

    @Transactional
    public ReservationResponse update(Long id, ReservationRequest req) {
        Reservation r = reservationRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));
        User current = currentUserService.getCurrentUser();
        if (!current.getRole().equals(Role.ADMIN) && !r.getUser().getId().equals(current.getId())) {
            throw new AccessDeniedException("Access denied");
        }
        if (req.getStartTime() != null) r.setStartTime(req.getStartTime());
        if (req.getEndTime() != null) r.setEndTime(req.getEndTime());
        if (req.getPrice() != null) r.setPrice(req.getPrice());
        if (req.getResourceId() != null) {
            Resource resource = resourceRepository.findById(req.getResourceId()).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
            r.setResource(resource);
        }
        r = reservationRepository.save(r);
        return toDto(r);
    }

    @Transactional
    public void delete(Long id) {
        Reservation r = reservationRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));
        User current = currentUserService.getCurrentUser();
        if (!current.getRole().equals(Role.ADMIN) && !r.getUser().getId().equals(current.getId())) {
            throw new AccessDeniedException("Access denied");
        }
        reservationRepository.deleteById(id);
    }

    private ReservationResponse toDto(Reservation r) {
        return ReservationResponse.builder()
                .id(r.getId())
                .username(r.getUser().getUsername())
                .resourceId(r.getResource().getId())
                .startTime(r.getStartTime())
                .endTime(r.getEndTime())
                .price(r.getPrice())
                .status(r.getStatus())
                .build();
    }
}
