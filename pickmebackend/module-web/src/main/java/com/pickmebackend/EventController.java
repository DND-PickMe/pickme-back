package com.pickmebackend;

import com.pickmebackend.domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/event")
    public Event content() {
        Event hello = Event.builder().content("hello").build();
        return eventService.saveContent(hello);
    }
}
