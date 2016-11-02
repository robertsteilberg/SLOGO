package GUIController;

import BackEndInternalAPI.ObservableProperties;
import Base.OptionsMenu;
import FrontEndInternalAPI.RenderSprite;
import GUI.DisplayHelp;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
//
//import java.awt.*;
//import java.awt.Button;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Delia on 10/15/2016.
 */
public class GUIDisplay implements RenderSprite {
    private static final int FRAMES_PER_SECOND = 60;
    private static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    private static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    private static final int BOUNCER_SPEED = 300;
    private static final int TURTLE_FIT_SIZE = 40;
    private static final int X_POS = 620;
    private static final int Y_POS = 110;
    private static boolean visibility = true;
    private int numSteps = 0;
    private int strokeWidth = 5;
    private Timeline animation = new Timeline();
    private Pane window;
    private Line myPath;
    private ImageView helpButton;
    private Button optionsButton;
    private ImageView myTurtle, displayGraph;
    private DisplayHelp helpWindow;
    private Paint pathColor;
    private DisplayMenu myOptions;
    private ArrayList<Line> turtleMotion = new ArrayList<>();
//    private ArrayList<Turtle> myTurtles = new ArrayList<>();
    private HashMap<Double, Turtle> myTurtles = new HashMap<>();
    private String currentTurtle;

    private String overButton = "-fx-background-color: linear-gradient(#0079b3, #00110e);" +
            "-fx-background-radius: 20;" +
            "-fx-text-fill: white;";
    private String buttonFill = "-fx-background-color: linear-gradient(#00110e, #0079b3);" +
            "-fx-background-radius: 20;" +
            "-fx-text-fill: white;";

    /**
     *
     * @param p
     * @param turtle
     * @param pathColor
     */
    public GUIDisplay(Pane p, ImageView turtle, Paint pathColor, Line lineType){
        this.window = p;
        this.myTurtle = turtle;
        this.pathColor = pathColor;
        this.myPath = lineType;
        drawDisplay();
        addDisplayControlButtons();
        addTextLabel();
//        addMoreTurtlesButton();
//        addTurtle();
        addHelpButton();
    }

    public void setInitialTurtle(String initialTurtle){
        currentTurtle = initialTurtle;
    }

    private void drawDisplay(){
        Image newImg = new Image(getClass().getClassLoader()
                .getResourceAsStream("images/graphPaper.gif"));
        displayGraph = new ImageView(newImg);
        displayGraph.setFitWidth(770);
        displayGraph.setFitHeight(480);
        displayGraph.setTranslateY(Y_POS);
        displayGraph.setTranslateX(X_POS);
//        displayGraph.setstr
        displayGraph.opacityProperty().setValue(0.9);
        window.getChildren().add(displayGraph);
    }

    public ObservableProperties addTurtle(double newID){
        ImageView myNewTurtle = new ImageView();
        myNewTurtle.setImage(myTurtle.getImage());
        myNewTurtle.setTranslateX(displayGraph.getTranslateX() + (displayGraph.getFitWidth() / 2));
        myNewTurtle.setTranslateY(displayGraph.getTranslateY() + (displayGraph.getFitHeight() / 2));
//        myNewTurtle.setX(displayGraph.getTranslateX() + (displayGraph.getFitWidth() / 2));
//        myNewTurtle.setY(displayGraph.getTranslateY() + (displayGraph.getFitHeight() / 2));
        myNewTurtle.setFitHeight(TURTLE_FIT_SIZE);
        myNewTurtle.setFitWidth(TURTLE_FIT_SIZE);
        Turtle newTurtle = new Turtle();
        newTurtle.setImage(myNewTurtle);
        newTurtle.setID(newID);
        myTurtles.put(newID, newTurtle);
//        myTurtle.min
        makeTooltip();
        System.out.println("w = " + newTurtle.getImage().getFitWidth());
        window.getChildren().add(newTurtle.getImage());

        return new ObservableProperties(myNewTurtle, this, newID);
    }

