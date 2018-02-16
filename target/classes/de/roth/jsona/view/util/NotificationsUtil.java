package de.roth.jsona.view.util;

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
