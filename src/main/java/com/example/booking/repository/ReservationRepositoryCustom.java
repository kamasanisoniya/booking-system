package com.example.booking.repository;

import com.example.booking.entity.Reservation;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ReservationRepositoryCustom {
    Page<Reservation> findWithFilters(User userOrNull, ReservationStatus status, BigDecimal min, BigDecimal max, Pageable pageable, boolean onlyOwner);
}
