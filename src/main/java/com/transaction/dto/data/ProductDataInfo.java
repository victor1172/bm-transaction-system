package com.transaction.dto.data;

import java.util.UUID;

public interface ProductDataInfo {
    Integer getProductId();
    String getProductName();
    String getProductDesc();
    UUID getMerchantUuid();
}
