package de.roth.jsona.javafx;

import de.roth.jsona.config.Config;
import de.roth.jsona.javafx.close.CloseEventHandler;
import de.roth.jsona.logic.LogicManagerFX;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ViewManagerFX {

    public static final String THEME = "themes/" + Config.getInstance().THEME + "/";
    private static ViewManagerFX instance = new ViewManagerFX();
    private ViewController controller;

    public void init(Stage stage, LogicManagerFX logic) {
        try {
            // setting min windows size
            stage.setMinHeight(Config.getInstance().MIN_HEIGHT);
            stage.setMinWidth(Config.getInstance().MIN_WIDTH);

            // setting application properties
            stage.getIcons().add(new Image(THEME + "icon.png"));
            stage.setTitle(Config.getInstance().TITLE);

            if (Config.getInstance().WINDOW_OS_DECORATION == false) {
                stage.initStyle(StageStyle.UNDECORATED);
            }

            Platform.setImplicitExit(false);

            // setting controller
            this.controller = new ViewController(stage);

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(THEME + "layout.fxml"));
            fxmlLoader.setController(this.controller);
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent, Config.getInstance().WIDTH, Config.getInstance().HEIGHT, true, SceneAntialiasing.DISABLED);
            controller.setScene(scene);
            stage.setScene(scene);
            stage.show();

            stage.setOnCloseRequest(new CloseEventHandler(logic));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ViewManagerFX getInstance() {
        return instance;
    }

    public ViewController getController() {
        return controller;
    }

    public void setController(ViewController controller) {
        this.controller = controller;
    }
}