package com.smartorders.customer.controller;

import com.smartorders.customer.common.BaseResponse;
import com.smartorders.customer.dto.CreateCustomerRequest;
import com.smartorders.customer.dto.CustomerResponse;
import com.smartorders.customer.dto.UpdateCustomerRequest;
import com.smartorders.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Customer management endpoints")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Create a new customer")
    public ResponseEntity<BaseResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {

        CustomerResponse response = customerService.createCustomer(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(BaseResponse.success("Customer created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<BaseResponse<CustomerResponse>> getCustomerById(
            @PathVariable Long id) {

        CustomerResponse response = customerService.getCustomerById(id);
        return ResponseEntity.ok(BaseResponse.success("Customer retrieved successfully", response));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get customer by email")
    public ResponseEntity<BaseResponse<CustomerResponse>> getCustomerByEmail(
            @PathVariable String email) {

        CustomerResponse response = customerService.getCustomerByEmail(email);
        return ResponseEntity.ok(BaseResponse.success("Customer retrieved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all customers")
    public ResponseEntity<BaseResponse<List<CustomerResponse>>> getAllCustomers() {

        List<CustomerResponse> response = customerService.getAllCustomers();
        return ResponseEntity.ok(BaseResponse.success("Customers retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer")
    public ResponseEntity<BaseResponse<CustomerResponse>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerRequest request) {

        CustomerResponse response = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(BaseResponse.success("Customer updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer")
    public ResponseEntity<BaseResponse<Void>> deleteCustomer(
            @PathVariable Long id) {

        customerService.deleteCustomer(id);
        return ResponseEntity.ok(BaseResponse.success("Customer deleted successfully"));
    }

    // Internal endpoint — called by Order Service via Feign
    @GetMapping("/{id}/validate")
    @Operation(summary = "Validate customer exists and is active (internal use)")
    public ResponseEntity<BaseResponse<CustomerResponse>> validateCustomer(
            @PathVariable Long id) {

        CustomerResponse response = customerService.validateCustomer(id);
        return ResponseEntity.ok(BaseResponse.success("Customer is valid", response));
    }
}