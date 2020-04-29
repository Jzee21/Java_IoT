package my_procon;

public class Producer implements Runnable {

	private SharedTable table;
	
	Producer(SharedTable table) {
		this.table = table;
	}
	
	@Override
	public void run() {
		table.put();
	}
	
}
