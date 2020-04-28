package javaArduino;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
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
	private Socket socket;
	private BufferedReader socketIn;
	private PrintWriter socketOut;
	
	private CommPortIdentifier portIdentifier;
	private CommPort commPort;
	private SerialPort serial;
	private InputStream serialIn;
	private OutputStream serialOut;
	private BufferedWriter serialBW;
	private BufferedReader serialBR;
	
	private final String COMPORT = "COM5";

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
			
			Runnable sender = new Runnable() {
				
				@Override
				public void run() {
					try {
						server = new ServerSocket();
						server.bind(new InetSocketAddress(55566));
						displayText("## Server Start ##");
						
						socket = server.accept();
						displayText("[Client - " + socket.getInetAddress() + "] connected");
						socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						socketOut = new PrintWriter(socket.getOutputStream());
							
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
						displayText("[Error]" + e.toString());
						System.out.println(e.toString());
					}
				}
			}; // Runnable
			Thread t = new Thread(sender);
			t.start();
			
		}); // startBtn.setOnAction
		
		stopBtn = new Button("Stop Server");
		stopBtn.setPrefSize(250,  50);
		stopBtn.setOnAction((e) -> {
			try {
				if(socket != null && !socket.isClosed()) {
					socket.close();
					socketIn.close();
					socketOut.close();
				}
				if(server != null && !server.isClosed()) {
					server.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
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
				commPort = portIdentifier.open("PORT_OPEN", 2000);
				
				if(commPort instanceof SerialPort) {
					serial = (SerialPort)commPort;
					serial.setSerialPortParams(
							9600, 
							SerialPort.DATABITS_8, 
							SerialPort.STOPBITS_1, 
							SerialPort.PARITY_NONE);
					
					serialIn = this.serial.getInputStream();
					serialOut = this.serial.getOutputStream();
					serialBW = new BufferedWriter(new OutputStreamWriter(serialOut));
					serialBR = new BufferedReader(new InputStreamReader(serialIn));
					
					serial.addEventListener(new SerialEventListener(serialIn));
					serial.notifyOnDataAvailable(true);
					
				} else {
					System.out.println("Serial Port가 아닙니다.");
				} // check SerialPort
			} // isCurrentlyOwned()
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	class SerialEventListener implements SerialPortEventListener {
		
		private InputStream in;
//		private String datas = "";
		
		public SerialEventListener (InputStream in) {
			this.in = in;
		}
		
		@Override
		public void serialEvent(SerialPortEvent event) {		
			if(event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
				try {
					int size = in.available();		// data size
					byte[] data = new byte[size];
					
					in.read(data, 0, size);
					String str = new String(data);
					socketOut.println(str);
					socketOut.flush();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		launch();
	}
	
}
