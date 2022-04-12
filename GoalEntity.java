public class GoalEntity extends Entity {

    private Entity target;
    
    public GoalEntity(String r, int newX, int newY, Entity newTarget) {
        super(r, newX, newY);
        target = newTarget;
    }

    public Entity getTarget(){
        return target;
    }

    public void collidedWith(Entity other) {
        // TODO Auto-generated method stub
        
    }
    
}
