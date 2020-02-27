package com.pickmebackend.repository;

import com.pickmebackend.domain.Technology;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechnologyRepository extends JpaRepository<Technology, Long> {
}
