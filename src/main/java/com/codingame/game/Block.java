package com.codingame.game;

public class Block {
	public int owner;
	public int x;
	public int y;
	
	public Block(int X, int Y) {
		this.owner = -1;
		this.x = X;
		this.y = Y;
	}
	
	public String data() {
		return String.valueOf(this.owner) + " " + String.valueOf(this.x) + " " + String.valueOf(this.y);
	}
	
	public void SetOwner(int ownerId) {
		this.owner = ownerId;
	}
}
