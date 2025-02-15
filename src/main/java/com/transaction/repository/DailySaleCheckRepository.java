package com.transaction.repository;

import com.transaction.entity.DailySaleCheck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailySaleCheckRepository extends JpaRepository<DailySaleCheck, Integer> {
}
