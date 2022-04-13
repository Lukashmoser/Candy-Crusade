public class MovableBlockEntity extends Entity{

	Game game;

    public MovableBlockEntity(String r, int newX, int newY, Game g) {
        super(r, newX, newY);
		game = g;
        //TODO Auto-generated constructor stub
    }

	// allows the player to push the block if it isn't against a wall
public boolean attemptMove(double movement){
		boolean moveStatus = false;
		String collisions = "";

		this.setHorizontalMovement(movement);

		collisions = this.willCollideWithSomething(game.getEntities());

		if((movement < 0 && !(collisions.contains("left"))) || (movement > 0 && !(collisions.contains("right")))) {
			super.move();
			moveStatus = true;
		}

		this.setHorizontalMovement(0);
		
		return moveStatus;
	}

    public void collidedWith(Entity other) {
        // TODO Auto-generated method stub
    }
    
}