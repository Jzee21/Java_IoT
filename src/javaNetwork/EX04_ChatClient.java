package javaNetwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

public class EX04_ChatClient extends Application {
	
	private TextArea ta;
	private Button connBtn;
	private TextField tf;
	
	private ExecutorService executor = Executors.newCachedThreadPool();
	
	private Socket socket;
	private BufferedReader br;
	private PrintWriter pr;

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
			try {
				socket = new Socket("localhost", 55566);
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				pr = new PrintWriter(socket.getOutputStream());
				
				System.out.println("[" + socket.getInetAddress() + "] connected");
				tf.setDisable(false);
				connBtn.setDisable(true);
				
				ReceiveRunnable receiver = new ReceiveRunnable(br);
				executor.execute(receiver);
				
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		tf = new TextField();
		tf.setPrefSize(400, 50);
		tf.setDisable(true);
		tf.setOnAction((e) -> {

			String msg = tf.getText();

			if(!msg.equals("@EXIT")) {
				pr.println(msg);
				pr.flush();
				tf.clear();				
			} else {
				printMsg("[Server Disconnected]");
				this.executor.shutdownNow();
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
		primaryStage.setTitle("Chat Client");
		primaryStage.setOnCloseRequest(e -> {
			// System.exit(0);
			executor.shutdownNow();
		});
		primaryStage.show();
		
	}
	
	class ReceiveRunnable implements Runnable {
		
		private BufferedReader br;
		
		ReceiveRunnable(BufferedReader br) {
			this.br = br;
		}
		
		@Override
		public void run() {
			String msg = "";
			try {
				while(true) {
					msg = br.readLine();
					if(msg == null)	{
						socket.close();
						break;
					}
					printMsg(msg);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	public static void main(String[] args) {
		launch();
	}
	
}

