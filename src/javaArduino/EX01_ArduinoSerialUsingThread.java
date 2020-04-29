package javaArduino;

import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class EX01_ArduinoSerialUsingThread {

	public static void main(String[] args) {
		CommPortIdentifier portIdentifier = null;
		try {
			// 1. Serial 통신을 위한 COM 포트 설정
			portIdentifier = CommPortIdentifier.getPortIdentifier("COM7");
			
			// 2. 포트의 점유 상태 확인
			if(portIdentifier.isCurrentlyOwned()) {
				System.out.println("포트가 사용중입니다.");
			} else {
				// 3. 포트 객체를 받아온다
				CommPort commPort = portIdentifier.open("PORT_OPEN", 2000);
				
				// 4. 포트 종류 확인
				// 목적은 Serial 통신 (Serial Port)(직렬 포트)
				// Port는 Serial Port 말고도 Parallel Port(병렬 포트)도 있다
				if(commPort instanceof SerialPort) {
					// 5. 포트 설정(통신속도)
					SerialPort port = (SerialPort)commPort;
					port.setSerialPortParams(
							9600, 
							SerialPort.DATABITS_8, 
							SerialPort.STOPBITS_1, 
							SerialPort.PARITY_NONE);
					
					InputStream in = port.getInputStream();
					OutputStream out = port.getOutputStream();
					
					Thread t = new Thread(() -> {
						byte[] buffer = new byte[1024];
						int len = -1;
						try {
							while((len = in.read(buffer)) != -1) {
								System.out.println("data : " + new String(buffer, 0, len));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
					t.start();
					
				} else {
					System.out.println("Serial Port가 아닙니다.");
				}
				
				
			} // if(isCurrentlyOwned())
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
