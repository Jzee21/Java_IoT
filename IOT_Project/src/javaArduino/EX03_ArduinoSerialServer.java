package javaArduino;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class EX03_ArduinoSerialServer extends Application {
	
	private TextArea ta;
	private Button startBtn, stopBtn;
	
	private ServerSocket server;
	private BufferedReader socketIn;
	
	private CommPortIdentifier portIdentifier;
	private CommPort commPort;
	private SerialPort serial;
	private InputStream serialIn;
	private OutputStream serialOut;
//	 private BufferedWriter bw;
	
	private final String COMPORT = "COM7";

	private void displayText(String msg) {
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
		
		startBtn = new Button("Start Server");
		startBtn.setPrefSize(250, 50);
		startBtn.setOnAction((e) -> {
			
			Runnable r = new Runnable() {
				
				@Override
				public void run() {
					try {
						server = new ServerSocket();
						server.bind(new InetSocketAddress(55566));
						displayText("## Server Start ##");
						
						Socket s = server.accept();
						displayText("[Client - " + s.getInetAddress() + "] connected");
						socketIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
						
						String msg = "";
						while ((msg = socketIn.readLine()) != null) {
							int data = Integer.parseInt(msg);
							if(data >= 0 && data < 256) {
//								System.out.println("pwm : " + data);
								displayText("pwm : " + data);
								serialOut.write(data);
//								bw.write(msg, 0, msg.length());
							}
						}
					} catch (Exception e) {
						System.out.println(e.toString());
					}
				}
			}; // Runnable
			Thread t = new Thread(r);
			t.start();
			
		}); // startBtn.setOnAction
		
		stopBtn = new Button("Stop Server");
		stopBtn.setPrefSize(250,  50);
		stopBtn.setOnAction((e) -> {
			//
		}); // stopBtn.setOnAction
		
		FlowPane flowpane = new FlowPane();
		flowpane.setPrefSize(700, 50);
		//flowpane.getChildren().add(startBtn);
		flowpane.getChildren().addAll(startBtn, stopBtn);
		
		root.setBottom(flowpane);
		
		//
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("예제용 JavaFX");
		primaryStage.setOnCloseRequest(e -> {
			// System.exit(0);
		});
		primaryStage.show();
		
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(COMPORT);
			if(portIdentifier.isCurrentlyOwned()) {
				System.out.println("포트가 사용중입니다.");
			} else {
				this.commPort = portIdentifier.open("PORT_OPEN", 2000);
				
				if(commPort instanceof SerialPort) {
					this.serial = (SerialPort)commPort;
					this.serial.setSerialPortParams(
							9600, 
							SerialPort.DATABITS_8, 
							SerialPort.STOPBITS_1, 
							SerialPort.PARITY_NONE);
					
					this.serialIn = this.serial.getInputStream();
					this.serialOut = this.serial.getOutputStream();
//					this.bw = new BufferedWriter(new OutputStreamWriter(serialOut));
					
				} else {
					System.out.println("Serial Port가 아닙니다.");
				} // check SerialPort
			} // isCurrentlyOwned()
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		launch();
	}
	
}
