package com.lld.SnakeLadderGame.controller;

import com.lld.SnakeLadderGame.dto.CreateGameRequest;
import com.lld.SnakeLadderGame.dto.MoveResponse;
import com.lld.SnakeLadderGame.entity.Game;
import com.lld.SnakeLadderGame.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

  private final GameService gameService;

  @PostMapping("/start")
  public ResponseEntity<Game> startGame(@RequestBody CreateGameRequest request) {
    return ResponseEntity.ok(gameService.createGame(request));
  }

  @PostMapping("/{gameId}/roll")
  public ResponseEntity<MoveResponse> rollDice(@PathVariable String gameId) {
    return ResponseEntity.ok(gameService.playTurn(gameId));
  }

}
