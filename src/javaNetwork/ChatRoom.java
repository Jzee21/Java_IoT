package javaNetwork;

public class ChatRoom {
	int roomID;
	String roomName;
	
	ChatRoom() {}
	
	ChatRoom(ChatRoom room) {
		this.roomID = room.getRoomID();
		this.roomName = room.getRoomName();
	}
	
	public ChatRoom(int roomID, String roomName) {
		this.roomID = roomID;
		this.roomName = roomName;
	}
	
	public int getRoomID() {
		return roomID;
	}
	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
}
