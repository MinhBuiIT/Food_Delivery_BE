package com.dev.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@Table(indexes = @Index(columnList = "refreshToken"))
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "refresh_token")
    String refreshToken;

    @OneToOne
    @JoinColumn(name = "user_id")
    User user;
}
