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

import javaNetwork.multiChat.ChatClient;
import javaNetwork.multiChat.ChatMessage;
import javaNetwork.multiChat.ChatRoom;
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
	
	private Map<Integer, Room> chatrooms = new ConcurrentHashMap<Integer, Room>();
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
//			stopServer();
			if(stopServer()) { displayText("##### Server Stoped #####"); }
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
					socket = server.accept();
					displayText("[" + socket.getInetAddress() + "] Client Connected");
					Client client = new Client(socket);
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
		Platform.runLater(() -> {
			stopBtn.setDisable(false);
		});
		
	} // startServer() 
	
	public boolean stopServer() {
		try {
			for(Integer key : connections.keySet()) {
				Client client = connections.get(key);
				client.closeSocket();
				connections.remove(key);
			}
			if(server != null && !server.isClosed()) {
				server.close();
			}
			if(executor != null && !executor.isShutdown()) {
				executor.shutdown();
				do { 
					// 작업이 완료되었으면 즉시 정지 한다. 
					if (executor.isTerminated()) {
						executor.shutdownNow();
					}
					// 지정된 시간 별로(10초단위) 작업이 모든 작업이 중지되었는지 체크
					// 작업이 완료되었으면 루프 해제
				} while (!executor.awaitTermination(10, TimeUnit.SECONDS));
			}
		} catch (Exception e) {
			return false;
		} finally {
			Platform.runLater(() -> {
				startBtn.setDisable(false);
				stopBtn.setDisable(true);
			});
		}// try
		return true;
	} // stopServer()
	
	public List<ChatRoom> getRoomList() {

		List<ChatRoom> list = new ArrayList<ChatRoom>();
		
		for(int key : chatrooms.keySet()) {
			ChatRoom room = chatrooms.get(key);
			list.add(room);
		}
		
		return list;
		
	}
	
	public List<ChatClient> getRoomPartList(int roomID) {
		
		List<ChatClient> list = new ArrayList<ChatClient>();
		
		Room room = chatrooms.get(roomID);
		for(int key : room.getList()) {
			ChatClient part = connections.get(key);
			list.add(part);
		}
		
		return list;
	}
	
	
	// =================================================================
	class Client extends ChatClient {
//		int userID;
//		String nickname;
		private Socket socket;
		private BufferedReader input;
		private PrintWriter output;
		List<Integer> roomlist;

		
		public Client(Socket socket) {
			this.socket = socket;
			this.userID = this.hashCode();
			connections.put(this.userID, this);
			receive();
		}

		
		public void closeSocket() {
			String addr = socket.getInetAddress().toString();
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
		private void receive() {
			Runnable runnable = () -> {
				// set Stream
				try {
					this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
					this.output = new PrintWriter(socket.getOutputStream());
				} catch (IOException e) {
					displayText("Stream Create Error");
//					e.printStackTrace();
					this.closeSocket();
				} // try

				// set nickname
//				try {
//					String nickname = input.readLine();
//					this.nickname = nickname;
//					displayText("[" + socket.getInetAddress() + "] id : " + this.userID);
//					displayText("[" + socket.getInetAddress() + "] nickname : " + this.nickname);
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
				
				// send Room list
				// 
					
				String line = "";
				while(true) {
					try {
						line = input.readLine();
						displayText(line);
						/*	when client socket is closed*, method readLine return null*
						 * 	and, Gson.fromJson(null, class) >> return null*					*/
//						displayText("receive : " + message);
						if(line == null) {
							throw new IOException("Client Closed");
						} else {
							ChatMessage data = gson.fromJson(line, ChatMessage.class);
							displayText("[" + socket.getInetAddress() + "] : " + data.getCode());
							
							Room targetRoom = null;
							switch (data.getCode()) {
							case "FIRST" :
								displayText("[" + socket.getInetAddress() + "] : " + data.getCode());
								// data = {"FIRST", 0, 0, "nickname"}	// set first
								this.nickname = data.getStringData();
								data.setUserID(this.userID);
								data.setStringData(gson.toJson(getRoomList()));
								send(data);
								break;
							
							case "NICKNAME":
								displayText("[" + socket.getInetAddress() + "] : " + data.getCode());
								// data = {"NICKNAME", userID, 0, "nickname"}	// rename
								this.nickname = data.getStringData();
								send(data);
								break;
								
							case "ROOMLIST":
								displayText("[" + socket.getInetAddress() + "] : " + data.getCode());
								// data = {"ROOMLIST", userID, 0, null}
								data.setStringData(gson.toJson(getRoomList()));
								send(data);
								break;
								
							case "MESSAGE":
								displayText("[" + socket.getInetAddress() + "] : " + data.getCode());
								// data = {"MESSAGE", userID, roomID, "message"}
								// broadcast(by roomID)
								targetRoom = chatrooms.get(data.getDestID());
								for(int key : targetRoom.getList()) {
									Client client = connections.get(key);
									client.send(line);
								}
//								for(Integer key : connections.keySet()) {
//									Client client = connections.get(key);
//									client.send(line);
//								}
								break;
								
							case "NEW_ROOM":
								displayText("[" + socket.getInetAddress() + "] : " + data.getCode());
								// data = {"NEW_ROOM", userID, 0, "roomName"}
								// new Room
								targetRoom = new Room(data.getStringData());
								// add this client
								targetRoom.addPart(data.getUserID());
								chatrooms.put(targetRoom.getRoomID(), targetRoom);
								
								// broadcast(all connections)
								// all roomList
								data.setStringData(gson.toJson(getRoomList()));
								for(Integer key : connections.keySet()) {
									Client client = connections.get(key);
									client.send(data);
								}
								
								// Send a second message to the client who created the room
								ChatRoom roomInfo = targetRoom;
								data.setDestID(data.getUserID());
								data.setStringData(gson.toJson(roomInfo));
								send(data);
								break;
							
							case "ENTER_ROOM":
								displayText("[" + socket.getInetAddress() + "] : " + data.getCode());
								// data = {"ENTER_ROOM", userID, roomID, null}
								// broadcast(by roomID)
								targetRoom = chatrooms.get(data.getDestID());
								data.setStringData(gson.toJson(new ChatClient(this)));	// Newly joined clients info
								for(int key : targetRoom.getList()) {
									Client client = connections.get(key);
									client.send(data);
								}
								
								// Send a second message to the client who created the room
								data.setStringData(gson.toJson(getRoomPartList(data.getDestID())));
								send(data);
								break;
								
							case "EXIT_ROOM":
								displayText("[" + socket.getInetAddress() + "] : " + data.getCode());
								// data = {"EXIT_ROOM", userID, roomID, null}
								// broadcast(by roomID)
								targetRoom = chatrooms.get(data.getDestID());
								data.setStringData(gson.toJson(new ChatClient(this)));
								for(int key : targetRoom.getList()) {
									Client client = connections.get(key);
									client.send(data);
								}
								break;
								
							default:
								break;
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
		
		public void send(String message) {
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
		
		public void send(ChatMessage message) {
			Runnable runnable = () -> {
				String jsonMsg = gson.toJson(message);
				output.println(jsonMsg);
				output.flush();
			};
			executor.submit(runnable);
		}
		
	}
	
	
	
	// =================================================================
	class Room extends ChatRoom {
//		int roomID;
//		String roomName;
		private List<Integer> list;
		
		Room(String roomName) {
			this.roomName = roomName;
			this.roomID = this.hashCode();
		}


		public List<Integer> getList() {
			return list;
		}

		public void setList(List<Integer> list) {
			this.list = list;
		}
		
		public void addPart(int clientID) {
			this.list.add(clientID);
		}
		
		public void deletePart(int clientID) {
			this.list.remove(clientID);
		}
		
	}

}
