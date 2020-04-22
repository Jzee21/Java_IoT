package javaNetwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
	
	private ExecutorService executor; // = Executors.newCachedThreadPool();
	private ServerSocket server;
	
	private Map<Integer, Room> roomlist = new ConcurrentHashMap<Integer, Room>();
	private Map<Integer, Client> connections = new ConcurrentHashMap<Integer, Client>();
	
	
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
//			stopServer();
			try {
				server.close();
				executor.shutdownNow();
			} catch (IOException e1) {
//				e1.printStackTrace();
			}
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
		
		// runnable
		Runnable runnable = () -> {
			displayText("##### Server Start #####");
			Socket socket = null;
			while(true) {
				try {
//					displayText("Ready to accept()");
					socket = server.accept();
					displayText("[" + socket.getInetAddress() + "] Client Connected");
					Client client = new Client(socket);
					connections.put(client.userID, client);
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
		executor.submit(runnable);
		
		stopBtn.setDisable(false);
		
	} // startServer() 
	
	public void stopServer() {
		try {
			for(Integer key : connections.keySet()) {
				Client client = connections.get(key);
				client.socket.close();
				connections.remove(key);
			}
			if(server != null && !server.isClosed()) {
				server.close();
			}
			if(executor != null && executor.isShutdown()) {
				executor.shutdownNow();
			}
			displayText("##### Server Stoped #####");
		} catch (Exception e) {
			// do nothing
		} // try
		startBtn.setDisable(false);
		stopBtn.setDisable(true);
	} // stopServer()
	
	
	// =================================================================
	class Client {
		int userID;
		String nickname;
		Socket socket;
		BufferedReader input;
		PrintWriter output;
//		List<Room> list;
		
		Client(Socket socket) {
			this.socket = socket;
			this.userID = this.hashCode();
			connections.put(userID, this);
			receive();
		}
		
		void closeSocket() {
			String addr = socket.getInetAddress().toString();
			displayText("[" + addr + "] cleaning...");
			try {
				if(socket != null && !socket.isClosed()) {
					socket.close();
					input.close();
					output.close();
				}
				connections.remove(Client.this.userID);
			} catch (IOException e) {
				e.printStackTrace();
			} // try
			displayText("[" + addr + "] cleaned");
		}
		
		// method
		void receive() {
			Runnable runnable = () -> {
				String message = "";
				
				try {
					input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
					output = new PrintWriter(socket.getOutputStream());
				} catch (IOException e) {
					displayText("Stream Create Error");
//					e.printStackTrace();
					this.closeSocket();
				} // try
					
				while(true) {
					try {
						message = input.readLine();
//						displayText("receive : " + message);
						if(message == null) {
							throw new IOException("Client Closed");
						}
						for(Integer key : connections.keySet()) {
							Client client = connections.get(key);
							client.send(message);
						}
					} catch (IOException e) {
						displayText("socket closed at [" + socket.getInetAddress() + "]");
//						e.printStackTrace();
						this.closeSocket();
						break;
					} // try
				} // while
					
			}; // runnable
			executor.submit(runnable);
		} // receive()
		
		void send(String message) {
			Runnable runnable = () -> {
				try {
//					displayText("send() : " + message);
					output.println(message);
					output.flush();
				} catch (Exception e) {
					displayText("OutputStream Create Error");
					this.closeSocket();
				} // try
			}; // runnable
			executor.submit(runnable);
		} // send()
		
	}
	
	
	// =================================================================
	class Room {
		int roomID;
		String roomName;
//		List<Client> list;
		
		Room(String roomName) {
			this.roomName = roomName;
			this.roomID = this.hashCode();
		}
		
	}

}
