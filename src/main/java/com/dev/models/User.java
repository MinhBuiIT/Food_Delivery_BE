package com.dev.models;

import com.dev.enums.RoleEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(indexes = @Index(columnList = "email"))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String fullName;
    String password;
    String email;

    RoleEnum role;

    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL,orphanRemoval = true)
    Set<Address> addresses = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL,orphanRemoval = true)
    Set<Order> orders = new HashSet<>();


    @ElementCollection
    Set<RestaurantDto> favorites = new HashSet<>();

    public void removeFavorite(Long id) {
        this.favorites.removeIf(favorite -> favorite.getId().equals(id));
    }

    public void addAddress(Address address) {
        this.addresses.add(address);
        address.setCustomer(this);
    }

    public void removeAddress(Address address) {
        this.addresses.remove(address);
        address.setCustomer(null);
    }
}
