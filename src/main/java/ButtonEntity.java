// allows for the players to open doors through interaction with buttons
public class ButtonEntity extends Entity {

    Entity target; // door that the button opens
    Game game; // the game in which the button exists
    boolean pressed = false; // state of the button
    Entity secondButton; // other button that opens the door if there is one
    int originX; // original x location of the door
    int originY; // original y location of the door

    // constructor if the there is not second button
    public ButtonEntity(Game g, String r, int newX, int newY, Entity newTarget) {
        super(r, newX, newY);
        target = newTarget;
        game = g;
        originX = newTarget.getX();
        originY = newTarget.getY();
    } // constructor

    // constructor for if there is a second button
    public ButtonEntity(Game g, String r, int newX, int newY, Entity newTarget, Entity newSecondButton) {
        super(r, newX, newY);
        target = newTarget;
        game = g;
        originX = newTarget.getX();
        originY = newTarget.getY();
        secondButton = newSecondButton;
    } // constructor

    // get the state of the button
    public boolean getPressed(){
        return pressed;
    } // getPressed

    // get the target door of the button
    public Entity getTarget(){
        return target;
    } // getTarget

    // get the original x location of the door
    public int getOriginX(){
        return originX;
    } // getOriginX

    // get the original y location of the door
    public int getOriginY(){
        return originY;
    } // getOriginY

    // get the second button that is associated with the same door
    public Entity getSecondButton(){
        return secondButton;
    } // getSecondButton

    // set the state of the button
    public void setPressed(boolean state){
        pressed = state;
    } // setPressed

    // set the second button that is associated with the same door
    public void setSecondButton(Entity newSecondButton){
        secondButton = newSecondButton;
    } // setSecondButton

    // if the player collides with the button set it as pressed and change its sprite
    public void collidedWith(Entity other) {
        if(other instanceof PlayerEntity || other instanceof MovableBlockEntity){
            pressed = true;
            this.setSprite("sprites/buttonPressed.png");
        }
        
    } // collidedWith
    
} // ButtonEntity
