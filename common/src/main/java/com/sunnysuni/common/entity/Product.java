package com.sunnysuni.common.entity;

import com.sunnysuni.common.enums.ProductStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

  // TODO(상품-카테고리 연동): 카테고리 API 완성 후 category 연관관계를 추가합니다.

  @Column(nullable = false, length = 255)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private Integer price;

  @Column(name = "sale_price")
  private Integer salePrice;

  @Column(name = "is_new", nullable = false)
  private Boolean isNew;

  @Column(name = "is_sale", nullable = false)
  private Boolean isSale;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ProductStatus status;

  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<ProductOption> options = new ArrayList<>();

  private Product(
      String name,
      String description,
      Integer price,
      Integer salePrice,
      Boolean isNew,
      Boolean isSale,
      ProductStatus status
  ) {
    this.name = name;
    this.description = description;
    this.price = price;
    this.salePrice = salePrice;
    this.isNew = isNew;
    this.isSale = isSale;
    this.status = status;
  }

  public static Product create(
      String name,
      String description,
      Integer price,
      Integer salePrice,
      Boolean isNew,
      Boolean isSale,
      ProductStatus status
  ) {
    return new Product(name, description, price, salePrice, isNew, isSale, status);
  }

  public void update(
      String name,
      String description,
      Integer price,
      Integer salePrice,
      Boolean isNew,
      Boolean isSale,
      ProductStatus status
  ) {
    this.name = name;
    this.description = description;
    this.price = price;
    this.salePrice = salePrice;
    this.isNew = isNew;
    this.isSale = isSale;
    this.status = status;
  }

  public void replaceOptions(List<ProductOption> productOptions) {
    this.options.clear();
    if (productOptions == null || productOptions.isEmpty()) {
      return;
    }
    productOptions.forEach(this::addOption);
  }

  public void addOption(ProductOption option) {
    option.assignProduct(this);
    this.options.add(option);
  }
}
