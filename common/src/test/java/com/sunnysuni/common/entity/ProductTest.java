package com.sunnysuni.common.entity;

import com.sunnysuni.common.enums.ProductStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductTest {

  @Test
  @DisplayName("Product.create는 전달된 값으로 상품을 생성한다.")
  void createBuildsProductFromArguments() {
    // Given

    // When
    Product product = Product.create(
        "맨투맨",
        "설명",
        10000,
        9000,
        true,
        true,
        ProductStatus.ACTIVE
    );

    // Then
    assertEquals("맨투맨", product.getName());
    assertEquals("설명", product.getDescription());
    assertEquals(10000, product.getPrice());
    assertEquals(9000, product.getSalePrice());
    assertTrue(product.getIsNew());
    assertTrue(product.getIsSale());
    assertEquals(ProductStatus.ACTIVE, product.getStatus());
  }

  @Test
  @DisplayName("Product.update는 필드를 새로운 값으로 갱신한다.")
  void updateMutatesProductFields() {
    // Given
    Product product = Product.create(
        "기존명",
        "기존설명",
        15000,
        null,
        false,
        false,
        ProductStatus.ACTIVE
    );

    // When
    product.update(
        "수정명",
        "수정설명",
        12000,
        10000,
        true,
        true,
        ProductStatus.SOLD_OUT
    );

    // Then
    assertEquals("수정명", product.getName());
    assertEquals("수정설명", product.getDescription());
    assertEquals(12000, product.getPrice());
    assertEquals(10000, product.getSalePrice());
    assertTrue(product.getIsNew());
    assertTrue(product.getIsSale());
    assertEquals(ProductStatus.SOLD_OUT, product.getStatus());
  }
}
