package javaNetwork;

public class ChatClient {
	int userID;
	String nickname;
	
	ChatClient() {}
	
	ChatClient(ChatClient client) {
		this.userID = client.getUserID();
		this.nickname = client.getNickname();
	}
	
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
}
