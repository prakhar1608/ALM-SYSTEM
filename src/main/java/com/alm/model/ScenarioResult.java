package com.alm.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ScenarioResult(long id, long scenarioId, BigDecimal assetImpact,
                             BigDecimal liabilityImpact, BigDecimal netImpact,
                             String riskLevel, LocalDateTime calculatedAt) {}
