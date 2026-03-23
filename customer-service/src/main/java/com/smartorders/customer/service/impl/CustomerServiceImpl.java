package com.smartorders.customer.service.impl;

import com.smartorders.customer.dto.CreateCustomerRequest;
import com.smartorders.customer.dto.CustomerResponse;
import com.smartorders.customer.dto.UpdateCustomerRequest;
import com.smartorders.customer.entity.Customer;
import com.smartorders.customer.enums.CustomerStatus;
import com.smartorders.customer.exception.CustomerNotFoundException;
import com.smartorders.customer.exception.EmailAlreadyExistsException;
import com.smartorders.customer.mapper.CustomerMapper;
import com.smartorders.customer.repository.CustomerRepository;
import com.smartorders.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        log.info("Creating customer with email: {}", request.getEmail());

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        Customer customer = customerMapper.toEntity(request);
        Customer saved = customerRepository.save(customer);

        log.info("Customer created successfully with id: {}", saved.getId());
        return customerMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        log.info("Fetching customer with id: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByEmail(String email) {
        log.info("Fetching customer with email: {}", email);

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with email: " + email));

        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        log.info("Fetching all customers");
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(Long id, UpdateCustomerRequest request) {
        log.info("Updating customer with id: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());

        Customer updated = customerRepository.save(customer);

        log.info("Customer updated successfully with id: {}", id);
        return customerMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with id: {}", id);

        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }

        customerRepository.deleteById(id);
        log.info("Customer deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse validateCustomer(Long id) {
        log.info("Validating customer with id: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new CustomerNotFoundException(
                    "Customer is not active. Current status: " + customer.getStatus());
        }

        return customerMapper.toResponse(customer);
    }
}