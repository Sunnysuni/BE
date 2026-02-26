package com.sunnysuni.admin.product.controller;

import com.sunnysuni.admin.product.dto.CreateProductRequest;
import com.sunnysuni.admin.product.dto.ProductResponse;
import com.sunnysuni.admin.product.dto.UpdateProductRequest;
import com.sunnysuni.admin.product.service.AdminProductService;
import com.sunnysuni.common.dto.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class AdminProductController {
  private final AdminProductService adminProductService;

  public AdminProductController(AdminProductService adminProductService) {
    this.adminProductService = adminProductService;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts() {
    return ResponseEntity.ok(ApiResponse.success(adminProductService.getProducts()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable("id") Long productId) {
    return ResponseEntity.ok(ApiResponse.success(adminProductService.getProduct(productId)));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
      @Valid @RequestBody CreateProductRequest request
  ) {
    return ResponseEntity.ok(
        ApiResponse.success("상품이 등록되었습니다.", adminProductService.createProduct(request))
    );
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
      @PathVariable("id") Long productId,
      @Valid @RequestBody UpdateProductRequest request
  ) {
    return ResponseEntity.ok(
        ApiResponse.success("상품이 수정되었습니다.", adminProductService.updateProduct(productId, request))
    );
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable("id") Long productId) {
    adminProductService.deleteProduct(productId);
    return ResponseEntity.ok(ApiResponse.success("상품이 삭제되었습니다.", null));
  }
}
