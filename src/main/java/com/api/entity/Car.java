package com.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Entity representing a car, encapsulating its key attributes and relationships.
 *
 * <p>This entity is mapped to the "CARS" table in the database and includes fields for car details and ownership.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CARS")
@Entity(name = "CARS")
public class Car {
    /**
     * Unique identifier for the car, auto-generated upon creation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The year the car was manufactured.
     */
    @Column(name = "CAR_YEAR")
    private Integer year;

    /**
     * Unique license plate number of the car.
     */
    @Column(name = "LICENSE_PLATE", unique = true)
    private String licensePlate;

    /**
     * The model name of the car.
     */
    @Column(name = "MODEL")
    private String model;

    /**
     * The color of the car.
     */
    @Column(name = "color")
    private String color;

    /**
     * The total usage amount of the car (defaults to 0).
     */
    @Column(name = "USAGE_AMOUNT")
    private Integer usageAmount = 0;

    /**
     * The URL of the car's photo.
     */
    @Column(name = "PHOTO_CAR_URL")
    private String photoCarUrl;

    @Version
    private int version;

    /**
     * The user who owns this car.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    @JsonBackReference
    private User user;
}
