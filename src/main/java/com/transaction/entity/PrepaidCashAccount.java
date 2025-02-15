package com.transaction.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "prepaid_cash_account")
public class PrepaidCashAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 使用自增 Integer
    private Integer accountId;

    @Column(nullable = false, unique = true)
    private UUID ownerId; // 使用者或商家 UUID

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    private String accountType; // "USER" or "MERCHANT"

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public boolean withdraw(BigDecimal amount) {
        if (this.balance.compareTo(amount) >= 0) {
            this.balance = this.balance.subtract(amount);
            return true;
        }
        return false;
    }
}
