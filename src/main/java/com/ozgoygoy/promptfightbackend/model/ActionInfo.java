package com.ozgoygoy.promptfightbackend.model;

public class ActionInfo {
    private final MoveType moveType;
    private int healAmount;
    private int baseDmg;
    private int creativitySuitabilityDamage;
    private int environmentSuitabilityDamage;
    private int backgroundSuitabilityDamage;

    public ActionInfo(MoveType moveType){
        this.moveType = moveType;
    }

    public int getHealAmount() {
        return healAmount;
    }

    public void setHealAmount(int healAmount) {
        this.healAmount = healAmount;
    }

    public int getBaseDmg() {
        return baseDmg;
    }

    public void setBaseDmg(int baseDmg) {
        this.baseDmg = baseDmg;
    }

    public int getCreativitySuitabilityDamage() {
        return creativitySuitabilityDamage;
    }

    public void setCreativitySuitabilityDamage(int creativitySuitabilityDamage) {
        this.creativitySuitabilityDamage = creativitySuitabilityDamage;
    }

    public int getEnvironmentSuitabilityDamage() {
        return environmentSuitabilityDamage;
    }

    public void setEnvironmentSuitabilityDamage(int environmentSuitabilityDamage) {
        this.environmentSuitabilityDamage = environmentSuitabilityDamage;
    }

    public int getBackgroundSuitabilityDamage() {
        return backgroundSuitabilityDamage;
    }

    public void setBackgroundSuitabilityDamage(int backgroundSuitabilityDamage) {
        this.backgroundSuitabilityDamage = backgroundSuitabilityDamage;
    }

    public MoveType getMoveType() {
        return moveType;
    }
}