package com.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDto {

    @NotNull(message = "Year é obrigatória")
    private Integer year;

    @NotBlank(message = "Placa é obrigatória")
    private String licensePlate;

    @NotBlank(message = "Modelo é obrigatório")
    private String model;

    @NotBlank(message = "Cor é obrigatória")
    private String color;
}
