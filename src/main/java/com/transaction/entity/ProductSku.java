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
public class ProductSku {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID productSkuId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 255)
    private String productSkuName;

    @Column(nullable = false)
    private BigDecimal productSkuPrice;

    @Column(nullable = false)
    private BigDecimal productSkuCost;
}
