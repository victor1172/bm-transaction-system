package com.transaction.dto;

import com.transaction.entity.Product;
import com.transaction.entity.ProductSku;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class ProductResponse {
    private Integer productId;
    private String productName;
    private String productDesc;
    private UUID merchantUuid;
    private List<ProductSku> productSkus = new ArrayList<>();

    public ProductResponse(Product product, List<ProductSku> productSkus) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.productDesc = product.getProductDesc();
        this.merchantUuid = product.getMerchantUuid();
        this.productSkus =  productSkus;
    }

}
