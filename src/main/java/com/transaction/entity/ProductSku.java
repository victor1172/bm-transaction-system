package com.transaction.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "product_sku")
public class ProductSku extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productSkuId;

    @Column(nullable = false)
    private Integer productId;

    @Column(nullable = false, length = 255)
    private String productSkuName;

    @Column(nullable = false)
    private Integer qty;

    @Column(nullable = false)
    private BigDecimal productSkuPrice;

    @Column(nullable = false)
    private BigDecimal productSkuCost;
}
