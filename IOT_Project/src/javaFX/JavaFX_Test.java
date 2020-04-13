package javaFX;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class JavaFX_Test extends Application {
	
	@Override
	public void start(Stage stage) throws Exception {
		
//		BorderPane pane = new BorderPane();
//		pane.setPrefSize(500, 500);
		
//		Scene scene = new Scene(pane);					// size : 500x500
//		Scene scene = new Scene(pane, 400, 400);		// size : 400x400
		
		BorderPane pane = new BorderPane();
		pane.setPrefSize(700, 500);

		Button btn = new Button("Button Click");
		btn.setPrefSize(250, 50);

//		pane.getChildren().add(btn);
		pane.setCenter(btn);

		Scene scene = new Scene(pane);
		stage.setScene(scene);
		stage.show();
	}
	
	public static void main(String[] args) {
		launch();
//		System.out.println("main");
//		try {
//			Thread.sleep(100);
//			Singleton.getInstance();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
	}
}

//class Singleton {
//	private static Singleton instance = new Singleton();
//	private Singleton() {
//		System.out.println("created");
//	}
//	public static Singleton getInstance() {
//		if (instance == null) {
//			instance =  new Singleton();
//		}
//		return instance;
//	}
//}
