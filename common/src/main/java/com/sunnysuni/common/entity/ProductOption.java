package com.sunnysuni.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "product_option")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(nullable = false, length = 20)
  private String size;

  @Column(nullable = false, length = 50)
  private String color;

  @Column(nullable = false)
  private Integer stock;

  @Column(name = "additional_price", nullable = false)
  private Integer additionalPrice;

  private ProductOption(String size, String color, Integer stock, Integer additionalPrice) {
    this.size = size;
    this.color = color;
    this.stock = stock;
    this.additionalPrice = additionalPrice;
  }

  public static ProductOption create(String size, String color, Integer stock, Integer additionalPrice) {
    return new ProductOption(size, color, stock, additionalPrice);
  }

  void assignProduct(Product product) {
    this.product = product;
  }
}
