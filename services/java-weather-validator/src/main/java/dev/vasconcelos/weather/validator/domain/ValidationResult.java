package dev.vasconcelos.weather.validator.domain;

public class ValidationResult {

    private boolean valid;
    private String ruleCode;
    private String message;


    public ValidationResult(boolean valid, String ruleCode, String message) {
        this.valid = valid;
        this.ruleCode = ruleCode;
        this.message = message;
    }

    public boolean isValid() { return valid;}
    public String getRuleCode() {return ruleCode;}
    public String getMessage() {return message;}
}
