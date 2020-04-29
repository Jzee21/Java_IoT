package my_procon;

public class Consumer implements Runnable {

	private SharedTable table;
	
	Consumer(SharedTable table) {
		this.table = table;
	}
	
	@Override
	public void run() {
		System.out.println("get() " + table.get());
	}
	
}
