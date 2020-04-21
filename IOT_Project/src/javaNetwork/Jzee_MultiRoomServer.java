package javaNetwork;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class Jzee_MultiRoomServer extends Application{
	
	private TextArea textarea;
	private Button startBtn, stopBtn;
	
	private ExecutorService executor = Executors.newCachedThreadPool();
	private ServerSocket server;
	
	private HashMap<Integer, Room> rooms;
	private HashMap<Integer, Client> connections;
	
	public void displayText(String msg) {
		Platform.runLater(() -> {
			textarea.appendText(msg + "\n");
		});
	}
	
	public void startServer() {
		List<Client> list = new Vector<Client>();
	}
	
	public void stopServer() {
		
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		BorderPane root = new BorderPane();
		root.setPrefSize(700, 500);
		
		textarea = new TextArea();
		textarea.setEditable(false);
		root.setCenter(textarea);
		
		// Bottom		
		startBtn = new Button("Server Start");
		startBtn.setPrefSize(200, 40);
		startBtn.setOnAction((e) -> {
			
		});
		
		stopBtn = new Button("Server Stop");
		stopBtn.setPrefSize(200, 40);
		stopBtn.setOnAction((e) -> {
			
		});
		
		FlowPane bottom = new FlowPane();
		bottom.setPrefSize(700, 40);
		bottom.setPadding(new Insets(5,5,5,5));
		bottom.setHgap(5);
		bottom.getChildren().addAll(startBtn, stopBtn);
		bottom.setAlignment(Pos.CENTER_RIGHT);
		root.setBottom(bottom);		
		
		// Scene
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Multi Room Chat Client");
		primaryStage.setOnCloseRequest((e) -> {
			//
		});
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch();
	}
	
	class Client {
		
		int userID;
		String nickname;
		Socket socket;
		List<Room> list;
		
	}
	
	class Room {
		int roomID;
		String roomName;
		List<Client> list;
	}

}
