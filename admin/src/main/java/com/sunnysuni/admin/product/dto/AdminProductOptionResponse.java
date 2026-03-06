package com.sunnysuni.admin.product.dto;

import com.sunnysuni.common.entity.ProductOption;

public record AdminProductOptionResponse(
    Long id,
    String size,
    String color,
    Integer stock,
    Integer additionalPrice
) {
  public static AdminProductOptionResponse from(ProductOption option) {
    return new AdminProductOptionResponse(
        option.getId(),
        option.getSize(),
        option.getColor(),
        option.getStock(),
        option.getAdditionalPrice()
    );
  }
}
