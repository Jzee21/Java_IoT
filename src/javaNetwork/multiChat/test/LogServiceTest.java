package javaNetwork.multiChat.test;

import javaNetwork.multiChat.LogService;

public class LogServiceTest {

	public static void main(String[] args) {
		LogService service = LogService.getInstance();
		
		Thread t1 = new Thread(() -> {
			for (int i = 0; i < 10; i++) {
				service.add("" + i);
				try {
					Thread.sleep(700);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("t1 is end");
		});
		
		Thread t2 = new Thread(() -> {
			for (int j=0 ; j<10 ; j++) {
				System.out.println(service.get());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		t1.start();
		t2.start();

	}

}
