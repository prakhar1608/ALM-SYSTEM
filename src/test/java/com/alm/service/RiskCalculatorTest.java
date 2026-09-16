package com.alm.service;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
class RiskCalculatorTest {
 private final RiskCalculator calculator=new RiskCalculator();
 @Test void rateIncreaseProducesNegativeNetImpact(){var r=calculator.calculate(new BigDecimal("1000000"),new BigDecimal("800000"),new BigDecimal("2"));assertEquals(new BigDecimal("-28000.00"),r.netImpact());assertEquals("MEDIUM",r.riskLevel());}
 @Test void extremeShockIsCritical(){assertEquals("CRITICAL",calculator.calculate(new BigDecimal("1000000"),new BigDecimal("800000"),new BigDecimal("10")).riskLevel());}
}
