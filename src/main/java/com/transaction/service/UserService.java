package com.transaction.service;

import com.transaction.dto.UserRequest;
import com.transaction.entity.ClientUser;
import com.transaction.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<ClientUser> getAllUsers() {
        logger.info("Fetching all users...");
        return userRepository.findAll();
    }

    public Optional<ClientUser> getUserById(UUID userUuid) {
        logger.info("Fetching user with ID: {}", userUuid);
        return userRepository.findById(userUuid);
    }

    public Optional<ClientUser> getUserByEmail(String email) {
        logger.info("Fetching user with email: {}", email);
        return userRepository.findByUserEmail(email);
    }

    public ClientUser createUser(UserRequest request) {
        logger.info("Creating new user: {}", request.getUserEmail());
        ClientUser user = new ClientUser();
        user.setUserName(request.getUserName());
        user.setUserEmail(request.getUserEmail());
        user.setUserPassword(request.getUserPassword());
        return userRepository.save(user);
    }

    public void deleteUser(UUID userUuid) {
        logger.info("Deleting user with ID: {}", userUuid);
        userRepository.deleteById(userUuid);
    }
}
