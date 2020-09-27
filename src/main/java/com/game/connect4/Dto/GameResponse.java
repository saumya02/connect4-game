package com.game.connect4.Dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class GameResponse<T> {

    private T response;
    private String message;
    private int status;

    public GameResponse(T response, String message, HttpStatus status) {
        this.response = response;
        this.message = message;
        this.status = status.value();
    }
}
