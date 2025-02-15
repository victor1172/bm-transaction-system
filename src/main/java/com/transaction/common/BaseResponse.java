package com.transaction.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BaseResponse<T> {
    private Meta meta;
    private T data;

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(new Meta(ResultCode.SUCCESS), data);
    }

    public static <T> BaseResponse<T> error(ResultCode resultCode) {
        return new BaseResponse<>(new Meta(resultCode), null);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Meta {
        private String code;
        private String desc;

        public Meta(ResultCode resultCode) {
            this.code = resultCode.getCode();
            this.desc = resultCode.getDesc();
        }
    }
}
