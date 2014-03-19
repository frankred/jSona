package de.roth.jsona.javafx;

import insidefx.undecorator.Undecorator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.FocusModel;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import org.jsoup.Jsoup;

import com.sun.media.jfxmedia.events.PlayerStateEvent.PlayerState;

import de.roth.jsona.artist.JSonaArtist;
import de.roth.jsona.config.Config;
import de.roth.jsona.javafx.draghandler.ListItemDragHandler;
import de.roth.jsona.javafx.util.AlignmentUtil;
import de.roth.jsona.javafx.util.BrowserUtil;
import de.roth.jsona.javafx.util.TabUtil;
import de.roth.jsona.logic.LogicInterfaceFX;
import de.roth.jsona.mediaplayer.PlayBackMode;
import de.roth.jsona.model.MusicListItem;
import de.roth.jsona.model.MusicListItem.Status;
import de.roth.jsona.model.PlayList;
import de.roth.jsona.util.Logger;
import de.roth.jsona.util.TimeFormatter;
import de.umass.lastfm.Track;

public class ViewController implements Initializable, ViewInterface {

	// JavaFX UI-Components
	@FXML
	private AnchorPane applicationContainer, searchContent;

	@FXML
	private Slider volumeSlider, durationSlider;

	@FXML
	private ProgressBar volumeProgress, durationProgress;
	boolean blockDurationProgress;

	@FXML
	private Label volumeLabel, durationLabel, artistLabel, titleLabel, artistBio;

	@FXML
	private TabPane musicTabs, playlistTabs;

	@FXML
	private Tab searchTab;

	@FXML
	private FlowPane topTracks;

	@FXML
	private Button playButton, nextButton, prevButton;

	@FXML
	private Hyperlink removePlaylistButton;

	@FXML
	private ImageView playButtonImage, pauseButtonImage, nextButtonImage, prevButtonImage, shuffleToggleButtonImage, artistImage, addPlaylistImage;

	@FXML
	private ToggleButton shuffleToggleButton;

	@FXML
	private Image playImage, pauseImage;

	@FXML
	AnchorPane imageContainer;

	@FXML
	TextField searchText;

	@FXML
	ListView<MusicListItem> searchResultsListView;

	private Stage stage;
	private Scene scene;
	private ArrayList<ListView<MusicListItem>> playListViews;
	private ArrayList<ListView<MusicListItem>> musicListViews;

	// Dragging data
	private static final DataFormat DATA_FORMAT_LIST = new DataFormat("java.util.List");

	// Tmp
	private int searchResultCounter = -1;

	public ViewController(Stage stage) {
		this.stage = stage;
	}

	private void setVolumeFX(int value, boolean updateItself) {
		// Volume
		if (updateItself) {
			volumeSlider.valueProperty().set(value);
		}
		volumeProgress.setProgress(value / 100d);
		volumeLabel.setText(value + "%");
	}

	private void setDurationFX(long ms, boolean updateItself) {
		if (updateItself) {
			durationSlider.setValue(ms);
		}
		durationProgress.setProgress(ms / durationSlider.getMax());
		durationLabel.setText(TimeFormatter.formatMilliseconds(ms));
	}

	private void setShuffleFX(boolean shuffle) {
		shuffleToggleButton.setSelected(shuffle);
		if (shuffle) {
			ListItemManager.getInstance().setPlayBackMode(PlayBackMode.SHUFFLE);
		} else {
			ListItemManager.getInstance().setPlayBackMode(PlayBackMode.NORMAL);
		}
	}

	public Slider getDurationSlider() {
		return durationSlider;
	}

	public Stage getStage() {
		return this.stage;
	}

	private void setDurationLength(long ms) {
		durationSlider.setMax(ms);
	}
	
	public void hide(){
		stage.hide();
	}

