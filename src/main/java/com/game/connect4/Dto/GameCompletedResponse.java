package com.game.connect4.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameCompletedResponse {
    private Long gameId;
    private String winner;
    private String color;
}
