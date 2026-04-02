package com.sunnysuni.store.product.controller;

import com.sunnysuni.common.entity.Product;
import com.sunnysuni.common.entity.ProductOption;
import com.sunnysuni.common.enums.ProductStatus;
import com.sunnysuni.common.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerIntegrationTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ProductRepository productRepository;

  @BeforeEach
  void setUp() {
    productRepository.deleteAll();
  }

  @Test
  @DisplayName("상품 목록 조회 시 ACTIVE 상태 상품만 반환합니다.")
  void getProductsReturnsActiveProductsOnly() throws Exception {
    // Given
    productRepository.save(Product.create("공개 상품", "설명", 10000, null, true, false, ProductStatus.ACTIVE));
    productRepository.save(Product.create("비공개 상품", "설명", 10000, null, true, false, ProductStatus.HIDDEN));

    // When & Then
    mockMvc.perform(get("/api/v1/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.content.length()").value(1))
        .andExpect(jsonPath("$.data.content[0].name").value("공개 상품"));
  }

  @Test
  @DisplayName("상품 상세 조회 시 비활성 상품은 404 공통 응답을 반환합니다.")
  void getProductReturnsNotFoundWhenNotActive() throws Exception {
    // Given
    Product hidden = productRepository.save(
        Product.create("숨김 상품", "설명", 10000, null, false, false, ProductStatus.HIDDEN)
    );

    // When & Then
    mockMvc.perform(get("/api/v1/products/{id}", hidden.getId()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false));
  }

  @Test
  @DisplayName("상품 상세 조회 시 옵션과 재고 정보를 함께 반환합니다.")
  void getProductReturnsOptionsWithStock() throws Exception {
    // Given
    Product active = Product.create("상세 상품", "상세 설명", 12000, 10000, true, true, ProductStatus.ACTIVE);
    active.addOption(ProductOption.create("M", "ivory", 3, 0));
    Product saved = productRepository.save(active);

    // When & Then
    mockMvc.perform(get("/api/v1/products/{id}", saved.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(saved.getId()))
        .andExpect(jsonPath("$.data.name").value("상세 상품"))
        .andExpect(jsonPath("$.data.options.length()").value(1))
        .andExpect(jsonPath("$.data.options[0].size").value("M"))
        .andExpect(jsonPath("$.data.options[0].stock").value(3));
  }

  @Test
  @DisplayName("상품 상세 조회 시 옵션이 없으면 빈 배열을 반환합니다.")
  void getProductReturnsEmptyOptionsWhenNoOption() throws Exception {
    // Given
    Product saved = productRepository.save(
        Product.create("옵션 없는 상품", "설명", 9000, null, false, false, ProductStatus.ACTIVE)
    );

    // When & Then
    mockMvc.perform(get("/api/v1/products/{id}", saved.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.options.length()").value(0));
  }

  @Test
  @DisplayName("신상품 조회 시 isNew=true, ACTIVE 상품만 반환합니다.")
  void getNewProductsReturnsOnlyNewActiveProducts() throws Exception {
    // Given
    productRepository.save(Product.create("신상품", "설명", 10000, null, true, false, ProductStatus.ACTIVE));
    productRepository.save(Product.create("일반상품", "설명", 10000, null, false, false, ProductStatus.ACTIVE));
    productRepository.save(Product.create("숨김신상품", "설명", 10000, null, true, false, ProductStatus.HIDDEN));

    // When & Then
    mockMvc.perform(get("/api/v1/products/new"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.content.length()").value(1))
        .andExpect(jsonPath("$.data.content[0].name").value("신상품"));
  }

  @Test
  @DisplayName("세일상품 조회 시 isSale=true, ACTIVE 상품만 반환합니다.")
  void getSaleProductsReturnsOnlySaleActiveProducts() throws Exception {
    // Given
    productRepository.save(Product.create("세일상품", "설명", 10000, 9000, false, true, ProductStatus.ACTIVE));
    productRepository.save(Product.create("일반상품", "설명", 10000, null, false, false, ProductStatus.ACTIVE));
    productRepository.save(Product.create("숨김세일상품", "설명", 10000, 9000, false, true, ProductStatus.HIDDEN));

    // When & Then
    mockMvc.perform(get("/api/v1/products/sale"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.content.length()").value(1))
        .andExpect(jsonPath("$.data.content[0].name").value("세일상품"));
  }

  @Test
  @DisplayName("상품 검색 시 키워드 포함 ACTIVE 상품만 반환합니다.")
  void searchProductsReturnsMatchedActiveProducts() throws Exception {
    // Given
    productRepository.save(Product.create("맨투맨 블랙", "설명", 10000, null, false, false, ProductStatus.ACTIVE));
    productRepository.save(Product.create("후드 블랙", "설명", 10000, null, false, false, ProductStatus.HIDDEN));
    productRepository.save(Product.create("티셔츠 화이트", "설명", 10000, null, false, false, ProductStatus.ACTIVE));

    // When & Then
    mockMvc.perform(get("/api/v1/products/search").param("keyword", "블랙"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.content.length()").value(1))
        .andExpect(jsonPath("$.data.content[0].name").value("맨투맨 블랙"));
  }

  @Test
  @DisplayName("검색어가 비어 있으면 400 공통 에러 응답을 반환합니다.")
  void searchProductsReturnsBadRequestWhenKeywordBlank() throws Exception {
    // Given

    // When & Then
    mockMvc.perform(get("/api/v1/products/search").param("keyword", " "))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").isNotEmpty());
  }

  @Test
  @DisplayName("검색어 파라미터가 누락되면 400 공통 에러 응답을 반환합니다.")
  void searchProductsReturnsBadRequestWhenKeywordMissing() throws Exception {
    // Given

    // When & Then
    mockMvc.perform(get("/api/v1/products/search"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").isNotEmpty());
  }
}
