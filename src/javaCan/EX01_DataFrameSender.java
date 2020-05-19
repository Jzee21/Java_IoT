package javaCan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class EX01_DataFrameSender extends Application implements SerialPortEventListener{

	private static final String PORTNAME = "COM16";
	
	private TextArea	textarea;
	private Button		connBtn, sendBtn, envBtn, revEnableBtn, revDisableBtn;
	
	private CommPortIdentifier portIdentifier;
	private CommPort	commPort;
	private SerialPort	serialPort;
	
	private OutputStream out;
	private BufferedReader in;
	
	// ===================================================
	private void displayText(String msg) {
		Platform.runLater(() -> {
			this.textarea.appendText(msg + "\n");
		});
	}
	
	private void initialize() {
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(PORTNAME);
			if (portIdentifier.isCurrentlyOwned()) {
				System.out.println("포트가 다른 프로그램에 의해 사용중입니다.");
			} else {
				commPort = portIdentifier.open("PortOpen", 4000);
				if (commPort instanceof SerialPort) {
					serialPort = (SerialPort) commPort;
					serialPort.setSerialPortParams(
							9600, 
							SerialPort.DATABITS_8, 
							SerialPort.STOPBITS_1, 
							SerialPort.PARITY_NONE);
					serialPort.setFlowControlMode(
							SerialPort.FLOWCONTROL_RTSCTS_IN |
							SerialPort.FLOWCONTROL_RTSCTS_OUT );
					out = serialPort.getOutputStream();
					in = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
					
					serialPort.setRTS(true);
					serialPort.addEventListener(this);
					serialPort.notifyOnDataAvailable(true);
					
//					System.out.println("포트가 연결되었습니다.");
					displayText("포트가 연결되었습니다.");
				}
				
			} // else - portIdentifier.isCurrentlyOwned()
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // initialize
	
	private synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	} // close
	
	private void send(String dataframe) {
		try {
			out.write(dataframe.getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendDataFrame(String data) {
		String dataframe = null;
		data = data.toUpperCase();
		
		int checksum = 0;
		char c[] = data.toCharArray();
		for (char cc : c) {
			checksum += cc;
		}
		checksum = (checksum & 0xFF);
		
		dataframe = ":" + data + Integer.toHexString(checksum).toUpperCase() + "\r";
		displayText("보낼 메세지 ] " + dataframe);
		send(dataframe);
	}
	
	@Override
	public void serialEvent(SerialPortEvent event) {
		if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			displayText("get Something!");
			try {
				String line = null;
				if (in.ready())
					line = in.readLine();
				displayText("받은 데이터 ] " + line);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	// ===================================================
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		BorderPane root = new BorderPane();
		root.setPrefSize(700, 500);
		textarea = new TextArea();
		root.setCenter(textarea);
		
		// ---------------------------------
		connBtn = new Button("COM포트 접속");
		connBtn.setPrefSize(100, 50);
		connBtn.setPadding(new Insets(10));
		connBtn.setOnAction((e) -> {
			this.initialize();
		});
		
		sendBtn = new Button("Data Frame\n전송");
		sendBtn.setPrefSize(100, 50);
		sendBtn.setPadding(new Insets(10));
		sendBtn.setOnAction((e) -> {
			String dataFrame = "W280000000611000022000000FF";
			// : W 28 00000001 11000022000000FF
			sendDataFrame(dataFrame);
		});
		
//		envBtn, revEnableBtn, revDisableBtn
		envBtn = new Button("환경설정쓰기");
		envBtn.setPrefSize(100, 50);
		envBtn.setPadding(new Insets(10));
		envBtn.setOnAction((e) -> {
			String envInfo = "Z1C0x340000000600000006";
			// :Z 1C 0x34 00000006 00000006
			sendDataFrame(envInfo);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			sendDataFrame("G11");
		});
		
		revEnableBtn = new Button("수신 시작");
		revEnableBtn.setPrefSize(100,50);
		revEnableBtn.setPadding(new Insets(10));
		revEnableBtn.setOnAction((e) -> {
			sendDataFrame("G11");
		});
		
		revDisableBtn = new Button("수신 중지");
		revDisableBtn.setPrefSize(100,50);
		revDisableBtn.setPadding(new Insets(10));
		revDisableBtn.setOnAction((e) -> {
			sendDataFrame("G10");
		});
		
		FlowPane bottom = new FlowPane();
		bottom.setPrefSize(700, 60);
		bottom.setPadding(new Insets(5));
		bottom.setHgap(5);
		bottom.getChildren().addAll(connBtn, sendBtn, envBtn, revEnableBtn, revDisableBtn);
		root.setBottom(bottom);
		
		// ---------------------------------
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Can Protocol Tester");
		primaryStage.setOnCloseRequest(e -> {
			this.close();
		});
		primaryStage.show();
	} // start
	
	public static void main(String[] args) {
		launch(args);
	}

}
