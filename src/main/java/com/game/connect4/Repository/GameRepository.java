package com.game.connect4.Repository;

import com.game.connect4.Model.GameModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends CrudRepository<GameModel,Long> {
    @Override
    Optional<GameModel> findById(Long id);
}
