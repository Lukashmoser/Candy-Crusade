// an object that appears in the game and deals with detecting collisions
import java.awt.*;
import java.util.ArrayList;

public abstract class Entity {
	protected double x; // current x location
	protected double y; // current y location
	private Sprite sprite; // this entity's sprite
	protected double dx; // horizontal speed (px/s) + -> right
	protected double dy; // vertical speed (px/s) + -> down

	private long delta; // time since last check or move

	private Rectangle me = new Rectangle(); // bounding rectangle of this entity
	private Rectangle him = new Rectangle(); // bounding rectangle of other entities

	/*
	 * Constructor input: reference to the image for this entity, initial x and y
	 * location to be drawn at
	 */
	public Entity(String r, int newX, int newY) {
		x = newX;
		y = newY;
		sprite = (SpriteStore.get()).getSprite(r);
	} // constructor

	/*
	 * move input: delta - the amount of time passed in ms output: none purpose:
	 * after a certain amout of time has passed, update the location
	 */
	public void move() {
		// update location of entity based on move speeds
		x += (delta * dx) / 1000;
		y += (delta * dy) / 1000;
	} // move

	// update time since last move or check
	public void setDelta(long newDelta) {
		delta = newDelta;
	} // setDelta

	// set horizontal velocity
	public void setHorizontalMovement(double newDX) {
		dx = newDX;
	} // setHorizontalMovement

	// sey vertical movement
	public void setVerticalMovement(double newDY) {
		dy = newDY;
	} // setVerticalMovement

	// set sprite to display
	public void setSprite(String r){
		sprite = (SpriteStore.get()).getSprite(r);
	}

	// set x position
	public void setX(int newX){
		x = newX;
	} // setX

	// set y position
	public void setY(int newY){
		y = newY;
	} // setY

	// get current velocity for x and y
	public double getHorizontalMovement() {
		return dx;
	} // getHorizontalMovement

	public double getVerticalMovement() {
		return dy;
	} // getVerticalMovement

	// get position
	public int getX() {
		return (int) x;
	} // getX

	public int getY() {
		return (int) y;
	} // getY

	// get current sprite
	public Sprite getSprite(){
		return sprite;
	}

	/*
	 * Draw this entity to the graphics object provided at (x,y)
	 */
	public void draw(Graphics g) {
		sprite.draw(g, (int) x, (int) y);
	} // draw

	/*
	 * collidesWith input: the other entity to check collision against output: true
	 * if entities collide purpose: check if this entity collides with the other.
	 */
	public boolean collidesWith(Entity other) {
		me.setBounds((int) x, (int) y, sprite.getWidth(), sprite.getHeight());
		him.setBounds(other.getX(), other.getY(), other.sprite.getWidth(), other.sprite.getHeight());
		return me.intersects(him);
	} // collidesWith

	// checks if the entity will collide with something then returns a string with the directions that it will collide with something
	public String willCollideWithSomething(ArrayList entities) {
		String result = "";
		
		for(int i = 0; i < entities.size(); i++){
			// checks if the entity it is being compared to is one that it can't directecly collide with
			if(entities.get(i) instanceof TileEntity || entities.get(i) instanceof MovableBlockEntity || (this instanceof MovableBlockEntity && entities.get(i) instanceof PlayerEntity)) {
				// sets bounding box for entity that is being compared to
				him.setBounds(((Entity) entities.get(i)).getX(), ((Entity) entities.get(i)).getY(), ((Entity)entities.get(i)).sprite.getWidth(), ((Entity)entities.get(i)).sprite.getHeight());
				
				// checks left for collisions
				me.setBounds((int) x + ((int) (delta * dx) / 1000) - 5, (int) y, sprite.getWidth(), sprite.getHeight());
				if(me.intersects(him)){
					if(entities.get(i) instanceof MovableBlockEntity && !(this instanceof MovableBlockEntity)){
						if(!((MovableBlockEntity) entities.get(i)).attemptMove(this.getHorizontalMovement())){
							result += "left";
						}
					} else if(!(entities.get(i) instanceof MovableBlockEntity && this instanceof MovableBlockEntity)){
						result += "left";
					}
				}
				
				// checks top for collisions
				me.setBounds((int) x, (int) y - (int) (delta * 200) / 1000 - 2, sprite.getWidth(), sprite.getHeight());
				if(me.intersects(him)){
					result += "top";
				}

				// checks right for collisions
				me.setBounds((int) x, (int) y, sprite.getWidth() + (int) (delta * dx) / 1000 + 5, sprite.getHeight());
				if(me.intersects(him)){
					if(entities.get(i) instanceof MovableBlockEntity && !(this instanceof MovableBlockEntity)){
						if(!((MovableBlockEntity) entities.get(i)).attemptMove(this.getHorizontalMovement())){
							result += "right";
						}
					} else if(!(entities.get(i) instanceof MovableBlockEntity && this instanceof MovableBlockEntity)){
						result += "right";
					}
				}

				// checks bottom for collisions
				me.setBounds((int) x, (int) y, sprite.getWidth(), sprite.getHeight() + (int) (delta * 100) / 1000 + 2);
				if(me.intersects(him)){
					result += "bottom";
				}
			} // if
		} // for

		return result;
	} // willCollideWithSomething

	// allows for direct collision detection and must be implemented by every child of this class
	public abstract void collidedWith(Entity other);

} // Entity class