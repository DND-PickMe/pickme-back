package com.pickmebackend.repository;

import com.pickmebackend.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepo extends JpaRepository<Event, Long> {
}
