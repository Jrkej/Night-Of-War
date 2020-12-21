import java.io.IOException;

import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

        //Choose league level
        gameRunner.setLeagueLevel(2);

        //Add players
        gameRunner.addAgent(BasicAgent.class, "Vaulton");
        gameRunner.addAgent(BasicAgent.class, "Jarv");

        //Set game seed
        //gameRunner.setSeed(5842184981578562716L); not neccesary

        //Run game and start viewer on 'http://localhost:8888/'
        gameRunner.start(8888);
    }
}
