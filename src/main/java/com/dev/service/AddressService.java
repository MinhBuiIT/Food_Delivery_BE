package com.dev.service;


import com.dev.dto.request.CreateAddressRequest;
import com.dev.dto.response.AddressResponse;
import com.dev.enums.ErrorEnum;
import com.dev.exception.AppException;
import com.dev.mapper.AddressMapper;
import com.dev.models.Address;
import com.dev.models.User;
import com.dev.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AddressService {

    AddressMapper addressMapper;
    UserRepository userRepository;

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public AddressResponse createAddress(CreateAddressRequest request) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmailWithAddress(email)
                .orElseThrow(() -> new AppException(ErrorEnum.NOT_FOUND_USER));

        //Kiểm tra address đã tồn tại chưa
        for (Address address1: user.getAddresses()) {
            if(address1.getCity().equals(request.city()) &&
               address1.getDistrict().equals(request.district()) &&
               address1.getWard().equals(request.ward()) &&
               address1.getNumberStreet().equals(request.numberStreet())) {
                   throw new AppException(ErrorEnum.ADDRESS_EXIST);
            }
        }
        //Bỏ default các address còn lại
        for (Address address1: user.getAddresses()) {
            address1.setCustomerDefault(false);
        }


        Address address = addressMapper.toAddress(request);
        address.setCustomerPhone(request.phone());
        address.setCustomerDefault(true);
        user.addAddress(address);
        userRepository.save(user);
        AddressResponse addressResponse = addressMapper.toAddressResponse(address);
        addressResponse.setPhone(request.phone());
        return addressResponse;
    }

    @PreAuthorize("hasRole('USER')")
    public List<AddressResponse> getAllAddresses() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmailWithAddress(email)
                .orElseThrow(() -> new AppException(ErrorEnum.NOT_FOUND_USER));

        List<AddressResponse> addresses = new ArrayList<>();
        for (Address address : user.getAddresses()) {
            AddressResponse addressResponse = addressMapper.toAddressResponse(address);
            addressResponse.setPhone(address.getCustomerPhone());
            addressResponse.setAddressDefault(address.getCustomerDefault());
            addresses.add(addressResponse);

        }
        return addresses;
    }

    @PreAuthorize("hasRole('USER')")
    public AddressResponse getAddressDefault() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmailWithAddress(email)
                .orElseThrow(() -> new AppException(ErrorEnum.NOT_FOUND_USER));

        Set<Address> addressSet = user.getAddresses();
        for (Address address : addressSet) {
            if(address.getCustomerDefault()) {
                AddressResponse addressResponse = addressMapper.toAddressResponse(address);
                addressResponse.setPhone(address.getCustomerPhone());
                addressResponse.setAddressDefault(address.getCustomerDefault());
                return addressResponse;
            }
        }
        return null;
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public void setAddressDefault(Long id) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmailWithAddress(email)
                .orElseThrow(() -> new AppException(ErrorEnum.NOT_FOUND_USER));
        Optional<Address> foundAddress = user.getAddresses().stream().filter(address -> Objects.equals(address.getId(), id)).findFirst();
        if(foundAddress.isEmpty()) {
            throw new AppException(ErrorEnum.ADDRESS_NOT_FOUND);
        }
        user.getAddresses().forEach(address -> {
            address.setCustomerDefault(false);
        });
        userRepository.save(user);

        Address address = foundAddress.get();
        address.setCustomerDefault(true);

    }


}
