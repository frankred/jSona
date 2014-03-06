package de.roth.jsona.javafx;

import insidefx.undecorator.Undecorator;

import java.io.IOException;

import de.roth.jsona.config.Config;
import de.roth.jsona.javafx.close.CloseEventHandler;
import de.roth.jsona.logic.LogicManagerFX;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ViewManagerFX {

	private static ViewManagerFX instance = new ViewManagerFX();
	private ViewController controller;
	private Parent root;

	public void init(Stage stage, LogicManagerFX logic, boolean undecorated) {
		try {
			// setting min windows size
			stage.setMinHeight(Config.getInstance().MIN_HEIGHT); 
			stage.setMinWidth(Config.getInstance().MIN_WIDTH);
			
			// setting application properties
			stage.getIcons().add(new Image("/de/roth/jsona/view/themes/" + Config.getInstance().THEME + "/" + "icon.png"));
			stage.setTitle(Config.getInstance().TITLE);

			// setting controller
			this.controller = new ViewController(stage);

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/roth/jsona/view/themes/" + Config.getInstance().THEME + "/" + "layout.fxml"));
			loader.setController(this.controller);
			this.root = (Parent) loader.load();

			Scene scene = null;

			if (undecorated) {
				Undecorator undecorator = new Undecorator(stage, (Region) this.root);
				undecorator.getStylesheets().add("insidefx/undecorator/undecorator.css");
				scene = new Scene(undecorator);
				scene.setFill(Color.TRANSPARENT);
				stage.initStyle(StageStyle.TRANSPARENT);
			} else {
				scene = new Scene(root);
				stage.initStyle(StageStyle.DECORATED);
			}

			this.controller.setScene(scene);

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