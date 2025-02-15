package com.transaction.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    private String userName;
    private String userEmail;
    private String userPassword;
}
