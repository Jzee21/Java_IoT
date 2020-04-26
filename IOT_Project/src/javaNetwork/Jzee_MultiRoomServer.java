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
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

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
	
	private Gson gson = new Gson();
	
	
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
			System.out.println("### StopBtn call stopServer()");
//			stopServer();
			if(stopServer()) { displayText("##### Server Stoped #####"); }
//			try {
//				server.close();
//				executor.shutdownNow();
//			} catch (IOException e1) {
////				e1.printStackTrace();
//			}
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
			System.out.println("### CloseBtn call stopServer()");
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
		Platform.runLater(() -> {
			startBtn.setDisable(true);
		});
		
		// ServerSocket
		try {
			server = new ServerSocket();
			server.bind(new InetSocketAddress(55566));
			server.setSoTimeout(3000);	// accept() 시간을 3초로 제한
		} catch (Exception e) {
			if(!server.isClosed()) {
				System.out.println("### startServer() call-01 stopServer()");
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
//					System.out.println("Accept() SocketTimeoutException");
					if(Thread.interrupted()) {
						System.out.println("Accept() interrupted");
						break;
					} else continue;
				} catch (IOException e) {
					System.out.println("Accept() IOException");
					break;
				} // try				
			} // while
			System.out.println("### startServer() call-02 stopServer()");
			stopServer();
		}; // runnable
		executor.submit(runnable);
		Platform.runLater(() -> {
			stopBtn.setDisable(false);
		});
		
	} // startServer() 
	
	public boolean stopServer() {
		System.out.println("stopServer()");
		try {
			for(Integer key : connections.keySet()) {
				Client client = connections.get(key);
				client.socket.close();
				connections.remove(key);
			}
			System.out.println("stopServer() connections close");
			if(server != null && !server.isClosed()) {
				server.close();
				System.out.println("stopServer() server close 1");
			}
			System.out.println("stopServer() server close 2");
			if(executor != null && !executor.isShutdown()) {
				executor.shutdown();
				do { 
					System.out.println("Pool Running.... {}" + executor.isTerminated());
					// 작업이 완료되었으면 즉시 정지 한다. 
					if (executor.isTerminated()) {
						System.out.println("Running....2 {}" + executor.isTerminated());
						executor.shutdownNow();
					}
					// 지정된 시간 별로(10초단위) 작업이 모든 작업이 중지되었는지 체크
					// 작업이 완료되었으면 루프 해제
				} while (!executor.awaitTermination(10, TimeUnit.SECONDS));
				System.out.println("stopServer() executor closed 1");
			}
			System.out.println("stopServer() executor closed 2");
		} catch (Exception e) {
			// do nothing
			System.out.println("stopServer() Exception");
			return false;
		} finally {
			Platform.runLater(() -> {
				startBtn.setDisable(false);
				stopBtn.setDisable(true);
			});
			System.out.println("stopServer() set View");
		}// try
		System.out.println("stopServer() finish");
		return true;
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
//			displayText("[" + addr + "] cleaning...");
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
			displayText("[" + addr + "] Cleaned");
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
						/*	when client socket is closed*, method readLine return null*
						 * 	and, Gson.fromJson(null, class) >> return null*					*/
//						displayText("receive : " + message);
						if(message == null) {
							throw new IOException("Client Closed");
						} else {
							Message data = gson.fromJson(message, Message.class);
							
							for(Integer key : connections.keySet()) {
								Client client = connections.get(key);
								client.send(message);
							}
						}
					} catch (IOException e) {
						displayText("[" + socket.getInetAddress() + "] socket closed at Client");
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
			
		}

}
