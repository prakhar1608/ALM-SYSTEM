package com.alm.service;

import com.alm.dao.AssetDao;
import com.alm.dao.LiabilityDao;
import com.alm.dao.ScenarioDao;
import com.alm.dto.ScenarioRequest;
import com.alm.dto.ScenarioResponse;
import com.alm.exception.ResourceNotFoundException;
import com.alm.model.Scenario;
import com.alm.model.ScenarioResult;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScenarioService {
  private final ScenarioDao scenarioDao;
  private final AssetDao assetDao;
  private final LiabilityDao liabilityDao;
  private final RiskCalculator calculator;
  public ScenarioService(ScenarioDao scenarioDao, AssetDao assetDao, LiabilityDao liabilityDao, RiskCalculator calculator) {
    this.scenarioDao = scenarioDao; this.assetDao = assetDao; this.liabilityDao = liabilityDao; this.calculator = calculator;
  }
  public List<ScenarioResponse> findAll() { return scenarioDao.findAll().stream().map(this::response).toList(); }
  public ScenarioResponse findById(long id) { return response(require(id)); }
  public Scenario create(ScenarioRequest request) { return scenarioDao.create(request, status(request.status())); }
  public Scenario update(long id, ScenarioRequest request) { require(id); scenarioDao.update(id, request, status(request.status())); return require(id); }
  public void deactivate(long id) { if (!scenarioDao.deactivate(id)) throw new ResourceNotFoundException("Active scenario", id); }
  @Transactional
  public ScenarioResponse run(long id) {
    Scenario scenario = require(id);
    if (!"ACTIVE".equals(scenario.status())) throw new IllegalArgumentException("Only active scenarios can be run");
    BigDecimal assets = assetDao.totalActiveValue();
    BigDecimal liabilities = liabilityDao.totalActiveValue();
    RiskCalculator.ScenarioImpact impact = calculator.calculate(assets, liabilities, scenario.interestRateShock());
    scenarioDao.replaceResult(id, new ScenarioResult(0, id, impact.assetImpact(), impact.liabilityImpact(), impact.netImpact(), impact.riskLevel(), null));
    return response(scenario);
  }
  private ScenarioResponse response(Scenario scenario) { return new ScenarioResponse(scenario, scenarioDao.findLatestResult(scenario.id()).orElse(null)); }
  private Scenario require(long id) { return scenarioDao.findById(id).orElseThrow(() -> new ResourceNotFoundException("Scenario", id)); }
  private String status(String value) { String status = value == null || value.isBlank() ? "ACTIVE" : value.toUpperCase(Locale.ROOT); if (!status.equals("ACTIVE") && !status.equals("INACTIVE")) throw new IllegalArgumentException("status must be ACTIVE or INACTIVE"); return status; }
}
