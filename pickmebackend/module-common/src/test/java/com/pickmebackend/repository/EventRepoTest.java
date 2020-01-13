package com.pickmebackend.repository;

import com.pickmebackend.domain.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class EventRepoTest {

    @Autowired
    EventRepo eventRepo;

    @Test
    void eventRepoTest() {
        eventRepo.save(Event.builder().content("콘텐츠").build());
        Optional<Event> savedEvent = eventRepo.findById(1L);
        Event newEvent = savedEvent.orElseGet(() -> fail("저장된 객체가 없습니다."));

        assertEquals(newEvent.getContent(), "콘텐츠");
    }
}