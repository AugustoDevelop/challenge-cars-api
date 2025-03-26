package com.api.service;

import com.api.entity.Car;
import com.api.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for sorting cars of a user.
 */
@Service
public class CarSortingService {

    /**
     * Sorts the cars of a user by usage amount in descending order.
     *
     * @param user the user whose cars need to be sorted
     */
    public void sortCarsByUsageAmount(User user) {
        List<Car> cars = user.getCars();
        cars.sort((c1, c2) -> c2.getUsageAmount() - c1.getUsageAmount());
    }
}
