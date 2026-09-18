package com.alm.dto;

import com.alm.model.Scenario;
import com.alm.model.ScenarioResult;
import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(BigDecimal assetValue, BigDecimal liabilityValue,
                                BigDecimal netPosition, BigDecimal liquidityRatio,
                                List<ScenarioSummary> latestScenarios) {
  public record ScenarioSummary(Scenario scenario, ScenarioResult result) {}
}