    private void makeTooltip(){
        Tooltip t = new Tooltip("X: " + getTurtleLocation().getX() + "\n" + "Y: " + getTurtleLocation().getY() + "\n");
        t.setText(t.getText() + "Line Width: " + strokeWidth + "\n");
        t.setText(t.getText() + "Pen Color: " + pathColor + "\n");
        t.setText(t.getText() + "Rotation: " + myTurtle.getRotate() + "\n");
        Tooltip.install(myTurtle, t);
    }
    
    private void addTextLabel(){
        Text label = new Text("Display");
        label.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        label.setTranslateX(630);
        label.setTranslateY(130);
        window.getChildren().add(label);
    }

    private void addOptionsButton(){
        Stage s = new Stage();
        optionsButton = new Button("Display Options");
        optionsButton.setStyle(overButton);
        optionsButton.setOnMouseEntered(e -> optionsButton.setStyle(buttonFill));
        optionsButton.setOnMouseExited(e -> optionsButton.setStyle(overButton));
        optionsButton.setOnMouseClicked(e -> updateDisplayOptions());
        optionsButton.setTranslateX(760);
        optionsButton.setTranslateY(120);
        window.getChildren().add(optionsButton);
    }

    private void addHelpButton(){
        Image newImage = new Image(getClass().getClassLoader()
                .getResourceAsStream("images/help.png"));
        helpButton = new ImageView(newImage);
        helpButton.setOnMouseClicked(e -> helpHandler());
        helpButton.setTranslateX(displayGraph.getTranslateX() + displayGraph.getFitWidth() - 35);
        helpButton.setTranslateY(displayGraph.getTranslateY() + 10);
        helpButton.setFitWidth(30);
        helpButton.setFitHeight(30);
        window.getChildren().add(helpButton);
    }
    
//    private void addMoreTurtlesButton(){
//        Button addTurtles = new Button("Add Turtles");
//        addTurtles.setTranslateX(1110);
//        addTurtles.setTranslateY(125);
//        addTurtles.setStyle(overButton);
//        addTurtles.setOnMouseEntered(e -> addTurtles.setStyle(buttonFill));
//        addTurtles.setOnMouseExited(e -> addTurtles.setStyle(overButton));
////        addTurtles.setOnMouseClicked(e -> addTurtle());
//        window.getChildren().add(addTurtles);
//    }
    
    public String getTurtleStr(){
        if(myOptions != null)
            return myOptions.getTurtleString();
        else
            return null;
    }
    
    private void helpHandler(){
        Stage s = new Stage();
        helpWindow = new DisplayHelp(s);
        helpWindow.init();
    }

    /**
     *
     * @param width
     */
    public void bindNodes(ReadOnlyDoubleProperty width){
        helpButton.translateXProperty().bind(width.subtract(50));
    }

//    /**
//     *
//     * @param x
//     * @param y
//     */
    public void moveTurtle(double x, double y, double id){
//        public void animate(){
        System.out.println("moveTurtle() coordinates: " + x + ", " + y);
        double turtleX = myTurtles.get(id).getImage().getTranslateX();
        double turtleY = myTurtles.get(id).getImage().getTranslateY();
//        double newX = turtleX + x;
//        double newY = turtleY - y;
//        System.out.println("newX = " + newX);
//        System.out.println("newY = " + newY);
        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY),
                ee -> {
                    double newX = turtleX + x;
                    double newY = turtleY - y;
                    System.out.println("turtleY = " + turtleY);
                    System.out.println("Y = " + y);

                    System.out.println("after keyframe: \nnewX = " + newX);
                    System.out.println("newY = " + newY);
                    step(SECOND_DELAY, newX, newY, id);
                });

        //whenever the animation plays, it's what we want to happen in each moment in time.
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
//        }
    	numSteps++;
//        System.out.println("turtle original position:" + (int) myTurtle.getTranslateX());
//        System.out.println("translate x of the editor" + X_POS);
       // myTurtles.get(1);
        System.out.println();

        drawNewLine(x, y, id);
