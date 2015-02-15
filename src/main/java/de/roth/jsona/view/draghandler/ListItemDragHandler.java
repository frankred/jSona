package de.roth.jsona.view.draghandler;

import de.roth.jsona.view.ViewController.ListItemCell;
import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;

public class ListItemDragHandler implements EventHandler<DragEvent> {

    private int currentIndex;
    private double currentCellHeight;
    private double currentCellHeightHalf;
    private ListItemCell currentCell;
    private boolean before;

    @Override
    public void handle(DragEvent event) {
        if (event.getEventType() == DragEvent.DRAG_ENTERED) {
            currentCell = ((ListItemCell) event.getTarget());
            currentIndex = currentCell.getIndex();
            currentCellHeight = currentCell.getBoundsInLocal().getHeight();
            currentCellHeightHalf = currentCellHeight / 2;
        } else if (event.getEventType() == DragEvent.DRAG_OVER) {
            if (currentCell.getIndex() >= currentCell.getListView().getItems().size()) {
                return;
            }
            if (event.getY() <= currentCellHeightHalf) {
                // mark top
                currentCell.setStyle("-fx-border-style: solid; -fx-border-width: 1px 0 0 0; -fx-border-color: #0063AA;");
                before = true;
            } else {
                // mark bottom
                currentCell.setStyle("-fx-border-style: solid; -fx-border-width: 0 0 1px 0; -fx-border-color: #0063AA;");
                before = false;
            }
        } else if (event.getEventType().toString().equals("DRAG_EXITED")) {
            currentCell.setStyle("-fx-border-style: none;");
        }
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public boolean isBefore() {
        return before;
    }
}
