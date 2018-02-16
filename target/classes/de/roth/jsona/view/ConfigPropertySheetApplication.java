package de.roth.jsona.view;

import de.roth.jsona.config.Config;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.stage.Stage;

public class ConfigPropertySheetApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        ConfigPropertySheet sheet = new ConfigPropertySheet();
        Scene scene = new Scene(sheet, 600, 800, true, SceneAntialiasing.DISABLED);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
