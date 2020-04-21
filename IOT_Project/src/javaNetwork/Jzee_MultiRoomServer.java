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
	
	private ExecutorService executor = Executors.newCachedThreadPool();
	private ServerSocket server;
	
	private Map<Integer, Room> roomlist = new ConcurrentHashMap<Integer, Room>();
	private Map<Integer, Client> connections = new ConcurrentHashMap<Integer, Client>();
	
	private final Object MONITOR = new Object();
	
	public void displayText(String msg) {
		Platform.runLater(() -> {
			textarea.appendText(msg + "\n");
		});
	}
	
	public void startServer() {
		
		executor = Executors.newCachedThreadPool();
		
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
					connections.put(client.hashCode(), client);
				} catch (SocketTimeoutException e) {					
//						displayText("" + Thread.interrupted());
					if(Thread.interrupted()) {
						break;
					} else continue;
				} catch (IOException e) {
					break;
				} // try				
			} // while
			stopServer();
//			// server close
//			if(!server.isClosed()) {
//				displayText("after stopServer");
//				if(socket != null && !socket.isClosed()) {
//					try {
//						socket.close();
//						server.close();
//					} catch (IOException e) {
//						// do nothing
//					}
//				}
//			} // if(!server.isClosed())
		}; // runnable
		executor.submit(runnable);
		
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
	} // stopServer()
	
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
			startBtn.setDisable(true);
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
			startBtn.setDisable(false);
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
	
	public static void main(String[] args) {
		launch();
	}
	
	class Client {
		int userID;
		String nickname;
		Socket socket;
//		BufferedReader input;
//		PrintWriter output;
//		List<Room> list;
		
		Client(Socket socket) {
			this.socket = socket;
			this.userID = this.hashCode();
			connections.put(userID, this);
			receive();
		}
		
		// method
		void receive() {
			Runnable runnable = () -> {
				try {
					BufferedReader input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
					String message = "";
					boolean interrupt = false;
					
					while(!interrupt) {
						if(input.ready()) {
							message = input.readLine();
							displayText("receive : " + message);
							if(message == null) {
								throw new IOException();
							}
							for(Integer key : connections.keySet()) {
								Client client = connections.get(key);
								client.send(message);
							}
						}
						interrupt = Thread.interrupted();
					} // while
//					while(true) {
//						
//					}
					
				} catch (IOException e) {
					displayText("InputStream Create Error");
					try {
						socket.close();
						connections.remove(Client.this);
					} catch (IOException e1) {
						// do nothing
					}
				} // try
			};
			executor.submit(runnable);
		} // receive()
		
		void send(String message) {
			Runnable runnable = () -> {
				try {
					PrintWriter output = new PrintWriter(socket.getOutputStream());
					displayText("send() : " + message);
					output.println(message);
					output.flush();
				} catch (Exception e) {
					displayText("OutputStream Create Error");
					try {
						socket.close();
						connections.remove(Client.this);
					} catch (IOException e1) {
						// do nothing
					}
				}
			};
			executor.submit(runnable);
		} // send()
		
	}
	
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
