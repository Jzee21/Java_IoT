package javaNetwork.multiChat;

import java.util.LinkedList;

public class LogService {
	
	private LinkedList<String> loglist = new LinkedList<String>();
	
	// Singleton
	private LogService() {}
	
	private static class InstanceHandler {
		public static final LogService INSTANCE = new LogService();
	}
	
	public static LogService getInstance() {
		return InstanceHandler.INSTANCE;
	}
	
	// methods
	public synchronized void add(String log) {
		this.loglist.add(log);
		this.notify();
	}
	
	public synchronized String get() {
		
		while(this.loglist.isEmpty()) {
			try {
				this.wait();
			} catch (Exception e) {
			}
		}
		
		return this.loglist.removeFirst();
	}

}
