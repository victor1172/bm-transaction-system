package com.transaction.dto.data;

import java.math.BigDecimal;

public interface ProductSkuDataInfo {
    Integer getProductSkuId();
    String getProductSkuName();
    BigDecimal getProductSkuPrice();
    BigDecimal getProductSkuCost();
}
