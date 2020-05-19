package javaNetwork.multiChat;

public class ChatMessage {
	private String code;
	private String userID;
	private String destID;		// destination room id
	private String stringData;
	
	// constructor
	public ChatMessage() {
		this(null, null, null, null);
	}
	
	public ChatMessage(String code, String userID, String destID, String data) {
		this.code = code;
		this.userID = userID;
		this.destID = destID;
		this.stringData = data;
	}

	// get, set
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getDestID() {
		return destID;
	}

	public void setDestID(String destID) {
		this.destID = destID;
	}

	public String getStringData() {
		return stringData;
	}

	public void setStringData(String stringData) {
		this.stringData = stringData;
	}

	@Override
	public String toString() {
		return "ChatMessage [code=" + code + ", userID=" + userID + ", destID=" + destID + ", stringData=" + stringData
				+ "]";
	}
	
} // class Message
