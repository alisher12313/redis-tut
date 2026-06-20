package com.pm.redistut.api;

import java.math.BigDecimal;

public record ProductUpdateRequest(
        BigDecimal price,
        String description
) {
}
