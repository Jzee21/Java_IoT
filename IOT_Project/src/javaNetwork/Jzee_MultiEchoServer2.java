package javaNetwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
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

/*	Server
 *  클라이언트가 접속하면 현재 시간을 알아내서 클라이언트에 전송
*/
public class Jzee_MultiEchoServer2 extends Application {

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
		ta.setEditable(false);
		root.setCenter(ta);
		
		// bottom
		startBtn = new Button("Server Start");
		startBtn.setPrefSize(150, 40);
		startBtn.setOnAction((e) -> {
			
//			boolean interrupted = false;
//			while(!interrupted) {
//				if(this.br.ready()) {
//					line = this.br.readLine();
//					if(line.toUpperCase().equals("@EXIT")) {
//						break;
//					}
//					pr.println(line);
//					pr.flush();
//				}
//				interrupted = Thread.interrupted();
//			}
//			stop();
			
			Runnable myRunnable = () -> {
				
				Runnable myInner = () -> {
					try {
						this.server = new ServerSocket(55566);
						printMsg("===== [Port : " + server.getLocalPort() + "] Server Start =====");
						
						while(true) {
							Socket socket = server.accept();
							printMsg("[" + socket.getInetAddress() + "] Client Connected");
							EchoCallbackJ r = new EchoCallbackJ(socket);
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
				new Thread(myInner).start();
				
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
				
			};
			
			Runnable runnable = () -> {	
				try {
					server = new ServerSocket(55566);
					printMsg("===== [Port : " + server.getLocalPort() + "] Server Start =====");
					
//					while(true) {
//						Socket socket = server.accept();
//						printMsg("[" + socket.getInetAddress() + "] Client Connected");
//						EchoRunnable r = new EchoRunnable(socket);
//						executorService.execute(r);
//						EchoCallback r = new EchoCallback(socket);
//						Future<String> future = executorService.submit(r);
//						printMsg("[" + future.get() + "] Client Disconnected");
//					} // while
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
		stage.setTitle("Multi Echo Server");
		stage.setOnCloseRequest((e) -> {
			executorService.shutdownNow();
		});
		stage.show();
		
	} // start(Stage stage)
	
	public static void main(String[] args) {
		launch();
	}
}

class EchoCallbackJ implements Callable<String> {
	
	private Socket socket;
	private PrintWriter pr;
	private BufferedReader br;
	
	EchoCallbackJ() {}
	EchoCallbackJ(Socket socket) {
		this.socket = socket;
		try {
			this.pr = new PrintWriter(this.socket.getOutputStream());
			this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String call() throws Exception {
		System.out.println(Thread.currentThread().getName() + " start");
		String line = "";
		try {
			
			boolean interrupted = false;
			while(!interrupted) {
				if(this.br.ready()) {
					line = this.br.readLine();
					if(line.toUpperCase().equals("@EXIT")) {
						break;
					}
					pr.println(line);
					pr.flush();
				}
				interrupted = Thread.interrupted();
			}
			stop();
			System.out.println(Thread.currentThread().getName() + " stop");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return socket.getInetAddress().toString();
	};
	
	private void stop() {
		try {
			if(this.br != null) this.br.close();
			if(this.pr != null) this.pr.close();
			if(this.socket != null) this.socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}





