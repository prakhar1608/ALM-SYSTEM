package com.alm.api;

import java.util.*; import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/reports") public class ReportController {
 private final JdbcTemplate jdbc; public ReportController(JdbcTemplate jdbc){this.jdbc=jdbc;}
 @GetMapping("/risk") public Map<String,Object> risk(){return Map.of("interestRateSensitivity",jdbc.queryForList("select scenario_name,interest_rate_shock,net_impact,risk_level from scenarios s left join scenario_results r on r.scenario_id=s.scenario_id"),"maturityProfile",jdbc.queryForList("select 'ASSET' category,status,sum(asset_value) total from assets group by status union all select 'LIABILITY',status,sum(liability_value) from liabilities group by status"));}
}
