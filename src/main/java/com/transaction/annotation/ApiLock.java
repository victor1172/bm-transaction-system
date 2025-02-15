package com.transaction.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiLock {
    String moduleName(); // 鎖的模組名稱
    String lockName();   // 鎖的 Key 來源，支援 SpEL 表達式
    long tryLockTime() default 0; // 嘗試加鎖時間，0 表示立即返回
}
