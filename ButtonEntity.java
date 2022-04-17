public class ButtonEntity extends Entity {

    Entity target;
    Game game;
    boolean pressed = false;
    int originX;
    int originY;

    public ButtonEntity(Game g, String r, int newX, int newY, Entity newTarget) {
        super(r, newX, newY);
        target = newTarget;
        game = g;
        originX = newTarget.getX();
        originY = newTarget.getY();
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

    public void setPressed(boolean state){
        pressed = state;
    }

    public void collidedWith(Entity other) {
        if(other instanceof PlayerEntity){
            pressed = true;
            this.setSprite("sprites/death.png");
        }
        
    }
    
}
