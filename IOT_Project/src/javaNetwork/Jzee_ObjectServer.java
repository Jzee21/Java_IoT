package javaNetwork;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

public class Jzee_ObjectServer {
	
	public static void main(String[] args) {
		
		ServerSocket server;
		Socket socket = null;
		// Socket socket = new Socket("localhost", 9999);
		try {
			
			System.out.println("Server Run");
			
			server = new ServerSocket(9999);
			
			while(true) {
				System.out.println("Wait accept()");
				socket = server.accept();
				System.out.println("Pass accept()");
				
				System.out.println("Create Stream");
				ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
//				ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
				System.out.println("Create Stream End");
				
				System.out.println("Get Object");
				MsgTest msg = (MsgTest) oin.readObject();

				System.out.println("roomID : " + msg.roomID);
				System.out.println("senderID : " + msg.senderID);
				System.out.println("message : " + msg.message);
				
				// close
//				oout.close();
				oin.close();
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}

}

class MsgTest implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	int roomID;
	String senderID;
	String message;
	
	MsgTest(int roomID, String senderID, String message) {
		this.roomID = roomID;
		this.senderID = senderID;
		this.message = message;
	}

	public int getRoomID() {
		return roomID;
	}

	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}

	public String getSenderID() {
		return senderID;
	}

	public void setSenderID(String senderID) {
		this.senderID = senderID;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
