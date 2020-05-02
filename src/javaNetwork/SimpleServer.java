package javaNetwork;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer {

	public static void main(String[] args) {
		try {
			ServerSocket server = new ServerSocket(55566);
			
			Socket socket = server.accept();
			
			OutputStream out = socket.getOutputStream();
			
			String sendData = "서버에서 날라옴";
			out.write(sendData.getBytes());
			
			socket.close();
			server.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
