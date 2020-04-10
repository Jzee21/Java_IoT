package javaThread.procon;

public class ProConTest {

	public static void main(String[] args) {
		
		SharedObject obj = SharedObject.getInstance();
		
		Thread producer = new Thread(new Producer(obj));
		Thread con1 = new Thread(new Consumer(obj));
		Thread con2 = new Thread(new Consumer(obj));
		Thread con3 = new Thread(new Consumer(obj));
		
		con1.start();
		con2.start();
//		con3.start();
		
		producer.start();
		
		try {
			Thread.sleep(1);
			producer.interrupt();
			
			Thread.sleep(1);
			con1.interrupt();
			con2.interrupt();
//			con3.interrupt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
