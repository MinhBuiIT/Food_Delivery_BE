package com.dev.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
public class IngredientItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    String name;
    long price;
    boolean isStock;

    @ManyToMany(mappedBy = "ingredients")
    @JsonBackReference
    Set<Food> foods;

    @ManyToMany(mappedBy = "ingredients")
    @JsonIgnore
    Set<OrderItem> orderItems;

    @ManyToMany(mappedBy = "ingredients")
    @JsonIgnore
    Set<CartItem> cartItems;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_ingredient_id")
    @JsonBackReference
    CategoryIngredient categoryIngredient;
}
