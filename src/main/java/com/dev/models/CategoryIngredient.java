package com.dev.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
public class CategoryIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    //@Column(unique = true)
    String name;

    @OneToMany(mappedBy = "categoryIngredient", cascade = CascadeType.ALL)
    @JsonManagedReference
    Set<IngredientItem> ingredients = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    @JsonBackReference
    Restaurant restaurant;

    Boolean pick;

    public void addIngredient(IngredientItem ingredient) {
        ingredients.add(ingredient);
        ingredient.setCategoryIngredient(this);
    }

    public void removeIngredient(IngredientItem ingredient) {
        ingredients.remove(ingredient);
        ingredient.setCategoryIngredient(null);
    }
}
