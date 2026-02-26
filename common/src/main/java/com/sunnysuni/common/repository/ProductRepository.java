package com.sunnysuni.common.repository;

import com.sunnysuni.common.entity.Product;
import com.sunnysuni.common.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * TODO(상품-카테고리 연동):
 * - [ ] 상품 등록/수정 DTO에 categoryId 필드를 반영합니다.
 * - [ ] Service 계층에서 categoryId 존재 여부를 검증한 뒤 Product.category를 매핑합니다.
 * - [ ] 상품 목록/상세 응답 DTO에 카테고리 요약 정보(id, name)를 포함합니다.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
  Page<Product> findByStatus(ProductStatus status, Pageable pageable);

  Page<Product> findByIsNewTrueAndStatus(ProductStatus status, Pageable pageable);

  Page<Product> findByIsSaleTrueAndStatus(ProductStatus status, Pageable pageable);

  Page<Product> findByNameContainingIgnoreCaseAndStatus(
      String keyword,
      ProductStatus status,
      Pageable pageable
  );
}
