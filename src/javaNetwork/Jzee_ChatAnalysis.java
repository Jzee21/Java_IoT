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
	/*	ConcurrentHashMap<Key, Object>
	 * 	- Multi-Thread 환경에서 부분적인 Lock을 지원한다
	 * 	- putIfAbsent(Key, Value) 지원
	 *
	 *	Map<Integer, Client> connections	: key(hashCode), Room.class
	 * 	Map<Integer, Room>	 roomlist		: key(hashCode), Room.class
	*/
	
	// Server Object (Simple)
	/*
	@ Client
		int		userID
		String	nickname
		Socket	socket
		List<Integer>	list	: Keys of ConcurrentHashMap
		
	@ Room
		int 	roomID
		String	roomName
		List<Integer>	list	: Keys of ConcurrentHashMap
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
		- Before saving a connection, Make sure the nickname is being used by another client
		1) isExits() : true		(Connection not saved.	The nickname is already in use by another client)
			{serverID, null*, null*}
				
		2) isExist() : false	(Connection saved.		No one is using the nickname)
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
		
			
	
	##### 	Use/Code	Message			
	- @A	Server		Before saving a connection, 
						Make sure the nickname is being used by another client
			0x00000		{code, 0, 0, nickname}
				
	- @A1	Client		Connection not saved. [isExist(nickname) = true]
						The nickname is already in use by another client
			0x000001	{code, serverID, 0, 0}
				 
	- @A2	Client		Connection saved.	[isExist(nickname) = false]
						No one is using the nickname
			0x000002	{code, serverID, userID, {room_count, [{roomID, roomName},...]}}
		
	- @B	Server		The client requests to enter* the chat room.
			0x001000	{code, userID, roomID, 0}
	
	- @B1	Server		The client fails to access the room.
			0x001000	{code, serverID, roomID, {user_count, [{userID, 0}]}}		
			
	- @B2	Client		The client receives the participant info for the chat room.
						info : list of participants info (all - new one)(id, name)
			0x001001	{code, serverID, roomID, {client_count, [{userID, nickname}, ...]}}

	- @B3	Clients(B)	Inform new users to participants.	(Broadcast)
						info : new participants info (new one)(id, name)
			0x001002	{code, serverID, roomID, {userID, nickname}}
	
	- @C	Server		Request broadcast of messages to participants by room id
			0x001001	{code, userID, roomID, {userID, message}}
	
	- @C1	Client		The client fails to broadcast message.
			0x001001	{code, serverID, roomID, {userID, message}}
	
	- @C2	Client(B)	Receive a message from a specific chat room (Broadcast)
			0x001001	{code, serverID, roomID, {userID, message}}
	
	- @D	Server		The client requests to create a new room
			0x001001	{code, userID, 0, {roomName}
	
	- @D1	Client		New room is created
			0x001001	{code, serverID, roomID, {romID, roomName}}
	
	- @E	Server		The client disconnected or requests to finish chat.
			0x001001	null or {code, userID, roomID, {@EXIT}}
	
	- @E1	Client(B)	A participant leaves the room
			0x001001	{code, serverID, roomID, {userID}}
	
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
