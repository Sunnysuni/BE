package com.sunnysuni.admin.product.service;

import com.sunnysuni.admin.product.dto.CreateProductRequest;
import com.sunnysuni.admin.product.dto.ProductResponse;
import com.sunnysuni.admin.product.dto.UpdateProductRequest;
import com.sunnysuni.common.entity.Product;
import com.sunnysuni.common.exception.EntityNotFoundException;
import com.sunnysuni.common.repository.ProductRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
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
    return productRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
        .stream()
        .map(ProductResponse::from)
        .toList();
  }

  public ProductResponse getProduct(Long productId) {
    return ProductResponse.from(findById(productId));
  }

  @Transactional
  public ProductResponse createProduct(CreateProductRequest request) {
    Product product = Product.create(
        request.name(),
        request.description(),
        request.price(),
        request.salePrice(),
        request.isNew(),
        request.isSale(),
        request.status()
    );

    return ProductResponse.from(productRepository.save(product));
  }

  @Transactional
  public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {
    Product product = findById(productId);
    product.update(
        request.name(),
        request.description(),
        request.price(),
        request.salePrice(),
        request.isNew(),
        request.isSale(),
        request.status()
    );
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
}
