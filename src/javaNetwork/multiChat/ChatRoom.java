package javaNetwork.multiChat;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
	
	private String roomID;
	private String roomName;
	private List<String> participants;
	
	// constructor
	public ChatRoom() {
		this(null, null);
	}
	
	public ChatRoom(String roomID, String roomName) {
		this.roomID = roomID;
		this.roomName = roomName;
		this.participants = new ArrayList<String>();
	}
	
	// get, set
	public String getRoomID() {
		return roomID;
	}

	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public List<String> getParticipants() {
		return participants;
	}

	public void setParticipants(List<String> participants) {
		this.participants = participants;
	}

	@Override
	public String toString() {
		return "ChatRoom [roomID=" + roomID + ", roomName=" + roomName + ", part_count" + participants.size() +  "]";
	}
	
}
