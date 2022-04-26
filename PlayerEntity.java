// an individual player
public class PlayerEntity extends Entity {

	private Game game; // the game in which the player exists

	private double fuelLevel; // the amount of fuel the player has left


	// constructor
	public PlayerEntity(Game g, String r, int newX, int newY, double newFuelLevel) {
		super(r, newX, newY); // calls the constructor in Entity
		game = g;
		fuelLevel = newFuelLevel;
	} // constructor

	// get the players fuel level
	public double getFuelLevel(){
		return fuelLevel;
	} // getFuelLevel

	// update the players fuel level
	public void setFuelLevel(double newLevel){
		fuelLevel = newLevel;
	} // setFuelLevel

	// move the player based on its movement speed and the time since last movement
	public void move() {
		// stop at left side of screen
		if ((dx < 0) && (x < 0)) {
			this.setHorizontalMovement(0);
		} // if
		
		// stop at right side of screen
		if ((dx > 0) && (x > 1216)) {
			this.setHorizontalMovement(0);
		} // if

		// stop at top of screen
		if ((dy < 0) && (y < 0)) {
			this.setVerticalMovement(0);
		} // if

		// stop at bottom of screen
		if ((dy > 0) && (y > 680)){
			this.setVerticalMovement(0);
		}

		super.move(); // calls the move method in Entity
	} // move

	// handles collisions with GoalEntity and DeathEntity
	public void collidedWith(Entity other) {
		// if it collides with the correct goal entity register complete
		if(other instanceof GoalEntity){
			if(((GoalEntity) other).getTarget() == this){
				game.registerComplete(this);
			}
		} 
		
		// if it collides with a DeathEntity notify the game of the players death
		else if (other instanceof DeathEntity){
			game.notifyDeath();
		}

	} // collidedWith

} // PlayerEntity