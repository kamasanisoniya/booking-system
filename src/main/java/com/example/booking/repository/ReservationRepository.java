package com.example.booking.repository;

import com.example.booking.entity.Reservation;
import com.example.booking.entity.User;
import com.example.booking.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {
    Page<Reservation> findByUser(User user, Pageable pageable);
    Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);
    Page<Reservation> findByPriceBetween(BigDecimal min, BigDecimal max, Pageable pageable);
}
