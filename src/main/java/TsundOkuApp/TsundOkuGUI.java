package TsundOkuApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

public class ColllectionGUI {
	//Collection Components
	private FlowPane collection;
	private Stage settingsWindow;
	private Label bookTypeAndPrintStatus;
	private Text publisher;
	private Text seriesTitle;
	private Text mangaka;
	private Text desc;
	private TextFlow descWrap;
	private Label progressTxt;
	private ProgressBar volProgressBar;
	private FontIcon seriesSettingIcon;
	private Button seriesCardSettingsButton;
	private FontIcon backToSeriesDataIcon;
	private Button backToSeriesCardDataButton;
	private BorderPane rightSideBottomPane;
	private BorderPane rightSideOfSeriesCard;
	private TextArea userNotes;
	private FontIcon deleteButtonIcon;
	private FontIcon changeVolButtonIcon;
	private Button deleteSeriesButton;
	private Button changeVolCountButton;
	private TextField curVolChange;
	private TextField maxVolChange;
	private Button decrementButton;
	private Button incrementButton;
	private HBox seriesCardSettings;

	private static final int SERIES_CARD_WIDTH = 515;
	private static final int SERIES_CARD_HEIGHT = 245;
	private static final int LEFT_SIDE_CARD_WIDTH = 165;
	private static final int RIGHT_SIDE_CARD_WIDTH = SERIES_CARD_WIDTH - LEFT_SIDE_CARD_WIDTH;
	private static final double NAV_HEIGHT = 100;
	private static final double BOTTOM_CARD_HEIGHT = 40;
	private static final int MAX_SERIES_VOLUME_AMOUNT = 999;
	private static final ObservableList<String> LANGUAGE_OPTIONS = FXCollections.observableArrayList("Romaji", "English", "日本語");
	private static final double WINDOW_HEIGHT = Screen.getPrimary().getBounds().getHeight();
	private static final double WINDOW_WIDTH = Screen.getPrimary().getBounds().getWidth();

	private void collectionSetup(Stage primaryStage){
		userCollection = userCollection.stream().sorted().collect(Collectors.toList());
		filteredUserCollection = userCollection;
		totalVolumesCollected = 0;
		maxVolumesInCollection = 0;

		collection = new FlowPane();
		collection.setId("Collection");
		collection.setStyle(collectionMasterCSS);
		collection.setCache(true);
		collection.setCacheHint(CacheHint.DEFAULT);

		ScrollPane collectionScroll = new ScrollPane();
		collectionScroll.setId("CollectionScroll");
		collectionScroll.setCache(true);
		collectionScroll.setCacheHint(CacheHint.DEFAULT);

		for (Series series : filteredUserCollection) {
			Pane seriesCard = new Pane();
			seriesCard.setId("SeriesCard");
			seriesCard.setMinSize(SERIES_CARD_WIDTH, SERIES_CARD_HEIGHT);
			seriesCard.getChildren().addAll(leftSideCardSetup(series), rightSideCardSetup(series, language, primaryStage));
			seriesCard.setCache(true);
			seriesCard.setCacheHint(CacheHint.DEFAULT);
			collection.getChildren().add(seriesCard);
		}

		user.setTotalVolumes(totalVolumesCollected);
		collectionScroll.setContent(collection);
		content.setCenter(collectionScroll);
	}

