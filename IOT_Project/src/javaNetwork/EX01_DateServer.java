package javaNetwork;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/*	Server
 *  클라이언트가 접속하면 현재 시간을 알아내서 클라이언트에 전송
*/
public class EX01_DateServer {

	public static void main(String[] args) {
		
		try {
			// 1. 클라이언트의 Socket 접속을 기다리는 ServerSocket 생성
			ServerSocket server = new ServerSocket(5556);	// new ServerSocket(Port_number)
			System.out.println("[server created]");
			
			// 2. 클라이언트의 접속을 기다리는 method 호출
			Socket s = server.accept();		// blocking method
									// 클라이언트가 접속할 때까지
			System.out.println("Client Accept!");
			
			// 3. Socket이 생성되면 데이터 입출력을 위해 Stream을 생성
			String date = (new Date()).toLocaleString();	// 사용 중지 권장	// 현재시간
			PrintWriter out = new PrintWriter(s.getOutputStream());
			out.println(date);
			out.flush();
			out.close();
			
			s.close();
			server.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
