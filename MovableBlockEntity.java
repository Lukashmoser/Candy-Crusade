// a block or other object that the player can push around
public class MovableBlockEntity extends Entity{

	Game game; // the game in which the block exists

	// constructor
    public MovableBlockEntity(String r, int newX, int newY, Game g) {
        super(r, newX, newY);
		game = g;
    } // constructor

	// allows the player to push the block if it isn't against a wall
	public boolean attemptMove(double movement){
		boolean moveStatus = false; // whether or not the box was able to move
		String collisions = ""; // the directions that it will collide with something

		this.setHorizontalMovement(movement); // set the boxes movement speed to the movment speed of the player pushing it

		collisions = this.willCollideWithSomething(game.getEntities()); // register the potential collisions for the box

		// if there are no collisons in the direction it is being pushed allow it to move
		if((movement < 0 && !(collisions.contains("left"))) || (movement > 0 && !(collisions.contains("right")))) {
			super.move();
			moveStatus = true;
		}

		this.setHorizontalMovement(0); // allow it only move when pushed
		
		return moveStatus;
	} // attemptMove

	// direct collisions handled in other entities
    public void collidedWith(Entity other) {}
    
} // MoveableBlockEntity