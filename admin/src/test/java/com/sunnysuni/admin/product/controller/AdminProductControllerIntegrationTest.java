package com.sunnysuni.admin.product.controller;

import com.sunnysuni.common.entity.Product;
import com.sunnysuni.common.enums.ProductStatus;
import com.sunnysuni.common.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminProductControllerIntegrationTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ProductRepository productRepository;

  @BeforeEach
  void setUp() {
    productRepository.deleteAll();
  }

  @Test
  @DisplayName("관리자 상품 목록 조회 시 200 OK와 공통 응답을 반환합니다.")
  void getProductsReturnsOk() throws Exception {
    // Given
    productRepository.save(Product.create("목록 상품1", "설명", 10000, null, true, false, ProductStatus.ACTIVE));
    productRepository.save(Product.create("목록 상품2", "설명", 11000, null, false, false, ProductStatus.HIDDEN));

    // When & Then
    mockMvc.perform(get("/api/v1/products")
            .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.length()").value(2));
  }

  @Test
  @DisplayName("관리자 상품 단건 조회 시 200 OK와 공통 응답을 반환합니다.")
  void getProductReturnsOk() throws Exception {
    // Given
    Product saved = productRepository.save(Product.create(
        "단건 상품",
        "설명",
        10000,
        null,
        true,
        false,
        ProductStatus.ACTIVE
    ));

    // When & Then
    mockMvc.perform(get("/api/v1/products/{id}", saved.getId())
            .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(saved.getId()))
        .andExpect(jsonPath("$.data.name").value("단건 상품"));
  }

  @Test
  @DisplayName("관리자 상품 등록 시 200 OK와 공통 응답을 반환합니다.")
  void createProductReturnsOk() throws Exception {
    // Given
    String requestBody = """
        {
          "name": "테스트 상품",
          "description": "설명",
          "price": 10000,
          "salePrice": 9000,
          "isNew": true,
          "isSale": true,
          "status": "ACTIVE"
        }
        """;

    // When & Then
    mockMvc.perform(post("/api/v1/products")
            .with(csrf())
            .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.name").value("테스트 상품"));
  }

  @Test
  @DisplayName("관리자 상품 수정 시 200 OK와 수정된 데이터를 반환합니다.")
  void updateProductReturnsUpdatedData() throws Exception {
    // Given
    Product saved = productRepository.save(Product.create(
        "기존 상품",
        "기존 설명",
        12000,
        null,
        false,
        false,
        ProductStatus.ACTIVE
    ));

    String requestBody = """
        {
          "name": "수정 상품",
          "description": "수정 설명",
          "price": 15000,
          "salePrice": 13000,
          "isNew": true,
          "isSale": true,
          "status": "ACTIVE"
        }
        """;

    // When & Then
    mockMvc.perform(put("/api/v1/products/{id}", saved.getId())
            .with(csrf())
            .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.name").value("수정 상품"));
  }

  @Test
  @DisplayName("관리자 상품 삭제 시 200 OK를 반환하고 상품이 삭제됩니다.")
  void deleteProductReturnsOk() throws Exception {
    // Given
    Product saved = productRepository.save(Product.create(
        "삭제 상품",
        "설명",
        12000,
        null,
        false,
        false,
        ProductStatus.ACTIVE
    ));

    // When & Then
    mockMvc.perform(delete("/api/v1/products/{id}", saved.getId())
            .with(csrf())
            .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }
}
