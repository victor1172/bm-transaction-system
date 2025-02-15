package com.transaction.dto;

import com.transaction.entity.ProductSku;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
public class ProductRequest {

    private String productName;
    private String productDesc;
    private UUID merchantUuid;
    private List<ProductSku> productSkus;
}
