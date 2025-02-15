package com.transaction.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
public class ClientUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID userUuid;

    @Column(nullable = false, length = 100)
    private String userName;

    @Column(nullable = false, unique = true, length = 255)
    private String userEmail;

    @Column(nullable = false)
    private String userPassword;

    @Column(nullable = true)
    private Integer prepaidCashAccountId;
}
