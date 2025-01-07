package com.ozgoygoy.promptfightbackend.service;

import com.ozgoygoy.promptfightbackend.model.ActionInfo;
import com.ozgoygoy.promptfightbackend.model.GameInfo;
import com.ozgoygoy.promptfightbackend.model.MoveType;
import com.ozgoygoy.promptfightbackend.model.PlayerInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
public class GameService {

    private final ActionService actionService;
    private final HashMap<String, GameInfo> gameInfos = new HashMap<>();
    private final ConcurrentHashMap<String, List<Consumer<GameInfo>>> callbacks = new ConcurrentHashMap<>();

    public GameService(ActionService actionService) {
        this.actionService = actionService;
    }

    public GameInfo joinGame(String playerName) {
        for (GameInfo gameInfo : gameInfos.values()) {
            if (!gameInfo.isGameOccupied()) {
                gameInfo.setPlayerName(playerName);
                notifyCallbacks(gameInfo.getGameId(), gameInfo);
                return gameInfo;
            }
        }

        GameInfo gameInfo = new GameInfo();
        gameInfo.setPlayerName(playerName);
        gameInfos.put(gameInfo.getGameId(), gameInfo);
        notifyCallbacks(gameInfo.getGameId(), gameInfo);
        return gameInfo;
    }

    public void setPlayerBackground(String gameId, String playerName, String background) {
        GameInfo gameInfo = gameInfos.get(gameId);
        gameInfo.setPlayerBackground(playerName, background);
        notifyCallbacks(gameId, gameInfo);
    }

    public void setEnvironment(String gameId, String environment) {
        GameInfo gameInfo = gameInfos.get(gameId);
        gameInfo.setEnvironment(environment);
        notifyCallbacks(gameId, gameInfo);
    }

    public ActionInfo endTurn(String gameId, String playerName, String action) throws Exception {
        GameInfo gameInfo = gameInfos.get(gameId);
        MoveType moveType = actionService.resolveMoveType(action);

        PlayerInfo actionPlayer = gameInfo.getPlayers().stream().filter(player -> player.getName().equals(playerName)).findFirst().get();

        if (!actionPlayer.isActiveTurn()) {
            throw new IllegalArgumentException("It is not your turn");
        }

        PlayerInfo otherPlayer = gameInfo.getPlayers().stream().filter(player -> !player.getName().equals(playerName)).findFirst().get();

        actionPlayer.setActiveTurn(false);
        otherPlayer.setActiveTurn(true);

        String background = actionPlayer.getBackground();
        String environment = gameInfo.getEnvironment();
        gameInfo.addTurnHistory(action);

        ActionInfo actionInfo;
        if (moveType == MoveType.Heal) {
            actionInfo = actionService.resolveHealAmount(action, background, environment);
            actionPlayer.setHealth(actionPlayer.getHealth() + actionInfo.getHealAmount());
        } else if (moveType == MoveType.Attack) {
            actionInfo = actionService.resolveAttackAmount(action, background, environment);
            int totalDamage = actionInfo.getBaseDmg() + actionInfo.getBackgroundSuitabilityDamage() + actionInfo.getCreativitySuitabilityDamage() + actionInfo.getEnvironmentSuitabilityDamage();
            otherPlayer.setHealth(otherPlayer.getHealth() - totalDamage);
        } else if (moveType == MoveType.Pass) {
            actionInfo = new ActionInfo(MoveType.Pass);
        } else if (moveType == MoveType.Irrelevant) {
            actionInfo = new ActionInfo(MoveType.Irrelevant);
        } else {
            throw new IllegalArgumentException("Invalid move type");
        }

        notifyCallbacks(gameId, gameInfo);
        return actionInfo;
    }

    public List<String> getTurnHistory(String gameId) {
        GameInfo gameInfo = gameInfos.get(gameId);
        return gameInfo.getTurnHistory();
    }

    public GameInfo getGameInfo(String gameId) {
        return gameInfos.get(gameId);
    }

    public void registerCallback(String gameId, Consumer<GameInfo> callback) {
        List<Consumer<GameInfo>> listCallbacks = callbacks.getOrDefault(gameId, new ArrayList<Consumer<GameInfo>>());
        listCallbacks.add(callback);
        System.out.println("GameId: " + gameId + " Callback Size:" + listCallbacks.size());
        callbacks.put(gameId, listCallbacks);
        System.out.println("Callback Added");
    }

    public void notifyCallbacks(String gameId, GameInfo gameInfo) {
        List<Consumer<GameInfo>> callbackss = callbacks.get(gameId);
        if (callbackss != null) {
            System.out.println("Popped Callbacks Size:" + callbackss.size());
            while (!callbackss.isEmpty()) {
                Consumer<GameInfo> callback = callbackss.remove(0);
                if (callback != null) {
                    callback.accept(gameInfo);
                    System.out.println("Callback Invoked");
                }
            }
        }
    }
}