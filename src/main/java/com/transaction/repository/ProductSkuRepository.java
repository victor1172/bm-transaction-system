package com.transaction.repository;

import com.transaction.entity.ProductSku;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSkuRepository extends JpaRepository<ProductSku, Integer> {
}
