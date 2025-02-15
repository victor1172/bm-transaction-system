package com.transaction.common;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS("0000", "Success"),
    SYSTEM_ERROR("9999", "System Error"),
    USER_NOT_FOUND("1001", "User not found"),
    INVALID_REQUEST("1002", "Invalid request data");

    private final String code;
    private final String desc;

    ResultCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
