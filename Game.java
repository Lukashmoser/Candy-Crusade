
/* Game.java
 * Space Invaders Main Program
 *
 */

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends Canvas {

	private BufferStrategy strategy; // take advantage of accelerated graphics
	private boolean waitingForKeyPress = false; // true if game held up until

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
	private int currentLevel = 0; // the current level
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

	private Font stringFont = new Font( "Unispace", Font.BOLD, 30);
	private String message = ""; // message to display while waiting for a key press

	/*
	 * Construct our game and set it running.
	 */
	public Game() {
		// create a frame to contain game
		JFrame container = new JFrame("Candy Crusade");

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
				initialFuelLevelOne = 50;
				initialFuelLevelTwo = 50;
				background = "sprites/BackgroundOne.png";
				break;
			case 2:
				initialFuelLevelOne = 50;
				initialFuelLevelTwo = 50;
				background = "sprites/BackgroundOne.png";
				break;
			case 3:
				initialFuelLevelOne = 50;
				initialFuelLevelTwo = 50;
				background = "sprites/BackgroundOne.png";
				break;
			case 4:
				initialFuelLevelOne = 50;
				initialFuelLevelTwo = 50;
				background = "sprites/BackgroundOne.png";
				break;
			case 5:
				initialFuelLevelOne = 50;
				initialFuelLevelTwo = 50;
				background = "sprites/BackgroundOne.png";
				break;
			case 6:
				initialFuelLevelOne = 45;
				initialFuelLevelTwo = 45;
				background = "sprites/BackgroundOne.png";
				break;
			case 7:
				initialFuelLevelOne = 50;
				initialFuelLevelTwo = 50;
				background = "sprites/BackgroundOne.png";
				break;
			case 8:
				initialFuelLevelOne = 50;
				initialFuelLevelTwo = 50;
				background = "sprites/BackgroundOne.png";
				break;
			case 9:
				initialFuelLevelOne = 50;
				initialFuelLevelTwo = 50;
				background = "sprites/BackgroundOne.png";
				break;
			case 10:
				initialFuelLevelOne = 45;
				initialFuelLevelTwo = 45;
				background = "sprites/BackgroundOne.png";
				break;
			case 11:
				background = "sprites/endingScreen.png";
				break;
			default:
				background = "sprites/beginningScreen.png";
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

			// only draw fuel tanks if there are players
			if(!(currentLevel == 0 || currentLevel == 11)){
				// draw fuel tank for p1
				g.setColor(Color.green);
				(SpriteStore.get()).getSprite("sprites/jetPackBarP1.png").draw(g, 5, 10);
				g.fillRect(53, 20, (int) ((101 - (int) ((((PlayerEntity) playerOne).getFuelLevel() / initialFuelLevelOne) * 100)) * 1.42), 20);

				// draw fuel tank for p2
				g.setColor(Color.pink);
				(SpriteStore.get()).getSprite("sprites/jetPackBarP2.png").draw(g, 1075, 10);
				g.fillRect(1085, 20, (int) ((101 - (int) ((((PlayerEntity) playerTwo).getFuelLevel() / initialFuelLevelTwo) * 100)) * 1.42), 20);
			}

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
				g.fillRoundRect(490, 255, 300, 140, 25, 25);
				g.setColor(new Color(57, 181, 74));
				g.fillRoundRect(500, 265, 280, 120, 20, 20);
				g.setColor(Color.white);
				g.setFont(stringFont);
				g.drawString(message, (GAME_WIDTH - g.getFontMetrics().stringWidth(message)) / 2, GAME_HEIGHT / 2 - 50);
				g.drawString("Press any key", (GAME_WIDTH - g.getFontMetrics().stringWidth("Press any key")) / 2, GAME_HEIGHT / 2);
				
			} // if

			// clear graphics and flip buffer
			g.dispose();
			strategy.show();

			// only do player related code if there are players
			if(!(currentLevel == 0 || currentLevel == 11)){
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
						playerOne.setSprite("sprites/P1JL.png");
					} else {
						playerOne.setSprite("sprites/P1L.png");
					}
				} else if ((rightPressedOne) && (!leftPressedOne) && !(playerOneCollisions.contains("right"))) {
					playerOne.setHorizontalMovement(moveSpeed);
					if(upPressedOne){
						playerOne.setSprite("sprites/P1JR.png");
					} else {
						playerOne.setSprite("sprites/P1R.png");
					}
				} else if ((!rightPressedOne) && (!leftPressedOne)) {
					if(upPressedOne){
						playerOne.setSprite("sprites/P1JF.png");
					} else {
						playerOne.setSprite("sprites/P1F.png");
					}
				}

				// respond to playerTwo moving character
				if ((leftPressedTwo) && (!rightPressedTwo) && !(playerTwoCollisions.contains("left"))) {
					playerTwo.setHorizontalMovement(-moveSpeed);
					if(upPressedTwo){
						playerTwo.setSprite("sprites/P2JL.png");
					} else {
						playerTwo.setSprite("sprites/P2L.png");
					}
				} else if ((rightPressedTwo) && (!leftPressedTwo) && !(playerTwoCollisions.contains("right"))) {
					playerTwo.setHorizontalMovement(moveSpeed);
					if(upPressedTwo){
						playerTwo.setSprite("sprites/P2JR.png");
					} else {
						playerTwo.setSprite("sprites/P2R.png");  
					}
				} else if ((!rightPressedTwo) && (!leftPressedTwo)) {
					if(upPressedTwo){
						playerTwo.setSprite("sprites/P2JF.png");
					} else {
						playerTwo.setSprite("sprites/P2F.png");
					}
				}

				// if up arrow pressed try to jump
				if (upPressedOne) {
					tryToJump(playerOne, playerOneCollisions);
				}

				if (upPressedTwo) {
					tryToJump(playerTwo, playerTwoCollisions);
				}
			} // if


			

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

		// generate levels
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
				if (pressCount >= 1) {
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

			// allows the user to continue to the next screen
			if (e.getKeyChar() == 32 && (currentLevel == 0 || currentLevel == 11)) {
				levelCleared = true;
				levelRunning = false;
				if(currentLevel == 11){
					currentLevel = -1;
				}
			}

		} // keyTyped

	} // class KeyInputHandler
	
	// initialize the starting state of entities for a specific level
	private void initEntities(int level) {
		switch(level){
			case 1:
				// create players and and put in correct location
				playerOne = new PlayerEntity(this, "sprites/P1F.png", 20, 560, initialFuelLevelOne);
				playerTwo = new PlayerEntity(this, "sprites/P2F.png", 1180, 60, initialFuelLevelTwo);

				// create goal location for each player
				goalOne = new GoalEntity("sprites/playerOneExit.png", 1120, 120, playerOne);
				goalTwo = new GoalEntity("sprites/playerTwoExit.png", 120, 600, playerTwo);

				// bottom floor
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 120, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 240, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 680, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 680));

				// middle floor
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 120, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 240, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 680, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 440));

				// top floor
				entities.add(new TileEntity(this, "sprites/chocolate.png", 240, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 680, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 200));

				// bottom jump platform
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 560));

				// middle jump platform
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 320));

				// players and goals
				entities.add(goalOne);
				entities.add(goalTwo);
				entities.add(playerOne);
				entities.add(playerTwo);
				break;
				
			case 2:
				playerOne = new PlayerEntity(this, "sprites/P1F.png", 0, 0, initialFuelLevelOne);
				playerTwo = new PlayerEntity(this, "sprites/P2F.png", 0, 80, initialFuelLevelTwo);
				
				goalOne = new GoalEntity("sprites/playerOneExit.png", 0, 360, playerOne);
				goalTwo = new GoalEntity("sprites/playerTwoExit.png", 80, 360, playerTwo);
				
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
					entities.add(new DeathEntity("sprites/slime.png", i, 440, this));
				} // W
				
				for (int i = 640; i < 720; i += 40){
					entities.add(new TileEntity(this, "sprites/chocolate.png", 0, i));
				} // W
				
				for (int i = 40; i < 400; i += 40){
					entities.add(new DeathEntity("sprites/slime.png", i, 680, this));
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
				
				door = new DeathEntity("sprites/doorHorizontal.png", 1200, 320, this);

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
			case 3:
				// create players and and put in correct location
				playerOne = new PlayerEntity(this, "sprites/P1F.png", 160, 560, initialFuelLevelOne);
				playerTwo = new PlayerEntity(this, "sprites/P2F.png", 1040, 560, initialFuelLevelTwo);

				// create goal location for each player
				goalOne = new GoalEntity("sprites/playerOneExit.png", 1230, 600, playerOne);
				goalTwo = new GoalEntity("sprites/playerTwoExit.png", 10, 600, playerTwo);

				//bottom left floor
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 120, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 240, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 680));

				// bottom middle death tiles
				entities.add(new DeathEntity("sprites/slime.png", 440, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 480, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 520, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 560, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 600, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 640, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 680, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 720, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 760, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 800, 680, this));

				// bottom right floor
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 680));

				// left side 3 x 1 platforms
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 560));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 240));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 240));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 240));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 80));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 80));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 240, 80));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 400));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 400));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 240, 400));

				// right side 3 x 1 platforms
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png",1200, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 560));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 240));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 240));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 240));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 80));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 80));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 80));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 400));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 400));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 400));

				// left side tower
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 80));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 160));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 240));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 360));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 400));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 600));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 640));

				// right side tower
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 80));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 160));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 240));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 360));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 400));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 600));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 640));

				// left side 2 x 1 platforms
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 200));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 320));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 440));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 560));

				// right side 2 x 1 platforms
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 200));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 320));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 440));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 560));

				// middle divider
				entities.add(new TileEntity(this, "sprites/chocolate.png", 620, 0));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 660, 0));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 620, 40));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 660, 40));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 620, 80));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 660, 80));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 620, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 660, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 620, 160));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 660, 160));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 620, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 660, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 620, 240));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 660, 240));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 620, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 660, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 620, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 660, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 620, 360));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 660, 360));

				// players and goals
				entities.add(goalOne);
				entities.add(goalTwo);
				entities.add(playerOne);
				entities.add(playerTwo);

				break;
			case 4:
				// create players and and put in correct location
				playerOne = new PlayerEntity(this, "sprites/P1F.png", 120, 60, initialFuelLevelOne);
				playerTwo = new PlayerEntity(this, "sprites/P2F.png", 40, 60, initialFuelLevelTwo);

				// create goal location for each player
				goalOne = new GoalEntity("sprites/playerOneExit.png", 300, 400, playerOne);
				goalTwo = new GoalEntity("sprites/playerTwoExit.png", 300, 600, playerTwo);

				// bottom floor
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 120, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 240, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 680, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 680));

				// middle floor
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 120, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 240, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 680, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 320));

				// top left shooters
				entities.add(new ShotGeneratorEntity(this, "sprites/shooterRight.png", 240, -400, "sprites/enemyCandyOne.png", 160, 3000, 'd'));
				entities.add(new ShotGeneratorEntity(this, "sprites/shooterRight.png", 320, -400, "sprites/enemyCandyTwo.png", 120, 3000, 'd'));
				entities.add(new ShotGeneratorEntity(this, "sprites/shooterRight.png", 400, -400, "sprites/enemyCandyTwo.png", 80, 3000, 'd'));

				// top right shooter
				entities.add(new ShotGeneratorEntity(this, "sprites/shooterRight.png", 520, -400, "sprites/enemyCandyOne.png", 200, 3000, 'd'));
				entities.add(new ShotGeneratorEntity(this, "sprites/shooterRight.png", 600, -400, "sprites/enemyCandyTwo.png", 160, 3000, 'd'));
				entities.add(new ShotGeneratorEntity(this, "sprites/shooterRight.png", 680, -400, "sprites/enemyCandyOne.png", 120, 3000, 'd'));
				entities.add(new ShotGeneratorEntity(this, "sprites/shooterRight.png", 760, -400, "sprites/enemyCandyOne.png", 80, 3000, 'd'));

				// double thick platform
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 120, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 120, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 240, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 240, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 680, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 680, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 520));

				// boxes to push
				entities.add(new MovableBlockEntity("sprites/box.png", 1040, 600, this));
				entities.add(new MovableBlockEntity("sprites/box.png", 1040, 400, this));

				// shot generators
				entities.add(new ShotGeneratorEntity(this, "sprites/shooterRight.png", 0, 400, "sprites/bulletRight.png", 500, 500, 'r'));
				entities.add(new ShotGeneratorEntity(this, "sprites/shooterRight.png", 0, 600, "sprites/bulletRight.png", 500, 500, 'r'));

				// players and goals
				entities.add(goalOne);
				entities.add(goalTwo);
				entities.add(playerOne);
				entities.add(playerTwo);

				break;
			case 5:
				// create players and and put in correct location
				playerOne = new PlayerEntity(this, "sprites/P1F.png", 85, 100, initialFuelLevelOne);
				playerTwo = new PlayerEntity(this, "sprites/P2F.png", 460, 100, initialFuelLevelTwo);

				// create goal location for each player
				goalOne = new GoalEntity("sprites/playerOneExit.png", 10, 240, playerOne);
				goalTwo = new GoalEntity("sprites/playerTwoExit.png", 20, 600, playerTwo);

				// floor
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 120, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 240, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 680, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 680));

				// command center box and platforms
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 120, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 240, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 320));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 240));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 160));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 80));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 40));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 0));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 80));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 80));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 80));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 80));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 80));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 80));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 120, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 200));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 200));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 240));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 280));

				// top right zone
				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 680, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 200));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 160));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 80));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 0));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 40));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 80));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 160));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 80));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 0));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 40));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 80));

				// far right drop chute
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 240));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 360));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 400));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 520));

				// in and out jumps
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 360));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 400));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 600));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 640));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 400));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 400));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 400));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 560));

				// middle chute
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 240));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 360));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 400));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 520));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 680, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 440));

				// far left chute
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 360));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 400));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 600));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 640));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 360));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 400));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 520));

				// ending box
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 560));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 120, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 440));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 600));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 640));

				// button 1 and door 1
				door = new DeathEntity("sprites/doorVertical.png", 80, 480, this);
				entities.add(door);

				entities.add(new ButtonEntity(this, "sprites/button.png", 0, 61, door));

				// button 2 and door 2
				door = new DeathEntity("sprites/doorVertical.png", 880, 240, this);
				entities.add(door);

				entities.add(new ButtonEntity(this, "sprites/button.png", 160, 61, door));

				// button 3 and door 3
				door = new DeathEntity("sprites/doorHorizontal.png", 1200, 200, this);
				entities.add(door);

				entities.add(new ButtonEntity(this, "sprites/button.png", 320, 61, door));

				// button 4 and door 4
				door = new DeathEntity("sprites/doorHorizontal.png", 800, 520, this);
				entities.add(door);

				entities.add(new ButtonEntity(this, "sprites/button.png", 160, 181, door));

				// button 5 and door 5
				door = new DeathEntity("sprites/doorHorizontal.png", 440, 520, this);
				entities.add(door);

				entities.add(new ButtonEntity(this, "sprites/button.png", 320, 181, door));

				// button 6 and door 6
				door = new DeathEntity("sprites/doorHorizontal.png", 0, 200, this);
				entities.add(door);

				entities.add(new ButtonEntity(this, "sprites/button.png", 80, 661, door));

				// button 7 and door 7
				door = new DeathEntity("sprites/doorHorizontal.png", 1200, 520, this);
				entities.add(door);

				entities.add(new ButtonEntity(this, "sprites/button.png", 160, 301, door));

				// button 8 and door 8
				door = new DeathEntity("sprites/doorVertical.png", 520, 240, this);
				entities.add(door);

				entities.add(new ButtonEntity(this, "sprites/button.png", 320, 301, door));

				// boxes
				entities.add(new MovableBlockEntity("sprites/box.png", 360, 600, this));
				entities.add(new MovableBlockEntity("sprites/box.png", 720, 600, this));

				// players and goals
				entities.add(goalOne);
				entities.add(goalTwo);
				entities.add(playerOne);
				entities.add(playerTwo);
				break;
			case 6:
				// create players and and put in correct location
				playerOne = new PlayerEntity(this, "sprites/P1F.png", 10, 560, initialFuelLevelOne);
				playerTwo = new PlayerEntity(this, "sprites/P2F.png", 100, 560, initialFuelLevelTwo);

				// create goal location for each player
				goalOne = new GoalEntity("sprites/playerOneExit.png", 1160, 40, playerOne);
				goalTwo = new GoalEntity("sprites/playerTwoExit.png", 1080, 40, playerTwo);

				// start platform
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 640));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 640));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 640));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 120, 640));
				
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 120, 680));

				// death floor
				entities.add(new DeathEntity("sprites/slime.png", 160, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 200, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 240, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 280, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 320, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 360, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 400, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 440, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 480, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 520, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 560, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 600, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 640, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 680, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 720, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 760, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 800, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 840, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 880, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 920, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 960, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 1000, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 1040, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 1080, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 1120, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 1160, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 1200, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 1240, 680, this));

				// first level of platforms
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 560));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 560));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 560));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 560));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 480));

				// second level of platforms
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 360));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 360));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 360));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 360));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 360));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 360));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 240, 360));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 360));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 280));

				// third level of platforms
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 160));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 240, 160));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 160));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 160));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 160));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 160));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 120));

				// shooter 1 - 3
				entities.add(new ShotGeneratorEntity(this, "sprites/shooterLeft.png", 1200, 520, "sprites/bulletLeft.png", -80, 4000, 'l'));

				entities.add(new ShotGeneratorEntity(this, "sprites/shooterRight.png", 40, 320, "sprites/bulletRight.png", 120, 3000, 'r'));

				entities.add(new ShotGeneratorEntity(this, "sprites/shooterLeft.png", 960, 120, "sprites/bulletLeft.png", -160, 3000, 'l'));

				// players and goals
				entities.add(goalOne);
				entities.add(goalTwo);
				entities.add(playerOne);
				entities.add(playerTwo);
				break;
			case 7:
				// create players and and put in correct location
				playerOne = new PlayerEntity(this, "sprites/P1F.png", 420, 20, initialFuelLevelOne);
				playerTwo = new PlayerEntity(this, "sprites/P2F.png", 800, 20, initialFuelLevelTwo);

				// create goal location for each player
				goalOne = new GoalEntity("sprites/playerOneExit.png", 1240, 600, playerOne);
				goalTwo = new GoalEntity("sprites/playerTwoExit.png", 0, 600, playerTwo);

				// middle divider
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 0));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 0));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 40));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 40));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 80));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 80));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 160));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 160));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 240));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 240));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 360));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 360));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 400));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 400));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 560));

				// left side top half
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 120, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 240, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 440));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 120, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 160));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 240));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 360));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 400));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 0));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 40));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 80));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 160));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 240));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 240, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 280));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 120));

				// right side top half
				entities.add(new TileEntity(this, "sprites/chocolate.png", 680, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 440));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 160));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 240));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 360));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 400));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 0));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 40));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 80));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 160));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 240));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 280));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 680, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 120));

				// bottom jump and duck
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 600));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 640));
				
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 560));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 600));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 640));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 600));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 640));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 520));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 560));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 600));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 640));


				// floor
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 120, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 240, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 680, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 680));

				// players and goals
				entities.add(goalOne);
				entities.add(goalTwo);
				entities.add(playerOne);
				entities.add(playerTwo);
				break;
			case 8:
				// create players and and put in correct location
				playerOne = new PlayerEntity(this, "sprites/P1F.png", 20, 20, initialFuelLevelOne);
				playerTwo = new PlayerEntity(this, "sprites/P2F.png", 100, 20, initialFuelLevelTwo);

				// create goal location for each player
				goalOne = new GoalEntity("sprites/playerOneExit.png", 1240, 600, playerOne);
				goalTwo = new GoalEntity("sprites/playerTwoExit.png", 1160, 600, playerTwo);

				// bottom floor
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 120, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 240, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 680, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 680));

				// middle floor
				entities.add(new TileEntity(this, "sprites/chocolate.png", 120, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 240, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 680, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 440));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 440));

				// top floor
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 120, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 200, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 240, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 280, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 360, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 400, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 440, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 680, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 200));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 200));

				// top level walls
				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 80));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 160, 160));
				
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 0));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 40));	
				entities.add(new TileEntity(this, "sprites/chocolate.png", 320, 80));

				// top hiding boxes
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 480, 160));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 520, 160));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 680, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 680, 160));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 720, 160));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 160));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 160));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 160));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 160));

				// middle floor platforms
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 240));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 280));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 320));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1040, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1000, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 960, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 920, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 880, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 840, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 800, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 760, 320));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 320));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 360));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 400));
				
				// bottom shot support
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 480));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1080, 520));
				
				// box
				entities.add(new MovableBlockEntity("sprites/box.png", 1040, 360, this));

				// button and door
				door = new DeathEntity("sprites/doorVertical.png", 560, 240, this);
				buttonOne = new ButtonEntity(this, "sprites/button.png", 1040, 301, door);
				buttonTwo = new ButtonEntity(this, "sprites/button.png", 200, 421, door, buttonOne);

				((ButtonEntity) buttonOne).setSecondButton(buttonTwo);
				entities.add(door);
				entities.add(buttonOne);
				entities.add(buttonTwo);

				// shot generators
				entities.add(new ShotGeneratorEntity(this, "sprites/shooterLeft.png", 1240, 80, "sprites/bulletLeft.png", -160, 2000, 'l'));
				entities.add(new ShotGeneratorEntity(this, "sprites/shooterLeft.png", 1040, 480, "sprites/bulletLeft.png", -160, 2000, 'l'));
				entities.add(new ShotGeneratorEntity(this, "sprites/shooterLeft.png", 1040, 640, "sprites/bulletLeft.png", -160, 2000, 'l'));

				// players and goals
				entities.add(goalOne);
				entities.add(goalTwo);
				entities.add(playerOne);
				entities.add(playerTwo);
				break;
			case 9:
				// create players and and put in correct location
				playerOne = new PlayerEntity(this, "sprites/P1F.png", 20, 20, initialFuelLevelOne);
				playerTwo = new PlayerEntity(this, "sprites/P2F.png", 1180, 20, initialFuelLevelTwo);

				// create goal location for each player
				goalOne = new GoalEntity("sprites/playerOneExit.png", 1200, 560, playerOne);
				goalTwo = new GoalEntity("sprites/playerTwoExit.png", 40, 560, playerTwo);
			
				// top left platform
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 120));

				// top right platform
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 120));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 120));

				// middle platform
				entities.add(new TileEntity(this, "sprites/chocolate.png", 560, 360));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 600, 360));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 640, 360));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 680, 360));

				// bottom left platform
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 640));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 640));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 640));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 0, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 40, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 80, 680));

				// bottom right platform
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 640));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 640));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 640));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 680));

				// death floor
				entities.add(new DeathEntity("sprites/slime.png", 120, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 160, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 200, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 240, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 280, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 320, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 360, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 400, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 440, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 480, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 520, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 560, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 600, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 640, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 680, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 720, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 760, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 800, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 840, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 880, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 920, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 960, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 1000, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 1040, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 1080, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 1120, 680, this));

				// left side shooters (top to bottom)
				entities.add(new ShotGeneratorEntity(this, "sprites/shooterRight.png", 0, 200, "sprites/bulletRight.png", 120, 4000, 'r'));
				entities.add(new ShotGeneratorEntity(this, "sprites/shooterRight.png", 0, 440, "sprites/bulletRight.png", 120, 4000, 'r'));

				// right side shooter (top to bottom)
				entities.add(new ShotGeneratorEntity(this, "sprites/shooterLeft.png", 1240, 280, "sprites/bulletLeft.png", -120, 4000, 'l'));
				entities.add(new ShotGeneratorEntity(this, "sprites/shooterLeft.png", 1240, 520, "sprites/bulletLeft.png", -120, 4000, 'l'));

				// players and goals
				entities.add(goalOne);
				entities.add(goalTwo);
				entities.add(playerOne);
				entities.add(playerTwo);
				break;
			case 10:
				// create players and and put in correct location
				playerOne = new PlayerEntity(this, "sprites/P1F.png", 20, 560, initialFuelLevelOne);
				playerTwo = new PlayerEntity(this, "sprites/P2F.png", 1215, 600, initialFuelLevelTwo);

				// create goal location for each player
				goalOne = new GoalEntity("sprites/playerOneExit.png", 20, 40, playerOne);
				goalTwo = new GoalEntity("sprites/playerTwoExit.png", 80, 40, playerTwo);

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
				entities.add(new DeathEntity("sprites/slime.png", 120, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 160, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 200, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 240, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 280, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 320, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 360, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 400, 680, this));

				entities.add(new DeathEntity("sprites/slime.png", 520, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 560, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 600, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 640, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 680, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 720, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 760, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 800, 680, this));

				entities.add(new DeathEntity("sprites/slime.png", 920, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 960, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 1000, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 1040, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 1080, 680, this));
				entities.add(new DeathEntity("sprites/slime.png", 1120, 680, this));
				
				// box that holds p2
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1200, 680));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1240, 680));

				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 640));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 600));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1160, 560));
				entities.add(new TileEntity(this, "sprites/chocolate.png", 1120, 560));

				//door and button
				door = new DeathEntity("sprites/doorHorizontal.png", 1200, 560, this);
				entities.add(door);
				entities.add(new ButtonEntity(this, "sprites/button.png", 1120, 541, door));

				// shooter
				entities.add(new ShotGeneratorEntity(this, "sprites/shooterLeft.png", 1120, 600, "sprites/bulletLeft.png", -120, 3000, 'l'));

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

				entities.add(new ShotGeneratorEntity(this, "sprites/shooterRight.png", 80, 160, "sprites/bulletRight.png", 120, 3000, 'r'));

				//add players and goal
				entities.add(goalOne);
				entities.add(goalTwo);
				entities.add(playerOne);
				entities.add(playerTwo);
				break;
		}
	} // initEntities

	// starts the game when the program is opened
	 public static void main(String[] args) {
		// instantiate this object
		new Game();
	} // main
} // Game
