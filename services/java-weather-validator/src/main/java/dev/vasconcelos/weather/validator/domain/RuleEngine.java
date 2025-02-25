package dev.vasconcelos.weather.validator.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RuleEngine {

     private List<Rule> rules = new ArrayList<>();

    public static RuleEngine create(){
        var ruleEngine = new RuleEngine();
        ruleEngine.registerAllRules();
        return ruleEngine;
    }

    private void registerRule(Rule rule){
         rules.add(rule);
     }
     private void registerAllRules(){
        registerRule(new HumidityTemperatureLowRule());
        registerRule(new PhysicalRangeRule());
        registerRule(new HumidityTemperatureHighRule());
     }


    public List<ValidationResult> validate(WeatherData data) {
        return rules.stream()
                .map(rule -> {
                    try {
                        return rule.validate(data);
                    } catch (Exception e) {
                        Logger.getLogger(RuleEngine.class.getName())
                                .log(Level.SEVERE, "Error running rule " + rule.getCode(), e);

                        return new ValidationResult(false, rule.getCode(),
                                "Error running rule: " + e.getMessage());
                    }
                })
                .collect(Collectors.toList());
    }

    public List<ValidationResult> validateBatch(List<WeatherData> dataList) {
        return dataList.stream()
                .flatMap(data -> validate(data).stream())
                .collect(Collectors.toList());
    }


}
