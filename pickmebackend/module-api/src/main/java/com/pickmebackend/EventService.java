package com.pickmebackend;

import com.pickmebackend.domain.Event;
import com.pickmebackend.repository.EventRepo;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    private EventRepo eventRepo;

    public EventService(EventRepo eventRepo) {
        this.eventRepo = eventRepo;
    }

    Event saveContent(Event event) {
        return eventRepo.save(event);
    }
}
