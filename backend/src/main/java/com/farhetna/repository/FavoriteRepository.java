package com.farhetna.repository;

import com.farhetna.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByCustomer(Customer customer);
    List<Favorite> findByCustomerId(Long customerId);
    Optional<Favorite> findByCustomerIdAndHallId(Long customerId, Long hallId);
    boolean existsByCustomerIdAndHallId(Long customerId, Long hallId);
    void deleteByCustomerIdAndHallId(Long customerId, Long hallId);
}