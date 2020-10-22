package com.game.connect4.Service;

import com.game.connect4.Dto.*;
import com.game.connect4.Model.GameModel;
import com.game.connect4.Model.GamePlayer;
import com.game.connect4.Dto.GameResponse;
import com.game.connect4.Repository.GameRepository;
import com.game.connect4.Repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {
    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    GameRepository gameRepository;

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    public GameResponse createGame(GameUsersForm gameUsersForm) {

        GamePlayer gamePlayer1 = playerRepository.findByPlayerName(gameUsersForm.getPlayerName1());
        if(gamePlayer1!=null){

            return new GameResponse(null,"PlayerName1 alraedy Exists.Try with another name", HttpStatus.BAD_REQUEST);
        }

        GamePlayer gamePlayer2 = playerRepository.findByPlayerName(gameUsersForm.getPlayerName2());
        if(gamePlayer2!=null){
            return new GameResponse(null,"PlayerName2 alraedy Exists.Try with another name", HttpStatus.BAD_REQUEST);
        }
        GameModel game = new GameModel();

        GamePlayer player1 = new GamePlayer();
        player1.setPlayerName(gameUsersForm.getPlayerName1());
        player1.setColor("Yellow");
        player1.setStatus("In game");
        player1.setGameId(game);

        GamePlayer player2 = new GamePlayer();
        player2.setPlayerName(gameUsersForm.getPlayerName2());
        player2.setColor("Red");
        player2.setStatus("In game");
        player2.setGameId(game);

        game.getGamePlayer().add(player1);
        game.getGamePlayer().add(player2);

        GameModel savedGame = gameRepository.save(game);
        playerRepository.save(player1);
        playerRepository.save(player2);

        Response response = new Response();
        response.setGameId(savedGame.getId());
        response.setPlayer1(player1.getPlayerName());
        response.setPlayer1Color(player1.getColor());
        response.setPlayer2(player2.getPlayerName());
        response.setPlayer2Color(player2.getColor());


        return new GameResponse(response,"Ready",HttpStatus.OK);
    }

    public GameResponse getGameStatus(Long gameId) {
        Optional<GameModel> gameModel = gameRepository.findById(gameId);
        if(!gameModel.isPresent()){
            return new GameResponse<GameStatus>(null,"No game present with the given game Id",HttpStatus.BAD_REQUEST);
        }
        GameModel game = gameModel.get();
        String board[][] = game.getGameBoard();
        GameStatus status = new GameStatus();
        status.setBoard(Arrays.deepToString(board));
        logger.info(status.getBoard());
        return new GameResponse<>(status,game.getGameStatus(),HttpStatus.OK);
    }

    public GameResponse playGame(Long gameId, String playerName, int columnNo) {
       Optional<GameModel> gameModel = gameRepository.findById(gameId);
       if(!gameModel.isPresent()){
           return new GameResponse(null,"No game present with the given game Id",HttpStatus.BAD_REQUEST);
       }
       GameModel game = gameModel.get();
       String gameBoard [][] = game.getGameBoard();
       String firstRowBoard[] = gameBoard[0];
       boolean matchDrawFlag = true;
       for(String rowElement : firstRowBoard){
           if(rowElement.equals("_")){
               matchDrawFlag = false;
               break;
           }
       }
       boolean userNameFlag = false;
       List<GamePlayer> gamePlayers = game.getGamePlayer();
       GamePlayer player = null;
       for(GamePlayer gamePlayer : gamePlayers){
           if(playerName.equals(gamePlayer.getPlayerName())){
               player = gamePlayer;
               userNameFlag = true;
               break;
           }
       }
       if(!userNameFlag){
           return new GameResponse(null,"Invalid move !!! No Player present with the given Player Name for " + gameId + " Game Id",HttpStatus.BAD_REQUEST);
       }
       if(game.getGameStatus().equalsIgnoreCase("Completed")){
           return new GameResponse(null,"Invalid move !!! Game already completed",HttpStatus.BAD_REQUEST);
       }
       if(game.getGameStatus().equalsIgnoreCase("DRAW")){
           return new GameResponse(null,"Match Draw !!! All rows are full",HttpStatus.OK);

       }
       if(player.getColor().equals(getLastPlayedColor(game))){
           return new GameResponse(null,"Invalid move !!! Game Should be played by another player",HttpStatus.BAD_REQUEST);
       }
       if(matchDrawFlag){
           GamePlayer player1 = gamePlayers.get(0);
           player1.setStatus("DRAW");
           GamePlayer player2 = gamePlayers.get(1);
           player2.setStatus("DRAW");
           game.setGameStatus("DRAW");
           gameRepository.save(game);
           playerRepository.save(player1);
           playerRepository.save(player2);
           return new GameResponse(null,"Match Draw !!! All rows are full",HttpStatus.OK);
       }
       return dropCoin(game,player,columnNo);

    }

    private GameResponse dropCoin(GameModel game, GamePlayer player, int columnNo) {
        String gameBoard[][] = game.getGameBoard();
        if(!gameBoard[0][columnNo].equals("_")){
            return new GameResponse(null,"Invalid Move",HttpStatus.BAD_REQUEST);
        }
        for (int i = 5; i >= 0; i--) {
            if (gameBoard[i][columnNo].equals("_")) {
                gameBoard[i][columnNo] = player.getColor();
                game.setGameBoard(gameBoard);
                game.setLastRow(i);
                game.setLastColumn(columnNo);
                break;
            }
        }
        game = findWinnerPlayer(game);
        gameRepository.save(game);

        List<GamePlayer> gamePlayers = game.getGamePlayer();
        GamePlayer nextPlayer = null;
        for(GamePlayer players : gamePlayers){
            if(!players.getPlayerName().equals(player.getPlayerName())){
                nextPlayer = players;
                break;
            }
        }
        if(game.getGameStatus().equalsIgnoreCase("Completed")){
            player.setStatus("winner");
            nextPlayer.setStatus("lost");
            playerRepository.save(player);
            playerRepository.save(nextPlayer);
            GameCompletedResponse gameCompletedResponse = new GameCompletedResponse();
            gameCompletedResponse.setGameId(game.getId());
            gameCompletedResponse.setWinner(player.getPlayerName());
            gameCompletedResponse.setColor(player.getColor());
            return new GameResponse(gameCompletedResponse,"Player "+player.getPlayerName()+" won the game",HttpStatus.OK);
        }

        OnGoingGameResponse onGoingGameResponse = new OnGoingGameResponse();
        onGoingGameResponse.setGameid(game.getId());
        onGoingGameResponse.setPlayerName(player.getPlayerName());
        onGoingGameResponse.setNextTurnPlayerName(nextPlayer.getPlayerName());
        return new GameResponse(onGoingGameResponse,"Valid move !!! Player "+ player.getPlayerName()+" drop coin in column "+columnNo,HttpStatus.OK);

    }

    private GameModel findWinnerPlayer(GameModel game) {
        if(checkInRowWise(game) || chackInColumnWise(game) || checkInForwardDiagonal(game)
                || checkInReverseDiagonal(game)){
            game.setGameStatus("Completed");
        }else{
            game.setGameStatus("On Going");
        }
        return game;

    }

    private boolean checkInReverseDiagonal(GameModel game) {
        int startX = game.getLastRow();
        int startY = game.getLastColumn();
        for (int i = 0; i < 3; i++) {
            if (startX == 0 || startY == 6) {
                break;
            }
            startX--;
            startY++;
        }

        int endX = game.getLastRow();
        int endY = game.getLastColumn();
        for (int i = 0; i < 3; i++) {
            if (endX == 5 || endY == 0) {
                break;
            }
            endX++;
            endY--;
        }
        String gameBoard[][]= game.getGameBoard();
        int count = 0;
        for (int i = startX, j = startY; i <= endX || j >= endY; i++, j--) {
            if (getLastPlayedColor(game).equals(gameBoard[i][j])) {
                count++;
                if (count == 4) {
                    return true;
                }
            } else {
                count = 0;
            }
        }
        return false;
    }

    private boolean checkInForwardDiagonal(GameModel game) {
        int startX = game.getLastRow();
        int startY = game.getLastColumn();
        for (int i = 0; i < 3; i++) {
            if (startX == 0 || startY == 0) {
                break;
            }
            startX--;
            startY--;
        }

        int endX = game.getLastRow();
        int endY = game.getLastColumn();
        for (int i = 0; i < 3; i++) {
            if (endX == 5 || endY == 6) {
                break;
            }
            endX++;
            endY++;
        }
        String gameBoard[][]= game.getGameBoard();
        int count = 0;
        for (int i = startX, j = startY; i <= endX || j <= endY; i++, j++) {
            if (getLastPlayedColor(game).equals(gameBoard[i][j])) {
                count++;
                if (count == 4) {
                    return true;
                }
            } else {
                count = 0;
            }
        }
        return false;
    }


    private boolean chackInColumnWise(GameModel game) {
        String gameBoard[][]= game.getGameBoard();
        int start = (game.getLastRow() - 3) > 0 ? game.getLastRow() - 3 : 0;
        int end = (game.getLastRow() + 3) < 6 ? game.getLastRow() + 3 : 5;
        int count = 0;
        for (int i = start; i <= end; i++) {
            if (getLastPlayedColor(game).equals(gameBoard[i][game.getLastColumn()])) {
                count++;
                if (count == 4) {
                    return true;
                }
            } else {
                count = 0;
            }
        }
        return false;
    }


    private boolean checkInRowWise(GameModel game) {
        String gameBoard[][]= game.getGameBoard();
        int start = (game.getLastColumn() - 3) > 0 ? game.getLastColumn() - 3 : 0;
        int end = (game.getLastColumn() + 3) < 6 ? game.getLastColumn() + 3 : 6;
        int count = 0;
        for (int i = start; i <= end; i++) {
            if (getLastPlayedColor(game).equals(gameBoard[game.getLastRow()][i])) {
                count++;
                if (count == 4) {
                    return true;
                }
            } else {
                count = 0;
            }
        }
        return false;
    }



    private String getLastPlayedColor(GameModel game) {
        if(game.getLastColumn() == -1 || game.getLastRow() == -1){
            return game.getGamePlayer().get(1).getColor();
        }
        String board[][] = game.getGameBoard();
        String color = board[game.getLastRow()][game.getLastColumn()];
        return color;
    }


}
