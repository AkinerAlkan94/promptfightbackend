package com.ozgoygoy.promptfightbackend.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum MoveType {
    Attack, Heal, Pass, Irrelevant
}
