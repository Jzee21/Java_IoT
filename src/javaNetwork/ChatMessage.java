package javaNetwork;

public class ChatMessage {
	private String code;
	private int userID;
	private int destID;		// destination room id
	private String stringData;
	
	// constructor
	public ChatMessage(String code) {
		this.code = code;
	}
	
	public ChatMessage(String code, int userID) {
		this.code = code;
		this.userID = userID;
	}
	
	public ChatMessage(String code, String stringData) {
		this.code = code;
		this.stringData = stringData;
	}
	
	public ChatMessage(String code, int userID, String stringData) {
		this(code, userID);
		this.stringData = stringData;
	}
	
	public ChatMessage(String code, int userID, int destID) {
		this(code, userID);
		this.destID = destID;
	}
	
	public ChatMessage(String code, int userID, int destID, String stringData) {
		this(code, userID, stringData);
		this.destID = destID;
	}
	
	// getter - setter
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getDestID() {
		return destID;
	}

	public void setDestID(int destID) {
		this.destID = destID;
	}

	public String getStringData() {
		return stringData;
	}

	public void setStringData(String jsonData) {
		this.stringData = jsonData;
	}

	@Override
	public String toString() {
		return "Message [code=" + code + ", userID=" + userID + ", destID=" + destID + ", stringData=" + stringData
				+ "]";
	}
	
} // class Message
