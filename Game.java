
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
	private ArrayList entities = new ArrayList(); // list of entities
													// in game
	private ArrayList removeEntities = new ArrayList(); // list of entities
														// to remove this loop
	private Entity playerOne; // first player
	private Entity playerTwo; // second player
	private double moveSpeed = 100; // hor. vel. of ship (px/s)
	private long lastFire = 0; // time last shot fired
	private long firingInterval = 301; // interval between shots (ms)
	private long lastJump = 0; // time last jump was activated
	private long jumpingInterval = 3500; // interval between jumps (ms)
	private long lastBomb = 0; // time last bomb was activated
	private long bombingInterval = 3000; // interval between bombs (ms)
	private int alienCount; // # of aliens left on screen

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

		// initialize entities
		initEntities();

		// start the game
		gameLoop();
	} // constructor

	/*
	 * initEntities input: none output: none purpose: Initialise the starting state
	 * of the ship and alien entities. Each entity will be added to the array of
	 * entities in the game.
	 */
	private void initEntities() {
		// create the ship and put in center of screen
		playerOne = new PlayerEntity(this, "sprites/blankPlayer.gif", 32, 688);
		playerTwo = new PlayerEntity(this, "sprites/blankPlayer.gif", 400, 688);

		for (int i = 0; i < 1280; i += 40){
			tileStone1 = new TileEntity(this, "sprites/stone.png", i, 0);
			entitiies.add(tileStone1);
		} // W
		
		tileStone2 = new TileEntity(this, "sprites/stone.png", 1240, 40);
		entitiies.add(tileStone2);
		
		tileStone3 = new TileEntity(this, "sprites/stone.png", 1240, 80);
		entitiies.add(tileStone3);

		tileStone4 = new TileEntity(this, "sprites/stone.png", 1240, 120);
		entitiies.add(tileStone4);
		
		for (int i = 0; i < 1040; i += 40){
			tileStone5 = new TileEntity(this, "sprites/stone.png", i, 160);
			entitiies.add(tileStone5);
		} // W
		tileStone6 = new TileEntity(this, "sprites/stone.png", 1240, 160);
		entitiies.add(tileStone6);

		tileStone7 = new TileEntity(this, "sprites/stone.png", 0, 200);
		entitiies.add(tileStone7);
		tileStone8 = new TileEntity(this, "sprites/stone.png", 1240, 200);
		entitiies.add(tileStone8);

		tileStone9 = new TileEntity(this, "sprites/stone.png", 0, 240;
		entitiies.add(tileStone9);
		tileStone10 = new TileEntity(this, "sprites/stone.png", 1240, 240);
		entitiies.add(tileStone10);
		
		tileStone11 = new TileEntity(this, "sprites/stone.png", 0, 280);
		entitiies.add(tileStone11);
		tileStone12 = new TileEntity(this, "sprites/stone.png", 1240, 280);
		entitiies.add(tileStone12);

		tileStone13 = new TileEntity(this, "sprites/stone.png", 0, 320);
		entitiies.add(tileStone13);
		for (int i = 200; i < 1280; i += 40){
			tileStone14 = new TileEntity(this, "sprites/stone.png", i, 320);
			entitiies.add(tileStone14);
		} // W

		tileStone15 = new TileEntity(this, "sprites/stone.png", 0, 360);
		entitiies.add(tileStone15);
		tileStone16 = new TileEntity(this, "sprites/stone.png", 1240, 360);
		entitiies.add(tileStone16);

		tileStone17 = new TileEntity(this, "sprites/stone.png", 0, 400;
		entitiies.add(tileStone17);
		tileStone18 = new TileEntity(this, "sprites/stone.png", 1240, 400);
		entitiies.add(tileStone18);
		
		tileStone19 = new TileEntity(this, "sprites/stone.png", 0, 440);
		entitiies.add(tileStone19);
		tileStone20 = new TileEntity(this, "sprites/stone.png", 1240, 440);
		entitiies.add(tileStone20);

		for (int i = 0; i < 560; i += 40){
			tileStone21 = new TileEntity(this, "sprites/stone.png", i, 480);
			entitiies.add(tileStone21);
		} // W
		tileStone22 = new TileEntity(this, "sprites/stone.png", 1240, 480);
		entitiies.add(tileStone22);

		tileStone24 = new TileEntity(this, "sprites/stone.png", 0, 520);
		entitiies.add(tileStone24);
		for (int i = 560; i < 1040; i += 40){
			tileStone25 = new TileEntity(this, "sprites/stone.png", i, 520);
			entitiies.add(tileStone25);
		} // W
		tileStone26 = new TileEntity(this, "sprites/stone.png", 1240, 520);
		entitiies.add(tileStone26);

		tileStone27 = new TileEntity(this, "sprites/stone.png", 0, 560);
		entitiies.add(tileStone27);
		tileStone28 = new TileEntity(this, "sprites/stone.png", 1240, 560);
		entitiies.add(tileStone28);

		tileStone29 = new TileEntity(this, "sprites/stone.png", 0, 600;
		entitiies.add(tileStone29);
		tileStone30 = new TileEntity(this, "sprites/stone.png", 1240, 600);
		entitiies.add(tileStone30);
		
		tileStone31 = new TileEntity(this, "sprites/stone.png", 0, 640);
		entitiies.add(tileStone31);
		tileStone32 = new TileEntity(this, "sprites/stone.png", 1240, 640);
		entitiies.add(tileStone32);

		for (int i = 0; i < 1280; i += 40){
			tileStone33 = new TileEntity(this, "sprites/stone.png", i, 680);
			entitiies.add(tileStone33);
		} // W

		entities.add(playerOne);
		entities.add(playerTwo);

		// create a block of aliens (7x12)
		/*
		 * alienCount = 0; for (int row = 0; row < 7; row++) { for (int col = 0; col <
		 * 12; col++) { Entity alien = new AlienEntity(this, "sprites/alien.gif", 100 +
		 * (col * 40), 50 + (row * 30), ((row) * 12) + col); entities.add(alien);
		 * alienCount++; } // for } // outer for
		 */
	} // initEntities

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
	 * Notification that the play has killed all aliens
	 */
	public void notifyWin() {
		message = "You exterminated the Pegasus Race.  Good Job!";
		waitingForKeyPress = true;
	} // notifyWin

	/*
	 * Notification than an alien has been killed
	 */
	public void notifyAlienKilled() {
		alienCount--;

		if (alienCount == 0) {
			notifyWin();
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

	/* Attempt to fire. */
	/*
	 * public void tryToFire() { // check that we've waited long enough to fire if
	 * ((System.currentTimeMillis() - lastFire) < firingInterval) { return; } // if
	 * 
	 * // otherwise add a shot lastFire = System.currentTimeMillis(); ShotEntity
	 * shot = new ShotEntity(this, "sprites/shot.gif", ship.getX() + 10, ship.getY()
	 * - 30); entities.add(shot); } // tryToFire
	 */

	public void tryToJump(int player) { // check that the character is on the ground then jump
		if(player == 1) {
		
		} else {
			
		}
		

		
	} // tryToJump

	/*
	 * public void tryToBomb() { if ((System.currentTimeMillis() - lastBomb) <
	 * bombingInterval) { return; }
	 * 
	 * // otherwise add a bomb lastBomb = System.currentTimeMillis(); BombEntity
	 * bomb = new BombEntity(this, "sprites/missile00.png", ship.getX() + 10,
	 * ship.getY() - 30, entities); entities.add(bomb); }
	 */

	/*
	 * gameLoop input: none output: none purpose: Main game loop. Runs throughout
	 * game play. Responsible for the following activities: - calculates speed of
	 * the game loop to update moves - moves the game entities - draws the screen
	 * contents (entities, text) - updates game events - checks input
	 */
	public void gameLoop() {
		long lastLoopTime = System.currentTimeMillis();

		// keep loop running until game ends
		while (gameRunning) {

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
					entity.move(delta);
				} // for
			} // if

			// draw all entities
			for (int i = 0; i < entities.size(); i++) {
				Entity entity = (Entity) entities.get(i);
				entity.draw(g);
			} // for

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
				g.drawString("Press any key", (GAME_WIDTH - g.getFontMetrics().stringWidth("Press any key")) / 2,
						GAME_HEIGHT / 2);
			} // if

			// clear graphics and flip buffer
			g.dispose();
			strategy.show();

			// ship should not move without user input
			playerOne.setHorizontalMovement(0);
			playerOne.setVerticalMovement(0);

			playerTwo.setHorizontalMovement(0);
			playerTwo.setVerticalMovement(0);

			// respond to playerOne moving character
			if ((leftPressedOne) && (!rightPressedOne)) {
				playerOne.setHorizontalMovement(-moveSpeed);
				playerOne.changeSprite("leftPlayer.gif");
			} else if ((rightPressedOne) && (!leftPressedOne)) {
				playerOne.setHorizontalMovement(moveSpeed);
				playerOne.changeSprite("rightPlayer.gif");
			} // else

			// respond to playerTwo moving character
			if ((leftPressedTwo) && (!rightPressedTwo)) {
				playerTwo.setHorizontalMovement(-moveSpeed);
				playerTwo.changeSprite("leftPlayer.gif");
			} else if ((rightPressedTwo) && (!leftPressedTwo)) {
				playerTwo.setHorizontalMovement(moveSpeed);
				playerTwo.changeSprite("rightPlayer.gif");
			} // else

			// if spacebar pressed, try to fire
			if (firePressed) {
				// tryToFire();
			} // if

			// if up arrow pressed set vetical movement to 50

			if (upPressedOne) {
				tryToJump(1);
			}

			if (upPressedTwo) {
				tryToJump(2);
			}

			if (bPressed) {
				// tryToBomb();
			}
			// pause
			try {
				Thread.sleep(100);
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

		initEntities();

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

	/**
	 * Main Program
	 */
	public static void main(String[] args) {
		// instantiate this object
		new Game();
	} // main
} // Game
