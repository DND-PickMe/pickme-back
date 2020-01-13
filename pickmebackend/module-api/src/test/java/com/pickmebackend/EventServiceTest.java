package com.pickmebackend;

import com.pickmebackend.domain.Event;
import com.pickmebackend.repository.EventRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class EventServiceTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepo eventRepo;

    @Test
    void eventServiceTest() {
        Event event = new Event();
        event.setContent("콘텐츠");
        eventService.saveContent(event);
        Optional<Event> testEvent = eventRepo.findById(1L);
        Event newEvent = testEvent.orElseGet(() -> fail("저장된 이벤트 객체가 없습니다."));

        assertEquals(newEvent.getContent(), "콘텐츠");
    }
}