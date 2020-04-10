package javaThread;

class MyRunnable implements Runnable {
	@Override
	public void run() {
		for (int i = 1; i <= 10; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("[i] : " + i);
		}
	}
}

class SyncRunnable implements Runnable {
	@Override
	public void run() {
//		synchronized (mutex) {
//			
//		}
	}
}

public class Thread_State {
	
	/*
	 * class 안에 class가 존재하는 inner class 형태로 class를 정의할 수 있다
	 * >> Android에서는 흔한일  (Java 프로그램에서는 되도록 지양
	*/
	public static void main(String[] args) {
		/*
		 * main method를 호출하는 주체 : main thread
		 * JVM이 하나의 Thread(main)를 내부적으로 생성하고
		 * static method인 main을 호출해서 프로그램이 시작
		 * 
		 * 별도의 Thread 생성
		 * 1. Thread class를 상속받으면서 class를 define하여 생성 후 실행
		 * 		(상속개념을 사용하기 때문에 객체사용에 제한)
		 * 2. Runnable interface를 구현한 객체를 만들고
		 *    이 객체를 가지고 Thread를 생성
		*/
		
		MyRunnable runnable = new MyRunnable();
		Thread t = new Thread(runnable);
		t.start();
		
	}

}
