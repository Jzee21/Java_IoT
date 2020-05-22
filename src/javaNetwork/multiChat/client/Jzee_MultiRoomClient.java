package javaNetwork.multiChat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javaNetwork.multiChat.ChatMessage;
import javaNetwork.multiChat.client.vo.ChatRoom;
import javafx.application.Application;
import javafx.application.Platform;
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

public class Jzee_MultiRoomClient extends Application {
	
	// UI
	private Stage window; 
	
	private BorderPane root;
	private FlowPane namePane, menuPane, inputPane;
	
	private TextField nameField, inputField;
	private Button connBtn, disconnBtn, createBtn, menuBtn;
	private Label nameLabel;
	
	private ListView<String> roomListView;				// 채팅방 목록
	private ListView<String> participantsListView;		// 채팅방 참여목록
	
	// User
	private String nickname;
	private TextArea textarea;
	
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;
	private ExecutorService receiverPool;
	
	private Map<String, ChatRoom> roomList;
	private String currentRoom = "DEFAULT";
	
	private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
	
	
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
	
	
//	private void setChatTitle(String title) {
//		Platform.runLater(() -> {
//			if(title.equals("DEFAULT")) {
//				this.window.setTitle("Jzee\'s Multi Chatting");
//			} else {
//				this.window.setTitle(title);
//			}
//		});
//	}
	
	public void setRoomList(List<String> list) {
		Platform.runLater(() -> {
			roomListView.getItems().clear();
			for(String roomName : list) {
//				if(!roomList.containsKey(room.getRoomName())) {
//					Room newRoom = new Room(room);
//					rooms.put(newRoom.getRoomID(), newRoom);
//					roomNames.put(newRoom.getRoomName(), newRoom.getRoomID());
//				}
				roomListView.getItems().add(roomName);
			}
		});
	}
	
	public void setRoomPartList(List<String> list) {
		Platform.runLater(() -> {
			participantsListView.getItems().clear();
			if(list != null) {
				for(String part : list) {
					participantsListView.getItems().add(part);
				}
			}
		});
	}
	
	public void setCurrentRoom(ChatRoom currRoom) {
		if(currRoom == null) {
			Platform.runLater(() -> {
				this.window.setTitle("Jzee\'s Multi Chatting");
				root.setCenter(textarea);
				this.roomListView.getSelectionModel().clearSelection();
				this.setRoomPartList(null);
				root.setBottom(menuPane);
				this.currentRoom = "DEFAULT";
			});
		} else {
			Platform.runLater(() -> {
				this.window.setTitle(currRoom.getRoomName());
				root.setCenter(currRoom.getTextarea());
				this.setRoomPartList(currRoom.getParticipants());
				root.setBottom(inputPane);
				this.currentRoom = currRoom.getRoomName();
			});
		}
	}
	
	public void enterRoom(String roomName) {
		if(this.roomList.containsKey(roomName.toUpperCase())) {
			displayText("방 있다");
			this.setCurrentRoom(this.roomList.get(roomName.toUpperCase()));
		} else {
			displayText("방 없다");
			send(new ChatMessage("ENTER_ROOM", this.nickname, roomName, null));
			displayText("방 만들어줘");
		}
	}
	
	public void addEnterRoom(ChatRoom enterRoom) {
		this.roomList.put(enterRoom.getRoomName().toUpperCase(), enterRoom);
		this.setCurrentRoom(enterRoom);
	}
	
