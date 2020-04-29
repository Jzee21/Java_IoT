package my_procon;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

class ViewRunnable implements Runnable {

	private SharedTable table;
	
	ViewRunnable(SharedTable table) {
		this.table = table;
	}
	
	@Override
	public void run() {
		table.printTable();
	}
}

public class TestMain extends Application {
	
	private TextArea ta;
	private Button putBtn, getBtn, viewBtn;
	private Thread currentThread;
	

	private void printMsg(String msg) {
		Platform.runLater(() -> {
			ta.appendText(msg + "\n");
		});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		System.out.println(Thread.currentThread().getName());
		// JavaFX Application Thread

		BorderPane root = new BorderPane();
		root.setPrefSize(700, 500);
		
		ta = new TextArea();
		root.setCenter(ta);
		
		putBtn = new Button("Put");
		putBtn.setPrefSize(200, 50);
		putBtn.setOnAction((e) -> {
			//
			new Thread(new Producer(SharedTable.getInstance())).start();
		});
		
		getBtn = new Button("Get");
		getBtn.setPrefSize(200,  50);
		getBtn.setOnAction((e) -> {
			//
			new Thread(new Consumer(SharedTable.getInstance())).start();
		});
		
		viewBtn = new Button("View");
		viewBtn.setPrefSize(200,  50);
		viewBtn.setOnAction((e) -> {
			//
			new Thread(new ViewRunnable(SharedTable.getInstance())).start();
		});
		
		FlowPane flowpane = new FlowPane();
		flowpane.setPrefSize(700, 50);
		//flowpane.getChildren().add(startBtn);
		flowpane.getChildren().addAll(putBtn, getBtn, viewBtn);
		
		root.setBottom(flowpane);
		
		//
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("예제용 JavaFX");
		primaryStage.setOnCloseRequest(e -> {
			System.exit(0);
		});
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch();
	}
	
}

//class TestRunnable implements Runnable {
//	
//	private SharedTable table;
//	
//	TestRunnable(SharedTable table) {
//		this.table = table;
//	}
//	
//	@Override
//	public void run() {
//		for (int i = 0; i < 10; i++) {
//			try {
//				table.put();
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}		
//	}
//}

//public class TestMain {
//
//	public static void main(String[] args) {
//		//
//		SharedTable table = new SharedTable();
//		System.out.println(table.getSize());
//		
//		TestRunnable r = new TestRunnable(table);
//		Thread t1 = new Thread(r);
//		Thread t2 = new Thread(r);
//		t1.start();
//		t2.start();
//		
//		try {
//			Thread.sleep(2000);
//			for (int i = 0; i < 15; i++) {
//				System.out.println("main get() " + table.get());
//			}
//			Thread.sleep(1000);
//			table.printTable();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
////		ArrayList<Integer> list = new ArrayList<Integer>();
////		list.add(1);
////		list.add(2);
////		list.add(3);
////		
////		System.out.println(list.size() + ", " + list.get(0));
////		list.remove(0);
////		System.out.println(list.size() + ", " + list.get(0));
//		
////		table.get();
////		table.put();
////		table.put();
////		table.put();
////		table.put();
////		table.put();
////		table.put();
////		table.put();
////		table.put();
////		table.put();
////		table.put();
////		System.out.println(table.getTableSize());
////		table.put();
//		
//	}
//
//}
