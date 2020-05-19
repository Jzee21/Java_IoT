package javaNetwork.multiChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatClient implements Runnable {
	
	private String	nickname;
	private Socket 	socket;
	private BufferedReader  input;
	private PrintWriter 	output;
	private List<String> enterRooms;
	
	private ChatService service = ChatService.getInstnace();
	
	// // constructor
	ChatClient() {
		this(null, null);
	}
	
	ChatClient(String nickname, Socket socket) {
		this.nickname = nickname;
		this.socket = socket;
		this.enterRooms = new ArrayList<String>();
	}

	// get, set
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public List<String> getEnterRooms() {
		return enterRooms;
	}

	public void setEnterRooms(List<String> enterRooms) {
		this.enterRooms = enterRooms;
	}

	// custom method
	public void close() {
		if (socket != null) {
			String addr = socket.getInetAddress().toString();
			try {
				if (!socket.isClosed()) {
					try {
						input.readLine();
					} catch (Exception e) {
						System.out.println("아오");
					}
					System.out.println(addr + " - socket close");
					socket.shutdownInput();
					socket.isOutputShutdown();
					socket.close();
					input.close();
					output.close();
				}
			} catch (Exception e) {
				// do nothing
			}
			this.socket = null;
			this.input = null;
			this.output = null;
			System.out.println("[" + addr + "] closed");
		}
	} // close
	
	public void send(String msg) {
		if(this.socket != null && !socket.isClosed()) {
			output.println(msg);
			output.flush();
		}
	}
	
	@Override
	public void run() {
		// set streams
		try {
			this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.output = new PrintWriter(this.socket.getOutputStream());
		} catch (Exception e) {
			this.close();
		}
		
		String line = "";
		while(true) {
			try {
				if(input.ready()) {
					line = input.readLine();
					if(line == null) {
						throw new IOException("Client Closed");
					} else {
						ChatMessage data = service.getGson().fromJson(line, ChatMessage.class);
						service.messageHandler(this, data);
					}
				}
			} catch (IOException e) {
				close();
				break;
			}
		} // while
		System.out.println("client finish");
		
	} // run
	
	
} // ChatClient
