/* ShipEntity.java
 * March 27, 2006
 * Represents player's ship
 */
public class PlayerEntity extends Entity {

	private Game game; // the game in which the ship exists

	private double fuelLevel;


	/*
	 * construct the player's ship input: game - the game in which the ship is being
	 * created ref - a string with the name of the image associated to the sprite
	 * for the ship x, y - initial location of ship
	 */
	public PlayerEntity(Game g, String r, int newX, int newY, double newFuelLevel) {
		super(r, newX, newY); // calls the constructor in Entity
		game = g;
		fuelLevel = newFuelLevel;
	} // constructor

	public double getFuelLevel(){
		return fuelLevel;
	}

	public void setFuelLevel(double newLevel){
		fuelLevel = newLevel;
	}

	/*
	 * move input: delta - time elapsed since last move (ms) purpose: move ship
	 */
	public void move() {
		// stop at left side of screen
		if ((dx < 0) && (x < 0)) {
			this.setHorizontalMovement(0);
		} // if
		
		// stop at right side of screen
		if ((dx > 0) && (x > 1240)) {
			this.setHorizontalMovement(0);
		} // if

		// stop at top of screen
		if ((dy < 0) && (y < 0)) {
			this.setVerticalMovement(0);
		} // if

		// stop at bottom of screen
		if ((dy > 0) && (y > 680)){

		}


		super.move(); // calls the move method in Entity
	} // move

	/*
	 * collidedWith input: other - the entity with which the ship has collided
	 * purpose: notification that the player's ship has collided with something
	 */
	public void collidedWith(Entity other) {
		if(other instanceof GoalEntity){
			if(((GoalEntity) other).getTarget() == this){
				game.registerComplete(this);
			}
		} else if (other instanceof DeathEntity){
			game.notifyDeath();
		}
	} // collidedWith

} // ShipEntity class