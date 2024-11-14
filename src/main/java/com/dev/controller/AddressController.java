package com.dev.controller;

import com.dev.core.ResponseSuccess;
import com.dev.dto.request.CreateAddressRequest;
import com.dev.enums.ErrorEnum;
import com.dev.exception.AppException;
import com.dev.service.AddressService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/address")
public class AddressController {

    AddressService addressService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseSuccess addAddress(
            @Valid @RequestBody CreateAddressRequest request
    ) {
       var result = addressService.createAddress(request);
       return ResponseSuccess.builder()
               .message("Add address for user successful")
               .code(HttpStatus.CREATED.value())
               .metadata(result)
               .build();
    }

    @GetMapping("/me")
    public ResponseSuccess getAddressMe() {
        var result = addressService.getAllAddresses();
        return ResponseSuccess.builder()
                .message("Get addresses successful")
                .code(HttpStatus.OK.value())
                .metadata(result)
                .build();
    }
    @GetMapping("/me/default")
    public ResponseSuccess getAddressMeDefault() {
        var result = addressService.getAddressDefault();

        return ResponseSuccess.builder()
                .message("Get addresses default successful")
                .code(HttpStatus.OK.value())
                .metadata(result)
                .build();
    }

    @PostMapping("/me/default/{id}")
    public ResponseSuccess setAddressDefault(
            @PathVariable Long id
    ) {
        addressService.setAddressDefault(id);
        return ResponseSuccess.builder()
                .message("Set addresses default successful")
                .code(HttpStatus.OK.value())
                .build();
    }


}
