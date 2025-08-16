package PageReplacement;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX 애플리케이션의 진입점이다.
 * 페이지 교체 시뮬레이터의 UI를 초기화하고 화면에 띄운다.
 */

public class Main extends Application {
	@Override
	public void start(Stage stage) {
		SimulatorUI ui = new SimulatorUI();
		Scene scene = new Scene(ui.createContent(), 800, 600);
		stage.setTitle("Page Replacement Simulator");
		stage.setScene(scene);
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
