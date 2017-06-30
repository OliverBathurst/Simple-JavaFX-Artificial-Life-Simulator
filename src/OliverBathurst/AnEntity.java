package OliverBathurst;

import javafx.scene.image.Image;

/**
 * @author Oliver
 *
 */
abstract class AnEntity {
	protected String species, species2; //strings for species input
	protected double energy2;
	protected char symbol, symbol2; //char symbols for each bug
	protected boolean isherbivore, ismeat,isden,isfoodsource;
	protected Image image = null;	
	protected int x, y, uniID;
	
	protected AnEntity(int ix, int jy, String species, char symbol) {
		ismeat = false;//default
		isherbivore = false;
		energy2 = 5; //standard energy lvl
		species2 = species; //attributes of each bug
		symbol2 = symbol;
		x = ix;
		y = jy;		
	}
	protected void setImage(Image image) {
        this.image = image; //set img
    }
	protected AnEntity() {
		//get details constructor
	}
	protected int getPosX(){
		return x; //returning x coordinates
	}
	protected int getPosY(){
		return y; //returning y coordinate
	}
	protected String toStringA(){
		return "Species: " + species2 + " , Coordinates: (" + x +","+ y+ ")";	
	}
	protected String toText(){
		return "Species: " + species2 + " , Coordinates: (" + x +","+ y+ ")" + " Symbol: " + symbol2;
	}
	protected boolean getAnimalAtPos(int tx, int ty){
		return tx == x && ty == y; //just return x and y
	}
	protected void displayAnimal(GUIInterface guiInterface) {
		guiInterface.showAnimal(image,x, y);
	}	
}