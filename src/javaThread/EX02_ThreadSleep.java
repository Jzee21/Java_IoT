package javaThread;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class EX02_ThreadSleep extends Application {
	
	private TextArea ta;
	private Button btn;

	private void addMsg(String msg) {
		Platform.runLater(() -> {
			ta.appendText(msg);
		});
	}
	
	// 5개의 Thread가 각각 1초마다 숫자를 찍는다
	private void runThread() {
		for (int i = 0; i < 3; i++) {
			Thread t = new Thread(() -> {
				for (int j = 1; j <= 3; j++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					addMsg(Thread.currentThread().getName() + "] " + j + "\n");
				}
			});
			t.start();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage) throws Exception {

		BorderPane root = new BorderPane();
		root.setPrefSize(700, 500);
		
		ta = new TextArea();
		root.setCenter(ta);
		
		btn = new Button("Button Click");
		btn.setPrefSize(250, 50);

		btn.setOnAction((e) -> {
			// addMsg("Button Clicked!\n");
			runThread();
		});
		
		FlowPane flowpane = new FlowPane();
		flowpane.setPrefSize(700, 50);
		flowpane.getChildren().add(btn);
		
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
