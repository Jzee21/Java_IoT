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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class Jzee_EchoClient extends Application {
	
	private TextArea ta;
	private TextField tf;
	private Button sendBtn;
	
	private Socket socket;
	private BufferedReader br;
	private PrintWriter pr;
	
	private void getConn() {
		if(socket == null) {
			try {
				socket = new Socket("localhost", 55556);
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				pr = new PrintWriter(socket.getOutputStream());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void echoMsg() {
		try {
			this.getConn();
			
			String msg = tf.getText();
			pr.println(msg);
			pr.flush();
			tf.clear();
			
			if(!msg.toUpperCase().equals("@EXIT"))
				printMsg(br.readLine());
			else
				printMsg("Server disconnected");
			
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

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
		
		tf = new TextField();
		tf.setPrefSize(400, 50);
		tf.setOnAction((e)-> {			

			echoMsg();

		});
		
		sendBtn = new Button("Server Conn");
		sendBtn.setPrefSize(250, 50);
		sendBtn.setOnAction((e) -> {
					
			echoMsg();
			
		});
		
		FlowPane flowpane = new FlowPane();
		flowpane.setPrefSize(700, 50);
		//flowpane.getChildren().add(startBtn);
		flowpane.getChildren().addAll(tf, sendBtn);
		
		root.setBottom(flowpane);
		
		//
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Date Client");
		primaryStage.setOnCloseRequest(e -> {
			try {
				if(br != null) br.close();
				if(pr != null) pr.close();
				if(socket != null) socket.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		});
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch();
	}
	
}