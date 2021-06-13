package TsundOkuApp;

import javafx.application.Application;
import javafx.stage.Stage;

public class TsundOku extends Application {
    @Override
    public void start(Stage primaryStage){
        TsundOkuGUI TsundOku = new TsundOkuGUI();
        TsundOku.setupTsundOkuGUI(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
