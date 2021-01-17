package com.codingame.game;

import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.*;

public class Animation {
	
	private final static int SCREEN_WIDTH = 1920;
    private final static int SCREEN_HEIGHT = 1080;
    private final static int BOX_START_X = 460;
    private final static int BOX_START_Y = 40;
    private final static int TEXT_WIDTH = 320;
    private final static int TEXT_HEIGHT = 50;
    private final static int SCORE_WIDTH = 150;
    private final static int SCORE_HEIGHT = 50;
    private final static int PIC_WIDTH = 110;
    private final static int MESS_LINE_WIDTH = 4;
    private final static int SCORE_LINE_WIDTH = 4;
    private final static int TEXT_COLOR = 0xf5ffba;
    private final static int SCORE_COLOR = 0xffe7ba;
    private final static int MSG_TEXT_COLOR = 0x4e2224;
    private final static String FONT_FAMILY = "Dialog";
    private final static String NAME_FONT = "Dialog";
    private final static int FONT_SIZE = 40;
    private final static int FONT_SIZE_TEXT = 20;
    private final static int MARGIN_X = 20;
    private final static int MARGIN_Y = 12;
    private final int RANGE_X = SCREEN_WIDTH - (2 * BOX_START_X);
    private final int RANGE_Y = SCREEN_HEIGHT - (2 * BOX_START_Y);
    private final int LINE_WIDTH = 10;
    private final int LINE_COLOR = 0xff9696;
    private final int BOX_COLOR = 0xffe4b5;
    private String P0;
    private String P1;
    private int CIRCLE_RADIUS = 35;
    private int boardSize;
    private int BlockSizeX;
    private int BlockSizeY;
    private int colorA;
    private int colorB;
    private String AVATAR_A;
    private String AVATAR_B;
    private Sprite logo;
    private Sprite[] soldier = new Sprite[2];
    private Sprite[] player_score_image = new Sprite[2];
    private Sprite[] player_logo = new Sprite[2];
    private Sprite[] soldiers;
    private Rectangle[] player_text = new Rectangle[2];
    private Rectangle[] player_score = new Rectangle[2];
    private Circle[][][] BLOCK_OWNERS;
    private Text[] player_msg = new Text[2];
    private Text[] player_scr = new Text[2];
    private GraphicEntityModule graphics;
    
    public Animation(GraphicEntityModule graphics, int Size, int colora, int colorb, Player player0, Player player1) {
        this.graphics = graphics;
        this.boardSize = Size;
        this.colorA = colora;
        this.colorB = colorb;
        this.BlockSizeX = RANGE_X / this.boardSize;
        this.BlockSizeY = RANGE_Y / this.boardSize;
        this.CIRCLE_RADIUS = Math.min(this.BlockSizeX, this.BlockSizeY) / 3;
        this.AVATAR_A = player0.getAvatarToken();
        this.AVATAR_B = player1.getAvatarToken();
        this.P0 = player0.getNicknameToken();
        this.P1 = player1.getNicknameToken();
    }
    
    public void initialise(Game game) {
    	this.graphics.createSprite().setImage("background.jpg").setBaseWidth(SCREEN_WIDTH).setBaseHeight(SCREEN_HEIGHT);
    	this.graphics.createRectangle().setX(BOX_START_X).setY(BOX_START_Y).setWidth(SCREEN_WIDTH - (2 * BOX_START_X)).setHeight(SCREEN_HEIGHT - (2 * BOX_START_Y)).setFillColor(this.BOX_COLOR);
    	this.create_box();
    	this.save_ownership_flags();
    	this.update_owners_sdk(game);
    	this.player_logo[0] = this.graphics.createSprite().setImage(this.AVATAR_A).setX((BOX_START_X - PIC_WIDTH) / 2).setY(75).setBaseWidth(PIC_WIDTH).setBaseHeight(PIC_WIDTH);
    	this.player_logo[1] = this.graphics.createSprite().setImage(this.AVATAR_B).setX(SCREEN_WIDTH - ((BOX_START_X - PIC_WIDTH) / 2) - PIC_WIDTH).setY(75).setBaseWidth(PIC_WIDTH).setBaseHeight(PIC_WIDTH);
    	this.create_textboxes();
    	this.create_score_displayer();
    	this.create_texts();
    	soldier[0] = this.graphics.createSprite().setImage("S1.png").setX(-10).setY(500);
    	soldier[1] = this.graphics.createSprite().setImage("S2.png").setX(BOX_START_X+970).setY(500);
    	this.generate_soldiers(game);
    	this.create_nameplate();
    	this.logo = this.graphics.createSprite().setImage("logo.png").setBaseWidth(1000).setBaseHeight(500).setX(460).setY(300).setAlpha(0);
    }
    public void turn(Game state) {
    	this.update_opacity(state);
    	this.update_msg_scores(state);
    	this.update_owners_sdk(state);
    	this.update_soldiers(state);
    }
    public void end() {
    	this.logo.setAlpha(1);
    	this.soldier[0].setAlpha(.5);
    	this.soldier[1].setAlpha(.5);
    }
    private void save_ownership_flags() {
    	this.BLOCK_OWNERS = new Circle[2][this.boardSize][this.boardSize];
    	for (int x = 0; x < this.boardSize; x++) {
    		for (int y = 0; y < this.boardSize; y++) {
    			BLOCK_OWNERS[0][x][y] = this.graphics.createCircle().setX(BOX_START_X + (x * this.BlockSizeX) + (this.BlockSizeX/2)).setY(BOX_START_Y + (y * this.BlockSizeY) + (this.BlockSizeY/2)).setFillColor(this.colorA).setRadius(this.CIRCLE_RADIUS).setAlpha(0);
    			BLOCK_OWNERS[1][x][y] = this.graphics.createCircle().setX(BOX_START_X + (x * this.BlockSizeX) + (this.BlockSizeX/2)).setY(BOX_START_Y + (y * this.BlockSizeY) + (this.BlockSizeY/2)).setFillColor(this.colorB).setRadius(this.CIRCLE_RADIUS).setAlpha(0);
    		}
    	}
    }
    
