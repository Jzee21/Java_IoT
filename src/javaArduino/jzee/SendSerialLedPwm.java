package javaArduino.jzee;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import javafx.application.Platform;

class Serial {
	
	private CommPortIdentifier portIdentifier;
	private CommPort commPort;
	private SerialPort serial;
	private InputStream in;
	private OutputStream out;
	
	private final Object MONITOR = new Object();
	
	public Serial() { }
	
	public boolean connSerialPort(String port) {
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(port);
			if(portIdentifier.isCurrentlyOwned()) {
				System.out.println("포트가 사용중입니다.");
				return false;
			} else {
				this.commPort = portIdentifier.open("PORT_OPEN", 2000);
				
				if(commPort instanceof SerialPort) {
					this.serial = (SerialPort)commPort;
					this.serial.setSerialPortParams(
							9600, 
							SerialPort.DATABITS_8, 
							SerialPort.STOPBITS_1, 
							SerialPort.PARITY_NONE);
					
					this.in = this.serial.getInputStream();
					this.out = this.serial.getOutputStream();
					
				} else {
					System.out.println("Serial Port가 아닙니다.");
					return false;
				} // check SerialPort
			} // isCurrentlyOwned()
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} // try
		return true;
	} // boolean connSerialPort(String port)
	
	public void close() {
		try {
			if(serial != null)
				this.serial.close();
			if(in != null)
				this.in.close();
			if(out != null)
				this.out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void write(int data) {
		try {
			System.out.println("Serial.write(" + data + ")");
			this.out.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // void sendNum(int data)
	
}

class SerialServer {
	
	private Serial serial;
	private ServerSocket server;
	private ExecutorService executor;
	
	private final int PORT = 55566;
	
	SerialServer(Serial serial) {
		this.serial = serial;
	}
	
	void startServer() {
		try {
			executor = Executors.newCachedThreadPool();
			
			server = new ServerSocket();
			server.bind(new InetSocketAddress(PORT));
			server.setSoTimeout(3000);
			
			System.out.println("### Start Server ###");
		} catch (Exception e) {
			e.printStackTrace();
			stopServer();
		}
		
		Runnable runnable = () -> {
			Socket socket = null;
			while(true) {
				try {
					socket = server.accept();
					System.out.println("[" + socket.getInetAddress() + "] Client Connected");
					
					receiver(socket);
					
				} catch (SocketTimeoutException e) {
					if(Thread.interrupted()) {
						break;
					} else continue;
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
			stopServer();
		};
		executor.submit(runnable);
	}
	
	void stopServer() {
		try {
			if(server != null && !server.isClosed()) {
				server.close();
			}
			if(executor != null && executor.isShutdown()) {
				executor.shutdownNow();
			}
		} catch (Exception e) {
			// do nothing
		}// try
		System.out.println("### Server Stop ###");
	}
	
	protected void receiver(Socket socket) {
		Runnable runnable = () -> {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));	
			} catch (IOException e) {
				e.printStackTrace();
				// close
				if(socket != null && !socket.isClosed()) {
					try {
						socket.close();
						if(in != null)
							in.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			} // try
			
			String msg = "";
			while(true) {
				try {
					msg = in.readLine();
					if(msg == null) {
						throw new IOException("Client Closed");
					} else {
						int data = Integer.parseInt(msg);
						if(data >= 0 && data < 256) {
							serial.write(data);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					if(socket != null && !socket.isClosed()) {
						try {
							socket.close();
							if(in != null)
								in.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					break;
				}
			}
			
		};
		executor.submit(runnable);
		
	}
	
}

public class SendSerialLedPwm {
	
	public static void main(String[] args) {
		
		Serial serial = new Serial();
		if(serial.connSerialPort("COM7")) {
			SerialServer server = new SerialServer(serial);
			try {
				server.startServer();
			} catch (Exception e) {
				server.stopServer();
			}
		}
		
//		if(serial.connSerialPort("COM7")) {
//			System.out.println("Conn Success");
//		}
//		
//		try {
//			for (int pwm = 0; pwm < 25; pwm++) {
//				serial.write(pwm*10);
//				Thread.sleep(100);
//			}
//			for (int i = 0; i < 3; i++) {
//				serial.write(0);
//				Thread.sleep(100);
//				serial.write(255);
//				Thread.sleep(100);
//			}
//			for (int pwm = 25; pwm >= 0; pwm--) {
//				serial.write(pwm*10);
//				Thread.sleep(50);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			serial.close();
//		}
		
	}

}