//        myTurtles.get(id).getImage().setX(x);
//        myTurtles.get(id).getImage().setY(-y);



    }


    private void step(double elapsedTime, double x, double y, double id){
        int xMult = 1;
        int yMult = 1;
        boolean up = false, down = false, left = false, right = false;
        double currentX = myTurtles.get(id).getImage().getTranslateX();
        double currentY = myTurtles.get(id).getImage().getTranslateY();
//        double x = currentX + xChange;
//        double y = currentY - yChange;
//        double currentX =  myTurtles.get(id).getImage().getTranslateX() +  myTurtles.get(id).getImage().getX() + 20;
//        double currentY =  myTurtles.get(id).getImage().getTranslateY() +  myTurtles.get(id).getImage().getY() + 20;
        System.out.println("dest coordinates: " + x + ", " + y);
        System.out.println("Turtle coordinates: " + currentX + ", " + currentY);
        if(x < currentX) {
            xMult = -1;
            left = true;
        }
        if(x > currentX) right = true;
        if (y < currentY) {
            yMult = -1;
            up = true;
            System.out.println("true set");
        }
        if (y < currentY) down = true;
        double singleStepChange = BOUNCER_SPEED * elapsedTime;
        double stepX = currentX + (xMult * singleStepChange);
        double stepY = currentY + (yMult * singleStepChange);

        if(currentX == x && currentY == y) animation.stop();
        if (currentX != x) myTurtles.get(id).getImage().setTranslateX(stepX);
        if(currentY != y) myTurtles.get(id).getImage().setTranslateY(stepY);

        if (up){
            if(left){
                if(currentY <= y && currentX <= x){
                    animation.stop();
                    System.out.println("up left");
                }
            }
            else if(right){
                if(currentY <= y && currentX >= x){
                    animation.stop();
                    System.out.println("up right");
                }
            }

            if(currentY <= y){
                animation.stop();
                System.out.println("up: turtle y = " + currentY + " dest y = " + y);
            }
        }
        if (down){
            if(left){
                if(currentY >= y && currentX <= x){
                    animation.stop();
                    System.out.println("down left");
                }
            }
            else if(right){
                if(currentY >= y && currentX >= x){
                    animation.stop();
                    System.out.println("down right");
                }
            }
        }
//        drawNewLine(stepX, stepY, id);
//        for(int i = 0; i < gems.size(); i++){
//            gems.get(i).setY(singleStepChange(gems, elapsedTime, i));
//
//            gemBelowScreen(i, gems);
//
//            //end this loop so the gems don't keep falling in the background when
//            //user levels up!!
//            if(gameover || levelup) break;
//        }
//        myLevel.stepOnce(s);
//        myLevel.updatePoints(score.wasBonus(), score.pointsJustAdded());
//        if(myLevel.gameover) gameOver();
//        if(myLevel.isLeveledUp() && isLevel2){
//            victoryScene();
//        }
//        else if(myLevel.isLeveledUp()){
//            animation.stop();
//            levelUp();
//        }
    }

    public void stepOnce(double elapsedTime) {

    }

