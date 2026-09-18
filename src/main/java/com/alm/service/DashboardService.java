package com.alm.service;

import com.alm.dao.AssetDao;
import com.alm.dao.LiabilityDao;
import com.alm.dao.ScenarioDao;
import com.alm.dto.DashboardResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
  private final AssetDao assetDao;
  private final LiabilityDao liabilityDao;
  private final ScenarioDao scenarioDao;
  public DashboardService(AssetDao assetDao, LiabilityDao liabilityDao, ScenarioDao scenarioDao) { this.assetDao = assetDao; this.liabilityDao = liabilityDao; this.scenarioDao = scenarioDao; }
  public DashboardResponse getDashboard() {
    BigDecimal assets = assetDao.totalActiveValue();
    BigDecimal liabilities = liabilityDao.totalActiveValue();
    return new DashboardResponse(assets, liabilities, assets.subtract(liabilities),
        liabilities.signum() == 0 ? BigDecimal.ZERO : assets.divide(liabilities, 4, RoundingMode.HALF_UP),
        scenarioDao.findLatest(5).stream().map(s -> new DashboardResponse.ScenarioSummary(s, scenarioDao.findLatestResult(s.id()).orElse(null))).toList());
  }
}
