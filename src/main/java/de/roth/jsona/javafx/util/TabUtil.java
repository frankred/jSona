package de.roth.jsona.javafx.util;

import de.roth.jsona.logic.LogicInterfaceFX;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class TabUtil {

    /**
     * Creates a JavaFX tab. The title of the tab is editable via a double-click
     * on the titel.
     *
     * @param name
     * @return
     */
    public static Tab createEditableTab(final String atomicId, String name, boolean selectTitle, final LogicInterfaceFX logic) {
        final Label label = new Label(name);
        final Tab tab = new Tab();
        tab.setId(atomicId);
        tab.setGraphic(label);
        final TextField textField = new TextField();

        label.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldname, String newname) {
                logic.event_playlist_namechanged(atomicId, oldname, newname);
            }
        });

        label.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    textField.setText(label.getText());
                    tab.setGraphic(textField);
                    textField.selectAll();
                    textField.requestFocus();
                }
            }
        });

        if (selectTitle) {
            textField.setText(label.getText());
            tab.setGraphic(textField);
            textField.requestFocus();
            textField.selectAll();
        }

        textField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                label.setText(textField.getText());
                tab.setGraphic(label);
            }
        });

        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    label.setText(textField.getText());
                    tab.setGraphic(label);
                }
            }
        });

        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (textField.isFocused() && !textField.getText().isEmpty()) {
                            textField.selectAll();
                        }
                    }
                });
            }
        });
        return tab;
    }

    /**
     * Create a tab with a icon as the tab head
     *
     * @param icon
     * @return
     */
    public static Tab createIconTab(ImageView icon) {
        final Tab tab = new Tab();
        StackPane pane = new StackPane();
        pane.getChildren().add(icon);
        tab.setGraphic(pane);
        return tab;
    }
}
