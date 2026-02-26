package com.sunnysuni.store.product.service;

import com.sunnysuni.common.entity.Product;
import com.sunnysuni.common.enums.ProductStatus;
import com.sunnysuni.common.exception.EntityNotFoundException;
import com.sunnysuni.common.repository.ProductRepository;
import com.sunnysuni.store.product.dto.ProductDetailResponse;
import com.sunnysuni.store.product.dto.ProductSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {
  private final ProductRepository productRepository;

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public Page<ProductSummaryResponse> getProducts(Pageable pageable) {
    return toSummaryPage(productRepository.findByStatus(ProductStatus.ACTIVE, pageable));
  }

  public ProductDetailResponse getProduct(Long productId) {
    return ProductDetailResponse.from(findActiveProductById(productId));
  }

  public Page<ProductSummaryResponse> getNewProducts(Pageable pageable) {
    return toSummaryPage(productRepository.findByIsNewTrueAndStatus(ProductStatus.ACTIVE, pageable));
  }

  public Page<ProductSummaryResponse> getSaleProducts(Pageable pageable) {
    return toSummaryPage(productRepository.findByIsSaleTrueAndStatus(ProductStatus.ACTIVE, pageable));
  }

  public Page<ProductSummaryResponse> searchProducts(String keyword, Pageable pageable) {
    return toSummaryPage(
        productRepository.findByNameContainingIgnoreCaseAndStatus(keyword, ProductStatus.ACTIVE, pageable)
    );
  }

  private Product findActiveProductById(Long productId) {
    return productRepository.findById(productId)
        .filter(found -> found.getStatus() == ProductStatus.ACTIVE)
        .orElseThrow(() -> new EntityNotFoundException("상품", productId));
  }

  private Page<ProductSummaryResponse> toSummaryPage(Page<Product> products) {
    return products.map(ProductSummaryResponse::from);
  }
}
