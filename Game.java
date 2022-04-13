
/* Game.java
 * Space Invaders Main Program
 *
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;

public class Game extends Canvas {

	private BufferStrategy strategy; // take advantage of accelerated graphics
	private boolean waitingForKeyPress = true; // true if game held up until

	private final int GAME_WIDTH = 1280; // width of game
	private final int GAME_HEIGHT = 720; // height of game
											// a key is pressed
	// playerOne movement keys
	private boolean leftPressedOne = false; // true if left arrow key currently pressed
	private boolean rightPressedOne = false; // true if right arrow key currently pressed
	private boolean firePressed = false; // true if firing
	private boolean upPressedOne = false; // true if up arrow is pressed
	private boolean bPressed = false; // true if b is pressed

	// playerTwo movement keys
	private boolean leftPressedTwo = false; // true if left arrow key currently pressed
	private boolean rightPressedTwo = false; // true if right arrow key currently pressed
	private boolean upPressedTwo = false; // true if up arrow is pressed

	private boolean gameRunning = true;
	private boolean levelRunning = true;
	private boolean levelCleared = false;
	private boolean playerOneCleared = false;
	private boolean playerTwoCleared = false;
	private int currentLevel = 1;
	private ArrayList entities = new ArrayList(); // list of entities
													// in game
	private ArrayList removeEntities = new ArrayList(); // list of entities
														// to remove this loop
	private Entity playerOne; // first player
	private Entity playerTwo; // second player
	private String playerOneCollisions; // directions of movement that will result in a collision for playerOne
	private String playerTwoCollisions; // directions of movement that will result in a collision for playerTwp
	private double moveSpeed = 100; // hor. vel. of ship (px/s)
	private int alienCount; // # of aliens left on screen
	private int gravity = 100;
	private double initialFuelLevelOne;
	private double initialFuelLevelTwo;

	// dynamic entities
	private Entity goalOne;
	private Entity goalTwo;
	private Entity DeathEntity;

	// tile entities
	private Entity tileStone1;
	private Entity tileStone2;
	private Entity tileStone3;
	private Entity tileStone4;
	private Entity tileStone5;
	private Entity tileStone6;
	private Entity tileStone7;
	private Entity tileStone8;
	private Entity tileStone9;
	private Entity tileStone10;
	private Entity tileStone11;
	private Entity tileStone12;
	private Entity tileStone13;
	private Entity tileStone14;
	private Entity tileStone15;
	private Entity tileStone16;
	private Entity tileStone17;
	private Entity tileStone18;
	private Entity tileStone19;
	private Entity tileStone20;
	private Entity tileStone21;
	private Entity tileStone22;
	private Entity tileStone24;
	private Entity tileStone25;
	private Entity tileStone26;
	private Entity tileStone27;
	private Entity tileStone28;
	private Entity tileStone29;
	private Entity tileStone30;
	private Entity tileStone31;
	private Entity tileStone32;
	private Entity tileStone33;

	private String message = ""; // message to display while waiting
									// for a key press

	private boolean logicRequiredThisLoop = false; // true if logic
													// needs to be
													// applied this loop

	/*
	 * Construct our game and set it running.
	 */
	public Game() {
		// create a frame to contain game
		JFrame container = new JFrame("App Adventure");

		// get hold the content of the frame
		JPanel panel = (JPanel) container.getContentPane();

		// set up the resolution of the game
		panel.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
		panel.setLayout(null);

		// set up canvas size (this) and add to frame
		setBounds(0, 0, GAME_WIDTH, GAME_HEIGHT);
		panel.add(this);

		// Tell AWT not to bother repainting canvas since that will
		// be done using graphics acceleration
		setIgnoreRepaint(true);

		// make the window visible
		container.pack();
		container.setResizable(false);
		container.setVisible(true);

		// if user closes window, shutdown game and jre
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			} // windowClosing
		});

		// add key listener to this canvas
		addKeyListener(new KeyInputHandler());

		// request focus so key events are handled by this canvas
		requestFocus();

		// create buffer strategy to take advantage of accelerated graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();

		// TO DO Create loop here that called a specific initEntities based on the current level
		// then runs gameLoop until the level is completed or they die
		// if they die the level is cleared and re init
		// if they complete the level the level is cleared and the next level is loaded
		while(gameRunning){
			initLevel(currentLevel);
			levelLoop();
			if(levelCleared = true){
				currentLevel++;
			}
			entities.clear();
		}
	} // constructor

	public ArrayList getEntities(){
		return entities;
	}

	private void initLevel(int level){
		switch(level){
			case 1:
				initialFuelLevelOne = 100;
				initialFuelLevelTwo = 100;
				initEntities(level);
				break;
			case 2:
				initialFuelLevelOne = 100;
				initialFuelLevelTwo = 100;
				initEntities(level);
				levelRunning = true;
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				break;
			case 6:
				break;
			case 7:
				break;
			case 8:
				break;
		}
	}

	/*
	 * Notification from a game entity that the logic of the game should be run at
	 * the next opportunity
	 */
	public void updateLogic() {
		logicRequiredThisLoop = true;
	} // updateLogic

	/*
	 * Remove an entity from the game. It will no longer be moved or drawn.
	 */
	public void removeEntity(Entity entity) {
		removeEntities.add(entity);
	} // removeEntity

	/*
	 * Notification that the player has died.
	 */
	public void notifyDeath() {
		message = "You died to a horse, come on your better than that.";
		waitingForKeyPress = true;
	} // notifyDeath

	/*
	 * register a player that has completed the level
	 */
	public void registerComplete(Entity player) {
		if(player == playerOne){
			playerOneCleared = true;
		} else if(player == playerTwo){
			playerTwoCleared = true;
		}

		if(playerOneCleared && playerTwoCleared){
			notifyLevelComplete();
		}
	} // notifyComplete

	// Notification that the a level has been completed
	public void notifyLevelComplete(){
		levelRunning = false;
	}
	/*
	 * Notification than an alien has been killed
	 */
	public void notifyAlienKilled() {
		alienCount--;

		if (alienCount == 0) {
			//notifyWin();
		} // if

		// speed up existing aliens
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = (Entity) entities.get(i);
			if (entity instanceof AlienEntity) {
				// speed up by 2%
				entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.04);
			} // if
		} // for
	} // notifyAlienKilled

	public void tryToJump(Entity player, String collisions) { // check that the character is on the ground then jump
		double fuelLevel = ((PlayerEntity) player).getFuelLevel();
		
		// add fuel check to jump
		if(collisions.contains("top") || fuelLevel <= 0){
			return;
		} else {
			fuelLevel -= 1;
			// update fuel display based on new fuel level
			((PlayerEntity) player).setFuelLevel(fuelLevel);
			player.setVerticalMovement(-200);
		}
	} // tryToJump

	/*
	 * gameLoop input: none output: none purpose: Main game loop. Runs throughout
	 * game play. Responsible for the following activities: - calculates speed of
	 * the game loop to update moves - moves the game entities - draws the screen
	 * contents (entities, text) - updates game events - checks input
	 */
	public void levelLoop() {
		long lastLoopTime = System.currentTimeMillis();

		// keep loop running until game ends
		while (levelRunning) {

			// calc. time since last update, will be used to calculate
			// entities movement
			long delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();

			// get graphics context for the accelerated surface and make it black
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(new Color(0, 0, 0));
			g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

			// move each entity
			if (!waitingForKeyPress) {
				for (int i = 0; i < entities.size(); i++) {
					Entity entity = (Entity) entities.get(i);
					entity.setDelta(delta);
					entity.move();
				} // for
			} // if

			// draw all entities
			for (int i = 0; i < entities.size(); i++) {
				Entity entity = (Entity) entities.get(i);
				entity.draw(g);
			} // for

			// draw fuel tank for p1
			g.setColor(new Color(0, 0, 0));
			(SpriteStore.get()).getSprite("sprites/P1JetPackBar.png").draw(g, 5, 0);
			g.fillRect(18, 7, (int) ((((PlayerEntity) playerOne).getFuelLevel() / initialFuelLevelOne) * 100), 12);

			// draw fuel tank for p2
			g.setColor(Color.pink);
			(SpriteStore.get()).getSprite("sprites/P2JetPackBar.png").draw(g, 1155, 0);
			g.fillRect(1168, 7, (int) ((((PlayerEntity) playerTwo).getFuelLevel() / initialFuelLevelTwo) * 100), 12);

			// brute force collisions, compare every entity
			// against every other entity. If any collisions
			// are detected notify both entities that it has
			// occurred
			for (int i = 0; i < entities.size(); i++) {
				for (int j = i + 1; j < entities.size(); j++) {
					Entity me = (Entity) entities.get(i);
					Entity him = (Entity) entities.get(j);

					if (me.collidesWith(him)) {
						me.collidedWith(him);
						him.collidedWith(me);
					} // if
				} // inner for
			} // outer for

			// remove dead entities
			entities.removeAll(removeEntities);
			removeEntities.clear();

			// run logic if required
			if (logicRequiredThisLoop) {
				for (int i = 0; i < entities.size(); i++) {
					Entity entity = (Entity) entities.get(i);
					entity.doLogic();
				} // for
				logicRequiredThisLoop = false;
			} // if

			// if waiting for "any key press", draw message
			if (waitingForKeyPress) {
				g.setColor(Color.white);
				g.drawString(message, (GAME_WIDTH - g.getFontMetrics().stringWidth(message)) / 2, GAME_HEIGHT / 2 - 50);
				g.drawString("Press any key", (GAME_WIDTH - g.getFontMetrics().stringWidth("Press any key")) / 2, GAME_HEIGHT / 2);
			} // if

			// clear graphics and flip buffer
			g.dispose();
			strategy.show();

			// store potential collsions for player character
			playerOneCollisions = playerOne.willCollideWithSomething(entities);
			playerTwoCollisions = playerTwo.willCollideWithSomething(entities);

			// intial movement logic for playerOne
			playerOne.setHorizontalMovement(0);
			if(playerOneCollisions.contains("bottom") && !(playerOneCollisions.contains("top"))){
				playerOne.setVerticalMovement(0);
			} else {
				playerOne.setVerticalMovement(gravity); // gravity must be equal to jump speed
			}

			// initial movement logic for playerTwo
			playerTwo.setHorizontalMovement(0);
			if(playerTwoCollisions.contains("bottom") && !(playerTwoCollisions.contains("top"))){
				playerTwo.setVerticalMovement(0);
			} else {
				playerTwo.setVerticalMovement(gravity); // gravity must be equal to jump speed
			}

			// respond to playerOne moving character left or right
			if ((leftPressedOne) && (!rightPressedOne) && !(playerOneCollisions.contains("left"))) {
				playerOne.setHorizontalMovement(-moveSpeed);
				playerOne.changeSprite("sprites/leftPlayer.gif");
			} else if ((rightPressedOne) && (!leftPressedOne) && !(playerOneCollisions.contains("right"))) {
				playerOne.setHorizontalMovement(moveSpeed);
				playerOne.changeSprite("sprites/rightPlayer.gif");
			} else if ((!rightPressedOne) && (!leftPressedOne)) {
				playerOne.changeSprite("sprites/blankPlayer.gif");
			}

			// respond to playerTwo moving character left or right
			if ((leftPressedTwo) && (!rightPressedTwo) && !(playerTwoCollisions.contains("left"))) {
				playerTwo.setHorizontalMovement(-moveSpeed);
				playerTwo.changeSprite("sprites/leftPlayer.gif");
			} else if ((rightPressedTwo) && (!leftPressedTwo) && !(playerTwoCollisions.contains("right"))) {
				playerTwo.setHorizontalMovement(moveSpeed);
				playerTwo.changeSprite("sprites/rightPlayer.gif");
			} else if ((!rightPressedTwo) && (!leftPressedTwo)) {
				playerTwo.changeSprite("sprites/blankPlayer.gif");
			}

			// if spacebar pressed, try to fire
			if (firePressed) {
				// tryToFire();
			} // if

			// if up arrow pressed set vetical movement to 50

			if (upPressedOne) {
				tryToJump(playerOne, playerOneCollisions);
			}

			if (upPressedTwo) {
				tryToJump(playerTwo, playerTwoCollisions);
			}

			if (bPressed) {
				// tryToBomb();
			}

			//check if they have completed the level
			/*if(playerOne.getCompletition() && playerTwo.getCompletion()){
				
			}*/

			// pause
			try {
				Thread.sleep(16);
			} catch (Exception e) {
			}

		} // while

	} // gameLoop

	/*
	 * startGame input: none output: none purpose: start a fresh game, clear old
	 * data
	 */
	private void startGame() {
		// clear out any existing entities and initalize a new set
		entities.clear();

		initLevel(1);

		// blank out any keyboard settings that might exist
		leftPressedOne = false;
		rightPressedOne = false;
		firePressed = false;
		upPressedOne = false;
		bPressed = false;
		leftPressedTwo = false;
		rightPressedTwo = false;
		upPressedTwo = false;
	} // startGame

	/*
	 * inner class KeyInputHandler handles keyboard input from the user
	 */
	private class KeyInputHandler extends KeyAdapter {

		private int pressCount = 1; // the number of key presses since
									// waiting for 'any' key press

		/*
		 * The following methods are required for any class that extends the abstract
		 * class KeyAdapter. They handle keyPressed, keyReleased and keyTyped events.
		 */
		public void keyPressed(KeyEvent e) {

			// if waiting for keypress to start game, do nothing
			if (waitingForKeyPress) {
				return;
			} // if

			// respond to move left, right or fire
			if (e.getKeyCode() == KeyEvent.VK_A) {
				leftPressedOne = true;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_D) {
				rightPressedOne = true;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_W) {
				upPressedOne = true;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressedTwo = true;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressedTwo = true;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_UP) {
				upPressedTwo = true;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_B) {
				bPressed = true;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				firePressed = true;
			} // if
		} // keyPressed

		public void keyReleased(KeyEvent e) {
			// if waiting for keypress to start game, do nothing
			if (waitingForKeyPress) {
				return;
			} // if

			// respond to move left, right or fire
			if (e.getKeyCode() == KeyEvent.VK_A) {
				leftPressedOne = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_D) {
				rightPressedOne = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_W) {
				upPressedOne = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressedTwo = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressedTwo = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_UP) {
				upPressedTwo = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				firePressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_B) {
				bPressed = false;
			}

		} // keyReleased

		public void keyTyped(KeyEvent e) {

			// if waiting for key press to start game
			if (waitingForKeyPress) {
				if (pressCount == 1) {
					waitingForKeyPress = false;
					startGame();
					pressCount = 0;
				} else {
					pressCount++;
				} // else
			} // if waitingForKeyPress

			// if escape is pressed, end game
			if (e.getKeyChar() == 27) {
				System.exit(0);
			} // if escape pressed

		} // keyTyped

	} // class KeyInputHandler
	/*
	 * initEntities input: none output: none purpose: Initialise the starting state
	 * of the ship and alien entities. Each entity will be added to the array of
	 * entities in the game.
	 */
	private void initEntities(int level) {
		switch(level){
			case 1:
				// create players and and put in correct location
				playerOne = new PlayerEntity(this, "sprites/blankPlayer.gif", 40, 60, initialFuelLevelOne);
				playerTwo = new PlayerEntity(this, "sprites/blankPLayer.gif", 400, 580, initialFuelLevelTwo);

				// create goal location for each player
				goalOne = new GoalEntity("sprites/blankPlayer.gif", 300, 80, playerOne);
				goalTwo = new GoalEntity("sprites/blankPlayer.gif", 800, 600, playerTwo);

				entities.add(new DeathEntity("sprites/leftPlayer.gif", 100, 600));// death entity

				entities.add(new MovableBlockEntity("sprites/stone.png", 800, 640, this));
				// tip : order in entities is order of drawing(if something needs to be infront put it later)
				for (int i = 0; i < 1280; i += 40){
					tileStone1 = new TileEntity(this, "sprites/stone.png", i, 0, "platform");
					entities.add(tileStone1);
				} // W
				
				tileStone2 = new TileEntity(this, "sprites/stone.png", 1240, 40, "wall");
				entities.add(tileStone2);
				
				tileStone3 = new TileEntity(this, "sprites/stone.png", 1240, 80, "wall");
				entities.add(tileStone3);

				entities.add(new TileEntity(this, "sprites/stone.png", 1240, 120, "wall"));
				
				for (int i = 0; i < 1040; i += 40){
					tileStone5 = new TileEntity(this, "sprites/stone.png", i, 160, "platform");
					entities.add(tileStone5);
				} // W
				tileStone6 = new TileEntity(this, "sprites/stone.png", 1240, 160, "wall");
				entities.add(tileStone6);

				tileStone7 = new TileEntity(this, "sprites/stone.png", 0, 200, "wall");
				entities.add(tileStone7);
				tileStone8 = new TileEntity(this, "sprites/stone.png", 1240, 200, "wall");
				entities.add(tileStone8);

				tileStone9 = new TileEntity(this, "sprites/stone.png", 0, 240, "wall");
				entities.add(tileStone9);
				tileStone10 = new TileEntity(this, "sprites/stone.png", 1240, 240, "wall");
				entities.add(tileStone10);
				
				tileStone11 = new TileEntity(this, "sprites/stone.png", 0, 280, "wall");
				entities.add(tileStone11);
				tileStone12 = new TileEntity(this, "sprites/stone.png", 1240, 280, "wall");
				entities.add(tileStone12);

				tileStone13 = new TileEntity(this, "sprites/stone.png", 0, 320, "wall");
				entities.add(tileStone13);
				for (int i = 200; i < 1280; i += 40){
					tileStone14 = new TileEntity(this, "sprites/stone.png", i, 320, "platform");
					entities.add(tileStone14);
				} // W

				tileStone15 = new TileEntity(this, "sprites/stone.png", 0, 360, "wall");
				entities.add(tileStone15);
				tileStone16 = new TileEntity(this, "sprites/stone.png", 1240, 360, "wall");
				entities.add(tileStone16);

				tileStone17 = new TileEntity(this, "sprites/stone.png", 0, 400, "wall");
				entities.add(tileStone17);
				tileStone18 = new TileEntity(this, "sprites/stone.png", 1240, 400, "wall");
				entities.add(tileStone18);
				
				tileStone19 = new TileEntity(this, "sprites/stone.png", 0, 440, "wall");
				entities.add(tileStone19);
				tileStone20 = new TileEntity(this, "sprites/stone.png", 1240, 440, "wall");
				entities.add(tileStone20);

				for (int i = 0; i < 560; i += 40){
					tileStone21 = new TileEntity(this, "sprites/stone.png", i, 480, "platform");
					entities.add(tileStone21);
				} // W
				tileStone22 = new TileEntity(this, "sprites/stone.png", 1240, 480, "wall");
				entities.add(tileStone22);

				tileStone24 = new TileEntity(this, "sprites/stone.png", 0, 520, "wall");
				entities.add(tileStone24);
				for (int i = 560; i < 1040; i += 40){
					tileStone25 = new TileEntity(this, "sprites/stone.png", i, 520, "platform");
					entities.add(tileStone25);
				} // W
				tileStone26 = new TileEntity(this, "sprites/stone.png", 1240, 520, "wall");
				entities.add(tileStone26);

				tileStone27 = new TileEntity(this, "sprites/stone.png", 0, 560, "wall");
				entities.add(tileStone27);
				tileStone28 = new TileEntity(this, "sprites/stone.png", 1240, 560, "wall");
				entities.add(tileStone28);

				tileStone29 = new TileEntity(this, "sprites/stone.png", 0, 600, "wall");
				entities.add(tileStone29);
				tileStone30 = new TileEntity(this, "sprites/stone.png", 1240, 600, "wall");
				entities.add(tileStone30);
				
				tileStone31 = new TileEntity(this, "sprites/stone.png", 0, 640, "wall");
				entities.add(tileStone31);
				tileStone32 = new TileEntity(this, "sprites/stone.png", 1240, 640, "wall");
				entities.add(tileStone32);

				for (int i = 0; i < 1280; i += 40){
					tileStone33 = new TileEntity(this, "sprites/stone.png", i, 680, "platform");
					entities.add(tileStone33);
				} // W

				entities.add(goalOne);
				entities.add(goalTwo);

				entities.add(playerOne);
				entities.add(playerTwo);
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				break;
			case 6:
				break;
			case 7:
				break;
			case 8:
				break;
		}
	} // initEntities
	/**
	 * Main Program
	 */
	public static void main(String[] args) {
		// instantiate this object
		new Game();
	} // main
} // Game