	public void toggleView() {
		Platform.runLater(new Runnable() {
			public void run() {
				if(!stage.isFocused()){
					stage.setIconified(false);
					stage.toFront();
				} else {
					if(!stage.isIconified()){
						stage.setIconified(true);
					}
				}
			}
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.playListViews = new ArrayList<ListView<MusicListItem>>();
		this.musicListViews = new ArrayList<ListView<MusicListItem>>();
		artistImage.setImage(new Image("/de/roth/jsona/view/themes/" + Config.getInstance().THEME + "/" + "icon.png"));
	}

	public void removeItem(final MusicListItem item) {
		final ArrayList<ListView<MusicListItem>> p = this.playListViews;
		final ArrayList<ListView<MusicListItem>> m = this.musicListViews;
		final ListView<MusicListItem> s = this.searchResultsListView;
		Platform.runLater(new Runnable() {
			public void run() {
				for (ListView<MusicListItem> listView : p) {
					listView.getItems().remove(item);
				}

				for (ListView<MusicListItem> listView : m) {
					listView.getItems().remove(item);
				}

				s.getItems().remove(item);
			}
		});
	}

	public void init(final LogicInterfaceFX logic, String theme) {
		// Setup theme
		String themePath = "/de/roth/jsona/view/themes/" + theme;
		this.playImage = new Image(themePath + "/" + "play.png");
		this.pauseImage = new Image(themePath + "/" + "pause.png");
		this.playButtonImage.setImage(playImage);
		this.nextButtonImage.setImage(new Image(themePath + "/" + "next.png"));
		this.prevButtonImage.setImage(new Image(themePath + "/" + "prev.png"));
		this.shuffleToggleButtonImage.setImage(new Image(themePath + "/" + "shuffle.png"));

		// Application keys
		getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN), new Runnable() {
			@Override
			public void run() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						musicTabs.getSelectionModel().select(searchTab);

						// Does not has any effects, expect from if search tab
						// is already selected
						searchText.requestFocus();
					}
				});
			}
		});
		searchContent.visibleProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> newVal, Boolean oldVal, Boolean arg2) {
				// If the pane from the search tab gets visible, focus the
				// search text field
				if (newVal.getValue()) {
					searchText.requestFocus();
				}
			}
		});

		// Buttons
		playButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				logic.event_player_play_pause();
			}
		});

		nextButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				durationSlider.setValue(0);
				durationProgress.setProgress(0);
				ListItemManager.getInstance().next(logic);
			}
		});

		prevButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				durationSlider.setValue(0);
				durationProgress.setProgress(0);
				ListItemManager.getInstance().prev(logic);
			}
		});

		removePlaylistButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				try {
					final Stage dialog = new Stage(StageStyle.TRANSPARENT);
					dialog.initModality(Modality.WINDOW_MODAL);
					dialog.initOwner(stage);
					dialog.setResizable(false);

					FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/roth/jsona/view/themes/" + Config.getInstance().THEME + "/" + "layout_confirm_dialog.fxml"));
					Parent root = (Parent) loader.load();

					Button ok = (Button) root.lookup("#okButton");
					Button abort = (Button) root.lookup("#abortButton");
					Label message = (Label) root.lookup("#confirmMessage");
					message.setText("Do you really want to delete the playlist '" + ((Label) playlistTabs.getSelectionModel().getSelectedItem().getGraphic()).getText() + "'?");

					ok.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent e) {
							// Delete the tab and playlist
							Tab t = playlistTabs.getSelectionModel().getSelectedItem();
							String atomicId = t.getId();
							playlistTabs.getTabs().remove(t);
							logic.event_playlist_remove(atomicId);

							dialog.hide();
						}
					});

					abort.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent e) {
							dialog.hide();
						}
					});

					Undecorator undecorator = new Undecorator(dialog, (Region) root);
					undecorator.getStylesheets().add("insidefx/undecorator/undecorator.css");

					Scene s = new Scene(undecorator);
					s.setFill(Color.TRANSPARENT);
					dialog.setScene(s);
					AlignmentUtil.center(stage, dialog);
					dialog.show();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		removePlaylistButton.setTooltip(new Tooltip("Remove selected playlist."));

		// Toggle Buttons
		shuffleToggleButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (shuffleToggleButton.isSelected()) {
					logic.event_playbackmode_shuffle();
					ListItemManager.getInstance().setPlayBackMode(PlayBackMode.SHUFFLE);
					return;
				}
				ListItemManager.getInstance().setPlayBackMode(PlayBackMode.NORMAL);
				logic.event_playbackmode_normal();
			}
		});

		// Slider
		setVolumeFX(Config.getInstance().VOLUME, true);
		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue) {
				setVolumeFX(newValue.intValue(), false);
				logic.event_player_volume(newValue.intValue(), oldValue.intValue());
			}
		});

		durationSlider.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent m) {
				blockDurationProgress = true;
			};
		});

		durationSlider.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent m) {
				blockDurationProgress = false;
				logic.event_player_play_skipto(durationSlider.getValue());
			};
		});

		durationSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue) {
				setDurationFX(newValue.longValue(), false);
			}
		});

		durationSlider.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent arg0) {
				double newTime;

				switch (arg0.getCode()) {
				case RIGHT:
					newTime = durationSlider.getValue() + Config.getInstance().KEY_SKIP_TIME * 1000;
					if (newTime > durationSlider.getMax()) {
						newTime = durationSlider.getMax();
					}
					logic.event_player_play_skipto(newTime);
					break;
				case LEFT:
					newTime = durationSlider.getValue() - Config.getInstance().KEY_SKIP_TIME * 1000;
					if (newTime < 0) {
						newTime = 0;
					}
					logic.event_player_play_skipto(newTime);
					break;
				default:
					break;
				}

			}
		});

		// Search
		searchText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				logic.event_search_music(newValue);
			}
		});
		searchText.setPromptText("Search word...");
		searchResultsListView.setCellFactory(new Callback<ListView<MusicListItem>, ListCell<MusicListItem>>() {
			@Override
			public ListCell<MusicListItem> call(ListView<MusicListItem> item) {
				// http://docs.oracle.com/javafx/2/ui_controls/list-view.htm
				return new ListItemCell(logic);
			}
		});
		// keys
		searchResultsListView.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(final KeyEvent keyEvent) {
				switch (keyEvent.getCode()) {
				case ENTER:
					ListItemManager.getInstance().play(logic, searchResultsListView, searchResultsListView.getSelectionModel().getSelectedItem());
					break;
				default:
					break;
				}
			}
		});

		searchText.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.DOWN) {
					searchResultsListView.getSelectionModel().clearAndSelect(0);
					searchResultsListView.requestFocus();
					searchResultsListView.getFocusModel().focus(0);
				}
			};
		});
		searchResultsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		enableListViewDragItems(searchResultsListView, TransferMode.COPY);
	}

	public void setPlaybackMode(final PlayBackMode mode) {
		Platform.runLater(new Runnable() {
			public void run() {
				if (mode == PlayBackMode.NORMAL) {
					setShuffleFX(false);
				} else if (mode == PlayBackMode.SHUFFLE) {
					setShuffleFX(true);
				}
				ListItemManager.getInstance().setPlayBackMode(mode);
			}
		});
	}

	public void setCurrentItem(MusicListItem item) {
		ListItemManager.getInstance().load(item);
	}

	public void setVolume(final int vol) {
		Platform.runLater(new Runnable() {
			public void run() {
				setVolumeFX(Config.getInstance().VOLUME, true);
			}
		});
	}

	public void setCurrentTotalDuration(final long value) {
		Platform.runLater(new Runnable() {
			public void run() {
				setDurationLength(value);
			}
		});
	}

	public void setCurrentDuration(final long value) {
		Platform.runLater(new Runnable() {
			public void run() {
				if (blockDurationProgress)
					return;
				setDurationFX(value, true);
			}
		});
	}

	public void setPlayerState(final PlayerState pbm) {
		Platform.runLater(new Runnable() {
			public void run() {
				switch (pbm) {
				case PLAYING:
					playButtonImage.setImage(pauseImage);
					playButtonImage.setTranslateX(0);
					playButtonImage.setX(0);
					break;
				case PAUSED:
					playButtonImage.setImage(playImage);
					playButtonImage.setTranslateX(2);
					playButtonImage.setX(20);
					break;
				default:
					break;
				}
			}
		});
	}

	public void initPlaylists(final LogicInterfaceFX logic, final ArrayList<PlayList> playlists, int activeIndex) {
		Platform.runLater(new Runnable() {
			public void run() {
				for (PlayList p : playlists) {
					playlistTabs.getTabs().add(createPlayList(logic, p, false, false));
				}
				playlistTabs.getTabs().add(createNewTabButton(logic));
			}
		});
	}

	public void newPlaylist(final LogicInterfaceFX logic, final PlayList p) {
		Platform.runLater(new Runnable() {
			public void run() {
				int last = playlistTabs.getTabs().size() - 1;
				if (last < 0) {
					last = 0;
				}
				playlistTabs.getTabs().add(last, createPlayList(logic, p, true, true));
				playlistTabs.getSelectionModel().select(last);
			}
		});
	}

	private Tab createNewTabButton(final LogicInterfaceFX logic) {
		final Tab newAddTabButton = new Tab("+");

		newAddTabButton.setOnSelectionChanged(new EventHandler<Event>() {
			@Override
			public void handle(Event t) {
				if (newAddTabButton.isSelected()) {
					logic.event_playlist_new();
				}
			}
		});
		return newAddTabButton;
	}

	public synchronized void addMusicListItem(final String musicListViewId, final String rootFolder, final MusicListItem item) {
		final ListView<MusicListItem> listView = getMusicListView(musicListViewId);
		if (listView == null) {
			return;
		}

		// Determe index of list item
		int index = 0;
		for (MusicListItem i : listView.getItems()) {
			if (i.getFile().getAbsolutePath().compareTo(item.getFile().getAbsolutePath()) >= 0) {

				// If they have same parent folder, then item gets the same
				// color
				if (i.getFile().getParentFile().getAbsolutePath().equals(item.getFile().getParentFile().getAbsolutePath())) {
					item.setColorClass(i.getColorClass());
				}

				break;
			}
			index++;
		}

		final int addIndex = index;
		Platform.runLater(new Runnable() {
			public void run() {
				listView.getItems().add(addIndex, item);
			}
		});
	}

	public ListView<MusicListItem> getMusicListView(String id) {
		for (ListView<MusicListItem> listView : this.musicListViews) {
			if (listView.getId().equals(id)) {
				return listView;
			}
		}
		return null;
	}

	public void addMusicFolder(final LogicInterfaceFX logic, final String t, final String id, final int pos, final ArrayList<MusicListItem> items) {
		Platform.runLater(new Runnable() {
			public void run() {
				Tab m = new Tab();
				try {
					AnchorPane listPane = (AnchorPane) FXMLLoader.load(getClass().getResource("/de/roth/jsona/view/themes/" + Config.getInstance().THEME + "/" + "layout_list.fxml"));
					@SuppressWarnings("unchecked")
					final ListView<MusicListItem> listView = (ListView<MusicListItem>) listPane.getChildren().get(0);
					listView.setItems(FXCollections.observableList(items));
					listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
					listView.setId(id);

					// ListCell
					listView.setCellFactory(new Callback<ListView<MusicListItem>, ListCell<MusicListItem>>() {
						@Override
						public ListCell<MusicListItem> call(ListView<MusicListItem> item) {
							// http://docs.oracle.com/javafx/2/ui_controls/list-view.htm
							return new MusicListItemCell(logic);
						}
					});

					// Enable draggable itmes
					enableListViewDragItems(listView, TransferMode.COPY);

					// keys
					listView.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
						public void handle(final KeyEvent keyEvent) {
							switch (keyEvent.getCode()) {
							case ENTER:
								ListItemManager.getInstance().play(logic, listView, listView.getSelectionModel().getSelectedItem());
								break;
							default:
								break;
							}
						}
					});

					musicListViews.add(listView);

					m.setContent(listPane);
				} catch (IOException e) {
					e.printStackTrace();
				}
				StringProperty tabTextProperty = new SimpleStringProperty(t);
				m.textProperty().bindBidirectional(tabTextProperty);
				musicTabs.getTabs().add(pos, m);
			}
		});
	}

	private Tab createPlayList(final LogicInterfaceFX logic, final PlayList playlist, boolean selectTitle, boolean activateTab) {
		Tab tab = TabUtil.createEditableTab(playlist.getAtomicId(), playlist.getName(), selectTitle, logic);

		// get layout
		AnchorPane listPane = null;
		try {
			listPane = (AnchorPane) FXMLLoader.load(getClass().getResource("/de/roth/jsona/view/themes/" + Config.getInstance().THEME + "/" + "layout_list.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		// create list view
		@SuppressWarnings("unchecked")
		final ListView<MusicListItem> listView = (ListView<MusicListItem>) listPane.getChildren().get(0);
		listView.setItems(FXCollections.observableList(playlist.getItems()));
		listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		playListViews.add(listView);

		final ListItemDragHandler dragHandler = new ListItemDragHandler();

		// select tab
		if (activateTab) {
			ListItemManager.getInstance().setCurrentListView(listView);
		}

		enableListViewDragItems(listView, TransferMode.MOVE);

		// item drag and drop
		listView.setCellFactory(new Callback<ListView<MusicListItem>, ListCell<MusicListItem>>() {
			@Override
			public ListCell<MusicListItem> call(ListView<MusicListItem> item) {
				ListItemCell cell = new ListItemCell(logic);
				cell.setOnDragOver(dragHandler);
				cell.setOnDragEntered(dragHandler);
				cell.setOnDragExited(dragHandler);
				cell.setOnDragDropped(dragHandler);
				return cell;
			}
		});
		listView.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				event.acceptTransferModes(TransferMode.ANY);
				event.consume();
			}
		});
		listView.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				@SuppressWarnings("unchecked")
				ArrayList<MusicListItem> items = (ArrayList<MusicListItem>) event.getDragboard().getContent(DATA_FORMAT_LIST);
				String url = (String) event.getDragboard().getUrl();

				int dropIndex = dragHandler.getCurrentIndex();
				if (dropIndex < listView.getItems().size()) {
					if (dragHandler.isBefore()) {
						// this is ok
					} else {
						dropIndex = dropIndex + 1;
					}
				} else {
					dropIndex = listView.getItems().size();
				}

				// Any transfer mode
				if (url != null) {
					logic.event_play_url(url);
				}

				switch (event.getTransferMode()) {
				/* ############# COPY ############# */
				case COPY:
					if (items != null) {
						listView.getSelectionModel().clearSelection();
						listView.getItems().addAll(dropIndex, items);
						listView.getSelectionModel().selectRange(dropIndex, dropIndex + items.size());
						event.setDropCompleted(true);
						event.consume();
						logic.event_playlist_changed(playlist.getAtomicId(), listView.getItems());
					}
					break;

				/* ############# MOVE ############# */
				case MOVE:
					// save selection temporarily
					int[] indices = new int[listView.getSelectionModel().getSelectedIndices().size()];
					int j = 0;
					for (Integer i : listView.getSelectionModel().getSelectedIndices()) {
						indices[j] = i;
						++j;
					}

					// add items
					listView.getItems().addAll(dropIndex, listView.getSelectionModel().getSelectedItems());

					// clear selection
					listView.getSelectionModel().clearSelection();

					// change indices to remove items
					int amountSmallerDropIndex = 0;
					for (int i = 0; i < indices.length; i++) {
						if (indices[i] >= dropIndex) {
							indices[i] = indices[i] + indices.length;
						} else {
							amountSmallerDropIndex++;
						}
					}

					// remove items
					for (int i = indices.length - 1; i >= 0; i--) {
						listView.getItems().remove(indices[i]);
					}

					// select moved items
					listView.getSelectionModel().selectRange(dropIndex - amountSmallerDropIndex, dropIndex - amountSmallerDropIndex + indices.length);

					break;
				default:
					break;
				}
			}
		});

		// keys
		final EventHandler<KeyEvent> playlistKeyEventHandler = new EventHandler<KeyEvent>() {
			public void handle(final KeyEvent keyEvent) {
				switch (keyEvent.getCode()) {
				case DELETE:
					if (listView.getSelectionModel().getSelectedItems().size() == listView.getItems().size()) {
						listView.getItems().clear();
						keyEvent.consume();
						logic.event_playlist_changed(playlist.getAtomicId(), listView.getItems());
						return;
					}

					int firstIndex = listView.getSelectionModel().getSelectedIndex();
					ObservableList<MusicListItem> delete = FXCollections.observableArrayList(listView.getSelectionModel().getSelectedItems());
					listView.getItems().removeAll(delete);
					listView.getSelectionModel().clearSelection();

					// last item was removed, that isn't available
					// anymore => Reset index
					if (firstIndex >= listView.getItems().size()) {
						firstIndex = listView.getItems().size() - 1;
					}
					listView.getSelectionModel().select(firstIndex);

					keyEvent.consume();
					logic.event_playlist_changed(playlist.getAtomicId(), listView.getItems());

					break;

				case ENTER:
					ListItemManager.getInstance().play(logic, listView, listView.getSelectionModel().getSelectedItem());
					break;
				default:
					break;
				}
			}
		};
		listView.addEventHandler(KeyEvent.KEY_PRESSED, playlistKeyEventHandler);
		tab.setContent(listPane);

		return tab;
	}

	private void enableListViewDragItems(final ListView<MusicListItem> listView, final TransferMode mode) {
		// Drag
		listView.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				/* allow any transfer mode */
				Dragboard db = listView.startDragAndDrop(mode);

				/* put a string on dragboard */
				ClipboardContent content = new ClipboardContent();
				content.clear();

				content.put(DATA_FORMAT_LIST, new ArrayList<MusicListItem>(listView.getSelectionModel().getSelectedItems()));

				db.setContent(content);
				event.consume();
			}
		});
	}

	public void next(LogicInterfaceFX logic) {
		ListItemManager.getInstance().next(logic);
	}

	public void prev(LogicInterfaceFX logic) {
		ListItemManager.getInstance().prev(logic);
	}

	public static class MusicListItemCell extends ListItemCell {

		public MusicListItemCell(LogicInterfaceFX logic) {
			super(logic);
		}

		@Override
		public void updateItem(MusicListItem item, boolean empty) {
			super.updateItem(item, empty);

			if (Config.getInstance().COLORIZE_ITEMS && !empty) {
				this.getStyleClass().clear();
				this.getStyleClass().add("c" + item.getColorClass());
				this.getStyleClass().addAll(styleClasses);
				this.getStyleClass().add(defaultCellClass);
			}
		}
	}

	public static class ListItemCell extends ListCell<MusicListItem> {

		public final String[] styleClasses = { "cell", "indexed-cell", "list-cell" };
		public final String defaultCellClass = "listitem";
		public final String defaultTextClass = "listtext";

		private AnchorPane listItem;
		private ImageView playback_icon;
		private Label artist;
		private Label title;
		private Label duration;
		private BorderPane borderPane;
		private FlowPane flowPane;
		private boolean playSymbol;

		/**
		 * Constructor for musiclist
		 */
		public ListItemCell() {
			this.playSymbol = false;
			initCellLayout();
		}

		public boolean isPlaySymbol() {
			return playSymbol;
		}

		public void setPlaySymbol(boolean playSymbol) {
			this.playSymbol = playSymbol;
		}

		/**
		 * Constructor for playlist
		 * 
		 * 
		 * @param atomicId
		 * @param logic
		 */
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
								ListItemManager.getInstance().play(logic, getListView(), getItem());
							}
						}
					}
				});
			}
		}

		public void initCellLayout() {
			try {
				this.listItem = (AnchorPane) FXMLLoader.load(getClass().getResource("/de/roth/jsona/view/themes/" + Config.getInstance().THEME + "/" + "layout_list_item.fxml"));
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.borderPane = (BorderPane) listItem.getChildren().get(0);
			this.flowPane = (FlowPane) borderPane.getLeft();
			this.playback_icon = new ImageView(new Image("/de/roth/jsona/view/themes/" + Config.getInstance().THEME + "/" + "item_playing.png"));
			this.artist = (Label) flowPane.getChildren().get(0);
			this.artist.getStyleClass().add(defaultTextClass);

			this.title = (Label) flowPane.getChildren().get(1);
			this.title.getStyleClass().add(defaultTextClass);

			this.duration = (Label) borderPane.getRight();
			this.duration.getStyleClass().add(defaultTextClass);

			this.playback_icon.setManaged(false);

			// Create icon and hide
			this.flowPane.getChildren().add(0, this.playback_icon);
			this.playback_icon.setManaged(false);
			this.playback_icon.setVisible(false);

			this.setGraphic(listItem);
		}

		@Override
		public void updateItem(MusicListItem item, boolean empty) {
			super.updateItem(item, empty);

			if (!empty && item != null) {
				// Duration
				this.duration.setText(item.getDuration());

				// Artist / Title
				if (item.getArtist() != null) {
					this.artist.setText(item.getArtist());
					this.title.setText(" - " + item.getTitle());
				} else {
					this.artist.setText("");
					this.title.setText(item.getFile().getName());
				}

				// Playing / None
				switch (item.getTmp_status()) {
				case SET_PLAYING:
					if (item.getArtist() != null) {
						this.artist.setText(" »   ".concat(this.artist.getText()));
					} else {
						this.title.setText(" »   ".concat(this.title.getText()));
					}
					break;
				case SET_NONE:
					break;
				default:
					break;
				}
			} else {
				this.artist.setText("");
				this.title.setText("");
				this.duration.setText("");
			}

		}
	}

	@Override
	public void showSearchResultError(final String message) {
		Platform.runLater(new Runnable() {
			public void run() {
				searchResultsListView.getItems().clear();
				searchResultsListView.getItems().add(new MusicListItem(new File(message), null, null));
			}
		});
	}

	@Override
	public void showSearchResults(final ArrayList<MusicListItem> searchResult, final int counter) {
		// Just show it if it's not out of date
		Platform.runLater(new Runnable() {
			public void run() {
				if (counter > searchResultCounter) {
					searchResultCounter = counter;
					searchResultsListView.getItems().clear();
					if (searchResult != null && searchResult.size() > 0) {
						searchResultsListView.getItems().addAll(searchResult);
					}
				}
			}
		});
	}

	public void showInformations(final JSonaArtist artist, final MusicListItem item) {
		Platform.runLater(new Runnable() {
			public void run() {
				// Reset view
				artistLabel.setText("");
				artistLabel.setManaged(false);
				artistLabel.setVisible(false);
				artistBio.setText("");
				artistBio.setManaged(false);
				artistBio.setVisible(false);
				imageContainer.setVisible(false);
				imageContainer.setManaged(false);
				artistImage.setVisible(false);
				artistImage.setManaged(false);
				topTracks.getChildren().clear();
				topTracks.setVisible(false);
				titleLabel.setText("");
				titleLabel.setVisible(false);
				titleLabel.setManaged(false);

				// no artist
				if (item.getArtist() == null) {
					artistLabel.setText(item.getFile().getName());
					artistLabel.setManaged(true);
					artistLabel.setVisible(true);
					return;
				}

				artistLabel.setManaged(true);
				artistLabel.setVisible(true);
				artistLabel.setText(item.getArtist());
				titleLabel.setText(item.getTitle());
				titleLabel.setManaged(true);
				titleLabel.setVisible(true);

				// artist image found
				if (artist != null && artist.getImageFilesystemPath() != null && !artist.getImageFilesystemPath().equals("")) {
					File f = new File(artist.getImageFilesystemPath());
					// image exists
					if (f.exists()) {
						try {
							imageContainer.setVisible(true);
							imageContainer.setManaged(true);
							artistImage.setVisible(true);
							artistImage.setManaged(true);
							artistImage.setImage(new Image(new FileInputStream(f)));
						} catch (FileNotFoundException e2) {
							e2.printStackTrace();
						}
					}
				}

				// artist bio
				if (artist != null && artist.getArtist() != null && artist.getArtist().getWikiSummary() != null) {
					String text = Jsoup.parse(artist.getArtist().getWikiSummary()).text();

					artistBio.setText(text);
					artistBio.setVisible(true);
					artistBio.setManaged(true);
				}

				// Top tracks
				if (artist != null && artist.getTopTracks() != null) {
					topTracks.setVisible(true);

					Collection<Track> top = artist.getTopTracks();
					int trackCounter = 0;

					for (final Track t : top) {
						Hyperlink h = new Hyperlink(t.getName());
						h.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent e) {
								try {
									try {
										BrowserUtil.openWebpage(new URL("http://www.youtube.com/results?search_query=" + URLEncoder.encode(artist.getArtist().getName() + " " + t.getName(), "UTF-8")));
									} catch (UnsupportedEncodingException e1) {
										e1.printStackTrace();
									}
								} catch (MalformedURLException e1) {
									e1.printStackTrace();
								}
							}
						});
						topTracks.getChildren().add(h);
						++trackCounter;
						if (trackCounter == 32) {
							break;
						}
					}
				}
			}
		});
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public Scene getScene() {
		return this.scene;
	}

	protected TabPane getPlaylistTabs() {
		return this.playlistTabs;
	}

	public static class ListItemManager {

		private Random random;
		private int currentItemIndexGuess;
		private MusicListItem currentItem;
		private PlayBackMode playBackMode;
		private ListView<MusicListItem> currentListView;
		private static ListItemManager instance = new ListItemManager();

		public ListItemManager() {
			this.random = new Random();
		}

		public void next(final LogicInterfaceFX logic) {
			final ListView<MusicListItem> clv = this.currentListView;
			final MusicListItem ci = this.currentItem;

			// Fix: That the slider isn't toggling if length changed
			Platform.runLater(new Runnable() {
				public void run() {
					ViewManagerFX.getInstance().getController().durationSlider.setValue(0);
					ViewManagerFX.getInstance().getController().durationProgress.setProgress(0);
				}
			});

			if (clv == null) {
				return; // fuck my life, do nothing
			}

			// Current index
			int oldIndex = -1;
			if (ci == clv.getItems().get(currentItemIndexGuess)) {
				oldIndex = currentItemIndexGuess;
			} else {
				oldIndex = clv.getItems().indexOf(ci);
			}

			// Calc next index
			final int nextIndex = getNextIndex(oldIndex);
			final MusicListItem nextItem = currentListView.getItems().get(nextIndex);
			final MusicListItem oldItem = currentItem;

			Platform.runLater(new Runnable() {
				public void run() {
					// scrollTo is fuckin' slow, don't know how to workarround
					// that
					currentListView.scrollTo(nextIndex);
					currentItem.setTmp_status(Status.SET_NONE);
					nextItem.setTmp_status(Status.SET_PLAYING);
					setCurrentItem(nextItem);
					setCurrentListView(currentListView);
					repaint(currentListView);
					currentListView.getSelectionModel().clearSelection();
					currentListView.getSelectionModel().select(nextIndex);

				}
			});
			logic.event_player_next(oldItem, nextItem);
		}

		public void prev(LogicInterfaceFX logic) {
			if (this.currentListView == null) {
				return; // fuck my life, do nothing
			}

			// Current index
			int oldIndex = -1;
			if (this.currentItem == this.currentListView.getItems().get(currentItemIndexGuess)) {
				oldIndex = currentItemIndexGuess;
			} else {
				oldIndex = this.currentListView.getItems().indexOf(this.currentItem);
			}

			// Calc prev index
			int tmpNextIndex = (oldIndex - 1) % this.currentListView.getItems().size();
			if (tmpNextIndex < 0) {
				tmpNextIndex = this.currentListView.getItems().size() - 1;
			}
			final int prevIndex = tmpNextIndex;
			final MusicListItem nextItem = currentListView.getItems().get(prevIndex);
			final MusicListItem oldItem = currentItem;

			Platform.runLater(new Runnable() {
				public void run() {
					// scrollTo is fuckin' slow, don't know how to workarround
					// that
					currentListView.scrollTo(prevIndex);
					currentItem.setTmp_status(Status.SET_NONE);

					nextItem.setTmp_status(Status.SET_PLAYING);
					setCurrentItem(nextItem);
					setCurrentListView(currentListView);

					repaint(currentListView);

					currentListView.getSelectionModel().clearSelection();
					currentListView.getSelectionModel().select(prevIndex);
				}
			});

			logic.event_player_previous(oldItem, nextItem);
		}

		public void play(LogicInterfaceFX logic, final ListView<MusicListItem> playMeListView, final MusicListItem playMe) {
			ViewManagerFX.getInstance().getController().durationSlider.setValue(0);
			ViewManagerFX.getInstance().getController().durationProgress.setProgress(0);

			final ListView<MusicListItem> listViewOld = ListItemManager.getInstance().getCurrentListView();

			// Is there an item that was played befored?
			MusicListItem oldItem = null;
			if (listViewOld != null) {
				oldItem = ListItemManager.getInstance().getCurrentItem();
				if (oldItem != null) {
					oldItem.setTmp_status(Status.SET_NONE);
				}

				// repaint quickfix, javafx is awesome, but some features are
				// missing!
				Platform.runLater(new Runnable() {
					public void run() {
						repaint(listViewOld);
					}
				});
			}

			final int index = playMeListView.getItems().indexOf(playMe);
			if (playMe == null)
				return;
			playMe.setTmp_status(Status.SET_PLAYING);
			Platform.runLater(new Runnable() {
				public void run() {
					setCurrentListView(playMeListView);
					setCurrentItem(playMe);

					repaint(playMeListView);

					playMeListView.getSelectionModel().clearSelection();
					playMeListView.getSelectionModel().select(index);
				}
			});
			logic.event_player_next(oldItem, playMe);
		}

		public int getNextIndex(int oldIndex) {
			int nextIndex = -1;

			switch (this.playBackMode) {
			case NORMAL:
				nextIndex = (oldIndex + 1) % this.currentListView.getItems().size();
				break;
			case SHUFFLE:
				nextIndex = random.nextInt(this.currentListView.getItems().size());

				// not the same song again pls...
				if (nextIndex == oldIndex) {
					// ok then play the next song
					nextIndex = (nextIndex + 1) % this.currentListView.getItems().size();
				}
				break;
			}
			return nextIndex;
		}

		private void repaint(final ListView<MusicListItem> listView) {
			ObservableList<MusicListItem> items = listView.getItems();
			listView.setItems(null);
			listView.setItems(items);
		}

		public static ListItemManager getInstance() {
			return instance;
		}

		public MusicListItem getCurrentItem() {
			return this.currentItem;
		}

		public void load(final MusicListItem item) {
			currentItem = item;

			Platform.runLater(new Runnable() {
				public void run() {
					int index = currentListView.getItems().indexOf(item);
					currentListView.getSelectionModel().clearSelection();
					currentListView.getSelectionModel().select(index);
					// scrollTo is fuckin' slow, don't know how to workarround
					// that
					currentListView.scrollTo(index);
				}
			});

		}

		public void setCurrentItem(MusicListItem item) {
			this.currentItem = item;
		}

		public ListView<MusicListItem> getCurrentListView() {
			return currentListView;
		}

		public void setCurrentListView(ListView<MusicListItem> currentListView) {
			this.currentListView = currentListView;
		}

		public PlayBackMode getPlayBackMode() {
			return playBackMode;
		}

		public void setPlayBackMode(PlayBackMode playBackMode) {
			this.playBackMode = playBackMode;
		}
	}
}