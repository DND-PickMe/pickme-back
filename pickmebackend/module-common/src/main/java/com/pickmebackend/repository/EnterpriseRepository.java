package com.pickmebackend.repository;

import com.pickmebackend.domain.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {
    Optional<Enterprise> findByEmail(String email);
}
