package javaThread;

class EX03_MyRunnable implements Runnable {
	
	private EX03_SharedObject obj;
	private int number;
	
	EX03_MyRunnable () {}
	EX03_MyRunnable (EX03_SharedObject obj, int number) {
		this.obj = obj;
		this.number = number;
	}
	
	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName() + " - " + this.number);
		this.obj.setNumber(this.number);		
	}
}

/* [ 공유객체 ]
 * 일반적으로 공유객체는 class로부터 객체가 단 1개만 생성되는 형태로 만들어진다. (Singleton)
 * */
class EX03_SharedObject {
	private int number;
	private Object monitor = new Object();	// monitor 객체 : 표식 용도
	
	// singleton
//	private static SharedObject obj = new SharedObject();
//	private SharedObject() { }
//	public static SharedObject getInstance() {
//		return this.obj;
//	}

	public int getNumber() {
		return number;
	}

	// Problem
	// 데이터의 안정성, 신뢰성 보장 X
//	public void setNumber(int number) {
//		this.number = number;
//		try {
//			Thread.sleep(1000);
//			System.out.println("after " + Thread.currentThread().getName() + " : " + this.getNumber());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	// 100, 100 or 200, 200
	
	// sol_1. 순차처리
	/* 동기화(synchronized) - 공유객체의 접근을 제한하여 순차처리 유도
	 * method 자체가 동가화 처리되어 프로그래밍 하기는 쉬우나
	 * 해당 method의 실행이 오래 걸린다면 다수의 method 호출 시 시간지연 (순차처리)
	 */
//	public synchronized void setNumber(int number) {
//		this.number = number;
//		try {
//			Thread.sleep(1000);
//			System.out.println("after " + Thread.currentThread().getName() + " : " + this.getNumber());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	// (1s) 100 (1s) 200
	// (1s) 200 (1s) 100
	
	// sol_2. 
	/* method 전체 동기화가 아닌, 필요한 부분만 동기화 처리한다.
	*/
	public void setNumber(int number) {
		// synchronized (this)
		synchronized (this.monitor) {
			this.number = number;
			try {
				Thread.sleep(1000);
				System.out.println("after " + Thread.currentThread().getName() + " : " + this.getNumber());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
		
}

public class EX03_ThreadSync {
	
	
	public static void main(String[] args) {
		EX03_SharedObject obj = new EX03_SharedObject();
		
		EX03_MyRunnable r1 = new EX03_MyRunnable(obj, 100);
		EX03_MyRunnable r2 = new EX03_MyRunnable(obj, 200);
		
		Thread t1 = new Thread(r1);
		Thread t2 = new Thread(r2);
		t1.start();
		t2.start();
	}

}