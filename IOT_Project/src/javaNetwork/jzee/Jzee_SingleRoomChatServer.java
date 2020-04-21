package javaNetwork.jzee;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
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

public class Jzee_SingleRoomChatServer extends Application {

	private TextArea ta;
	private Button startBtn, stopBtn;
	private SingleRoom object = new SingleRoom();
	
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
		ta.setEditable(false);
		root.setCenter(ta);
		
		// bottom
		startBtn = new Button("Server Start");
		startBtn.setPrefSize(150, 40);
		startBtn.setOnAction((e) -> {
//			Thread.currentThread().getClass();
			Runnable myRunnable = () -> {
				
				Runnable myInner = () -> {
					try {
						this.server = new ServerSocket(55566);
						printMsg("===== [Port : " + server.getLocalPort() + "] Server Start =====");
						
						while(true) {
							Socket socket = server.accept();
							printMsg("[" + socket.getInetAddress() + "] Client Connected");
							SingleChat r = new SingleChat(socket, this.object);
							Future<String> future = executorService.submit(r);
							printMsg("[" + future.get() + "] Client Disconnected");
						} // while
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (InterruptedException | ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				};
//				new Thread(myInner).start();
				Thread t = new Thread(myInner);
				t.setDaemon(true);
				t.start();
				// myInner
				
				while(true) {
					try {
						Thread.sleep(3000);
					} catch (Exception e2) {
						if(this.server != null)
							try {
								this.server.close();
							} catch (IOException e1) {
								Thread.currentThread().interrupt();
							}
					}
				}
				
			}; // myRunnable
			executorService.execute(myRunnable);
			
		});
		
		stopBtn = new Button("Server Stop");
		stopBtn.setPrefSize(150, 40);
		stopBtn.setOnAction((e) -> {
			
		});
		
		FlowPane bottom = new FlowPane();
		bottom.getChildren().addAll(startBtn, stopBtn);
		bottom.setPadding(new Insets(5,5,5,5));
		bottom.setHgap(5);
		root.setBottom(bottom);
		
		// stage
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Multi Echo Server");
		stage.setOnCloseRequest((e) -> {
			
		});
		stage.show();
		
	} // start(Stage stage)
	
	public static void main(String[] args) {
		launch();
	}
}

class SingleRoom {
	List<SingleChat> list = new ArrayList<SingleChat>();
	
	public SingleRoom() {}
	
	// didn't work!
	public void addList(SingleChat t) {
		list.add(t);
		System.out.println("addList, " + list.size());
	}
	
	public void removeList(SingleChat t) {
		list.remove(t);
	}
	
	public void broadcast(String msg) {
		System.out.println("SingleRoom - broadcast()");
		if(list.size() != 0) {
			for(SingleChat room : list) {
				room.update(msg);
			}
		}
	}
	
}

class SingleChat implements Callable<String> {
	
	private SingleRoom object;

	private Socket socket;
	private PrintWriter pr;
	private BufferedReader br;
	
	private SingleChat() {}
	public SingleChat(Socket socket, SingleRoom object) {
		this.socket = socket;
		this.object = object;
		System.out.println("creating...");
		try {
			this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.pr = new PrintWriter(this.socket.getOutputStream());
			this.object.addList(this);
		} catch (Exception e) {
			close();
		}
		System.out.println("SingleChat created");
	}
	
	@Override
	public String call() throws Exception {
		
		String result = this.socket.getInetAddress().toString();
		String line = "";
		boolean interrupted = false;
		
		try {
			while(!interrupted) {
				if(this.br.ready()) {
					line = this.br.readLine();
					if(line.toUpperCase().equals("@EXIT")) {
						break;
					}
					object.broadcast(line);
//					pr.println(line);
//					pr.flush();
				}
				interrupted = Thread.interrupted();
			}
			close();
		} catch (Exception e) {
			close();
		}
		
		return result;
	}
	
	public void close() {
		try {
			object.removeList(this);
			if(this.br != null) this.br.close();
			if(this.pr != null) this.pr.close();
			if(this.socket != null) this.socket.close();
		} catch (Exception e) {
			//
		}
	}
	
	public void update(String msg) {
		System.out.println("SingleChat - update()");
		pr.println(msg);
		pr.flush();
	}
}


