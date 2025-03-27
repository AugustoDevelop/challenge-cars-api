package com.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a car.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CARS")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`YEAR`")
    private Integer year;

    @Column(name = "LICENSE_PLATE", unique = true)
    private String licensePlate;

    @Column(name = "MODEL")
    private String model;

    @Column(name = "color")
    private String color;

    @Column(name = "USAGE_AMOUNT")
    private Integer usageAmount = 0;

    @Column(name = "PHOTO_URL")
    private String photoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User user;
}