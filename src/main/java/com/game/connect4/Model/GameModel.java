package com.game.connect4.Model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "game_connect4")
public class GameModel extends BaseModel{

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "gameId")
    private List<GamePlayer> gamePlayer = new ArrayList<>();

    private String gameBoard[][] = new String[][]{{"_","_","_","_","_","_","_"},
                                                {"_","_","_","_","_","_","_"},
                                                {"_","_","_","_","_","_","_"},
                                                {"_","_","_","_","_","_","_"},
                                                {"_","_","_","_","_","_","_"},
                                                {"_","_","_","_","_","_","_"}};

    private String gameStatus = "Ready";

    private int lastColumn = -1;

    private int lastRow = -1;





}
