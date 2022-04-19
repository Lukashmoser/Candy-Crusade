public class ButtonEntity extends Entity {

    Entity target;
    Game game;
    boolean pressed = false;
    Entity secondButton;
    int originX;
    int originY;

    public ButtonEntity(Game g, String r, int newX, int newY, Entity newTarget) {
        super(r, newX, newY);
        target = newTarget;
        game = g;
        originX = newTarget.getX();
        originY = newTarget.getY();
    }

    public ButtonEntity(Game g, String r, int newX, int newY, Entity newTarget, Entity newSecondButton) {
        super(r, newX, newY);
        target = newTarget;
        game = g;
        originX = newTarget.getX();
        originY = newTarget.getY();
        secondButton = newSecondButton;
    }

    public boolean getPressed(){
        return pressed;
    }

    public Entity getTarget(){
        return target;
    }

    public int getOriginX(){
        return originX;
    }

    public int getOriginY(){
        return originY;
    }

    public Entity getSecondButton(){
        return secondButton;
    }

    public void setPressed(boolean state){
        pressed = state;
    }

    public void setSecondButton(Entity newSecondButton){
        secondButton = newSecondButton;
    }

    public void collidedWith(Entity other) {
        if(other instanceof PlayerEntity){
            pressed = true;
            this.setSprite("sprites/death.png");
        }
        
    }
    
}
