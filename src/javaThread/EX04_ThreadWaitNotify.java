package javaThread;

class EX04_SharedObject {
	
	private Object monitor = new Object();
	
	public synchronized void printNum() {
		for (int j = 0; j < 10; j++) {
			System.out.println(j + " : " + Thread.currentThread().getName());
			try {
				Thread.sleep(1000);
				notify();	// wait()으로 Blocked된 Thread를 깨움
				wait();		// monitor 강제 반납 >> go to Blocked
			} catch (Exception e) {
				e.printStackTrace();
			}
		} // for
	}
	
}

class EX04_Runnable implements Runnable {
	
	private EX04_SharedObject obj;
	
	EX04_Runnable() { }
	EX04_Runnable(EX04_SharedObject obj) {
		this.obj = obj;
	}
	
	@Override
	public void run() {
//		for (int j = 0; j < 10; j++) {
//			System.out.println(j + " : " + Thread.currentThread().getName());
//			try {
//				Thread.sleep(1000);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		} // for
		this.obj.printNum();
	}
}

/* 객체가 가지는 method를 호출할 때
 * 일반적으로 blocking method를 사용한다. (메서드의 수행이 다 끝나야 다음 라인 수행)(순차처리)
 * 
 * Thread.start() : nonblocking method : 수행 결과를 기다리지 않아
 */

/* 특수한 method를 이용해 Thread 실행 순서를 제어할 수 있다
 * wait(), notify(), notifyAll()	>> 반드시 Critical Section(임계영역)에서만 사용 가능하다
 * 									>> 동기화 코드가 적용된 부분
 */
public class EX04_ThreadWaitNotify {
	
	public static void main(String[] args) {
//		EX04_Runnable r1 = new EX04_Runnable();
		EX04_Runnable r1 = new EX04_Runnable(new EX04_SharedObject());
		
		Thread t1 = new Thread(r1);
		Thread t2 = new Thread(r1);
		t1.start();
		t2.start();
	}

}
