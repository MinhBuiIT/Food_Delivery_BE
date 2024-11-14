package com.dev.dto.request;


import com.dev.enums.EventTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class EventRequest {
    LocalDateTime startTime;
    LocalDateTime endTime;
    EventTypeEnum eventType;
    Long value;
    Boolean allFood;
    Set<Long> foods;
}
