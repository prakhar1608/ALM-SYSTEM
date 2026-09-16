package com.alm.service;

import java.math.*;
import org.springframework.stereotype.Service;

@Service public class RiskCalculator {
 public ScenarioImpact calculate(BigDecimal assetValue, BigDecimal liabilityValue, BigDecimal shockPercent) {
  BigDecimal assetImpact=assetValue.multiply(shockPercent).divide(BigDecimal.valueOf(-100),2,RoundingMode.HALF_UP);
  BigDecimal liabilityImpact=liabilityValue.multiply(shockPercent).divide(BigDecimal.valueOf(200),2,RoundingMode.HALF_UP);
  BigDecimal net=assetImpact.subtract(liabilityImpact), abs=net.abs();
  String level=abs.compareTo(assetValue.multiply(BigDecimal.valueOf(.08)))>=0?"CRITICAL":abs.compareTo(assetValue.multiply(BigDecimal.valueOf(.04)))>=0?"HIGH":"MEDIUM";
  return new ScenarioImpact(assetImpact,liabilityImpact,net,level);
 }
 public record ScenarioImpact(BigDecimal assetImpact,BigDecimal liabilityImpact,BigDecimal netImpact,String riskLevel){}
}
