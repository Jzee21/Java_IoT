package javaArduino;

import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class EX00_ArduinoSerialUsingTemplate {

	public static void main(String[] args) {
		CommPortIdentifier portIdentifier = null;
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier("COM7");
			
			if(portIdentifier.isCurrentlyOwned()) {
				System.out.println("포트가 사용중입니다.");
			} else {
				CommPort commPort = portIdentifier.open("PORT_OPEN", 2000);
				
				if(commPort instanceof SerialPort) {
					SerialPort port = (SerialPort)commPort;
					port.setSerialPortParams(
							9600, 
							SerialPort.DATABITS_8, 
							SerialPort.STOPBITS_1, 
							SerialPort.PARITY_NONE);
					
					InputStream in = port.getInputStream();
					OutputStream out = port.getOutputStream();
					
					Thread t = new Thread(() -> {
						// using port
					});
					t.start();
					
				} else {
					System.out.println("Serial Port가 아닙니다.");
				} // if(commPort instanceof SerialPort)
				
			} // if(isCurrentlyOwned())
			
		} catch (Exception e) {
			e.printStackTrace();
		} // try
	} // main

}
