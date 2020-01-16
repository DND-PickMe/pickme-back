package com.pickmebackend.repository;

import com.pickmebackend.domain.SelfInterview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelfInterviewRepository extends JpaRepository<SelfInterview, Long> {
}
