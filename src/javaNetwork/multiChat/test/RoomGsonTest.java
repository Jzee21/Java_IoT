package javaNetwork.multiChat.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javaNetwork.multiChat.ChatMessage;
import javaNetwork.multiChat.ChatRoom;
import javafx.scene.control.TextArea;

public class RoomGsonTest {

	public static void main(String[] args) {
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
		
		ChatRoom room1 = new ChatRoom("A");
		room1.addParticipants("P1");
		room1.addParticipants("P2");
		room1.addParticipants("P3");
		room1.addParticipants("P3");
		
		String jsonDate = gson.toJson(room1);
		System.out.println(jsonDate);
		
		ChatRoom jsonRoom = gson.fromJson(jsonDate, ChatRoom.class);
		
		System.out.println(gson.toJson(room1.getParticipants()));
		
		ChatMessage notice = new ChatMessage("UPDATE_PART", "SERVER", room1.getRoomName(), gson.toJson(room1.getParticipants()));
		
		jsonRoom.setTextarea(new TextArea());
		System.out.println(jsonRoom.toString());
		
		for(String part : jsonRoom.getParticipants()) {
			System.out.println(part);
		}
	}

}
