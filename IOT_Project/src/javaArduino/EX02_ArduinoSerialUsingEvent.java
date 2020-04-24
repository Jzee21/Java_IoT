package javaArduino;

import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

class SerialListener implements SerialPortEventListener {
	
	private InputStream in;
	
	public SerialListener(InputStream in) {
		this.in = in;
	}
	
	@Override
	public void serialEvent(SerialPortEvent event) {		
		if(event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				int size = in.available();		// data size
				byte[] data = new byte[size];
				
				in.read(data, 0, size);
				System.out.print("_" + new String(data) + "_");
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

public class EX02_ArduinoSerialUsingEvent {

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
					
					// read data by Event Listener
					port.addEventListener(new SerialListener(in));
					port.notifyOnDataAvailable(true);	// Serial로 data가 왔을때만 Event 처리
					
				} else {
					System.out.println("Serial Port가 아닙니다.");
				} // if(commPort instanceof SerialPort)
				
			} // if(isCurrentlyOwned())
			
		} catch (Exception e) {
			e.printStackTrace();
		} // try
	} // main

}
