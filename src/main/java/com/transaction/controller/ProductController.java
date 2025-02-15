package com.transaction.controller;

import com.transaction.annotation.ApiLock;
import com.transaction.common.ApiLockModules;
import com.transaction.common.BaseResponse;
import com.transaction.common.ResultCode;
import com.transaction.dto.ProductRequest;
import com.transaction.dto.ProductResponse;
import com.transaction.entity.Product;
import com.transaction.entity.ProductSku;
import com.transaction.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @ApiLock(moduleName = ApiLockModules.CREATE_PRODUCT, lockName = "#request.merchantUuid")
    @PostMapping
    public BaseResponse<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        logger.info("Received request to create product: {}", request.getProductName());

        try {
            Product product = new Product();
            product.setProductName(request.getProductName());
            product.setMerchantUuid(request.getMerchantUuid());
            product.setProductDesc(request.getProductDesc());
            List<ProductSku> productSkus = request.getProductSkus();

            ProductResponse response = productService.createProductWithSkus(product, productSkus);
            return BaseResponse.success(response);
        } catch (Exception e) {
            logger.error("Error creating product: ", e);
            return BaseResponse.error(ResultCode.INVALID_REQUEST);
        }
    }
}
