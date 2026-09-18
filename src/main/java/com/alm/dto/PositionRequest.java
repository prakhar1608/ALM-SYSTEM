package com.alm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PositionRequest(
    @NotBlank @Size(max = 150) String name,
    @NotNull @DecimalMin(value = "0.00") BigDecimal value,
    @NotNull @DecimalMin(value = "0.00") BigDecimal principal,
    String status) {}
