// platforms and walls that the player can stand on and run into
public class TileEntity extends Entity {

    private Game game; // the game that this entity exists in


    public TileEntity(Game g, String r, int newX, int newY) {
        super(r, newX, newY);
        game = g;
    }

    public void collidedWith(Entity other){}

}