
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
	private String background = ""; 
	private ArrayList entities = new ArrayList(); // list of entities
													// in game
	private ArrayList removeEntities = new ArrayList(); // list of entities
														// to remove this loop
	private Entity playerOne; // first player
	private Entity playerTwo; // second player
	private String playerOneCollisions; // directions of movement that will result in a collision for playerOne
	private String playerTwoCollisions; // directions of movement that will result in a collision for playerTwo
	private double moveSpeed = 200; // hor. vel. of ship (px/s)
	private int alienCount; // # of aliens left on screen
	private int gravity = 150;
	private double initialFuelLevelOne;
	private double initialFuelLevelTwo;

	// dynamic entities
	private Entity goalOne;
	private Entity goalTwo;
	private Entity door;

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
		JFrame container = new JFrame("Super Candroid Siblings");

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
			if(levelCleared){
				currentLevel++;
			}
		}
	} // constructor

	public ArrayList getEntities(){
		return entities;
	}

	private void initLevel(int level){
		// reset variables that remain constant between level
		entities.clear();
		removeEntities.clear();
		playerOneCleared = false;
		playerTwoCleared = false;
		levelCleared = false;

		// reset and set varibles that change with each level
		switch(level){
			case 1:
				initialFuelLevelOne = 45;
				initialFuelLevelTwo = 45;
				background = "sprites/BackgroundTwo.png";
				break;
			case 2:
				initialFuelLevelOne = 45;
				initialFuelLevelTwo = 45;
				break;
			case 3:
				initialFuelLevelOne = 45;
				initialFuelLevelTwo = 45;
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

		// init and start level
		initEntities(level);
		levelRunning = true;
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
		message = "You died.";
		waitingForKeyPress = true;
		levelRunning = false;
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
			levelRunning = false;
			levelCleared = true;
		}
	} // notifyComplete

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

	// checks if a individual shot generator is able to fire then if it is able to it generates a shot based on the generators parameters and shoots it
	public void tryToFire(Entity generator){
		int x = 0;
		int y = 0;
		char direction;
		
		if(System.currentTimeMillis() - ((ShotGeneratorEntity) generator).getTimeOfLastShot() >= ((ShotGeneratorEntity) generator).getShootingInterval()){
			direction = ((ShotGeneratorEntity) generator).getDirectionOfShot();
			
			// determine the shots starting position
			switch(direction){
				case 'r':
					x = generator.getX() + generator.getSprite().getWidth();
					y = generator.getY() + (generator.getSprite().getHeight() / 2);
					break;
				case 'l':
					x = generator.getX();
					y = generator.getY() + (generator.getSprite().getHeight() / 2);
					break;
				case 'u':
					x = generator.getX() + (generator.getSprite().getWidth() / 2);
					y = generator.getY();
					break;
				case 'd':
					x = generator.getX() + (generator.getSprite().getWidth() / 2);
					y = generator.getY() + generator.getSprite().getHeight();
					break;
			}
			
			Entity tempShot = new DeathEntity(((ShotGeneratorEntity) generator).getShotSprite(), x, y, this);
			
			// set the shots movement
			if(direction == 'l' || direction == 'r'){
				tempShot.setHorizontalMovement(((ShotGeneratorEntity) generator).getShotMoveSpeed());
			} else {
				tempShot.setVerticalMovement(((ShotGeneratorEntity) generator).getShotMoveSpeed());
			}

			entities.add(tempShot);

			((ShotGeneratorEntity) generator).setTimeOfLastShot(System.currentTimeMillis());
		}
	} // tryToFire

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
			
			g.drawImage((SpriteStore.get()).getSprite(background).getImage(), 0, 0, null);

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
			g.setColor(Color.green);
			(SpriteStore.get()).getSprite("sprites/P1JetPackBar.png").draw(g, 5, 10);
			g.fillRect(53, 20, (int) ((101 - (int) ((((PlayerEntity) playerOne).getFuelLevel() / initialFuelLevelOne) * 100)) * 1.42), 20);

			// draw fuel tank for p2
			g.setColor(Color.pink);
			(SpriteStore.get()).getSprite("sprites/P2JetPackBar.png").draw(g, 1075, 10);
			g.fillRect(1085, 20, (int) ((101 - (int) ((((PlayerEntity) playerTwo).getFuelLevel() / initialFuelLevelTwo) * 100)) * 1.42), 20);

			// brute force collisions, compare every entity
			// against every other entity. If any collisions
			// are detected notify both entities that it has
			// occurred
			for (int i = 0; i < entities.size(); i++) {
				// auto set button pressed to false
				if(entities.get(i) instanceof ButtonEntity){
					((ButtonEntity) entities.get(i)).setPressed(false);
					((Entity) entities.get(i)).setSprite("sprites/button.png");
				}

				for (int j = i + 1; j < entities.size(); j++) {
					Entity me = (Entity) entities.get(i);
					Entity him = (Entity) entities.get(j);

					if (me.collidesWith(him)) {
						me.collidedWith(him);
						him.collidedWith(me);
					} // if
				} // inner for

				// check if there are any shot generators on screen if so have them attempt to shoot
				if(entities.get(i) instanceof ShotGeneratorEntity){
					tryToFire((Entity) entities.get(i));
				}

				// checks if there are any buttons on the screen if they are not pressed add their corresponding door
				if(entities.get(i) instanceof ButtonEntity){
					if(((ButtonEntity) entities.get(i)).getSecondButton() == null){
						if(((ButtonEntity) entities.get(i)).getPressed()){
							((ButtonEntity) entities.get(i)).getTarget().setX(1280);
							((ButtonEntity) entities.get(i)).getTarget().setY(720);
						} else {
							((ButtonEntity) entities.get(i)).getTarget().setX(((ButtonEntity) entities.get(i)).getOriginX());
							((ButtonEntity) entities.get(i)).getTarget().setY(((ButtonEntity) entities.get(i)).getOriginY());
						}
					} else {
						if(((ButtonEntity) entities.get(i)).getPressed() || ((ButtonEntity) ((ButtonEntity) entities.get(i)).getSecondButton()).getPressed()){
							((ButtonEntity) entities.get(i)).getTarget().setX(1280);
							((ButtonEntity) entities.get(i)).getTarget().setY(720);
						} else {
							((ButtonEntity) entities.get(i)).getTarget().setX(((ButtonEntity) entities.get(i)).getOriginX());
							((ButtonEntity) entities.get(i)).getTarget().setY(((ButtonEntity) entities.get(i)).getOriginY());
						}
					}
				}
				
			} // outer for

			
			for(int i = 0; i < entities.size(); i++){
			}

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
				((PlayerEntity) playerOne).setFuelLevel(initialFuelLevelOne);
				playerOne.setVerticalMovement(0);
			} else {
				playerOne.setVerticalMovement(gravity); // gravity must be equal to jump speed
			}

			// initial movement logic for playerTwo
			playerTwo.setHorizontalMovement(0);
			if(playerTwoCollisions.contains("bottom") && !(playerTwoCollisions.contains("top"))){
				((PlayerEntity) playerTwo).setFuelLevel(initialFuelLevelTwo);
				playerTwo.setVerticalMovement(0);
			} else {
				playerTwo.setVerticalMovement(gravity); // gravity must be equal to jump speed
			}

			// respond to playerOne moving character left or right
			if ((leftPressedOne) && (!rightPressedOne) && !(playerOneCollisions.contains("left"))) {
				playerOne.setHorizontalMovement(-moveSpeed);
				if(upPressedOne){
					playerOne.setSprite("sprites/PlayerOneJumpLeft.png");
				} else {
					playerOne.setSprite("sprites/PlayerOneLeft.png");
				}
			} else if ((rightPressedOne) && (!leftPressedOne) && !(playerOneCollisions.contains("right"))) {
				playerOne.setHorizontalMovement(moveSpeed);
				if(upPressedOne){
					playerOne.setSprite("sprites/PlayerOneJumpRight.png");
				} else {
					playerOne.setSprite("sprites/PlayerOneRight.png");
				}
			} else if ((!rightPressedOne) && (!leftPressedOne)) {
				if(upPressedOne){
					playerOne.setSprite("sprites/PlayerOneJumpForward.png");
				} else {
					playerOne.setSprite("sprites/PlayerOneForward.png");
				}
			}

			// respond to playerTwo moving character left or right
			if ((leftPressedTwo) && (!rightPressedTwo) && !(playerTwoCollisions.contains("left"))) {
				playerTwo.setHorizontalMovement(-moveSpeed);
				if(upPressedTwo){
					playerTwo.setSprite("sprites/PlayerTwoJumpLeft.png");
				} else {
					playerTwo.setSprite("sprites/PlayerTwoLeft.png");
				}
			} else if ((rightPressedTwo) && (!leftPressedTwo) && !(playerTwoCollisions.contains("right"))) {
				playerTwo.setHorizontalMovement(moveSpeed);
				if(upPressedTwo){
					playerTwo.setSprite("sprites/PlayerTwoJumpRight.png");
				} else {
					playerTwo.setSprite("sprites/PlayerTwoRight.png");
				}
			} else if ((!rightPressedTwo) && (!leftPressedTwo)) {
				if(upPressedTwo){
					playerTwo.setSprite("sprites/PlayerTwoJumpForward.png");
				} else {
					playerTwo.setSprite("sprites/PlayerTwoForward.png");
				}
			}

			// if spacebar pressed, try to fire
			if (firePressed) {
				// tryToFire();
			} // if

			// if up arrow pressed try to jump
			if (upPressedOne) {
				tryToJump(playerOne, playerOneCollisions);
			}

			if (upPressedTwo) {
				tryToJump(playerTwo, playerTwoCollisions);
			}

			if (bPressed) {
				// tryToBomb();
			}

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

		initLevel(currentLevel);

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

			// respond to move left, right or jump
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
					pressCount = 1;
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

				entities.add(new ShotGeneratorEntity(this, "sprites/death.png", 120, 80, "sprites/tempBullet.png", 100, 1500, 'r'));
				entities.add(new DeathEntity("sprites/leftPlayer.gif", 100, 600, this));// death entity

				entities.add(new MovableBlockEntity("sprites/chocolate.png", 800, 640, this));

				

				// tip : order in entities is order of drawing(if something needs to be infront put it later)
				for (int i = 0; i < 1280; i += 40){
					tileStone1 = new TileEntity(this, "sprites/chocolate.png", i, 0, "platform");
					entities.add(tileStone1);
				} // W
				
				tileStone2 = new TileEntity(this, "sprites/chocolate.png", 1240, 40, "wall");
				entities.add(tileStone2);
				
				tileStone3 = new TileEntity(this, "sprites/chocolate.png", 1240, 80, "wall");
				entities.add(tileStone3);

				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 120, "wall"));
				
				for (int i = 0; i < 1040; i += 40){
					tileStone5 = new TileEntity(this, "sprites/chocolate.png", i, 160, "platform");
					entities.add(tileStone5);
				} // W
				tileStone6 = new TileEntity(this, "sprites/chocolate.png", 1240, 160, "wall");
				entities.add(tileStone6);

				tileStone7 = new TileEntity(this, "sprites/chocolate.png", 0, 200, "wall");
				entities.add(tileStone7);
				tileStone8 = new TileEntity(this, "sprites/chocolate.png", 1240, 200, "wall");
				entities.add(tileStone8);

				tileStone9 = new TileEntity(this, "sprites/chocolate.png", 0, 240, "wall");
				entities.add(tileStone9);
				tileStone10 = new TileEntity(this, "sprites/chocolate.png", 1240, 240, "wall");
				entities.add(tileStone10);
				
				tileStone11 = new TileEntity(this, "sprites/chocolate.png", 0, 280, "wall");
				entities.add(tileStone11);
				tileStone12 = new TileEntity(this, "sprites/chocolate.png", 1240, 280, "wall");
				entities.add(tileStone12);

				tileStone13 = new TileEntity(this, "sprites/chocolate.png", 0, 320, "wall");
				entities.add(tileStone13);
				for (int i = 200; i < 1280; i += 40){
					tileStone14 = new TileEntity(this, "sprites/chocolate.png", i, 320, "platform");
					entities.add(tileStone14);
				} // W

				tileStone15 = new TileEntity(this, "sprites/chocolate.png", 0, 360, "wall");
				entities.add(tileStone15);
				tileStone16 = new TileEntity(this, "sprites/chocolate.png", 1240, 360, "wall");
				entities.add(tileStone16);

				tileStone17 = new TileEntity(this, "sprites/chocolate.png", 0, 400, "wall");
				entities.add(tileStone17);
				tileStone18 = new TileEntity(this, "sprites/chocolate.png", 1240, 400, "wall");
				entities.add(tileStone18);
				
				tileStone19 = new TileEntity(this, "sprites/chocolate.png", 0, 440, "wall");
				entities.add(tileStone19);
				tileStone20 = new TileEntity(this, "sprites/chocolate.png", 1240, 440, "wall");
				entities.add(tileStone20);

				for (int i = 0; i < 560; i += 40){
					tileStone21 = new TileEntity(this, "sprites/chocolate.png", i, 480, "platform");
					entities.add(tileStone21);
				} // W
				tileStone22 = new TileEntity(this, "sprites/chocolate.png", 1240, 480, "wall");
				entities.add(tileStone22);

				tileStone24 = new TileEntity(this, "sprites/chocolate.png", 0, 520, "wall");
				entities.add(tileStone24);
				for (int i = 560; i < 1040; i += 40){
					tileStone25 = new TileEntity(this, "sprites/chocolate.png", i, 520, "platform");
					entities.add(tileStone25);
				} // W
				tileStone26 = new TileEntity(this, "sprites/chocolate.png", 1240, 520, "wall");
				entities.add(tileStone26);

				tileStone27 = new TileEntity(this, "sprites/chocolate.png", 0, 560, "wall");
				entities.add(tileStone27);
				tileStone28 = new TileEntity(this, "sprites/chocolate.png", 1240, 560, "wall");
				entities.add(tileStone28);

				tileStone29 = new TileEntity(this, "sprites/chocolate.png", 0, 600, "wall");
				entities.add(tileStone29);
				tileStone30 = new TileEntity(this, "sprites/chocolate.png", 1240, 600, "wall");
				entities.add(tileStone30);
				
				tileStone31 = new TileEntity(this, "sprites/chocolate.png", 0, 640, "wall");
				entities.add(tileStone31);
				tileStone32 = new TileEntity(this, "sprites/chocolate.png", 1240, 640, "wall");
				entities.add(tileStone32);

				for (int i = 0; i < 1280; i += 40){
					tileStone33 = new TileEntity(this, "sprites/chocolate.png", i, 680, "platform");
					entities.add(tileStone33);
				} // W

				entities.add(goalOne);
				entities.add(goalTwo);

				entities.add(playerOne);
				entities.add(playerTwo);
				break;
			case 2:
				playerOne = new PlayerEntity(this, "sprites/BaseFrontCharacter.png", 0, 0, initialFuelLevelOne);
				playerTwo = new PlayerEntity(this, "sprites/blankPLayer.gif", 0, 80, initialFuelLevelTwo);
				
				goalOne = new GoalEntity("sprites/blankPlayer.gif", 0, 280, playerOne);
				goalTwo = new GoalEntity("sprites/blankPlayer.gif", 80, 280, playerTwo);
				
				for (int i = 0; i < 640; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", i, 160, "platform"));
				} // W
				for (int i = 200; i < 520; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", 600, i, "wall"));
				} // W
				for (int i = 640; i < 1160; i += 40){
				entities.add(new TileEntity(this, "sprites/chocolate.png", i, 480, "platform"));
				} // W
				for (int i = 320; i < 520; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, i, "wall"));
				} // W
				
				for (int i = 640; i < 760; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", i, 320, "platform"));
				} // W
				
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 320, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 320, "platform"));
				
				for (int i = 1080; i < 1160; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", i, 320, "platform"));
				} // W
				
				for (int i = 640; i < 1160; i += 40){
					entities.add(new DeathEntity("sprites/death.png", i, 440, this));
				} // W
				
				for (int i = 640; i < 720; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", 0, i, "wall"));
				} // W
				
				for (int i = 40; i < 400; i += 40){
					entities.add(new DeathEntity("sprites/death.png", i, 680, this));
				} // W
				
				for (int i = 640; i < 720; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", 400, i, "wall"));
				} // W
				
				for (int i = 440; i < 1280; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", i, 680, "platform"));
				} // W
				
				for (int i = 0; i < 120; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", i, 440, "platform"));
				} // W
				
				for (int i = 200; i < 280; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", i, 440, "platform"));
				} // W
				for (int i = 360; i < 440; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", i, 480, "platform"));
				} // W
				for (int i = 1200; i < 1280; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", i, 640, "platform"));
				} // W
				
				entities.add(new MovableBlockEntity("sprites/box.png", 840, 600, this));
				
				door = new DeathEntity("sprites/door.png", 1200, 320, this);

				entities.add(new ButtonEntity(this, "sprites/button.png", 480, 141, door));
				

				entities.add(goalOne);
				entities.add(goalTwo);
				
				entities.add(playerOne);
				entities.add(playerTwo);
				break;
			case 3:
				// create players and and put in correct location
				playerOne = new PlayerEntity(this, "sprites/blankPlayer.gif", 20, 560, initialFuelLevelOne);
				playerTwo = new PlayerEntity(this, "sprites/blankPLayer.gif", 1205, 610, initialFuelLevelTwo);

				// create goal location for each player
				goalOne = new GoalEntity("sprites/blankPlayer.gif", 20, 40, playerOne);
				goalTwo = new GoalEntity("sprites/blankPlayer.gif", 80, 40, playerTwo);

				//platform 1
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 680, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 680, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 680, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 640, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 640, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 640, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 600, "platform"));

				// platform 2
				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 680, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 680, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 640, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 640, "platform"));

				// platform 3
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 680, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 680, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 640, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 640, "platform"));

				// death floor
				entities.add(new DeathEntity("sprites/death.png", 120, 680, this));
				entities.add(new DeathEntity("sprites/death.png", 160, 680, this));
				entities.add(new DeathEntity("sprites/death.png", 200, 680, this));
				entities.add(new DeathEntity("sprites/death.png", 240, 680, this));
				entities.add(new DeathEntity("sprites/death.png", 280, 680, this));
				entities.add(new DeathEntity("sprites/death.png", 320, 680, this));
				entities.add(new DeathEntity("sprites/death.png", 360, 680, this));
				entities.add(new DeathEntity("sprites/death.png", 400, 680, this));

				entities.add(new DeathEntity("sprites/death.png", 520, 680, this));
				entities.add(new DeathEntity("sprites/death.png", 560, 680, this));
				entities.add(new DeathEntity("sprites/death.png", 600, 680, this));
				entities.add(new DeathEntity("sprites/death.png", 640, 680, this));
				entities.add(new DeathEntity("sprites/death.png", 680, 680, this));
				entities.add(new DeathEntity("sprites/death.png", 720, 680, this));
				entities.add(new DeathEntity("sprites/death.png", 760, 680, this));
				entities.add(new DeathEntity("sprites/death.png", 800, 680, this));

				entities.add(new DeathEntity("sprites/death.png", 920, 680, this));
				entities.add(new DeathEntity("sprites/death.png", 960, 680, this));
				entities.add(new DeathEntity("sprites/death.png", 1000, 680, this));
				entities.add(new DeathEntity("sprites/death.png", 1040, 680, this));
				entities.add(new DeathEntity("sprites/death.png", 1080, 680, this));
				entities.add(new DeathEntity("sprites/death.png", 1120, 680, this));
				
				// box that holds p2
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 680, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 680, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 680, "platform"));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 640, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 600, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 560, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 560, "platform"));

				//door and button
				door = new DeathEntity("sprites/door.png", 1200, 560, this);
				entities.add(door);
				entities.add(new ButtonEntity(this, "sprites/button.png", 1120, 520, door));

				// shooter
				entities.add(new ShotGeneratorEntity(this, "sprites/death.png", 1120, 600, "sprites/tempBullet.png", -120, 3000, 'l'));

				// platform ladder
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 440, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 440, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 440, "platform"));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 320, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 320, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 320, "platform"));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 200, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 200, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 200, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 160, "platform"));

				// second long jumps
				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 200, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 200, "platform"));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 200, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 200, "platform"));

				// end platform and shooter
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 120, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 120, "platform"));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 120, "platform"));

				entities.add(new ShotGeneratorEntity(this, "sprites/death.png", 80, 160, "sprites/tempBullet.png", 120, 3000, 'r'));

				//add players and goal
				entities.add(goalOne);
				entities.add(goalTwo);
				entities.add(playerOne);
				entities.add(playerTwo);
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
