package com.transaction.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MerchantRequest {
    private String merchantName;
    private String merchantEmail;
    private String merchantPassword;
}
