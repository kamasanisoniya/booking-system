package com.example.booking.repository;

import com.example.booking.entity.Reservation;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReservationRepositoryImpl implements ReservationRepositoryCustom {

    private final EntityManager em;

    public ReservationRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Page<Reservation> findWithFilters(User userOrNull, ReservationStatus status, BigDecimal min, BigDecimal max, Pageable pageable, boolean onlyOwner) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Reservation> cq = cb.createQuery(Reservation.class);
        Root<Reservation> root = cq.from(Reservation.class);

        List<Predicate> predicates = new ArrayList<>();
        if (onlyOwner && userOrNull != null) {
            predicates.add(cb.equal(root.get("user"), userOrNull));
        }
        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }
        if (min != null && max != null) {
            predicates.add(cb.between(root.get("price"), min, max));
        } else if (min != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("price"), min));
        } else if (max != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("price"), max));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        // sorting handled by pageable
        TypedQuery<Reservation> query = em.createQuery(cq);
        int totalRows = query.getResultList().size();

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Reservation> result = query.getResultList();

        return new PageImpl<>(result, pageable, totalRows);
    }
}
