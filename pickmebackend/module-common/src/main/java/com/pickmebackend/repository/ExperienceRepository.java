package com.pickmebackend.repository;

import com.pickmebackend.domain.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
}
