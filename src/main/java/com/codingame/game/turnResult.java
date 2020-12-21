package com.codingame.game;
import java.util.*;

public class turnResult {
	
	ArrayList<String> Summary;
	ArrayList<String> ToolTip;
	String name;

	public turnResult(String nickname) {
		this.name = nickname;
		this.Summary = new ArrayList<String> ();
		this.ToolTip = new ArrayList<String> ();
	}
	public void add(String summ) {
		this.Summary.add(summ);
	}
	public void add_tooltip(String summ) {
		this.ToolTip.add(summ);
	}
}
