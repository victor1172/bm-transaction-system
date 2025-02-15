package com.transaction.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "merchant")
public class Merchant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID merchantUuid;

    @Column(nullable = false, length = 100)
    private String merchantName;

    @Column(nullable = false, unique = true, length = 255)
    private String merchantEmail;

    @Column(nullable = false)
    private String merchantPassword;
}
