package com.pm.redistut.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.redistut.api.ProductCreateRequest;
import com.pm.redistut.api.ProductUpdateRequest;
import com.pm.redistut.domain.ProductService;
import com.pm.redistut.domain.db.ProductEntity;
import com.pm.redistut.domain.db.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManualCachingProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    private final static String PRODUCT_CACHE_PREFIX = "product:";

    @Override
    public ProductEntity create(ProductCreateRequest createRequest) {
        log.info("Creating product in DB: {}", createRequest.name());
        ProductEntity product = ProductEntity.builder()
                .name(createRequest.name())
                .price(createRequest.price())
                .description(createRequest.description())
                .build();
        return productRepository.save(product);
    }

    @Override
    public ProductEntity update(Long id, ProductUpdateRequest updateRequest) {
        log.info("Updating product in DB: {}", id);
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));

        if (updateRequest.price() != null) {
            product.setPrice(updateRequest.price());
        }
        if (updateRequest.description() != null) {
            product.setDescription(updateRequest.description());
        }

        return productRepository.save(product);
    }

    @Override
    public ProductEntity getById(Long id) throws JsonProcessingException {
        log.info("Getting product: id={}", id);
        var cacheKey = PRODUCT_CACHE_PREFIX + id;

        String objectFromCache = stringRedisTemplate.opsForValue().get(cacheKey);

        if (objectFromCache != null) {
            log.info("Getting product from cache: id={}", id);
            return objectMapper.readValue(objectFromCache, ProductEntity.class);
        }

        log.info("Getting product from db and then cache: id={}", id);
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));

        stringRedisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(entity));

        return entity;
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting product from DB: {}", id);
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }
}
