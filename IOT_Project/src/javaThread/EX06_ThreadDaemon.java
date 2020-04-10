package javaThread;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class EX06_ThreadDaemon extends Application {
	
	private TextArea ta;
	private Button startBtn, stopBtn;
	private Thread currentThread;
	

	private void printMsg(String msg) {
		Platform.runLater(() -> {
			ta.appendText(msg + "\n");
		});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage) throws Exception {

		BorderPane root = new BorderPane();
		root.setPrefSize(700, 500);
		
		ta = new TextArea();
		root.setCenter(ta);
		
		// [root - Bottom]
		startBtn = new Button("Start Thread");
		startBtn.setPrefSize(250, 50);
		startBtn.setOnAction((e) -> {
			this.currentThread = new Thread(()-> {
				for (int i = 1; i <= 100; i++) {
					try {
						Thread.sleep(1000);
						this.printMsg(i + " 출력");
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						// e1.printStackTrace();
						break;
					}
				}// for
			});
			this.currentThread.setDaemon(true);		// Thread main이 currentThread 생성
			this.currentThread.start();				// Thread main이 종료되면 currentThread도 종료
		});
		
		stopBtn = new Button("Stop Thread");
		stopBtn.setPrefSize(250,  50);
		stopBtn.setOnAction((e) -> {
			this.currentThread.interrupt();
			/* sleep()을 수행할 때, interrupt가 수행되면
			 * InterruptedException를 발생시키고 catch 문으로 이동한다. */
		});
		
		FlowPane flowpane = new FlowPane();
		flowpane.setPrefSize(700, 50);
		//flowpane.getChildren().add(startBtn);
		flowpane.getChildren().addAll(startBtn, stopBtn);
		
		root.setBottom(flowpane);
		// ~ [root - Bottom]
		
		//
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("예제용 JavaFX");
		primaryStage.setOnCloseRequest(e -> {
			//
		});
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch();
	}
	
}
