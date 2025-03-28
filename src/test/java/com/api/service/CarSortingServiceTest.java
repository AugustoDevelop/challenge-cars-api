package com.api.service;

import com.api.entity.Car;
import com.api.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CarSortingServiceTest {

    private CarSortingService carSortingService;

    @BeforeEach
    void setUp() {
        carSortingService = new CarSortingService();
    }

    @Test
    void sortCarsByUsageAmount_shouldSortCarsInDescendingOrder() {
        User user = new User();
        List<Car> cars = new ArrayList<>();
        cars.add(new Car(null, 2010, "ABC123", "Model A", "Red", 5000, null, 0, null));
        cars.add(new Car(null, 2015, "XYZ789", "Model B", "Blue", 15000, null, 0, null));
        cars.add(new Car(null, 2012, "DEF456", "Model C", "Green", 10000, null, 0, null));
        user.setCars(cars);

        carSortingService.sortCarsByUsageAmount(user);

        assertEquals(15000, user.getCars().get(0).getUsageAmount());
        assertEquals(10000, user.getCars().get(1).getUsageAmount());
        assertEquals(5000, user.getCars().get(2).getUsageAmount());
    }

    @Test
    void sortCarsByUsageAmount_shouldHandleEmptyCarList() {
        User user = new User();
        user.setCars(new ArrayList<>());

        carSortingService.sortCarsByUsageAmount(user);

        assertEquals(0, user.getCars().size());
    }

    @Test
    void sortCarsByUsageAmount_shouldHandleNullCarList() {
        User user = new User();
        user.setCars(null);

        carSortingService.sortCarsByUsageAmount(user);

        assertEquals(null, user.getCars());
    }
}