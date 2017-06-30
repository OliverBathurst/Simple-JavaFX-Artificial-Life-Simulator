package OliverBathurst;

import java.util.Random;
import javafx.scene.image.Image;

/**
 * @author Oliver
 *
 */
class Life extends AnEntity{
	Life(int ix, int jy, String species, char symbol){
		isherbivore = eatinghabits(); //get its type using the method
		energy2 = 5;
		species2 = species; //attributes of each bug
		symbol2 = symbol;
		x = ix;
		y = jy;				
	}
	private boolean eatinghabits() {
		boolean torf = false;
		Random random = new Random();
		int x = random.nextInt(2); //0 or 1 (50% chance of being herbivore/carnivore)
		if (x == 1){
			setImage(new Image(getClass().getResourceAsStream("herbanimal.png")));
			torf = true; //herbivore
		}else{
			setImage(new Image(getClass().getResourceAsStream("animal.png")));
			torf = false; //is meat eater
		}
		return torf;
	}
}
