package com.alm.dto;

import com.alm.model.Scenario;
import com.alm.model.ScenarioResult;

public record ScenarioResponse(Scenario scenario, ScenarioResult result) {}
