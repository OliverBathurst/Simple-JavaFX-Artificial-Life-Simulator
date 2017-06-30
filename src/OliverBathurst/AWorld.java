package OliverBathurst;

import java.util.ArrayList;
import java.util.Random;
import OliverBathurst.AnInterface.Direction;
/**
 * @author Oliver
 *
 */
class AWorld extends AnEntity{
	protected static int xdimen =10, ydimen=10, maxanimals = 20;		// number of entities
	protected static ArrayList<AnEntity> AWorld;					// array of entities
	private Random rng; //(random no. gen)
	/**
	 * AWorld constructor
	 */
	protected AWorld(){
		xdimen = 10;//sets default size and maximum number of entities
		ydimen = 10;
		maxanimals = 20;
	}
	protected AWorld(int a, int b, int c){
		xdimen = a;								// store sizes
		ydimen = b;
		maxanimals = c;
		AWorld = new ArrayList<AnEntity>(maxanimals);
		rng = new Random(); //for creating new random bugs at a random pos
	}
	protected int getx(){
		return xdimen;	//return x dimension
	}
	protected int gety(){
		return ydimen;	//return y dimension
	}
	protected void moveAnimals() {
			for (int i=0;i<AWorld.size();i++){
				scanArea(AnInterface.randDirection(), AWorld.get(i).x, AWorld.get(i).y, i); //update positions by sending animals and a direction
			}			
		}	
	protected int GetAnimalAtPos(int x, int y){
		int animalnumberatpos = 0;
		for(int i=0;i<AWorld.size();i++){
			if (AWorld.get(i).getAnimalAtPos(x,y)) {
				animalnumberatpos = i;	
				break;			
			}
		}
		return animalnumberatpos; //will always return 0 as animal ID if nothing found, need to be sure you double check 
	}
	@SuppressWarnings("static-access")
	protected void addAnimal(String species, char symbol,AWorld a) {
		if (GUIInterface.enumEntities() < maxanimals && GUIInterface.enumEntities() <= (a.xdimen*a.ydimen)) {	//e.g. 10x10 grid can only hold 100 entities, can't add 102 for example
			int newX, newY;
			do { ///Finds random space in which no aminal is present (TASK 4.3)//////////
				newX = rng.nextInt(a.xdimen);// within world boundaries
				newY = rng.nextInt(a.ydimen);
			}while (getAnimalAtPos(newX, newY) == true);// try again if already a robot at tryX,tryY
			if (symbol =='*'){//IF FOOD SYMBOL
				AWorld.add(new Food(newX,newY, species, symbol));	
			}else if (symbol == '#'){//IF OBS SYMBOL
				AWorld.add(new Obstacles(newX,newY, species, symbol));
			}else{//OTHERWISE IT IS ANIMAL
				AWorld.add(new Life(newX,newY, species, symbol));	
			}					
		}
	}	
	private boolean isFoodHere(int x, int y){///Used to identify food in surrounding area
		boolean result = false; //default to false
		for(int i=0;i<AWorld.size();i++){
			if (AWorld.get(i).getAnimalAtPos(x,y) != true){
				result =false;
			}else if(AWorld.get(i).symbol2 == '*'){ //if food is here return true
				result = true;
				break;
			}else{
				result =false; //otherwise no food
			}
		}
		return result; 
	}
	protected static boolean doesExist(int x, int y){	///Used in GUI implementation to spawn food///
		boolean res = false;
		for(int i=0;i<AWorld.size(); i++){
			if(AWorld.get(i).getAnimalAtPos(x,y)){
				res = true;//breakif it does exist
				break;		
			}else{
				res = false; //if nothing is there
			}
		}
		return res;
	}
	protected static int enumFood(){ //sum food
		int counter = 0;
		for (int i=0;i<AWorld.size();i++){
			if(AWorld.get(i).symbol2 == '*'){ //if symbol is food, increment
				counter++;
			}
		}
		return counter;
	}
	protected static int enumObstacles(){ //sum obstacles
		int counter = 0;
		for (int i=0;i<AWorld.size();i++){
			if(AWorld.get(i).symbol2 == '#'){//if symbol is obstacle, increment
				counter++;
			}
		}
		return counter;
	}
	/**
	 * Method used to sniff around for food
	 * */
	private int[] sniffAround(int x, int y){ //search for food (1 unit each direction)
		int xret = -1,yret = -1, a = 1;
		boolean north = isFoodHere(x,y+a); //call isFood here, returns boolean
		boolean south = isFoodHere(x,y-a);
		boolean east = isFoodHere(x+a,y);
		boolean west = isFoodHere(x-a,y);
		BREAK:{
		if(north ==true){
			int anim = GetAnimalAtPos(x, y+a); //get entity one unit north
			if (AWorld.get(anim).x == x && AWorld.get(anim).y == y+a){ //verify it is one unit north
				xret=x;//replace entity position with animal
				yret=y+a;
				break BREAK;//break loop
			}
		}else if (south==true){
			int anim = GetAnimalAtPos(x, y-a);
			if (AWorld.get(anim).x == x && AWorld.get(anim).y == y-a){ //verification as that function will return 
				xret=x;
				yret=y-a;
				break BREAK;
			}
		}else if (east==true){
			int anim = GetAnimalAtPos(x+a, y);
			if (AWorld.get(anim).x == x+a && AWorld.get(anim).y == y){
				xret=x+a;
				yret=y;
				break BREAK;
			}
		}else if (west==true){
			int anim = GetAnimalAtPos(x-a, y);
			if (AWorld.get(anim).x == x-a && AWorld.get(anim).y == y){
				xret=x-a;
				yret=y;
				break BREAK;
			}
		}
		}
		return new int[] {xret, yret};
	}
	/**
	 * Method for finding a herbivore around entity
	 * */
	private int[] eatHerbivore(int x, int y){
		int xret = -1,yret = -1, a=1;
		int north = GetAnimalAtPos(x, y+a); //get animals at positions around entity
		int south = GetAnimalAtPos(x, y-a);
		int east = GetAnimalAtPos(x+a, y);
		int west = GetAnimalAtPos(x-a, y);
		BREAK:{
		if (AWorld.get(north).x == x && AWorld.get(north).y == y+a && AWorld.get(north).isherbivore==true){ //north
			xret = x; //if the entity is in the correct position and it's a herbivore and not deleted
			yret = y+a;
			break BREAK;
		}else if(AWorld.get(south).x == x && AWorld.get(south).y == y-a && AWorld.get(south).isherbivore==true){
			xret=x;
			yret=y-a;
			break BREAK;
		}else if(AWorld.get(east).x == x+a && AWorld.get(east).y == y && AWorld.get(east).isherbivore==true){
			xret=x+a;
			yret=y;
			break BREAK;
		}else if(AWorld.get(west).x == x-a && AWorld.get(west).y == y && AWorld.get(west).isherbivore==true){
			xret=x-a;
			yret=y;
			break BREAK;
			}	
		}
		return new int[] {xret, yret};
	}
	protected boolean scanArea(Direction n, int xa, int yb, int aworldno){
		String dir = n.toString().toLowerCase(); //convert dir to char
		char _dir = dir.charAt(0);
			if (AWorld.get(aworldno).symbol2 != '*' && AWorld.get(aworldno).symbol2 !='#'){
				
				int i[] = sniffAround(xa, yb);		//sniff for food			
				int anim = GetAnimalAtPos(i[0], i[1]); //get food pos
				int a[] = eatHerbivore(xa,yb); //see if there's a herbivore around
				
				
				switch(_dir){//switch dir
				case 'n':
					ERROR:{
					
					if(yb+1>ydimen-1){ //if it's going off the grid
						break ERROR; //break movement
					}
					
					
					if (a[0]!=-1&&a[1]!=-1 && AWorld.get(aworldno).isherbivore == false){
							int herb = GetAnimalAtPos(a[0], a[1]); //if the entity around it is a herbivore
							AWorld.get(aworldno).x = a[0]; //take entity's position
							AWorld.get(aworldno).y = a[1];  					
							AWorld.get(aworldno).energy2 = AWorld.get(aworldno).energy2 + AWorld.get(herb).energy2; //add energy to animal	
							AWorld.remove(herb);	//delete entities symbol
							break ERROR;							
					}else if (i[0]!=-1&&i[1]!=-1 && AWorld.get(aworldno).isherbivore ==true && AWorld.get(anim).ismeat == false){
							AWorld.get(aworldno).x = i[0];//take fruits position
							AWorld.get(aworldno).y = i[1];	
							AWorld.get(aworldno).energy2 = AWorld.get(aworldno).energy2 + AWorld.get(anim).energy2; //add energy to animal	
							AWorld.remove(anim); //delete fruit
							break ERROR;
					}else if (i[0]!=-1&&i[1]!=-1 && AWorld.get(aworldno).isherbivore == false && AWorld.get(anim).ismeat == true){
							AWorld.get(aworldno).x = i[0];//take meat's position
							AWorld.get(aworldno).y = i[1];
							AWorld.get(aworldno).energy2 = AWorld.get(aworldno).energy2 + AWorld.get(anim).energy2; //add energy to animal	
							AWorld.remove(anim);
							break ERROR;									
					}else if (doesExist(xa, yb+1) == false){	//if there's nothing there
						if (AWorld.get(aworldno).energy2 <=0){//If it runs out of energy, display dead icon
							AWorld.remove(aworldno);			
						}else{
							AWorld.get(aworldno).x = xa; //take empty space
							AWorld.get(aworldno).y = yb+1;
							AWorld.get(aworldno).energy2 = AWorld.get(aworldno).energy2 - 0.0005; //how much health goes down per movement
						}
						break ERROR;//break outer loop
					}else{
						break ERROR;//break outer loop
					}
				}
				break;
				
				case 's':
					ERROR:{
					
					if(yb-1<1){
						break ERROR;
					}
					
					
					if (a[0]!=-1&&a[1]!=-1 && AWorld.get(aworldno).isherbivore == false){
							int herb = GetAnimalAtPos(a[0], a[1]);
							AWorld.get(aworldno).x = a[0];
							AWorld.get(aworldno).y = a[1];	
							AWorld.get(aworldno).energy2 = AWorld.get(aworldno).energy2 + AWorld.get(herb).energy2; //add energy to animal	
							AWorld.remove(herb);
							break ERROR;
					}else if (i[0]!=-1&&i[1]!=-1 && AWorld.get(aworldno).isherbivore ==true && AWorld.get(anim).ismeat == false){
							AWorld.get(aworldno).x = i[0];
							AWorld.get(aworldno).y = i[1];
							AWorld.get(aworldno).energy2 = AWorld.get(aworldno).energy2 + AWorld.get(anim).energy2; //add energy to animal									
							AWorld.remove(anim);
							break ERROR;
					}else if (i[0]!=-1&&i[1]!=-1 && AWorld.get(aworldno).isherbivore == false && AWorld.get(anim).ismeat == true){
							AWorld.get(aworldno).x = i[0];
							AWorld.get(aworldno).y = i[1];
							AWorld.get(aworldno).energy2 = AWorld.get(aworldno).energy2 + AWorld.get(anim).energy2; //add energy to animal									
							AWorld.remove(anim);
							break ERROR;					
					}else if (doesExist(xa, yb-1) == false){					
						if (AWorld.get(aworldno).energy2 <=0){
							AWorld.remove(aworldno);						
						}else{
							AWorld.get(aworldno).x = xa;
							AWorld.get(aworldno).y = yb-1;
							AWorld.get(aworldno).energy2 = AWorld.get(aworldno).energy2 - 0.0005;
						}
						break ERROR;
					}else{
						break ERROR;
					}
				}
				break;
				case 'e':
					ERROR:{
					if(xa+1>xdimen-1){
						break ERROR;
					}
					
					if (a[0]!=-1&&a[1]!=-1 && AWorld.get(aworldno).isherbivore == false){
							int herb = GetAnimalAtPos(a[0], a[1]);
							AWorld.get(aworldno).x = a[0];
							AWorld.get(aworldno).y = a[1];	
							AWorld.get(aworldno).energy2 = AWorld.get(aworldno).energy2 + AWorld.get(herb).energy2; //add energy to animal	
							AWorld.remove(herb);
							break ERROR;
					}else if (i[0]!=-1&&i[1]!=-1 && AWorld.get(aworldno).isherbivore ==true && AWorld.get(anim).ismeat == false){	
							AWorld.get(aworldno).x = i[0];
							AWorld.get(aworldno).y = i[1];
							AWorld.get(aworldno).energy2 = AWorld.get(aworldno).energy2 + AWorld.get(anim).energy2; //add energy to animal			
							AWorld.remove(anim);
							break ERROR;
					}else if (i[0]!=-1&&i[1]!=-1 && AWorld.get(aworldno).isherbivore == false && AWorld.get(anim).ismeat == true){
							AWorld.get(aworldno).x = i[0];
							AWorld.get(aworldno).y = i[1];
							AWorld.get(aworldno).energy2 = AWorld.get(aworldno).energy2 + AWorld.get(anim).energy2; //add energy to animal			
							AWorld.remove(anim);
							break ERROR;		
					}else if (doesExist(xa+1, yb) == false){					
						if (AWorld.get(aworldno).energy2 <=0){
							AWorld.remove(aworldno);						
						}else{
							AWorld.get(aworldno).x = xa+1;
							AWorld.get(aworldno).y = yb;
							AWorld.get(aworldno).energy2 = AWorld.get(aworldno).energy2 - 0.0005;
						}
						break ERROR;
					}else{
						break ERROR;
					}
				}
				break;
				case 'w':
					ERROR:{
					if(xa-1<1){
						break ERROR;
					}
					
					if (a[0]!=-1&&a[1]!=-1 && AWorld.get(aworldno).isherbivore == false){
							int herb = GetAnimalAtPos(a[0], a[1]);
							AWorld.get(aworldno).x = a[0];
							AWorld.get(aworldno).y = a[1];			
							AWorld.get(aworldno).energy2 = AWorld.get(aworldno).energy2 + AWorld.get(anim).energy2; //add energy to animal	
							AWorld.remove(herb);	
							break ERROR;
					}else if (i[0]!=-1&&i[1]!=-1 && AWorld.get(aworldno).isherbivore ==true && AWorld.get(anim).ismeat == false){
							AWorld.get(aworldno).x = i[0];
							AWorld.get(aworldno).y = i[1];			
							AWorld.get(aworldno).energy2 = AWorld.get(aworldno).energy2 + AWorld.get(anim).energy2; //add energy to animal		
							AWorld.remove(anim);
							break ERROR;					
					}else if(i[0]!=-1&&i[1]!=-1 && AWorld.get(aworldno).isherbivore == false && AWorld.get(anim).ismeat == true){
							AWorld.get(aworldno).x = i[0];
							AWorld.get(aworldno).y = i[1];
							AWorld.get(aworldno).energy2 = AWorld.get(aworldno).energy2 + AWorld.get(anim).energy2; //add energy to animal		
							AWorld.remove(anim);
							break ERROR;										
					}else if (doesExist(xa-1, yb) == false){ //add this if breaks					
						if (AWorld.get(aworldno).energy2 <=0){
							AWorld.remove(aworldno);
						}else{
							AWorld.get(aworldno).x = xa-1;
							AWorld.get(aworldno).y = yb;						
							AWorld.get(aworldno).energy2 = AWorld.get(aworldno).energy2 - 0.0005; 
						}
						break ERROR;
					}else{
						break ERROR;
					}
				}
				break;							
				}				
			}
		return false;
	}
	/**
	 * Pass the GUI interface for display
	 */
	protected void showAnimal(GUIInterface guiInterface) {///Pass the GUI interface for display
		for (int i=0; i<AWorld.size(); i++) {
			AWorld.get(i).displayAnimal(guiInterface);//display animals (number of animal times)
		}	
	}	
	}