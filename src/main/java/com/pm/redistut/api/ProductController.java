package com.pm.redistut.api;

import com.pm.redistut.domain.db.ProductEntity;
import com.pm.redistut.domain.service.DbProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.CacheMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.hibernate.FlushMode.MANUAL;


@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final DbProductService service;
    private final ProductDtoMapper mapper;

    @PostMapping
    public ResponseEntity<ProductDto> create(
            @RequestBody ProductCreateRequest request
    ) {
        log.info("Creating product with cacheMode={}", "none");

        ProductEntity product = service.create(request);
        ProductDto dto = mapper.toProductDto(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(
            @PathVariable("id") Long id
    ) {
        log.info("Getting product {} with cacheMode={}", id, "none");

        ProductEntity product = service.getById(id);
        ProductDto dto = mapper.toProductDto(product);

        return ResponseEntity.ok(dto);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(
            @PathVariable("id") Long id,
            @RequestBody ProductUpdateRequest request

    ) {
        log.info("Updating product {} with cacheMode={}", id, "none");

        ProductEntity product = service.update(id, request);
        ProductDto dto = mapper.toProductDto(product);

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long id
    ) {
        log.info("Deleting product {} with cacheMode={}", id, "none");

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}