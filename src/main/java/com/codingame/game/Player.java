package com.codingame.game;
import com.codingame.gameengine.core.AbstractMultiplayerPlayer;

// Uncomment the line below and comment the line under it to create a Solo Game
// public class Player extends AbstractSoloPlayer {
public class Player extends AbstractMultiplayerPlayer {

    @Override
    public int getExpectedOutputLines() {
    	//No of lines of output the player should print 
        return 1;
    }
}
