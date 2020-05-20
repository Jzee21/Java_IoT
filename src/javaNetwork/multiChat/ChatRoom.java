package javaNetwork.multiChat;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.TextArea;

public class ChatRoom {
	
	private String roomName;
	private TextArea textarea;
	private List<String> participants;
	
	// constructor
	public ChatRoom() {
		this(null);
	}
	
	public ChatRoom(String roomName) {
		this.roomName = roomName;
		this.textarea = null;
		this.participants = new ArrayList<String>();
	}
	
	// get, set
	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public TextArea getTextarea() {
		return textarea;
	}

	public void setTextarea(TextArea textarea) {
		this.textarea = textarea;
	}

	public List<String> getParticipants() {
		return participants;
	}

	public void setParticipants(List<String> participants) {
		this.participants = participants;
	}

	@Override
	public String toString() {
		return "ChatRoom [roomName=" + roomName + ", part_count" + participants.size() +  "]";
	}
	
}
