package com.lld.SnakeLadderGame.repo;

import com.lld.SnakeLadderGame.entity.Game;
import java.util.Optional;

public interface GameRepository {

  Game save(Game game);

  Optional<Game> findById(String id);

}
