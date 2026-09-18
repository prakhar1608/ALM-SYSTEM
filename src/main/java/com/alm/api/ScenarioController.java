package com.alm.api;

import com.alm.dto.ScenarioRequest;
import com.alm.dto.ScenarioResponse;
import com.alm.model.Scenario;
import com.alm.service.ScenarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scenarios")
public class ScenarioController {
  private final ScenarioService service;
  public ScenarioController(ScenarioService service) { this.service = service; }
  @GetMapping public List<ScenarioResponse> all() { return service.findAll(); }
  @GetMapping("/{id}") public ScenarioResponse findById(@PathVariable long id) { return service.findById(id); }
  @PostMapping @PreAuthorize("hasAnyRole('ADMIN','ANALYST')") public ResponseEntity<Scenario> create(@Valid @RequestBody ScenarioRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request)); }
  @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','ANALYST')") public Scenario update(@PathVariable long id, @Valid @RequestBody ScenarioRequest request) { return service.update(id, request); }
  @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','ANALYST')") @ResponseStatus(HttpStatus.NO_CONTENT) public void deactivate(@PathVariable long id) { service.deactivate(id); }
  @PostMapping("/{id}/run") @PreAuthorize("hasAnyRole('ADMIN','ANALYST')") public ScenarioResponse run(@PathVariable long id) { return service.run(id); }
}
