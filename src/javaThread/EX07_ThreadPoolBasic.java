package javaThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/*
 * Java		필요한 객체 생성(new) -> Heap 영역에 메모리 할당
 * 			메모리를 이용해서 여러가지 처리 (데이터 저장, 메서드를 이용한 로직 처리)
 * 			-> GC(Garbage Collector)에 의한 메모리 반환
 * 		! 객체의 생성과 메모리 반환에는 생각보다 많은 시간이 소요된다
 * 
 * Pool 개념
 * 	사용할 객체를 미리 많이 생성해서 Pool이라 불리는 영역에 모아둔다
 * 	객체가 필요할 때, Pool에서 객체를 가져다 사용하고 사용 후에는 Pool에 반납한다
 * 
 *  대표적인 활용 : 	Database Connection Pool(DBCP)
 *  			Object Pool
 *  			Thread Pool
 *  
 * Thread를 사용할 때, Thread t = new Thread();		// 생성 및 반환에 시간이 소요
 * 
 * ExecutorService라는 이름의 Thread Pool을 이용해보자.
*/
public class EX07_ThreadPoolBasic extends Application {
	
	private TextArea ta;
	private Button initBtn, startBtn, shutdownBtn;
	
	private ExecutorService executorService;
	// Thread Pool class

	// print at TextArea ta
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
		
		// initBtn, startBtn, shutdownBtn
		initBtn = new Button("Thread Pool 생성");
		initBtn.setPrefSize(230,  50);
		initBtn.setOnAction((e) -> {
			//
//			executorService = Executors.newFixedThreadPool(5);
			executorService = Executors.newCachedThreadPool();
			this.printMsg("Pool size : " + ((ThreadPoolExecutor)executorService).getPoolSize());
		});
		
		startBtn = new Button("Thread 생성");
		startBtn.setPrefSize(230, 50);
		startBtn.setOnAction((e) -> {
			// Thread Pool에서 10개의 Thread를 가져다 사용한다.
			for (int i = 0; i < 10; i++) {
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						String msg = "Pool size : " + ((ThreadPoolExecutor)executorService).getPoolSize();
						msg += ", Thread name : " + Thread.currentThread().getName();
						printMsg(msg);
						try {
							Thread.sleep(1000);
						} catch (Exception e2) {
							// TODO: handle exception
						}
					}
				};
				executorService.execute(runnable);
				// new Thread()로 Thread를 생성하는 것보다
				// Thread Pool을 이용하는 것이 일반적이다.
			}
		});
		
		shutdownBtn = new Button("Thread Pool 종료");
		shutdownBtn.setPrefSize(230,  50);
		shutdownBtn.setOnAction((e) -> {
			// Thread Pool을 다 사용하고 더이상 사용하지 않으면 종료해주어야 한다.
			executorService.shutdownNow();
		});
		
		FlowPane flowpane = new FlowPane();
		flowpane.setPrefSize(700, 50);
		flowpane.getChildren().addAll(initBtn, startBtn, shutdownBtn);
		root.setBottom(flowpane);
		
		//
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("예제용 JavaFX");
		primaryStage.setOnCloseRequest(e -> {
			// System.exit(0);
		});
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch();
	}
	
}
