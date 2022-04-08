
/* Entity.java
 * An entity is any object that appears in the game.
 * It is responsible for resolving collisions and movement.
 */

import java.awt.*;
import java.util.ArrayList;

public abstract class Entity {

	// Java Note: the visibility modifier "protected"
	// allows the variable to be seen by this class,
	// any classes in the same package, and any subclasses
	// "private" - this class only
	// "public" - any class can see it

	protected double x; // current x location
	protected double y; // current y location
	private Sprite sprite; // this entity's sprite
	protected double dx; // horizontal speed (px/s) + -> right
	protected double dy; // vertical speed (px/s) + -> down

	private long delta;

	private Rectangle me = new Rectangle(); // bounding rectangle of
											// this entity
	private Rectangle him = new Rectangle(); // bounding rect. of other
												// entities

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
		// update location of entity based ov move speeds
		x += (delta * dx) / 1000;
		y += (delta * dy) / 1000;
	} // move

	public void setDelta(long newDelta) {
		delta = newDelta;
	}
	// get and set velocities
	public void setHorizontalMovement(double newDX) {
		dx = newDX;
	} // setHorizontalMovement

	public void setVerticalMovement(double newDY) {
		dy = newDY;
	} // setVerticalMovement

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

	/*
	 * Draw this entity to the graphics object provided at (x,y)
	 */
	public void draw(Graphics g) {
		sprite.draw(g, (int) x, (int) y);
	} // draw

	/*
	 * Do the logic associated with this entity. This method will be called
	 * periodically based on game events.
	 */
	public void doLogic() {
	}

	/*
	 * collidesWith input: the other entity to check collision against output: true
	 * if entities collide purpose: check if this entity collides with the other.
	 */
	public boolean collidesWith(Entity other) {
		me.setBounds((int) x, (int) y, sprite.getWidth(), sprite.getHeight());
		him.setBounds(other.getX(), other.getY(), other.sprite.getWidth(), other.sprite.getHeight());
		return me.intersects(him);
	} // collidesWith

	public String willCollideWithTile(ArrayList entities) {
		String result = "";
		
		for(int i = 0; i < entities.size(); i++){
			if(entities.get(i) instanceof TileEntity) {
				him.setBounds(((Entity) entities.get(i)).getX(), ((Entity) entities.get(i)).getY(), ((Entity)entities.get(i)).sprite.getWidth(), ((Entity)entities.get(i)).sprite.getHeight());
				
				// checks left for collisions
				me.setBounds((int) x + ((int) (delta * dx) / 1000) - 2, (int) y, sprite.getWidth(), sprite.getHeight());
				if(me.intersects(him)){
					result += "left";
				}

				// checks top for collisions
				me.setBounds((int) x, (int) y - (int) (delta * 200) / 1000 - 2, sprite.getWidth(), sprite.getHeight());
				if(me.intersects(him)){
					result += "top";
				}

				// checks right for collisions
				me.setBounds((int) x, (int) y, sprite.getWidth() + (int) (delta * dx) / 1000 + 2, sprite.getHeight());
				if(me.intersects(him)){
					result += "right";
				}

				// checks bottom for collisions
				me.setBounds((int) x, (int) y, sprite.getWidth(), sprite.getHeight() + (int) (delta * 100) / 1000 + 2);
				if(me.intersects(him)){
					result += "bottom";
				}
			}	
		}

		return result;
	}

	/*
	 * collidedWith input: the entity with which this has collided purpose:
	 * notification that this entity collided with another Note: abstract methods
	 * must be implemented by any class that extends this class
	 */
	public abstract void collidedWith(Entity other);

	public void changeSprite(String r){
		sprite = (SpriteStore.get()).getSprite(r);
	}

} // Entity class