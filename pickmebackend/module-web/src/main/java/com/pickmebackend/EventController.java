package com.pickmebackend;

import com.pickmebackend.domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/event")
    public ResponseEntity<?> content(@RequestBody Event event) {
        return new ResponseEntity<>(eventService.saveContent(event), HttpStatus.CREATED);
    }
}
