package javaNetwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
public class EX04_ChatServer extends Application {

	private TextArea ta;
	private Button startBtn, stopBtn;
	
	private ExecutorService executorService = Executors.newCachedThreadPool();
	
	private ServerSocket server;
	
	private ChatSharedObject objcet = new ChatSharedObject();
	
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
		ta.setEditable(false);
		root.setCenter(ta);
		
		// bottom
		startBtn = new Button("Server Start");
		startBtn.setPrefSize(150, 40);
		startBtn.setOnAction((e) -> {
		
			Runnable runnable = () -> {	
				try {
					server = new ServerSocket(55566);
					printMsg("===== [Port : " + server.getLocalPort() + "] Server Start =====");
					
					while(true) {
						Socket socket = server.accept();
						printMsg("[" + socket.getInetAddress() + "] Client Connected");
						ChatRunnable client = new ChatRunnable(socket, this.objcet);
						this.objcet.add(client);
						executorService.execute(client);
					}
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			};
			
			executorService.execute(runnable);
			
		});
		
		stopBtn = new Button("Server Stop");
		stopBtn.setPrefSize(150, 40);
		stopBtn.setOnAction((e) -> {
			printMsg("===== [Port : " + server.getLocalPort() + "] Server Closing... =====");
			executorService.shutdownNow();
			try {
			    if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
			        printMsg("===== Server Closing... =====");
			        executorService.shutdownNow();
			        if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
			            System.out.println("여전히 종료하지 않은 작업 존재");
			        }
			    }
			} catch (InterruptedException e1) {
			    executorService.shutdownNow();
			    Thread.currentThread().interrupt();
			}
		});
		
		FlowPane bottom = new FlowPane();
		bottom.getChildren().addAll(startBtn, stopBtn);
		bottom.setPadding(new Insets(10,10,10,10));
		bottom.setHgap(10);
		root.setBottom(bottom);
		
		// stage
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Multi Chat Server");
		stage.setOnCloseRequest((e) -> {
			executorService.shutdownNow();
		});
		stage.show();
		
	} // start(Stage stage)
	
	public static void main(String[] args) {
		launch();
	}
}

class ChatSharedObject {
	List<ChatRunnable> clients = new ArrayList<ChatRunnable>();
	
	ChatSharedObject() {}
	
	public void add(ChatRunnable runnable) {
		this.clients.add(runnable);
	}
	
	public void remove(ChatRunnable runnable) {
		this.clients.remove(runnable);
	}
	
	public void broadcast(String msg) {
		for(ChatRunnable client : this.clients) {
			client.update(msg);
//			PrintWriter pr = client.getPr();
//			pr.println(msg);
//			pr.flush();
		}
	}
}

class ChatRunnable implements Runnable {
	
	private Socket socket;
	private PrintWriter pr;
	private BufferedReader br;
	
	private ChatSharedObject object;
	
//	ChatRunnable() {}
	ChatRunnable(Socket socket) {
		this.socket = socket;
		try {
			this.pr = new PrintWriter(this.socket.getOutputStream());
			this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	ChatRunnable(Socket socket, ChatSharedObject object) {
		this(socket);
		this.object = object;
	}
	
	public PrintWriter getPr() {
		return this.pr;
	}
	
	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName() + " start");
		String line = "";
		try {
						
			while((line = this.br.readLine()) != null) {
				if(line.toUpperCase().equals("@EXIT")) {
					break;
				}
//				pr.println(line);
//				pr.flush();
				this.object.broadcast(line);
			}
			System.out.println(Thread.currentThread().getName() + " break");
			stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void stop() {
		try {
			this.object.remove(this);
			if(this.br != null) this.br.close();
			if(this.pr != null) this.pr.close();
			if(this.socket != null) this.socket.close();
			System.out.println("socket status : " + socket.isConnected());	// true
			System.out.println("socket status : " + socket.isClosed());		// true
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(String msg) {
		try {
			this.pr.println(msg);
			this.pr.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}




