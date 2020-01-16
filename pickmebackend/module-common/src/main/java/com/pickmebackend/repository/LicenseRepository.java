package com.pickmebackend.repository;

import com.pickmebackend.domain.License;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicenseRepository extends JpaRepository<License, Long> {
}
