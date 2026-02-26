package com.sunnysuni.admin.product.service;

import com.sunnysuni.admin.product.dto.CreateProductRequest;
import com.sunnysuni.admin.product.dto.UpdateProductRequest;
import com.sunnysuni.common.entity.Product;
import com.sunnysuni.common.enums.ProductStatus;
import com.sunnysuni.common.exception.EntityNotFoundException;
import com.sunnysuni.common.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminProductServiceTest {
  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private AdminProductService adminProductService;

  @Test
  @DisplayName("상품 등록 시 요청값으로 Product를 생성해 저장합니다.")
  void createProductSavesCreatedEntity() {
    // Given
    CreateProductRequest request = new CreateProductRequest(
        "맨투맨",
        "설명",
        10000,
        9000,
        true,
        true,
        ProductStatus.ACTIVE
    );
    when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    adminProductService.createProduct(request);

    // Then
    ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
    verify(productRepository).save(captor.capture());
    Product saved = captor.getValue();
    assertThat(saved.getName()).isEqualTo("맨투맨");
    assertThat(saved.getPrice()).isEqualTo(10000);
    assertThat(saved.getStatus()).isEqualTo(ProductStatus.ACTIVE);
  }

  @Test
  @DisplayName("상품 수정 시 엔티티 필드가 갱신됩니다.")
  void updateProductMutatesFields() {
    // Given
    Product product = Product.create("기존", "기존설명", 10000, null, false, false, ProductStatus.ACTIVE);
    UpdateProductRequest request = new UpdateProductRequest(
        "수정",
        "수정설명",
        12000,
        11000,
        true,
        true,
        ProductStatus.SOLD_OUT
    );
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    // When
    adminProductService.updateProduct(1L, request);

    // Then
    assertThat(product.getName()).isEqualTo("수정");
    assertThat(product.getSalePrice()).isEqualTo(11000);
    assertThat(product.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);
  }

  @Test
  @DisplayName("존재하지 않는 상품 수정 시 EntityNotFoundException을 발생시키고 저장하지 않습니다.")
  void updateProductThrowsWhenNotFound() {
    // Given
    UpdateProductRequest request = new UpdateProductRequest(
        "수정",
        "설명",
        12000,
        null,
        false,
        false,
        ProductStatus.ACTIVE
    );
    when(productRepository.findById(99L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> adminProductService.updateProduct(99L, request))
        .isInstanceOf(EntityNotFoundException.class);
    verify(productRepository, never()).save(any(Product.class));
  }

  @Test
  @DisplayName("상품 목록 조회는 id 내림차순 정렬로 조회합니다.")
  void getProductsUsesDescSortById() {
    // Given
    when(productRepository.findAll(any(Sort.class))).thenReturn(java.util.List.of());

    // When
    adminProductService.getProducts();

    // Then
    ArgumentCaptor<Sort> captor = ArgumentCaptor.forClass(Sort.class);
    verify(productRepository).findAll(captor.capture());
    Sort.Order order = captor.getValue().getOrderFor("id");
    assertThat(order).isNotNull();
    assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
  }
}
