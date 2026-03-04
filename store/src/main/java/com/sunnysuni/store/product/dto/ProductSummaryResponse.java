package com.sunnysuni.store.product.dto;

import com.sunnysuni.common.entity.Product;
import com.sunnysuni.common.enums.ProductStatus;

public record ProductSummaryResponse(
    Long id,
    String name,
    Integer price,
    Integer salePrice,
    Boolean isNew,
    Boolean isSale,
    ProductStatus status
) {
  public static ProductSummaryResponse from(Product product) {
    return new ProductSummaryResponse(
        product.getId(),
        product.getName(),
        product.getPrice(),
        product.getSalePrice(),
        product.getIsNew(),
        product.getIsSale(),
        product.getStatus()
    );
  }
}
