package javaNetwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class EX02_EchoClient extends Application {
	
	private TextArea ta;
	private Button connBtn;
	private TextField tf;
	
	private Socket socket;
	private BufferedReader br;
	private PrintWriter pr;
	
	private Thread receiver;

	private void printMsg(String msg) {
		Platform.runLater(() -> {
			ta.appendText(msg + "\n");
		});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		System.out.println(Thread.currentThread().getName());
		// JavaFX Application Thread

		BorderPane root = new BorderPane();
		root.setPrefSize(700, 500);
		
		ta = new TextArea();
		root.setCenter(ta);
		
		//		
		connBtn = new Button("Server Conn");
		connBtn.setPrefSize(250, 50);
		connBtn.setOnAction((e) -> {
			ta.clear();
			
			Runnable runnable = () -> {
				try {
					socket = new Socket("localhost", 55556);
					br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					pr = new PrintWriter(socket.getOutputStream());
					
					String msg = br.readLine();
					if(msg.equals("Error")) {
						printMsg("현재 접속자가 많아 서비스 이용이 어렵습니다.\n잠시 후 다시 이용해주세요.");					
					} else {					
						System.out.println("[" + socket.getInetAddress() + "] connected");
						tf.setDisable(false);
						connBtn.setDisable(true);
					}
					
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			};
			receiver = new Thread(runnable);
			receiver.start();
			
		});
		
		tf = new TextField();
		tf.setPrefSize(400, 50);
		tf.setDisable(false);
		tf.setOnAction((e) -> {
//			if (((KeyEvent)e).getCode().equals(KeyCode.ENTER)) {
//				
//			}
			String msg = tf.getText();
			pr.println(msg);
			pr.flush();
			tf.clear();
			
			if(!msg.equals("@EXIT")) {
				try {
					String revString = br.readLine();
					printMsg(msg);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else {
				printMsg("[Server Disconnected]");
				tf.setDisable(true);
				connBtn.setDisable(false);
			}
		});
		
		FlowPane flowpane = new FlowPane();
		flowpane.setPrefSize(700, 50);
		//flowpane.getChildren().add(startBtn);
		flowpane.getChildren().addAll(connBtn, tf);
		
		root.setBottom(flowpane);
		
		//
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Date Client");
		primaryStage.setOnCloseRequest(e -> {
			// System.exit(0);
			receiver.interrupt();
		});
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch();
	}
	
}