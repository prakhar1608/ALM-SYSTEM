package com.alm.dao;

import com.alm.dto.RiskReportResponse.PortfolioTotal;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PortfolioReportDao {
  private final JdbcTemplate jdbc;
  public PortfolioReportDao(JdbcTemplate jdbc) { this.jdbc = jdbc; }
  public List<PortfolioTotal> totalsByStatus() {
    return jdbc.query("select 'ASSET' category,status,sum(asset_value) total from assets group by status union all select 'LIABILITY' category,status,sum(liability_value) total from liabilities group by status",
        (rs, row) -> new PortfolioTotal(rs.getString("category"), rs.getString("status"), rs.getBigDecimal("total")));
  }
}
