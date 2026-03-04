package com.sunnysuni.admin.product.dto;

import com.sunnysuni.common.entity.Product;
import com.sunnysuni.common.enums.ProductStatus;
import java.time.LocalDateTime;

public record ProductResponse(
    Long id,
    String name,
    String description,
    Integer price,
    Integer salePrice,
    Boolean isNew,
    Boolean isSale,
    ProductStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
  public static ProductResponse from(Product product) {
    return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getDescription(),
        product.getPrice(),
        product.getSalePrice(),
        product.getIsNew(),
        product.getIsSale(),
        product.getStatus(),
        product.getCreatedAt(),
        product.getUpdatedAt()
    );
  }
}
