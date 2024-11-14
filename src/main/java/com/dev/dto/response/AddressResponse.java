package com.dev.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse {
    Long id;

    String numberStreet;

    String street;

    String ward;

    String district;

    String city;

    String postalCode;

    String phone;

    Boolean addressDefault;
}
