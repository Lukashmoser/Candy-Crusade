public class DeathEntity extends Entity {

    Game game;
    
    public DeathEntity(String r, int newX, int newY, Game g) {
        super(r, newX, newY);
        game = g;
    }

    public void collidedWith(Entity other) {
        if(other instanceof TileEntity || other instanceof MovableBlockEntity || other instanceof DeathEntity){
            game.removeEntity(this);
        }
        
    }
    
}
