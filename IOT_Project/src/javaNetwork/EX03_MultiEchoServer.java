package javaNetwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/*	Server
 *  클라이언트가 접속하면 현재 시간을 알아내서 클라이언트에 전송
*/
public class EX03_MultiEchoServer extends Application {

	private TextArea ta;
	private Button startBtn, stopBtn;
	
	private ExecutorService executorService = Executors.newCachedThreadPool();
	
	private ServerSocket server;
	
	public void printMsg(String msg) {
		Platform.runLater(() -> {
			ta.appendText(msg + "\n");
		});
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		
		BorderPane root = new BorderPane();
		root.setPrefSize(700, 500);
		
		// center
		ta = new TextArea();
		ta.setPrefSize(700, 450);
		root.setCenter(ta);
		
		// bottom
		startBtn = new Button("Server Start");
		startBtn.setPrefSize(150, 40);
		startBtn.setOnAction((e) -> {
			
		});
		
		stopBtn = new Button("Server Stop");
		stopBtn.setPrefSize(150, 40);
		stopBtn.setOnAction((e) -> {
			
		});
		
		FlowPane bottom = new FlowPane();
		bottom.getChildren().addAll(startBtn, stopBtn);
		bottom.setPadding(new Insets(10,10,10,10));
		bottom.setHgap(10);
		root.setBottom(bottom);
		
		// stage
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Multi Echo Server");
		stage.setOnCloseRequest((e) -> {
			executorService.shutdownNow();
		});
		stage.show();
		
	}
	
	public static void main(String[] args) {
		launch();
	}
}