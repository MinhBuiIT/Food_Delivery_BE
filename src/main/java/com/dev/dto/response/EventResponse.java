package com.dev.dto.response;

import com.dev.enums.EventTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventResponse {
    Long id;
    String code;
    EventTypeEnum type;
    Integer percent;
    Long amount;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Date createdAt;
}
