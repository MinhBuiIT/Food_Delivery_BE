package com.dev.models;

import com.dev.enums.EventTypeEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    private String code;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private EventTypeEnum type;
    private Integer percent;
    private Long amount;
    private Boolean allFood;
    private boolean active;
    private Date createdAt;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Food> foods = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    @JsonBackReference
    Restaurant restaurant;

    public void addFood(Food food) {
        foods.add(food);
        food.setEvent(this);
    }
}
