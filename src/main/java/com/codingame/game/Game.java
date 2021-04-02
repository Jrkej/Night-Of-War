package com.codingame.game;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Game {
	public int MapSize;
	public int TotalSoldiers;
	public int[] scores = {0,0};
	public int CurrPlayerIndex;
	public ArrayList<Soldier> ActiveSoldiers;
	public Block[][] MAP;
	
	private boolean IS_MOVE_PLAYABLE;
	private boolean IS_ATTACK_PLAYABLE;
	private boolean IS_UPGRADE_PLAYABLE;
	private boolean IS_DEGRADE_PLAYABLE;
	private boolean IS_SUICIDE_PLAYABLE;
	
	final Pattern WAIT = Pattern.compile(
	        "^WAIT(?:\\s+(?<message>.*))?",
	        Pattern.CASE_INSENSITIVE
	);
	final Pattern MOVE = Pattern.compile(
			"^MOVE\\s+(?<id>\\d+)\\s+(?<dir>(LEFT|RIGHT|UP|DOWN))"
		            + "(?:\\s+(?<message>.+))?",
	        Pattern.CASE_INSENSITIVE
	);
	final Pattern ATTACK = Pattern.compile(
			"^ATTACK\\s+(?<id>\\d+)\\s+(?<oppId>-?\\d+)"
		            + "(?:\\s+(?<message>.+))?",
	        Pattern.CASE_INSENSITIVE
	);
	final Pattern UPGRADE = Pattern.compile(
	        "^UPGRADE\\s+(?<id>\\d+)(?:\\s+(?<message>.*))?",
	        Pattern.CASE_INSENSITIVE
	);
	final Pattern DEGRADE = Pattern.compile(
	        "^DEGRADE\\s+(?<id>\\d+)(?:\\s+(?<message>.*))?",
	        Pattern.CASE_INSENSITIVE
	);
	final Pattern SUICIDE = Pattern.compile(
	        "^SUICIDE\\s+(?<id>\\d+)(?:\\s+(?<message>.*))?",
	        Pattern.CASE_INSENSITIVE
	);
	private final int MAX_ATTACK_RANGE = 2;
	private final int UPGRADE_COST = 25;
	private final int DEGRADE_COST = 20;
	private final int ATTACK_COST = 35;
	private final int INCOME_PER_BLOCK = 2;
	private final int PROFIT_SUICIDE = 35;

	private static final int MAPSIZE_LEVEL1 = 4;
    private static final int MAPSIZE_LEVEL2 = 5;
    private static final int MAPSIZE_LEVEL3 = 8;
    private static final int TOTALSOLDIERS_LEVEL1 = 1;
    private static final int TOTALSOLDIERS_LEVEL2 = 2;
    private static final int TOTALSOLDIERS_LEVEL3 = 3;
    private static final int TOTAL_PLAYERS = 2;
    private final int MAX_MESSAGE_LENGTH = 17;
    
    String MESSAGE;
    
    public Game(int level) {
    	if (level == 1) {
    		this.IS_MOVE_PLAYABLE = true;
    		this.IS_ATTACK_PLAYABLE = true;
    		this.MapSize = MAPSIZE_LEVEL1;
    		this.TotalSoldiers = TOTALSOLDIERS_LEVEL1;
    	}
    	if (level == 2) {
    		this.IS_MOVE_PLAYABLE = true;
    		this.IS_ATTACK_PLAYABLE = true;
    		this.IS_UPGRADE_PLAYABLE = true;
    		this.MapSize = MAPSIZE_LEVEL2;
    		TotalSoldiers = TOTALSOLDIERS_LEVEL2;
    	}
		if (level >= 3) {
			this.IS_MOVE_PLAYABLE = true;
    		this.IS_ATTACK_PLAYABLE = true;
    		this.IS_UPGRADE_PLAYABLE = true;
    		this.IS_DEGRADE_PLAYABLE = true;
    		this.IS_SUICIDE_PLAYABLE = true;
			this.MapSize = MAPSIZE_LEVEL3;
    		this.TotalSoldiers = TOTALSOLDIERS_LEVEL3;
		}
    }
    
    public void initialise() {
    	this.ActiveSoldiers = new ArrayList<Soldier> ();
    	CurrPlayerIndex = 0;
    	MAP = new Block[this.MapSize][this.MapSize];
    	for (int y = 0; y < this.MapSize; y++) {
    		for (int x = 0; x < this.MapSize; x++) {
    			MAP[x][y] = new Block(x, y);
    		}
    	}
    	int ID = 0;
    	for (int owner = 0; owner < TOTAL_PLAYERS; owner++) {
    		for (int i = 0; i < this.TotalSoldiers; i++) {
    			int x = owner == 0?0:this.MapSize-1;
    			int y = owner == 0?i:this.MapSize-1-i;
    			this.ActiveSoldiers.add(new Soldier(ID, owner, x, y));
    			ID += 1;
    			MAP[x][y].SetOwner(owner);
    		}
    	}
    }
    
    public turnResult play(Player player, String move, String nickname, Animation SDK){
    	boolean recognise = false;
    	turnResult result = new turnResult(nickname);
    	Matcher match;
    	String message = "";
    	match = this.WAIT.matcher(move);
    	if (match.matches()) {
    		recognise =  true;
    		result.add(nickname.toUpperCase() + " WAITED AND DECIDED NOTHING TO DO.");
    		 message = match.group("message");
    	}
    	match = this.MOVE.matcher(move);
    	if(match.matches()) {
    		message = match.group("message");
    		recognise =  true;
    		String dir = match.group("dir");
    		int id = Integer.valueOf(match.group("id"));
    		if (this.is_valid_move(dir,id)) {
    			for (Soldier m: this.ActiveSoldiers) {
    				if (m.soldierId == id) {
    					if (dir.matches("LEFT")) {
    						m.left();
    					}
    					if (dir.matches("RIGHT")) {
    						m.right();
    					}
    					if (dir.matches("UP")) {
    						m.up();
    					}
    					if (dir.matches("DOWN")) {
    						m.down();
    					}
    					result.add(nickname.toUpperCase() + " MOVED SOLDIER OF ID = " + String.valueOf(id) + " TOWARDS " + dir + ".");
    					this.MAP[m.x][m.y].SetOwner(this.CurrPlayerIndex);
    				}
    			}
    		}
    		else {
    			result.add("CANNOT MOVE SOLDIER OF ID " + String.valueOf(id) + " TOWARDS " + dir + ".");
    		}
    	}
    	match = this.ATTACK.matcher(move);
    	if (match.matches()) {
    		message = match.group("message");
    		recognise =  true;
    		int id = Integer.valueOf(match.group("oppId"));
    		int mid = Integer.valueOf(match.group("id"));
    		if (this.is_valid_attack(id, mid)) {
    			for (Soldier m: this.ActiveSoldiers) {
    				if (m.soldierId == id) {
    					m.die();
    					this.scores[this.CurrPlayerIndex] -= this.ATTACK_COST;
    				}
    			}
    			result.add(nickname.toUpperCase() + " ATTACKED ON OPPONENT SOLDIER OF ID " + String.valueOf(id) + " FROM SOLDIER OF ID " + String.valueOf(mid) + "!");
    			result.add_tooltip(nickname + " ATTACKED");
    		}
    		else {
    			result.add("SOLDIER OF ID " + String.valueOf(mid) + " CANNOT ATTACK ON SOLDIER OF ID " + String.valueOf(id) + ".");
    		}
    	}
    	match = this.UPGRADE.matcher(move);
    	if (match.matches()) {
    		message = match.group("message");
    		recognise =  true;
    		int id = Integer.valueOf(match.group("id"));
    		if (this.is_valid_upgrade(id)) {
    			for (Soldier m: this.ActiveSoldiers) {
    				if (m.soldierId == id) {
    					m.upgrade();
    					SDK.upgrade(m.x, m.y);
    					this.scores[this.CurrPlayerIndex] -= this.UPGRADE_COST;
    				}
    			}
    			result.add(nickname.toUpperCase() + " UPGRADED SOLDIER OF ID " + String.valueOf(id) + "!");
    		}
    		else {
    			result.add("CANNOT UPGRADE SOLDIER OF ID " + String.valueOf(id) + ".");
    		}
    		
    	}
    	match = this.DEGRADE.matcher(move);
    	if (match.matches()) {
    		message = match.group("message");
    		recognise =  true;
    		int id = Integer.valueOf(match.group("id"));
    		if (this.is_valid_degrade(id)) {
    			for (Soldier m: this.ActiveSoldiers) {
    				if (m.soldierId == id) {
    					m.degrade();
    					SDK.degrade(m.x, m.y);
    					this.scores[this.CurrPlayerIndex] -= this.DEGRADE_COST;
    				}
    			}
    			result.add(nickname.toUpperCase() + " DEGRADED OPPONENT SOLDIER OF ID " + String.valueOf(id) + "!");
    		}
    		else {
    			result.add("CANNOT DEGRADE OPPONENT SOLDIER OF ID " + String.valueOf(id) + ".");
    		}
    		
    	}
    	match = this.SUICIDE.matcher(move);
    	if (match.matches()) {
    		message = match.group("message");
    		recognise =  true;
    		int id = Integer.valueOf(match.group("id"));
    		if (this.is_valid_suicide(id)) {
    			for (Soldier m: this.ActiveSoldiers) {
    				if (m.soldierId == id) {
    					m.die();
    					this.scores[this.CurrPlayerIndex] += this.PROFIT_SUICIDE;
    				}
    			}
    			result.add_tooltip(nickname + " SUICIDED.");
    			result.add("SOLDIER OF ID " + String.valueOf(id) + " SUICIDED v_v");
    		}
    		else {
    			result.add("SOLDIER OF ID " + String.valueOf(id) + " CANNOT SUICIDE.");
    		}
    		
    	}
    	if (!recognise) {
    		result.add("GOT AN UNRECOGNISED COMMAND DEACTIVATIING PLAYER!");
    		player.setScore(-1);
    		this.scores[this.CurrPlayerIndex] = -1;
    		player.deactivate("GOT AN UNRECOGNISED COMMAND");
    	}
    	if (message == null)
            message = "";
        else if (message.length() > this.MAX_MESSAGE_LENGTH) {
            message = message.substring(0, this.MAX_MESSAGE_LENGTH);
        }
    	this.MESSAGE = message;
    		
    	this.updatescores();
    	return result;
    }
    
    private void updatescores() {
    	int c = 0;
    	for (int x = 0;  x < this.MapSize; x++) {
    		for (int y = 0; y < this.MapSize; y++) {
    			if (this.MAP[x][y].owner == this.CurrPlayerIndex) c += 1;
    		}
    	}
    	if (this.scores[this.CurrPlayerIndex] != -1) this.scores[this.CurrPlayerIndex] += c * this.INCOME_PER_BLOCK;
    }
    public void end() {
    	this.CurrPlayerIndex = 1 - this.CurrPlayerIndex;
    }
    
    private boolean is_valid_move(String dir, int id) {
    	Soldier sold = new Soldier(-1,0,0,0);
    	for (Soldier a: this.ActiveSoldiers) {
    		if (a.soldierId == id) sold = a;
    	}
    	if (sold.alive == 0) return false;
    	if (sold.ownerId == 1 - this.CurrPlayerIndex) return false;
    	if (!this.IS_MOVE_PLAYABLE) return false;
    	if (dir.matches("LEFT") && sold.x > 0 && sold.direction.matches("RIGHT") == false) {
    		for (Soldier s: this.ActiveSoldiers) {
    			if (s.x + 1 == sold.x &&  s.y == sold.y && s.alive == 1) return false;
    		}
    		return true;
    	}
    	if (dir.matches("RIGHT") && sold.x < this.MapSize - 1 && sold.direction.matches("LEFT") == false) {
    		for (Soldier s: this.ActiveSoldiers) {
    			if (s.x - 1 == sold.x && s.y == sold.y && s.alive == 1) return false;
    		}
    		return true;
    	}
    	if (dir.matches("UP") && sold.y > 0 && sold.direction.matches("DOWN") == false) {
    		for (Soldier s: this.ActiveSoldiers) {
    			if (s.y + 1 == sold.y && s.x == sold.x && s.alive == 1) return false;
    		}
    		return true;
    	}
    	if (dir.matches("DOWN") && sold.y < this.MapSize - 1 && sold.direction.matches("UP") == false) {
    		for (Soldier s: this.ActiveSoldiers) {
    			if (s.y - 1 == sold.y && s.x == sold.x && s.alive == 1) return false;
    		}
    		return true;
    	}
    	return false;
    }
    
    private boolean is_valid_attack(int id, int mid) {
    	Soldier sold = new Soldier(0,0,0,0);
    	for (Soldier a: this.ActiveSoldiers) {
    		if (a.soldierId == mid) sold = a;
    	}
    	if (sold.alive == 0) return false;
    	if (!this.IS_ATTACK_PLAYABLE) return false;	
    	for (Soldier s: this.ActiveSoldiers) {
    		if (s.soldierId == id) {
    			if (s.alive == 0) return false;
    			if (s.ownerId == this.CurrPlayerIndex) return false;
    			if (s.level > sold.level) return false;
    			if (Math.abs(s.x - sold.x) + Math.abs(s.y - sold.y) <= this.MAX_ATTACK_RANGE) {
    				if (sold.direction == "UP" && s.y > sold.y) return false;
    				if (sold.direction == "DOWN" && s.y < sold.y) return false;
    				if (sold.direction == "LEFT" && s.x > sold.x) return false;
    				if (sold.direction == "RIGHT" && s.x < sold.x) return false;
    				if (this.scores[this.CurrPlayerIndex] < this.ATTACK_COST) return false;
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    private boolean is_valid_upgrade(int id) {
    	Soldier sold = new Soldier(0,0,0,0);
    	for (Soldier a: this.ActiveSoldiers) {
    		if (a.soldierId == id) sold = a;
    	}
    	if (sold.alive == 0) return false;
    	if (!this.IS_UPGRADE_PLAYABLE) return false;
    	if (sold.level == 10) return false;
    	if (this.scores[this.CurrPlayerIndex] < UPGRADE_COST) return false;
    	if (sold.ownerId == 1-this.CurrPlayerIndex) return false;
    	return true;
    }
    
    private boolean is_valid_degrade(int id) {
    	Soldier sold = new Soldier(0,0,0,0);
    	for (Soldier a: this.ActiveSoldiers) {
    		if (a.soldierId == id) sold = a;
    	}
    	if (sold.alive == 0) return false;
    	if (!this.IS_DEGRADE_PLAYABLE) return false;
    	if (sold.level == 0) return false;
    	if (this.scores[this.CurrPlayerIndex] < DEGRADE_COST) return false;
    	if (sold.ownerId == this.CurrPlayerIndex) return false;
    	for (Soldier s: this.ActiveSoldiers) {
    		if (Math.abs(s.x - sold.x) + Math.abs(s.y - sold.y) <= this.MAX_ATTACK_RANGE) return true;
    	}
    	return false;
    }
    
    private boolean is_valid_suicide(int id) {
    	Soldier sold = new Soldier(0,0,0,0);
    	for (Soldier a: this.ActiveSoldiers) {
    		if (a.soldierId == id) sold = a;
    	}
    	if (sold.alive == 0) return false;
    	if (!this.IS_SUICIDE_PLAYABLE) return false;
    	if (sold.ownerId == 1 - this.CurrPlayerIndex) return false;
    	return true;
    }
    public boolean check_if_game_ended() {
    	int a = 0;
    	int b = 0;
    	for (Soldier c: this.ActiveSoldiers) {
    		if (c.alive == 1 && c.ownerId == 0) a += 1;
    		if (c.alive == 1 && c.ownerId == 1) b += 1;
    	}
    	if (a == 0) {
    		this.scores[0] = -1;
    		return true;
    	}
    	if (b == 0) {
    		this.scores[1] = -1;
    		return true;
    	}
    	return false;
    }
    public ArrayList<String> getPlayerTurnInput() {
    	ArrayList<String> INPUTS = new ArrayList<String> ();
    	INPUTS.add(String.valueOf(this.scores[this.CurrPlayerIndex]));
    	INPUTS.add(String.valueOf(this.scores[1 - this.CurrPlayerIndex]));
    	for (int y = 0; y < this.MapSize; y++) {
    		for (int x = 0; x < this.MapSize; x++) {
    			INPUTS.add(MAP[x][y].data());
    		}
    	}
    	int c = 0;
		for (Soldier s: this.ActiveSoldiers) {
			if (s.alive == 1) c += 1;
		}
		INPUTS.add(String.valueOf(c));
    	for (Soldier soldier: ActiveSoldiers) {
    		if (soldier.alive == 1) INPUTS.add(soldier.data());
    	}
    	return INPUTS;
    }
}