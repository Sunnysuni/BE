package com.sunnysuni.store.product.controller;

import com.sunnysuni.common.dto.ApiResponse;
import com.sunnysuni.store.product.dto.ProductDetailResponse;
import com.sunnysuni.store.product.dto.ProductSummaryResponse;
import com.sunnysuni.store.product.service.ProductService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<Page<ProductSummaryResponse>>> getProducts(
      @PageableDefault(size = 20) Pageable pageable
  ) {
    return ResponseEntity.ok(ApiResponse.success(productService.getProducts(pageable)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductDetailResponse>> getProduct(@PathVariable("id") Long productId) {
    return ResponseEntity.ok(ApiResponse.success(productService.getProduct(productId)));
  }

  @GetMapping("/new")
  public ResponseEntity<ApiResponse<Page<ProductSummaryResponse>>> getNewProducts(
      @PageableDefault(size = 20) Pageable pageable
  ) {
    return ResponseEntity.ok(ApiResponse.success(productService.getNewProducts(pageable)));
  }

  @GetMapping("/sale")
  public ResponseEntity<ApiResponse<Page<ProductSummaryResponse>>> getSaleProducts(
      @PageableDefault(size = 20) Pageable pageable
  ) {
    return ResponseEntity.ok(ApiResponse.success(productService.getSaleProducts(pageable)));
  }

  @GetMapping("/search")
  public ResponseEntity<ApiResponse<Page<ProductSummaryResponse>>> searchProducts(
      @RequestParam("keyword") @NotBlank(message = "검색어는 필수입니다.") String keyword,
      @PageableDefault(size = 20) Pageable pageable
  ) {
    return ResponseEntity.ok(ApiResponse.success(productService.searchProducts(keyword, pageable)));
  }
}
