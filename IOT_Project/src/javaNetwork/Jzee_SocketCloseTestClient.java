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

public class Jzee_SocketCloseTestClient extends Application{
	
	private String userID;
	private String userNickname;
	
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
	
	//
	public void startClient() {
		receiverPool = Executors.newFixedThreadPool(1);
		senderPool = Executors.newFixedThreadPool(1);
		
		Runnable runnable = () -> {
			try {
				socket = new Socket();
				socket.connect(new InetSocketAddress("localhost", 55566));
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream());
				displayText("[Connected : " + socket.getRemoteSocketAddress() + "]");
			} catch (Exception e) {
				if(!socket.isClosed()) { stopClient(); }
				return;
			}
			receive();
		};
		receiverPool.submit(runnable);
	}
	
	public void stopClient() {
		try {
			if(socket != null && !socket.isClosed()) {
				socket.close();
				if(input != null) input.close();
				if(output != null) output.close();
			}
			receiverPool.shutdownNow();
			senderPool.shutdownNow();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(socket != null)
			System.out.println("socket.isClosed() : " + socket.isClosed());
		System.out.println("stopClient() end");
		displayText("[Server Disconnected]");
	}
	
	public void receive() {
		String message = "";
		boolean interrupt = false;
		try {
			while(true) {
				// no ready()
				displayText("waiting readLine()");
				message = input.readLine();
				if(message == null) {
					displayText("readLine() : null");
					throw new IOException();				// Exception 어디로????????
				}
				displayText("after readLine(), input.ready() : " + input.ready());
				interrupt = Thread.interrupted();
			}
//			while(!interrupt) {
//				if(input.ready()) {
//					displayText("input.ready() : true"); 
//					displayText("waiting readLine()");
//					message = input.readLine();
//					displayText("after readLine(), input.ready() : " + input.ready());
//				}
//				interrupt = Thread.interrupted();
//			}
		} catch (IOException e) {
			stopClient();
		}
	}
	
	public void send(String message) {
		Runnable runnable = () -> {
			try {
//				displayText("send() : " + message);
				output.println(message);
				output.flush();
			} catch (Exception e) {
				displayText("send Error");
				stopClient();
			}
		};
		senderPool.submit(runnable);
	}
	
	// displayText(String msg)
	// displayText(TextArea ta, String msg)
	public void displayText(String msg) {
		Platform.runLater(() -> {
			textarea.appendText(msg + "\n");
		});
	}
	
	// setBottomPane(FlowPane pane)
	private void setBottomPane (FlowPane pane) {
		pane.setPrefSize(700, 40);
		pane.setPadding(new Insets(5,5,5,5));
		pane.setHgap(10);
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
		FlowPane inputPane = new FlowPane();
		setBottomPane(inputPane);

		connBtn = new Button("Conn");
		connBtn.setPrefSize(100, 40);
		connBtn.setOnAction((e) -> {
			startClient();
		});
		
		disconnBtn = new Button("Disconn");
		disconnBtn.setPrefSize(100, 40);
		disconnBtn.setOnAction((e) -> {
			stopClient();
		});
		
		inputField = new TextField();
		inputField.setEditable(true);
		inputField.setPrefSize(450, 40);
		inputField.setOnAction((e) -> {
			send(inputField.getText());
			inputField.clear();
		});
		
		inputPane.getChildren().addAll(connBtn, disconnBtn, inputField);
		root.setBottom(inputPane);
		
		
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
