package com.sunnysuni.admin.product.service;

import com.sunnysuni.admin.product.dto.CreateProductRequest;
import com.sunnysuni.admin.product.dto.ProductOptionRequest;
import com.sunnysuni.admin.product.dto.UpdateProductRequest;
import com.sunnysuni.common.entity.Product;
import com.sunnysuni.common.enums.ProductStatus;
import com.sunnysuni.common.exception.BusinessException;
import com.sunnysuni.common.exception.EntityNotFoundException;
import com.sunnysuni.common.repository.ProductRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
  @DisplayName("상품 등록 시 옵션을 함께 저장합니다.")
  void createProductSavesWithOptions() {
    // Given
    CreateProductRequest request = new CreateProductRequest(
        "맨투맨",
        "설명",
        10000,
        9000,
        true,
        true,
        ProductStatus.ACTIVE,
        List.of(
            new ProductOptionRequest("M", "black", 3, 0),
            new ProductOptionRequest("L", "black", 0, 1000)
        )
    );
    when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    adminProductService.createProduct(request);

    // Then
    ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
    verify(productRepository).save(captor.capture());
    Product saved = captor.getValue();
    assertThat(saved.getName()).isEqualTo("맨투맨");
    assertThat(saved.getOptions()).hasSize(2);
    assertThat(saved.getStatus()).isEqualTo(ProductStatus.ACTIVE);
  }

  @Test
  @DisplayName("상품 등록 시 모든 옵션 재고가 0이면 상태를 SOLD_OUT으로 처리합니다.")
  void createProductMarksSoldOutWhenAllOptionStockZero() {
    // Given
    CreateProductRequest request = new CreateProductRequest(
        "품절 상품",
        "설명",
        10000,
        null,
        false,
        false,
        ProductStatus.ACTIVE,
        List.of(
            new ProductOptionRequest("M", "ivory", 0, 0),
            new ProductOptionRequest("L", "ivory", 0, 0)
        )
    );
    when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    adminProductService.createProduct(request);

    // Then
    ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
    verify(productRepository).save(captor.capture());
    assertThat(captor.getValue().getStatus()).isEqualTo(ProductStatus.SOLD_OUT);
  }

  @Test
  @DisplayName("상품 등록 시 숨김 상태 요청은 재고와 무관하게 HIDDEN을 유지합니다.")
  void createProductKeepsHiddenWhenRequestedHidden() {
    // Given
    CreateProductRequest request = new CreateProductRequest(
        "숨김 상품",
        "설명",
        10000,
        null,
        false,
        false,
        ProductStatus.HIDDEN,
        List.of(new ProductOptionRequest("FREE", "black", 5, 0))
    );
    when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    adminProductService.createProduct(request);

    // Then
    ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
    verify(productRepository).save(captor.capture());
    assertThat(captor.getValue().getStatus()).isEqualTo(ProductStatus.HIDDEN);
  }

  @Test
  @DisplayName("상품 등록 시 옵션이 없으면 요청 상태를 그대로 사용합니다.")
  void createProductKeepsRequestedStatusWhenOptionsEmpty() {
    // Given
    CreateProductRequest request = new CreateProductRequest(
        "상태 유지 상품",
        "설명",
        10000,
        null,
        false,
        false,
        ProductStatus.SOLD_OUT,
        List.of()
    );
    when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    adminProductService.createProduct(request);

    // Then
    ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
    verify(productRepository).save(captor.capture());
    assertThat(captor.getValue().getStatus()).isEqualTo(ProductStatus.SOLD_OUT);
  }

  @Test
  @DisplayName("상품 수정 시 옵션과 상태가 갱신됩니다.")
  void updateProductUpdatesOptionsAndStatus() {
    // Given
    Product product = Product.create("기존", "기존설명", 10000, null, false, false, ProductStatus.ACTIVE);
    UpdateProductRequest request = new UpdateProductRequest(
        "수정",
        "수정설명",
        12000,
        11000,
        true,
        true,
        ProductStatus.ACTIVE,
        List.of(new ProductOptionRequest("FREE", "white", 0, 0))
    );
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    // When
    adminProductService.updateProduct(1L, request);

    // Then
    assertThat(product.getName()).isEqualTo("수정");
    assertThat(product.getSalePrice()).isEqualTo(11000);
    assertThat(product.getOptions()).hasSize(1);
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
        ProductStatus.ACTIVE,
        List.of()
    );
    when(productRepository.findById(99L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> adminProductService.updateProduct(99L, request))
        .isInstanceOf(EntityNotFoundException.class);
    verify(productRepository, never()).save(any(Product.class));
  }

  @Test
  @DisplayName("옵션 목록에 null 항목이 있으면 BusinessException을 발생시킵니다.")
  void createProductThrowsWhenOptionsContainNull() {
    // Given
    CreateProductRequest request = new CreateProductRequest(
        "테스트",
        "설명",
        10000,
        null,
        false,
        false,
        ProductStatus.ACTIVE,
        Arrays.asList((ProductOptionRequest) null)
    );

    // When & Then
    assertThatThrownBy(() -> adminProductService.createProduct(request))
        .isInstanceOf(BusinessException.class)
        .hasMessage("옵션 항목은 null일 수 없습니다.");
  }

  @Test
  @DisplayName("상품 목록 조회는 옵션을 함께 로딩하는 전용 메서드를 사용합니다.")
  void getProductsUsesDedicatedMethodWithOptions() {
    // Given
    when(productRepository.findAllByOrderByIdDesc()).thenReturn(List.of());

    // When
    adminProductService.getProducts();

    // Then
    verify(productRepository).findAllByOrderByIdDesc();
  }
}
