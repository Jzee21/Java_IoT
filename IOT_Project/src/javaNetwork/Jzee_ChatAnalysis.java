package javaNetwork;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class Jzee_ChatAnalysis {
	
	// 다중 채팅방을 지원하는 
	// Multiple-Room Chat Server & Client 요구분석
	/*
	// Server 기준
	 * 	* Request	: Client 요청
	 * 	* Response	: 요청 응답
	*/
	
	// Server Shared Data
	/*	Map<Integer, Client> connections	: key(hashCode), Room.class
	 * 	Map<Integer, Room>	 roomlist		: key(hashCode), Room.class
	*/
	
	// Server Object (Simple)
	/*
	@ Client
		int		userID
		String	nickname
		Socket	socket
		List<Integer>	list
		
	@ Room
		int 	roomID
		String	roomName
		List<Integer>	list
	*/
	
	// Message
	/*	int code;		(생략)
	 * 	int senderID;
	 * 	int targetID;
	 * 	List<Object> data(message)
	*/
	
	// Request		{senderID, targetID, Data}
	/*
	@A Connect	
		{null, null, String nickname}
		- nickname 중복체크 (1 or 2)
		1) isExits() : true
			{serverID, null*, null*}
				
		2) isExist() : false
			create new Client
			{serverID, userID, [{Room.roomID, Room.roomName}, ...]}
		
	
	@B Enter Room
		{userID, roomID, null}
		- 방 입장, broadcast (1 and 2)
		1) (roomList.get(roomID).add(Client.userID, Client.this)
			{serverID, roomID, [{(Client : Room.list)userID, nickname}, ...]}
		
		!! @E-2
		2) broadcast - (roomList.get(roomID)).broadcast(userID)
					   (roomList.get(roomID)).inClient(userID)
			{serverID, roomID, {userID, nickname}}
				
	@C Send Message
		{userID, roomID, {message}}
		- 전체 전달 (1)
		1) broadcast - (roomList.get(roomID)).broadcast(message)
			{serverID, roomID, {userID, message}
				or
			{userID, roomID, {message}}
	
	@D New Room
		{userID, null, {roomName}}
		- 방 생성, broadcast (1 and 2)
		1) new Room(roomName, userID) {
				this.roomID = this.hashCode
				this.roomName = roomName
				this.list.add(userID)
		   }
		
		2) broadcast - (roomList.get(roomID)).breadcast(roomID)
			{serverID, roomID, {roomID, roomName}}
				or
			{serverID, roomID, {roomName}}
	
	@E Disconn (Leave Room)
		null*	- disconnected
		- client.close, broadcast(1 and 2)
		1) ...
		
		!! @B-2
		2) broadcast - (roomList.get(roomID)).broadcast(userID)
					   (roomList.get(roomID)).outClient(userID)
			{serverID, roomID, {userID}}
		
	### Code
		- @A	0x000100
		- @A1	0x000101
		- @A2	0x000102
		- @
	
	
	*/
	
	// Server Object (Simple)
	/*
	@ Client
		int		userID
		String	nickname
		Socket	socket
		List<Integer>	list
		
	@ Room
		int 	roomID
		String	roomName
		List<Integer>	list
	*/	
	/*
	
	Client {
		int		userID
		String nickname
		Socket socket
		BufferedReader	input;
		PrintWriter		output;
		List<Integer>	list
		
		
		
	}
	
	
	*/
}
