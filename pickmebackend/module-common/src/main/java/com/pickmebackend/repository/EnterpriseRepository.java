package com.pickmebackend.repository;

import com.pickmebackend.domain.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {
}
