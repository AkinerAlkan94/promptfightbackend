package com.ozgoygoy.promptfightbackend.model;

import java.util.ArrayList;
import java.util.List;

public class GameInfo {
    private final String gameId;
    private String environment;
    private final List<PlayerInfo> players = new ArrayList<>();
    private final List<String> turnHistory = new ArrayList<>();

    public GameInfo(){
        this.gameId = java.util.UUID.randomUUID().toString();
    }

    public void setPlayerName(String name){
        if(players.isEmpty()){
            PlayerInfo playerInfo = new PlayerInfo(name);
            playerInfo.setActiveTurn(true);
            players.add(playerInfo);
        } else {
            players.add(1, new PlayerInfo(name));
        }
    }

    public void addTurnHistory(String turn){
        turnHistory.add(turn);
    }

    public boolean isGameOccupied(){
        return players.size() == 2;
    }

    public String getGameId() {
        return gameId;
    }

    public void setPlayerBackground(String playerName, String background) {
        for(PlayerInfo player : players){
            if(player.getName().equals(playerName)){
                player.setBackground(background);
            }
        }
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getEnvironment() {
        return environment;
    }

    public List<PlayerInfo> getPlayers() {
        return players;
    }

    public List<String> getTurnHistory() {
        return turnHistory;
    }
}