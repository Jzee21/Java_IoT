package javaThread;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class EX01_ThreadBasic extends Application {
	
	private TextArea ta;
	private Button btn;

	private void printMSG(String msg) {
//		Platform.runLater(new Runnable() {
//			@Override
//			public void run() {
//				System.out.println(Thread.currentThread().getName());
//				ta.appendText(msg + "\n");
//			}
//		});
		
		Platform.runLater(() -> {
			System.out.println(Thread.currentThread().getName());
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
		
		btn = new Button("Button Click");
		btn.setPrefSize(250, 50);
		btn.setOnAction((e) -> {
			printMSG("Button Clicked!");
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
		// 현재 실행중인 Thread의 이름 출력
		System.out.println(Thread.currentThread().getName());
		// main
		launch();
		System.out.println(Thread.currentThread().getName() + " end");
		// 안나와요...
	}
}
