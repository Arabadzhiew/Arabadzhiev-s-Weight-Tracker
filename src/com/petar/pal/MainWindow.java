package com.petar.pal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainWindow extends Application implements EventHandler<ActionEvent>{
	ConfirmationBox cBox = new ConfirmationBox();
	
	Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
	public static Rectangle2D stageBounds;
	
	Group graphPane;
	Rectangle graph;
	Text firstDateText;
	Text lastDateText;
	boolean monthOfYear = false;
	double graphCeil = 200;
	double graphBot = 20;
	double scopeIndex;
	double graphMargins = 180;
	int graphDays = 6;
	boolean sinceFirstEntry = false;
	double dotSize = 2;
	
	GridPane entryPane;
	TextField weightField;
	Label errorLabel;
	Label errorLabel2;
	ComboBox<Integer> yearBox, dayBox;
	ComboBox<String> monthBox;
	Button weightEntry;
	Button weightRemoval;
	GridPane datePane;
	GridPane weightButtonPane;
	
	GridPane infoPane;
	GridPane innerInfoPane;
	Label currentLabel = new Label();
	Label averageLabel = new Label();
	Label progressLabel = new Label();
	ComboBox<String> periodBox;
	String graphPeriod = "1 Week";
	
	GridPane entryScrollPanePane;
	ScrollPane entryScrollPane;
	GridPane dailyEntryTable;
	Border entryTableBorder = new Border(new BorderStroke(null,null,Color.DARKGRAY,null,
			BorderStrokeStyle.NONE,BorderStrokeStyle.NONE,BorderStrokeStyle.SOLID,BorderStrokeStyle.NONE,
			CornerRadii.EMPTY,new BorderWidths(1),Insets.EMPTY));
	
	GridPane averageTablePane;
	TableView<AverageTableRow> averageTable;
	TableColumn<AverageTableRow, Calendar> startDateColumn;
	TableColumn<AverageTableRow, Calendar> endDateColumn;
	TableColumn<AverageTableRow, String> averageWeightColumn;
	ComboBox<String> averageTableBox;
	String tableRowMargins = "Weekly";
	
	DecimalFormat infoDf =  new DecimalFormat("###.#");
	DecimalFormat tableDf = new DecimalFormat("###.##");
	DecimalFormat graphDf = new DecimalFormat("###.0");
	DecimalFormatSymbols decimalSymbol = new DecimalFormatSymbols(Locale.getDefault());
	
	Font graphFont = new Font("Arial Black",9);
	
	Calendar currentDate = Calendar.getInstance();
	Calendar firstEntryDate;
	Calendar lastEntryDate;
	
	Map<Calendar,Double> weightEntries = new HashMap<Calendar,Double>(364);
	File storage;
	
	Map<Calendar,Label> scrollPaneEntries = new HashMap<Calendar, Label>(364);
	ArrayList<Line> lineList = new ArrayList<Line>();
	ArrayList<Ellipse> dotList = new ArrayList<Ellipse>();
	ArrayList<Shape> graphBase = new ArrayList<Shape>();
	ObservableList<AverageTableRow> averageTableRows = FXCollections.observableArrayList();
	
	@Override
	public void start(Stage window){
		try {
			GridPane mainPane = new GridPane();
			GridPane topHalfMainPane = new GridPane();
			GridPane bottomHalfMainPane = new GridPane();
			setGridColumnConstraints(mainPane, 100.0, HPos.CENTER);
			for(int i = 0; i < 2; i++) {
				setGridRowConstraints(mainPane,100.0/2,VPos.CENTER);
			}
			mainPane.add(topHalfMainPane, 0, 0);
			mainPane.add(bottomHalfMainPane, 0, 1);
			Scene scene = new Scene(mainPane,screenBounds.getHeight(),screenBounds.getHeight()*0.8);
			if(scene.getWidth()<880) {
				window.setMaximized(true);
			}
			scene.getStylesheets().add(getClass().getResource("/resources/SceneTheame.css").toExternalForm());
			for(int i = 0; i < 3; i++) {
				setGridColumnConstraints(topHalfMainPane,100.0/3,HPos.CENTER);
			}
			setGridRowConstraints(topHalfMainPane,100.0,VPos.CENTER);
			topHalfMainPane.setPadding(new Insets(20));
			
			setGridColumnConstraints(bottomHalfMainPane, 10.0, HPos.CENTER);
			setGridColumnConstraints(bottomHalfMainPane, 35.0, HPos.CENTER);
			setGridColumnConstraints(bottomHalfMainPane, 10.0, HPos.CENTER);
			setGridColumnConstraints(bottomHalfMainPane, 35.0, HPos.CENTER);
			setGridColumnConstraints(bottomHalfMainPane, 10.0, HPos.CENTER);
			setGridRowConstraints(bottomHalfMainPane, 100.0, VPos.CENTER);
			bottomHalfMainPane.setPadding(new Insets(10,20,10,20));
			
			decimalSymbol.setDecimalSeparator('.');
			infoDf.setDecimalFormatSymbols(decimalSymbol);
			graphDf.setDecimalFormatSymbols(decimalSymbol);
			tableDf.setDecimalFormatSymbols(decimalSymbol);
			
			graphPane = new Group();
			entryPane = new GridPane();
			datePane = new GridPane();
			weightButtonPane = new GridPane();
			infoPane = new GridPane();
			innerInfoPane = new GridPane();
			entryScrollPanePane = new GridPane();
			entryScrollPane = new ScrollPane();
			dailyEntryTable = new GridPane();
			averageTablePane = new GridPane();
			
			
			if(scene.getWidth()<880) {
				graph = new Rectangle(0,0,screenBounds.getHeight()/2.75,screenBounds.getHeight()/2.75);
			}else {
				graph = new Rectangle(0,0,scene.getHeight()/2.75,scene.getHeight()/2.75);
			}
			graph.setFill(Color.GHOSTWHITE);
			firstDateText = new Text(-1, graph.getHeight()+20,"");
			firstDateText.setFont(graphFont);
			firstDateText.setFill(Color.SILVER);
			lastDateText = new Text(graph.getWidth(),graph.getHeight()+20,"");
			lastDateText.setFont(graphFont);
			lastDateText.setFill(Color.SILVER);
			
			graphPane.getChildren().add(graph);
			graphPane.getChildren().addAll(firstDateText,lastDateText);
			scopeIndex = graph.getHeight()/(graphCeil-graphBot);
			drawGraphBase();
			
			
			weightField = new TextField();
			weightField.setPromptText("kg");
			errorLabel = new Label();
			errorLabel.setId("error-label");
			errorLabel2 = new Label();
			errorLabel2.setId("error-label");
			dayBox = new ComboBox<Integer>();
			for(int i = 1; i <= currentDate.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
				dayBox.getItems().add(i);
			}
			dayBox.getSelectionModel().select(currentDate.get(Calendar.DAY_OF_MONTH)-1);
			dayBox.setOnAction(this);
			monthBox = new ComboBox<String>();
			for(int i = 0; i < 12; i++) {
				monthBox.getItems().add(numberToMonth(i));
			}
			monthBox.getSelectionModel().select(currentDate.get(Calendar.MONTH));
			monthBox.setOnAction(this);
			yearBox = new ComboBox<Integer>();
			for(int i = currentDate.get(Calendar.YEAR)-10; i <= currentDate.get(Calendar.YEAR)+10; i++) {
				yearBox.getItems().add(i);
			}
			yearBox.getSelectionModel().select(10);
			yearBox.setOnAction(this);
			weightEntry = new Button("Add");
			weightEntry.setMinWidth(66);
			weightEntry.setOnAction(this);
			weightRemoval = new Button("Remove");
			weightRemoval.setMinWidth(66);
			weightRemoval.getStyleClass().add("red-button");
			weightRemoval.setOnAction(this);
			
			currentLabel.setId("info-label");
			currentLabel.setText("Current:\nn/a");
			averageLabel.setId("info-label");
			averageLabel.setText("Average:\nn/a");
			progressLabel.setId("info-label");
			progressLabel.setText("Progress:\nn/a");
			
			periodBox = new ComboBox<String>();
			periodBox.getItems().addAll("1 Week", "2 Weeks", "3 Weeks", "1 Month", "2 Months", "3 Months", "6 Months", "1 Year", "Since first entry");
			periodBox.getSelectionModel().select(0);
			periodBox.setOnAction(this);
			
			setGridColumnConstraints(entryPane, 100.0, HPos.CENTER);
			for(int i = 0; i <10; i++) {
				setGridRowConstraints(entryPane, 100.0/10, VPos.CENTER);
			}
			
			for(int i = 0; i < 3; i++) {
				setGridColumnConstraints(datePane, 100.0/3, HPos.CENTER);
			}
			setGridRowConstraints(datePane,100.0, VPos.CENTER);
			setGridColumnConstraints(weightButtonPane, 45.0, HPos.RIGHT);
			setGridColumnConstraints(weightButtonPane, 10.0, HPos.CENTER);
			setGridColumnConstraints(weightButtonPane, 45.0, HPos.LEFT);
			setGridRowConstraints(weightButtonPane, 100.0, VPos.CENTER);
			
			setGridColumnConstraints(infoPane, 100.0, HPos.CENTER);
			for(int i = 0; i <9; i++) {
				setGridRowConstraints(infoPane, 100.0/10, VPos.CENTER);
				if(i == 2) {
					setGridRowConstraints(infoPane, 100.0/5, VPos.TOP);
				}
			}
			
			for(int i = 0; i < 3; i++) {
				setGridColumnConstraints(innerInfoPane, 100.0/3, HPos.CENTER);
			}
			setGridRowConstraints(innerInfoPane, 100.0, VPos.CENTER);
			
			setGridColumnConstraints(dailyEntryTable,100.0,HPos.CENTER);
			
			setGridColumnConstraints(entryScrollPanePane, 100.0, HPos.CENTER);
			setGridRowConstraints(entryScrollPanePane, 100.0, VPos.TOP);
			
			setGridRowConstraints(averageTablePane, 80.0, VPos.TOP);
			setGridRowConstraints(averageTablePane, 20.0, VPos.CENTER);
			setGridColumnConstraints(averageTablePane, 100.0, HPos.CENTER);
			
			datePane.add(dayBox, 0, 0);
			datePane.add(monthBox, 1, 0);
			datePane.add(yearBox, 2, 0);
			weightButtonPane.add(weightEntry, 0, 0);
			weightButtonPane.add(weightRemoval, 2, 0);
			
			entryPane.add(weightField, 0, 3);
			entryPane.add(errorLabel, 0, 4);
			entryPane.add(datePane, 0, 5);
			entryPane.add(errorLabel2, 0, 6);
			entryPane.add(weightButtonPane, 0, 7);
		
			innerInfoPane.add(currentLabel, 0, 0);
			innerInfoPane.add(averageLabel, 1, 0);
			innerInfoPane.add(progressLabel, 2, 0);
			
			infoPane.add(innerInfoPane, 0, 3);
			infoPane.add(periodBox, 0, 5);
			
			
			topHalfMainPane.add(graphPane, 0, 0);
			topHalfMainPane.add(entryPane, 1, 0);
			topHalfMainPane.add(infoPane, 2, 0);
			
			dailyEntryTable.setMinSize(300, 50);
			dailyEntryTable.setPadding(new Insets(0,20,0,5));
			entryScrollPane.setMinSize(300, 350);
			entryScrollPane.setMaxSize(300, 350);
			entryScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
			entryScrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
			entryScrollPane.setContent(dailyEntryTable);
			entryScrollPanePane.add(entryScrollPane,0,0);
			
			createAverageTableRows();
			averageTable = new TableView<>();
			averageTable.setMaxSize(300, 350);
			averageTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			averageTable.setItems(averageTableRows);
			averageTable.getColumns().add(startDateColumn);
			averageTable.getColumns().add(averageWeightColumn);
			averageTable.getColumns().add(endDateColumn);
			averageTableBox = new ComboBox<>();
			averageTableBox.getItems().addAll("Weekly", "Monthly", "Yearly");
			averageTableBox.getSelectionModel().select(0);
			averageTableBox.setOnAction(this);
			averageTablePane.add(averageTable, 0, 0);
			averageTablePane.add(averageTableBox, 0, 1);
			
			bottomHalfMainPane.add(entryScrollPanePane, 1, 0);
			bottomHalfMainPane.add(averageTablePane, 3, 0);
			
			String home = System.getenv("APPDATA");
			if(home==null) {
				home = (System.getProperty("user.home"));
			}
			storage = new File(home,".weightTracker");
			storage.mkdir();
			
			try {
				if(readEntries().size()>0) {
					weightEntries = readEntries();
					updateGraph();
					clearEntryScrollPane();
					updateEntryScrollPane();
					currentLabelUpdate(weightEntries.get(lastEntryDate));
				}
			}catch(FileNotFoundException exc) {
				//
			}
			
			try {
				readSettings("GraphPeriod");
			}catch(FileNotFoundException e) {
				saveSettings("GraphPeriod", graphPeriod);
				readSettings("GraphPeriod");
			}
			try {
				readSettings("AverageTable");
			}catch(FileNotFoundException e) {
				saveSettings("AverageTable", tableRowMargins);
				readSettings("AverageTable");
			}
			
			window.setTitle("Arabadzhiev's Weight Tracker");
			window.getIcons().add(new Image(MainWindow.class.getResourceAsStream("/resources/fitnessIcon.png")));
			window.setScene(scene);
			window.show();
			setWindowCentered(screenBounds, window);
			window.setMinWidth(window.getWidth());
			window.setMinHeight(window.getHeight());
			stageBounds = new Rectangle2D(window.getX(),window.getY(),window.getWidth(),window.getHeight());
			window.xProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> property, Number oldValue, Number newValue) {
					stageBounds = new Rectangle2D((double)newValue,stageBounds.getMinY(),stageBounds.getWidth(),stageBounds.getHeight());
				}
				
			});
			window.yProperty().addListener((property, olvValue, newValue) -> stageBounds = new Rectangle2D(stageBounds.getMinX(),
					(double) newValue,stageBounds.getWidth(),stageBounds.getHeight()));
			
			switch(graphPeriod) {
			case "1 Week":
				if(lineList.isEmpty()==false) {
					checkForGraphExpansion();
				}else {
					if(firstEntryDate!=null) {
						Calendar lastDateRepository = new GregorianCalendar(firstEntryDate.get(Calendar.YEAR),
								firstEntryDate.get(Calendar.MONTH),
								firstEntryDate.get(Calendar.DAY_OF_MONTH)+graphDays);
						updateGraphDate(firstEntryDate, lastDateRepository, monthOfYear);
					}
				}
				break;
			case "2 Weeks":
				periodBox.getSelectionModel().select(1);
				break;
			case "3 Weeks":
				periodBox.getSelectionModel().select(2);
				break;
				case "1 Month":
				periodBox.getSelectionModel().select(3);
				break;
			case "2 Months":
				periodBox.getSelectionModel().select(4);
				break;
			case "3 Months":
				periodBox.getSelectionModel().select(5);
				break;
			case "6 Months":
				periodBox.getSelectionModel().select(6);
				break;
			case "1 Year":
				periodBox.getSelectionModel().select(7);
				break;
			case "Since first entry":
				periodBox.getSelectionModel().select(8);
				break;
			}
		
			switch(tableRowMargins) {
			case "Weekly":
				if(weightEntries.size()>0) {
					updateAverageTable();
				}
				break;
			case "Monthly":
				averageTableBox.getSelectionModel().select(1);
				break;
			case "Yearly":
				averageTableBox.getSelectionModel().select(2);
			}
			
		}catch(Exception oops) {
			oops.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void setWindowCentered(Rectangle2D screenBounds, Stage window) {
		window.setX((screenBounds.getWidth()-window.getWidth())/2);
		window.setY((screenBounds.getHeight()-window.getHeight())/2);
		
	}
	
	public static void setGridColumnConstraints(GridPane editedPane, double columnPercent, HPos hAligment) {
		ColumnConstraints cc = new ColumnConstraints();
		cc.setPercentWidth(columnPercent);
		cc.setHalignment(hAligment);
		editedPane.getColumnConstraints().add(cc);
	}
	
	public static void setGridRowConstraints(GridPane editedPane, double rowPercent, VPos vAligment) {
		RowConstraints rc = new RowConstraints();
		rc.setPercentHeight(rowPercent);
		rc.setValignment(vAligment);
		editedPane.getRowConstraints().add(rc);
	}
	
	private void saveEntries(Map<Calendar,Double> savedMap) throws IOException {
		FileOutputStream weightFile = new FileOutputStream(storage.getAbsolutePath()+"/weights.ser");
		ObjectOutputStream weightOut = new ObjectOutputStream(weightFile);
		FileOutputStream firstDateFile = new FileOutputStream(storage.getAbsolutePath()+"/first-date.ser");
		ObjectOutputStream firstDateOut = new ObjectOutputStream(firstDateFile);
		FileOutputStream lastDateFile = new FileOutputStream(storage.getAbsoluteFile()+"/last-date.ser");
		ObjectOutputStream lastDateOut = new ObjectOutputStream(lastDateFile);
		weightOut.writeObject(savedMap);
		weightOut.close();
		weightFile.close();
		firstDateOut.writeObject(firstEntryDate);
		firstDateOut.close();
		firstDateFile.close();
		lastDateOut.writeObject(lastEntryDate);
		lastDateOut.close();
		lastDateFile.close();
	}
	
	private HashMap<Calendar,Double> readEntries() throws IOException, ClassNotFoundException {
		FileInputStream weightFile = new FileInputStream(storage.getAbsolutePath()+"/weights.ser");
		ObjectInputStream weightIn = new ObjectInputStream(weightFile);
		FileInputStream firstDateFile = new FileInputStream(storage.getAbsolutePath()+"/first-date.ser");
		ObjectInputStream firstDateIn = new ObjectInputStream(firstDateFile);
		FileInputStream lastDateFile = new FileInputStream(storage.getAbsolutePath()+"/last-date.ser");
		ObjectInputStream lastDateIn = new ObjectInputStream(lastDateFile);
		@SuppressWarnings("unchecked")
		HashMap<Calendar, Double> returnMap = (HashMap<Calendar, Double>)weightIn.readObject();
		weightIn.close();
		weightFile.close();
		firstEntryDate = (Calendar) firstDateIn.readObject();
		firstDateIn.close();
		firstDateFile.close();
		lastEntryDate = (Calendar) lastDateIn.readObject();
		lastDateIn.close();
		lastDateFile.close();
		return returnMap;
	}
	
	private void saveSettings(String graphOrTable, String setting) throws IOException {
		switch(graphOrTable) {
			case "GraphPeriod":
				FileOutputStream graphPeriodFile = new FileOutputStream(storage.getAbsolutePath()+"/GP-settings.ser");
				ObjectOutputStream graphPeriodOut = new ObjectOutputStream(graphPeriodFile);
				graphPeriodOut.writeObject(setting);
				graphPeriodOut.close();
				graphPeriodFile.close();
				break;
			case "AverageTable":
				FileOutputStream averageTableFile = new FileOutputStream(storage.getAbsolutePath()+"/AT-settings.ser");
				ObjectOutputStream averageTableOut = new ObjectOutputStream(averageTableFile);
				averageTableOut.writeObject(setting);
				averageTableOut.close();
				averageTableFile.close();
				break;
		}
	}
	
	private void readSettings(String graphOrTable) throws IOException, ClassNotFoundException {
		switch(graphOrTable) {
			case "GraphPeriod":
				FileInputStream graphPeriodFile = new FileInputStream(storage.getAbsolutePath()+"/GP-settings.ser");
				ObjectInputStream graphPeriodIn = new ObjectInputStream(graphPeriodFile);
				graphPeriod = (String) graphPeriodIn.readObject();
				graphPeriodIn.close();
				graphPeriodFile.close();
				break;
			case "AverageTable":
				FileInputStream averageTableFile = new FileInputStream(storage.getAbsolutePath()+"/AT-settings.ser");
				ObjectInputStream averageTableIn = new ObjectInputStream(averageTableFile);
				tableRowMargins = (String) averageTableIn.readObject();
				averageTableIn.close();
				averageTableFile.close();
				break;
			
		}
	}
	
	private void drawGraphBase() {
		for(double i = graphBot; i <=graphCeil; i+=graphMargins) {
			if(graphMargins%1==0) {
				graphBase.add(drawCustomText(-20,graph.getHeight()-(i-graphBot)*scopeIndex+3,""+(int)i,graphFont,Color.FIREBRICK));
				graphPane.getChildren().add(graphBase.get(graphBase.size()-1));
			}else {
				graphBase.add(drawCustomText(-29,graph.getHeight()-(i-graphBot)*scopeIndex+3,""+graphDf.format(i),graphFont,Color.FIREBRICK));
				graphPane.getChildren().add(graphBase.get(graphBase.size()-1));
			}
			double lineY = graph.getHeight()-(i-graphBot)*scopeIndex;
			if(lineY!=0&&lineY!=graph.getHeight()) {
				graphBase.add(drawCustomLine(0.5,lineY,graph.getWidth()-0.5,lineY,Color.LIGHTGREY));
				graphPane.getChildren().add(graphBase.get(graphBase.size()-1));
			}
		}
	}
	
	private void removeGraphBase() {
		for(int i = 0; i < graphBase.size(); i++) {
			graphPane.getChildren().remove(graphBase.get(i));
		}
		graphBase.clear();
		scopeIndex = graph.getHeight()/(graphCeil-graphBot);
	}
	
	private void updateGraphDate(Calendar startDate, Calendar endDate, boolean monthOfYear) {
		if(monthOfYear) {
			firstDateText.setText(numberToMonth(startDate.get(Calendar.MONTH))+" "+ startDate.get(Calendar.YEAR));
			lastDateText.setText(numberToMonth(endDate.get(Calendar.MONTH))+" "+endDate.get(Calendar.YEAR));
		}else {
			firstDateText.setText(startDate.get(Calendar.DAY_OF_MONTH)+" "+numberToMonth(startDate.get(Calendar.MONTH)));
			lastDateText.setText(endDate.get(Calendar.DAY_OF_MONTH)+" "+numberToMonth(endDate.get(Calendar.MONDAY)));
		}
		Bounds dateBounds = lastDateText.getBoundsInLocal();
		lastDateText.setX(graph.getWidth()-dateBounds.getWidth());
	}
	
	private void checkForGraphExpansion() {
		if(sinceFirstEntry) {
			graphDays = dateDifference(firstEntryDate,lastEntryDate);
			if(graphDays>=0&&graphDays<61) {
				monthOfYear = false;
				dotSize = 2;
			}else if(graphDays>=61&&graphDays<91) {
				monthOfYear = false;
				dotSize = 1.5;
			}else if(graphDays>=91&&graphDays<182) {
				monthOfYear = false;
				dotSize = 1.25;
			}else if(graphDays>=182&&graphDays<364) {
				monthOfYear = false;
				dotSize = 0.75;
			}else if(graphDays>=364) {
				monthOfYear = true;
				dotSize = 0.5;
			}
		}
		double highestInGraph=20;
		double lowestInGraph=200;
		int loopLenght = graphDays+1;
		if(graphDays+1>dateDifference(firstEntryDate,lastEntryDate)) loopLenght = dateDifference(firstEntryDate,lastEntryDate)+1;
		Calendar loopDate = new GregorianCalendar(lastEntryDate.get(Calendar.YEAR),lastEntryDate.get(Calendar.MONDAY),
				lastEntryDate.get(Calendar.DAY_OF_MONTH)-loopLenght+1);
		for(int i = 0; i < loopLenght; i++) {
			double check = 0;
			if(weightEntries.get(loopDate)!=null) {
				check = weightEntries.get(loopDate);
				if(highestInGraph<check) {
					highestInGraph = check;
				}
				if(lowestInGraph>check) {
					lowestInGraph = check;
				}
			}
			
			loopDate = new GregorianCalendar(loopDate.get(Calendar.YEAR),loopDate.get(Calendar.MONTH),
					loopDate.get(Calendar.DAY_OF_MONTH)+1);
		}
		
		graphCeil = highestInGraph;
		graphBot = lowestInGraph;
		double marginDeterminer = graphCeil-graphBot;
		if(marginDeterminer>0 && marginDeterminer<=1) {
			graphCeil += 0.1;
			graphBot -= 0.1;
			graphMargins = 0.1;
		}else if(marginDeterminer>1 && marginDeterminer<=3) {
			graphCeil += 0.5;
			graphBot -= 0.5;
			graphMargins = 0.5;
		}else if(marginDeterminer>3 && marginDeterminer<=5) {
			graphCeil += 1;
			graphBot -= 1;
			graphMargins = 1;
		}else if(marginDeterminer>5 && marginDeterminer<=20) {
			graphCeil += 2;
			graphBot -= 2;
			graphMargins = 2;
		}else if(marginDeterminer>20 && marginDeterminer<=50) {
			graphCeil += 5;
			graphBot -=5 ;
			graphMargins = 5;
		}else if(marginDeterminer>50 && marginDeterminer<=100) {
			graphCeil += 10;
			graphBot -= 10;
			graphMargins = 10;
		}else if(marginDeterminer>100 && marginDeterminer<=200) {
			graphCeil += 20;
			graphBot -= 20;
			graphMargins = 20;
		}else {
			graphCeil += 5;
			graphBot -= 5;
			graphMargins = 5;
		}
		removeGraphBase();
		drawGraphBase();
		if(lineList.size()!=0) {
			updateGraph();
		}
	}
	
	private void updateGraph() {
		double lineIncrement = graph.getWidth()/graphDays;
		for(int i = 0; i < lineList.size(); i++) {
			graphPane.getChildren().remove(lineList.get(i));
		}
		lineList.clear();
		for(int i = 0; i < dotList.size(); i++) {
			graphPane.getChildren().remove(dotList.get(i));
		}
		dotList.clear();
		
		if(graphDays<=dateDifference(firstEntryDate,lastEntryDate)) {
			Calendar referenceDate = new GregorianCalendar(lastEntryDate.get(Calendar.YEAR),
					lastEntryDate.get(Calendar.MONTH), lastEntryDate.get(Calendar.DAY_OF_MONTH)-graphDays);
			updateGraphDate(referenceDate, lastEntryDate, monthOfYear);
			while(weightEntries.get(referenceDate)==null) {
				referenceDate = new GregorianCalendar(referenceDate.get(Calendar.YEAR),referenceDate.get(Calendar.MONTH),
						referenceDate.get(Calendar.DAY_OF_MONTH)+1);
			}
			double lastDrawnX = 0;
			double lastDrawnY = weightToCoordinate(weightEntries.get(referenceDate))+graphBot*scopeIndex;
			for(int i = 1; i <= graphDays; i++) {
				Calendar loopDate = new GregorianCalendar(lastEntryDate.get(Calendar.YEAR),
						lastEntryDate.get(Calendar.MONTH), lastEntryDate.get(Calendar.DAY_OF_MONTH)-graphDays+i);
				if(weightEntries.get(loopDate)!=null) {
					addCustomLine(drawCustomLine(lastDrawnX,lastDrawnY,
							lineIncrement*i,weightToCoordinate(weightEntries.get(loopDate))+graphBot*scopeIndex,Color.DARKBLUE));
					if(lineIncrement*i<graph.getWidth()) {
						addCustomDot(drawCustomDot(lineIncrement*i,weightToCoordinate(weightEntries.get(loopDate)),Color.CRIMSON));
					}
					lastDrawnX = lineList.get(lineList.size()-1).getEndX();
					lastDrawnY = lineList.get(lineList.size()-1).getEndY();
				}
			}
		}else {
			Calendar lastDateRepository = new GregorianCalendar(firstEntryDate.get(Calendar.YEAR),firstEntryDate.get(Calendar.MONTH),
					firstEntryDate.get(Calendar.DAY_OF_MONTH)+graphDays);
			updateGraphDate(firstEntryDate, lastDateRepository, monthOfYear);
			double lastDrawnX = 0;
			double lastDrawnY = weightToCoordinate(weightEntries.get(firstEntryDate))+graphBot*scopeIndex;
			for(int i = 1; i <= graphDays; i++) {
				Calendar loopDate = new GregorianCalendar(firstEntryDate.get(Calendar.YEAR),
						firstEntryDate.get(Calendar.MONTH),firstEntryDate.get(Calendar.DAY_OF_MONTH)+i);
				if(weightEntries.get(loopDate)!=null) {
					addCustomLine(drawCustomLine(lastDrawnX,lastDrawnY,
							lineIncrement*i,weightToCoordinate(weightEntries.get(loopDate))+graphBot*scopeIndex,Color.DARKBLUE));
					if(loopDate.equals(lastEntryDate)) {
						double dotSizeStorage = dotSize;
						dotSize += 0.5;
						addCustomDot(drawCustomDot(lineIncrement*i,weightToCoordinate(weightEntries.get(loopDate)),Color.SEAGREEN));
						dotSize = dotSizeStorage;
					}else {
						addCustomDot(drawCustomDot(lineIncrement*i,weightToCoordinate(weightEntries.get(loopDate)),Color.CRIMSON));
					}
					lastDrawnX = lineList.get(lineList.size()-1).getEndX();
					lastDrawnY = lineList.get(lineList.size()-1).getEndY();
				}
			}
		}
		averageLabelUpdate();
		if(lineList.size()>0) {
			progressLabelUpdate(lineList.get(0).getStartY()/scopeIndex
					,lineList.get(lineList.size()-1).getEndY()/scopeIndex);
		}
	}
	
	private void updateEntryScrollPane() {
		int tableSize = 0;
		Calendar targetDate = lastEntryDate;
		while(targetDate.equals(firstEntryDate)|targetDate.after(firstEntryDate)) {
			if(weightEntries.get(targetDate)!=null) {
				tableSize++;
			}
			targetDate = new GregorianCalendar(targetDate.get(Calendar.YEAR),targetDate.get(Calendar.MONTH),
					targetDate.get(Calendar.DAY_OF_MONTH)-1);
		}
		targetDate = lastEntryDate;
		for(int i = 0; i < tableSize; i++) {
			while(weightEntries.get(targetDate)==null) {
				targetDate = new GregorianCalendar(targetDate.get(Calendar.YEAR),targetDate.get(Calendar.MONTH),
						targetDate.get(Calendar.DAY_OF_MONTH)-1);
			}
			RowConstraints rc = new RowConstraints();
			rc.setPercentHeight(100.0/tableSize);
			rc.setValignment(VPos.TOP);
			rc.setMinHeight(50);
			dailyEntryTable.getRowConstraints().add(rc);
			GridPane tablePane = new GridPane();
			setGridColumnConstraints(tablePane, 50.0, HPos.LEFT);
			setGridColumnConstraints(tablePane, 50.0, HPos.RIGHT);
			setGridRowConstraints(tablePane, 100.0, VPos.CENTER);
			tablePane.setBorder(entryTableBorder);
			Label dateLabel = new Label(targetDate.get(Calendar.DAY_OF_MONTH)+" "+
					numberToMonth(targetDate.get(Calendar.MONTH))+" "+targetDate.get(Calendar.YEAR)+"\n"+
					numberToWeek(targetDate.get(Calendar.DAY_OF_WEEK)));
			dateLabel.setId("date-label");
			Label weightLabel = new Label(infoDf.format(weightEntries.get(targetDate))+" kg");
			targetDate = new GregorianCalendar(targetDate.get(Calendar.YEAR),targetDate.get(Calendar.MONTH),
					targetDate.get(Calendar.DAY_OF_MONTH)-1);
			weightLabel.setId("weight-label");
			weightLabel.setTextFill(Color.DARKBLUE);
			tablePane.add(dateLabel, 0, 0);
			tablePane.add(weightLabel, 1, 0);
			dailyEntryTable.add(tablePane, 0, i);
		}
		
	}
	
	private void clearEntryScrollPane() {
		dailyEntryTable.getChildren().clear();
		dailyEntryTable.getRowConstraints().clear();
	}
	
	private void updateAverageTable() {
		averageTableRows.clear();
		int tableRowLenght = 7;
		Calendar startDate = new GregorianCalendar(lastEntryDate.get(Calendar.YEAR),lastEntryDate.get(Calendar.MONTH),
				lastEntryDate.get(Calendar.DAY_OF_MONTH)+lastEntryDate.getActualMaximum(Calendar.DAY_OF_WEEK)-convertWeekNumber(lastEntryDate.get(Calendar.DAY_OF_WEEK)));;
		int tableSize = dateDifference(firstEntryDate, startDate)/tableRowLenght+1;
		switch(tableRowMargins) {
			case "Weekly":
				break;
			case "Monthly":
				startDate = new GregorianCalendar(lastEntryDate.get(Calendar.YEAR),lastEntryDate.get(Calendar.MONTH),
						lastEntryDate.getActualMaximum(Calendar.DAY_OF_MONTH));
				tableRowLenght = startDate.getActualMaximum(Calendar.DAY_OF_MONTH);
				tableSize = (startDate.get(Calendar.YEAR)-firstEntryDate.get(Calendar.YEAR))*12+
						startDate.get(Calendar.MONTH)-firstEntryDate.get(Calendar.MONTH)+1;
				
				break;
			case "Yearly":
				startDate = new GregorianCalendar(lastEntryDate.get(Calendar.YEAR),11,31);
				tableRowLenght = startDate.getActualMaximum(Calendar.DAY_OF_YEAR);
				tableSize = startDate.get(Calendar.YEAR)-firstEntryDate.get(Calendar.YEAR)+1;
				break;
		}
		Calendar loopDate = startDate;
		for(int i = 0; i < tableSize; i++) {
			double average=0;
			double entrySum=0;
			int averageFactor=0;
			if(weightEntries.get(loopDate)!=null) {
				entrySum+=weightEntries.get(loopDate);
				averageFactor++;
			}
			for(int j = 1; j < tableRowLenght; j++) {
				loopDate = new GregorianCalendar(loopDate.get(Calendar.YEAR),loopDate.get(Calendar.MONTH),
						loopDate.get(Calendar.DAY_OF_MONTH)-1);
				if(weightEntries.get(loopDate)!=null) {
					entrySum+=weightEntries.get(loopDate);
					averageFactor++;
				}
			}
			average = entrySum/averageFactor;
			if(averageFactor!=0) {
				averageTableRows.add(new AverageTableRow(loopDate,tableDf.format(average),startDate));
			}
			loopDate = new GregorianCalendar(loopDate.get(Calendar.YEAR),loopDate.get(Calendar.MONTH),
					loopDate.get(Calendar.DAY_OF_MONTH)-1);
			startDate = loopDate;
			switch(tableRowMargins) {
				case "Weekly":
					break;
				case "Monthly":
					tableRowLenght = startDate.getActualMaximum(Calendar.DAY_OF_MONTH);
					break;
				case "Yearly":
					tableRowLenght = startDate.get(Calendar.DAY_OF_YEAR);
					break;
			}
		}
	}
	
	private void createAverageTableRows() {
		startDateColumn = new TableColumn<>("Start");
		startDateColumn.setMinWidth(110);
		startDateColumn.setCellFactory(column -> {
		    TableCell<AverageTableRow, Calendar> cell = new TableCell<AverageTableRow, Calendar>() {
		        private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

		        @Override
		        protected void updateItem(Calendar item, boolean empty) {
		            super.updateItem(item, empty);
		            if(empty) {
		                setText(null);
		            }
		            else {
		                setText(format.format(item.getTime()));
		            }
		        }
		    };

		    return cell;
		});
		startDateColumn.setCellValueFactory(new PropertyValueFactory<AverageTableRow, Calendar>("startDate"));
		averageWeightColumn = new TableColumn<>("Average");
		averageWeightColumn.setMinWidth(78);
		averageWeightColumn.setCellValueFactory(new PropertyValueFactory<AverageTableRow, String>("averageWeight"));
		endDateColumn = new TableColumn<>("End");
		endDateColumn.setMinWidth(110);
		endDateColumn.setCellFactory(column -> {
		    TableCell<AverageTableRow, Calendar> cell = new TableCell<AverageTableRow, Calendar>() {
		        private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

		        @Override
		        protected void updateItem(Calendar item, boolean empty) {
		            super.updateItem(item, empty);
		            if(empty) {
		                setText(null);
		            }
		            else {
		                setText(format.format(item.getTime()));
		            }
		        }
		    };

		    return cell;
		});
		endDateColumn.setCellValueFactory(new PropertyValueFactory<AverageTableRow, Calendar>("endDate"));
	}
	
	private void addWeight(double weight) {
		int year = yearBox.getValue();
		int month = monthToNumber(monthBox.getValue());
		int day = dayBox.getValue();
		Calendar adderDate = new GregorianCalendar(year,month,day);
		weightEntries.put(adderDate, weight);
	}
	
	private double weightToCoordinate(double weight) {
		double coordinate = graph.getHeight()-weight*scopeIndex;
		return coordinate;
	}
	
	private Line drawCustomLine(double startX, double startY, double endX, double endY, Color stroke) {
		Line graphLine = new Line(startX,startY,endX,endY);
		graphLine.setStroke(stroke);
		return graphLine;
	}
	
	private void addCustomLine(Line line) {
		lineList.add(line);
		graphPane.getChildren().add(lineList.get(lineList.size()-1));
		if(dotList.size()!=0) {
			graphPane.getChildren().remove(dotList.get(dotList.size()-1));
			graphPane.getChildren().add(dotList.get(dotList.size()-1));
		}
	}
	
	private Ellipse drawCustomDot(double startX, double startY,Color stroke) {
		Ellipse graphEllipse = new Ellipse(startX,startY+graphBot*scopeIndex,dotSize,dotSize);
		graphEllipse.setFill(stroke);
		return graphEllipse;
	}
	
	private void addCustomDot(Ellipse dot) {
		dotList.add(dot);
		graphPane.getChildren().add(dotList.get(dotList.size()-1));
	}
	
	private Text drawCustomText(double xPos, double yPos, String text, Font font, Color stroke) {
		Text graphText = new Text(xPos, yPos, text);
		graphText.setFont(font);
		graphText.setFill(stroke);
		return graphText;
	}
	
	private int convertWeekNumber(int number) {
		int convertedNumber = 0;
		switch (number) {
			case 1:
				convertedNumber = 7;
				break;
			case 2:
				convertedNumber = 1;
				break;
			case 3:
				convertedNumber = 2;
				break;
			case 4:
				convertedNumber = 3;
				break;
			case 5:
				convertedNumber = 4;
				break;
			case 6:
				convertedNumber = 5;
				break;
			case 7:
				convertedNumber = 6;
				break;
		}
		return convertedNumber;
	}
	
	private String numberToWeek(int number) {
		String convertedString = "";
		switch(number) {
			case 1:
				convertedString = "Sunday";
				break;
			case 2:
				convertedString = "Monday";
				break;
			case 3:
				convertedString = "Tuesday";
				break;
			case 4:
				convertedString = "Wednesday";
				break;
			case 5:
				convertedString = "Thursday";
				break;
			case 6:
				convertedString = "Friday";
				break;
			case 7:
				convertedString = "Satruday";
				break;
		}
		return convertedString;
	}
	
	private String numberToMonth(int number) {
		String convertedString = "";
		switch (number) {
			case 0:
				convertedString = "January";
				break;
			case 1:
				convertedString = "February";
				break;
			case 2:
				convertedString = "March";
				break;
			case 3:
				convertedString = "April";
				break;
			case 4:
				convertedString = "May";
				break;
			case 5:
				convertedString = "June";
				break;
			case 6:
				convertedString = "July";
				break;
			case 7:
				convertedString = "August";
				break;
			case 8:
				convertedString = "September";
				break;
			case 9:
				convertedString = "October";
				break;
			case 10:
				convertedString = "November";
				break;
			case 11:
				convertedString = "December";
				break;
				
		}		
		return convertedString;
	}
	
	private int monthToNumber(String month) {
		int number = 0;
		switch(month) {
			case "January":
				number = 0;
				break;
			case "February":
				number = 1;
				break;
			case "March":
				number = 2;
				break;
			case "April":
				number = 3;
				break;
			case "May":
				number = 4;
				break;
			case "June":
				number = 5;
				break;
			case "July":
				number = 6;
				break;
			case "August":
				number = 7;
				break;
			case "September":
				number = 8;
				break;
			case "October":
				number = 9;
				break;
			case "November":
				number = 10;
				break;
			case "December":
				number = 11;
				break;
		}
		return number;
	}
	
	private int dateDifference(Calendar dateStart, Calendar dateEnd) {
		int dateStartYear = dateStart.get(Calendar.YEAR);
		int dateEndYear = dateEnd.get(Calendar.YEAR);
		int difference = 0;
		while(dateEndYear > dateStartYear) {
			Calendar loopCalendar = new GregorianCalendar(dateEndYear, dateEnd.get(Calendar.MONTH), dateEnd.get(Calendar.DAY_OF_MONTH));
			difference += loopCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
			dateEndYear--;
		}
		difference += dateEnd.get(Calendar.DAY_OF_YEAR)-dateStart.get(Calendar.DAY_OF_YEAR);
		return difference;
	}
	
	private void currentLabelUpdate(double weight) {
		currentLabel.setText("Current:\n" + infoDf.format(weight)+" kg");
	}
	
	private void averageLabelUpdate() {
		double entrySum = 0;
		double average;
		int averageFactor = 0;
		if(dateDifference(firstEntryDate,lastEntryDate)<=graphDays) {
			for(int i = 0; i <= dateDifference(firstEntryDate,lastEntryDate); i++) {
				Calendar loopDate = new GregorianCalendar(firstEntryDate.get(Calendar.YEAR),firstEntryDate.get(Calendar.MONTH),
						firstEntryDate.get(Calendar.DAY_OF_MONTH)+i);
				if(weightEntries.get(loopDate)!=null) {
					entrySum+=weightEntries.get(loopDate);
					averageFactor++;
				}
			}
		}else {
			Calendar referenceDate = new GregorianCalendar(lastEntryDate.get(Calendar.YEAR),lastEntryDate.get(Calendar.MONTH),
					lastEntryDate.get(Calendar.DAY_OF_MONTH)-graphDays);
			for(int i = 0; i <= graphDays; i++) {
				Calendar loopDate = new GregorianCalendar(referenceDate.get(Calendar.YEAR),referenceDate.get(Calendar.MONTH),
						referenceDate.get(Calendar.DAY_OF_MONTH)+i);
				if(weightEntries.get(loopDate)!=null) {
					entrySum+=weightEntries.get(loopDate);
					averageFactor++;
				}
			}
		}
		average = entrySum/averageFactor;
		averageLabel.setText("Average:\n" + infoDf.format(average)+" kg");
	}
	private void progressLabelUpdate(double firstEntry, double lastEntry) {
		double progress = firstEntry-lastEntry;
		if(progress>0) {
			progressLabel.setText("Progress:\n" + "+" + infoDf.format(progress)+" kg");
		}else if(progress<0){
			progressLabel.setText("Progress:\n" + infoDf.format(progress)+" kg");
		}else{
			progressLabel.setText("Progress:\n" + infoDf.format(progress));
		}
	}

	@Override
	public void handle(ActionEvent event) {
		Object source = event.getSource();
		if(source==weightEntry) {
			if(errorLabel.getText()!="") {
				errorLabel.setText("");
			}
			if(errorLabel2.getText()!="") {
				errorLabel2.setText("");
			}
			int year = yearBox.getValue();
			int month = monthToNumber(monthBox.getValue());
			int day = dayBox.getValue();
			Calendar thisEntryDate = new GregorianCalendar(year,month,day);
			boolean emptyWeightField = false;
			try {
				String entryString = weightField.getText();
				if(entryString.length()==0) {
					emptyWeightField = true;
				}
				try {
					char charCheck = entryString.charAt(entryString.length()-1);
					if(charCheck=='d'||charCheck=='D'||charCheck=='f'||charCheck=='F'||charCheck=='.') {
						throw new NumberFormatException();
					}	
				}catch(StringIndexOutOfBoundsException oops) {
					errorLabel.setText("The weight field is empty!");
				}
				double entryExtract = Double.parseDouble(entryString);
				
				if(entryExtract>=20 && entryExtract<=200){
					if(weightEntries.size()>0) {
						if(weightEntries.get(thisEntryDate)!=null&&weightEntries.get(thisEntryDate)!=entryExtract) {
							if(cBox.display("Are you sure you want to replace the entry of " + infoDf.format(weightEntries.get(thisEntryDate)) + 
									" kg with " + infoDf.format(entryExtract) + " kg for " + day + " " + numberToMonth(month) + " " + year + "?")) {
								addWeight(entryExtract);
								saveEntries(weightEntries);
								clearEntryScrollPane();
								updateEntryScrollPane();
								updateAverageTable();
								updateGraph();
								currentLabelUpdate(weightEntries.get(lastEntryDate));
							}
						}else {
							addWeight(entryExtract);
							if(lastEntryDate.before(thisEntryDate)) {
								lastEntryDate = thisEntryDate;
							}else if(firstEntryDate.after(thisEntryDate)) {
								firstEntryDate = thisEntryDate;
							}
							saveEntries(weightEntries);
							clearEntryScrollPane();
							updateEntryScrollPane();
							updateAverageTable();
							updateGraph();
							currentLabelUpdate(weightEntries.get(lastEntryDate));
						}
					}else {
						addWeight(entryExtract);
						firstEntryDate = new GregorianCalendar(year,month,day);
						lastEntryDate = firstEntryDate;
						saveEntries(weightEntries);
						updateEntryScrollPane();
						updateAverageTable();
						Calendar lastDateRepository = new GregorianCalendar(year,month,day+graphDays);
						updateGraphDate(firstEntryDate, lastDateRepository, monthOfYear);
						currentLabelUpdate(weightEntries.get(lastEntryDate));
						averageLabelUpdate();
					}
					checkForGraphExpansion();
				}else {
					errorLabel.setText("Please enter a valid weight!");
				}
			}catch(NumberFormatException oops) {
				if(emptyWeightField==false) {
					errorLabel.setText(weightField.getText() + " is not a number!");
				}
			} catch (IOException e) {
				//
			}
		}else if(source==weightRemoval) {
			if(errorLabel.getText()!="") {
				errorLabel.setText("");
			}
			if(errorLabel2.getText()!="") {
				errorLabel2.setText("");
			}
			int year = yearBox.getValue();
			int month = monthToNumber(monthBox.getValue());
			int day = dayBox.getValue();
			Calendar removalDate = new GregorianCalendar(year,month,day);
			
			if(weightEntries.get(removalDate)!=null) {
				if(cBox.display("Are you sure you want to remove the entry for" + "\n" + day + " " + numberToMonth(month) + 
						" " +year + "?")) {
					weightEntries.remove(removalDate);
					if(weightEntries.size()>0) {
						if(removalDate.get(Calendar.YEAR)==lastEntryDate.get(Calendar.YEAR)&&
								removalDate.get(Calendar.MONTH)==lastEntryDate.get(Calendar.MONTH)&&
								removalDate.get(Calendar.DAY_OF_MONTH)==lastEntryDate.get(Calendar.DAY_OF_MONTH)) {
							while(weightEntries.get(removalDate)==null) {
								removalDate = new GregorianCalendar(removalDate.get(Calendar.YEAR),removalDate.get(Calendar.MONTH),
										removalDate.get(Calendar.DAY_OF_MONTH)-1);
							}
							lastEntryDate = removalDate;
						}else if(removalDate.get(Calendar.YEAR)==firstEntryDate.get(Calendar.YEAR)&&
								removalDate.get(Calendar.MONTH)==firstEntryDate.get(Calendar.MONTH)&&
								removalDate.get(Calendar.DAY_OF_MONTH)==firstEntryDate.get(Calendar.DAY_OF_MONTH)) {
							while(weightEntries.get(removalDate)==null) {
								removalDate = new GregorianCalendar(removalDate.get(Calendar.YEAR),removalDate.get(Calendar.MONTH),
										removalDate.get(Calendar.DAY_OF_MONTH)+1);
							}
							firstEntryDate = removalDate;
						}
						currentLabelUpdate(weightEntries.get(lastEntryDate));
						clearEntryScrollPane();
						updateEntryScrollPane();
						updateAverageTable();
						checkForGraphExpansion();
					}else {
						graphCeil = 200;
						graphBot = 20;
						graphMargins = 180;
						removeGraphBase();
						clearEntryScrollPane();
						averageTableRows.clear();
						drawGraphBase();
						firstDateText.setText("");
						lastDateText.setText("");
						currentLabel.setText("Current:\nn/a");
						averageLabel.setText("Average:\nn/a");
						progressLabel.setText("Progress:\nn/a");
					}
					try {
						saveEntries(weightEntries);
					} catch (IOException e) {
						//
					}
				}
			}else {
				errorLabel2.setText("There is no entry for this date.");
			}
			
		}else if(source==periodBox) {
			String selected = periodBox.getValue();
			try {
				saveSettings("GraphPeriod", selected);
			} catch (IOException e) {
				//
			}
			switch(selected) {
				case "1 Week":
					sinceFirstEntry = false;
					monthOfYear = false;
					graphDays = 6;
					dotSize = 2;
					break;
				case "2 Weeks":
					sinceFirstEntry = false;
					monthOfYear = false;
					graphDays = 13;
					dotSize = 2;
					break;
				case "3 Weeks":
					sinceFirstEntry = false;
					monthOfYear = false;
					graphDays = 20;
					dotSize = 2;
					break;
				case "1 Month":
					sinceFirstEntry = false;
					monthOfYear = false;
					graphDays = 30;
					dotSize = 2;
					break;
				case "2 Months":
					sinceFirstEntry = false;
					monthOfYear = false;
					graphDays = 61;
					dotSize = 1.5;
					break;
				case "3 Months":
					monthOfYear = false;
					sinceFirstEntry = false;
					graphDays = 91;
					dotSize = 1.25;
					break;
				case "6 Months":
					monthOfYear = false;
					sinceFirstEntry = false;
					graphDays = 182;
					dotSize = 0.75;
					break;
				case "1 Year":
					monthOfYear = true;
					sinceFirstEntry = false;
					graphDays = 364;
					dotSize = 0.5;
					break;
				case "Since first entry":
					sinceFirstEntry = true;
					break;
			}
			if(lineList.isEmpty()==false) {
				checkForGraphExpansion();
			}else {
				if(firstEntryDate!=null) {
					Calendar lastDateRepository = new GregorianCalendar(firstEntryDate.get(Calendar.YEAR),
							firstEntryDate.get(Calendar.MONTH),
							firstEntryDate.get(Calendar.DAY_OF_MONTH)+graphDays);
					updateGraphDate(firstEntryDate, lastDateRepository, monthOfYear);
				}
			}
				
		}else if(source==dayBox||source==monthBox||source==yearBox) {
			if(errorLabel2.getText()!="") {
				errorLabel2.setText("");
			}
			int day = dayBox.getValue();
			int month = monthToNumber(monthBox.getValue());
			int year = yearBox.getValue();
			Calendar entryDate = new GregorianCalendar(year,month,1);
			if(day>entryDate.getActualMaximum(Calendar.DAY_OF_MONTH)) {
				day = entryDate.getActualMaximum(Calendar.DAY_OF_MONTH);
			}
			entryDate = new GregorianCalendar(year,month,day);
			
			if(entryDate.getActualMaximum(Calendar.DAY_OF_MONTH)!=dayBox.getItems().get(dayBox.getItems().size()-1)) {
				dayBox.setOnAction(null);
				dayBox.getItems().clear();
				for(int i = 1; i <= entryDate.getActualMaximum(Calendar.DAY_OF_MONTH); i++){
					dayBox.getItems().add(i);
				}
				dayBox.getSelectionModel().select(day-1);
				dayBox.setOnAction(this);
			}
			
			if(entryDate.after(currentDate)) {
				errorLabel2.setText("Chill out! You can't guess your future weight.");
				entryDate = currentDate;
				dayBox.setOnAction(null);
				monthBox.setOnAction(null);
				yearBox.setOnAction(null);
				dayBox.getSelectionModel().select(entryDate.get(Calendar.DAY_OF_MONTH)-1);
				monthBox.getSelectionModel().select(entryDate.get(Calendar.MONTH));
				yearBox.getSelectionModel().select(entryDate.get(Calendar.YEAR)-yearBox.getItems().get(0));
				dayBox.setOnAction(this);
				monthBox.setOnAction(this);
				yearBox.setOnAction(this);
			}
		}else if(source == averageTableBox)	{
			switch(averageTableBox.getValue()) {
				case "Weekly":
					tableRowMargins = "Weekly";
					break;
				case "Monthly":
					tableRowMargins = "Monthly";
					break;
				case "Yearly":
					tableRowMargins = "Yearly";
					break;
			}
			try {
				saveSettings("AverageTable", tableRowMargins);
			} catch (IOException e) {
				//
			}
			if(weightEntries.size()>0) {
				updateAverageTable();
			}
		}
	}
}
