// generates projectiles based on the given parameters
public class ShotGeneratorEntity extends Entity {

	private Game game; // the game in which the shoteGenerator exists

	private String shotSprite; // the type of projectile it shoots
	private int shotMoveSpeed; // movement speed of the shot
	private int shootingInterval; // how often it shoots
	private double timeOfLastShot; // how long ago the generator last shot
	private char directionOfShot; // what direction the generator shoots


	// constructor
	public ShotGeneratorEntity(Game g, String r, int newX, int newY, String shotType, int newShotSpeed, int newShootingInterval, char newDirectionOfShot) {
		super(r, newX, newY); // calls the constructor in Entity
		game = g;
		shotSprite = shotType;
		shotMoveSpeed = newShotSpeed;
		shootingInterval = newShootingInterval;
		directionOfShot = newDirectionOfShot;
	} // constructor

	// get the shots sprite
	public String getShotSprite(){
		return shotSprite;
	} // getShotSprite

	// get the shots movement speed
	public int getShotMoveSpeed(){
		return shotMoveSpeed;
	} // getShotMoveSpeed

	// get how often the generator should shoot
	public int getShootingInterval(){
		return shootingInterval;
	} // getShootingInterval

	// get the time that the generator last shot
	public double getTimeOfLastShot(){
		return timeOfLastShot;
	} // getTimeOfLastShot

	// get the direction that the generator should shoot
	public char getDirectionOfShot(){
		return directionOfShot;
	} // getDirectionOfShot

	// sets the last time that the generator shot
	public void setTimeOfLastShot(double time){
		timeOfLastShot = time;
	} // setTimeOfLastShot

	// direct collisions handled in other entities
	public void collidedWith(Entity other) {}

} // ShipEntity class