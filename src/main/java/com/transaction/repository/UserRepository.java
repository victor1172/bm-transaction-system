package com.transaction.repository;

import com.transaction.entity.ClientUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<ClientUser, UUID> {
    Optional<ClientUser> findByUserEmail(String email);
}
