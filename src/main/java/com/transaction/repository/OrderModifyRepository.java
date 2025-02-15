package com.transaction.repository;

import com.transaction.entity.OrderModify;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderModifyRepository extends JpaRepository<OrderModify, Integer> {
}
