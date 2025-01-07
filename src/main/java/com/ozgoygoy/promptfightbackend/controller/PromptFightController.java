package com.ozgoygoy.promptfightbackend.controller;

import com.ozgoygoy.promptfightbackend.model.ActionInfo;
import com.ozgoygoy.promptfightbackend.model.GameInfo;
import com.ozgoygoy.promptfightbackend.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@CrossOrigin(origins = "*")
public class PromptFightController {
    private final GameService gameService;

    @Autowired
    public PromptFightController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping(value = "/hello", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello World");
    }

    @PostMapping(value = "/join-game", produces = APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<GameInfo> joinGame(@RequestBody Map<String, String> request) {
        String playerName = request.get("playerName");
        GameInfo gameInfo = gameService.joinGame(playerName);
        return ResponseEntity.ok(gameInfo);
    }

    @PostMapping(value = "/set-player-background", produces = APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> setPlayerBackground(@RequestBody Map<String, String> request) {
        String gameId = request.get("gameId");
        String playerName = request.get("playerName");
        String background = request.get("background");
        gameService.setPlayerBackground(gameId, playerName, background);
        gameService.notifyCallbacks(gameId, gameService.getGameInfo(gameId));
        return ResponseEntity.ok("Player Background Set");
    }

    @PostMapping(value = "/set-game-environment", produces = APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> setGameEnvironment(@RequestBody Map<String, String> request) {
        String gameId = request.get("gameId");
        String environment = request.get("environment");
        gameService.setEnvironment(gameId, environment);
        gameService.notifyCallbacks(gameId, gameService.getGameInfo(gameId));
        return ResponseEntity.ok("Game Environment Set");
    }

    @PostMapping(value = "/end-turn", produces = APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<ActionInfo> endTurn(
            @RequestBody Map<String, String> request) throws Exception {
        String gameId = request.get("gameId");
        String playerName = request.get("playerName");
        String action = request.get("action");

        ActionInfo actionInfo = gameService.endTurn(gameId, playerName, action);
        this.gameService.notifyCallbacks(gameId, gameService.getGameInfo(gameId));
        return ResponseEntity.ok(actionInfo);
    }

    @GetMapping(value = "/turn-history", produces = APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<List<String>> getTurnHistory(
            @RequestBody Map<String, String> request) {
        String gameId = request.get("gameId");

        List<String> turnHistory = gameService.getTurnHistory(gameId);
        return ResponseEntity.ok(turnHistory);
    }

    @GetMapping(value = "/game-info/{gameId}", produces = APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<GameInfo> getGameInfo(
            @PathVariable String gameId) {

        GameInfo gameInfo = gameService.getGameInfo(gameId);
        return ResponseEntity.ok(gameInfo);
    }

    @GetMapping(value = "/long-polling-game-info/{gameId}", produces = APPLICATION_JSON_VALUE)
    public @ResponseBody DeferredResult<ResponseEntity<GameInfo>> getGameInfoLongPolling(
            @PathVariable String gameId) {

        DeferredResult<ResponseEntity<GameInfo>> deferredResult = new DeferredResult<>(60000L); // Timeout after 60 seconds

        gameService.registerCallback(gameId, newGameInfo -> {
            deferredResult.setResult(ResponseEntity.ok(newGameInfo));
        });

        deferredResult.onTimeout(() -> {
            GameInfo gameInfo = gameService.getGameInfo(gameId);
            gameService.notifyCallbacks(gameId, gameInfo);
            deferredResult.setResult(ResponseEntity.ok(gameInfo));
        });
        return deferredResult;
    }
}
