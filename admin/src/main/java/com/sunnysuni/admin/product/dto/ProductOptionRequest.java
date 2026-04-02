package com.sunnysuni.admin.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductOptionRequest(
    @NotBlank(message = "옵션 사이즈는 필수입니다.")
    @Size(max = 20, message = "옵션 사이즈는 20자 이하여야 합니다.")
    String size,

    @NotBlank(message = "옵션 컬러는 필수입니다.")
    @Size(max = 50, message = "옵션 컬러는 50자 이하여야 합니다.")
    String color,

    @NotNull(message = "재고는 필수입니다.")
    @PositiveOrZero(message = "재고는 0 이상이어야 합니다.")
    Integer stock,

    @NotNull(message = "추가 금액은 필수입니다.")
    @PositiveOrZero(message = "추가 금액은 0 이상이어야 합니다.")
    Integer additionalPrice
) {
}
