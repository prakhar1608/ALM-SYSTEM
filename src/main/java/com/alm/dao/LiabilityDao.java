package com.alm.dao;

import com.alm.dto.PositionRequest;
import com.alm.model.Liability;
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
public class LiabilityDao {
  private static final RowMapper<Liability> MAPPER = (rs, row) -> new Liability(
      rs.getLong("liability_id"), rs.getString("liability_name"), rs.getBigDecimal("liability_value"),
      rs.getBigDecimal("principal_amount"), rs.getString("status"),
      rs.getTimestamp("created_at").toLocalDateTime(), rs.getTimestamp("updated_at").toLocalDateTime());
  private final JdbcTemplate jdbc;
  public LiabilityDao(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  public List<Liability> findAll() { return jdbc.query("select liability_id,liability_name,liability_value,principal_amount,status,created_at,updated_at from liabilities order by liability_id desc", MAPPER); }
  public Optional<Liability> findById(long id) { return jdbc.query("select liability_id,liability_name,liability_value,principal_amount,status,created_at,updated_at from liabilities where liability_id=?", MAPPER, id).stream().findFirst(); }
  public Liability create(PositionRequest request, String status) {
    KeyHolder keys = new GeneratedKeyHolder();
    jdbc.update(connection -> { PreparedStatement ps = connection.prepareStatement(
        "insert into liabilities (liability_name,liability_value,principal_amount,status) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, request.name()); ps.setBigDecimal(2, request.value()); ps.setBigDecimal(3, request.principal()); ps.setString(4, status); return ps; }, keys);
    return findById(((Number) keys.getKeys().get("LIABILITY_ID")).longValue()).orElseThrow();
  }
  public boolean update(long id, PositionRequest request, String status) { return jdbc.update("update liabilities set liability_name=?,liability_value=?,principal_amount=?,status=?,updated_at=current_timestamp where liability_id=?", request.name(), request.value(), request.principal(), status, id) == 1; }
  public boolean deactivate(long id) { return jdbc.update("update liabilities set status='INACTIVE',updated_at=current_timestamp where liability_id=? and status<>'INACTIVE'", id) == 1; }
  public java.math.BigDecimal totalActiveValue() { return jdbc.queryForObject("select nvl(sum(liability_value),0) from liabilities where status='ACTIVE'", java.math.BigDecimal.class); }
}
