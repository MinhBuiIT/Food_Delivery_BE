package com.dev.dto.response;

import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FoodCategoryResponse {
    Long id;
    String name;
    String price;
    String description;
    Set<String> images;
    Boolean available;
    Integer ingredientsNum;
    EventResponse event;
}
