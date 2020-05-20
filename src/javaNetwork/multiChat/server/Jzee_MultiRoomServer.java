package javaNetwork.multiChat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javaNetwork.multiChat.ChatClient;
import javaNetwork.multiChat.ChatMessage;
import javaNetwork.multiChat.ChatRoom;
import javaNetwork.multiChat.ChatService;
import javaNetwork.multiChat.LogService;
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
	
	private ExecutorService executor; // = Executors.newCachedThreadPool();
	private ServerSocket server;
	
	private ChatService chatService = ChatService.getInstnace();
	private LogService logService = LogService.getInstance();
	private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
	
	
	// =================================================================
	public void displayText(String msg) {
		Platform.runLater(() -> {
			textarea.appendText(msg + "\n");
		});
	}
	
	
	// =================================================================
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
			startServer();
		});
		
		stopBtn = new Button("Server Stop");
		stopBtn.setPrefSize(200, 40);
		stopBtn.setOnAction((event) -> {
			stopServer();
//			if(stopServer()) { displayText("##### Server Stoped #####"); }
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
		primaryStage.setTitle("Multi Room Chat Server");
		primaryStage.setOnCloseRequest((e) -> {
			//
//			System.out.println("### CloseBtn call stopServer()");
			stopServer();
		});
		primaryStage.show();
		
	}
	
	
	// =================================================================
	public static void main(String[] args) {
		launch();
	}
	
	
	// =================================================================
	public void startServer() {
		
		executor = Executors.newCachedThreadPool();
		startBtn.setDisable(true);
		
		// ServerSocket
		try {
			server = new ServerSocket();
			server.bind(new InetSocketAddress(55566));
			server.setSoTimeout(3000);	// accept() 시간을 3초로 제한
		} catch (Exception e) {
			if(!server.isClosed()) {
				stopServer();
			}
			return;		// Skip - runnable
		}
		
		Runnable getLog = () -> {
			while(true) {
				if(Thread.currentThread().isInterrupted()) {
					logService.throwsInterrupt();
					break;
				}
				try {
					String log = logService.getLog();
					displayText("[log] " + log);
				} catch (Exception e) {
					logService.throwsInterrupt();
					break;
				}
			}
		};
		executor.submit(getLog);
		
		Runnable getConnection = () -> {
			displayText("##### Server Start #####");
			Socket socket = null;
			while(true) {
				try {
					socket = server.accept();
					
					BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String nickname = input.readLine();
					
					ChatClient client = chatService.addClient(nickname, socket);
					executor.submit(client);
					
				} catch (SocketTimeoutException e) {	
					if(Thread.interrupted()) {
						break;
					} else continue;
				} catch (IOException e) {
					break;
				} // try				
			} // while
			stopServer();
		}; // runnable
		executor.submit(getConnection);
		
		stopBtn.setDisable(false);
		
	} // startServer() 
	
	public void stopServer() {
		
		if(this.server != null) {
			try {
				chatService.removeAll();
				
				if(!this.server.isClosed()) {
					server.close();
					server = null;
				}
				
				if(executor != null && !executor.isShutdown()) {
					executor.shutdownNow();
//					executor.shutdown();
//					do { 
//						// 작업이 완료되었으면 즉시 정지 한다. 
//						if (executor.isTerminated()) {
//							executor.shutdownNow();
//						}
//						// 지정된 시간 별로(10초단위) 작업이 모든 작업이 중지되었는지 체크
//						// 작업이 완료되었으면 루프 해제
//					} while (!executor.awaitTermination(10, TimeUnit.SECONDS));
				}
			} catch (Exception e) {
				// do nothing
			}
			startBtn.setDisable(false);
			stopBtn.setDisable(true);
		}
	} // stopServer()

} // Jzee_MultiRoomServer
