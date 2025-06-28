// the individual end goal of each level
public class GoalEntity extends Entity {

    private Entity target; // the player that has to get to this goal
    
    // constructor
    public GoalEntity(String r, int newX, int newY, Entity newTarget) {
        super(r, newX, newY);
        target = newTarget;
    } // constructor

    // get the entity that has to get to this goal
    public Entity getTarget(){
        return target;
    } // getTarget

    // direct collision checks handled in other entities
    public void collidedWith(Entity other){}
    
} // GoalEntity
