package javaThread.procon;

public class Producer implements Runnable {
	
	private SharedObject table;
	
	Producer() {}
	
	public Producer(SharedObject table) {
		this.table = table;
	}
	
	@Override
	public void run() {
		System.out.println("producer start");
		int i = 1;
		
		while (!Thread.currentThread().isInterrupted()) {
//			if(Thread.currentThread().isInterrupted()) {
//				break;
//			}
			this.table.put(new Integer(i++).toString());
			
		}
		System.out.println("producer end");
	}
}
