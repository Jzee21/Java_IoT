package javaNetwork;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

public class Jzee_ObjectClient {

	public static void main(String[] args) {
		
		try {
			
			System.out.println("Socket");
			Socket socket = new Socket("localhost", 9999);
			
			System.out.println("make Object Stream");
//			ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
			
			System.out.println("Send Object");
			oout.writeObject(new MsgTest(0001, "Tester", "message"));
			System.out.println("Write Object");
			oout.flush();
			System.out.println("Flush Object");
			
			System.out.println("Close");
			// close
			oout.close();
//			oin.close();
			socket.close();
			
			//
//			String msg = "a,b,c,d,e";
//			String[] arr = msg.split(",");
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}

