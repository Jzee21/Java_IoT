package javaNetwork;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

import javafx.scene.control.TextArea;

public class Jzee_ClientRoomTest {

	public static void main(String[] args) {
		Gson gson = new Gson();
		
//		Client c01 = new Client("A");
//		Client c02 = new Client("B");
//		Client c03 = new Client("C");
//		Client c04 = new Client("D");
//		Client c05 = new Client("E");
//		
//		Room room01 = new Room("01");
//		Room room02 = new Room("02");
//		Room room03 = new Room("03");
//		
//		room01.getPartlist().add(c01.getUserID());
//		c01.getRoomlist().add(room01.getRoomID());
//		room01.getPartlist().add(c02.getUserID());
//		c02.getRoomlist().add(room01.getRoomID());
//		room01.getPartlist().add(c03.getUserID());
//		c03.getRoomlist().add(room01.getRoomID());
//		
//		room02.getPartlist().add(c04.getUserID());
//		c04.getRoomlist().add(room02.getRoomID());
//		room02.getPartlist().add(c05.getUserID());
//		c05.getRoomlist().add(room02.getRoomID());
//		
//		room03.getPartlist().add(c02.getUserID());
//		c02.getRoomlist().add(room03.getRoomID());
//		room03.getPartlist().add(c03.getUserID());
//		c03.getRoomlist().add(room03.getRoomID());
//		room03.getPartlist().add(c04.getUserID());
//		c04.getRoomlist().add(room03.getRoomID());
//		
//		System.out.println(gson.toJson(room01));
//		System.out.println(gson.toJson(room02));
//		System.out.println(gson.toJson(room03));
//		System.out.println();
//		System.out.println(gson.toJson(c01));
//		System.out.println(gson.toJson(c02));
//		System.out.println(gson.toJson(c03));
//		System.out.println(gson.toJson(c04));
//		System.out.println(gson.toJson(c05));
		
//		{\"roomID\":935044096,\"roomName\":\"01\",\"partlist\":[780237624,205797316,1128032093]}
//		{\"roomID\":396180261,\"roomName\":\"02\",\"partlist\":[1066516207,443308702]}
//		{\"roomID\":625576447,\"roomName\":\"03\",\"partlist\":[205797316,1128032093,1066516207]}
//
//		{\"userID\":780237624,\"nickname\":\"A\",\"roomlist\":[935044096]}
//		{\"userID\":205797316,\"nickname\":\"B\",\"roomlist\":[935044096,625576447]}
//		{\"userID\":1128032093,\"nickname\":\"C\",\"roomlist\":[935044096,625576447]}
//		{\"userID\":1066516207,\"nickname\":\"D\",\"roomlist\":[396180261,625576447]}
//		{\"userID\":443308702,\"nickname\":\"E\",\"roomlist\":[396180261]}
		
		String sroom1 = "{\"roomID\":935044096,\"roomName\":\"01\",\"partlist\":[780237624,205797316,1128032093]}";
		String sroom2 = "{\"roomID\":396180261,\"roomName\":\"02\",\"partlist\":[1066516207,443308702]}";
		String sroom3 = "{\"roomID\":625576447,\"roomName\":\"03\",\"partlist\":[205797316,1128032093,1066516207]}";
		
		String sc01 = "{\"userID\":780237624,\"nickname\":\"A\",\"roomlist\":[935044096]}";
		String sc02 = "{\"userID\":205797316,\"nickname\":\"B\",\"roomlist\":[935044096,625576447]}";
		String sc03 = "{\"userID\":1128032093,\"nickname\":\"C\",\"roomlist\":[935044096,625576447]}";
		String sc04 = "{\"userID\":1066516207,\"nickname\":\"D\",\"roomlist\":[396180261,625576447]}";
		String sc05 = "{\"userID\":443308702,\"nickname\":\"E\",\"roomlist\":[396180261]}";
		
		
//		System.out.println((gson.fromJson(sroom1, Room.class)).toString());
//		System.out.println((gson.fromJson(sroom2, Room.class)).toString());
//		System.out.println((gson.fromJson(sroom3, Room.class)).toString());
		
		System.out.println((gson.fromJson(sc01, Client.class)).toString());
		System.out.println((gson.fromJson(sc02, Client.class)).toString());
		System.out.println((gson.fromJson(sc03, Client.class)).toString());
		System.out.println((gson.fromJson(sc04, Client.class)).toString());
		System.out.println((gson.fromJson(sc05, Client.class)).toString());
	}

}

class Client {
	private int userID;
	private String nickname;
	private TextArea ta;
	
	public Client(String nickname) {
		this.userID = this.hashCode();
		this.nickname = nickname;
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

	@Override
	public String toString() {
		return "Client [userID=" + userID + ", nickname=" + nickname + ", ta=" + ta + "]";
	}

}

class Room {
	private int roomID;
	private String roomName;
	private List<Client> partlist;
	
	public Room(String roomName) {
		this.roomName = roomName;
		this.roomID = this.hashCode();
		this.partlist = new ArrayList<Client>();
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

	public List<Client> getPartlist() {
		return partlist;
	}

	@Override
	public String toString() {
		return "Room [roomID=" + roomID + ", roomName=" + roomName + ", partlist=" + partlist + "]";
	}
	
}

// ===========================================
//class Client {
//	private int userID;
//	private String nickname;
//	private Socket socket;
//	private List<Integer> roomlist;
//	
//	public Client(String nickname) {
//		this.userID = this.hashCode();
//		this.nickname = nickname;
//		this.roomlist = new ArrayList<Integer>();
//	}
//
//	public int getUserID() {
//		return userID;
//	}
//
//	public void setUserID(int userID) {
//		this.userID = userID;
//	}
//
//	public String getNickname() {
//		return nickname;
//	}
//
//	public void setNickname(String nickname) {
//		this.nickname = nickname;
//	}
//
//	public List<Integer> getRoomlist() {
//		return roomlist;
//	}
//
//	@Override
//	public String toString() {
//		return "Client [userID=" + userID + ", nickname=" + nickname + ", socket=" + socket + ", list=" + roomlist + "]";
//	}
//	
//}
//
//class Room {
//	private int roomID;
//	private String roomName;
//	private List<Integer> partlist;
//	
//	public Room(String roomName) {
//		this.roomName = roomName;
//		this.roomID = this.hashCode();
//		this.partlist = new ArrayList<Integer>();
//	}
//
//	public int getRoomID() {
//		return roomID;
//	}
//
//	public void setRoomID(int roomID) {
//		this.roomID = roomID;
//	}
//
//	public String getRoomName() {
//		return roomName;
//	}
//
//	public void setRoomName(String roomName) {
//		this.roomName = roomName;
//	}
//
//	public List<Integer> getPartlist() {
//		return partlist;
//	}
//
//	@Override
//	public String toString() {
//		return "Room [roomID=" + roomID + ", roomName=" + roomName + ", partlist=" + partlist + "]";
//	}
//	
//}