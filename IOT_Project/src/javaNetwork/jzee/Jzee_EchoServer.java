package javaNetwork.jzee;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Jzee_EchoServer {

	public static void main(String[] args) {
		
		ServerSocket server;
		Socket socket;
		
		BufferedReader br;
		PrintWriter out;
		
		String msg = "";
		String endKeyword = "@EXIT";
		
		try {
			server = new ServerSocket(55556);
			System.out.println("[server created]");
			
			socket = server.accept();
			System.out.println("[" + socket.getInetAddress() + "] Client Connected");
			
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
			
			while(true) {
				msg = br.readLine();
				if((msg == null) || (msg.toUpperCase().equals(endKeyword))) {
					break;
				}
				System.out.println("##### msg : " + msg);
				out.println(msg);
				out.flush();
			}
			
			if(out != null) out.close();
			if(br != null) br.close();
			
			if(socket != null) socket.close();
			if(server != null) server.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
