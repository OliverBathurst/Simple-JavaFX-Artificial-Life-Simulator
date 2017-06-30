package OliverBathurst;

import javafx.scene.image.Image;

/**
 * @author Oliver
 *
 */
class Obstacles extends AnEntity{
	Obstacles(int ix, int jy, String species, char symbol){
		energy2 = 0;
		species2 = "obs"; //attributes of each bug
		symbol2 = '#';
		isden = false;//special attributes
		isfoodsource = false;
		x = ix;
		y = jy;	
		setImage(new Image(getClass().getResourceAsStream("obstacles.png")));//set image
	}
}
