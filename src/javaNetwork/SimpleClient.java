package javaNetwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class SimpleClient {

	public static void main(String[] args) {
		
		try {
			Socket socket = new Socket("70.12.60.105", 55566);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String msg = in.readLine();
			System.out.println(msg);
			
			socket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
