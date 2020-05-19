package javaNetwork.multiChat;

import java.util.Date;

public class TestSingleton {

	public static void main(String[] args) throws InterruptedException {

		System.out.println(new Date(System.currentTimeMillis()).toString());
		// Tue May 19 21:41:10 KST 2020
		
		Thread.sleep(5000);
		
		LogService target = LogService.getInstance();
		// Singleton field value - Creation time
		
		Thread.sleep(5000);
		
//		System.out.println(target.date.toString());
		// Tue May 19 21:41:15 KST 2020
		
		
		
	}

}
