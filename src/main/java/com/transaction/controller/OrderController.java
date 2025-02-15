package com.transaction.controller;

import com.transaction.annotation.ApiLock;
import com.transaction.common.ApiLockModules;
import com.transaction.common.BaseResponse;
import com.transaction.dto.OrderRequest;
import com.transaction.entity.Order;
import com.transaction.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @ApiLock(moduleName = ApiLockModules.CREATE_ORDER, lockName = "#request.userId")
    @PostMapping("/create")
    public BaseResponse<Order> createOrder(@RequestBody OrderRequest request) {
        Order order = orderService.createOrder(request.getUserId(), request.getProductId(), request.getProductSkuId(), request.getQty());
        return BaseResponse.success(order);
    }
}

