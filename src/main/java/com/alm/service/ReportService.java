package com.alm.service;

import com.alm.dao.PortfolioReportDao;
import com.alm.dao.ScenarioDao;
import com.alm.dto.RiskReportResponse;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
  private final ScenarioDao scenarioDao;
  private final PortfolioReportDao reportDao;
  public ReportService(ScenarioDao scenarioDao, PortfolioReportDao reportDao) { this.scenarioDao = scenarioDao; this.reportDao = reportDao; }
  public RiskReportResponse riskReport() {
    return new RiskReportResponse(scenarioDao.findAll().stream().map(s -> new RiskReportResponse.ScenarioRisk(s, scenarioDao.findLatestResult(s.id()).orElse(null))).toList(), reportDao.totalsByStatus());
  }
}
