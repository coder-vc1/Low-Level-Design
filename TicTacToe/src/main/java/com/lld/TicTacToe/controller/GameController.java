package com.lld.TicTacToe.controller;

import com.lld.TicTacToe.dto.ConnectGameRequest;
import com.lld.TicTacToe.dto.GameplayRequest;
import com.lld.TicTacToe.entity.Game;
import com.lld.TicTacToe.service.GameService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

  private final GameService gameService;

  @PostMapping("/start")
  public ResponseEntity<Game> startGame(@RequestBody ConnectGameRequest request) {
    return ResponseEntity.ok(gameService.createGame(request.getPlayer1Name(), request.getPlayer2Name()));
  }

  @PostMapping("/move")
  public ResponseEntity<Game> makeMove(@RequestBody GameplayRequest request) {
    return ResponseEntity.ok(gameService.makeMove(
        request.getGameId(),
        request.getPlayerId(),
        request.getRow(),
        request.getCol()
    ));
  }
}