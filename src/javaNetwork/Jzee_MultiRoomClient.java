package javaNetwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

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

public class Jzee_MultiRoomClient extends Application{
	
	private int userID = 123456;
	private String nickname;
	
	private BorderPane root;
	private FlowPane namePane, menuPane, inputPane;
	
	private TextArea textarea;
	
	private List<TextArea> taList;
	
	private TextField nameField, inputField;
	private Button connBtn, disconnBtn, createBtn, menuBtn;
	private Label nameLabel;
	
	private ListView<String> roomListView;
	private ListView<String> participantsListView;		// 채팅방 참여목록
	
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;
//	private Thread receiver;
	private ExecutorService receiverPool;
	private ExecutorService senderPool;
	
	private Gson gson = new Gson();
	
	
	// =================================================================
	// displayText(String msg)
	// displayText(TextArea ta, String msg)
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
	
	// setBottomPane(FlowPane pane)
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
		
		taList = new ArrayList<TextArea>();
//		taList.add(new TextArea("1"));
//		taList.add(new TextArea("2"));
//		taList.add(new TextArea("3"));
		
		
		// Right -----------------------------------------------
		// roomList
		roomListView = new ListView<String>();
		roomListView.getSelectionModel().selectedItemProperty().addListener(
				(ObservableValue<?> arg0, Object arg1, Object arg2) -> {
					int index = roomListView.getSelectionModel().getSelectedIndex();
					Platform.runLater(() -> {
						root.setCenter(taList.get(index));
						inputField.setEditable(true);
					});
				});
		
		// participantsList
		participantsListView = new ListView<String>();
		
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
			startClient();
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
			System.out.println("disconnBtn call stopClient()");
			stopClient("DisconnBtn");
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
			send(inputField.getText());
//			displayText(inputField.getText());
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
			System.out.println("closeBtn call stopClient()");
			stopClient("CloseBtn");
		});
		primaryStage.show();
		
	} // start(Stage primaryStage)

	
	// =================================================================
	// main
	public static void main(String[] args) {
		launch();
	} // main
	
	
	// =================================================================
	public void startClient() {
		
		connBtn.setDisable(true);
		disconnBtn.setDisable(false);
		receiverPool = Executors.newFixedThreadPool(1);
		senderPool = Executors.newFixedThreadPool(1);
		
		Runnable runnable = () -> {
			try {
				socket = new Socket();
				socket.connect(new InetSocketAddress("localhost", 55566));
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream());
				displayText("[Connected : " + socket.getRemoteSocketAddress() + "]");
				
				// send nickname
				String nickname = nameField.getText();
				output.println(nickname);
				output.flush();
			} catch (Exception e) {	
				System.out.println("Connection Exception");
				if(!socket.isClosed()) { 
					System.out.println("Connecter call stopClient()");
					stopClient("Connecter");
				}
				System.out.println("Connecter return");
				return;
			}
			System.out.println("Connecter start receiver");
			receive();
		};
		System.out.println("out of runnable");
		receiverPool.submit(runnable);
		System.out.println("pool.submit(runnable)");
	} // startClient()
	
	public void stopClient(String who) {
		System.out.println(who + "] stopClient() start");
		try {
			if(socket != null && !socket.isClosed()) {
				socket.close();
				System.out.println(who + "] stopClient() socket close");
				if(input != null) input.close();
				if(output != null) output.close();
				System.out.println(who + "] stopClient() stream close");
				displayText("[ Disconnected ]");
			}
			System.out.println(who + "] stopClient() socket clean");
			if(receiverPool != null && !receiverPool.isShutdown()) {
				List<Runnable> list = receiverPool.shutdownNow();
				System.out.println("reveiver ] " + list.size() + " is until running...");
//				receiverPool.shutdown();
//				do {
//					if(receiverPool.isTerminated()) {
//						receiverPool.shutdownNow();
//					}
//				} while (!receiverPool.awaitTermination(10, TimeUnit.SECONDS));
				System.out.println(who + "] stopClient() receiver pool close");
			}
			System.out.println(who + "] stopClient() receiver pool clean");
			if(senderPool != null && !senderPool.isShutdown()) {
				List<Runnable> list = senderPool.shutdownNow();
				System.out.println("sender ] " + list.size() + " is until running...");
//				senderPool.shutdown();
//				do {
//					if(senderPool.isTerminated()) {
//						senderPool.shutdownNow();
//					}
//				} while (!senderPool.awaitTermination(10, TimeUnit.SECONDS));
				System.out.println(who + "] stopClient() sender pool close");
			}
			System.out.println(who + "] stopClient() sender pool clean");
		} catch (Exception e) {
			System.out.println(who + "] stopClient() Exception");
			displayText("[ Disconnection Error ]");
			e.printStackTrace();
		} finally {
			Platform.runLater(() -> {
				connBtn.setDisable(false);
				disconnBtn.setDisable(true);
				root.setBottom(namePane);
			});
		} // try
		System.out.println(who + "] stopClient() finish");
	} // stopClient()
	
	// ---------------------------------------------------
	public void receive() {
		System.out.println("receive");
		String message = "";
		while(true) {
			try {
				System.out.println("receive running...");
				message = input.readLine();
				if(message == null) {
					// Server's socket closed
					throw new IOException();
				} else {
					displayText(message);
				}
				System.out.println("receive loop end");
			} catch (IOException e) {
				System.out.println("receiver call stopClient()");
				stopClient("receive()");
				break;
			}
		}
	} // receive()
	
	public void send(String message) {
		
		if(message != null && !message.equals(""))
			send(new Message(0x000101, userID, message));
		
//		Runnable runnable = () -> {
//			try {
////				displayText("send() : " + message);
//				String json = gson.toJson(new Message(message));
//				output.println(json);
////				output.println(message);
//				output.flush();
//			} catch (Exception e) {
//				displayText("send Error");
//				stopClient();
//			}
//		};
//		senderPool.submit(runnable);
	} // send(String message)
	
	public void send(Message message) {
		Runnable runnable = () -> {
			try {
				String jsonMsg = gson.toJson(message);
				output.println(jsonMsg);
				output.flush();
			} catch (Exception e) {
				displayText("send Error");
				System.out.println("send() call stopClient()");
				stopClient("send()");
			}
		};
		senderPool.submit(runnable);
	} // send(Message message)
	

	
	// =================================================================
	class Message {
		private int code;
		private int userID;
		private int targetID;
		private String jsonData;
		
		public Message(String jsonData) {
			this.jsonData = jsonData;
		}
		
		public Message(int code, int userID, int targetID) {
			this.code = code;
			this.userID = userID;
			this.targetID = targetID;
		}
		
		public Message(int code, int userID, String jsonData) {
			this.code = code;
			this.userID = userID;
			this.jsonData = jsonData;
		}
		
		public Message(int code, int userID, int targetID, String jsonData) {
			this(code, userID, targetID);
			this.jsonData = jsonData;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public int getUserID() {
			return userID;
		}

		public void setUserID(int userID) {
			this.userID = userID;
		}

		public int getTargetID() {
			return targetID;
		}

		public void setTargetID(int targetID) {
			this.targetID = targetID;
		}

		public String getJsonData() {
			return jsonData;
		}

		public void setJsonData(String jsonData) {
			this.jsonData = jsonData;
		}

		@Override
		public String toString() {
			return "Message [code=" + code + ", userID=" + userID + ", targetID=" + targetID + ", jsonData=" + jsonData
					+ "]";
		}
		
	} // class Message
	
}