package com.transaction.service;

import com.transaction.entity.ClientUser;
import com.transaction.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserById() {
        UUID userId = UUID.randomUUID();
        ClientUser user = new ClientUser();
        user.setUserUuid(userId);
        user.setUserName("Test User");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<ClientUser> result = userService.getUserById(userId);

        assertTrue(result.isPresent());
        assertEquals("Test User", result.get().getUserName());
        verify(userRepository, times(1)).findById(userId);
    }
}
