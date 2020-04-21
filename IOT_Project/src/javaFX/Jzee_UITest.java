package javaFX;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Jzee_UITest extends Application {
	
	private TextArea textarea;
	
	
	//
	public void displayText(String msg) {
		Platform.runLater(() -> {
			textarea.appendText(msg + "\n");
		});
	}
	
	public void dispalyText(TextArea ta, String msg) {
		Platform.runLater(() -> {
			ta.appendText(msg + "\n");
		});
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// Page 2
		// ...
		BorderPane root = new BorderPane();
		root.setPrefSize(700, 500);
		textarea = new TextArea("root");
		
		
		
		// Page1
		// Page 1 - row 1
		HBox first_name = new HBox();
		Label nameLabel = new Label("Nickname");
		nameLabel.setStyle("-fx-font-size: 15");
		
		TextField nameField = new TextField();
		nameField.setPrefSize(200, 30);
		nameField.setPromptText("Please Enter Your Nickname");
		first_name.setAlignment(Pos.CENTER);
		first_name.setSpacing(10);
		first_name.getChildren().addAll(nameLabel, nameField);
		
		// Page 1 - row 2
		Button connBtn = new Button("Login");
		connBtn.setPrefSize(250, 30);
		connBtn.setOnAction((e) -> {
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
		});
				
		VBox first_center = new VBox();
		first_center.setPrefSize(400, 500);
		first_center.setAlignment(Pos.CENTER_RIGHT);
		first_center.setSpacing(10);
		first_center.getChildren().addAll(first_name, connBtn);
		
		BorderPane first = new BorderPane();
		first.setPrefSize(700, 500);
//		first.setRight(new FlowPane(150, 500));
//		first.setLeft(new FlowPane(150, 500));
		first.setCenter(first_center);
		
		
		
		
		//
		Scene firstScene = new Scene(first);
		primaryStage.setScene(firstScene);
		primaryStage.setTitle("Tester");
		primaryStage.setOnCloseRequest((e) -> {
			//
		});
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch();
	}

}
