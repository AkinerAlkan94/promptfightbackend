package com.ozgoygoy.promptfightbackend.model;

public class PlayerInfo {
    private String name;
    private String background;
    private int health;
    private boolean activeTurn;

    public PlayerInfo(String name){
        this.name = name;
        this.health = 100;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public boolean isActiveTurn() {
        return activeTurn;
    }

    public void setActiveTurn(boolean activeTurn) {
        this.activeTurn = activeTurn;
    }
}