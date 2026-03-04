package com.sunnysuni.admin.product.dto;

import com.sunnysuni.common.enums.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateProductRequest(
    @NotBlank(message = "상품명은 필수입니다.")
    @Size(max = 255, message = "상품명은 255자 이하여야 합니다.")
    String name,

    String description,

    @NotNull(message = "가격은 필수입니다.")
    @PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
    Integer price,

    @PositiveOrZero(message = "할인 가격은 0 이상이어야 합니다.")
    Integer salePrice,

    @NotNull(message = "신상품 여부는 필수입니다.")
    Boolean isNew,

    @NotNull(message = "세일 여부는 필수입니다.")
    Boolean isSale,

    @NotNull(message = "상품 상태는 필수입니다.")
    ProductStatus status
) {
}
