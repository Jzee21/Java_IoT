package javaNetwork.jzee;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class Jzee_MultiEchoServer extends Application {
	
	private TextArea ta;
	private Button startBtn, stopBtn;

	final static int POOLSIZE = 5;
	
	private ExecutorService executorService = Executors.newFixedThreadPool(POOLSIZE);
	private ServerSocket server;
	private Socket socket;

	void printMsg(String msg) {
		Platform.runLater(() -> {
			ta.appendText(msg + "\n");
		});
	}
	
	private void run() {
		try {
			server = new ServerSocket(55556);
			System.out.println("[Server created]");
			
			while(true) {
				socket = server.accept();
				if(((ThreadPoolExecutor)executorService).getActiveCount() >= POOLSIZE) {
					new Thread(new OutofPool(socket)).start();
				} else {					
					executorService.execute(new EchoSocket(socket, this.clone()));
				}
//				System.out.println("[Server pool size] " + ((ThreadPoolExecutor)executorService).getPoolSize());
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		System.out.println(Thread.currentThread().getName());

		BorderPane root = new BorderPane();
		root.setPrefSize(700, 500);
		
		ta = new TextArea();
		root.setCenter(ta);
		
		startBtn = new Button("Start Thread");
		startBtn.setPrefSize(250, 50);
		startBtn.setOnAction((e) -> {
			//
		});
		
		stopBtn = new Button("Stop Thread");
		stopBtn.setPrefSize(250,  50);
		stopBtn.setOnAction((e) -> {
			//
		});
		
		FlowPane flowpane = new FlowPane();
		flowpane.setPrefSize(700, 50);
		//flowpane.getChildren().add(startBtn);
		flowpane.getChildren().addAll(startBtn, stopBtn);
		
		root.setBottom(flowpane);
		
		//
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Echo Server - ThreadPool");
		primaryStage.setOnCloseRequest(e -> {
			try {
				if(socket != null) socket.close();
				if(server != null) server.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch();
	}
	
}

class OutofPool implements Runnable {
	private Socket socket;
	
	OutofPool() {}
	OutofPool(Socket socket) {
		this.socket = socket;
	}
	@Override
	public void run() {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.println("Error");
			out.flush();
			
			if(out != null) out.close();
			if(socket != null) socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class EchoSocket implements Runnable {
	
	private Socket socket;
	private String endKeyword = "@EXIT";
	private Jzee_MultiEchoServer clone;
	
	EchoSocket() {}
	EchoSocket(Socket socket) {
		this.socket = socket;
	}
	EchoSocket(Socket socket, Object clone) {
		this.socket = socket;
		this.clone = (Jzee_MultiEchoServer)clone;
	}
	
	@Override
	public void run() {
		try {
//			System.out.println("[" + socket.getInetAddress() + "] Client Connected");
//			System.out.println("[" + Thread.currentThread().getName() + "] Client Connected");
			this.clone.printMsg("[" + Thread.currentThread().getName() + "] Client Connected");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			
			out.println("Success");
			out.flush();
			
			String msg;
			
			while(true) {
				msg = br.readLine();
				if((msg == null) || (msg.toUpperCase().equals(endKeyword))) {
					break;
				}
				out.println(msg);
				out.flush();
			}

			this.clone.printMsg("[" + Thread.currentThread().getName() + "] disconnected");
//			System.out.println("[" + socket.getInetAddress() + "] Client Disconnected");
			if(out != null) out.close();
			if(br != null) br.close();
			if(socket != null) socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
