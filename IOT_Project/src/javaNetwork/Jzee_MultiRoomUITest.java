package javaNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Jzee_MultiRoomUITest extends Application{
	
	private String userID;
	
	private TextArea textarea;
	
	private List<TextArea> taList;
	
	private Button connBtn, disconnBtn;
	private Button createRoomBtn;
	private Button connBoomBtn;
	
	private ListView<String> roomListView;
	private ListView<String> participantsListView;		// 채팅방 참여목록
	
	
	public void printMsg(String msg) {
		Platform.runLater(() -> {
			textarea.appendText(msg + "\n");
		});
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		BorderPane root = new BorderPane();
		root.setPrefSize(700, 500);
		
		// Center
		textarea = new TextArea();
		textarea.setEditable(false);
		root.setCenter(textarea);
		
		taList = new ArrayList<TextArea>();
		taList.add(new TextArea("1"));
		taList.add(new TextArea("2"));
		taList.add(new TextArea("3"));
		
		// Right
		roomListView = new ListView<String>();
		roomListView.getSelectionModel().selectedItemProperty().addListener(
				(ObservableValue<?> arg0, Object arg1, Object arg2) -> {
					int index = roomListView.getSelectionModel().getSelectedIndex();
					root.setCenter(taList.get(index));
				});
		
		
		participantsListView = new ListView<String>();
		
		GridPane gridpane = new GridPane();
		gridpane.setPadding(new Insets(5,5,5,5));
		gridpane.setVgap(10);
		gridpane.add(roomListView, 0, 0);
		gridpane.add(participantsListView, 0, 1);	// (column, row)
		
		root.setRight(gridpane);
		
		// Bottom
		connBtn = new Button("Conn");
		connBtn.setPrefSize(150, 40);
		connBtn.setOnAction((e) -> {
			// Enter ID(Nickname) - Dialog
			Dialog<String> dialog = new TextInputDialog("Please Enter Your Nickname");
			dialog.setTitle("Chat Setting");
			dialog.setHeaderText("Nickname Setting. Please Enter Your Nickname");
			
			Optional<String> result = dialog.showAndWait();
			String entered = "";
			if(result.isPresent()) {
				// Nickname 입력, 확인버튼
				entered = result.get();
			}
			
			// Room List from Server
			//
			
			// Set List
			roomListView.getItems().add("서울, 경기 등산모임");
			roomListView.getItems().add("기사시험 공부방");
			roomListView.getItems().add("잉어킹");
			printMsg("Chat Server Connected!");
			printMsg("Welcome, " + entered + " !");
			
		});
		
		disconnBtn = new Button("Disconn");
		disconnBtn.setPrefSize(150, 40);
		disconnBtn.setOnAction((e) -> {
			
		});
		
		createRoomBtn = new Button();
		createRoomBtn.setPrefSize(150,  40);
		createRoomBtn.setOnAction((e) -> {
			// New Chat Room
			Dialog<String> dialog = new TextInputDialog("Please Enter Room Name");
			dialog.setTitle("Room Setting");
			dialog.setHeaderText("Room Setting. Please Enter Room Name");
			
			Optional<String> result = dialog.showAndWait();
			String entered = "";
			if(result.isPresent()) {
				entered = result.get();
			}
			
			roomListView.getItems().add(entered);
			printMsg("Room " + entered + " is added");
			
		});
		
		connBoomBtn = new Button("Room Connect");
		connBoomBtn.setPrefSize(150,  40);
		connBoomBtn.setOnAction((e) -> {
			// Open Stream
			String roomName = roomListView.getSelectionModel().getSelectedItem();
			// server conn
			
			// participants
			participantsListView.getItems().add("");
			participantsListView.getItems().add("");
			participantsListView.getItems().add("");
			
			//
			printMsg("Enter Room " + roomName);
			
			// 화면 하단 레이아웃 전환
			FlowPane inputFlowPane = new FlowPane();
			inputFlowPane.setPrefSize(700, 40);
			inputFlowPane.setPadding(new Insets(5,5,5,5));
			inputFlowPane.setHgap(10);
			
			TextField inputTF = new TextField();
			inputTF.setPrefSize(500, 40);
			
			inputFlowPane.getChildren().addAll(inputTF);
			
			root.setBottom(inputFlowPane);
			
		});
		
		FlowPane menuFlowPane = new FlowPane();
		menuFlowPane.setPrefSize(700, 40);
		menuFlowPane.setPadding(new Insets(5,5,5,5));
		menuFlowPane.setHgap(10);
		menuFlowPane.getChildren().addAll(connBtn, disconnBtn, connBoomBtn);
		root.setBottom(menuFlowPane);
		
		
		//
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Multi Room Chat Client");
		primaryStage.setOnCloseRequest((e) -> {
			//
		});
		primaryStage.show();
		
	} // start(Stage primaryStage)

	public static void main(String[] args) {
		launch();
	}

}
