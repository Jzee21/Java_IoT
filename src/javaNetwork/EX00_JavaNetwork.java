package javaNetwork;

/*	Network	: 유선, 무선을 이용하여 데이터를 통신하기 위해
 * 			 컴퓨터를 논리적, 물리적으로 묶어놓은 형태
 * 
 * 	Network의 형태 (크기에 따라 여러가지 형태로 구분)
 * 	1. LAN(Local Area Network)
 * 	2. WAN(Wide Area Network) 
 * 	3. MAN(Metropolitan Area Network)
 * 
 * 	Internet : Network of Network
 * 				물리적인 네트워크의 형태
 * 
 *  Internet을 사용하기 위해서는 그 위에서 동작하는 Service가 있어야한다
 *  가장 대표적인 서비스 : Web, Email, Streaming, ...
 *  
 *  Internet 위에서 각자의 서비스가 동작하려면
 *  각 컴퓨터들이 서로를 인지할 수 있는 수단이 필요하다 (주소)
 *  NIC(Network Interface Card) 마다 IP Address 부여
 *  
 *  - 논리적 주소
 *  	IPv4 : xxx.xxx.xxx.xxx		(모바일, 주소 부족)
 *  	IPv6
 *  
 *  - 물리적 주소
 *  	MAC Address
 *  
 *  IP Address는 숫자로 구성되어 기억하기 어렵다
 *  DNS(Domain Name System)의 도입		- www.naver.com
 *  
 *  IP Address를 알아야 통신하고자 하는 상대 컴퓨터를 인지할 수 있다
 *  + 통신을 위해서 Protocol이 필요하다.
 *  TCP, IP, ARP, TELNET, FTP, HTTP .....
 *  
 *  Port	: 컴퓨터 내에서 동작하고 있는 프로그램을 지칭하는 숫자 
 *  - 0~65535의 범위를 갖는 숫자
 *  - 0~1023 까지는 예약되어있다
 *  
 *  * 한 컴퓨터와 다른 컴퓨터가 데이터를 주고받기 위해서는
 *    1. protocol
 *    2. IP Address
 *    3. Port 번호
 *  
 *  Socket
 *  복잡한 네트워크 처리를 Socket이 알아서 처리하도록
 *  
 *  Java Network 프로그램은 CS 구조를 갖는다(Client - Server 구조)
 *  
 *  
 *  
*/
public class EX00_JavaNetwork {
	
	public static void main(String[] args) {
		
	}

}
