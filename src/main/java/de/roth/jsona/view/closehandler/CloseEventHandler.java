package de.roth.jsona.view.closehandler;

import de.roth.jsona.logic.LogicManagerFX;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

public class CloseEventHandler implements EventHandler<WindowEvent> {

    LogicManagerFX logic;

    public CloseEventHandler(LogicManagerFX logic) {
        this.logic = logic;
    }

    @Override
    public void handle(WindowEvent arg0) {
        logic.close();
        Platform.exit();
        System.exit(0);
    }
}
