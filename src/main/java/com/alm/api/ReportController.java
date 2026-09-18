package com.alm.api;

import com.alm.dto.RiskReportResponse;
import com.alm.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
  private final ReportService service;
  public ReportController(ReportService service) { this.service = service; }
  @GetMapping("/risk") public RiskReportResponse risk() { return service.riskReport(); }
}
