package com.pm.redistut.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pm.redistut.api.ProductCreateRequest;
import com.pm.redistut.api.ProductUpdateRequest;
import com.pm.redistut.domain.db.ProductEntity;

public interface ProductService {
    ProductEntity create(ProductCreateRequest createRequest);
    ProductEntity update(Long id, ProductUpdateRequest updateRequest);
    ProductEntity getById(Long id) throws JsonProcessingException;
    void delete(Long id);
}