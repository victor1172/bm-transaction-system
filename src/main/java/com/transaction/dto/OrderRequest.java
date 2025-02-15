package com.transaction.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrderRequest {
    private UUID userId;
    private Integer productId;
    private Integer productSkuId;
    private Integer qty;
}
