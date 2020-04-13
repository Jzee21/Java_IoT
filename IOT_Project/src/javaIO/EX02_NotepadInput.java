package javaIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class EX02_NotepadInput extends Application {
	
	private TextArea ta;
	private Button oepnBtn, saveBtn;
	private Thread currentThread;
	

	private void printMsg(String msg) {
		Platform.runLater(() -> {
			ta.appendText(msg + "\n");
		});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		System.out.println(Thread.currentThread().getName());
		// JavaFX Application Thread

		BorderPane root = new BorderPane();
		root.setPrefSize(700, 500);
		
		ta = new TextArea();
		root.setCenter(ta);
		
		// oepnBtn, saveBtn
		oepnBtn = new Button("Open File");
		oepnBtn.setPrefSize(250, 50);
		oepnBtn.setOnAction((e) -> {
			//
			ta.clear();
			// select file to open	- file chooser
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open File");
						
			try {
				File file = fileChooser.showOpenDialog(primaryStage);
				
				FileReader fr = new FileReader(file);	// try exception
				BufferedReader br = new BufferedReader(fr);
				
				String line = "";
				while ((line = br.readLine()) != null) {
					printMsg(line);
				}
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			
		});
		
		saveBtn = new Button("Save File");
		saveBtn.setPrefSize(250, 50);
		saveBtn.setOnAction((e) -> {
			//
		});
		
		FlowPane flowpane = new FlowPane();
		flowpane.setPrefSize(700, 50);
		//flowpane.getChildren().add(startBtn);
		flowpane.getChildren().addAll(oepnBtn, saveBtn);
		
		root.setBottom(flowpane);
		
		//
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("예제용 JavaFX");
		primaryStage.setOnCloseRequest(e -> {
			// System.exit(0);
		});
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch();
	}
	
}
