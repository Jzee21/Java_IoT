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

public class Jzee_SingleChatClient extends Application {
	
	private TextArea ta;
	private Button connBtn;
	private TextField tf, idtf;
	private String id;
	
	private ExecutorService executor = Executors.newCachedThreadPool();
	
	private Socket socket;
	private BufferedReader br;
	private PrintWriter pr;

	private void printMsg(String msg) {
		Platform.runLater(() -> {
			ta.appendText(msg + "\n");
		});
	}
	
	private void close() {
		try {
			if(this.br != null) this.br.close();
			if(this.pr != null) this.pr.close();
			if(this.socket != null) this.socket.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		System.out.println(Thread.currentThread().getName());
		// JavaFX Application Thread

		BorderPane root = new BorderPane();
		root.setPrefSize(700, 500);
		
		ta = new TextArea();
//		ta.setDisable(true);
		root.setCenter(ta);
		
		//		
		connBtn = new Button("Join");
		connBtn.setPrefSize(150, 50);
		connBtn.setOnAction((e) -> {
			ta.clear();
			printMsg("대화가 시작됩니다. 대화에서 나가시려면 @EXIT를 입력하세요!");
			idtf.setDisable(true);
			tf.setDisable(false);
			tf.setVisible(true);
			id = idtf.getText();
			connBtn.setDisable(true);
//			try {
//				socket = new Socket("localhost", 55566);
//				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//				pr = new PrintWriter(socket.getOutputStream());
//				
//				System.out.println("[" + socket.getInetAddress() + "] connected");
//				tf.setDisable(false);
//				connBtn.setDisable(true);
//				
//				ReceiveRunnable receiver = new ReceiveRunnable(br);
//				executor.execute(receiver);
//				
//			} catch (UnknownHostException e1) {
//				e1.printStackTrace();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
		});
		
		idtf = new TextField();
		idtf.setPrefSize(150, 50);
		idtf.setPromptText("Enter your Chat ID");
		idtf.setDisable(false);
		idtf.setVisible(true);
		idtf.requestFocus();
		
		tf = new TextField();
		tf.setPrefSize(400, 50);
		tf.setDisable(true);
		tf.setVisible(false);
		tf.setOnAction((e) -> {

			String msg = tf.getText();

			if(!msg.equals("")) {				
				if(!msg.equals("@EXIT")) {
//					pr.println(id + " ] " + msg);
//					pr.flush();
					tf.clear();
				} else {
					printMsg("[Server Disconnected]");
//					close();
//					this.executor.shutdownNow();
					tf.clear();
					tf.setDisable(true);
					idtf.setDisable(false);
					connBtn.setDisable(false);
				}
			}
			
		});
				
		FlowPane flowPane = new FlowPane();
		flowPane.setPrefSize(700, 50);
		//flowpane.getChildren().add(startBtn);
		flowPane.getChildren().addAll(idtf, tf, connBtn);
		
		root.setBottom(flowPane);
		
		//
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Date Client");
		primaryStage.setOnCloseRequest(e -> {
			// System.exit(0);
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
					if(msg == null)	break;
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

