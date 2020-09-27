package com.game.connect4.Model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "players")
public class GamePlayer extends BaseModel{

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id", nullable = false)
    private GameModel gameId;

    @Column(nullable = false)
    private String playerName;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String status;


}
