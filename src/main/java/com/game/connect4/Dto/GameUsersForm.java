package com.game.connect4.Dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class GameUsersForm {

    private String playerName1;
    private String playerName2;

}
