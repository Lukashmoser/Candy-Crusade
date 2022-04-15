/* ShotEntity.java
 * March 27, 2006
 * Represents player's ship
 */
public class ShotGeneratorEntity extends Entity {

	private Game game; // the game in which the ship exists

	private String shotSprite; // the type of projectile it shoots
	private int shotMoveSpeed; // movement speed of the shot
	private int shootingInterval; // how often it shoots
	private double timeOfLastShot; // how long ago the generator last shot
	private char directionOfShot; // what direction the generator shoots


	/*
	 * construct the shot input: game - the game in which the shot is being created
	 * ref - a string with the name of the image associated to the sprite for the
	 * shot x, y - initial location of shot
	 */
	public ShotGeneratorEntity(Game g, String r, int newX, int newY, String shotType, int newShotSpeed, int newShootingInterval, char newDirectionOfShot) {
		super(r, newX, newY); // calls the constructor in Entity
		game = g;
		shotSprite = shotType;
		shotMoveSpeed = newShotSpeed;
		shootingInterval = newShootingInterval;
		directionOfShot = newDirectionOfShot;
	} // constructor

	public String getShotSprite(){
		return shotSprite;
	}

	public int getShotMoveSpeed(){
		return shotMoveSpeed;
	}

	public int getShootingInterval(){
		return shootingInterval;
	}

	public double getTimeOfLastShot(){
		return timeOfLastShot;
	}

	public char getDirectionOfShot(){
		return directionOfShot;
	}

	public void setTimeOfLastShot(double time){
		timeOfLastShot = time;
	}

	/*
	 * collidedWith input: other - the entity with which the shot has collided
	 * purpose: notification that the shot has collided with something
	 */
	public void collidedWith(Entity other) {
	} // collidedWith

} // ShipEntity class