package dev.vasconcelos.weather.validator.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum AnomalySeverity {
    NO_ANOMALY,
    LOW,
    MEDIUM,
    HIGH;


    /**
     * Determines the overall anomaly severity based on validation results
     *
     * @param results List of validation results to analyze
     * @return The appropriate severity level
     */
    public static AnomalySeverity fromValidations(List<ValidationResult> results) {
         List<ValidationResult> failures = results.stream()
                .filter(result -> !result.isValid())
                .toList();

         if (failures.isEmpty()) {
            return LOW;
        }

         Map<String, Long> failuresByCode = failures.stream()
                .collect(Collectors.groupingBy(ValidationResult::getRuleCode, Collectors.counting()));

        // HIGH severity conditions
        // - Physical range violations (Q1)
        // - Identical measurements for 3+ days (Q3)
        // - Extreme thermal gradients (D2)
        // - More than 3 failures
        if (failuresByCode.containsKey("Q1") ||
                failuresByCode.containsKey("Q3") ||
                failuresByCode.containsKey("D2") ||
                failures.size() > 3) {
            return HIGH;
        }
        // MEDIUM severity conditions
        // - Temperatures exceeding percentile thresholds (D1)
        // - Extreme humidity/temperature combinations (D4, D5)
        // - Temperatures above historical max (S2)
        // - 2-3 failures of any type
        if (failuresByCode.containsKey("D1") ||
                failuresByCode.containsKey("D4") ||
                failuresByCode.containsKey("D5") ||
                failuresByCode.containsKey("S2") ||
                failures.size() >= 2) {
            return MEDIUM;
        }
        // Default to LOW severity for other cases (1 non-critical failure)
        return LOW;
    }
}