//    protected double singleStepChange(ImageView turtle, double elapsedTime) {
//        return turtle + BOUNCER_SPEED * elapsedTime;
//    }
    /**
     *
     */
    public void drawNewLine(){
    	double centerX =  20;
		double centerY = 20;
        boolean yBoundUpper = false;
		Line newPath;
        double yDest = myTurtle.getY() + myTurtle.getTranslateY() + centerY;
        System.out.println("line dest is " + yDest);
//        System.out.println("getY() is " + yDest);
//        System.out.println("getTranslateY() is " + myTurtle.getTranslateY());
//        if(yDest < Y_POS){
//            yDest = Y_POS + centerY;
//            yBoundUpper = true;
//        }

    	if (turtleMotion.size() < 1) {

    		newPath = new Line(centerX + myTurtle.getTranslateX(),
                    centerY + myTurtle.getTranslateY(),
                    myTurtle.getX() + myTurtle.getTranslateX() + centerX,
                    myTurtle.getY() + myTurtle.getTranslateY() + centerY);
//            newPath = new Line(centerX + myTurtle.getTranslateX(),
//                    centerY + myTurtle.getTranslateY(),
//                    myTurtle.getX() + myTurtle.getTranslateX() + centerX,
//                    yDest);
    	}
    	else{
    		newPath = new Line(turtleMotion.get(turtleMotion.size() - 1).getEndX(),
                    turtleMotion.get(turtleMotion.size() - 1).getEndY(),
    				myTurtle.getX() + myTurtle.getTranslateX() + centerX,
                    myTurtle.getY() + myTurtle.getTranslateY() + centerY);
//            newPath = new Line(turtleMotion.get(turtleMotion.size() - 1).getEndX(),
//                    turtleMotion.get(turtleMotion.size() - 1).getEndY(),
//                    myTurtle.getX() + myTurtle.getTranslateX() + centerX,
//                    yDest);
    	}

//    	System.out.println(pathColor);
    	newPath.setFill(pathColor);
        newPath.setStroke(pathColor);
        newPath.setStrokeWidth(strokeWidth);
//        newPath.setStrokeDashOffset(2);
        newPath.getStrokeDashArray().addAll(myPath.getStrokeDashArray());
        newPath.setId("Step" + numSteps);
        newPath.setVisible(visibility);
        turtleMotion.add(newPath);
        window.getChildren().add(newPath);


        if(yBoundUpper) myTurtle.setTranslateY(Y_POS);
        //window.getChildren().remove(myTurtle);
        //window.getChildren().add(myTurtle);
    	
    	//bool.set(false);
    }

    //DONT NEED IT ANYMORE

    /**
     *
     */
    private void drawNewLine(double x, double y, double id){
//        Line newLine = new Line(origin.getX() + 20, origin.getY() + 20,
//                X_POS + destination.getX() + 20, Y_POS + destination.getY() + 20);
//        System.out.println("my origin: " + myTurtles.get(1.0).getImage().getX() + " " + myTurtles.get(1.0).getImage().getY());
        System.out.println("my Translate " + myTurtles.get(id).getImage().getTranslateX() + " " + myTurtles.get(id).getImage().getTranslateY());
        System.out.println("my destination: " + x + " " + y);
        double xFrom =  myTurtles.get(id).getImage().getTranslateX() +  myTurtles.get(id).getImage().getX() + 20;
        double yFrom =  myTurtles.get(id).getImage().getTranslateY() +  myTurtles.get(id).getImage().getY() + 20;
        System.out.println("old points " + xFrom + " "+ yFrom);
        System.out.println("new points " + (xFrom + x) + " " + (yFrom - y));
        Line newLine = new Line(xFrom, yFrom, myTurtles.get(id).getImage().getTranslateX() + x + 20,  myTurtles.get(id).getImage().getTranslateY() - y + 20);
        newLine.setFill(pathColor);
        newLine.setStroke(pathColor);
        newLine.setStrokeWidth(5);
        newLine.setId("Step" + numSteps);
        newLine.getStrokeDashArray().addAll(myPath.getStrokeDashArray());
        newLine.setVisible(visibility);
        //turtleMotion.add(newLine);
        myTurtles.get(1.0).getLines().add(newLine);
        window.getChildren().add(newLine);
    }

    /**
     *
     */
	public void clearScreen() {
		window.getChildren().removeAll(turtleMotion);
		turtleMotion.clear();
		//clearScreenProperty.set(false);
	}

    private void addDisplayControlButtons(){
        Image newImage = new Image(getClass().getClassLoader()
                .getResourceAsStream("images/clear.png"));
        ImageView clearImg = new ImageView(newImage);
        Button clear = newButton("Clear", clearImg, (int) displayGraph.getTranslateX() + 300,
                (int) displayGraph.getTranslateY() + 10);
        clear.setOnMouseClicked(e -> {
            window.getChildren().removeAll(turtleMotion);
            turtleMotion.clear();
        });
        newImage = new Image(getClass().getClassLoader()
                .getResourceAsStream("images/options.png"));
        ImageView optionsImg = new ImageView(newImage);
        optionsButton = newButton("Display Options", optionsImg, 760, 120);
        optionsButton.setOnMouseClicked(e -> {
            Stage s = new Stage();
            myOptions = new DisplayMenu(s);
            updateDisplayOptions();
        });
        newImage = new Image(getClass().getClassLoader()
                .getResourceAsStream("images/reset.png"));
        ImageView resetImg = new ImageView(newImage);
        Button reset = newButton("Reset", resetImg, 1010, 120);
        reset.setOnMouseClicked(e -> resetIDE());
//        optionsButton.setTranslateX(760);
//        optionsButton.setTranslateY(120);
        window.getChildren().addAll(clear, optionsButton, reset);
//        window.getChildren().add(clear);
    }

    private Button newButton(String text, ImageView imgV, int x, int y){
        imgV.setFitWidth(25);
        imgV.setFitHeight(25);
        Button run = new Button(text, imgV);
        run.setStyle(overButton);
        run.setOnMouseEntered(e -> {
            run.setStyle(buttonFill);
        });
        run.setOnMouseExited(e -> run.setStyle(overButton));
        run.setTranslateX(x);
        run.setTranslateY(y);
        return run;
    }
   


    /**
     *
     * @param isVisible
     */
    public void setVisibility(boolean isVisible){
        visibility = isVisible;
    }

    /**
     *
     * @return
     */
    public ImageView getGraph(){
        return displayGraph;
    }

    /**
     *
     * @return
     */
    public Point getTurtleLocation(){
        return new Point((int)myTurtle.getTranslateX(), (int)myTurtle.getTranslateY());
    }

    @Override
    public void updateNodes() {

    }

    public Paint getPenColor(){
        return pathColor;
    }
    
    @Override
    public void updateDisplayOptions() {
//        myOptions.setDefaults(pathColor, currentTurtle);
        myOptions.initPopup();

    }

    public void applyDisplayChanges(){
        pathColor = myOptions.getPenColor().getValue();
        System.out.println("pen: " + pathColor);
        System.out.println("display: " + myOptions.getDisplayColor());
//        applyDisplayChanges(myOptions.getTurtleBox().getValue());
        myOptions.setTurtleString();
        myTurtle.setImage(myOptions.generateTurtleImage());
        myPath = myOptions.getLineBox().getValue();
        createDisplayShading();
        strokeWidth = myOptions.getStrokeWidth();
        setVisibility(!myOptions.isPenUp());
        makeTooltip();
    }

    private void createDisplayShading(){
        double hue = myOptions.map( (myOptions.getDisplayColor().getHue() + 180) % 360, 0, 360, -1, 1);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setHue(hue);
        colorAdjust.setSaturation(myOptions.getDisplayColor().getSaturation());
        colorAdjust.setBrightness(myOptions.getDisplayColor().getBrightness());
        displayGraph.setEffect(colorAdjust);
    }

    @Override
    public void resetIDE() {
        window.getChildren().remove(myTurtle);
        window.getChildren().removeAll(turtleMotion);
        turtleMotion.clear();
        Image newImage = new Image(getClass().getClassLoader()
                .getResourceAsStream("images/graphPaper.gif"));
//        drawDisplay();
        displayGraph.setEffect(null);
        displayGraph.setImage(newImage);
        newImage = new Image(getClass().getClassLoader()
                .getResourceAsStream("images/turtle.png"));
//        ImageView defaultTurtle = new ImageView(newImage);
//        myTurtle = defaultTurtle;
        myTurtle.setImage(newImage);
        myTurtle.setTranslateX(displayGraph.getTranslateX() + (displayGraph.getFitWidth() / 2));
        myTurtle.setTranslateY(displayGraph.getTranslateY() + (displayGraph.getFitHeight() / 2));
//        addTurtle();
        Paint defaultColor = Color.MIDNIGHTBLUE;
        pathColor = defaultColor;
        myPath.getStrokeDashArray().clear();
//        newImage = new Image(getClass().getClassLoader()
//                .getResourceAsStream("images/graphPaper.png"));
//        ImageView defaultDisplay = new ImageView(newImage);
//        displayGraph = defaultDisplay;
        strokeWidth = 5;
//        addTextLabel();
//        addHelpButton();
//        addDisplayControlButtons();
    }

    private class DisplayMenu extends OptionsMenu{

        private static final int DROP_DOWN_X_VALUE = 400;
        private static final int PEN_MIN = 0;
        private static final int PEN_MAX = 10;
        private static final int PEN_INIT = 5;
        private ColorPicker displayColor;
        private Slider slider;
        private CheckBox penUpBox;
        /**
         * @param s
         */
        public DisplayMenu(Stage s) {
            super(s);
        }

        public void addNodes(){
            addTitle();
            addRectangle();
            changePenColor();
            changeSpriteImage();
            addLaunchButton();
            addLineStylePicker();
            changeDisplayColor();
            addPenSizeSlider();
            addCheckBox();
        }

        @Override
        public void addTitle() {
            Text title = new Text("Select your preferences for the display.");
            title.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
            title.setFill(Color.WHITE);
            title.setTranslateX(30);
            title.setTranslateY(130);
            getStartWindow().getChildren().add(title);
        }

        @Override
        public void addRectangle() {
            Rectangle backdrop = new Rectangle(500, 340, Color.MIDNIGHTBLUE);
            backdrop.setTranslateY(180);
            backdrop.setTranslateX(100);
            backdrop.opacityProperty().setValue(0.5);
            getStartWindow().getChildren().add(backdrop);
        }

        @Override
        public void addLaunchButton() {
            Button newButton = new javafx.scene.control.Button("Apply");
            newButton.setStyle(getOverButton());
            newButton.setOnMouseEntered(e -> newButton.setStyle(getButtonFill()));
            newButton.setOnMouseExited(e -> newButton.setStyle(getOverButton()));
            newButton.setOnMouseClicked(e -> {
                applyDisplayChanges();
                getStage().close();
            });
            newButton.setTranslateX(300);
            newButton.setTranslateY(500);
//        newButton.setOnMouseClicked(e -> setParameters());
            getStartWindow().getChildren().add(newButton);
        }

        private void addPenSizeSlider(){
//            JSlider framesPerSecond = new JSlider(JSlider.HORIZONTAL,
//                    PEN_MIN, PEN_MAX, PEN_INIT);
//            framesPerSecond.addChangeListener(this);

            slider = new Slider(0, 10, 5);
            slider.setShowTickMarks(true);
            slider.setShowTickLabels(true);
            slider.setMajorTickUnit(2);
            slider.setMinorTickCount(1);
//            slider.setBlockIncrement(1);
            slider.setSnapToTicks(true);
//            slider.setPrefWidth(200);
//            slider.tick
            slider.setTranslateX(DROP_DOWN_X_VALUE);
            slider.setTranslateY(200);

            Label sliderLabel = generateLabel("Choose pen size \t\t" + (int) slider.getValue(), 125, 200);

            slider.valueProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> ov,
                                    Number old_val, Number new_val) {
                    sliderLabel.setText("Choose pen size \t\t" + String.format("%.0f", new_val));
                }
            });

            getStartWindow().getChildren().addAll(slider, sliderLabel);
        }

        @Override
        public void initIDE(String background, String turtle) {
//nope
        }

        public Image generateTurtleImage(){
            Image newImg = new Image(getClass().getClassLoader()
                    .getResourceAsStream(getTurtleString()));
            return newImg;
        }

        public void changeDisplayColor() {
            displayColor = generateColorPicker(Color.MIDNIGHTBLUE, 400, 400);
            Label penLabel = generateLabel("Select display color", 125, 400);
            getStartWindow().getChildren().add(displayColor);
            getStartWindow().getChildren().add(penLabel);
        }

        private void addCheckBox(){
            penUpBox = new CheckBox("penup");
            penUpBox.setTranslateX(DROP_DOWN_X_VALUE);
            penUpBox.setTranslateY(450);
            Label penUpLabel = generateLabel("Check to make pen invisible", 125, 450);
            getStartWindow().getChildren().addAll(penUpLabel, penUpBox);
        }

        /**
         *
         */
        public void initPopup(){
            getStage().setTitle("Options");
            getStage().setScene(new Scene(setUpWindow()));
            getStage().show();
        }

        public double map(double value, double start, double stop, double targetStart, double targetStop) {
            return targetStart + (targetStop - targetStart) * ((value - start) / (stop - start));
        }


        public Color getDisplayColor(){
            return displayColor.getValue();
        }

        public int getStrokeWidth(){
            return (int) slider.getValue();
        }

        public boolean isPenUp(){
            System.out.println(penUpBox.isSelected());
            return penUpBox.isSelected();
        }
    }

	public void changePenColor(Double newValue) {
		// TODO Auto-generated method stub
	}

	public Object setPenSize(Double newValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object changeImage(Double newValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object setBackgroundImage(Double newValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object changePalette(Double newValue) {
		// TODO Auto-generated method stub
		return null;
	}
}
