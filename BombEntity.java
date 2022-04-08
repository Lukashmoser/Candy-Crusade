import java.util.ArrayList;

/** 
* Boom
*/
public class BombEntity extends Entity {

	private double moveSpeed = -300;
	private boolean used = false;
	private Game game;
	private ArrayList entities;

	public BombEntity(Game g, String r, int newX, int newY, ArrayList entitiesIn) {
		super(r, newX, newY);
		game = g;
		dy = moveSpeed;
		entities = entitiesIn;
	}

	public void move() {
		super.move();

		if (y < -100) {
			game.removeEntity(this);
		}
	}

	public void collidedWith(Entity other) {
		if (used) {
			return;
		}

		if (other instanceof AlienEntity) {
			int location = ((AlienEntity) other).getLocation();
			game.removeEntity(this);
			game.removeEntity(other);
			game.notifyAlienKilled();
			
			// remove the entities around the original entity
			if (getAlienByLocation(location + 1) != null) {
				game.removeEntity(getAlienByLocation(location + 1));
				game.notifyAlienKilled();
			}
			if (getAlienByLocation(location - 1) != null) {
				game.removeEntity(getAlienByLocation(location - 1));
				game.notifyAlienKilled();
			}
			if (getAlienByLocation(location - 11) != null) {
				game.removeEntity(getAlienByLocation(location - 11));
				game.notifyAlienKilled();
			}
			if (getAlienByLocation(location - 12) != null) {
				game.removeEntity(getAlienByLocation(location - 12));
				game.notifyAlienKilled();
			}
			if (getAlienByLocation(location - 13) != null) {
				game.removeEntity(getAlienByLocation(location - 13));
				game.notifyAlienKilled();
			}
			if (getAlienByLocation(location + 11) != null) {
				game.removeEntity(getAlienByLocation(location + 11));
				game.notifyAlienKilled();
			}
			if (getAlienByLocation(location + 12) != null) {
				game.removeEntity(getAlienByLocation(location + 12));
				game.notifyAlienKilled();
			}
			if (getAlienByLocation(location + 13) != null) {
				game.removeEntity(getAlienByLocation(location + 13));
				game.notifyAlienKilled();
			}
			used = true;
		}
	}

	// get an alien based off of its beginning location
	public AlienEntity getAlienByLocation(int location) {
		AlienEntity target = null;
		for (int i = 0; i < entities.size(); i++) {
			if (entities.get(i) instanceof AlienEntity && ((AlienEntity) entities.get(i)).getLocation() == location) {
				target = (AlienEntity) entities.get(i);
			}
		}
		return target;
	}

}
