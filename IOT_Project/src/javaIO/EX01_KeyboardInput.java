package javaIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 * Java I/O (Input / Output)
 * Stream을 이용하서 처리한다.
 * 
 * Stream : data를 받아들이고 보낼 수 있는 통로
 * 
 * Java 표준 출력(Dos)에 문자열을 출력하기 위해서는
 * Java 프로그램과 도스창 사이에 연결된 Stream이 존재해야 한다.
 * 
 * Stream은 객체로 존재한다.
 * 2가지 종류 (가장 기본적인)
 * - InputStream		: public abstract class InputStream extends org.omg.CORBA.portable.InputStream
 * - OutputStream		: public abstract class OutputStream extends java.io.OutputStream
 * >> 성능이 상당히 좋지 않아...
 * 
 * 
 * Stream은 결합해서 보다 좋은 Stream을 만들 수 있다.**
 * 
 * 
*/
public class EX01_KeyboardInput {

	public static void main(String[] args) {
		System.out.println("소리없는 아우성!");
		/* System.out	: Dos창과 연결된, 미리 제공된 Stream
		 * Stream이 가지는 println() 메서드를 이용해서 Dos창에 전달
		 * 
		 * System.out 	: public final static PrintStream out = null;
		*/
		
		/* 도스창에서 문자열을 입력받을 수 있다
		 * 기본적으로 InputStream이 있어야 데이터를 받을 수 있다
		 * System.in -> 도스창과 연결된 InputStream
		 * 	- 효율이 좋지 않고 문자열을 읽어들이기에 좋지 않다.
		 *  - 문자열을 입력받기 좋은 InputStreamReader로 업그레이드
		*/
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		
		// BufferedReader 를 이용하면 readLine() method를 이용할 수 있다.
		try {
			String msg = br.readLine();
			System.out.println("get : " + msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
