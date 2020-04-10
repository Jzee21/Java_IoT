package javaThread.procon;

import java.util.LinkedList;

import com.sun.marlin.stats.Monitor;

/* 공용객체
 * Thread가 굥유하는 자료구조로 단 하나의 객체만 필요하다  - Singleton
 * 단 하나의 객체를 여러 Thread가 공유한다.
 * */
public class SharedObject {
	
	private static final Object MONITOR = new Object();
	private static SharedObject instance;
	
	private LinkedList<String> list;
	
	private SharedObject() {
		this.list = new LinkedList<String>();
	}
	
	public static SharedObject getInstance() {
		if (instance == null) {
			instance = new SharedObject();
		}
		return instance;
	}

	// Business method		// Thread에 의해 공용으로 사용되는 method
	// Producer
	public void put(String s) {		
		synchronized (MONITOR) {
			list.add(s);
//			System.out.println("add " + s);
			MONITOR.notify();
		}
	}
	
	// Consumer
	public String pop() {
		String result = null;
		
		synchronized (MONITOR) {			
			try {
//				if (list.isEmpty()) {
//					System.out.println(Thread.currentThread().getName() + " before wait()");
//					MONITOR.wait();
//					System.out.println(Thread.currentThread().getName() + " after wait() " + result);
//				} else {
//					result = list.removeFirst();
////					notify();
//				}
				while(list.isEmpty()) {
					MONITOR.wait();
				}
				result = list.removeFirst();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
//		System.out.println(Thread.currentThread().getName() + " return " + result);
		return result;
	}
}