	private Hyperlink leftSideCardSetup(Series series){
		Rectangle coverImgScaling = new Rectangle(LEFT_SIDE_CARD_WIDTH, SERIES_CARD_HEIGHT);
		coverImgScaling.setId("ImgScaling");
		coverImgScaling.setArcWidth(12);
		coverImgScaling.setArcHeight(12);
		coverImgScaling.setCache(true);
		coverImgScaling.setCacheHint(CacheHint.DEFAULT);

		ImageView cover = new ImageView("File:" + series.getCover());
		cover.setFitHeight(SERIES_CARD_HEIGHT);
		cover.setFitWidth(LEFT_SIDE_CARD_WIDTH);
		cover.setSmooth(true);
		cover.isResizable();
		cover.relocate(0, 0);
		cover.setClip(coverImgScaling);
		cover.setCache(true);
		cover.setCacheHint(CacheHint.QUALITY);

		bookTypeAndPrintStatus = new Label(series.getBookType()+ " | " + series.getPrintStatus());
		bookTypeAndPrintStatus.setPrefHeight(BOTTOM_CARD_HEIGHT);
		bookTypeAndPrintStatus.setPrefWidth(LEFT_SIDE_CARD_WIDTH);
		bookTypeAndPrintStatus.relocate(0, SERIES_CARD_HEIGHT - BOTTOM_CARD_HEIGHT);
		bookTypeAndPrintStatus.setId("SeriesStatus");

		Pane leftSideOfSeriesCard = new Pane();
		leftSideOfSeriesCard.relocate(0, 0);
		leftSideOfSeriesCard.setPrefWidth(LEFT_SIDE_CARD_WIDTH);
		leftSideOfSeriesCard.setPrefHeight(SERIES_CARD_HEIGHT);
		leftSideOfSeriesCard.setMaxSize(LEFT_SIDE_CARD_WIDTH, SERIES_CARD_HEIGHT);
		leftSideOfSeriesCard.setId("LeftSideCard");
		leftSideOfSeriesCard.getChildren().addAll(cover, bookTypeAndPrintStatus);

		Hyperlink aniListLink = new Hyperlink(series.getLink());
		aniListLink.setPrefHeight(SERIES_CARD_HEIGHT);
		aniListLink.setPrefWidth(LEFT_SIDE_CARD_WIDTH);
		aniListLink.setId("AniListLink");
		aniListLink.setOnMouseClicked((MouseEvent event) -> getHostServices().showDocument(aniListLink.getText()));
		aniListLink.setGraphic(leftSideOfSeriesCard);

		return aniListLink;
	}

	private Pane rightSideCardSetup(Series series, char language, Stage primaryStage){
		Integer curVolumes = series.getCurVolumes();
		Integer maxVolumes = series.getMaxVolumes();

		publisher = new Text(series.getPublisher() + "\n");
		publisher.setId("Publisher");

		seriesTitle = new Text();
		seriesTitle.setId("SeriesTitle");

		mangaka = new Text();
		mangaka.setId("Mangaka");

		switch (language){
			case 'R':
				seriesTitle.setText(series.getRomajiTitle());
				mangaka.setText("\n" + series.getRomajiStaff());
				break;
			case 'E':
				seriesTitle.setText(series.getEnglishTitle());
				mangaka.setText("\n" +series.getRomajiStaff());
				break;
			case 'N':
				seriesTitle.setText(series.getNativeTitle());
				mangaka.setText("\n" +series.getNativeStaff());
				break;
			default:
				seriesTitle.setText("Title Error");
				mangaka.setText("\n" + "Staff Error");
				break;
		}

		desc = new Text(series.getSeriesDesc());
		desc.setId("SeriesDescriptionText");

		descWrap = new TextFlow();
		descWrap.setId("SeriesDescriptionWrap");
		descWrap.getChildren().add(desc);

		ScrollPane descScroll = new ScrollPane();
		descScroll.setId("SeriesDescriptionScroll");
		descScroll.setContent(descWrap);

		TextFlow topTextWrap = new TextFlow();
		topTextWrap.setLineSpacing(-2.5);
		topTextWrap.setTextAlignment(TextAlignment.LEFT);
		topTextWrap.getChildren().addAll(publisher, seriesTitle, mangaka);

		double volAmount = (double) curVolumes / maxVolumes;
		DoubleProperty volUpdate = new SimpleDoubleProperty();
		volUpdate.set(volAmount);

		String progTxt = curVolumes + "/" + maxVolumes;
		progressTxt = new Label(progTxt);
		progressTxt.setId("VolProgressTxt");
		progressTxt.setPrefWidth(RIGHT_SIDE_CARD_WIDTH - (RIGHT_SIDE_CARD_WIDTH - 70));
		progressTxt.setCache(true);
		progressTxt.setCacheHint(CacheHint.SPEED);

		decrementButton = new Button("-");
		incrementButton = new Button("+");
		decrementButton.setStyle("-fx-padding: -18 0 -7 0; -fx-font-size: 30;");
		decrementButton.setId("VolProgressButton");
		decrementButton.setOnMouseClicked((MouseEvent event) -> {
			if (series.getCurVolumes() > 0){
				series.setCurVolumes(series.getCurVolumes() - 1);
				Integer seriesCurVolumes = series.getCurVolumes();
				progressTxt.setText(seriesCurVolumes + "/" + maxVolumes);
				volUpdate.set((double) seriesCurVolumes / maxVolumes);
				user.setTotalVolumes(user.getTotalVolumes() - 1);
				updateCollectionNumbers();
				incrementButton.setDisable(false);
			}
		});
		decrementButton.setCache(true);
		decrementButton.setCacheHint(CacheHint.SPEED);

		incrementButton.setStyle("-fx-padding: -12 0 -2 0; -fx-font-size: 27;");
		incrementButton.setId("VolProgressButton");
		incrementButton.setOnMouseClicked((MouseEvent event) -> {
			if (series.getCurVolumes() < maxVolumes){
				series.setCurVolumes(series.getCurVolumes() + 1);
				Integer seriesCurVolumes = series.getCurVolumes();
				progressTxt.setText(seriesCurVolumes + "/" + maxVolumes);
				volUpdate.set((double) seriesCurVolumes / maxVolumes);
				user.setTotalVolumes(user.getTotalVolumes() + 1);
				updateCollectionNumbers();
				decrementButton.setDisable(false);
			}
		});
		incrementButton.setCache(true);
		incrementButton.setCacheHint(CacheHint.SPEED);

		if (curVolumes == 0) { decrementButton.setDisable(true); }
		else if (curVolumes.equals(maxVolumes)) { incrementButton.setDisable(true); }

		HBox userButtons = new HBox();
		userButtons.setAlignment(Pos.CENTER);
		userButtons.setStyle("-fx-background-radius: 0px 0px 5px 0px; -fx-border-radius: 0px 0px 5px 0px;");
		userButtons.setSpacing(6);
		userButtons.getChildren().addAll(decrementButton, incrementButton);

		volProgressBar = new ProgressBar();
		volProgressBar.progressProperty().bind(volUpdate);
		volProgressBar.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 110, BOTTOM_CARD_HEIGHT);
		volProgressBar.setId("ProgressBar");
		volProgressBar.setCache(true);
		volProgressBar.setCacheHint(CacheHint.SPEED);

