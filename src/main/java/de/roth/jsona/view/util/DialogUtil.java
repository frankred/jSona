package de.roth.jsona.view.util;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialogs;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Simple Dialog generator
 *
 * @author Frank Roth
 */
public class DialogUtil {


    public static Stage createDialog(Stage parent, URL layoutSource, boolean modal) {
        return DialogUtil.createDialog(parent, layoutSource, modal, null);
    }

    /**
     * Create a dialog and define a parent stage, and a dialog layout.fxml
     *
     * @param parent
     * @param layoutSource
     * @return
     */
    public static Stage createDialog(Stage parent, URL layoutSource, boolean modal, ResourceBundle bundle) {
        final Stage dialog = new Stage(StageStyle.TRANSPARENT);
        if (modal) {
            dialog.initModality(Modality.WINDOW_MODAL);
        } else {
            dialog.initModality(Modality.NONE);
        }
        dialog.initOwner(parent);
        dialog.setResizable(false);

        FXMLLoader loader;
        if (bundle == null) {
            loader = new FXMLLoader(layoutSource);
        } else {
            loader = new FXMLLoader(layoutSource, bundle);
        }

        Parent root = null;
        try {
            root = (Parent) loader.load();
        } catch (IOException e1) {
            e1.printStackTrace();
            return null;
        }


        Scene s = new Scene(root);
        s.setFill(Color.TRANSPARENT);
        dialog.setScene(s);
        AlignmentUtil.center(parent, dialog);

        return dialog;
    }

    public static void showErrorDialog(Stage stage, String title, String message) {
        Action response = Dialogs.create()
                .owner(stage)
                .title(title)
                .message(message)
                .showError();
    }
}
