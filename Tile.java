public class TileEntity extends Entity {

    private Game game;

    public TileEntity(Game g, String r, int newX, int newY, int location) {
        super(r, newX, newY);
        gridLocation = location;
    }

    public int getLocation() {
		return gridLocation;
	}
}