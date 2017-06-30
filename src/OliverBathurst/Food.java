package OliverBathurst;

import java.util.Random;
import javafx.scene.image.Image;

/**
 * @author Oliver
 *
 */
class Food extends AnEntity{
	Food(int ix, int jy, String species, char symbol){
		energy2 = poison(); //get energy value from method
		ismeat = ismeat(); //get whether it is fruit or meat
		species2 = "food"; //attributes of each bug
		symbol2 = '*';		
		x = ix;
		y = jy;		
	}
/**
* 50% chance of being meat/fruit
*/
private boolean ismeat() {
	boolean torf = false;
	Random random = new Random();
	int x = random.nextInt(2); //result can be 0 or 1 (50% chance)
	if (x == 1){
		setImage(new Image(getClass().getResourceAsStream("fruit.png")));
		torf = false; //is fruit etc (not meat)
	}else{
		setImage(new Image(getClass().getResourceAsStream("meat.png")));
		torf = true; //is meat 
	}
	return torf;
}
/**
 * Random chance of being poison (value of -3), otherwise has a calorie range of 1-10
 */
private double poison(){
	double ret;
	Random rn = new Random();
	int res = rn.nextInt(10) + 1;
	if (res >=9){
		ret = -3; //POISON MODIFIER
	}else{
		ret = res; //give food random energy
	}
	return ret;
	}
}