    private void create_box() {
    	for (int x = 0; x < this.boardSize + 1; x++) {
    		for (int y = 0; y < this.boardSize + 1; y++) {
    			this.graphics.createLine().setX(BOX_START_X).setY(BOX_START_Y + (y * this.BlockSizeY)).setX2(BOX_START_X + this.RANGE_X).setLineWidth(this.LINE_WIDTH).setLineColor(this.LINE_COLOR).setY2(BOX_START_Y + (y * this.BlockSizeY));
    			this.graphics.createLine().setX(BOX_START_X + (x * this.BlockSizeX)).setY(BOX_START_Y).setX2(BOX_START_X + (x * this.BlockSizeX)).setLineWidth(this.LINE_WIDTH).setLineColor(this.LINE_COLOR).setY2(BOX_START_Y + this.RANGE_Y);
    		}
    	}
    }
    
    private void update_owners_sdk(Game state) {
    	for (int x = 0; x < this.boardSize; x++) {
    		for (int y = 0; y < this.boardSize; y++) {
    			if (state.MAP[x][y].owner != -1) {
    				this.BLOCK_OWNERS[state.MAP[x][y].owner][x][y].setAlpha(1);
    				this.BLOCK_OWNERS[1 - state.MAP[x][y].owner][x][y].setAlpha(0);
    			}
    		}
    	}
    }
    
    private void create_score_displayer() {
    	this.player_score_image[0] = this.graphics.createSprite().setImage("money.png").setX(((BOX_START_X - SCORE_WIDTH) / 2) - 95).setY(220).setBaseWidth(SCORE_WIDTH).setBaseHeight(SCORE_HEIGHT);
    	this.player_score_image[1] = this.graphics.createSprite().setImage("money.png").setX(SCREEN_WIDTH - ((BOX_START_X - SCORE_WIDTH) / 2) + 70 - TEXT_WIDTH).setY(220).setBaseWidth(SCORE_WIDTH).setBaseHeight(SCORE_HEIGHT);
    	this.player_score[0] = this.graphics.createRectangle().setX(((BOX_START_X - SCORE_WIDTH) / 2)+70).setY(220).setWidth(SCORE_WIDTH).setHeight(SCORE_HEIGHT).setFillColor(SCORE_COLOR).setLineColor(this.colorA).setLineWidth(SCORE_LINE_WIDTH);
    	this.player_score[1] = this.graphics.createRectangle().setX(SCREEN_WIDTH - ((BOX_START_X - SCORE_WIDTH) / 2) + 70 - SCORE_WIDTH).setY(220).setWidth(SCORE_WIDTH).setHeight(SCORE_HEIGHT).setFillColor(SCORE_COLOR).setLineColor(this.colorB).setLineWidth(SCORE_LINE_WIDTH);
    }
    
    private void create_textboxes() {
    	this.player_text[0] = this.graphics.createRectangle().setX((BOX_START_X - TEXT_WIDTH) / 2).setY(300).setWidth(TEXT_WIDTH).setHeight(TEXT_HEIGHT).setFillColor(TEXT_COLOR).setLineColor(this.colorA).setLineWidth(MESS_LINE_WIDTH);
    	this.player_text[1] = this.graphics.createRectangle().setX(SCREEN_WIDTH - ((BOX_START_X - TEXT_WIDTH) / 2) - TEXT_WIDTH).setY(300).setWidth(TEXT_WIDTH).setHeight(TEXT_HEIGHT).setFillColor(TEXT_COLOR).setLineColor(this.colorB).setLineWidth(MESS_LINE_WIDTH);
    }
    
