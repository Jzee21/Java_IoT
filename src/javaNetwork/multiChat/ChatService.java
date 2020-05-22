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
		
		logService.addLog("[" + from.getNickname() + "] " + data.getCode());
		logService.addLog(data.toString());
		switch (data.getCode().toUpperCase()) {
		case "MESSAGE":
			// data ["MESSAGE", "nickname", "roomName", "message"]
			/*	Message to room participants	*/
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
			// data ["MESSAGE", "nickname", "roomName", "message"]
			/*	Create a new chat room,
				Add owner to user list		*/
			ChatRoom newRoom = new ChatRoom(data.getDestID());
			newRoom.addParticipants(from.getNickname());
			this.chatrooms.put(newRoom.getRoomName().toUpperCase(), newRoom);
			
			logService.addLog("새 방 받아라 1");
			/* Update the list of chat rooms for all users	*/
			for (String key : connections.keySet()) {
				ChatClient client = connections.get(key);
				client.send(new ChatMessage("ROOMLIST", "SERVER", newRoom.getRoomName(), gson.toJson(getRoomList())));
			}
			
			logService.addLog("새 방 받아라 2");
			/*	Send the new chat room information to the owner		*/
			from.send(new ChatMessage("NEW_ROOM", "SERVER", data.getUserID(), gson.toJson(newRoom)));
			logService.addLog("새 방 받아라 3");
			
			break;

		case "ENTER_ROOM":
			// data ["ENTER_ROOM", "nickname", "roomName", null]
			/*	New participants enter the chat room		*/
			ChatRoom enterRoom = this.chatrooms.get(data.getDestID().toUpperCase());
			if(enterRoom != null) {
				/*	Add new participants to the chat room	*/
//				System.out.println(data.getUserID());
				enterRoom.addParticipants(data.getUserID());
//				enterRoom = this.chatrooms.get(data.getDestID().toUpperCase());
				logService.addLog("방 " + data.getDestID() + " 좀 달래");
				
				/*	Send room information to new participants	*/
				data.setStringData(gson.toJson(enterRoom));
				logService.addLog(gson.toJson(data));		// do not run
				from.send(data);
				logService.addLog("방 " + data.getDestID() + " 좀 달래 2");
				
				try {
					/*	Announce new participants to all participants	*/
					ChatMessage notice = new ChatMessage("UPDATE_PART", "SERVER", enterRoom.getRoomName(), gson.toJson(enterRoom.getParticipants()));
					for (String key : enterRoom.getParticipants()) {
//						System.out.println(key);
						ChatClient target = connections.get(key.toUpperCase());
						target.send(notice);
					}
					logService.addLog("방 " + data.getDestID() + " 좀 달래 3");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			break;

		case "EXIT_ROOM":
			// data ["EXIT_ROOM", "nickname", "roomName", "@EXIT"]
			
			
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
