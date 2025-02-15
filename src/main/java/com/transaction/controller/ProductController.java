package com.transaction.controller;

import com.transaction.common.BaseResponse;
import com.transaction.common.ResultCode;
import com.transaction.dto.ProductRequest;
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

    @PostMapping
    public BaseResponse<Product> createProduct(@RequestBody ProductRequest request) {
        logger.info("Received request to create product: {}", request.getProductName());

        try {
            Product product = new Product();
            product.setProductName(request.getProductName());
            product.setProductDesc(request.getProductDesc());
            product.setMerchantUuid(request.getMerchantUuid());

            List<ProductSku> productSkus = request.getProductSkus();

            Product savedProduct = productService.createProductWithSkus(product, productSkus);
            return BaseResponse.success(savedProduct);
        } catch (Exception e) {
            logger.error("Error creating product: ", e);
            return BaseResponse.error(ResultCode.INVALID_REQUEST);
        }
    }
}
