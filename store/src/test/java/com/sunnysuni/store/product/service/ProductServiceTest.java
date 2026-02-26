package com.sunnysuni.store.product.service;

import com.sunnysuni.common.entity.Product;
import com.sunnysuni.common.enums.ProductStatus;
import com.sunnysuni.common.exception.EntityNotFoundException;
import com.sunnysuni.common.repository.ProductRepository;
import com.sunnysuni.store.product.dto.ProductSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private ProductService productService;

  @Test
  @DisplayName("상품 상세 조회는 ACTIVE 상태가 아니면 EntityNotFoundException을 발생시킵니다.")
  void getProductThrowsWhenStatusIsNotActive() {
    // Given
    Product hidden = Product.create("숨김", "설명", 10000, null, false, false, ProductStatus.HIDDEN);
    when(productRepository.findById(1L)).thenReturn(Optional.of(hidden));

    // When & Then
    assertThatThrownBy(() -> productService.getProduct(1L))
        .isInstanceOf(EntityNotFoundException.class);
  }

  @Test
  @DisplayName("상품 상세 조회는 데이터가 없으면 EntityNotFoundException을 발생시킵니다.")
  void getProductThrowsWhenNotFound() {
    // Given
    when(productRepository.findById(99L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> productService.getProduct(99L))
        .isInstanceOf(EntityNotFoundException.class);
  }

  @Test
  @DisplayName("신상품 조회는 ACTIVE 조건의 전용 리포지토리 메서드를 사용합니다.")
  void getNewProductsUsesDedicatedRepositoryMethod() {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    Product product = Product.create("신상", "설명", 10000, null, true, false, ProductStatus.ACTIVE);
    when(productRepository.findByIsNewTrueAndStatus(ProductStatus.ACTIVE, pageable))
        .thenReturn(new PageImpl<>(List.of(product), pageable, 1));

    // When
    Page<ProductSummaryResponse> result = productService.getNewProducts(pageable);

    // Then
    verify(productRepository).findByIsNewTrueAndStatus(ProductStatus.ACTIVE, pageable);
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).name()).isEqualTo("신상");
  }

  @Test
  @DisplayName("검색은 ACTIVE 상태 조건을 포함해 리포지토리를 호출합니다.")
  void searchProductsUsesActiveStatusCondition() {
    // Given
    Pageable pageable = PageRequest.of(0, 10);
    when(productRepository.findByNameContainingIgnoreCaseAndStatus(any(), any(), any()))
        .thenReturn(Page.empty(pageable));

    // When
    productService.searchProducts("블랙", pageable);

    // Then
    ArgumentCaptor<String> keywordCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<ProductStatus> statusCaptor = ArgumentCaptor.forClass(ProductStatus.class);
    verify(productRepository).findByNameContainingIgnoreCaseAndStatus(
        keywordCaptor.capture(),
        statusCaptor.capture(),
        any(Pageable.class)
    );
    assertThat(keywordCaptor.getValue()).isEqualTo("블랙");
    assertThat(statusCaptor.getValue()).isEqualTo(ProductStatus.ACTIVE);
  }
}
