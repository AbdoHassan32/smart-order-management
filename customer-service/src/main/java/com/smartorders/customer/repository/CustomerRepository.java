package com.smartorders.customer.repository;

import com.smartorders.customer.entity.Customer;
import com.smartorders.customer.enums.CustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Customer> findAllByStatus(CustomerStatus status);
}