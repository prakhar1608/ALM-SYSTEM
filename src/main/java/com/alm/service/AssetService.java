package com.alm.service;

import com.alm.dao.AssetDao;
import com.alm.dto.PositionRequest;
import com.alm.exception.ResourceNotFoundException;
import com.alm.model.Asset;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class AssetService {
  private final AssetDao dao;
  public AssetService(AssetDao dao) { this.dao = dao; }
  public List<Asset> findAll() { return dao.findAll(); }
  public Asset findById(long id) { return dao.findById(id).orElseThrow(() -> new ResourceNotFoundException("Asset", id)); }
  public Asset create(PositionRequest request) { return dao.create(request, status(request.status())); }
  public Asset update(long id, PositionRequest request) { findById(id); dao.update(id, request, status(request.status())); return findById(id); }
  public void deactivate(long id) { if (!dao.deactivate(id)) throw new ResourceNotFoundException("Active asset", id); }
  private String status(String value) { String status = value == null || value.isBlank() ? "ACTIVE" : value.toUpperCase(Locale.ROOT); if (!status.equals("ACTIVE") && !status.equals("INACTIVE")) throw new IllegalArgumentException("status must be ACTIVE or INACTIVE"); return status; }
}
