package javaNetwork.multiChat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient implements Runnable {
	
	private int		userID;
	private String	nickname;
	private Socket 	socket;
	private BufferedReader  input;
	private PrintWriter 	output;
	
	// // constructor
	ChatClient() {
		this(0, null);
	}
	
	ChatClient(int userID, String nickname) {
		this.userID = userID;
		this.nickname = nickname;
	}

	// get, set
	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	// custom method
	public void close() {
		if (socket != null) {
			String addr = socket.getInetAddress().toString();
			try {
				if (!socket.isClosed()) {
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
	}
	
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
		
	}
	
	
}