    private void update_opacity(Game state) {
    	this.player_logo[state.CurrPlayerIndex].setAlpha(1);
    	this.player_logo[1 - state.CurrPlayerIndex].setAlpha(.4);
    	this.player_text[state.CurrPlayerIndex].setAlpha(1);
    	this.player_text[1 - state.CurrPlayerIndex].setAlpha(.4);
    	this.player_score[state.CurrPlayerIndex].setAlpha(1);
    	this.player_score[1 - state.CurrPlayerIndex].setAlpha(.4);
    	this.player_score_image[state.CurrPlayerIndex].setAlpha(1);
    	this.player_score_image[1 - state.CurrPlayerIndex].setAlpha(.4);
    	this.player_scr[state.CurrPlayerIndex].setAlpha(1);
    	this.player_scr[1 - state.CurrPlayerIndex].setAlpha(.4);
    	this.player_msg[state.CurrPlayerIndex].setAlpha(1);
    	this.player_msg[1 - state.CurrPlayerIndex].setAlpha(.4);
    }
    
    private void update_msg_scores(Game state) {
    	this.player_msg[state.CurrPlayerIndex].setText(state.MESSAGE);
    	this.player_msg[1 - state.CurrPlayerIndex].setText(state.MESSAGE);
    	this.player_scr[0].setText(String.valueOf(state.scores[0]));
    	this.player_scr[1].setText(String.valueOf(state.scores[1]));
    }
    
    private void create_texts() {
    	this.player_msg[0] = this.graphics.createText("").setFillColor(MSG_TEXT_COLOR).setFontSize(FONT_SIZE_TEXT).setX(((BOX_START_X - TEXT_WIDTH) / 2) + MARGIN_X).setY(300 + MARGIN_Y).setFontFamily(FONT_FAMILY);
    	this.player_msg[1] = this.graphics.createText("").setFillColor(MSG_TEXT_COLOR).setFontSize(FONT_SIZE_TEXT).setX((SCREEN_WIDTH - ((BOX_START_X - TEXT_WIDTH) / 2) - TEXT_WIDTH) + MARGIN_X).setY(300 + MARGIN_Y).setFontFamily(FONT_FAMILY);
    	this.player_scr[0] = this.graphics.createText("").setFillColor(this.colorA).setFontSize(FONT_SIZE).setX((((BOX_START_X - SCORE_WIDTH) / 2) + (MARGIN_X / 2))+70).setY(220 + (MARGIN_Y / 2)).setFontFamily(FONT_FAMILY);
    	this.player_scr[1] = this.graphics.createText("").setFillColor(this.colorB).setFontSize(FONT_SIZE).setX((SCREEN_WIDTH - ((BOX_START_X - SCORE_WIDTH) / 2) + (MARGIN_X / 2)) + 70 - SCORE_WIDTH).setY(220 + (MARGIN_Y / 2)).setFontFamily(FONT_FAMILY);
    }
    
    private void create_nameplate() {
    	this.graphics.createText(this.P0.toUpperCase()).setFillColor(this.colorA).setFontFamily(NAME_FONT).setFontSize(60).setX((BOX_START_X - PIC_WIDTH) / 2).setY(5);
    	this.graphics.createText(this.P1.toUpperCase()).setFillColor(this.colorB).setFontFamily(NAME_FONT).setFontSize(60).setX(SCREEN_WIDTH - ((BOX_START_X - PIC_WIDTH) / 2) - PIC_WIDTH).setY(5);
    }
    private void update_soldiers(Game game) {
    	for (int i = 0; i < game.ActiveSoldiers.size(); i++) {
    		soldiers[i].setImage("S" + (game.ActiveSoldiers.get(i).ownerId+1) + "_" + game.ActiveSoldiers.get(i).direction + ".png");
    		if (game.ActiveSoldiers.get(i).alive == 0) soldiers[i].setAlpha(0);
    		else soldiers[i].setX(BOX_START_X + (game.ActiveSoldiers.get(i).x * this.BlockSizeX)).setY(BOX_START_Y + (game.ActiveSoldiers.get(i).y * this.BlockSizeY)).setBaseWidth(this.BlockSizeX).setBaseHeight(this.BlockSizeY);
    	}
    }
    private void generate_soldiers(Game game) {
    	this.soldiers = new Sprite[game.ActiveSoldiers.size()];
    	for (int i = 0; i < game.ActiveSoldiers.size(); i++) {
    		soldiers[i] = this.graphics.createSprite().setImage("S" + (game.ActiveSoldiers.get(i).ownerId+1) + "_" + game.ActiveSoldiers.get(i).direction + ".png").setX(BOX_START_X + (game.ActiveSoldiers.get(i).x * this.BlockSizeX)).setY(BOX_START_Y + (game.ActiveSoldiers.get(i).y * this.BlockSizeY)).setBaseWidth(this.BlockSizeX).setBaseHeight(this.BlockSizeY);
    	}
    }
}
