package de.roth.jsona.javafx.util;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Util class to align stages related to another stage.
 *
 * @author Frank Roth
 *
 */
public class AlignmentUtil {

	public static void center(Stage parent, Stage dialog) {
		Pane root = (Pane) dialog.getScene().getRoot();
		double dialogX = parent.getX() + (parent.getWidth() / 2) - root.getPrefWidth() / 2;
		double dialogY = parent.getY() + (parent.getHeight() / 2) - root.getPrefHeight() / 2;
		dialog.setX(dialogX);
		dialog.setY(dialogY);
	}
}
