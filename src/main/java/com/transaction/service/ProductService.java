package com.transaction.service;

import com.transaction.common.ResultCode;
import com.transaction.dto.ProductResponse;
import com.transaction.entity.Merchant;
import com.transaction.entity.Product;
import com.transaction.entity.ProductSku;
import com.transaction.repository.MerchantRepository;
import com.transaction.repository.ProductRepository;
import com.transaction.repository.ProductSkuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ProductSkuRepository productSkuRepository;
    private final MerchantRepository merchantRepository;

    public ProductService(ProductRepository productRepository, ProductSkuRepository productSkuRepository, MerchantRepository merchantRepository) {
        this.productRepository = productRepository;
        this.productSkuRepository = productSkuRepository;
        this.merchantRepository = merchantRepository;
    }

    @Transactional
    public ProductResponse createProductWithSkus(Product product, List<ProductSku> productSkus) {
        logger.info("Creating product for merchant UUID: {}", product.getMerchantUuid());

        // 檢查 Merchant 是否存在
        Optional<Merchant> merchant = merchantRepository.findById(product.getMerchantUuid());
        if (merchant.isEmpty()) {
            throw new RuntimeException(ResultCode.INVALID_REQUEST.getDesc() + ": Merchant not found");
        }

        // 儲存 Product
        Product savedProduct = productRepository.save(product);

        // 設定 ProductSku 的關聯，並儲存 Skus
        for (ProductSku sku : productSkus) {
            sku.setProductId(savedProduct.getProductId());
            productSkuRepository.save(sku);
        }
        return new ProductResponse(savedProduct, productSkus);
    }
}
