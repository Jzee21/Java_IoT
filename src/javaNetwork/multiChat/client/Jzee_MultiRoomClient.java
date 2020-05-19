package javaNetwork.multiChat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import javaNetwork.multiChat.ChatClient;
import javaNetwork.multiChat.ChatMessage;
import javaNetwork.multiChat.ChatRoom;
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
	
	// UI
	private Stage window;
	
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
	
//	private Map<Integer, Client> users;
//	private Map<Integer, Room> rooms;
	private Map<Integer, ChatClient> users;		// nickname, key
	private Map<Integer, Room> rooms;		// roomID, Room.class
	private Map<String, Integer> roomNames;	// roomName, roomID
	
	private Room currentRoom;
	
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
	
//	private Map<Integer, ChatClient> users;		// nickname, key
//	private Map<Integer, Room> rooms;		// roomID, Room.class
//	private Map<String, Integer> roomNames;	// roomName, roomID
	
//	public Room(ChatRoom form) {
//		this(form.getRoomID(), form.getRoomName());
//	}
	
	public void setRoomList(List<ChatRoom> list) {
		roomListView.getItems().clear();
		for(ChatRoom room : list) {
			if(!rooms.containsKey(room.getRoomID())) {
				Room newRoom = new Room(room);
				rooms.put(newRoom.getRoomID(), newRoom);
				roomNames.put(newRoom.getRoomName(), newRoom.getRoomID());
			}
			roomListView.getItems().add(room.getRoomName());
		}
	}
	
	public void setRoomPartList(List<ChatClient> list) {
		participantsListView.getItems().clear();
		if(list != null) {
			for(ChatClient part : list) {
				if(!users.containsKey(part.getUserID())) {
					users.put(part.getUserID(), part);
				}
				participantsListView.getItems().add(part.getNickname());
			}
		}
	}
	
	public void setCurrentRoom(Room room) {
		currentRoom = room;
		currentRoom.setUpdateFlag(true);
		if(currentRoom == null) {
			root.setCenter(textarea);
			window.setTitle("Multi Room Chat Client");
		} else {
			window.setTitle(room.getRoomName());
		}
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
//					int index = roomListView.getSelectionModel().getSelectedIndex();
					String item = roomListView.getSelectionModel().getSelectedItem();
					send(new ChatMessage("ENTER_ROOM", userID, roomNames.get(item)));
					Platform.runLater(() -> {
//						root.setCenter(taList.get(index));
						inputField.setEditable(true);
					});
				});
		
		// participantsList
		participantsListView = new ListView<String>();
