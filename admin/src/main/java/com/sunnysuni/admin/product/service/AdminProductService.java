package com.sunnysuni.admin.product.service;

import com.sunnysuni.admin.product.dto.CreateProductRequest;
import com.sunnysuni.admin.product.dto.ProductOptionRequest;
import com.sunnysuni.admin.product.dto.ProductResponse;
import com.sunnysuni.admin.product.dto.UpdateProductRequest;
import com.sunnysuni.common.entity.Product;
import com.sunnysuni.common.entity.ProductOption;
import com.sunnysuni.common.enums.ProductStatus;
import com.sunnysuni.common.exception.BusinessException;
import com.sunnysuni.common.exception.EntityNotFoundException;
import com.sunnysuni.common.exception.ErrorCode;
import com.sunnysuni.common.repository.ProductRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminProductService {
  private final ProductRepository productRepository;

  public AdminProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public List<ProductResponse> getProducts() {
    return productRepository.findAllByOrderByIdDesc()
        .stream()
        .map(ProductResponse::from)
        .toList();
  }

  public ProductResponse getProduct(Long productId) {
    return ProductResponse.from(findWithOptionsById(productId));
  }

  @Transactional
  public ProductResponse createProduct(CreateProductRequest request) {
    validateNoNullOptions(request.options());
    ProductStatus status = resolveStatusByStock(request.status(), request.options());
    Product product = Product.create(
        request.name(),
        request.description(),
        request.price(),
        request.salePrice(),
        request.isNew(),
        request.isSale(),
        status
    );
    product.replaceOptions(toProductOptions(request.options()));

    return ProductResponse.from(productRepository.save(product));
  }

  @Transactional
  public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {
    Product product = findById(productId);
    validateNoNullOptions(request.options());
    ProductStatus status = resolveStatusByStock(request.status(), request.options());
    product.update(
        request.name(),
        request.description(),
        request.price(),
        request.salePrice(),
        request.isNew(),
        request.isSale(),
        status
    );
    product.replaceOptions(toProductOptions(request.options()));
    return ProductResponse.from(product);
  }

  @Transactional
  public void deleteProduct(Long productId) {
    Product product = findById(productId);
    productRepository.delete(product);
  }

  private Product findById(Long productId) {
    return productRepository.findById(productId)
        .orElseThrow(() -> new EntityNotFoundException("상품", productId));
  }

  private Product findWithOptionsById(Long productId) {
    return productRepository.findWithOptionsById(productId)
        .orElseThrow(() -> new EntityNotFoundException("상품", productId));
  }

  private List<ProductOption> toProductOptions(List<ProductOptionRequest> options) {
    if (options == null || options.isEmpty()) {
      return List.of();
    }
    return options.stream()
        .map(option -> ProductOption.create(
            option.size(),
            option.color(),
            option.stock(),
            option.additionalPrice()
        ))
        .toList();
  }

  private ProductStatus resolveStatusByStock(ProductStatus requestedStatus, List<ProductOptionRequest> options) {
    if (requestedStatus == ProductStatus.HIDDEN) {
      return ProductStatus.HIDDEN;
    }
    if (options == null || options.isEmpty()) {
      return requestedStatus;
    }

    boolean allSoldOut = options.stream().allMatch(option -> option.stock() <= 0);
    return allSoldOut ? ProductStatus.SOLD_OUT : ProductStatus.ACTIVE;
  }

  private void validateNoNullOptions(List<ProductOptionRequest> options) {
    if (options == null) {
      return;
    }
    if (options.stream().anyMatch(Objects::isNull)) {
      throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "옵션 항목은 null일 수 없습니다.");
    }
  }
}
