package com.dev.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class CategoryFood {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    //@Column(unique = true)
    String name;

    @OneToMany(mappedBy = "categoryFood",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    Set<Food> foods = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    @JsonBackReference
    Restaurant restaurant;
}
