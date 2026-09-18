package com.alm.service;

import com.alm.dao.LiabilityDao;
import com.alm.dto.PositionRequest;
import com.alm.exception.ResourceNotFoundException;
import com.alm.model.Liability;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class LiabilityService {
  private final LiabilityDao dao;
  public LiabilityService(LiabilityDao dao) { this.dao = dao; }
  public List<Liability> findAll() { return dao.findAll(); }
  public Liability findById(long id) { return dao.findById(id).orElseThrow(() -> new ResourceNotFoundException("Liability", id)); }
  public Liability create(PositionRequest request) { return dao.create(request, status(request.status())); }
  public Liability update(long id, PositionRequest request) { findById(id); dao.update(id, request, status(request.status())); return findById(id); }
  public void deactivate(long id) { if (!dao.deactivate(id)) throw new ResourceNotFoundException("Active liability", id); }
  private String status(String value) { String status = value == null || value.isBlank() ? "ACTIVE" : value.toUpperCase(Locale.ROOT); if (!status.equals("ACTIVE") && !status.equals("INACTIVE")) throw new IllegalArgumentException("status must be ACTIVE or INACTIVE"); return status; }
}
