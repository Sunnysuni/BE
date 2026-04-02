package com.sunnysuni.store.product.dto;

import com.sunnysuni.common.entity.ProductOption;

public record ProductOptionDetailResponse(
    Long id,
    String size,
    String color,
    Integer stock,
    Integer additionalPrice
) {
  public static ProductOptionDetailResponse from(ProductOption option) {
    return new ProductOptionDetailResponse(
        option.getId(),
        option.getSize(),
        option.getColor(),
        option.getStock(),
        option.getAdditionalPrice()
    );
  }
}
