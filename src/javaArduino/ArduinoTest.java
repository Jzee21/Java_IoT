package javaArduino;

/*	Anduino(아두이노)
 	이탈리아에서 만든 오픈소스 하드웨어
 	(강한친구, 강력한 친구, 절친한 친구란 의미)
 
 	아두이노는 컴퓨터가 아니다.
 	(OS나 응용프로그램을 설치할 수 없다)
 	
 	라떼팬더, 라즈베리파이 : 싱글보드 컴퓨터
 	(다른 보드인 라즈베리파이는 OS 설치와 응용프로그램 설치가 가능)
 	
 	아두이노는 Micro-Controller로서 이식된 코드를 수행하는 단순 기계다.
 	
 	아두이노가 각광받기 시작한 이유는 IoT와 맞물려있기 때문
 	
 	IoT(Internet of Things) 사물인터넷
 	일반적인 사물에 통신장비를 결합하고 고도의 통신기술을 이용해 특별한 작업을 수행하는 기술
 	(각종 사물에 센서와 통신기능을 결합하여 인터넷에 연결하는 기술)
 	
 	사물인터넷을 구현하기 위해 사용될 Controller 가 가져야할 조건
 	1. 가격이 저렴
 	2. 프로그램 작성과 프로그램 이식이 쉬워야한다
 	3. 센서와 엑추에이터를 쉽게 사용할 수 있어야한다
 	
 	아두이노에는 여러가지 버전이 존재
 	가장 대표적인 모델 - Uno
 	크기가 크지만 복잡한 처리를 할 수 있는 버전 - Mega
 	크기가 아주 작은 버전 - Mini
 	...
 	등등 다양한 버전이 존재
 	목적에 맞게 선택해서 사용하면 된다.
 	
 	=====================================================
 	전기
 	
 	전류와 전압
 	전압  >>  (+) -> (-) 으로 전류가 흐른다
 	
 	- 전압은 기호로 V(Voltage)를 사용		단위는 V(볼트)
 	- 전류는 기호로 I(Intensity)를 사용		단위는 A(암페어)
 	- 저항은 기호로 R(Resistance)를 사용	단위는 옴
 	
 	옴의법칙
 	- I = V / R
 	
 	저항은 전기를 열로 바꾼다
 	저항에는 색상띠를 이용해 저항 값을 표시한다
 	
 	
 	--------------------
 	센서는 주위환경에 대한 정보 수집
 	- 온도, 습도, 거리, 밝기 센서 등
 	
 	아두이노가 어떤 활동을 할 수 있도록 도와주는 전자부품
 	액츄에이터
 	- LED, 모터, 스피커
 	
 	아두이노를 제어하는 방법
 	- 아날로그
 	- 디지털
 	
 	Arduino Uno 에는 DIGITAL 헤더가 존재한다
 	총 13개 중 0, 1번은 Serial 통신에 사용되므로 사용을 자제하도록 한다.
 	
 	Arduino	: 5V			>>>		5-2(V) = 0.015A * ?R
 	LED  	: 2V, 15mA		>>>		R = 200
 	
 */
public class ArduinoTest {

}
