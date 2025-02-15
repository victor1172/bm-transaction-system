package com.transaction.dto;

import com.transaction.entity.ProductSku;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ProductRequest {
    private String productName;
    private String productDesc;
    private UUID merchantUuid;
    private List<ProductSku> productSkus;
}
