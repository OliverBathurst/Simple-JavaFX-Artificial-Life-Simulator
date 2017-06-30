package OliverBathurst;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GUIInterface extends Application {
	protected static int xdimen = 512,ydimen = 512, maxanim = 1000; //default world parameters
	private boolean autosave = false,debug = false,displayateach = true, clickaddf = false,clickadda = false; //add menu items
	protected static AWorld newarena2 = new AWorld(xdimen, ydimen, maxanim);//setup world for use needs to be statically edited
	private Label runningstatus = new Label("Simulation idle"); //show sim status
	private GraphicsContext gc; //global var so can be cleared/modified by other methods
	private Alert alertInfo = new Alert(AlertType.INFORMATION), alert = new Alert(AlertType.CONFIRMATION); //info dialog box
	private int dencount=0,foodsourcecount=0; //counters used as spawn timers
	private AnimationTimer timer; //global timer needed to be used within handlers and methods
	protected static Color e = Color.WHITE;
	protected static Color f = Color.BLACK;//Default font and background colours

	/**
	 * Setting up the menu with buttons, toolbar, and menuitems to be applied to GUI
	 */
	MenuBar setMenu() {
		alertInfo.setTitle("Information Dialog"); //setup dialog box for use
		alertInfo.setHeaderText(null); //no header text
		alertInfo.initStyle(StageStyle.UTILITY); //no icon/ window buttons
		
		alert.initStyle(StageStyle.UTILITY);//setup confirmation box for use
    	alert.setTitle("Confirmation Dialog");
    	alert.setHeaderText(null);
    	/**
    	 * Menus and sub menus initialize here onwards, along with their handlers  
    	 * */
		MenuBar menuBar = new MenuBar();		
		Menu mRun = new Menu("Simulation");	
		MenuItem mStart = new MenuItem("Start");
		MenuItem mPause = new MenuItem("Pause");
		MenuItem mRefresh = new MenuItem("Refresh");
		MenuItem mRestart = new MenuItem("Restart");
		MenuItem mIter = new MenuItem("Display At Each Iteration ON/OFF");
		MenuItem mAddOnClick = new MenuItem("Add Food On Click ON/OFF");
		MenuItem mAddOnClick2 = new MenuItem("Add Animal On Click ON/OFF");
		mStart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
            	doStart();   //start animations         	
            }	
		});			
		mPause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
            	doStop(); //stop animations
            	drawWorld();
            }	
		});
		mRefresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
            	drawWorld(); //redraw canvas
            }	
		});		
		mRestart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {        
            	gc.clearRect(0,  0,  AWorld.xdimen,  AWorld.ydimen); //clear
            	doRestart(); 
            }	
		});		
		mIter.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
            	doStop();
		    	alert.setContentText("Display at each iteration?");
		    	ButtonType buttonOne = new ButtonType("Yes");
		    	ButtonType buttonTwo = new ButtonType("No");
		    	alert.getButtonTypes().setAll(buttonOne, buttonTwo);
		    	
		    	Optional<ButtonType> result = alert.showAndWait();
		    	if (result.get() == buttonOne){
		    		displayateach = true; //set boolean
		    	} else if (result.get() == buttonTwo) {
		    		displayateach = false;
		    	} 
            }	
		});	
		mAddOnClick.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
            	doStop();
		    	alert.setContentText("Add food on click?");
		    	ButtonType buttonOne = new ButtonType("Yes");
		    	ButtonType buttonTwo = new ButtonType("No");
		    	alert.getButtonTypes().setAll(buttonOne, buttonTwo);
		    	
		    	Optional<ButtonType> result = alert.showAndWait();
		    	if (result.get() == buttonOne){
		    		clickaddf = true; //one active clicker at one time
		    		clickadda = false;
		    	} else if (result.get() == buttonTwo) {
		    		clickaddf = false;
		    	} 
            }	     		
		});		
		mAddOnClick2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
            	doStop();
		    	alert.setContentText("Add animal on click?");
		    	ButtonType buttonOne = new ButtonType("Yes");
		    	ButtonType buttonTwo = new ButtonType("No");
		    	alert.getButtonTypes().setAll(buttonOne, buttonTwo);
		    	
		    	Optional<ButtonType> result = alert.showAndWait();
		    	if (result.get() == buttonOne){
		    		clickadda = true;
		    		clickaddf = false;
		    	} else if (result.get() == buttonTwo) {
		    		clickadda = false;
		    	} 
            }	     		
		});		
		Menu mConfig = new Menu("Edit");	
		MenuItem mSet = new MenuItem("Add Animal");
		MenuItem mAddF = new MenuItem("Add Food");
		MenuItem mAddO = new MenuItem("Add Obstacle");
		////////////////////////////////////////
		MenuItem mAddD = new MenuItem("Add Den");
		MenuItem mAddFS = new MenuItem("Add Food Source");
		//////////EXPERIMENTAL////////////////////
		MenuItem mMod = new MenuItem("Modify Life Form Parameters");
		MenuItem mRemove = new MenuItem("Remove Entity");	
		MenuItem mRemoveCurrent = new MenuItem("Remove Current Entity");
		MenuItem mEdit = new MenuItem("Edit World Config");
		mAddD.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	doStop();
            	if (enumEntities() >= AWorld.maxanimals){           		
            		alertInfo.setContentText("Max number of entities reached");
            		alertInfo.showAndWait();
            	}else{
            		try{
            			newarena2.addAnimal("obs", '#',newarena2); //make generic obstacle
            			AWorld.AWorld.get(AWorld.AWorld.size()-1).isden = true; //set up as den
            			Image image = new Image("/den.png"); //set image
            			AWorld.AWorld.get(AWorld.AWorld.size()-1).image = image;
        				
            			drawWorld();//refresh
            			autoSav();  //autosave             	
            		}catch(Exception e){
            			alertInfo.setContentText("Error adding den: " + e.getMessage());
            			alertInfo.showAndWait();
            		}           
            	}	
            	drawWorld();	
		    }
		});
		mAddFS.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	doStop();
            	if (enumEntities() >= AWorld.maxanimals){
        			alertInfo.setContentText("Max number of entities reached");
        			alertInfo.showAndWait();
            	}else{
            		try{
            			newarena2.addAnimal("obs", '#',newarena2);
            			AWorld.AWorld.get(AWorld.AWorld.size()-1).isfoodsource = true; //set up as den	
            			Image image = new Image("/foodsource.png");//set special image
            			AWorld.AWorld.get(AWorld.AWorld.size()-1).image = image;
        			
            			drawWorld();
            			autoSav();               	
            		}catch(Exception e){           			
            			alertInfo.setContentText("Error adding foodsource: " + e.getMessage());
            			alertInfo.showAndWait();
            		}           
            	}	
            	drawWorld();	
		    }
		});
		mAddF.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	doStop();
            	if (enumEntities() >= AWorld.maxanimals){
            		alertInfo.setContentText("Max number of entities reached");
            		alertInfo.showAndWait();
            	}else{
            		TextInputDialog dialog = new TextInputDialog("");
            		dialog.setTitle("Text Input Dialog");
            		dialog.setHeaderText("Text Input Dialog");
            		dialog.setContentText("How many?: ");
            		Optional<String> result = dialog.showAndWait();
            		
            		try{
            			for (int i=0;i<Integer.parseInt(result.get());i++){//loop up to int
            				newarena2.addAnimal("food", '*',newarena2);
            				drawWorld();
            				autoSav();
            			}    
            		}catch(Exception e){    
                		alertInfo.setContentText("Error adding food: " + e.getMessage());
                		alertInfo.showAndWait();
            		}
            	}
		    }
		});
		mAddO.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	doStop();
            	if (enumEntities() >= AWorld.maxanimals){            		
            		alertInfo.setContentText("Max number of entities reached");
            		alertInfo.showAndWait();
            	}else{
            		TextInputDialog dialog = new TextInputDialog("");
            		dialog.setTitle("Text Input Dialog"); //text box input
            		dialog.setHeaderText("Text Input Dialog");
            		dialog.setContentText("How many?: ");
            		Optional<String> result = dialog.showAndWait();
            		
            		try{
            			for (int i=0;i<Integer.parseInt(result.get());i++){ //loop up until number of animals
            				newarena2.addAnimal("obs", '#',newarena2); //add entity
            				drawWorld();
            				autoSav();
            			}
            		}catch(Exception e){
                		alertInfo.setContentText("Error adding obstacle: " + e.getMessage());
                		alertInfo.showAndWait();
            		}           
            	}	
		    }
		});
		mSet.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	doStop();
            	if (enumEntities() >= AWorld.maxanimals){
            		alertInfo.setContentText("Max number of entities reached");
            		alertInfo.showAndWait();
            	}else{
            		try{
            			TextInputDialog dialog = new TextInputDialog("");
                		dialog.setTitle("Text Input Dialog");
                		dialog.setHeaderText("Text Input Dialog");
                		dialog.setContentText("Enter species:");
                		Optional<String> result = dialog.showAndWait();
                		
                		TextInputDialog dialog2 = new TextInputDialog("");
                		dialog2.setTitle("Text Input Dialog");
                		dialog2.setHeaderText("Text Input Dialog");
                		dialog2.setContentText("Enter symbol:" );
                		Optional<String> result2 = dialog2.showAndWait();
                		               	
                		char protect = result2.get().charAt(0);
                		if (protect == '#'|| protect == '*'){ //if they use a char used for special entities
                			alertInfo.setContentText("Error adding animal: protected character used");
                    		alertInfo.showAndWait();
                		}else{
                			newarena2.addAnimal(result.get(), protect,newarena2); //add animal with inputs
                			drawWorld();
                			autoSav();
                		}
            		}catch(Exception e){  
            			alertInfo.setContentText("Error adding animal: " + e.getMessage());
                		alertInfo.showAndWait();
            		}
            	}
		    }
		});
		mMod.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	doStop();
            	try{
            		TextInputDialog dialog = new TextInputDialog("");
            		dialog.setTitle("Text Input Dialog");
            		dialog.setHeaderText("Text Input Dialog");
            		dialog.setContentText("Enter ID of lifeform: ");
            		Optional<String> result = dialog.showAndWait();
            		
            		AnInterface.modifyLife(Integer.parseInt(result.get()));
            		autoSav();
            	}catch(Exception e){ 
            		alertInfo.setContentText("Error modifying: " + e.getMessage());
            		alertInfo.showAndWait();
            	}
		    }
		});
		mRemove.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	doStop();
            	try{
            		TextInputDialog dialog = new TextInputDialog("");
            		dialog.setTitle("Text Input Dialog");
            		dialog.setHeaderText("Text Input Dialog");
            		dialog.setContentText("Enter ID of lifeform: ");
            		Optional<String> result = dialog.showAndWait();

            		AnInterface.tryRemove(Integer.parseInt(result.get())); //call method
            		drawWorld();
    				autoSav();
            	}catch(Exception e){
            		alertInfo.setContentText("Error removing lifeform: " + e.getMessage());
            		alertInfo.showAndWait(); //print error
            	}	
		    }
		});		
		mEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {            	
            	try {
            		AnInterface.editWorld();
    		    	AnInterface.savConfig(); //call edit and save methods
    		    	drawWorld();
    		    	restartApplication();									
				} catch (IOException e) {
					alertInfo.setContentText("Error with saving: " + e.getMessage());
            		alertInfo.showAndWait();//print error
					drawWorld();
				}
            }	
		});	
		mRemoveCurrent.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				doStop();
		    	alert.setContentText("Are you sure?");
		    	ButtonType buttonOne = new ButtonType("Yes");
		    	ButtonType buttonTwo = new ButtonType("No");
		    	alert.getButtonTypes().setAll(buttonOne, buttonTwo);
		    	
		    	Optional<ButtonType> result = alert.showAndWait();
		    	if (result.get() == buttonOne){
		    		try{
		    			AnInterface.tryRemove(AWorld.AWorld.size()-1); //call interface remove method with ID
		    			autoSav();
		    			drawWorld();
		    		}catch(Exception e){	
		    			alertInfo.setContentText("Error removing lifeform");
	            		alertInfo.showAndWait();
		    		}
		    	} 
		    }
		});
		Menu mFile = new Menu("File");
		MenuItem mNew = new MenuItem("New Config file");	
		MenuItem mOpen = new MenuItem("Open Config file");	
		MenuItem mSav = new MenuItem("Save");	
		MenuItem mSavAs = new MenuItem("Save As");
		MenuItem mAuto = new MenuItem("Toggle Autosave");
		MenuItem mExit = new MenuItem("Exit");	
		mExit.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	doStop();         //stop timer, autosave and exit  	
            	autoSav();
		        System.exit(0); //exit the program
		    }
		});
		mNew.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	doStop();		    			    			    			    	
		    	try{
		    		AnInterface.tryNew();	//call interface methods and save	    				    		
		    		restartApplication();
		    	}catch(Exception e){
		    		drawWorld();
		    		alertInfo.setContentText("Cannot create new");
            		alertInfo.showAndWait();
		    	}
		    }
		});
		mOpen.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	try{		    		
		    		AnInterface.loadConfig(newarena2);		
		    		restartApplication();
		    	}catch (Exception ignored){}
		    }		
		});
		mSav.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	try {
					AnInterface.savConfig(); //calls save method
					defaultFont();
					gc.fillText("Saved", AWorld.xdimen-40,  AWorld.ydimen-20);
				} catch (IOException e) {				
					alertInfo.setContentText("Error while saving");
            		alertInfo.showAndWait();
				}
		    }
		});
		mSavAs.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	try{
		    		AnInterface.savConfigAs(); //call method in Interface (static method)
		    	}catch(Exception e){
		    		alertInfo.setContentText("Error while saving");
            		alertInfo.showAndWait();
		    	}
		    }
		});
		mAuto.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	doStop();
		    	alert.setContentText("Would you like to turn on autosave?");
		    	ButtonType buttonOne = new ButtonType("Yes"); //notify user
		    	ButtonType buttonTwo = new ButtonType("No");
		    	alert.getButtonTypes().setAll(buttonOne, buttonTwo);
		    	
		    	Optional<ButtonType> result = alert.showAndWait();
		    	if (result.get() == buttonOne){
		    		autosave = true;
		    	}else{
		    		autosave = false; //toggle autosave
		    	}
		    }
		});				
		Menu mView = new Menu("View");
		MenuItem mDisplayConf = new MenuItem("Display World Config");
		MenuItem mDisplayEnt = new MenuItem("Display Info(entities)");
		MenuItem mDisplayAnim = new MenuItem("Display Info(animals)");
		MenuItem mDisplayMap = new MenuItem("Display Info(map)");
		MenuItem mDebug = new MenuItem("Debug Animal Positions");
		mDisplayConf.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
            	doStop();
            	try{
            		drawWorld();
            		printConfig();
            	}catch(Exception ignored){}
            }	
		});	
		mDisplayEnt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
            	doStop();
            	try{
            		printStats();   
            	}catch(Exception ignored){}
            }	
		});	
		mDisplayAnim.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
            	doStop();
            	try{
            		printStatsAnim(); 
            	}catch(Exception ignored){}
            }	
		});	
		mDisplayMap.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
            	doStop();
            	map();
            }	
		});			
		mDebug.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
            	doStop(); //stop timer
		    	alert.setContentText("Would you like to turn on overlay?");
		    	ButtonType buttonOne = new ButtonType("Yes");
		    	ButtonType buttonTwo = new ButtonType("No");
		    	alert.getButtonTypes().setAll(buttonOne, buttonTwo);
		    	
		    	Optional<ButtonType> result = alert.showAndWait();
		    	if (result.get() == buttonOne){
		    		debug = true;
		    		drawWorld();
		    	}else{
		    		debug = false;
		    		drawWorld();
		    	}
            }
		});	
		Menu mHelp = new Menu("Help");
		MenuItem mDisplayUsage = new MenuItem("Display Usage");
		MenuItem mDisplayAuthor = new MenuItem("Display Author");
		MenuItem mRestartApp = new MenuItem("Restart Application");
		mDisplayUsage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
            	AnInterface.appHelp(); //display instructions
            }	
		});	
		mDisplayAuthor.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
            	AnInterface.authHelp();   //display the author to the user      	
            }	
		});	
		mRestartApp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
            	  restartApplication();    //restart app	
            }	
		});			
		mHelp.getItems().addAll(mDisplayUsage,mDisplayAuthor,mRestartApp);	//add all sub menus to base menu												
		menuBar.getMenus().addAll(mFile,mConfig,mRun,mView,mHelp);
		mView.getItems().addAll(mDisplayConf,mDisplayEnt,mDisplayAnim,mDisplayMap,mDebug);
		mFile.getItems().addAll(mExit,mNew,mOpen,mSav,mSavAs,mAuto);
		mConfig.getItems().addAll(mSet,mAddF,mAddO,mAddD,mAddFS,mMod,mRemove,mRemoveCurrent,mEdit);
		mRun.getItems().addAll(mStart, mPause,mRefresh,mRestart,mIter,mAddOnClick,mAddOnClick2);						
		return menuBar;	
	 }				 
	private void doRestart() {
		 doStop();
		 try{ //Restart by loading last saved config
			 AnInterface.lastConfig(newarena2); //call Interface method
			 drawWorld(); //refresh canvas
		 }catch(Exception e){	
			 alertInfo.setContentText("Cannot restart: " + e.getMessage()); //notify user of exception
     		 alertInfo.showAndWait();
		 }
	 }	 
	protected void showAnimal (Image image, int x, int y) {
		gc.drawImage(image, x, y, 12, 12); //draw to canvas
	 }
	private void drawWorld() {		           
		gc.clearRect(0,  0,  AWorld.xdimen,  AWorld.ydimen); //clear canvas
		gc.setFill(e); //fill with background colour
        gc.fillRect(0, 0, AWorld.xdimen, AWorld.ydimen); //fill rectangle
		newarena2.showAnimal(this); //show the animals
		debug();		//whether or not to show position debug info
	 }			
	@Override
	/**
	 * Main GUI stage building
	 */
	public void start(Stage primaryStage) throws Exception {	
	    primaryStage.setTitle("Oliver's Artificial Life Simulator"); //title
	    BorderPane bp = new BorderPane();	    //setup borderpane 
	    bp.setPadding(new Insets(0, 0, 0, 0)); //insects of bp
	    Label a = new Label("Background colour:"); //labels for colour picker
	    ColorPicker colorPicker = new ColorPicker();//Background colorpicker  
	    colorPicker.setStyle("-fx-color-label-visible: false ;");
	    
	    Label b = new Label("Font colour:");
	    ColorPicker colorPicker2 = new ColorPicker();  //font colorpicker
	    colorPicker2.setStyle("-fx-color-label-visible: false ;"); //remove the label
	    
	    /**
	     * Simulation menu
	     */
	    ToolBar toolBar = new ToolBar(); 
	    Button start = new Button("Start");
	    Button pause = new Button("Pause");
	    Button restart = new Button("Restart");
	    Button stop = new Button("Stop");	    
		toolBar.getItems().addAll(start,pause,restart,stop,a,colorPicker,b,colorPicker2,runningstatus);	
		stop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
			public void handle(ActionEvent actionEvent) {
            	doStop(); //stop timer
            	try{
            		timer = null;   //set timer to null, can't be used again
            	}catch(Exception e){
            		alertInfo.setContentText("Error while stopping timer");
            		alertInfo.showAndWait();
            	}
            }	
		});		
		start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
            	doStart();            	//start timer
            }	
		});		
		pause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {    
            	doStop();   //stop timer
            }	
		});		
		restart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
            	doRestart();       //call restart function   	
            }	
		});		
		colorPicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
            try{
               e = colorPicker.getValue();  //try getting colour
               drawWorld(); //refresh canvas
            }catch(Exception e){
            	alertInfo.setContentText("Cannot get value"); //error fxdialog
        		alertInfo.showAndWait();
            }
            }	
		});		
		colorPicker2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
            try{
               f = colorPicker2.getValue(); //get colour
               drawWorld();
            }catch(Exception e){    
            	alertInfo.setContentText("Cannot get value: " + e.getMessage()); //error dialog
        		alertInfo.showAndWait();
            }
            }	
		});	
	    Canvas canvas = new Canvas(AWorld.xdimen, AWorld.ydimen);	//+100 to avoid icons edging off screen
	    VBox newbox = new VBox(setMenu(),toolBar);		//set vbox menu to be toolbar  
	    newbox.setAlignment(Pos.TOP_CENTER); //set at top
	    bp.setTop(newbox);//set bp top
	    /**
	     * Used for clicking on canvas to add entity (event listener for mouse click on canvas)
	     */
	    canvas.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> doClick(e.getSceneX(), e.getSceneY())); 	   		    	
	    
	    Group root = new Group(); //create group
	    root.getChildren().add(canvas); //add canvas
    	    	    
	    gc = canvas.getGraphicsContext2D();	//setup canvas as graphic
	    gc.setFill(e); //fill with colour
        
        gc.fillRect(0, 0, AWorld.xdimen, AWorld.ydimen); //+100 to clear extended canvas for icon width
	    bp.setLeft(root);
	    
	    newarena2.showAnimal(this);  	    //showanimals
	    timer = new AnimationTimer() //timer to be started/stopped
	    {
	        public void handle(long currentNanoTime)
	        {	        	
	        	newarena2.moveAnimals();	//move the entities	      
	        	foodsourcecount++;
	        	dencount++;//respawn counters
	        	checkCounts();//check if animals should spawn
	        	if(displayateach == true){//if display at each iteration is true, display world each time    		        		
		            drawWorld(); 
	        	}else{
	        		defaultFont();
	        		gc.fillText("Running...Press refresh to update", AWorld.xdimen-100,  AWorld.ydimen-20);	//if turned off
	        	}	        	
	        }	
	    };
	    VBox rtPane = new VBox(); //new vbox
		rtPane.setAlignment(Pos.CENTER); //set as centre pane
		rtPane.setPadding(new Insets(25, 25, 25, 25));  

		
		Scene scene = new Scene(bp, AWorld.xdimen, AWorld.ydimen+53); //added offsets to fit icons on camvas fully
		//also to show animals don't go offscreen
        bp.prefHeightProperty().bind(scene.heightProperty());
        bp.prefWidthProperty().bind(scene.widthProperty()); //set height and width properties properly
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/animal.png"))); //aesthetic choice (put icon at top left of window)
        
        primaryStage.setResizable(false);//make GUI non resizable 
	    primaryStage.setScene(scene);//setup scene
	    primaryStage.show();	    //show GUI    
	}
	private void doClick(double a, double b) {
	    	if (clickaddf == true){ //if adding food
	    		doStop();//stop animation
	    		try{
	    			if (enumEntities() >= AWorld.maxanimals){
	            		alertInfo.setContentText("Max number of entities reached");
		        		alertInfo.showAndWait();
	            	}else{
	            		newarena2.addAnimal("food", '*',newarena2); //add food
	            		AWorld.AWorld.get(AWorld.AWorld.size()-1).x = (((int) a)-4); //modify its coordinates to be placed correctly
	            		AWorld.AWorld.get(AWorld.AWorld.size()-1).y = (((int) b)-64);
	            	}
	    			drawWorld(); //refresh
	    			autoSav();
	    		}catch(Exception e){
	    			alertInfo.setContentText("Cannot add food: " + e.getMessage());
	        		alertInfo.showAndWait();
	    		}
	    	}else if(clickadda == true){ //if animal
	    		doStop();
	    		try{
	    			if (enumEntities() >= AWorld.maxanimals){	            		
	            		alertInfo.setContentText("Max number of entities reached");
		        		alertInfo.showAndWait();
	            	}else{
	            			TextInputDialog dialog = new TextInputDialog("");
	                		dialog.setTitle("Text Input Dialog"); //enter the species in textbox
	                		dialog.setHeaderText("Text Input Dialog");
	                		dialog.setContentText("Enter species: ");
	                		Optional<String> result = dialog.showAndWait();	                	
	                		
	                		TextInputDialog dialog2 = new TextInputDialog("");
	                		dialog2.setTitle("Text Input Dialog");
	                		dialog2.setHeaderText("Text Input Dialog");
	                		dialog2.setContentText("Enter symbol: ");//enter the symbol in textbox
	                		Optional<String> result2 = dialog2.showAndWait();	                		    	                			                		
	          
	                		char protect = result2.get().charAt(0);
	                		if (protect == '#'|| protect == '*'){ //if they use a char used for special entities
	                			alertInfo.setContentText("Error adding animal: protected character used");
	                    		alertInfo.showAndWait();
	                		}else{
	                			newarena2.addAnimal(result.get(), protect,newarena2); //add animal
		            			AWorld.AWorld.get(AWorld.AWorld.size()-1).x = (((int) a)-4); //modify coords
		            			AWorld.AWorld.get(AWorld.AWorld.size()-1).y = (((int) b)-64);
		            			drawWorld();
	                		}
	                		drawWorld();
	                		autoSav();
	            		}
	    		}catch(Exception e){
	    			alertInfo.setContentText("Cannot add animal");
	        		alertInfo.showAndWait();
	    		}
	    	}
	}
	/**
	 * Check if the den and food source should spawn animal/food
	 * */
	private void checkCounts() { 
		if (foodsourcecount >= 1200){ //20 secs given 60fps refresh (60*20)
			Image image = new Image("/fruit.png"); //setup image of fruit
			for (int i=0;i<AWorld.AWorld.size();i++){
				if (AWorld.AWorld.get(i).symbol2 == '#' && AWorld.AWorld.get(i).isfoodsource == true){
					if (AWorld.doesExist(AWorld.AWorld.get(i).x+10, AWorld.AWorld.get(i).y) == false && AWorld.AWorld.get(i).x+10 < AWorld.xdimen){
						newarena2.addAnimal("food", '*',newarena2); //produce food
						AWorld.AWorld.get(AWorld.AWorld.size()-1).x = AWorld.AWorld.get(i).x+10; //setup its coords
						AWorld.AWorld.get(AWorld.AWorld.size()-1).y = AWorld.AWorld.get(i).y;
						AWorld.AWorld.get(AWorld.AWorld.size()-1).image = image; 
					}else if(AWorld.doesExist(AWorld.AWorld.get(i).x-10, AWorld.AWorld.get(i).y) == false && AWorld.AWorld.get(i).x-10 < 0){
						newarena2.addAnimal("food", '*',newarena2);
						AWorld.AWorld.get(AWorld.AWorld.size()-1).x = AWorld.AWorld.get(i).x-10;
						AWorld.AWorld.get(AWorld.AWorld.size()-1).y = AWorld.AWorld.get(i).y;
						AWorld.AWorld.get(AWorld.AWorld.size()-1).image = image; 
					}
				}
			}
			foodsourcecount = 0;
		}
		if (dencount >= 1200){
			Random r = new Random();
			for (int i=0;i<AWorld.AWorld.size();i++){
				if (AWorld.AWorld.get(i).symbol2 == '#' && AWorld.AWorld.get(i).isden == true){
					if (AWorld.doesExist(AWorld.AWorld.get(i).x+10, AWorld.AWorld.get(i).y) == false && AWorld.AWorld.get(i).x+10 < AWorld.xdimen){
						char c = (char)(r.nextInt(26) + 'a');
						newarena2.addAnimal("denanim", c,newarena2);
						AWorld.AWorld.get(AWorld.AWorld.size()-1).x = AWorld.AWorld.get(i).x+10;
						AWorld.AWorld.get(AWorld.AWorld.size()-1).y = AWorld.AWorld.get(i).y;
					}else if(AWorld.doesExist(AWorld.AWorld.get(i).x-10, AWorld.AWorld.get(i).y) == false && AWorld.AWorld.get(i).x-10 < 0){
						char b = (char)(r.nextInt(26) + 'a');
						newarena2.addAnimal("denanim", b,newarena2);
						AWorld.AWorld.get(AWorld.AWorld.size()-1).x = AWorld.AWorld.get(i).x-10;
						AWorld.AWorld.get(AWorld.AWorld.size()-1).y = AWorld.AWorld.get(i).y;
					}
				}
			}
			dencount = 0;
		}
	}
	/**
	 * Autosave functionality (off by default) 
	 * */	
	private void autoSav() {
		if (autosave == true){
			try {
				AnInterface.savConfig();	//save
				defaultFont();
				gc.fillText("Saved", AWorld.xdimen-40,  AWorld.ydimen-20); //print to canvas
			} catch (IOException e) {						
				alertInfo.setContentText("Error with autosave, recommend turning off: " + e.getMessage());
        		alertInfo.showAndWait();
			} //AUTOSAVE FEATURE
		}
	}	
	private void map(){
		drawWorld();
		defaultFont(); //display map stats
		gc.fillText("X: " +AWorld.xdimen + " Y: " + AWorld.ydimen + " Max entities: "+ AWorld.maxanimals, 15, 60);
	 }
	/**
	 * Prints text that doesn't fit on canvas
	 */
	@SuppressWarnings("unused")
	private void printStr(String a){
			if (a != ""){ //if string has something in it
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setHeaderText("Entity list");
				alert.setTitle("Canvas Overflow");
				alert.initStyle(StageStyle.UTILITY);
				// Create expandable Exception.
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);

				TextArea textArea = new TextArea(a); //write string to textbox
				textArea.setEditable(false);
				textArea.setWrapText(true);
				textArea.setMaxWidth(Double.MAX_VALUE);
				textArea.setMaxHeight(Double.MAX_VALUE);
				GridPane.setVgrow(textArea, Priority.ALWAYS);
				GridPane.setHgrow(textArea, Priority.ALWAYS);

				GridPane expContent = new GridPane();
				expContent.setMaxWidth(Double.MAX_VALUE);
				expContent.add(textArea, 0, 1);

				// Set expandable Exception into the dialog pane.
				alert.getDialogPane().setExpandableContent(expContent);

				alert.showAndWait();
		    }	 //used for printing stuff that doesn't fit on canvas
	 }
	/**
	 * Prints all attributes of entities onto the canvas  
	 * */
	private void printStats(){		
		drawWorld();
		String a = ""; //string for overflow
		if (AWorld.AWorld.size() == 0 || enumEntities()==0){	//if no entities		
			defaultFont();
			gc.fillText("No entities present",15,20);
		}else{	    	
	    	int j = 0; //small counter used to align/space text upwards	    	
			for (int i=0;i<AWorld.AWorld.size();i++){
				defaultFont();

				if ((15+j*10)> AWorld.ydimen){ //append to string
					a = a.concat("ID: " + i + " "+ AWorld.AWorld.get(i).toText());
					a = a.concat("\n");
				}else{
					gc.fillText("ID: " + i + " "+ AWorld.AWorld.get(i).toText(), 15, 15+j*10);
					j++;
				}									
			}			
		}		
		printStr(a);
	}
	private void printStatsAnim(){
		drawWorld();
		String a = "";
		if (AWorld.AWorld.size() ==0 || enumAnimals()==0){			
			defaultFont();
			gc.fillText("No animals present",15,20);
		}else{	    	
	    	int j = 0; //small counter used to align/space text upwards	    	
			for (int i=0;i<AWorld.AWorld.size();i++){
				defaultFont();
				if (AWorld.AWorld.get(i).species2 != "obs"&& AWorld.AWorld.get(i).species2 != "food"){
					if ((15+j*10)> AWorld.ydimen){
						a = a.concat("ID: " + i + " " + AWorld.AWorld.get(i).toText());
						a = a.concat("\n");
					}else{
						gc.fillText("ID: " + i + " " + AWorld.AWorld.get(i).toText(), 15, 15+j*10);
						j++;
					}
				}					
			}			
		}		
		printStr(a);
	}
	/**
	 * Prints all animals with an energy level of <=0  
	 * */	
	private void printConfig() {
			defaultFont(); //print world stats
			gc.fillText("Number of entities: " + enumEntities() + " Maximum entity count: "+ AWorld.maxanimals, 15, 50);
			gc.fillText("X dimension: " + AWorld.xdimen + " Y dimension: "+ AWorld.ydimen, 15, 40);
			gc.fillText("No. of food: " + AnInterface.enumFood() + " No. of obstacles: " + AnInterface.enumObstacles(), 15, 30);		
	}
	protected static int enumEntities(){ //enumerates entities that are alive (don't have a blank symbol)
		int counter = 0;
		for (int i=0;i<AWorld.AWorld.size();i++){
			counter++;
		}
		return counter;
	}
	protected static int enumAnimals(){ //enumerates entities that are alive (don't have a blank symbol)
		int j = 0;
		for (int i=0;i<AWorld.AWorld.size();i++){
			if(AWorld.AWorld.get(i).species2 != "food" && AWorld.AWorld.get(i).species2 != "obs"){
				j++; //increment
			}
		}
		return j;
	}
	private void defaultFont(){
		gc.setFill(f); //set font colour to be the global colour set by the colorpicker
		gc.setFont(Font.getDefault()); //default font
		gc.setFont(new Font(AWorld.xdimen/42)); //scaling
	}
	private void debug() {
		try{
			if (debug ==true){
				for (int i=0;i<AWorld.AWorld.size();i++){
					defaultFont(); //for just animals
					if (AWorld.AWorld.get(i).symbol2 != '#' && AWorld.AWorld.get(i).symbol2 != '*'){
						gc.fillText("ID: " + i + " ("+ AWorld.AWorld.get(i).x + "," + AWorld.AWorld.get(i).y + ")", AWorld.AWorld.get(i).x, AWorld.AWorld.get(i).y);
					}//write their coordinates and ID at their own position for each animal
				}					
			}		
		}catch(Exception e){	
			alertInfo.setContentText("Error with debugging: " + e.getMessage());
    		alertInfo.showAndWait();
		}
	}	
	/**
	 * Restart method used to resize the GUI after world config changes
	 * */
	private void restartApplication() 
	{ 
	  try {
		  final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java"; //get java bin folder
		  File currentJar; //treat as file
		  currentJar = new File(GUIInterface.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		  if(!currentJar.getName().endsWith(".jar"))
			    return;
			  final ArrayList<String> command = new ArrayList<String>(); //start making command the start program again
			  command.add(javaBin); //add commands
			  command.add("-jar");
			  command.add(currentJar.getPath()); //get jar path
			  final ProcessBuilder builder = new ProcessBuilder(command); //build commands into builder
			  builder.start(); //start builder
			  System.exit(0);	//exit current instance	  
	  } catch (URISyntaxException | IOException e) {
		  	alertInfo.setContentText("Error restarting GUI, please drag the window to the canvas size manually");
			alertInfo.showAndWait();
	  }  
	}
	/**
	 * Start and stop methods
	 * */
	private void doStop(){
		if (timer!=null){
			timer.stop();
			runningstatus.setText("Simulation idle");
		}else{
			alertInfo.setContentText("Timer has been stopped, please restart");
			alertInfo.showAndWait();
		}
	}
	private void doStart(){
		if (timer!=null){
			timer.start(); //start timer if timer is active (not null)
			runningstatus.setText("Simulation started"); //set label text
		}else{
			alertInfo.setContentText("Timer has been stopped, please restart");
			alertInfo.showAndWait();
		}
	}
	/**
	 * Loads last configuration then launches GUI
	 * */	
	public static void main(String[] args) {
		AnInterface.lastConfig(newarena2);	
	    Application.launch(args); //launches simulation
	}
}