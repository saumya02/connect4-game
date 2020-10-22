package com.game.connect4.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {
    private Long gameId;
    private String player1;
    private String player1Color;
    private String player2;
    private String player2Color;



}
