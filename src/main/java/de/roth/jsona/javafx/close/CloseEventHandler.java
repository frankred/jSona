package de.roth.jsona.javafx.close;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import de.roth.jsona.logic.LogicManagerFX;

public class CloseEventHandler implements EventHandler<WindowEvent> {
	
	LogicManagerFX logic;
	
	public CloseEventHandler(LogicManagerFX logic){
		this.logic = logic;
	}

	@Override
	public void handle(WindowEvent arg0) {
		logic.close();
		Platform.exit();
		System.exit(0);
	}
}
