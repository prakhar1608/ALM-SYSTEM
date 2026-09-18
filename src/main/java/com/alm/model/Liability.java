package com.alm.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Liability(long id, String name, BigDecimal value, BigDecimal principal,
                        String status, LocalDateTime createdAt, LocalDateTime updatedAt) {}
