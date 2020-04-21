package javaFX;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Jzee_UITest2 extends Application {
	
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
		
		BorderPane root = new BorderPane();
		root.setPrefSize(700, 500);
		
		GridPane center = new GridPane();
		center.setPadding(new Insets(10));
		center.setVgap(10);
		center.setHgap(10);
		center.setAlignment(Pos.CENTER);
		
		Label label = new Label("Nickname");
		label.setStyle("-fx-font-size: 15");
		
		TextField tf = new TextField();
		tf.setPrefSize(200, 30);
		tf.setPromptText("Please Enter Your Nickname");
		
		Button btn = new Button("Conn");
		btn.setPrefSize(250, 30);
		HBox box = new HBox();
		box.getChildren().add(btn);
		box.setAlignment(Pos.CENTER_RIGHT);
		
		center.add(label, 0, 0);
		center.add(tf, 1, 0);
		center.add(box, 0, 1);
		center.setColumnSpan(box, 2);
		
		root.setCenter(center);
		
		
		
		//
		Scene firstScene = new Scene(root);
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
