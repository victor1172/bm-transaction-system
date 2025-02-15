package com.transaction.repository;

import com.transaction.entity.ProductSku;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProductSkuRepository extends JpaRepository<ProductSku, UUID> {
}
