package com.game.connect4.Controller;

import com.game.connect4.Dto.GameUsersForm;
import com.game.connect4.Dto.GameResponse;
import com.game.connect4.Service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game/connect4")
public class GameController {

    @Autowired
    GameService gameService;

    @PostMapping("/start")
    public GameResponse startGame(@RequestBody GameUsersForm gameUsersForm){
        if(gameUsersForm.getPlayerName1() == null || gameUsersForm.getPlayerName1().equals("") ||
                gameUsersForm.getPlayerName2()==null || gameUsersForm.getPlayerName2().equals("")){

            return new GameResponse(null,"Enter both user names", HttpStatus.BAD_REQUEST);
        }
        if(gameUsersForm.getPlayerName1().equalsIgnoreCase(gameUsersForm.getPlayerName2())){
            return new GameResponse(null,"Both user name cannot be same",HttpStatus.BAD_REQUEST);
        }
        GameResponse gameResponse= gameService.createGame(gameUsersForm);
        return gameResponse;
    }

    @GetMapping("/gameStatus")
    public GameResponse getGameStatus(@RequestParam(name = "gameId") Long gameId){
        GameResponse gameResponse = gameService.getGameStatus(gameId);
        return gameResponse;
    }

    @GetMapping("/play")
    public GameResponse playGame(@RequestParam(name = "gameId") Long gameId,
                                 @RequestParam(name = "playerName") String playerName,
                                 @RequestParam(name = "columnNo") int columnNo){
        if(columnNo<0 || columnNo>6){
            return new GameResponse(null,"Enter valid column number between 0 to 6",HttpStatus.BAD_REQUEST);
        }

        GameResponse gameResponse = gameService.playGame(gameId,playerName, columnNo);


        return gameResponse;
    }
}
