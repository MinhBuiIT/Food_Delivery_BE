package com.dev.controller;

import com.dev.core.ResponseSuccess;
import com.dev.dto.request.CreateAddressRequest;
import com.dev.dto.request.EventChangeActive;
import com.dev.dto.request.EventRequest;
import com.dev.service.AddressService;
import com.dev.service.EventService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventController {

    EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseSuccess createEvent(
            @RequestBody EventRequest request
    ) {
       eventService.createEvent(request);
       return ResponseSuccess.builder()
               .message("Create Event successful")
               .code(HttpStatus.CREATED.value())
               .build();
    }

    @PostMapping("/change-active")
    public ResponseSuccess changeActiveEvent(
            @RequestBody EventChangeActive request
    ) {
        eventService.changeActiveEvent(request.getId());
        return ResponseSuccess.builder()
                .message("Change active event successful")
                .code(HttpStatus.CREATED.value())
                .build();
    }

    @GetMapping("list")
    public ResponseSuccess getEvents(
            @RequestParam(name = "active",defaultValue = "-1") Integer active
    ) {
        var result = eventService.getEventList(active);
        return ResponseSuccess.builder()
                .message("Change active event successful")
                .code(HttpStatus.CREATED.value())
                .metadata(result)
                .build();
    }
}
