package com.alm.dao;

import com.alm.dto.ScenarioRequest;
import com.alm.model.Scenario;
import com.alm.model.ScenarioResult;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ScenarioDao {
  private static final RowMapper<Scenario> SCENARIO_MAPPER = (rs, row) -> new Scenario(
      rs.getLong("scenario_id"), rs.getString("scenario_name"), rs.getString("scenario_type"),
      rs.getBigDecimal("interest_rate_shock"), rs.getString("status"),
      rs.getTimestamp("created_at").toLocalDateTime(), rs.getTimestamp("updated_at").toLocalDateTime());
  private static final RowMapper<ScenarioResult> RESULT_MAPPER = (rs, row) -> new ScenarioResult(
      rs.getLong("result_id"), rs.getLong("scenario_id"), rs.getBigDecimal("asset_impact"),
      rs.getBigDecimal("liability_impact"), rs.getBigDecimal("net_impact"), rs.getString("risk_level"),
      rs.getTimestamp("calculated_at").toLocalDateTime());
  private final JdbcTemplate jdbc;
  public ScenarioDao(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  public List<Scenario> findAll() { return jdbc.query("select scenario_id,scenario_name,scenario_type,interest_rate_shock,status,created_at,updated_at from scenarios order by scenario_id desc", SCENARIO_MAPPER); }
  public List<Scenario> findLatest(int limit) { return jdbc.query("select scenario_id,scenario_name,scenario_type,interest_rate_shock,status,created_at,updated_at from scenarios order by scenario_id desc fetch first ? rows only", SCENARIO_MAPPER, limit); }
  public Optional<Scenario> findById(long id) { return jdbc.query("select scenario_id,scenario_name,scenario_type,interest_rate_shock,status,created_at,updated_at from scenarios where scenario_id=?", SCENARIO_MAPPER, id).stream().findFirst(); }
  public Scenario create(ScenarioRequest request, String status) {
    KeyHolder keys = new GeneratedKeyHolder();
    jdbc.update(connection -> { PreparedStatement ps = connection.prepareStatement(
        "insert into scenarios (scenario_name,scenario_type,interest_rate_shock,status) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, request.name()); ps.setString(2, request.type()); ps.setBigDecimal(3, request.shock()); ps.setString(4, status); return ps; }, keys);
    return findById(((Number) keys.getKeys().get("SCENARIO_ID")).longValue()).orElseThrow();
  }
  public boolean update(long id, ScenarioRequest request, String status) { return jdbc.update("update scenarios set scenario_name=?,scenario_type=?,interest_rate_shock=?,status=?,updated_at=current_timestamp where scenario_id=?", request.name(), request.type(), request.shock(), status, id) == 1; }
  public boolean deactivate(long id) { return jdbc.update("update scenarios set status='INACTIVE',updated_at=current_timestamp where scenario_id=? and status<>'INACTIVE'", id) == 1; }
  public Optional<ScenarioResult> findLatestResult(long scenarioId) { return jdbc.query("select result_id,scenario_id,asset_impact,liability_impact,net_impact,risk_level,calculated_at from scenario_results where scenario_id=? order by calculated_at desc,result_id desc fetch first 1 rows only", RESULT_MAPPER, scenarioId).stream().findFirst(); }
  public void replaceResult(long scenarioId, ScenarioResult result) {
    jdbc.update("delete from scenario_results where scenario_id=?", scenarioId);
    jdbc.update("insert into scenario_results(scenario_id,asset_impact,liability_impact,net_impact,risk_level) values(?,?,?,?,?)", scenarioId, result.assetImpact(), result.liabilityImpact(), result.netImpact(), result.riskLevel());
  }
}
