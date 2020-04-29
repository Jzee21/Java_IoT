package javaNetwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/*	Server
 *  클라이언트가 접속하면 현재 시간을 알아내서 클라이언트에 전송
*/
public class EX02_EchoServer {

	public static void main(String[] args) {
		
		try {
			ServerSocket server = new ServerSocket(55556);
			System.out.println("[server created]");
			
			Socket socket = server.accept();
			System.out.println("[" + socket.getInetAddress() + "] Client Connected");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter pr = new PrintWriter(socket.getOutputStream());
			
			String msg = "";
			while(true) {
				msg = br.readLine();
				System.out.println("##### msg : " + msg);
				if((msg == null) || (msg.equals("@EXIT"))) {
					break;
				}
				pr.println(msg);
				pr.flush();
			}
			
			if(pr != null) pr.close();
			if(br != null) br.close();
			
			if(socket != null) socket.close();
			if(server != null) server.close();
			
			System.out.println("[server closed]");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