//		participantsListView.getSelectionModel().selectedItemProperty().addListener(
//				(ObservableValue<?> arg0, Object arg1, Object arg2) -> {
//					int index = participantsListView.getSelectionModel().getSelectedIndex();
//				});
		
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
//			displayText("send nickname");
			// send nickname -- response id, roomList
			String nickname = nameField.getText();
			send(new ChatMessage("FIRST", nickname));
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
//			System.out.println("disconnBtn call stopClient()");
//			stopClient("DisconnBtn");
			stopClient();
			Platform.runLater(() -> {
				root.setBottom(namePane);
			});
		});
		
		createBtn = new Button("Create Room");
		createBtn.setPrefSize(150, 40);
		createBtn.setOnAction((e) -> {
			// Enter Room Name - Dialog
			Dialog<String> dialog = new TextInputDialog("Please Enter Room Name");
			dialog.setTitle("Room Setting");
			dialog.setHeaderText("Room Name Setting. Please Enter Room Name");
			
			Optional<String> result = dialog.showAndWait();
			String entered = "";
			if(result.isPresent()) {
				// Nickname 입력, 확인버튼
				entered = result.get();
			}
			
			send(new ChatMessage("NEW_ROOM", userID, 0, entered));
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
				setCurrentRoom(null);
			});
		});
		
		inputField = new TextField();
		inputField.setEditable(false);
		inputField.setPrefSize(500, 40);
		inputField.setOnAction((e) -> {
//			send(inputField.getText());
//			displayText(inputField.getText());

			String text = inputField.getText();
			if(text.equals("@EXIT")) {
				send(new ChatMessage("EXIT_ROOM", userID, currentRoom.getRoomID()));
			} else {
				send(new ChatMessage("MESSAGE", userID, currentRoom.getRoomID(), text));
			}
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
//			stopClient("CloseBtn");
			stopClient();
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
		users = new HashMap<Integer, ChatClient>();
		rooms = new HashMap<Integer, Room>();
		roomNames = new HashMap<String, Integer>();
		
		Runnable runnable = () -> {
			try {
				socket = new Socket();
				socket.connect(new InetSocketAddress("localhost", 55566));
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream());
				displayText("[Connected : " + socket.getRemoteSocketAddress() + "]");
				
			} catch (Exception e) {	
				if(!socket.isClosed()) { 
					stopClient();
				}
				return;
			}
			receive();
		};
		receiverPool.submit(runnable);
	} // startClient()
	
	public void stopClient() {
		try {
			displayText("test1");
			if(socket != null && !socket.isClosed()) {
				socket.close();
				if(input != null) input.close();
				if(output != null) output.close();
				displayText("[ Disconnected ]");
			}
			displayText("test2");
			if(receiverPool != null && !receiverPool.isShutdown()) {
				List<Runnable> list = receiverPool.shutdownNow();
//				receiverPool.shutdown();
//				do {
//					if(receiverPool.isTerminated()) {
//						receiverPool.shutdownNow();
//					}
//				} while (!receiverPool.awaitTermination(10, TimeUnit.SECONDS));
			}
			displayText("test3");
			if(senderPool != null && !senderPool.isShutdown()) {
				List<Runnable> list = senderPool.shutdownNow();
//				senderPool.shutdown();
//				do {
//					if(senderPool.isTerminated()) {
//						senderPool.shutdownNow();
//					}
//				} while (!senderPool.awaitTermination(10, TimeUnit.SECONDS));
			}
			displayText("test4");
		} catch (Exception e) {
			displayText("[ Disconnection Error ]");
			e.printStackTrace();
		}
		displayText("test5");
		Platform.runLater(() -> {
			connBtn.setDisable(false);
			disconnBtn.setDisable(true);
			root.setBottom(namePane);
		});
		displayText("test6");
		
	} // stopClient()
	
	// ---------------------------------------------------
	public void receive() {
		String line = "";
		while(true) {
			try {
				line = input.readLine();
				displayText(line);
				if(line == null) {
					// Server's socket closed
					throw new IOException();
				} else {
					ChatMessage data = gson.fromJson(line, ChatMessage.class);
					
					switch (data.getCode()) {
					case "FIRST" :
						// data = {"FIRST", userID, 0, [{roomID, roomName}, ...]}	// ArrayList
						userID = data.getUserID();
						ChatRoom[] firstArray = gson.fromJson(data.getStringData(), ChatRoom[].class);
//						List<ChatRoom> list = Arrays.asList(array);
						setRoomList(Arrays.asList(firstArray));
						break;
					
					case "NICKNAME":
						// data = {"NICKNAME", userID, 0, "nickname"}
						userID = data.getUserID();
						break;
						
					case "ROOMLIST":
						// data = {"ROOMLIST", userID, 0, [{roomID, roomName}, ...]}	// ArrayList
						ChatRoom[] roomList = gson.fromJson(data.getStringData(), ChatRoom[].class);
						setRoomList(Arrays.asList(roomList));
						break;
					
					case "MESSAGE":
						// data = {"MESSAGE", userID, roomID, "message"}
						String who = "";
						if(data.getUserID() == userID) {
							who = "나";
						} else {
							who = users.get(data.getUserID()).getNickname();
						}
						rooms.get(data.getDestID()).displayText("[ " + who + " ] : " + data.getStringData());
						break;
						
					case "NEW_ROOM":
						// The first message goes to else
						if (data.getDestID() == userID) {
							// When this client creates a room, it receives a second message.
							// data = {"NEW_ROOM", userID, userID, [{roomID, roomName}]}
							ChatRoom roomData = gson.fromJson(data.getStringData(), ChatRoom.class);
							setCurrentRoom(rooms.get(roomData.getRoomID()));
						} else {
							// data = {"NEW_ROOM", userID, 0, [{roomID, roomName}, ...]}
							ChatRoom[] newRoomList = gson.fromJson(data.getStringData(), ChatRoom[].class);
							setRoomList(Arrays.asList(newRoomList));
						}
						break;
						
					case "ENTER_ROOM":
						// The first message goes to else
						if (data.getUserID() == userID) {
							// when this client enter the room, it receives a second message.
							// data = {"ENTER_ROOM", userID, roomID, [{userID, nickname}, ...]}
							ChatClient[] partList = gson.fromJson(data.getStringData(), ChatClient[].class);
							setCurrentRoom(rooms.get(data.getDestID()));
							setRoomPartList(Arrays.asList(partList));
						} else {
							// data = {"ENTER_ROOM", userID, roomID, {userID, nickname}}
							ChatClient newPart = gson.fromJson(data.getStringData(), ChatClient.class);
							rooms.get(data.getDestID()).addToList(newPart.getUserID());
							rooms.get(data.getDestID()).displayText("[ " + users.get(data.getUserID()) + " ] 님이 입장하셨습니다.");
						}
						break;
						
					case "EXIT_ROOM":
						// data = {"ENTER_ROOM", userID, roomID, {userID, nickname}}
						if (data.getUserID() == userID) {
							setCurrentRoom(null);
							setRoomPartList(null);
						} else {
							ChatClient newPart = gson.fromJson(data.getStringData(), ChatClient.class);
							rooms.get(data.getDestID()).removeAtList(newPart.getUserID());
							setRoomPartList(rooms.get(data.getDestID()).getPartList());
							rooms.get(data.getDestID()).displayText("[ " + users.get(data.getUserID()) + " ] : " + data.getStringData());
						}
						break;

					default:
						break;
					}
				}
			} catch (IOException e) {
				stopClient();
				break;
			}
		}
	} // receive()
	
	public void send(String message) {
		
		if(message != null && !message.equals(""))
			send(new ChatMessage("MESSAGE", userID, message));

	} // send(String message)
	
	public void send(ChatMessage message) {
		Runnable runnable = () -> {
			try {
				String jsonMsg = gson.toJson(message);
				output.println(jsonMsg);
				output.flush();
			} catch (Exception e) {
				displayText("send Error");
				stopClient();
			}
		};
		senderPool.submit(runnable);
	} // send(Message message)
	
	

	// =================================================================
	class Client extends ChatClient {
		
		public Client(int userID, String nickname) {
			this.userID = userID;
			this.nickname = nickname;
		}
		
		public Client(ChatClient form) {
			this.userID = form.getUserID();
			this.nickname = form.getNickname();
		}

		
	} // Client
	
	
	// =================================================================
	class Room extends ChatRoom {
		private TextArea textArea;
		private boolean updateFlag;
		private List<Integer> list;		// participants(id) list
		
		// constructor
		public Room(int roomID, String roomName) {
			this.roomID = roomID;
			this.roomName = roomName;
			this.textArea = new TextArea();
			this.updateFlag = false;
			this.list = new ArrayList<Integer>();
		}
		
		public Room(ChatRoom form) {
			this(form.getRoomID(), form.getRoomName());
		}
		
		// getter - setter
		public TextArea getTextArea() {
			return textArea;
		}

		public boolean isUpdateFlag() {
			return updateFlag;
		}

		public void setUpdateFlag(boolean updateFlag) {
			this.updateFlag = updateFlag;
		}

		public List<Integer> getList() {
			return list;
		}

		public void setList(List<Integer> list) {
			this.list = list;
		}
		
		//
		public void addToList(int userID) {
			this.list.add(userID);
		}
		
		public void removeAtList(int userID) {
			this.list.remove(userID);
		}
		
		public List<ChatClient> getPartList() {
			List<ChatClient> list = new ArrayList<ChatClient>();
			for (int key : this.list) {
				list.add(users.get(key));
			}
			return list;
		}
		
		public List<String> getPartNames() {
			List<String> list = new ArrayList<String>();
			for (int key : this.list) {
				list.add(users.get(key).getNickname());
			}
			return list;
		}
		
		public void displayText(String text) {
			if(this.updateFlag) this.textArea.appendText(text);
		}
		
	} // Room
	
}