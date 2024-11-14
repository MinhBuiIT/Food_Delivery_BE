package com.dev.mapper;

import com.dev.dto.request.CreateAddressRequest;
import com.dev.dto.response.AddressResponse;
import com.dev.models.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toAddress(CreateAddressRequest createAddressRequest);

    AddressResponse toAddressResponse(Address address);
}
