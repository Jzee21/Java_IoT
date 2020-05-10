package javaNetwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import javaNetwork.Jzee_MultiRoomServer.Client;
import javaNetwork.Jzee_MultiRoomServer.Room;
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

public class Jzee_ChatServer extends Application{
	
	private TextArea textarea;
	private Button startBtn, stopBtn;
	
	private ServerInfo serverInfo;
	
	private ServerSocket server;
	private ExecutorService executor;

	
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
		
		serverInfo = new ServerInfo(textarea);
		
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
			stopServer();
		});
		primaryStage.show();
		
	}
	
	
	
	// =================================================================
	public void startServer() {
		executor = Executors.newCachedThreadPool();
		Platform.runLater(() -> {
			startBtn.setDisable(true);
		});
		
		// Server open
		try {
			server = new ServerSocket();
			server.bind(new InetSocketAddress(55566));
			server.setSoTimeout(3000);	// accept() time limit (3s)
		} catch (Exception e) {
			// Server open fail.
			if(!server.isClosed()) {
				stopServer();
			}
			// startBtn reset for usable
			Platform.runLater(() -> {
				startBtn.setDisable(false);
			});
			return;		// Skip - runnable
		}
		
		// Server open success
		Runnable runnable = () -> {
			Socket socket = null;
			while(true) {
				displayText("Connection Waiting...");
				try {
					socket = server.accept();
					
					RunnableClient client = new RunnableClient(socket, serverInfo);
					serverInfo.addUser(client);
					executor.submit(client);
				} catch (SocketTimeoutException e) {	
					if(Thread.interrupted()) {
						break;
					} else continue;
				} catch (IOException e) {
					break;
				}
			}
			stopServer();
		};
		executor.submit(runnable);
	}
	
	public void stopServer() {
		try {
			serverInfo.displayText("[The server is shutting down...]");
			// close - connected all user's socket
			for(Integer key : serverInfo.users.keySet()) {
				RunnableClient client = serverInfo.users.get(key);
				client.close();
				serverInfo.removeUser(key);
			}
			
			// close - ServerSocket
			if(server != null && !server.isClosed()) {
				server.close();
			}
			
			// close - ExecutorService
			if(executor != null && !executor.isShutdown()) {
				executor.shutdown();
				do {
					if (executor.isTerminated()) {
						List<Runnable> list = executor.shutdownNow();
						serverInfo.displayText("\t" + list.size() + " jobs running...");
					}
				} while (!executor.awaitTermination(10, TimeUnit.SECONDS));
			}
		} catch (Exception e) {
			serverInfo.displayText("[Server shutdown failed.]");
		}
		serverInfo.displayText("[The server has been shut down.]");
		Platform.runLater(() -> {
			startBtn.setDisable(false);
			stopBtn.setDisable(true);
		});
	}
	
	
	
	// =================================================================
	public static void main(String[] args) {
		launch();
	}
	
}



// =================================================================
class ServerInfo {
	
	Map<Integer, RunnableClient> users = new ConcurrentHashMap<Integer, RunnableClient>();
//		Map<Integer, Room> rooms = new ConcurrentHashMap<Integer, Room>();
	private TextArea textarea;
	private Gson gson = new Gson();
	
	public ServerInfo(TextArea textarea) {
		this.textarea = textarea;
	}
	
	//
	public void displayText(String msg) {
		Platform.runLater(() -> {
			this.textarea.appendText(msg + "\n");
		});
	}
	
	//
	public void addUser(RunnableClient client) {
		users.put(client.getUserID(), client);
	}
	
	public void removeUser(int userID) {
		users.remove(userID);
	}
	
	public void broadcast(String message) {
		for(Integer key : users.keySet()) {
			users.get(key).getOutput().println(message);
			users.get(key).getOutput().flush();
		}
	}
	
	public void broadcast(ChatMessage message) {
		this.broadcast(gson.toJson(message));
	}
	
}



// =================================================================
class RunnableClient implements Runnable {
	
	private Socket socket;
	private ServerInfo info;
	
	private int userID;
	private String nickname;
	
	private BufferedReader input;
	private PrintWriter output;
	
	private Gson gson = new Gson();
	
	public RunnableClient (Socket socket, ServerInfo info) {
		this.socket = socket;
		this.info = info;
		try {
			this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.output = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public int getUserID() {
		return userID;
	}
	
	public PrintWriter getOutput() {
		return output;
	}
	
	public void close() {
		String addr = socket.getInetAddress().toString();
		try {
			if(socket != null && !socket.isClosed()) {
				socket.close();
				if(input != null)	input.close();
				if(output != null)	output.close();
			}
			info.removeUser(this.userID);
		} catch (IOException e) {
			e.printStackTrace();
		} // try
		info.displayText("[" + addr + "] Closed");
	}
	
	@Override
	public void run() {
		info.displayText("[" + socket.getInetAddress() + "] Client Connected");
		
		String line = "";
		try {
			while((line = input.readLine()) != null) {
				ChatMessage data = gson.fromJson(line, ChatMessage.class);
				
				switch (data.getCode()) {
				case "MESSAGE":
					info.broadcast(line);
					break;

				default:
					break;
				}
			}
			this.close();
		} catch (IOException e) {
			e.printStackTrace();
			this.close();
		}
	}
}



//=================================================================
class Message {
	private String code;
	private int userID;
	private int destID;		// destination room id
	private String jsonData;
	
	// constructor
	public Message(String code) {
		this.code = code;
	}
	
	public Message(String code, int userID) {
		this.code = code;
		this.userID = userID;
	}
	
	public Message(String code, String jsonData) {
		this.code = code;
		this.jsonData = jsonData;
	}
	
	public Message(String code, int userID, String jsonData) {
		this.code = code;
		this.userID = userID;
		this.jsonData = jsonData;
	}
	
	public Message(String code, int userID, int destID, String jsonData) {
		this(code, userID, jsonData);
		this.destID = destID;
	}

	// getter - setter
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getDestID() {
		return destID;
	}

	public void setDestID(int destID) {
		this.destID = destID;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	@Override
	public String toString() {
		return "Message [code=" + code + ", userID=" + userID + ", destID=" + destID + ", jsonData=" + jsonData
				+ "]";
	}
	
} // class Message