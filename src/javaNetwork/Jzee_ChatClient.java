package javaNetwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import javaNetwork.Jzee_MultiRoomServer.Room;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Jzee_ChatClient extends Application{
	
	// UI
	private BorderPane root;
	private FlowPane namePane, menuPane, inputPane;
	
	private TextField nameField, inputField;
	private Button connBtn, disconnBtn, createBtn, menuBtn;
	private Label nameLabel;
	
	private ListView<String> roomListView;
	private ListView<String> participantsListView;		// 채팅방 참여목록
	
	// User
	private int userID;
	private String nickname;
	private TextArea textarea;
	
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;
	private ExecutorService receiverPool;
	private ExecutorService senderPool;
	
	private List<Integer> roomIDs;
	private List<Integer> userIDs;
	
	private int currentRoomID = 0;		// default - not entered any room
	
	private Gson gson = new Gson();
	
	
	// =================================================================
	public void displayText(String msg) {
		Platform.runLater(() -> {
			textarea.appendText(msg + "\n");
		});
	}
	
	public void displayText(TextArea ta, String msg) {
		Platform.runLater(() -> {
			ta.appendText(msg + "\n");
		});
	}
	
	
	private void setBottomPane (FlowPane pane) {
		pane.setPrefSize(700, 40);
		pane.setPadding(new Insets(5,5,5,5));
		pane.setHgap(10);
	}
	
	
	// =================================================================
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		root = new BorderPane();
		root.setPrefSize(700, 500);
		
		// Center ----------------------------------------------
		textarea = new TextArea();
		textarea.setEditable(false);
		root.setCenter(textarea);
		
		
		// Right -----------------------------------------------
		// roomList
		roomListView = new ListView<String>();
		roomListView.getSelectionModel().selectedItemProperty().addListener(
				(ObservableValue<?> arg0, Object arg1, Object arg2) -> {
					int index = roomListView.getSelectionModel().getSelectedIndex();
					Platform.runLater(() -> {
//						root.setCenter(taList.get(index));
						inputField.setEditable(true);
					});
				});
		
		// participantsList
		participantsListView = new ListView<String>();
		participantsListView.getSelectionModel().selectedItemProperty().addListener(
				(ObservableValue<?> arg0, Object arg1, Object arg2) -> {
					int index = participantsListView.getSelectionModel().getSelectedIndex();
				});
		
		// Right GridPane
		GridPane gridpane = new GridPane();
		gridpane.setPadding(new Insets(5,5,5,5));
		gridpane.setVgap(10);
		gridpane.add(roomListView, 0, 0);
		gridpane.add(participantsListView, 0, 1);	// (column, row)
		
		root.setRight(gridpane);
		
		
		// Bottom ----------------------------------------------
		namePane = new FlowPane();
		menuPane = new FlowPane();
		inputPane = new FlowPane();
		
		
		// namePane
		nameLabel = new Label("Nickname");
		nameLabel.setStyle("-fx-font-size: 15");
		nameLabel.setPrefSize(150, 40);
		nameLabel.setAlignment(Pos.CENTER);
		
		nameField = new TextField();
		nameField.setPromptText("Please Enter Your Nickname");
		nameField.setPrefSize(350, 40);
		
		connBtn = new Button("Conn");
		connBtn.setPrefSize(150, 40);
		connBtn.setOnAction((e) -> {
			Platform.runLater(() -> {
				root.setBottom(menuPane);
			});
		});
		
		setBottomPane(namePane);
		namePane.getChildren().addAll(nameLabel, nameField, connBtn);
		namePane.setAlignment(Pos.CENTER);
		root.setBottom(namePane);
		
		// menuPane
		disconnBtn = new Button("Disconn");
		disconnBtn.setPrefSize(150, 40);
		disconnBtn.setOnAction((e) -> {
			Platform.runLater(() -> {
				root.setBottom(namePane);
			});
		});
		
		createBtn = new Button("Create Room");
		createBtn.setPrefSize(150, 40);
		createBtn.setOnAction((e) -> {
			Platform.runLater(() -> {
				root.setBottom(inputPane);
				inputField.setEditable(true);
			});
		});
		
		setBottomPane(menuPane);
		menuPane.getChildren().addAll(disconnBtn, createBtn);
		
		// inputPane
		menuBtn = new Button("Menu");
		menuBtn.setPrefSize(150, 40);
		menuBtn.setOnAction((e) -> {
			Platform.runLater(() -> {
				root.setBottom(menuPane);
			});
		});
		
		inputField = new TextField();
		inputField.setEditable(false);
		inputField.setPrefSize(500, 40);
		inputField.setOnAction((e) -> {
			Platform.runLater(() -> {
				inputField.clear();
			});
		});
		
		setBottomPane(inputPane);
		inputPane.getChildren().addAll(menuBtn, inputField);
		
		
		
		// Scene ------------------------------------------------
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Multi Room Chat Client");
		primaryStage.setOnCloseRequest((e) -> {
		});
		primaryStage.show();
		
	} // start(Stage primaryStage)

	
	// =================================================================
	// main
	public static void main(String[] args) {
		launch();
	} // main
	
	
	
	// =================================================================
	class ReceiveRunnable implements Runnable {
		
		public void close() {
			try {
				if(socket != null && !socket.isClosed()) {
					socket.close();
					if(input != null)	input.close();
					if(output != null)	output.close();
				}
				if(receiverPool != null && !receiverPool.isShutdown()) {
					receiverPool.shutdown();
					do {
						receiverPool.shutdownNow();
					} while (!receiverPool.awaitTermination(10, TimeUnit.SECONDS));
				}
				if(senderPool != null && !senderPool.isShutdown()) {
					senderPool.shutdown();
					do {
						senderPool.shutdownNow();
					} while (!senderPool.awaitTermination(10, TimeUnit.SECONDS));
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Platform.runLater(() -> {
				connBtn.setDisable(false);
				disconnBtn.setDisable(true);
				root.setBottom(namePane);
			});
		}
		
		@Override
		public void run() {
			
			connBtn.setDisable(true);
			disconnBtn.setDisable(false);
			
			receiverPool = Executors.newFixedThreadPool(1);
			senderPool = Executors.newFixedThreadPool(1);
			
			roomIDs = new ArrayList<Integer>();
			userIDs = new ArrayList<Integer>();
			
			// connect Server
			try {
				socket = new Socket();
				socket.connect(new InetSocketAddress("localhost", 55566));
				
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream());
				displayText("[Connected : " + socket.getRemoteSocketAddress() + "]");
				
				String line = "";
				while((line = input.readLine()) != null) {
					Message data = gson.fromJson(line, Message.class);
					
					switch (data.getCode()) {
					case "MESSAGE":
						displayText("[ 나  ] : " + data.getJsonData());
						break;

					default:
						break;
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				this.close();
			}
		}
		
	}
	
}