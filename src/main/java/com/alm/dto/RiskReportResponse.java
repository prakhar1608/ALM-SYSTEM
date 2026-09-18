package com.alm.dto;

import com.alm.model.Scenario;
import com.alm.model.ScenarioResult;
import java.math.BigDecimal;
import java.util.List;

public record RiskReportResponse(List<ScenarioRisk> interestRateSensitivity,
                                 List<PortfolioTotal> maturityProfile) {
  public record ScenarioRisk(Scenario scenario, ScenarioResult result) {}
  public record PortfolioTotal(String category, String status, BigDecimal total) {}
}
