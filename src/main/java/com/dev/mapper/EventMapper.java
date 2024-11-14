package com.dev.mapper;

import com.dev.dto.request.CreateAddressRequest;
import com.dev.dto.response.AddressResponse;
import com.dev.dto.response.EventResponse;
import com.dev.dto.response.EventResponseExtend;
import com.dev.models.Address;
import com.dev.models.Event;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventResponse toEventResponse(Event event);
    EventResponseExtend toEventResponseExtend(Event event);
}
