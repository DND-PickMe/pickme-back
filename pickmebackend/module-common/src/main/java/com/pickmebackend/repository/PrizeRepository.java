package com.pickmebackend.repository;

import com.pickmebackend.domain.Prize;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrizeRepository extends JpaRepository<Prize, Long> {
}
