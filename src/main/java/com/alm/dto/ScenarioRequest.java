package com.alm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ScenarioRequest(
    @NotBlank @Size(max = 150) String name,
    @NotBlank @Size(max = 30) String type,
    @NotNull @DecimalMin(value = "-100.00") BigDecimal shock,
    String status) {}
