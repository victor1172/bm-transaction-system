package com.transaction.controller;

import com.transaction.common.BaseResponse;
import com.transaction.common.ResultCode;
import com.transaction.entity.ClientUser;
import com.transaction.service.PrepaidCashAccountService;
import com.transaction.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final PrepaidCashAccountService prepaidCashAccountService;

    public UserController(UserService userService, PrepaidCashAccountService prepaidCashAccountService) {
        this.prepaidCashAccountService = prepaidCashAccountService;
        this.userService = userService;
    }

    @GetMapping
    public BaseResponse<List<ClientUser>> getAllUsers() {
        logger.debug("Fetching all users...");
        List<ClientUser> users = userService.getAllUsers();
        return BaseResponse.success(users);
    }

    @GetMapping("/{email}")
    public BaseResponse<ClientUser> getUserById(@PathVariable String email) {
        logger.debug("Fetching user with email: {}", email);
        Optional<ClientUser> user = userService.getUserByEmail(email);
        return user.map(BaseResponse::success)
                .orElseGet(() -> BaseResponse.error(ResultCode.USER_NOT_FOUND));
    }

    @PostMapping
    public BaseResponse<ClientUser> createUser(@RequestBody ClientUser user) {
        logger.debug("Creating new user: {}", user.getUserEmail());
        return BaseResponse.success(userService.createUser(user));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteUser(@PathVariable UUID id) {
        logger.debug("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        return BaseResponse.success(null);
    }

    @PostMapping("/{userId}/deposit")
    public BaseResponse<String> deposit(@PathVariable UUID userId, @RequestParam BigDecimal amount) {
        logger.info("User {} is depositing: {}", userId, amount);

        if (prepaidCashAccountService.deposit(userId, amount)) {
            return BaseResponse.success("Deposit successful");
        } else {
            return BaseResponse.error(ResultCode.INVALID_REQUEST);
        }
    }
}
