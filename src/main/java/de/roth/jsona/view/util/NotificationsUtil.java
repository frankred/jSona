package de.roth.jsona.view.util;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import org.controlsfx.control.Notifications;

public class NotificationsUtil {

    public static void notify(String title, String text) {
        Notifications.create().title(title).text(text).show();
    }

    public static void notify(String title, String text, Node image) {
        Notifications.create().graphic(image).title(title).text(text).show();
    }
}
