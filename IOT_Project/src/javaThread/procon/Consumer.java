package javaThread.procon;

public class Consumer implements Runnable {
	
	private SharedObject table;
	
	Consumer() {}
	
	public Consumer(SharedObject table) {
		this.table = table;
	}
	
	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName() + " start");
		
		while (!Thread.currentThread().isInterrupted()) {
//			if(Thread.currentThread().isInterrupted()) {
//				break;
//			}
			System.out.println(Thread.currentThread().getName() + " - " + this.table.pop());
		}
		
		System.out.println(Thread.currentThread().getName() + " end");
	}
}
