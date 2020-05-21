package javaNetwork.multiChat;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ChatService {
	
	private Map<String, ChatClient> connections = new ConcurrentHashMap<String, ChatClient>();
	private Map<String, ChatRoom> chatrooms = new ConcurrentHashMap<String, ChatRoom>();
//	private List<String> nicknames = new ArrayList<String>();
	
	private LogService logService = LogService.getInstance();
	private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
	
	// Singleton
	private ChatService() {
		this.chatrooms.put("A", new ChatRoom("A"));
		this.chatrooms.put("B", new ChatRoom("B"));
		this.chatrooms.put("C", new ChatRoom("C"));
		this.chatrooms.put("D", new ChatRoom("D"));
		this.chatrooms.put("E", new ChatRoom("E"));
	}
	
	private static class InstanceHandler {
		public static final ChatService INSTANCE = new ChatService();
	}
	
	public static ChatService getInstnace() {
		return InstanceHandler.INSTANCE;
	}
	
	public Gson getGson() {
		return gson;
	}

	// methods
	public ChatClient addClient(String nickname, Socket socket) {
		ChatClient client = null;
		if((client = connections.get(nickname.toUpperCase())) == null) {
			client = new ChatClient(nickname, socket);
			this.connections.put(client.getNickname().toUpperCase(), client);
		} else {
			client.setSocket(socket);
		}
		logService.addLog("[" + nickname + "][" + socket.getInetAddress().toString() + "] connected");
		return client;
	}
	
	public synchronized void removeAll() {
		for(String key : connections.keySet()) {
			ChatClient client = connections.get(key);
			client.close();
			connections.remove(key);
		}
	}
	
	public List<String> getRoomList() {
		List<String> roomNameList = new ArrayList<String>(chatrooms.keySet());
		Collections.sort(roomNameList);
		return roomNameList;
	}
	
	
	public void messageHandler(ChatClient from, ChatMessage data) {
		
		logService.addLog("[" + from.getNickname() + "] " + data.getStringData());
		switch (data.getCode().toUpperCase()) {
		case "MESSAGE":
			/*	Message to room participants	*/
			// data ["MESSAGE", "nickname", "roomName", "message"]
			for (String key : connections.keySet()) {
				ChatClient client = connections.get(key);
				client.send(data);
			}
			break;

		case "ROOMLIST":
			// data ["MESSAGE", "nickname", "roomName", "message"]
			from.send(new ChatMessage("ROOMLIST", "SERVER", from.getNickname(), gson.toJson(getRoomList())));
			break;

		case "NEW_ROOM":
			/*	Create a new chat room,
				Add owner to user list		*/
			// data ["MESSAGE", "nickname", "roomName", "message"]
			ChatRoom newRoom = new ChatRoom(data.getStringData());
			newRoom.addParticipants(from.getNickname());
			this.chatrooms.put(newRoom.getRoomName().toUpperCase(), newRoom);
			
			/* Update the list of chat rooms for all users	*/
			for (String key : connections.keySet()) {
				ChatClient client = connections.get(key);
				client.send(new ChatMessage("ROOMLIST", "SERVER", from.getNickname(), gson.toJson(getRoomList())));
			}
			
			/*	Send the new chat room information to the owner		*/
			from.send(new ChatMessage("NEW_ROOM", from.getNickname(), data.getUserID(), gson.toJson(newRoom)));
			break;

		case "ENTER_ROOM":
			// data ["ENTER_ROOM", "nickname", "roomName", null]
			
			
			break;

		case "EXIT_ROOM":
			// data ["EXIT_ROOM", "nickname", "roomName", "@EXIT"]
			
			
			break;
			
		case "":
			// data ["", "nickname", null, null]
			
			
			break;

		default:
			break;
		}
		
//		for (String key : connections.keySet()) {
//			ChatClient client = connections.get(key);
//			client.send(data.getStringData());
//		}
		
	}

} // ChatService