	public void removeEnterRoom(String roomName) {
		this.roomList.remove(roomName.toUpperCase());
		this.setCurrentRoom(null);
	}
	
	
	// =================================================================
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		this.window = primaryStage;
		
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
//					displayText("Clicked : " + item);
					if(item != null) {
						this.enterRoom(item);
						Platform.runLater(() -> {
							inputField.setEditable(true);
						});
					}
				});
		
		// participantsList
		participantsListView = new ListView<String>();
		participantsListView.setEditable(false);
		participantsListView.getSelectionModel().selectedItemProperty().addListener(
				(ObservableValue<?> arg0, Object arg1, Object arg2) -> {
//					int index = participantsListView.getSelectionModel().getSelectedIndex();
					participantsListView.getSelectionModel().clearSelection();
				});
		
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
		
		
		// namePane --------------------
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
			this.nickname = nameField.getText();
			startClient(nickname);
			root.setBottom(menuPane);
		});
		
		setBottomPane(namePane);
		namePane.getChildren().addAll(nameLabel, nameField, connBtn);
		namePane.setAlignment(Pos.CENTER);
		root.setBottom(namePane);
		
		// menuPane --------------------
		disconnBtn = new Button("Disconn");
		disconnBtn.setPrefSize(150, 40);
		disconnBtn.setOnAction((e) -> {
			stopClient();
			root.setBottom(namePane);
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
			
			send(new ChatMessage("NEW_ROOM", nickname, entered, null));
		});
		
		setBottomPane(menuPane);
		menuPane.getChildren().addAll(disconnBtn, createBtn);
		
		// inputPane --------------------
		menuBtn = new Button("Menu");
		menuBtn.setPrefSize(150, 40);
		menuBtn.setOnAction((e) -> {
			this.setCurrentRoom(null);
//			root.setBottom(menuPane);
//			participantsListView.getSelectionModel().clearSelection();
		});
		
		inputField = new TextField();
		inputField.setEditable(false);
		inputField.setPrefSize(500, 40);
		inputField.setOnAction((e) -> {
			String text = inputField.getText();
			if(text.equals("@EXIT")) {
//				send(new ChatMessage("EXIT_ROOM	", userID, currentRoom.getRoomID()));
			} else {
				send(new ChatMessage("MESSAGE", nickname, currentRoom, text));
			}
			
			inputField.clear();
		});
		
		setBottomPane(inputPane);
		inputPane.getChildren().addAll(menuBtn, inputField);
		
		
		
		// Scene ------------------------------------------------
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Jzee\\'s Multi Chatting");
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
	public void startClient(String nickname) {
		
		connBtn.setDisable(true);
		disconnBtn.setDisable(false);
		receiverPool = Executors.newFixedThreadPool(1);
		roomList = new HashMap<String, ChatRoom>();
//		users = new HashMap<String, ChatClient>();
//		rooms = new HashMap<String, Room>();
//		roomNames = new HashMap<String, Integer>();
		
		Runnable runnable = () -> {
			try {
				socket = new Socket();
				socket.connect(new InetSocketAddress("localhost", 55566));
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream());
				
				output.println(nickname);
				output.flush();
				
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
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		send(new ChatMessage("ROOMLIST", nickname, null, null));
	} // startClient()
	
	public void stopClient() {
		try {
			if(socket != null && !socket.isClosed()) {
				socket.close();
				if(input != null) input.close();
				if(output != null) output.close();
				displayText("[ Disconnected ]");
			}
			if(receiverPool != null && !receiverPool.isShutdown()) {
				List<Runnable> list = receiverPool.shutdownNow();
			}
		} catch (Exception e) {
			displayText("[ Disconnection Error ]");
//			e.printStackTrace();
		}
		Platform.runLater(() -> {
			connBtn.setDisable(false);
			disconnBtn.setDisable(true);
			root.setBottom(namePane);
		});
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
					case "MESSAGE" :
						// data ["MESSAGE", "nickname", "roomName", "message"]
						ChatRoom eventRoom = roomList.get(data.getDestID().toUpperCase());
						if(data.getUserID().equals(this.nickname)) {
							displayText(eventRoom.getTextarea(), "[  나  ] " + data.getStringData());
						} else {
							displayText(eventRoom.getTextarea(), "[ " + data.getUserID() + " ] " + data.getStringData());
						}
						break;
						
					case "ROOMLIST":
						// data ["ROOMLIST", "SERVER", "my_nickname", newRoom.class]	// When i asked
						// data ["ROOMLIST", "SERVER", "roomName", newRoom.class]		// When a new room is created
						List<String> roomNameList = Arrays.asList(gson.fromJson(data.getStringData(), String[].class));
						setRoomList(roomNameList);
						break;
						
					case "NEW_ROOM":
						// data ["NEW_ROOM", "SERVER", "nickname", "newRoom.class"]
						if(data.getDestID().equals(this.nickname)) {
							ChatRoom newRoom = gson.fromJson(data.getStringData(), ChatRoom.class);
							if(!roomList.containsKey(newRoom.getRoomName().toUpperCase())) {
								newRoom.setTextarea(new TextArea());
								roomList.put(newRoom.getRoomName().toUpperCase(), newRoom);
								this.addEnterRoom(newRoom);
							}
						}
						break;
						
					case "ENTER_ROOM":
						// data ["ENTER_ROOM", "nickname", "roomName", ChatRoom.class]
						if(data.getUserID().equals(this.nickname)) {
							displayText("방 왔다");
							ChatRoom enterRoom = null;
							if(roomList.containsKey(data.getDestID().toUpperCase())) {
								displayText("방 왜있냐");
								enterRoom = roomList.get(data.getDestID().toUpperCase());
								this.setCurrentRoom(enterRoom);
							} else {
								displayText("방 없다고");
								enterRoom = gson.fromJson(data.getStringData(), ChatRoom.class);
								enterRoom.setTextarea(new TextArea());
								this.addEnterRoom(enterRoom);
							}
						}
						break;
						
					case "UPDATE_PART":
						// data ["UPDATE_PART", "SERVER", "roomName", partList]
						ChatRoom updatedRoom = this.roomList.get(data.getDestID().toUpperCase());
						List<String> updatePartList = Arrays.asList(gson.fromJson(data.getStringData(), String[].class));
						updatedRoom.setParticipants(updatePartList);
						
						if(currentRoom.equals(data.getDestID())) {
							setRoomPartList(updatePartList);
						}
						break;
						
					default:
						break;
					}
					
//					ChatMessage data = gson.fromJson(line, ChatMessage.class);
//					displayText("? ]" + data.getStringData());
					
//					switch (data.getCode()) {
//					case "FIRST" :
//						// data = {"FIRST", userID, 0, [{roomID, roomName}, ...]}	// ArrayList
////						userID = data.getUserID();
//						ChatRoom[] firstArray = gson.fromJson(data.getStringData(), ChatRoom[].class);
////						List<ChatRoom> list = Arrays.asList(array);
//						setRoomList(Arrays.asList(firstArray));
//						break;
//					
//					case "NICKNAME":
//						// data = {"NICKNAME", userID, 0, "nickname"}
////						userID = data.getUserID();
//						break;
//						
//					case "ROOMLIST":
//						// data = {"ROOMLIST", userID, 0, [{roomID, roomName}, ...]}	// ArrayList
//						ChatRoom[] roomList = gson.fromJson(data.getStringData(), ChatRoom[].class);
//						setRoomList(Arrays.asList(roomList));
//						break;
//					
//					case "MESSAGE":
//						// data = {"MESSAGE", userID, roomID, "message"}
//						String who = "";
//						if(data.getUserID() == userID) {
//							who = "나";
//						} else {
//							who = users.get(data.getUserID()).getNickname();
//						}
//						rooms.get(data.getDestID()).displayText("[ " + who + " ] : " + data.getStringData());
//						break;
//						
//					case "NEW_ROOM":
//						// The first message goes to else
//						if (data.getDestID() == userID) {
//							// When this client creates a room, it receives a second message.
//							// data = {"NEW_ROOM", userID, userID, [{roomID, roomName}]}
//							ChatRoom roomData = gson.fromJson(data.getStringData(), ChatRoom.class);
//							setCurrentRoom(rooms.get(roomData.getRoomID()));
//						} else {
//							// data = {"NEW_ROOM", userID, 0, [{roomID, roomName}, ...]}
//							ChatRoom[] newRoomList = gson.fromJson(data.getStringData(), ChatRoom[].class);
//							setRoomList(Arrays.asList(newRoomList));
//						}
//						break;
//						
//					case "ENTER_ROOM":
//						// The first message goes to else
//						if (data.getUserID() == userID) {
//							// when this client enter the room, it receives a second message.
//							// data = {"ENTER_ROOM", userID, roomID, [{userID, nickname}, ...]}
//							ChatClient[] partList = gson.fromJson(data.getStringData(), ChatClient[].class);
//							setCurrentRoom(rooms.get(data.getDestID()));
//							setRoomPartList(Arrays.asList(partList));
//						} else {
//							// data = {"ENTER_ROOM", userID, roomID, {userID, nickname}}
//							ChatClient newPart = gson.fromJson(data.getStringData(), ChatClient.class);
//							rooms.get(data.getDestID()).addToList(newPart.getUserID());
//							rooms.get(data.getDestID()).displayText("[ " + users.get(data.getUserID()) + " ] 님이 입장하셨습니다.");
//						}
//						break;
//						
//					case "EXIT_ROOM":
//						// data = {"ENTER_ROOM", userID, roomID, {userID, nickname}}
//						if (data.getUserID() == userID) {
//							setCurrentRoom(null);
//							setRoomPartList(null);
//						} else {
//							ChatClient newPart = gson.fromJson(data.getStringData(), ChatClient.class);
//							rooms.get(data.getDestID()).removeAtList(newPart.getUserID());
//							setRoomPartList(rooms.get(data.getDestID()).getPartList());
//							rooms.get(data.getDestID()).displayText("[ " + users.get(data.getUserID()) + " ] : " + data.getStringData());
//						}
//						break;
//
//					default:
//						break;
//					}
				}
			} catch (IOException e) {
				stopClient();
				break;
			}
		}
	} // receive()
	
	public void send(String msg) {
		if(this.socket != null && !socket.isClosed()) {
			output.println(msg);
			output.flush();
		}
	}
	
	public void send(ChatMessage message) {
		this.send(gson.toJson(message));
	} // send(Message message)
	
}