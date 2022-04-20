// projectiles or static tiles that kill the players
public class DeathEntity extends Entity {

    Game game; // the game in which the deathEntity exists
    
    // constructor
    public DeathEntity(String r, int newX, int newY, Game g) {
        super(r, newX, newY);
        game = g;
    } // constructor

    // if the it collides with a something it doesn't kill remove it from the game
    public void collidedWith(Entity other) {
        if(other instanceof TileEntity || other instanceof MovableBlockEntity || other instanceof DeathEntity){
            game.removeEntity(this);
        }
        
    } // collidedWith
    
} // DeathEntity
