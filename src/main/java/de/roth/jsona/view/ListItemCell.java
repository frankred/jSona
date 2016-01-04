package de.roth.jsona.view;

import de.roth.jsona.logic.LogicInterfaceFX;
import de.roth.jsona.model.MusicListItem;
import de.roth.jsona.theme.ThemeUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class ListItemCell extends ListCell<MusicListItem> implements InvalidationListener {

    public final String[] styleClasses = {"cell", "indexed-cell", "list-cell"};
    public final String defaultCellClass = "listitem";
    public final static String PLAYING_CLASS = "playing";

    private AnchorPane listItem;
    private Label artist;
    private Label title;
    private Label duration;
    private ImageView live;

    public ListItemCell(final LogicInterfaceFX logic) {
        initCellLayout();
        if (logic != null) {
            setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                        if (mouseEvent.getClickCount() == 2) {
                            // Fix: that the slider isn't toggling if length
                            // changed is first called
                            ViewController.ListItemManager.getInstance().play(logic, getListView(), getItem());
                        }
                    }
                }
            });
        }
    }

    public void initCellLayout() {
        try {
            this.listItem = (AnchorPane) FXMLLoader.load(getClass().getClassLoader().getResource(ThemeUtils.getThemePath() + "/layout_list_item.fxml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.artist = (Label) this.listItem.lookup("#artist");
        this.artist.setWrapText(true);
        this.title = (Label) this.listItem.lookup("#title");
        this.title.setWrapText(true);
        this.duration = (Label) this.listItem.lookup("#duration");
        this.live = (ImageView) this.listItem.lookup("#live");
        this.live.setVisible(false);

        // Create icon and hide
        this.setGraphic(listItem);
    }

    @Override
    public void updateItem(MusicListItem item, boolean empty) {

        // If old item is not null remove listener because we dont need it
        // anymore
        if (getItem() != null) {
            getItem().removeListener(this);
        }
        super.updateItem(item, empty);

        if (empty || item == null) {
            this.live.setVisible(false);
            this.artist.setVisible(false);
            this.duration.setVisible(false);
            this.title.setVisible(false);
            return;
        }

        repaint(item);
        item.addListener(this);
    }

    @Override
    public void invalidated(Observable arg0) {
        repaint(getItem());
    }

    private void repaint(MusicListItem item) {

        if (item.getStatus() == null) {
            item.setStatus(MusicListItem.PlaybackStatus.SET_NONE);
        }

        // Duration
        this.duration.setVisible(true);
        this.duration.setText(item.getDuration());

        // Play icon
        switch (item.getStatus()) {
            case SET_PLAYING:
                this.setStyle("-fx-background: -fx-accent; -fx-background-color: -fx-focus-color, -fx-cell-focus-inner-border, -fx-selection-bar; -fx-background-insets: 0.0, 0.0, 0.0; -fx-text-fill: -fx-selection-bar-text;");
                this.live.setManaged(true);
                this.live.setVisible(true);
                break;

            case SET_NONE:
                this.setStyle("");
                this.live.setManaged(false);
                this.live.setVisible(false);
                break;
            default:
                break;
        }

        if (item.getTextForArtistLabel() == null) {
            this.artist.setText(""); // visual layout bugfix only occurs in the caspain theme
            this.artist.setManaged(false);
            this.artist.setVisible(false);
        } else {
            this.artist.setText(item.getArtist());
            this.artist.setManaged(true);
            this.artist.setVisible(true);
        }

        if (item.getTextForTitleLabel() == null) {
            this.title.setText("");
            this.title.setManaged(false);
            this.title.setVisible(false);
        } else {
            this.title.setText((item.getTextForArtistLabel() == null ? "" : " - ") + item.getTextForTitleLabel());
            this.title.setManaged(true);
            this.title.setVisible(true);
        }
    }
}