
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
	private boolean upPressedOne = false; // true if up arrow is pressed

	// playerTwo movement keys
	private boolean leftPressedTwo = false; // true if left arrow key currently pressed
	private boolean rightPressedTwo = false; // true if right arrow key currently pressed
	private boolean upPressedTwo = false; // true if up arrow is pressed

	private boolean gameRunning = true; // whether or not the game is running
	private boolean levelRunning = true; // whether or not an individual level is running
	private boolean levelCleared = false; // whether or not the level has been cleared
	private boolean playerOneCleared = false; // whether or not playerOne has cleared the level
	private boolean playerTwoCleared = false; // whether or not playerTwo has cleared the level
	private int currentLevel = 1; // the current level
	private String background = ""; // the image to grab from sprite store to display as the background
	private ArrayList entities = new ArrayList(); // list of entities currently in game
	private ArrayList removeEntities = new ArrayList(); // list of entities to remove this loop
	private Entity playerOne; // first player
	private Entity playerTwo; // second player
	private String playerOneCollisions; // directions of movement that will result in a collision for playerOne
	private String playerTwoCollisions; // directions of movement that will result in a collision for playerTwo
	private double moveSpeed = 200; // hor. vel. of ship (px/s)
	private int gravity = 150; // strength of gravity
	private double initialFuelLevelOne; // max fuel level of playerOne for each level
	private double initialFuelLevelTwo; // max fuel level of playerTwo for each level

	// dynamic entities
	private Entity goalOne; // the end goal of the level for playerOne
	private Entity goalTwo; // the end goal of the level for playerTwo
	private Entity door; // to be passed into the corresponding button
	private Entity buttonOne; // a button that will be on the screen only if required
	private Entity buttonTwo; // another button that will be on the screen only if required

	
	private String message = ""; // message to display while waiting for a key press

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

		
		// runs the game until all levels have been complete
		while(gameRunning){
			initLevel(currentLevel);
			levelLoop();
			if(levelCleared){
				currentLevel++;
			}
		}
	} // constructor

	// allows other enities to get a list of the other entities
	public ArrayList getEntities(){
		return entities;
	} // getEntities

	// initializes level based on current level
	private void initLevel(int level){
		// reset variables that remain constant between level
		entities.clear(); // clears previous entities
		removeEntities.clear(); // clears entities in removeEntities
		playerOneCleared = false; // playerOne has not completed the level
		playerTwoCleared = false; // playerTwo has not completed the level
		levelCleared = false; // the level has not been cleared

		// set varibles that can change with each level
		switch(level){
			case 1:
				initialFuelLevelOne = 45;
				initialFuelLevelTwo = 45;
				background = "sprites/BackgroundTwo.png";
				break;
			case 2:
				initialFuelLevelOne = 45;
				initialFuelLevelTwo = 45;
				background = "sprites/BackgroundTwo.png";
				break;
			case 3:
				initialFuelLevelOne = 45;
				initialFuelLevelTwo = 45;
				background = "sprites/BackgroundTwo.png";
				break;
			case 4:
				initialFuelLevelOne = 45;
				initialFuelLevelTwo = 45;
				background = "sprites/BackgroundTwo.png";
				break;
			case 5:
				initialFuelLevelOne = 45;
				initialFuelLevelTwo = 45;
				background = "sprites/BackgroundTwo.png";
				break;
			case 6:
				initialFuelLevelOne = 45;
				initialFuelLevelTwo = 45;
				background = "sprites/BackgroundTwo.png";
				break;
			case 7:
				initialFuelLevelOne = 45;
				initialFuelLevelTwo = 45;
				background = "sprites/BackgroundTwo.png";
				break;
			case 8:
				initialFuelLevelOne = 45;
				initialFuelLevelTwo = 45;
				background = "sprites/BackgroundTwo.png";
				break;
		}

		// initEntites for the level and start it
		initEntities(level);
		levelRunning = true;
	}

	// removes an entity from the game, it will no longer be drawn
	public void removeEntity(Entity entity) {
		removeEntities.add(entity);
	} // removeEntity

	// notify that a player has died and restart the level
	public void notifyDeath() {
		message = "You died.";
		waitingForKeyPress = true;
		levelRunning = false;
	} // notifyDeath

	// check if the parameters for a level to be complete are met
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
	} // registerComplete

	// have the player attempt to jump, if it hits the ceiling or has not fuel don't jump
	public void tryToJump(Entity player, String collisions) {
		double fuelLevel = ((PlayerEntity) player).getFuelLevel();
		
		// checks fuel level and collision
		if(collisions.contains("top") || fuelLevel <= 0){
			return;
		} else {
			fuelLevel -= 1;
			// update fuel display based on new fuel level
			((PlayerEntity) player).setFuelLevel(fuelLevel);
			player.setVerticalMovement(-200);
		}
	} // tryToJump

	// checks if a individual shot generator is able to fire then generates a shot based the given parameters
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
			
			// creates the shot entity
			Entity tempShot = new DeathEntity(((ShotGeneratorEntity) generator).getShotSprite(), x, y, this);
			
			// set the shots movement
			if(direction == 'l' || direction == 'r'){
				tempShot.setHorizontalMovement(((ShotGeneratorEntity) generator).getShotMoveSpeed());
			} else {
				tempShot.setVerticalMovement(((ShotGeneratorEntity) generator).getShotMoveSpeed());
			}

			// makes the shot be displayed on the screen
			entities.add(tempShot);

			// updates the generators time of last shot
			((ShotGeneratorEntity) generator).setTimeOfLastShot(System.currentTimeMillis());
		} // if
	} // tryToFire

	// calculates speed of the game loop to update moves - moves the game entities - draws the screen contents (entities, text) - updates game events - checks input
	public void levelLoop() {
		long lastLoopTime = System.currentTimeMillis();

		// keep loop running until level ends
		while (levelRunning) {

			// calc. time since last update, will be used to calculate entities movement
			long delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();

			// get graphics context for the accelerated surface and make it display the background image
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.drawImage((SpriteStore.get()).getSprite(background).getImage(), 0, 0, null);

			// move entities if not waiting for keypress
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

			// brute force collisions, compare every entity against every other entity. If any collisions are detected notify both entities that it has occurred
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
				} // if
				
			} // outer for

			// remove dead entities
			entities.removeAll(removeEntities);
			removeEntities.clear();

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

			// respond to playerOne moving character
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

			// respond to playerTwo moving character
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

			// if up arrow pressed try to jump
			if (upPressedOne) {
				tryToJump(playerOne, playerOneCollisions);
			}

			if (upPressedTwo) {
				tryToJump(playerTwo, playerTwoCollisions);
			}

			// set delays so the game runs at 60fps
			try {
				Thread.sleep(16);
			} catch (Exception e) {
			}

		} // while

	} // levelLoop

	// start a fresh level and clear keyboard settings
	private void startGame() {
		// clear out any existing entities and initalize a new set
		entities.clear();

		// generate level
		initLevel(currentLevel);

		// blank out any keyboard settings that might exist
		leftPressedOne = false;
		rightPressedOne = false;
		upPressedOne = false;
		leftPressedTwo = false;
		rightPressedTwo = false;
		upPressedTwo = false;
	} // startGame

	// handles key input from the users
	private class KeyInputHandler extends KeyAdapter {

		private int pressCount = 1; // the number of key presses since waiting for 'any' key press

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
		} // keyPressed

		public void keyReleased(KeyEvent e) {
			// if waiting for keypress to start game, do nothing
			if (waitingForKeyPress) {
				return;
			} // if

			// respond to move left, right or jump
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
	
	// initialize the starting state of entities for a specific level
	private void initEntities(int level) {
		switch(level){
			case 1:
				playerOne = new PlayerEntity(this, "sprites/BaseFrontCharacter.png", 0, 0, initialFuelLevelOne);
				playerTwo = new PlayerEntity(this, "sprites/blankPLayer.gif", 0, 80, initialFuelLevelTwo);
				
				goalOne = new GoalEntity("sprites/blankPlayer.gif", 0, 280, playerOne);
				goalTwo = new GoalEntity("sprites/blankPlayer.gif", 80, 280, playerTwo);
				
				for (int i = 0; i < 640; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", i, 160));
				} // W
				for (int i = 200; i < 520; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", 600, i));
				} // W
				for (int i = 640; i < 1160; i += 40){
				entities.add(new TileEntity(this, "sprites/chocolate.png", i, 480));
				} // W
				for (int i = 320; i < 520; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, i));
				} // W
				
				for (int i = 640; i < 760; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", i, 320));
				} // W
				
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 320));
				
				for (int i = 1080; i < 1160; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", i, 320));
				} // W
				
				for (int i = 640; i < 1160; i += 40){
					entities.add(new DeathEntity("sprites/death.png", i, 440, this));
				} // W
				
				for (int i = 640; i < 720; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", 0, i));
				} // W
				
				for (int i = 40; i < 400; i += 40){
					entities.add(new DeathEntity("sprites/death.png", i, 680, this));
				} // W
				
				for (int i = 640; i < 720; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", 400, i));
				} // W
				
				for (int i = 440; i < 1280; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", i, 680));
				} // W
				
				for (int i = 0; i < 120; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", i, 440));
				} // W
				
				for (int i = 200; i < 280; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", i, 440));
				} // W
				for (int i = 360; i < 440; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", i, 480));
				} // W
				for (int i = 1200; i < 1280; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", i, 640));
				} // W
				
				entities.add(new MovableBlockEntity("sprites/box.png", 840, 600, this));
				
				door = new DeathEntity("sprites/door.png", 1200, 320, this);

				buttonOne = new ButtonEntity(this, "sprites/button.png", 480, 141, door);
				buttonTwo = new ButtonEntity(this, "sprites/button.png", 1200, 621, door, buttonOne);

				((ButtonEntity) buttonOne).setSecondButton(buttonTwo);
				
				entities.add(buttonOne);
				entities.add(buttonTwo);
				entities.add(door);

				entities.add(goalOne);
				entities.add(goalTwo);
				
				entities.add(playerOne);
				entities.add(playerTwo);
			break;
			case 2:
				// create players and and put in correct location
				playerOne = new PlayerEntity(this, "sprites/blankPlayer.gif", 20, 560, initialFuelLevelOne);
				playerTwo = new PlayerEntity(this, "sprites/blankPLayer.gif", 1205, 610, initialFuelLevelTwo);

				// create goal location for each player
				goalOne = new GoalEntity("sprites/blankPlayer.gif", 20, 40, playerOne);
				goalTwo = new GoalEntity("sprites/blankPlayer.gif", 80, 40, playerTwo);

				//platform 1
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 640));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 640));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 640));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 600));

				// platform 2
				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 640));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 640));

				// platform 3
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 640));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 640));

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
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 680));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 640));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 600));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 560));

				//door and button
				door = new DeathEntity("sprites/door.png", 1200, 560, this);
				entities.add(door);
				entities.add(new ButtonEntity(this, "sprites/button.png", 1120, 520, door));

				// shooter
				entities.add(new ShotGeneratorEntity(this, "sprites/death.png", 1120, 600, "sprites/tempBullet.png", -120, 3000, 'l'));

				// platform ladder
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 440));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 320));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 160));

				// second long jumps
				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 200));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 200));

				// end platform and shooter
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 120));

				entities.add(new ShotGeneratorEntity(this, "sprites/death.png", 80, 160, "sprites/tempBullet.png", 120, 3000, 'r'));

				//add players and goal
				entities.add(goalOne);
				entities.add(goalTwo);
				entities.add(playerOne);
				entities.add(playerTwo);
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

	// starts the game when the program is opened
	 public static void main(String[] args) {
		// instantiate this object
		new Game();
	} // main
} // Game
