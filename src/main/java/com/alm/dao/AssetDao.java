package com.alm.dao;

import com.alm.dto.PositionRequest;
import com.alm.model.Asset;
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
public class AssetDao {
  private static final RowMapper<Asset> MAPPER = (rs, row) -> new Asset(
      rs.getLong("asset_id"), rs.getString("asset_name"), rs.getBigDecimal("asset_value"),
      rs.getBigDecimal("principal_amount"), rs.getString("status"),
      rs.getTimestamp("created_at").toLocalDateTime(), rs.getTimestamp("updated_at").toLocalDateTime());
  private final JdbcTemplate jdbc;
  public AssetDao(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  public List<Asset> findAll() { return jdbc.query("select asset_id,asset_name,asset_value,principal_amount,status,created_at,updated_at from assets order by asset_id desc", MAPPER); }
  public Optional<Asset> findById(long id) { return jdbc.query("select asset_id,asset_name,asset_value,principal_amount,status,created_at,updated_at from assets where asset_id=?", MAPPER, id).stream().findFirst(); }
  public Asset create(PositionRequest request, String status) {
    KeyHolder keys = new GeneratedKeyHolder();
    jdbc.update(connection -> { PreparedStatement ps = connection.prepareStatement(
        "insert into assets (asset_name,asset_value,principal_amount,status) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, request.name()); ps.setBigDecimal(2, request.value()); ps.setBigDecimal(3, request.principal()); ps.setString(4, status); return ps; }, keys);
    return findById(((Number) keys.getKeys().get("ASSET_ID")).longValue()).orElseThrow();
  }
  public boolean update(long id, PositionRequest request, String status) { return jdbc.update("update assets set asset_name=?,asset_value=?,principal_amount=?,status=?,updated_at=current_timestamp where asset_id=?", request.name(), request.value(), request.principal(), status, id) == 1; }
  public boolean deactivate(long id) { return jdbc.update("update assets set status='INACTIVE',updated_at=current_timestamp where asset_id=? and status<>'INACTIVE'", id) == 1; }
  public java.math.BigDecimal totalActiveValue() { return jdbc.queryForObject("select nvl(sum(asset_value),0) from assets where status='ACTIVE'", java.math.BigDecimal.class); }
}