		BorderPane volProgress = new BorderPane();
		volProgress.setPrefSize(RIGHT_SIDE_CARD_WIDTH - (RIGHT_SIDE_CARD_WIDTH - 70), BOTTOM_CARD_HEIGHT);
		volProgress.setStyle("-fx-background-radius: 0px 0px 5px 0px; -fx-border-radius: 0px 0px 5px 0px;");
		volProgress.setTop(progressTxt);
		volProgress.setBottom(userButtons);

		BorderPane rightSideTopPane = new BorderPane();
		rightSideTopPane.setStyle("-fx-padding: 5 2 4 10;");
		rightSideTopPane.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 4, SERIES_CARD_HEIGHT - BOTTOM_CARD_HEIGHT);
		rightSideTopPane.setTop(topTextWrap);
		rightSideTopPane.setCenter(descScroll);

		seriesSettingIcon = new FontIcon(BootstrapIcons.JOURNAL_TEXT);
		seriesSettingIcon.setIconSize(25);
		seriesSettingIcon.setId("CollectionIcon");

		seriesCardSettingsButton = new Button();
		seriesCardSettingsButton.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 310, BOTTOM_CARD_HEIGHT);
		seriesCardSettingsButton.setGraphic(seriesSettingIcon);
		seriesCardSettingsButton.setId("CollectionIconButton");

		backToSeriesDataIcon = new FontIcon(BootstrapIcons.CARD_HEADING);
		backToSeriesDataIcon.setId("CollectionIcon");
		backToSeriesDataIcon.setIconSize(25);

		backToSeriesCardDataButton = new Button();
		backToSeriesCardDataButton.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 310, BOTTOM_CARD_HEIGHT);
		backToSeriesCardDataButton.setGraphic(backToSeriesDataIcon);
		backToSeriesCardDataButton.setId("CollectionIconButton");

		rightSideBottomPane = new BorderPane();
		rightSideBottomPane.setPrefSize(RIGHT_SIDE_CARD_WIDTH, BOTTOM_CARD_HEIGHT);
		rightSideBottomPane.setId("SeriesCardBottomPane");
		rightSideBottomPane.setStyle(collectionMasterCSS);
		rightSideBottomPane.setLeft(seriesCardSettingsButton);
		rightSideBottomPane.setCenter(volProgressBar);
		rightSideBottomPane.setRight(volProgress);

		rightSideOfSeriesCard = new BorderPane();
		rightSideOfSeriesCard.setId("RightSideCard");
		rightSideOfSeriesCard.setStyle(collectionMasterCSS);
		rightSideOfSeriesCard.setPrefSize(RIGHT_SIDE_CARD_WIDTH, SERIES_CARD_HEIGHT - 50);
		rightSideOfSeriesCard.setMaxSize(RIGHT_SIDE_CARD_WIDTH, SERIES_CARD_HEIGHT - 50);
		rightSideOfSeriesCard.setLayoutX(LEFT_SIDE_CARD_WIDTH);
		rightSideOfSeriesCard.setTop(rightSideTopPane);
		rightSideOfSeriesCard.setBottom(rightSideBottomPane);

		seriesCardSettings = seriesCardSettingsPane(series, primaryStage);
		seriesCardSettingsButton.setOnMouseClicked((MouseEvent event) -> {
			rightSideOfSeriesCard.setTop(seriesCardSettings);
			rightSideBottomPane.setLeft(backToSeriesCardDataButton);
		});
		backToSeriesCardDataButton.setOnMouseClicked((MouseEvent event) -> {
			rightSideOfSeriesCard.setTop(rightSideTopPane);
			rightSideBottomPane.setLeft(seriesCardSettingsButton);
		});

		totalVolumesCollected += curVolumes;
		maxVolumesInCollection += maxVolumes;

		return rightSideOfSeriesCard;
	}

	private HBox seriesCardSettingsPane(Series series, Stage primaryStage){
		userNotes = new TextArea(series.getUserNotes());
		userNotes.setFocusTraversable(false);
		userNotes.setStyle(collectionMasterCSS);
		userNotes.setWrapText(true);
		userNotes.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 40, SERIES_CARD_HEIGHT - (2 * BOTTOM_CARD_HEIGHT));
		userNotes.textProperty().addListener((object, oldText, newText) -> series.setUserNotes(newText));

		deleteButtonIcon = new FontIcon(BootstrapIcons.TRASH);
		deleteButtonIcon.setId("CollectionIcon");
		deleteButtonIcon.setIconSize(30);

		deleteSeriesButton = new Button();
		deleteSeriesButton.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 310, BOTTOM_CARD_HEIGHT - 1);
		deleteSeriesButton.setGraphic(deleteButtonIcon);
		deleteSeriesButton.setId("CollectionIconButton");
		deleteSeriesButton.setOnMouseClicked((MouseEvent event) -> {
			userCollection.removeIf(delSeries -> delSeries.getRomajiTitle().equals(series.getRomajiTitle()));
			collectionSetup(primaryStage);
			primaryStage.setScene(mainScene);
		});

		UnaryOperator<TextFormatter.Change> filter = change -> {
			String text = change.getText();
			if (text.matches("[0-9]")) { return change; }
			return null;
		};

		curVolChange = new TextField();
		curVolChange.setId("CollectionTextField");
		curVolChange.setTextFormatter(new TextFormatter<>(filter));

		maxVolChange = new TextField();
		maxVolChange.setId("CollectionTextField");
		maxVolChange.setTextFormatter(new TextFormatter<>(filter));

		changeVolButtonIcon = new FontIcon(BootstrapIcons.ARROW_REPEAT);
		changeVolButtonIcon.setId("CollectionIcon");
		changeVolButtonIcon.setIconSize(30);

		changeVolCountButton = new Button();
		changeVolCountButton.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 310, BOTTOM_CARD_HEIGHT - 1);
		changeVolCountButton.setGraphic(changeVolButtonIcon);
		changeVolCountButton.setId("CollectionIconButton");
		changeVolCountButton.setOnMouseClicked((MouseEvent event) -> {
			int newMaxVolumeAmount = Integer.parseInt(maxVolChange.getText());
			int newCurVolAmount = Integer.parseInt(curVolChange.getText());
			if (newMaxVolumeAmount > 0 && newMaxVolumeAmount <= MAX_SERIES_VOLUME_AMOUNT){
				series.setMaxVolumes(newMaxVolumeAmount);
			}

			if (newCurVolAmount >= 0 && newCurVolAmount <= series.getMaxVolumes()){
				series.setCurVolumes(newCurVolAmount);
			}
			collectionSetup(primaryStage);
			primaryStage.setScene(mainScene);
		});

		VBox settingsButtons = new VBox();
		settingsButtons.setSpacing(10);
		settingsButtons.setPadding(new Insets(0, 7, 0, 0));
		settingsButtons.setAlignment(Pos.CENTER);
		settingsButtons.getChildren().addAll(deleteSeriesButton, curVolChange, maxVolChange, changeVolCountButton);

		HBox settingsCardPane = new HBox();
		settingsCardPane.setPadding(new Insets(5, 2, 4, 10));
		settingsCardPane.setAlignment(Pos.CENTER);
		settingsCardPane.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 20, SERIES_CARD_HEIGHT - BOTTOM_CARD_HEIGHT);
		settingsCardPane.setSpacing(10);
		settingsCardPane.getChildren().addAll(userNotes, settingsButtons);

		return settingsCardPane;
	}
}