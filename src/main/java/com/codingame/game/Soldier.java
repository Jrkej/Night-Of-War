package com.codingame.game;

public class Soldier {
	
	int soldierId;
	int ownerId;
	int x;
	int y;
	int level;
	int alive;
	String direction;
	
	public Soldier(int id, int playerId, int spawnX, int spawnY) {
		this.soldierId = id;
		this.ownerId = playerId;
		this.x = spawnX;
		this.y = spawnY;
		this.level = 0;
		this.alive = 1;
		this.direction = playerId == 0?"RIGHT":"LEFT";
	}
	
	private int INT(String dir) {
		if (dir == "UP") return 0;
		if (dir == "LEFT") return 1;
		if (dir == "DOWN") return 2;
		if (dir == "RIGHT") return 3;
		return -1;
	}
	public String data() {
		return String.valueOf(this.ownerId) + " " + String.valueOf(this.x) + " " + String.valueOf(this.y) + " " + String.valueOf(this.soldierId) + " " + String.valueOf(this.level) + " " + String.valueOf(INT(this.direction));
	}
	
	public void upgrade() {
		this.level += 1;
		this.level = Math.min(10, Math.max(this.level, 0));
	}
	
	public void degrade() {
		this.level -= 1;
		this.level = Math.min(10, Math.max(this.level, 0));
	}
	
	public void die() {
		this.alive = 0;
	}
	
	public void left() {
		this.x -= 1;
		this.direction = "LEFT";
	}
	
	public void right() {
		this.x += 1;
		this.direction = "RIGHT";
	}
	
	public void up() {
		this.y -= 1;
		this.direction = "UP";
	}
	
	public void down() {
		this.y += 1;
		this.direction = "DOWN";
	}
	
	public String tooltip() {
		return "SOLDIER\n------------------\nId : " + String.valueOf(this.soldierId) + "\nOwnerId : " + String.valueOf(this.ownerId) + "\nx : " + String.valueOf(this.x) + "\ny : " + String.valueOf(this.y) + "\ndirection : " + this.direction + "\nlevel : " + String.valueOf(this.level);
	}
}
