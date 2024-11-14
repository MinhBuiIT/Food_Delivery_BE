package com.dev.models;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {

    private String title;


    private Set<String> imagesLiked;

    private String description;

    private Long id;
}
