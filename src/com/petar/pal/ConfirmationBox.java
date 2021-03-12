package com.petar.pal;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ConfirmationBox {
	Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
	Rectangle2D mainWindowBounds;
	
	public boolean answer = false;
	
	public boolean display(String message) {
		mainWindowBounds = MainWindow.stageBounds;
		answer = false;
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Confirmation");
		
		Label messageLabel = new Label(message);
		messageLabel.setWrapText(true);
		messageLabel.setId("message-label");
		ImageView confirmationIcon = new ImageView(new Image(getClass().getResourceAsStream("/resources/confirmationIcon.png")));
		Button yesButton = new Button("Yes");
		Button noButton = new Button("No");
		yesButton.setMinWidth(50);
		noButton.setMinWidth(50);
		noButton.getStyleClass().add("red-button");
		
		yesButton.setOnAction(e->{
			answer = true;
			window.close();
		});
		noButton.setOnAction(e->{
			window.close();
		});
		
		VBox layout = new VBox();
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(1));
		GridPane buttonGrid = new GridPane();
		buttonGrid.setPadding(new Insets(10,0,0,0));
		MainWindow.setGridColumnConstraints(buttonGrid, 90.0/2, HPos.RIGHT);
		MainWindow.setGridColumnConstraints(buttonGrid, 10.0, HPos.CENTER);
		MainWindow.setGridColumnConstraints(buttonGrid, 90.0/2, HPos.LEFT);
		MainWindow.setGridRowConstraints(buttonGrid, 100.0, VPos.CENTER);
		buttonGrid.add(yesButton, 0, 0);
		buttonGrid.add(noButton, 2, 0);
		GridPane messageGrid = new GridPane();
		MainWindow.setGridColumnConstraints(messageGrid, 20.0, HPos.CENTER);
		MainWindow.setGridColumnConstraints(messageGrid, 80.0, HPos.LEFT);
		MainWindow.setGridRowConstraints(messageGrid, 100.0, VPos.CENTER);
		messageGrid.add(confirmationIcon, 0, 0);
		messageGrid.add(messageLabel, 1, 0);
		layout.getChildren().addAll(messageGrid,buttonGrid);
		Scene scene = new Scene(layout,400,120);
		scene.getStylesheets().add(getClass().getResource("/resources/SceneTheame.css").toExternalForm());
		window.getIcons().add(new Image(getClass().getResourceAsStream("/resources/fitnessIcon.png")));
		window.setResizable(false);
		window.setScene(scene);
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				double x = mainWindowBounds.getMinX()+(mainWindowBounds.getWidth()-window.getWidth())/2;
				double y = mainWindowBounds.getMinY()+(mainWindowBounds.getHeight()-window.getHeight())/2;
				
				if(x+window.getWidth()>screenBounds.getMaxX()) {
					x = x - (x+window.getWidth()-screenBounds.getMaxX());
				}else if(x<screenBounds.getMinX()) {
					x = screenBounds.getMinX();
				}
				
				if(y+window.getHeight()>screenBounds.getMaxY()) {
					y = y - (y+window.getHeight()-screenBounds.getMaxY());
				}else if(y < screenBounds.getMinY()) {
					y = screenBounds.getMinY();
				}
				
				window.setX(x);
				window.setY(y);
			}
		});
		window.showAndWait();
		return answer;
	}

}
