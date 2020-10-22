package com.game.connect4.Repository;

import com.game.connect4.Model.GamePlayer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PlayerRepository extends CrudRepository<GamePlayer,Long> {
   GamePlayer findByPlayerName(String playerName);
}
