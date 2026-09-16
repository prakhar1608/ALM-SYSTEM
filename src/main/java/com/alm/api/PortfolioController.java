package com.alm.api;

import java.math.BigDecimal;
import java.util.*;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api")
public class PortfolioController {
 private final JdbcTemplate jdbc;
 public PortfolioController(JdbcTemplate jdbc){this.jdbc=jdbc;}
 @GetMapping("/dashboard") public Map<String,Object> dashboard(){
   Map<String,Object> a=jdbc.queryForMap("select nvl(sum(asset_value),0) total_value, nvl(sum(principal_amount),0) principal_total from assets where status='ACTIVE'");
   Map<String,Object> l=jdbc.queryForMap("select nvl(sum(liability_value),0) total_value, nvl(sum(principal_amount),0) principal_total from liabilities where status='ACTIVE'");
   BigDecimal av=(BigDecimal)a.get("TOTAL_VALUE"), lv=(BigDecimal)l.get("TOTAL_VALUE");
   return Map.of("assetValue",av,"liabilityValue",lv,"netPosition",av.subtract(lv),"liquidityRatio",lv.signum()==0?0:av.divide(lv,4,java.math.RoundingMode.HALF_UP),"latestScenarios",jdbc.queryForList("select s.scenario_id,s.scenario_name,s.scenario_type,s.interest_rate_shock,r.net_impact,r.risk_level from scenarios s left join scenario_results r on r.scenario_id=s.scenario_id order by s.scenario_id desc fetch first 5 rows only"));
 }
 @GetMapping("/assets") public List<Map<String,Object>> assets(){return jdbc.queryForList("select * from assets order by asset_id desc");}
 @GetMapping("/liabilities") public List<Map<String,Object>> liabilities(){return jdbc.queryForList("select * from liabilities order by liability_id desc");}
 @PostMapping("/assets") @PreAuthorize("hasAnyRole('ADMIN','ANALYST')") ResponseEntity<?> createAsset(@RequestBody Position p){ return create("assets","asset_name","asset_value",p); }
 @PostMapping("/liabilities") @PreAuthorize("hasAnyRole('ADMIN','ANALYST')") ResponseEntity<?> createLiability(@RequestBody Position p){return create("liabilities","liability_name","liability_value",p);}
 private ResponseEntity<?> create(String table,String nameCol,String valueCol,Position p){
   if(p.name()==null||p.name().isBlank()||p.value()==null||p.principal()==null) throw new IllegalArgumentException("name, value and principal are required");
   jdbc.update("insert into "+table+" ("+nameCol+","+valueCol+",principal_amount,status) values (?,?,?,?)",p.name(),p.value(),p.principal(),p.status()==null?"ACTIVE":p.status());
   return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message","Position created"));
 }
 public record Position(String name,BigDecimal value,BigDecimal principal,String status){}
}
