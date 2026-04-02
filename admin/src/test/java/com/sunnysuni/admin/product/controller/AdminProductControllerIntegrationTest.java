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
  @DisplayName("Admin product list returns common response")
  void getProductsReturnsOk() throws Exception {
    // Given
    productRepository.save(Product.create("list-product-1", "desc", 10000, null, true, false, ProductStatus.ACTIVE));
    productRepository.save(Product.create("list-product-2", "desc", 11000, null, false, false, ProductStatus.HIDDEN));

    // When & Then
    mockMvc.perform(get("/api/v1/products")
            .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.length()").value(2));
  }

  @Test
  @DisplayName("Admin product detail returns common response")
  void getProductReturnsOk() throws Exception {
    // Given
    Product saved = productRepository.save(Product.create(
        "single-product",
        "desc",
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
        .andExpect(jsonPath("$.data.name").value("single-product"));
  }

  @Test
  @DisplayName("Admin product create returns common response")
  void createProductReturnsOk() throws Exception {
    // Given
    String requestBody = """
        {
          "name": "test-product",
          "description": "desc",
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
        .andExpect(jsonPath("$.data.name").value("test-product"));
  }

  @Test
  @DisplayName("Admin product update returns updated data")
  void updateProductReturnsUpdatedData() throws Exception {
    // Given
    Product saved = productRepository.save(Product.create(
        "before-update",
        "before-desc",
        12000,
        null,
        false,
        false,
        ProductStatus.ACTIVE
    ));

    String requestBody = """
        {
          "name": "after-update",
          "description": "after-desc",
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
        .andExpect(jsonPath("$.data.name").value("after-update"));
  }

  @Test
  @DisplayName("Admin product delete returns ok")
  void deleteProductReturnsOk() throws Exception {
    // Given
    Product saved = productRepository.save(Product.create(
        "to-delete",
        "desc",
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

  @Test
  @DisplayName("All zero stock options become SOLD_OUT")
  void createProductWithSoldOutOptionsReturnsSoldOut() throws Exception {
    // Given
    String requestBody = """
        {
          "name": "sold-out-option-product",
          "description": "desc",
          "price": 10000,
          "salePrice": 9000,
          "isNew": false,
          "isSale": false,
          "status": "ACTIVE",
          "options": [
            { "size": "M", "color": "ivory", "stock": 0, "additionalPrice": 0 },
            { "size": "L", "color": "ivory", "stock": 0, "additionalPrice": 1000 }
          ]
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
        .andExpect(jsonPath("$.data.status").value("SOLD_OUT"))
        .andExpect(jsonPath("$.data.options.length()").value(2));
  }

  @Test
  @DisplayName("Any positive stock option keeps ACTIVE")
  void createProductWithAvailableOptionsReturnsActive() throws Exception {
    // Given
    String requestBody = """
        {
          "name": "active-option-product",
          "description": "desc",
          "price": 10000,
          "salePrice": null,
          "isNew": false,
          "isSale": false,
          "status": "SOLD_OUT",
          "options": [
            { "size": "M", "color": "black", "stock": 0, "additionalPrice": 0 },
            { "size": "L", "color": "black", "stock": 2, "additionalPrice": 1000 }
          ]
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
        .andExpect(jsonPath("$.data.status").value("ACTIVE"))
        .andExpect(jsonPath("$.data.options.length()").value(2));
  }

  @Test
  @DisplayName("Negative option stock returns bad request")
  void createProductReturnsBadRequestWhenOptionStockNegative() throws Exception {
    // Given
    String requestBody = """
        {
          "name": "invalid-option-stock",
          "description": "desc",
          "price": 10000,
          "salePrice": null,
          "isNew": false,
          "isSale": false,
          "status": "ACTIVE",
          "options": [
            { "size": "M", "color": "black", "stock": -1, "additionalPrice": 0 }
          ]
        }
        """;

    // When & Then
    mockMvc.perform(post("/api/v1/products")
            .with(csrf())
            .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false));
  }

  @Test
  @DisplayName("Null option item returns bad request")
  void createProductReturnsBadRequestWhenOptionsContainNull() throws Exception {
    // Given
    String requestBody = """
        {
          "name": "null-option-item",
          "description": "desc",
          "price": 10000,
          "salePrice": null,
          "isNew": false,
          "isSale": false,
          "status": "ACTIVE",
          "options": [null]
        }
        """;

    // When & Then
    mockMvc.perform(post("/api/v1/products")
            .with(csrf())
            .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false));
  }

  @Test
  @DisplayName("Admin product list does not duplicate products when options exist")
  void getProductsDoesNotDuplicateWhenProductHasMultipleOptions() throws Exception {
    // Given
    String requestBody = """
        {
          "name": "multi-option-product",
          "description": "desc",
          "price": 10000,
          "salePrice": null,
          "isNew": false,
          "isSale": false,
          "status": "ACTIVE",
          "options": [
            { "size": "M", "color": "black", "stock": 3, "additionalPrice": 0 },
            { "size": "L", "color": "black", "stock": 3, "additionalPrice": 1000 }
          ]
        }
        """;
    mockMvc.perform(post("/api/v1/products")
            .with(csrf())
            .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk());

    // When & Then
    mockMvc.perform(get("/api/v1/products")
            .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.length()").value(1))
        .andExpect(jsonPath("$.data[0].name").value("multi-option-product"));
  }
}
