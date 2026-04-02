package com.sunnysuni.common.dto;

import com.sunnysuni.common.entity.ProductOption;

public record ProductOptionResponse(
    Long id,
    String size,
    String color,
    Integer stock,
    Integer additionalPrice
) {
  public static ProductOptionResponse from(ProductOption option) {
    return new ProductOptionResponse(
        option.getId(),
        option.getSize(),
        option.getColor(),
        option.getStock(),
        option.getAdditionalPrice()
    );
  }
}
