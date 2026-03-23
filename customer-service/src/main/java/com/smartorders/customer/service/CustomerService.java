package com.smartorders.customer.service;

import com.smartorders.customer.dto.CreateCustomerRequest;
import com.smartorders.customer.dto.CustomerResponse;
import com.smartorders.customer.dto.UpdateCustomerRequest;

import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(CreateCustomerRequest request);

    CustomerResponse getCustomerById(Long id);

    CustomerResponse getCustomerByEmail(String email);

    List<CustomerResponse> getAllCustomers();

    CustomerResponse updateCustomer(Long id, UpdateCustomerRequest request);

    void deleteCustomer(Long id);

    // Called by Order Service via Feign — validates customer exists and is ACTIVE
    CustomerResponse validateCustomer(Long id);
}