public class TileEntity extends Entity {

    private Game game;
    
    private String type;


    public TileEntity(Game g, String r, int newX, int newY, String newType) {
        super(r, newX, newY);

        game = g;

        type = newType;
    }

    public String getTileType(){
        return type;
    }

    public void setTileType(String newType){
        type = newType;
    }

    public void collidedWith(Entity other){}

}