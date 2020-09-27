package com.game.connect4.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnGoingGameResponse {
    private Long gameid;
    private String playerName;
    private String nextTurnPlayerName;
}
