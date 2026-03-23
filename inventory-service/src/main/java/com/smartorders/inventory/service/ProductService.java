package com.smartorders.inventory.service;

import com.smartorders.inventory.dto.CreateProductRequest;
import com.smartorders.inventory.dto.ProductResponse;
import com.smartorders.inventory.dto.UpdateProductRequest;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse getProductById(Long id);

    ProductResponse getProductBySku(String sku);

    List<ProductResponse> getAllProducts();

    ProductResponse updateProduct(Long id, UpdateProductRequest request);

    void deleteProduct(Long id);
}