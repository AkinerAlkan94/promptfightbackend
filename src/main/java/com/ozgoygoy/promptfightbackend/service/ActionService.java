package com.ozgoygoy.promptfightbackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ozgoygoy.promptfightbackend.model.ActionInfo;
import com.ozgoygoy.promptfightbackend.model.MoveType;
import com.ozgoygoy.promptfightbackend.service.helper.OpenRouterAccessor;
import org.springframework.stereotype.Service;

@Service
public class ActionService {

    private final OpenRouterAccessor openRouterService;

    public ActionService(OpenRouterAccessor openRouterService) {
        this.openRouterService = openRouterService;
    }

    public MoveType resolveMoveType(String action) throws Exception {
        if (action == null || action.equalsIgnoreCase("pass")) {
            return MoveType.Pass;
        }

        String instruction = """
                There could be possibly two types of move. These are: ['Attack', 'Heal'].
                What is the type of move based on the following information:
                Only respond as given options ['Attack','Heal','Irrelevant']
                Information:
                %s
                """;

        String prompt = String.format(instruction, action);
        String moveType = openRouterService.callOpenRouter(prompt);
        if (moveType.equalsIgnoreCase("attack")) {
            return MoveType.Attack;
        } else if (moveType.equalsIgnoreCase("heal")) {
            return MoveType.Heal;
        } else {
            return MoveType.Irrelevant;
        }
    }

    public ActionInfo resolveHealAmount(String action, String background, String environment) throws Exception {
        String instruction = """
                Healing move can max heal the player as 30HP (Health Point). But this healing amount should be decided based on Creativity of the move, Environment Suitability of the move and suitability of this action to the character Background of the move.
                If the suitabilities and creativeness are high, then the healing amount should be high as well.
                Only respond as numeric value from 0-30 based on the following information:
                Information:
                Action: %s
                Character Background: %s
                Environment Scenery: %s
                """;

        String prompt = String.format(instruction, action, background, environment);
        String response = openRouterService.callOpenRouter(prompt);
        ActionInfo actionInfo = new ActionInfo(MoveType.Heal);
        actionInfo.setHealAmount(Integer.parseInt(response));
        return actionInfo;

    }

    public ActionInfo resolveAttackAmount(String action, String background, String environment) throws Exception {
        String instruction = """
                In each turn attack move can max inflict 10 DMG (Damage) at as base damage.
                As extra to the given base attack damage value extra damage will be added based on the following conditions: Creativity, Environment Suitability, Character Background Suitability
                Creativity: This is decided based on the described attack move. If the described attack move is unique and original rather than common gaming attack moves it has higher value. This creativity extra damage can be ['low','medium','high','extreme']
                Environment Suitability: This is decided based on the described attack move. If the described attack move is suiting with the environment scene setup and even complimenting it, it has higher value. This environment suitability extra damage can be ['low','medium','high','extreme']
                Character Background Suitability: This is decided based on the described attack move. If the described attack move is suiting with the character background and even complimenting it, it has higher value. This character background suitability extra damage can be ['low','medium','high','extreme']
                Only respond as following json format:
                {
                    "baseDamage": 10,
                    "creativityExtraDamage": "low",
                    "environmentExtraDamage": "low",
                    "backgroundExtraDamage": "low"
                }

                Here is the following informations for you to decide damage values.
                    Information:
                    Action: %s
                    Character Background: %s
                    Environment Scenery: %s
                """;

        String prompt = String.format(instruction, action, background, environment);
        String response = openRouterService.callOpenRouter(prompt);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);

        int baseDamage = jsonNode.get("baseDamage").asInt();
        String creativityExtraDamage = jsonNode.get("creativityExtraDamage").asText();
        String environmentExtraDamage = jsonNode.get("environmentExtraDamage").asText();
        String backgroundExtraDamage = jsonNode.get("backgroundExtraDamage").asText();

        ActionInfo actionInfo = new ActionInfo(MoveType.Attack);
        actionInfo.setBaseDmg(baseDamage);
        actionInfo.setCreativitySuitabilityDamage(parseDamage(creativityExtraDamage));
        actionInfo.setEnvironmentSuitabilityDamage(parseDamage(environmentExtraDamage));
        actionInfo.setBackgroundSuitabilityDamage(parseDamage(backgroundExtraDamage));

        return actionInfo;
    }

    private int parseDamage(String damageLevel) {
        return switch (damageLevel.toLowerCase()) {
            case "low" -> 1;
            case "medium" -> 2;
            case "high" -> 3;
            case "extreme" -> 4;
            default -> 0;
        };
    }
}