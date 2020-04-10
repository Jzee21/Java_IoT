package my_procon;

import java.util.ArrayList;
import java.util.LinkedList;

public class SharedTable {
	
	private static SharedTable instance = new SharedTable();
	
	private ArrayList<Integer> table;
	private int size = 10;
	private int seq = 0;
	
	
	private SharedTable() {
		this.size = 10;
		this.setTable();
	}
	
	private SharedTable(int size) {
		this.size = size;
		this.setTable();
		
	}
	
	public static SharedTable getInstance() {
		return instance;
	}
	
	// custom method
	public synchronized void put() {
		while (this.table.size() >= this.getSize()) {
			System.out.println("Full");
//			printTable();
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//return;
		}
		notify();  // ??
//		System.out.println(Thread.currentThread().getName() + " : put() " + this.seq);
		this.table.add(++this.seq);
		System.out.println("put() " + this.seq);
		
	}
	
	public synchronized int get() {
		while (this.table.size() <= 0) {
			System.out.println("Empty");
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// return 0;
		}
		notify();  // ??
		int result = this.table.get(0);
		this.table.remove(0);
		return result;
	}
	
	// test code
	public int getTableSize() {
		return this.table.size();
	}
	
	public synchronized void printTable() {
		String result = "[ ";
		for (int i = 0; i < this.table.size(); i++) {
			result += this.table.get(i) + " " ;
		}
		result += "]";
		System.out.println(result);
	}
	
	
	// getter / setter
	private void setTable() {
		this.table = new ArrayList<Integer>();
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	

}
