package OliverBathurst;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.StageStyle;

class AnInterface extends AWorld{
	protected static AWorld newarena; //new world to add to
	
	/**
	 * Updates positions for each entity by calling scanArea
	 */
	protected void update() {
		for (int i=0;i<AWorld.size();i++){
			scanArea(randDirection(), AWorld.get(i).x, AWorld.get(i).y, i); //update positions by sending animals and a direction
		}
	}
	/**
	 * Contains all directions used by entities
	 */
	protected enum Direction {
	    N,S,E,W //set up all directions
	}
	protected static Direction randDirection() { //function for getting random direction
		Direction[] values = Direction.values();
		return values[(int) (Math.random() * 4)]; //return the direction
    }
	protected static void authHelp() {	///An option in the GUI///
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("Author");
		alert.setHeaderText(null);
		alert.setContentText("Author: Oliver Bathurst"); //display author
		alert.showAndWait();
	}
	protected static void appHelp() {///Printed instructions (A GUI menu option)	
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("App help");
		alert.setHeaderText(null);
		alert.setContentText("Use the letters and numbers printed on-screen and enter your selection into the console below, then press the enter button, "
				+ "to exit press the x button at any point." + "\n"+ "If you are using the GUI, use the menus at the top an make a selection, and then follow the on-screen prompts.");
		alert.showAndWait();
	}
	/**
	 * Method to create new file
	 */
	protected static void tryNew() {	
	try {
		File fileNew = new File("WorldConfig.txt");	//create new config file
		FileWriter w;		
		w = new FileWriter(fileNew.getAbsolutePath()); //new filewriter with filepath
		BufferedWriter writer =new BufferedWriter(w); //create buffered writer with filewriter
		writer.write("World");
		writer.newLine();		//write header
				
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Text Input Dialog");
		dialog.setHeaderText("Text Input Dialog");
		dialog.setContentText("Enter x dimension");//ask for input
		Optional<String> result = dialog.showAndWait();
		
		TextInputDialog dialog2 = new TextInputDialog("");
		dialog2.setTitle("Text Input Dialog");
		dialog2.setHeaderText("Text Input Dialog");
		dialog2.setContentText("Enter y dimension");
		Optional<String> result2 = dialog2.showAndWait();
		
		TextInputDialog dialog3 = new TextInputDialog("");
		dialog3.setTitle("Text Input Dialog");
		dialog3.setHeaderText("Text Input Dialog");
		dialog3.setContentText("Enter maximum number of entities");
		Optional<String> result3 = dialog3.showAndWait();
		
		int x = Integer.parseInt(result.get()); //try parsing strings to integers
		int y = Integer.parseInt(result2.get());
		int max = Integer.parseInt(result3.get());

		writer.write(x + ","+ y +","+ max);//write world parameters to file
		writer.newLine();
		writer.write("Colours");
		writer.write(Color.WHITE + ","+ Color.BLACK); //write colours
		writer.newLine();
		writer.close(); //close writer
		
		GUIInterface.e = Color.WHITE;//setup default colours
		GUIInterface.f = Color.BLACK;
		File file2 = new File("config.txt"); //create new config file
		FileWriter fw = new FileWriter(file2.getAbsoluteFile());//write the filepath into it
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(fileNew.getAbsolutePath());
		bw.close();//close writer
		fw.close();
	} catch (IOException e) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("Information Dialog");
		alert.setHeaderText(null);
		alert.setContentText("Error while saving");
		alert.showAndWait();
	}
	}
	protected static void savConfigAs() {
		FileChooser fileChooser = new FileChooser(); //open filechooser
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt")); //add filter
		fileChooser.setTitle("Specify a file to save");   	
		fileChooser.getExtensionFilters();
		File userSelection = fileChooser.showSaveDialog(null); 
		if (userSelection !=null) { //if a file has been chosen
			write(userSelection.getAbsolutePath());//pass filepath to be written to
		}	
	}
	protected static void savConfig() throws IOException {
		try{
			if(Files.exists(Paths.get("config.txt"))) {
				try{
					BufferedReader buff = new BufferedReader(new FileReader("config.txt"));//read in file
					write(buff.readLine());
					buff.close();								
				}catch(Exception e){
					write(new File("WorldConfig.txt").getAbsolutePath());//pass the filepath
				}
			}else{
				write(new File("WorldConfig.txt").getAbsolutePath());//pass the filepath
			}
		}catch(Exception ignored){}
	}
	/**
	 * Writes information to file (passed a file path as a parameter)
	 */
	private static void write(String a){
	FileWriter output=null;
	try {
		File file = new File(a);	//get file at the path
		output= new FileWriter(file);//new FileWriter
	
		BufferedWriter writer=new BufferedWriter(output);
		writer.write("World");
		writer.newLine();//write world params
		writer.write(OliverBathurst.AWorld.xdimen + ","+ OliverBathurst.AWorld.ydimen +","+ OliverBathurst.AWorld.maxanimals);
		writer.newLine();
		writer.write("Colours");
		writer.newLine();//write colours from file
		writer.write(GUIInterface.e + ","+ GUIInterface.f);
		writer.newLine();
		writer.write("Animals");
		writer.newLine();
		for (int i=0;i<AWorld.size();i++){ //loop through all entities
			if(AWorld.get(i).symbol2 !='*' && AWorld.get(i).symbol2 !='#' && AWorld.get(i).energy2 > 0){
				writer.write(AWorld.get(i).species2 + ","+ AWorld.get(i).symbol2 +","+ AWorld.get(i).energy2 + "," + AWorld.get(i).x+ "," + AWorld.get(i).y+ "," 
					  + AWorld.get(i).isherbivore + "\n");
			}else if (AWorld.get(i).symbol2 == '*'){
				writer.write(AWorld.get(i).species2 + ","+ AWorld.get(i).symbol2 +","+ AWorld.get(i).energy2 + "," + AWorld.get(i).x+ "," + AWorld.get(i).y + "," 
					  + AWorld.get(i).ismeat + "\n");
			}else if (AWorld.get(i).symbol2 == '#'){
				writer.write(AWorld.get(i).species2 + ","+ AWorld.get(i).symbol2 +","+ AWorld.get(i).energy2 + "," + AWorld.get(i).x+ "," + AWorld.get(i).y+ "," 
					  + AWorld.get(i).isden+ "," + AWorld.get(i).isfoodsource);
			}
		}
		writer.close();//close writer
	  
		File file2 = new File("config.txt"); //create new config file
		FileWriter fw = new FileWriter(file2.getAbsoluteFile());//setup file for writing
		BufferedWriter bw = new BufferedWriter(fw);

		bw.write(file.getAbsolutePath());//write config file path to config file
		bw.close();//close writer
	  
	} catch (Exception e) {		  
		    Alert alert = new Alert(AlertType.INFORMATION);
			alert.initStyle(StageStyle.UTILITY);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Error while saving");
			alert.showAndWait();
		} finally {
		  if (output != null) {
		  try {
		    output.flush(); //flush writer
		    output.close();//close it
		  } catch (IOException ignored) {}
		 }
		}	
	}
	protected static void loadConfig(AWorld a) {
	FileChooser chooser = new FileChooser(); //new open file dialog
	chooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"));
    File returnVal = chooser.showOpenDialog(null);
    if(returnVal !=null) { //if a file has been chosen
       		returnVal.getAbsolutePath();   	      		      	  
       		try {
       			File file2 = new File("config.txt"); //create new config file
           		FileWriter fw = new FileWriter(file2.getAbsoluteFile());//setup file for writing
           		BufferedWriter bw = new BufferedWriter(fw);
				bw.write(returnVal.getAbsolutePath());
				bw.close();//close writer
			} catch (IOException e) {			
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error Dialog");
				alert.setHeaderText(null);
				alert.setContentText("Last config failed to load: " + e.getMessage());
				alert.showAndWait();
			}//write config file path to config file
    	}
	}    
	private static void loadColour(String colour,AWorld a) {
		String colload[] = colour.split(","); //split string by comma	
		try {
			GUIInterface.e = Color.valueOf(colload[0]); //setup GUI color
		} catch (Exception ignored) {}	
		try{
			GUIInterface.f = Color.valueOf(colload[1]);
		}catch(Exception ignored){}	
	}
	/**
	 * LastConfig is used a lot to load the last known config (when loading and restarting)
	 */
	protected static void lastConfig(AWorld a){
	try{
		if(Files.exists(Paths.get("config.txt"))) { //if a config file exists
		try {
			BufferedReader buff = new BufferedReader(new FileReader("config.txt"));//read in file
			String text = buff.readLine();//read in the filepath line (1st and only line)
			loadWorld(Files.readAllLines(Paths.get(text)).get(1),a);//pass to loadWorld
   			loadColour(Files.readAllLines(Paths.get(text)).get(3),a);
						
			int count = 0;
   			BufferedReader reader = new BufferedReader(new FileReader(text));//open reader
   			while (reader.readLine() != null) {
                count++;//count all lines
          	}
   			for (int i=0;i<AWorld.size();i++){//take existing entities away
   				AWorld.remove(i);
   			}
   			for(int i=5;i<count;i++){
   				loadAnimals(Files.readAllLines(Paths.get(text)).get(i), i-5,a);//pass/load animals in
   			}
   			reader.close();
   			buff.close();
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Error Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Last config failed to load: " + e.getMessage());
			alert.showAndWait();
		}
	}else{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Error Dialog");
		alert.setHeaderText(null);
		alert.setContentText("Last config failed to load");
		alert.showAndWait();
	}
	}catch (Exception ignored){}
}
	private static void loadAnimals(String line2, int ID,AWorld a) {
	try{
		String passanim[] = line2.split(","); //split it up
		String passspec = passanim[0];
		char passsym = passanim[1].charAt(0);
		double passenergy = Double.parseDouble(passanim[2]);
		int passx = Integer.parseInt(passanim[3]);
		int passy = Integer.parseInt(passanim[4]);
				
		if (passsym == '*'){	//if entity is food		
			boolean pass5 = Boolean.parseBoolean(passanim[5]); //if it's meat
			a.addAnimal(passspec, passsym,a); //add it 
			AWorld.get(ID).energy2 = passenergy; //setup its variables
			AWorld.get(ID).x = passx;
			AWorld.get(ID).y = passy;
			AWorld.get(ID).ismeat = pass5;
			if (pass5 == true){				//if it's meat, set meat icon
				Image image = new Image("/meat.png");
				AWorld.get(ID).image = image;
			}else if (pass5 == false){
				Image image = new Image("/fruit.png");
				AWorld.get(ID).image = image;
			}
		}else if (passsym == '#'){ //if obstacle
			boolean isden = Boolean.parseBoolean(passanim[5]);//boolean if it's den
			boolean isfs = Boolean.parseBoolean(passanim[6]);//boolean if it's a foodsource
			a.addAnimal(passspec, passsym,a);
			AWorld.get(ID).x = passx;
			AWorld.get(ID).y = passy;
			AWorld.get(ID).isden = isden;
			AWorld.get(ID).isfoodsource = isfs;
			if (isden == true){
				Image image = new Image("/den.png");
				AWorld.get(ID).image = image;
			}else if (isfs == true){
				Image image = new Image("/foodsource.png");
				AWorld.get(ID).image = image;
			}else{
				Image image = new Image("/obstacles.png");
				AWorld.get(ID).image = image;
			}
		}else{
			boolean pass5 = Boolean.parseBoolean(passanim[5]);
			a.addAnimal(passspec, passsym,a);
			AWorld.get(ID).energy2 = passenergy;
			AWorld.get(ID).x = passx;
			AWorld.get(ID).y = passy;
			AWorld.get(ID).isherbivore = pass5;
			
			if (pass5 == true){
				Image image = new Image("/herbanimal.png");
				AWorld.get(ID).image = image;
			}else{
				Image image = new Image("/animal.png");
				AWorld.get(ID).image = image;
			}
		}
	}catch(Exception ignored){}
	}
	@SuppressWarnings("static-access")
	private static void loadWorld(String line1,AWorld a) {
	String[] parts = line1.split(","); //split string up
	try{
		a.xdimen = Integer.parseInt(parts[0]); //setup variable values
		a.ydimen = Integer.parseInt(parts[1]); 
		a.maxanimals = Integer.parseInt(parts[2]);		
	}catch(Exception ignored){}		
	}
	protected static void modifyLife(int ID) {
	try{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText(null);
		alert.setContentText(AWorld.get(ID).toText()); //show current attributes
		alert.showAndWait();		
		
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Text Input Dialog");
		dialog.setHeaderText("Text Input Dialog");
		dialog.setContentText("Enter new species: "); //ask for new species
		Optional<String> result = dialog.showAndWait();
		AWorld.get(ID).species2 = result.get(); //try to set species
		
		TextInputDialog dialog2 = new TextInputDialog("");
		dialog2.setTitle("Text Input Dialog");
		dialog2.setHeaderText("Text Input Dialog");
		dialog2.setContentText("Enter new symbol: ");//ask for new symbol
		Optional<String> result2 = dialog2.showAndWait();
		AWorld.get(ID).symbol2 = result2.get().charAt(0); //try to set symbol as new char
		
		Alert alertnew = new Alert(AlertType.INFORMATION);
		alertnew.setTitle("Information Dialog");
		alertnew.setHeaderText(null);
		alertnew.setContentText(AWorld.get(ID).toText()); //show new attributes
		alertnew.showAndWait();	
	}catch(Exception e){
		Alert alertnew = new Alert(AlertType.INFORMATION);
		alertnew.setTitle("Information Dialog");
		alertnew.setHeaderText(null);
		alertnew.setContentText("Cannot find lifeform: ");
		alertnew.showAndWait();	
	}		
	}
	protected static void tryRemove(int ID) {
		try{		
			AWorld.remove(ID);		
		}catch(Exception e){
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Error removing entity: " + e.getMessage()); //display error
			alert.showAndWait();
		}
	}
	protected static void editWorld() {
	final int pastEnt = maxanimals; //store value of maxents before changes are made
	Alert alert = new Alert(AlertType.INFORMATION);
	alert.setTitle("Information Dialog");
	alert.setHeaderText(null);
	alert.setContentText("Current world setup: "+xdimen+"," + ydimen + " max ents: " +maxanimals);
	alert.showAndWait();//notify user
	try{
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Text Input Dialog");
		dialog.setHeaderText("Text Input Dialog");
		dialog.setContentText("Enter x dimension: "); //new text input dialog 
		Optional<String> result = dialog.showAndWait();
		OliverBathurst.AWorld.xdimen = Integer.parseInt(result.get());//parse and setup variable
		
		TextInputDialog dialog2 = new TextInputDialog("");
		dialog2.setTitle("Text Input Dialog");
		dialog2.setHeaderText("Text Input Dialog");
		dialog2.setContentText("Enter y dimension: ");
		Optional<String> result2 = dialog2.showAndWait();
		OliverBathurst.AWorld.ydimen = Integer.parseInt(result2.get());
		
		TextInputDialog dialog3 = new TextInputDialog("");
		dialog3.setTitle("Text Input Dialog");
		dialog3.setHeaderText("Text Input Dialog");
		dialog3.setContentText("Enter max entities: ");
		Optional<String> result3 = dialog3.showAndWait();
		OliverBathurst.AWorld.maxanimals = Integer.parseInt(result3.get());
		
		for(int i = 0;i<AWorld.size();i++){ //loop, deleting all entities that don't fit in new dimensions
			if (AWorld.get(i).x > xdimen || AWorld.get(i).y > ydimen){
				AWorld.remove(i);
			}
		}
		if (pastEnt > maxanimals){//if new max entity value is smaller than the last
			for(int i=pastEnt;i>AWorld.size();i--){//work from old limit down to new one, deleting
				AWorld.remove(i);
			}
		}	
	}catch(Exception ignored){}
	}
}