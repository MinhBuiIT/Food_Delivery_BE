package com.dev.dto.response;

import com.dev.models.Address;
import com.dev.models.ContactInfo;
import com.dev.models.Order;
import com.dev.models.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantResponse{
    Long id;
    String name;
    String description;
    String cuisineType;
    Address address;
    String owner;
    ContactInfo contactInfo;
    String openHours;
    Set<String> images;
    Date createdAt;
    Boolean open;
    Long likes;
    Boolean isLikeUser;
}

