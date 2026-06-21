package com.pm.redistut.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pm.redistut.domain.CacheMode;
import com.pm.redistut.domain.ProductService;
import com.pm.redistut.domain.db.ProductEntity;
import com.pm.redistut.domain.service.DbProductService;
import com.pm.redistut.domain.service.ManualCachingProductServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.pm.redistut.domain.CacheMode.MANUAL_CACHE;
import static com.pm.redistut.domain.CacheMode.NONE_CACHE;


@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final DbProductService dbProductService;
    @Qualifier("productDtoMapper")
    private final ProductDtoMapper mapper;
    private final ManualCachingProductServiceImpl manualCachingProductService;

    @PostMapping
    public ResponseEntity<ProductDto> create(
            @RequestBody ProductCreateRequest request,
            @RequestParam(value = "cacheMode", defaultValue = "NONE_CACHE") CacheMode cacheMode
    ) {
        log.info("Creating product with cacheMode={}", cacheMode.name());

        ProductService service = resolveProductService(cacheMode);
        ProductEntity product = service.create(request);
        ProductDto dto = mapper.toProductDto(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(
            @PathVariable("id") Long id,
            @RequestParam(value = "cacheMode", defaultValue = "NONE_CACHE") CacheMode cacheMode
    ) throws JsonProcessingException {
        log.info("Getting product {} with cacheMode={}", id, "none");

        ProductService service = resolveProductService(cacheMode);
        ProductEntity product = service.getById(id);
        ProductDto dto = mapper.toProductDto(product);

        return ResponseEntity.ok(dto);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(
            @PathVariable("id") Long id,
            @RequestBody ProductUpdateRequest request,
            @RequestParam(value = "cacheMode", defaultValue = "NONE_CACHE") CacheMode cacheMode
    ) {
        log.info("Updating product {} with cacheMode={}", id, "none");

        ProductService service = resolveProductService(cacheMode);
        ProductEntity product = service.update(id, request);
        ProductDto dto = mapper.toProductDto(product);

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long id,
            @RequestParam(value = "cacheMode", defaultValue = "NONE_CACHE") CacheMode cacheMode
    ) {
        log.info("Deleting product {} with cacheMode={}", id, "none");

        ProductService service = resolveProductService(cacheMode);
        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    private ProductService resolveProductService(CacheMode cacheMode) {
        return switch (cacheMode) {
            case NONE_CACHE -> dbProductService;
            case MANUAL_CACHE -> manualCachingProductService;
        };
    }
}