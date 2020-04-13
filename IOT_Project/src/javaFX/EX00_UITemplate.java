package javaFX;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/*
 * JavaFX 를 이용한 화면 UI 생성
 * 	- JavaFX Library 설치, 등록
 * 	- javafx.application.Application 클래스를 상속한 Template 클래스 정의
 * 	- start()라는 abstract method를 overriding 한다.
*/
public class EX00_UITemplate extends Application {
	
	private TextArea ta;
	private Button btn;

	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage) throws Exception {
		/* 화면을 구성하고 event 처리를 담당
		 * 기본 Layout 생성 => BoarderPane(동서남북중앙 구성)으로
		*/
		BorderPane root = new BorderPane();
		
		// set Size
		root.setPrefSize(700, 500);
		
		ta = new TextArea();		// 글상자 생성
		root.setCenter(ta);			// BorderPane center에 부착
		
		btn = new Button("Button Click");
		btn.setPrefSize(250, 50);
		
		// button click event
		/*
		btn.setOnAction(new EventHandler() {	// @SuppressWarnings("unchecked")
			@Override
			public void handle(Event arg0) {
				System.out.println("Button Clicked!");
			}
		});	*/
		// lambda
		btn.setOnAction((e) -> {
			System.out.println("Button Clicked!");
			/* 컴포넌트에 동기화가 보장되지 않아 잘못된 결과가 발생할 가능성이 존대한다.
			 * 따라서, 직접 UI Component를 제어하는 방법은 좋지 않다. */
			// ta.appendText("Button Clicked!\n");
			/* Thread를 이용해서 컴포넌트에 접속하는 방식
			 * Platform.runLater(runnable)	*/
			Platform.runLater(() -> {
				// run()
				ta.appendText("Button Clicked!\n");
			});
		});
		
		// 일반 패널 하나 생성  -> like LinearLayout
		FlowPane flowpane = new FlowPane();
		flowpane.setPrefSize(700, 50);
		flowpane.getChildren().add(btn);	// flowpane에 btn 부착
		
		root.setBottom(flowpane);
		
		//
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("JavaFX Template");
//		primaryStage.setOnCloseRequest(new EventHandler() {
//			@Override
//			public void handle(Event arg0) {
//				System.exit(0);
//			}
//		});
		primaryStage.setOnCloseRequest(e -> {
			System.exit(0);
		});
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		// Window 띄우기
		//	- 객체 생성 후 start() 호출
		// 	- or  launch() 호출
		launch();	// Thread의 start()가 run()을 호출하듯 위의 start()를 호출한다.
	}

}
