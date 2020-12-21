package com.codingame.game;
import java.util.*;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.endscreen.EndScreenModule;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;

public class Referee extends AbstractReferee {
	
	@Inject private MultiplayerGameManager<Player> gameManager;
	@Inject private GraphicEntityModule graphicEntityModule;
	@Inject
	private EndScreenModule endScreenModule;
	private final int MAX_TURNS = 200;
	private static final int TIME_PER_TURN = 100;
	private static final int FIRST_TURN_TIME = 1000;
	Player[] players = new Player[2];
	Animation SDK;
	Game game;
	@Override
    public void init() {
        // Initialize your game here.
		try {
			gameManager.setMaxTurns(MAX_TURNS);
			gameManager.setTurnMaxTime(TIME_PER_TURN);
			gameManager.setFirstTurnMaxTime(FIRST_TURN_TIME);
			game = new Game(gameManager.getLeagueLevel());
			game.initialise();
			SDK = new Animation(this.graphicEntityModule, game.MapSize, gameManager.getPlayer(0).getColorToken(), gameManager.getPlayer(1).getColorToken(), gameManager.getPlayer(0), gameManager.getPlayer(1));
			SDK.initialise(game);
			
		} catch (Exception e) {
            e.printStackTrace();
            System.err.println("Referee CRASHED! Ending Game");
            abort();
        }
    }
	@Override
    public void gameTurn(int turn) {
		Player cilent = gameManager.getPlayer(game.CurrPlayerIndex);
		Player cilent_opponent = gameManager.getPlayer(1 - game.CurrPlayerIndex);
		SendInputs(cilent, turn);
		cilent.execute();
		try {
            List<String> outputs = cilent.getOutputs();
            turnResult result = game.play(cilent, outputs.get(0), cilent.getNicknameToken());
            cilent.setScore(game.scores[game.CurrPlayerIndex]);
            if (result.Summary.size() > 0) {
            	gameManager.addToGameSummary(result.Summary.get(0));
            }
            if (result.ToolTip.size() > 0) {
            	gameManager.addTooltip(cilent, result.ToolTip.get(0));
            }
            SDK.turn(game);
            if (turn == this.MAX_TURNS || game.check_if_game_ended()) {
            	cilent.setScore(game.scores[game.CurrPlayerIndex]);
            	cilent_opponent.setScore(game.scores[1 - game.CurrPlayerIndex]);
            	SDK.end();
            	gameManager.endGame();
            }
		} catch(TimeoutException e) {
			cilent.setScore(-1);
			game.scores[game.CurrPlayerIndex] = -2;
			cilent.deactivate(cilent.getNicknameToken()+" Ran out of time!");
			SDK.end();
			gameManager.endGame();
		} catch(Exception e) {
			cilent.setScore(-1);
			game.scores[game.CurrPlayerIndex] = -2;
			cilent.deactivate(cilent.getNicknameToken()+" Unexpected output!");
			SDK.end();
			gameManager.endGame();
		}
		game.end();
    }
	
	private void abort() {
        System.err.println("Unexpected Game Ended!");
        gameManager.endGame();
    }
	@Override
	public void onEnd() {
		String a = "WINNER";
		String b = "DEFEATED";
		if (game.scores[1] > game.scores[0]) {
			a = "DEFEATED";
			b = "WINNER";
		}
		if (game.scores[1] == game.scores[0]) {
			a = "TIE";
			b = "TIE";
		}
		String[] Text = {a + " - " + (game.scores[0] > -2?(game.scores[0] > -1?String.valueOf(game.scores[0]):"ARMY EXTINCTED!"):"ERROR!!!"), b + " - " + (game.scores[1] > -2?(game.scores[1] > -1?String.valueOf(game.scores[1]):"ARMY EXTINCTED!"):"ERROR!!!")};
		endScreenModule.setScores(game.scores, Text);
	}
	private void SendInputs(Player cilent, int turn) {
		if (turn <= 2) {
			cilent.sendInputLine(String.valueOf(game.CurrPlayerIndex));
			cilent.sendInputLine(String.valueOf(game.MapSize));
		}
		ArrayList<String> INPUTS = game.getPlayerTurnInput();
		for (String line: INPUTS) {
			cilent.sendInputLine(line);
		}
	}
}
