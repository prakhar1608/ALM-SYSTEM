package com.alm.api;

import java.math.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.alm.service.RiskCalculator;

@RestController @RequestMapping("/api/scenarios") public class ScenarioController {
 private final JdbcTemplate jdbc; private final RiskCalculator calculator; public ScenarioController(JdbcTemplate jdbc,RiskCalculator calculator){this.jdbc=jdbc;this.calculator=calculator;}
 @GetMapping public List<Map<String,Object>> all(){return jdbc.queryForList("select s.*, r.asset_impact,r.liability_impact,r.net_impact,r.risk_level,r.calculated_at from scenarios s left join scenario_results r on r.scenario_id=s.scenario_id order by s.scenario_id desc");}
 @PostMapping @PreAuthorize("hasAnyRole('ADMIN','ANALYST')") ResponseEntity<?> create(@RequestBody Scenario s){
  if(s.name()==null||s.name().isBlank()||s.type()==null)throw new IllegalArgumentException("name and type are required");
  jdbc.update("insert into scenarios (scenario_name,scenario_type,interest_rate_shock,status) values (?,?,?,?)",s.name(),s.type(),s.shock()==null?BigDecimal.ZERO:s.shock(),"ACTIVE"); return ResponseEntity.status(201).body(Map.of("message","Scenario created")); }
 @PostMapping("/{id}/run") @PreAuthorize("hasAnyRole('ADMIN','ANALYST')") public Map<String,Object> run(@PathVariable long id){
  Map<String,Object> s=jdbc.queryForMap("select * from scenarios where scenario_id=?",id); BigDecimal shock=(BigDecimal)s.get("INTEREST_RATE_SHOCK");
  BigDecimal assets=(BigDecimal)jdbc.queryForObject("select nvl(sum(asset_value),0) from assets where status='ACTIVE'",BigDecimal.class);
  BigDecimal liabilities=(BigDecimal)jdbc.queryForObject("select nvl(sum(liability_value),0) from liabilities where status='ACTIVE'",BigDecimal.class);
  RiskCalculator.ScenarioImpact impact=calculator.calculate(assets,liabilities,shock); BigDecimal ai=impact.assetImpact(),li=impact.liabilityImpact(),net=impact.netImpact(); String risk=impact.riskLevel();
  jdbc.update("delete from scenario_results where scenario_id=?",id); jdbc.update("insert into scenario_results(scenario_id,asset_impact,liability_impact,net_impact,risk_level) values(?,?,?,?,?)",id,ai,li,net,risk);
  return Map.of("scenarioId",id,"assetImpact",ai,"liabilityImpact",li,"netImpact",net,"riskLevel",risk); }
 public record Scenario(String name,String type,BigDecimal shock){}
}
