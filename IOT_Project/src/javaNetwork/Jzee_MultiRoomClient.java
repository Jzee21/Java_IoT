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
	
	private String userID;
	private String userNickname;
	
	private TextArea textarea;
	
	private List<TextArea> taList;
	
	private TextField nameField, inputField;
	private Button connBtn, createBtn, menuBtn;
	private Label nameLabel;
	
	private ListView<String> roomListView;
	private ListView<String> participantsListView;		// 채팅방 참여목록
	
	private Socket socket;
	private BufferedReader br;
	private PrintWriter pr;
//	private Thread receiver;
	private ExecutorService receiverPool = Executors.newFixedThreadPool(1);
	private ExecutorService senderPool = Executors.newFixedThreadPool(1);
	
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
	
	public void startClient() {
		Runnable runnable = () -> {
			try {
				socket = new Socket();
				socket.connect(new InetSocketAddress("70.12.60.91", 55566));
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				pr = new PrintWriter(socket.getOutputStream());
				displayText("[Connected : " + socket.getRemoteSocketAddress() + "]");
			} catch (Exception e) {
				if(!socket.isClosed()) { stopClient(); }
				return;
			}
			receive();
		};
		receiverPool.submit(runnable);
//		receiver = new Thread(() -> {
//			try {
//				socket = new Socket();
//				socket.connect(new InetSocketAddress("70.12.60.91", 55566));
//				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//				pr = new PrintWriter(socket.getOutputStream());
//				displayText("[Connected : " + socket.getRemoteSocketAddress() + "]");
//			} catch (Exception e) {
//				if(!socket.isClosed()) { stopClient(); }
//				return;
//			}
//			receive();
//		});
//		receiver.start();
	}
	
	public void stopClient() {
//		try {
//			receiver.interrupt();
//			if(socket != null && socket.isClosed()) {
//				br.close();
//				pr.close();
//				socket.close();
//			}
//			displayText("Server Disconnected");
//		} catch (Exception e) {
//			// do nothing
//		}
		System.out.println("called stopClient()");
		try {
			if(socket != null && !socket.isClosed()) {
				System.out.println("in if");
				br.close();
				pr.close();
				socket.close();
			}
			receiverPool.shutdownNow();
			senderPool.shutdownNow();
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("finish stopClient()");
		System.out.println(socket.isClosed());
	}
	
	public void receive() {
		String message = "";
		boolean interrupt = false;
		try {
			while(!interrupt) {
				if(br.ready()) {
					message = br.readLine();
					displayText("receive : " + message);
				}
			}
		} catch (IOException e) {
			stopClient();
		}
	}
	
	public void send(String message) {
		Runnable runnable = () -> {
			try {
				displayText("send() : " + message);
				pr.println(message);
				pr.flush();
			} catch (Exception e) {
				displayText("send Error");
				stopClient();
			}
		};
		senderPool.submit(runnable);
//		new Thread(() -> {
//			try {
//				displayText("send() : " + message);
//				pr.println(message);
//				pr.flush();
//			} catch (Exception e) {
//				displayText("send Error");
//				stopClient();
//			}
//		}).start();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		BorderPane root = new BorderPane();
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
					root.setCenter(taList.get(index));
					inputField.setEditable(true);
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
		FlowPane namePane = new FlowPane();
		FlowPane menuPane = new FlowPane();
		FlowPane inputPane = new FlowPane();
		setBottomPane(namePane);
		setBottomPane(menuPane);
		setBottomPane(inputPane);
		
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
			root.setBottom(menuPane);
		});
		
		namePane.getChildren().addAll(nameLabel, nameField, connBtn);
		namePane.setAlignment(Pos.CENTER);
		root.setBottom(namePane);
		
		// menuPane
		createBtn = new Button("Create Room");
		createBtn.setPrefSize(150, 40);
		createBtn.setOnAction((e) -> {
			root.setBottom(inputPane);
			inputField.setEditable(true);
		});
		
		menuPane.getChildren().addAll(createBtn);
		
		// inputPane
		menuBtn = new Button("Menu");
		menuBtn.setPrefSize(150, 40);
		menuBtn.setOnAction((e) -> {
			root.setBottom(menuPane);
		});
		
		inputField = new TextField();
		inputField.setEditable(false);
		inputField.setPrefSize(500, 40);
		inputField.setOnAction((e) -> {
			send(inputField.getText());
//			displayText(inputField.getText());
			inputField.clear();
		});
		
		inputPane.getChildren().addAll(menuBtn, inputField);
		
		
		
		// Scene ------------------------------------------------
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Multi Room Chat Client");
		primaryStage.setOnCloseRequest((e) -> {
			//
			System.out.println("call stopClient()");
			stopClient();
		});
		primaryStage.show();
		
	} // start(Stage primaryStage)

	// main
	public static void main(String[] args) {
		launch();
	}

}
