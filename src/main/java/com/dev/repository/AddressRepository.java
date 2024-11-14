package com.dev.repository;

import com.dev.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Boolean existsByCityAndDistrictAndWardAndStreetAndNumberStreet(String city,String district,String ward,String street,String numberStreet);
}
