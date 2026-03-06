package com.sunnysuni.store.product.dto;

import com.sunnysuni.common.dto.ProductOptionResponse;
import com.sunnysuni.common.entity.Product;
import com.sunnysuni.common.enums.ProductStatus;
import java.time.LocalDateTime;
import java.util.List;

public record ProductDetailResponse(
    Long id,
    String name,
    String description,
    Integer price,
    Integer salePrice,
    Boolean isNew,
    Boolean isSale,
    ProductStatus status,
    List<ProductOptionResponse> options,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
  public static ProductDetailResponse from(Product product) {
    return new ProductDetailResponse(
        product.getId(),
        product.getName(),
        product.getDescription(),
        product.getPrice(),
        product.getSalePrice(),
        product.getIsNew(),
        product.getIsSale(),
        product.getStatus(),
        product.getOptions().stream().map(ProductOptionResponse::from).toList(),
        product.getCreatedAt(),
        product.getUpdatedAt()
    );
  }
}
