package com.alm.api;

import com.alm.dto.DashboardResponse;
import com.alm.dto.PositionRequest;
import com.alm.model.Asset;
import com.alm.model.Liability;
import com.alm.service.AssetService;
import com.alm.service.DashboardService;
import com.alm.service.LiabilityService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PortfolioController {
  private final DashboardService dashboardService;
  private final AssetService assetService;
  private final LiabilityService liabilityService;
  public PortfolioController(DashboardService dashboardService, AssetService assetService, LiabilityService liabilityService) {
    this.dashboardService = dashboardService; this.assetService = assetService; this.liabilityService = liabilityService;
  }
  @GetMapping("/dashboard") public DashboardResponse dashboard() { return dashboardService.getDashboard(); }
  @GetMapping("/assets") public List<Asset> assets() { return assetService.findAll(); }
  @GetMapping("/assets/{id}") public Asset asset(@PathVariable long id) { return assetService.findById(id); }
  @PostMapping("/assets") @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
  public ResponseEntity<Asset> createAsset(@Valid @RequestBody PositionRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(assetService.create(request)); }
  @PutMapping("/assets/{id}") @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
  public Asset updateAsset(@PathVariable long id, @Valid @RequestBody PositionRequest request) { return assetService.update(id, request); }
  @DeleteMapping("/assets/{id}") @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
  @ResponseStatus(HttpStatus.NO_CONTENT) public void deactivateAsset(@PathVariable long id) { assetService.deactivate(id); }
  @GetMapping("/liabilities") public List<Liability> liabilities() { return liabilityService.findAll(); }
  @GetMapping("/liabilities/{id}") public Liability liability(@PathVariable long id) { return liabilityService.findById(id); }
  @PostMapping("/liabilities") @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
  public ResponseEntity<Liability> createLiability(@Valid @RequestBody PositionRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(liabilityService.create(request)); }
  @PutMapping("/liabilities/{id}") @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
  public Liability updateLiability(@PathVariable long id, @Valid @RequestBody PositionRequest request) { return liabilityService.update(id, request); }
  @DeleteMapping("/liabilities/{id}") @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
  @ResponseStatus(HttpStatus.NO_CONTENT) public void deactivateLiability(@PathVariable long id) { liabilityService.deactivate(id); }
}
