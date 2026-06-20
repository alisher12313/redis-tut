package com.pm.redistut.domain;

import com.pm.redistut.api.ProductCreateRequest;
import com.pm.redistut.api.ProductUpdateRequest;
import com.pm.redistut.domain.db.ProductEntity;

public interface ProductService {
    ProductEntity create(ProductCreateRequest createRequest);
    ProductEntity update(Long id, ProductUpdateRequest updateRequest);
    ProductEntity getById(Long id);
    void delete(Long id);
}