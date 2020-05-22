package javaNetwork.multiChat;

import java.util.Date;
import java.util.LinkedList;

public class LogService {
	
	private LinkedList<String> loglist = new LinkedList<String>();
//	public Date date = new Date(System.currentTimeMillis());		// Test for 'Creation time'
	
	// Singleton
	private LogService() {
//		System.out.println("run - LogService()");
	}
	
	private static class InstanceHandler {
		public static final LogService INSTANCE = new LogService();
	}
	
	public static LogService getInstance() {
		return InstanceHandler.INSTANCE;
	}
	
	// methods
	public synchronized void addLog(String log) {
		this.loglist.add(log);
		notify();
	}
	
	public synchronized String getLog() {
		String result = null;
		
		if(this.loglist.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		} else {
			result = loglist.removeFirst();
		}
		
		return result;
	}
	
	public void throwsInterrupt() {
		try {
			throw new InterruptedException();
		} catch (InterruptedException e) {
		}
	}

}
