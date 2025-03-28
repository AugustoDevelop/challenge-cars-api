package com.api.service;

import com.api.entity.Car;
import com.api.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for sorting cars owned by a user based on specific criteria.
 *
 * <p>This service provides methods to organize cars in a user-friendly manner, enhancing data presentation and analysis.
 */
@Service
public class CarSortingService {

    /**
     * Sorts the cars of a user by their usage amount in descending order.
     *
     * <p>This method modifies the user's car list in-place, arranging cars from most used to least used.
     *
     * @param user the user whose cars need to be sorted
     */
    public void sortCarsByUsageAmount(User user) {
        List<Car> cars = user.getCars();
        if (cars != null) {
            cars.sort((c1, c2) -> Integer.compare(c2.getUsageAmount(), c1.getUsageAmount()));
        }
    }
}
