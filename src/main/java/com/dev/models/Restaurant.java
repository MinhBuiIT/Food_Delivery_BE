package com.dev.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "cuisineType")
})
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;


    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "owner_id")
    User owner;

    String name;
    String description;

    String cuisineType;
    String openHours;

    Long likes = 0L;

    @ElementCollection
    Set<String> images = new HashSet<>();

    boolean isOpen;

    @Embedded
    ContactInfo contactInfo;

    Date createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Order> orders = new HashSet<>();

    @JoinColumn(name = "address_id")
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    Address address;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    Set<Food> foods = new HashSet<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    Set<CategoryFood> categoryFoods = new HashSet<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    Set<CategoryIngredient> categoryIngredients = new HashSet<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Event> events = new HashSet<>();

    boolean disable;

    public void addCategoryFood(CategoryFood categoryFood) {
        this.categoryFoods.add(categoryFood);
        categoryFood.setRestaurant(this);
    }

    public void addCategoryIngredient(CategoryIngredient categoryIngredient) {
        this.categoryIngredients.add(categoryIngredient);
        categoryIngredient.setRestaurant(this);
    }

    public void addEvent(Event event) {
        this.events.add(event);
        event.setRestaurant(this);
    }
}
