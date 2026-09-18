package com.alm.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Scenario(long id, String name, String type, BigDecimal interestRateShock,
                       String status, LocalDateTime createdAt, LocalDateTime updatedAt) {}
