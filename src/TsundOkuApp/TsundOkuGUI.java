/*
    "Icons made by Freepik from www.flatpngn.com"
 */

package TsundOkuApp;

import java.awt.Desktop;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.tbee.javafx.scene.layout.MigPane;

public class TsundOkuGUI{

	// Menu B& Settings Window Components
	private SimpleStringProperty totalVolDisplayUpdate, totalToCollectUpdate;
	private String collectionMasterCSS;
	private final Stage addNewSeriesWindow = new Stage();
	private final Stage themeSettingsWindow = new Stage();
	private final Stage userSettingsWindow = new Stage();
	private TsundOkuTheme finalNewTheme;
	private HBox menuBar;
	private ComboBox<String> languageSelect;

	// Collection Components
	private FlowPane collection;

	// Component Size Details
	private static final int SERIES_CARD_WIDTH = 515;
	private static final int SERIES_CARD_HEIGHT = 245;
	private static final int LEFT_SIDE_CARD_WIDTH = 165;
	private static final int RIGHT_SIDE_CARD_WIDTH = SERIES_CARD_WIDTH - LEFT_SIDE_CARD_WIDTH;
	private static final int NAV_HEIGHT = 100;
	private static final int BOTTOM_CARD_HEIGHT = 38;
	private static final double WINDOW_HEIGHT = Screen.getPrimary().getBounds().getHeight();
	private static final double WINDOW_WIDTH = Screen.getPrimary().getBounds().getWidth();
	private static final ObservableList<String> LANGUAGE_OPTIONS = FXCollections.observableArrayList("Romaji", "English", "Native");

	// Users Main Data
	private Integer totalVolumesCollected = 0, maxVolumesInCollection = 0;
	private List<Series> userCollection;
	private ObservableList<Series> filteredUserCollection;
	private Collector user;
	private TsundOkuTheme mainTheme;
	private BorderPane content;
	private String curTheme = "";
	private ObservableList<String> usersSavedThemes;

	public TsundOkuGUI() { }

	protected void setupTsundOkuGUI(Stage primaryStage) throws CloneNotSupportedException {
		getUsersData();
		filteredUserCollection = FXCollections.observableArrayList(userCollection);
		usersSavedThemes = FXCollections.observableArrayList(user.getSavedThemes().keySet());
		mainTheme = user.getMainTheme();
		collectionMasterCSS = drawTheme(mainTheme);

		content = new BorderPane();
		content.setCache(true);
		content.setMaxSize(WINDOW_WIDTH - 100, WINDOW_HEIGHT - 100);
		content.setCacheHint(CacheHint.SPEED);

		Scene mainScene = new Scene(content);
		mainScene.getStylesheets().addAll("CollectionCSS.css", "MenuCSS.css");

		userSettingsWindow.initStyle(StageStyle.UNIFIED);
		addNewSeriesWindow.initStyle(StageStyle.UNIFIED);
		themeSettingsWindow.initStyle(StageStyle.UNIFIED);
		collectionSetup(primaryStage);
		sortCollection();
		menuSetup(content, primaryStage);

		primaryStage.setMinWidth(SERIES_CARD_WIDTH + 550);
		primaryStage.setMinHeight(SERIES_CARD_HEIGHT + NAV_HEIGHT + 75);
		primaryStage.setTitle("TsundOku");
		primaryStage.getIcons().add(new Image("bookshelf.png"));
		primaryStage.setResizable(true);
		primaryStage.setOnCloseRequest((WindowEvent event) -> {
			storeUserData();
			if (addNewSeriesWindow.isShowing()) { addNewSeriesWindow.close(); }
			if (themeSettingsWindow.isShowing()) { themeSettingsWindow.close(); }
			if (userSettingsWindow.isShowing()) { userSettingsWindow.close(); }
		});
		primaryStage.initStyle(StageStyle.UNIFIED);
		primaryStage.setScene(mainScene);
		primaryStage.show();
	}

	private void sortCollection(){
		switch (user.getCurLanguage()) {
			case "English":
				userCollection.sort(Series::compareByEnglishTitle);
				break;
			case "Native":
				userCollection.sort(Series::compareByNativeTitle);
				break;
			default:
				userCollection.sort(Series::compareByRomajiTitle);
				break;
		}
	}

	private void storeUserData(){
		try {
			ObjectOutputStream outputNewSeries = new ObjectOutputStream(new FileOutputStream("UserData.dat"));
			user.setCollection(userCollection);
			outputNewSeries.writeObject(user);
			outputNewSeries.flush();
			outputNewSeries.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void getUsersData(){
		File collectionFile = new File("UserData.dat");
		if (!collectionFile.exists()) {
			new File("Covers").mkdir();
			user = new Collector("Default UserName", "Romaji", TsundOkuTheme.DEFAULT_THEME, new HashMap<>(), new ArrayList<>());
			user.addNewTheme(TsundOkuTheme.DEFAULT_THEME);
			storeUserData();
		}
		try {
			ObjectInputStream getUserObject = new ObjectInputStream(new FileInputStream("UserData.dat"));
			user = (Collector) getUserObject.readObject();
			userCollection = user.getCollection();
			mainTheme = user.getMainTheme();
			getUserObject.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void menuSetup(BorderPane content, Stage primaryStage) throws CloneNotSupportedException {
		userSettingsWindow(primaryStage);
		setupCollectionSettingsWindow(primaryStage);
		addNewSeriesWindow(primaryStage);
		Text userName = new Text(user.getUserName());
		userName.setId("UserName");

		FontIcon userSettingsIcon = new FontIcon(BootstrapIcons.PERSON_CIRCLE);
		userSettingsIcon.setIconSize(20);
		userSettingsIcon.setId("UserSettingsIcon");

		Button userSettingsButton = new Button();
		userSettingsButton.setPrefWidth(67.5);
		userSettingsButton.setGraphic(userSettingsIcon);
		userSettingsButton.setId("MenuButton");
		userSettingsButton.setOnMouseClicked(event -> {
			if (userSettingsWindow.isShowing()){
				userSettingsWindow.toFront();
			} else {
				userSettingsWindow.show();
			}
		});

		FontIcon themeSettingsIcon = new FontIcon(BootstrapIcons.PALETTE2);
		themeSettingsIcon.setIconSize(20);
		themeSettingsIcon.setId("ThemeSettingsIcon");

		Button themeSettingsButton = new Button();
		themeSettingsButton.setPrefWidth(67.5);
		themeSettingsButton.setId("MenuButton");
		themeSettingsButton.setGraphic(themeSettingsIcon);
		themeSettingsButton.setOnMouseClicked(event -> {
			if (themeSettingsWindow.isShowing()){
				themeSettingsWindow.toFront();
			} else {
				themeSettingsWindow.show();
			}
		});

		HBox settingsRoot = new HBox(userSettingsButton, themeSettingsButton);
		settingsRoot.setAlignment(Pos.CENTER);
		settingsRoot.setPrefWidth(135);
		settingsRoot.setSpacing(2);

		VBox userNameAndSettingsButtonLayout = new VBox(userName, settingsRoot);
		userNameAndSettingsButtonLayout.setId("UserNameAndSettingsButtonLayout");

		Label searchLabel = new Label("Search Collection");
		searchLabel.setPrefWidth(203);
		searchLabel.setId("MenuLabel");

		TextField titleSearch = new TextField();
		titleSearch.setId("CollectionSearchField");
		titleSearch.setCache(true);
		titleSearch.setCacheHint(CacheHint.SPEED);

		FontIcon searchButtonIcon = new FontIcon(BootstrapIcons.SEARCH);
		searchButtonIcon.setIconSize(20);
		searchButtonIcon.setId("ThemeSettingsIcon");

		Button searchButton = new Button();
		searchButton.setPrefWidth(30);
		searchButton.setId("MenuButton");
		searchButton.setGraphic(searchButtonIcon);
		searchButton.setDefaultButton(true);
		searchButton.setOnAction(event -> {
			if (!titleSearch.getText().isEmpty()){
				String newText = titleSearch.getText();
				filteredUserCollection = FXCollections.observableArrayList(userCollection.parallelStream().filter(series -> containsIgnoreCase(series.getRomajiTitle(), newText) | containsIgnoreCase(series.getEnglishTitle(), newText) | containsIgnoreCase(series.getNativeTitle(), newText) | containsIgnoreCase(series.getRomajiStaff(), newText) | containsIgnoreCase(series.getNativeStaff(), newText) | containsIgnoreCase(series.getPublisher(), newText) | containsIgnoreCase(series.getBookType(), newText) | containsIgnoreCase(series.getPrintStatus(), newText)).collect(Collectors.toList()));
			} else {
				filteredUserCollection = FXCollections.observableArrayList(userCollection);
			}
			collectionSetup(primaryStage);
			updateCollectionNumbers();
		});

		HBox searchPane = new HBox(titleSearch, searchButton);
		searchPane.setAlignment(Pos.CENTER);
		searchPane.setPrefWidth(200);
		searchPane.setSpacing(2);


		GridPane searchLayout = new GridPane();
		searchLayout.setAlignment(Pos.CENTER);
		searchLayout.add(searchLabel, 0, 0);
		searchLayout.add(searchPane, 0 ,1);

		totalVolDisplayUpdate = new SimpleStringProperty("Collected\n" + user.getTotalVolumes() + " Volumes");
		Text totalVolDisplay = new Text();
		totalVolDisplay.textProperty().bind(totalVolDisplayUpdate);
		totalVolDisplay.setId("MenuText");
		totalVolDisplay.setCache(true);
		totalVolDisplay.setCacheHint(CacheHint.SPEED);

		totalToCollectUpdate = new SimpleStringProperty("Need To Collect\n" + (maxVolumesInCollection - user.getTotalVolumes()) + " Volumes");
		Text totalToCollect = new Text();
		totalToCollect.textProperty().bind(totalToCollectUpdate);
		totalToCollect.setId("MenuText");
		totalToCollect.setCache(true);
		totalToCollect.setCacheHint(CacheHint.SPEED);

		ToggleButton addNewSeriesButton = new ToggleButton("Add New Series");
		addNewSeriesButton.setOnMouseClicked((MouseEvent event) -> {
			if (addNewSeriesWindow.isShowing()){
				addNewSeriesWindow.toFront();
			} else {
				addNewSeriesWindow.show();
			}
		});
		addNewSeriesButton.setId("MenuButton");

		languageSelect = new ComboBox<>(LANGUAGE_OPTIONS);
		languageSelect.setPrefWidth(135);
		languageSelect.setValue(user.getCurLanguage());
		languageSelect.setOnAction((event) -> {
			user.setCurLanguage(languageSelect.getValue());
			sortCollection();
			filteredUserCollection = FXCollections.observableArrayList(userCollection);
			collectionSetup(primaryStage);
		});

		VBox addSeriesAndLanguageLayout = new VBox(addNewSeriesButton, languageSelect);
		addSeriesAndLanguageLayout.setAlignment(Pos.CENTER);
		addSeriesAndLanguageLayout.setSpacing(5);

		menuBar = new HBox(userNameAndSettingsButtonLayout, totalVolDisplay, searchLayout, totalToCollect, addSeriesAndLanguageLayout);
		menuBar.setPrefHeight(NAV_HEIGHT);
		menuBar.setId("MenuBar");
		menuBar.setStyle(collectionMasterCSS);
		content.setTop(menuBar);
	}

	private void updateCollectionNumbers(){
		totalVolDisplayUpdate.set("Collected\n" + user.getTotalVolumes() + " Volumes");
		totalToCollectUpdate.set("Need To Collect\n" + (maxVolumesInCollection - user.getTotalVolumes()) + " Volumes");
	}

	private void userSettingsWindow(Stage primaryStage){
		TextField enterUserName = new TextField();
		enterUserName.setId("MenuTextField");
		enterUserName.setPrefWidth(250);
		enterUserName.lengthProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.intValue() > oldValue.intValue()) {
				if (enterUserName.getText().length() >= 20) {
					enterUserName.setText(enterUserName.getText().substring(0, 20));
				}
			}
		});

		Label enterUserNameLabel = new Label("Change User Name");
		enterUserNameLabel.setId("MenuLabel");
		enterUserNameLabel.setLabelFor(enterUserName);

		Button saveUserNameButton = new Button("Change");
		saveUserNameButton.setId("MenuButton");
		saveUserNameButton.disableProperty().bind(enterUserName.textProperty().isEmpty());
		saveUserNameButton.setOnMouseClicked(event -> {
			user.setUserName(enterUserName.getText());
			try {
				menuSetup(content, primaryStage);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		});

		HBox changeUserNameRoot = new HBox(enterUserName, saveUserNameButton);
		changeUserNameRoot.setSpacing(5);

		VBox userNameRoot = new VBox(enterUserNameLabel, changeUserNameRoot);
		userNameRoot.setId("SettingsLabel");

		Button deleteCollectionButton = new Button("Delete Collection");
		deleteCollectionButton.setId("MenuButton");
		deleteCollectionButton.setOnMouseClicked(event -> {
			userCollection.clear();
			filteredUserCollection = FXCollections.observableArrayList(userCollection);
			collectionSetup(primaryStage);
		});

		VBox userSettingsPane = new VBox(userNameRoot, deleteCollectionButton);
		userSettingsPane.setSpacing(100);
		userSettingsPane.setId("NewSeriesPane");
		userSettingsPane.setStyle(collectionMasterCSS);
		userSettingsPane.setCache(true);
		userSettingsPane.setCacheHint(CacheHint.SPEED);

		Scene userSettingsScene = new Scene(userSettingsPane);
		userSettingsScene.getStylesheets().add("MenuCSS.css");

		userSettingsWindow.setResizable(false);
		userSettingsWindow.getIcons().add(new Image("bookshelf.png"));
		userSettingsWindow.setTitle(user.getUserName() + " Settings");
		userSettingsWindow.setScene(userSettingsScene);
	}

	private static boolean containsIgnoreCase(String str, String searchStr){
		if (str == null || searchStr == null) {
			return false;
		}
		int len = searchStr.length();
		int max = str.length() - len;
		for (int i = 0; i <= max; i++) {
			if (str.regionMatches(true, i, searchStr, 0, len)) {
				return true;
			}
		}
		return false;
	}

	private void setupCollectionSettingsWindow(Stage primaryStage) throws CloneNotSupportedException {
		finalNewTheme = (TsundOkuTheme) mainTheme.clone();

		ColorPicker menuBGColor = new ColorPicker();
		menuBGColor.setPrefWidth(181);
		menuBGColor.setValue(convertStringToColor(mainTheme.getMenuBGColor()));
		menuBGColor.setOnAction(event -> {
			finalNewTheme.setMenuBGColor(formatColorCode(menuBGColor.getValue()));
			menuBar.setStyle(drawTheme(finalNewTheme));
		});

		Label menuBGColorLabel = new Label("Menu BG");
		menuBGColorLabel.setLabelFor(menuBGColor);
		menuBGColorLabel.setId("SettingsTextStyling");

		VBox menuBGColorRoot = new VBox(menuBGColorLabel, menuBGColor);
		menuBGColorRoot.setSpacing(2);

		ColorPicker userNameColor = new ColorPicker();
		userNameColor.setPrefWidth(181);
		userNameColor.setValue(convertStringToColor(mainTheme.getUserNameColor()));
		userNameColor.setOnAction(event -> {
			finalNewTheme.setUserNameColor(formatColorCode(userNameColor.getValue()));
			menuBar.setStyle(drawTheme(finalNewTheme));
		});

		Label userNameColorLabel = new Label("User Name");
		userNameColorLabel.setLabelFor(userNameColor);
		userNameColorLabel.setId("SettingsTextStyling");

		VBox userNameColorRoot = new VBox(userNameColorLabel, userNameColor);
		userNameColorRoot.setSpacing(2);

		ColorPicker userNormalSettingsIconColor = new ColorPicker();
		userNormalSettingsIconColor.setPrefWidth(181);
		userNormalSettingsIconColor.setValue(convertStringToColor(mainTheme.getUserNormalSettingsIconColor()));
		userNormalSettingsIconColor.setOnAction(event -> {
			finalNewTheme.setUserNormalSettingsIconColor(formatColorCode(userNormalSettingsIconColor.getValue()));
			menuBar.setStyle(drawTheme(finalNewTheme));
		});

		Label userNormalSettingsIconColorLabel = new Label("User Settings Icon");
		userNormalSettingsIconColorLabel.setLabelFor(userNormalSettingsIconColor);
		userNormalSettingsIconColorLabel.setId("SettingsTextStyling");

		VBox userNormalSettingsIconColorRoot = new VBox(userNormalSettingsIconColorLabel, userNormalSettingsIconColor);
		userNormalSettingsIconColorRoot.setSpacing(2);

		ColorPicker userHoverSettingsIconColor = new ColorPicker();
		userHoverSettingsIconColor.setPrefWidth(181);
		userHoverSettingsIconColor.setValue(convertStringToColor(mainTheme.getUserHoverSettingsIconColor()));
		userHoverSettingsIconColor.setOnAction(event -> {
			finalNewTheme.setUserHoverSettingsIconColor(formatColorCode(userHoverSettingsIconColor.getValue()));
			menuBar.setStyle(drawTheme(finalNewTheme));
		});

		Label userHoverSettingsIconColorLabel = new Label("User Settings Icon (Hover)");
		userHoverSettingsIconColorLabel.setLabelFor(userHoverSettingsIconColor);
		userHoverSettingsIconColorLabel.setId("SettingsTextStyling");

		VBox userHoverSettingsIconColorRoot = new VBox(userHoverSettingsIconColorLabel, userHoverSettingsIconColor);
		userHoverSettingsIconColorRoot.setSpacing(2);

		ColorPicker themeNormalSettingsIconColor = new ColorPicker();
		themeNormalSettingsIconColor.setPrefWidth(181);
		themeNormalSettingsIconColor.setValue(convertStringToColor(mainTheme.getThemeNormalSettingsIconColor()));
		themeNormalSettingsIconColor.setOnAction(event -> {
			finalNewTheme.setThemeNormalSettingsIconColor(formatColorCode(themeNormalSettingsIconColor.getValue()));
			menuBar.setStyle(drawTheme(finalNewTheme));
		});

		Label themeNormalSettingsIconColorLabel = new Label("Theme Settings Icon");
		themeNormalSettingsIconColorLabel.setLabelFor(themeNormalSettingsIconColor);
		themeNormalSettingsIconColorLabel.setId("SettingsTextStyling");

		VBox themeNormalSettingsIconColorRoot = new VBox(themeNormalSettingsIconColorLabel, themeNormalSettingsIconColor);
		themeNormalSettingsIconColorRoot.setSpacing(2);

		ColorPicker themeHoverSettingsIconColor = new ColorPicker();
		themeHoverSettingsIconColor.setPrefWidth(181);
		themeHoverSettingsIconColor.setValue(convertStringToColor(mainTheme.getThemeHoverSettingsIconColor()));
		themeHoverSettingsIconColor.setOnAction(event -> {
			finalNewTheme.setThemeHoverSettingsIconColor(formatColorCode(themeHoverSettingsIconColor.getValue()));
			menuBar.setStyle(drawTheme(finalNewTheme));
		});

		Label themeHoverSettingsIconColorLabel = new Label("Theme Settings Icon (Hover)");
		themeHoverSettingsIconColorLabel.setLabelFor(themeHoverSettingsIconColor);
		themeHoverSettingsIconColorLabel.setId("SettingsTextStyling");

		VBox themeHoverSettingsIconColorRoot = new VBox(themeHoverSettingsIconColorLabel, themeHoverSettingsIconColor);
		themeHoverSettingsIconColorRoot.setSpacing(2);

		ColorPicker menuCollectionSearchBorderColor = new ColorPicker();
		menuCollectionSearchBorderColor.setPrefWidth(181);
		menuCollectionSearchBorderColor.setValue(convertStringToColor(mainTheme.getCollectionSearchBorderColor()));
		menuCollectionSearchBorderColor.setOnAction(event -> {
			finalNewTheme.setCollectionSearchBorderColor(formatColorCode(menuCollectionSearchBorderColor.getValue()));
			menuBar.setStyle(drawTheme(finalNewTheme));
		});

		Label menuCollectionSearchBorderColorLabel = new Label("Collection Search Border");
		menuCollectionSearchBorderColorLabel.setLabelFor(menuCollectionSearchBorderColor);
		menuCollectionSearchBorderColorLabel.setId("SettingsTextStyling");

		VBox menuCollectionSearchBorderColorRoot = new VBox(menuCollectionSearchBorderColorLabel, menuCollectionSearchBorderColor);
		menuCollectionSearchBorderColorRoot.setSpacing(2);

		ColorPicker menuCollectionSearchBGColor = new ColorPicker();
		menuCollectionSearchBGColor.setPrefWidth(181);
		menuCollectionSearchBGColor.setValue(convertStringToColor(mainTheme.getCollectionSearchBGColor()));
		menuCollectionSearchBGColor.setOnAction(event -> {
			finalNewTheme.setCollectionSearchBGColor(formatColorCode(menuCollectionSearchBGColor.getValue()));
			menuBar.setStyle(drawTheme(finalNewTheme));
		});

		Label menuCollectionSearchBGColorLabel = new Label("Collection Search BG");
		menuCollectionSearchBGColorLabel.setLabelFor(menuCollectionSearchBGColor);
		menuCollectionSearchBGColorLabel.setId("SettingsTextStyling");

		VBox menuCollectionSearchBGColorRoot = new VBox(menuCollectionSearchBGColorLabel, menuCollectionSearchBGColor);
		menuCollectionSearchBGColorRoot.setSpacing(2);

		ColorPicker menuCollectionSearchTextColor = new ColorPicker();
		menuCollectionSearchTextColor.setPrefWidth(181);
		menuCollectionSearchTextColor.setValue(convertStringToColor(mainTheme.getCollectionSearchTextColor()));
		menuCollectionSearchTextColor.setOnAction(event -> {
			finalNewTheme.setCollectionSearchTextColor(formatColorCode(menuCollectionSearchTextColor.getValue()));
			menuBar.setStyle(drawTheme(finalNewTheme));
		});

		Label menuCollectionSearchTextColorLabel = new Label("Collection Search Text");
		menuCollectionSearchTextColorLabel.setLabelFor(menuCollectionSearchTextColor);
		menuCollectionSearchTextColorLabel.setId("SettingsTextStyling");

		VBox menuCollectionSearchTextColorRoot = new VBox(menuCollectionSearchTextColorLabel, menuCollectionSearchTextColor);
		menuCollectionSearchTextColorRoot.setSpacing(2);

		ColorPicker menuBottomBorderColor = new ColorPicker();
		menuBottomBorderColor.setPrefWidth(181);
		menuBottomBorderColor.setValue(convertStringToColor(mainTheme.getMenuBottomBorderColor()));
		menuBottomBorderColor.setOnAction(event -> {
			finalNewTheme.setMenuBottomBorderColor(formatColorCode(menuBottomBorderColor.getValue()));
			menuBar.setStyle(drawTheme(finalNewTheme));
		});

		Label menuBottomBorderColorLabel = new Label("Divider");
		menuBottomBorderColorLabel.setLabelFor(menuBottomBorderColor);
		menuBottomBorderColorLabel.setId("SettingsTextStyling");

		VBox menuBottomBorderColorRoot = new VBox(menuBottomBorderColorLabel, menuBottomBorderColor);
		menuBottomBorderColorRoot.setSpacing(2);

		ColorPicker menuTextColor = new ColorPicker();
		menuTextColor.setPrefWidth(181);
		menuTextColor.setValue(convertStringToColor(mainTheme.getMenuTextColor()));
		menuTextColor.setOnAction(event -> {
			finalNewTheme.setMenuTextColor(formatColorCode(menuTextColor.getValue()));
			menuBar.setStyle(drawTheme(finalNewTheme));
		});

		Label menuTextColorLabel = new Label("Menu Text");
		menuTextColorLabel.setLabelFor(menuTextColor);
		menuTextColorLabel.setId("SettingsTextStyling");

		VBox menuTextColorRoot = new VBox(menuTextColorLabel, menuTextColor);
		menuTextColorRoot.setSpacing(2);

		ColorPicker menuNormalButtonBGColor = new ColorPicker();
		menuNormalButtonBGColor.setPrefWidth(181);
		menuNormalButtonBGColor.setValue(convertStringToColor(mainTheme.getMenuNormalButtonBGColor()));
		menuNormalButtonBGColor.setOnAction(event -> {
			finalNewTheme.setMenuNormalButtonBGColor(formatColorCode(menuNormalButtonBGColor.getValue()));
			menuBar.setStyle(drawTheme(finalNewTheme));
		});

		Label menuNormalButtonBGColorLabel = new Label("Menu Button BG");
		menuNormalButtonBGColorLabel.setLabelFor(menuNormalButtonBGColor);
		menuNormalButtonBGColorLabel.setId("SettingsTextStyling");

		VBox menuNormalButtonBGColorRoot = new VBox(menuNormalButtonBGColorLabel, menuNormalButtonBGColor);
		menuNormalButtonBGColorRoot.setSpacing(2);

		ColorPicker menuHoverButtonBGColor = new ColorPicker();
		menuHoverButtonBGColor.setPrefWidth(181);
		menuHoverButtonBGColor.setValue(convertStringToColor(mainTheme.getMenuHoverButtonBGColor()));
		menuHoverButtonBGColor.setOnAction(event -> {
			finalNewTheme.setMenuHoverButtonBGColor(formatColorCode(menuHoverButtonBGColor.getValue()));
			menuBar.setStyle(drawTheme(finalNewTheme));
		});

		Label menuHoverButtonBGColorLabel = new Label("Menu Button BG Color (Hover)");
		menuHoverButtonBGColorLabel.setLabelFor(menuHoverButtonBGColor);
		menuHoverButtonBGColorLabel.setId("SettingsTextStyling");

		VBox menuHoverButtonBGColorRoot = new VBox(menuHoverButtonBGColorLabel, menuHoverButtonBGColor);
		menuHoverButtonBGColorRoot.setSpacing(2);

		ColorPicker menuNormalButtonBorderColor = new ColorPicker();
		menuNormalButtonBorderColor.setPrefWidth(181);
		menuNormalButtonBorderColor.setValue(convertStringToColor(mainTheme.getMenuNormalButtonBorderColor()));
		menuNormalButtonBorderColor.setOnAction(event -> {
			finalNewTheme.setMenuNormalButtonBorderColor(formatColorCode(menuNormalButtonBorderColor.getValue()));
			menuBar.setStyle(drawTheme(finalNewTheme));
		});

		Label menuNormalButtonBorderColorLabel = new Label("Menu Button Border");
		menuNormalButtonBorderColorLabel.setLabelFor(menuNormalButtonBorderColor);
		menuNormalButtonBorderColorLabel.setId("SettingsTextStyling");

		VBox menuNormalButtonBorderColorRoot = new VBox(menuNormalButtonBorderColorLabel, menuNormalButtonBorderColor);
		menuNormalButtonBorderColorRoot.setSpacing(2);

		ColorPicker menuHoverButtonBorderColor = new ColorPicker();
		menuHoverButtonBorderColor.setPrefWidth(181);
		menuHoverButtonBorderColor.setValue(convertStringToColor(mainTheme.getMenuHoverButtonBorderColor()));
		menuHoverButtonBorderColor.setOnAction(event -> {
			finalNewTheme.setMenuHoverButtonBorderColor(formatColorCode(menuHoverButtonBorderColor.getValue()));
			menuBar.setStyle(drawTheme(finalNewTheme));
		});

		Label menuHoverButtonBorderColorLabel = new Label("Menu Button Border Color (Hover)");
		menuHoverButtonBorderColorLabel.setLabelFor(menuHoverButtonBorderColor);
		menuHoverButtonBorderColorLabel.setId("SettingsTextStyling");

		VBox menuHoverButtonBorderColorRoot = new VBox(menuHoverButtonBorderColorLabel, menuHoverButtonBorderColor);
		menuHoverButtonBorderColorRoot.setSpacing(2);

		ColorPicker menuNormalButtonTextColor = new ColorPicker();
		menuNormalButtonTextColor.setPrefWidth(181);
		menuNormalButtonTextColor.setValue(convertStringToColor(mainTheme.getMenuNormalButtonTextColor()));
		menuNormalButtonTextColor.setOnAction(event -> {
			finalNewTheme.setMenuNormalButtonTextColor(formatColorCode(menuNormalButtonTextColor.getValue()));
			menuBar.setStyle(drawTheme(finalNewTheme));
		});

		Label menuNormalButtonTextColorLabel = new Label("Menu Button Text");
		menuNormalButtonTextColorLabel.setLabelFor(menuNormalButtonTextColor);
		menuNormalButtonTextColorLabel.setId("SettingsTextStyling");

		VBox menuNormalButtonTextColorRoot = new VBox(menuNormalButtonTextColorLabel, menuNormalButtonTextColor);
		menuNormalButtonTextColorRoot.setSpacing(2);

		ColorPicker menuHoverButtonTextColor = new ColorPicker();
		menuHoverButtonTextColor.setPrefWidth(181);
		menuHoverButtonTextColor.setValue(convertStringToColor(mainTheme.getMenuHoverButtonTextColor()));
		menuHoverButtonTextColor.setOnAction(event -> {
			finalNewTheme.setMenuHoverButtonTextColor(formatColorCode(menuHoverButtonTextColor.getValue()));
			menuBar.setStyle(drawTheme(finalNewTheme));
		});

		Label menuHoverButtonTextColorLabel = new Label("Menu Button Text Color (Hover)");
		menuHoverButtonTextColorLabel.setLabelFor(menuHoverButtonTextColor);
		menuHoverButtonTextColorLabel.setId("SettingsTextStyling");

		VBox menuHoverButtonTextColorRoot = new VBox(menuHoverButtonTextColorLabel, menuHoverButtonTextColor);
		menuHoverButtonTextColorRoot.setSpacing(2);

		FlowPane menuThemeChangePane = new FlowPane(menuBGColorRoot, userNameColorRoot, userNormalSettingsIconColorRoot, userHoverSettingsIconColorRoot, themeNormalSettingsIconColorRoot, themeHoverSettingsIconColorRoot, menuCollectionSearchBorderColorRoot, menuCollectionSearchBGColorRoot, menuCollectionSearchTextColorRoot, menuBottomBorderColorRoot, menuTextColorRoot, menuNormalButtonBGColorRoot, menuHoverButtonBGColorRoot, menuNormalButtonBorderColorRoot, menuHoverButtonBorderColorRoot, menuNormalButtonTextColorRoot, menuHoverButtonTextColorRoot);
		menuThemeChangePane.setId("ThemeSettingsBox");

		Label menuLabel = new Label("Menu Theme");
		menuLabel.setLabelFor(menuThemeChangePane);
		menuLabel.setId("SettingsLabelStyling");

		ColorPicker collectionBGColor = new ColorPicker();
		collectionBGColor.setPrefWidth(181);
		collectionBGColor.setValue(convertStringToColor(mainTheme.getCollectionBGColor()));
		collectionBGColor.setOnAction(event -> {
			finalNewTheme.setCollectionBGColor(formatColorCode(collectionBGColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionBGColorLabel = new Label("Collection BG");
		collectionBGColorLabel.setLabelFor(collectionBGColor);
		collectionBGColorLabel.setId("SettingsTextStyling");

		VBox collectionBGColorRoot = new VBox(collectionBGColorLabel, collectionBGColor);
		collectionBGColorRoot.setSpacing(2);

		ColorPicker collectionLinkNormalBGColor = new ColorPicker();
		collectionLinkNormalBGColor.setPrefWidth(181);
		collectionLinkNormalBGColor.setValue(convertStringToColor(mainTheme.getCollectionLinkNormalBGColor()));
		collectionLinkNormalBGColor.setOnAction(event -> {
			finalNewTheme.setCollectionLinkNormalBGColor(formatColorCode(collectionLinkNormalBGColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionLinkNormalBGColorLabel = new Label("Link BG");
		collectionLinkNormalBGColorLabel.setLabelFor(collectionLinkNormalBGColor);
		collectionLinkNormalBGColorLabel.setId("SettingsTextStyling");

		VBox collectionLinkNormalBGColorRoot = new VBox(collectionLinkNormalBGColorLabel, collectionLinkNormalBGColor);
		collectionLinkNormalBGColorRoot.setSpacing(2);

		ColorPicker collectionLinkHoverBGColor = new ColorPicker();
		collectionLinkHoverBGColor.setPrefWidth(181);
		collectionLinkHoverBGColor.setValue(convertStringToColor(mainTheme.getCollectionLinkHoverBGColor()));
		collectionLinkHoverBGColor.setOnAction(event -> {
			finalNewTheme.setCollectionLinkHoverBGColor(formatColorCode(collectionLinkHoverBGColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionLinkHoverBGColorLabel = new Label("Link BG Color (Hover)");
		collectionLinkHoverBGColorLabel.setLabelFor(collectionLinkHoverBGColor);
		collectionLinkHoverBGColorLabel.setId("SettingsTextStyling");

		VBox collectionLinkHoverBGColorRoot = new VBox(collectionLinkHoverBGColorLabel, collectionLinkHoverBGColor);
		collectionLinkHoverBGColorRoot.setSpacing(2);

		ColorPicker collectionLinkNormalTextColor = new ColorPicker();
		collectionLinkNormalTextColor.setPrefWidth(181);
		collectionLinkNormalTextColor.setValue(convertStringToColor(mainTheme.getCollectionLinkNormalTextColor()));
		collectionLinkNormalTextColor.setOnAction(event -> {
			finalNewTheme.setCollectionLinkNormalTextColor(formatColorCode(collectionLinkNormalTextColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionLinkNormalTextColorLabel = new Label("Print & Book Type Text");
		collectionLinkNormalTextColorLabel.setLabelFor(collectionLinkNormalTextColor);
		collectionLinkNormalTextColorLabel.setId("SettingsTextStyling");

		VBox collectionLinkNormalTextColorRoot = new VBox(collectionLinkNormalTextColorLabel, collectionLinkNormalTextColor);
		collectionLinkNormalTextColorRoot.setSpacing(2);

		ColorPicker collectionLinkHoverTextColor = new ColorPicker();
		collectionLinkHoverTextColor.setPrefWidth(181);
		collectionLinkHoverTextColor.setValue(convertStringToColor(mainTheme.getCollectionLinkHoverTextColor()));
		collectionLinkHoverTextColor.setOnAction(event -> {
			finalNewTheme.setCollectionLinkHoverTextColor(formatColorCode(collectionLinkHoverTextColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionLinkHoverTextColorLabel = new Label("Print & Book Type Text (Hover)");
		collectionLinkHoverTextColorLabel.setLabelFor(collectionLinkHoverTextColor);
		collectionLinkHoverTextColorLabel.setId("SettingsTextStyling");

		VBox collectionLinkHoverTextColorRoot = new VBox(collectionLinkHoverTextColorLabel, collectionLinkHoverTextColor);
		collectionLinkHoverTextColorRoot.setSpacing(2);

		ColorPicker collectionMainCardBGColor = new ColorPicker();
		collectionMainCardBGColor.setPrefWidth(181);
		collectionMainCardBGColor.setValue(convertStringToColor(mainTheme.getCollectionCardMainBGColor()));
		collectionMainCardBGColor.setOnAction(event -> {
			finalNewTheme.setCollectionCardMainBGColor(formatColorCode(collectionMainCardBGColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionMainCardBGColorLabel = new Label("Series Card BG");
		collectionMainCardBGColorLabel.setLabelFor(collectionMainCardBGColor);
		collectionMainCardBGColorLabel.setId("SettingsTextStyling");

		VBox collectionMainCardBGColorRoot = new VBox(collectionMainCardBGColorLabel, collectionMainCardBGColor);
		collectionMainCardBGColorRoot.setSpacing(2);

		ColorPicker collectionTitleColor = new ColorPicker();
		collectionTitleColor.setPrefWidth(181);
		collectionTitleColor.setValue(convertStringToColor(mainTheme.getCollectionTitleColor()));
		collectionTitleColor.setOnAction(event -> {
			finalNewTheme.setCollectionTitleColor(formatColorCode(collectionTitleColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionTitleColorLabel = new Label("Series Title");
		collectionTitleColorLabel.setLabelFor(collectionTitleColor);
		collectionTitleColorLabel.setId("SettingsTextStyling");

		VBox collectionTitleColorRoot = new VBox(collectionTitleColorLabel, collectionTitleColor);
		collectionTitleColorRoot.setSpacing(2);

		ColorPicker collectionPublisherColor = new ColorPicker();
		collectionPublisherColor.setPrefWidth(181);
		collectionPublisherColor.setValue(convertStringToColor(mainTheme.getCollectionPublisherColor()));
		collectionPublisherColor.setOnAction(event -> {
			finalNewTheme.setCollectionPublisherColor(formatColorCode(collectionPublisherColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionPublisherColorLabel = new Label("Publisher");
		collectionPublisherColorLabel.setLabelFor(collectionPublisherColor);
		collectionPublisherColorLabel.setId("SettingsTextStyling");

		VBox collectionPublisherColorRoot = new VBox(collectionPublisherColorLabel, collectionPublisherColor);
		collectionPublisherColorRoot.setSpacing(2);

		ColorPicker collectionMangakaColor = new ColorPicker();
		collectionMangakaColor.setPrefWidth(181);
		collectionMangakaColor.setValue(convertStringToColor(mainTheme.getCollectionMangakaColor()));
		collectionMangakaColor.setOnAction(event -> {
			finalNewTheme.setCollectionMangakaColor(formatColorCode(collectionMangakaColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionMangakaColorLabel = new Label("Mangaka");
		collectionMangakaColorLabel.setLabelFor(collectionMangakaColor);
		collectionMangakaColorLabel.setId("SettingsTextStyling");

		VBox collectionMangakaColorRoot = new VBox(collectionMangakaColorLabel, collectionMangakaColor);
		collectionMangakaColorRoot.setSpacing(2);

		ColorPicker collectionDescColor = new ColorPicker();
		collectionDescColor.setPrefWidth(181);
		collectionDescColor.setValue(convertStringToColor(mainTheme.getCollectionDescColor()));
		collectionDescColor.setOnAction(event -> {
			finalNewTheme.setCollectionDescColor(formatColorCode(collectionDescColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionDescColorLabel = new Label("Series Description");
		collectionDescColorLabel.setLabelFor(collectionDescColor);
		collectionDescColorLabel.setId("SettingsTextStyling");

		VBox collectionDescColorRoot = new VBox(collectionDescColorLabel, collectionDescColor);
		collectionDescColorRoot.setSpacing(2);

		ColorPicker collectionCardBottomBGColor = new ColorPicker();
		collectionCardBottomBGColor.setPrefWidth(181);
		collectionCardBottomBGColor.setValue(convertStringToColor(mainTheme.getCollectionCardBottomBGColor()));
		collectionCardBottomBGColor.setOnAction(event -> {
			finalNewTheme.setCollectionCardBottomBGColor(formatColorCode(collectionCardBottomBGColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionCardBottomBGColorLabel = new Label("Progress BG");
		collectionCardBottomBGColorLabel.setLabelFor(collectionCardBottomBGColor);
		collectionCardBottomBGColorLabel.setId("SettingsTextStyling");

		VBox collectionCardBottomBGColorRoot = new VBox(collectionCardBottomBGColorLabel, collectionCardBottomBGColor);
		collectionCardBottomBGColorRoot.setSpacing(2);

		ColorPicker collectionIconButtonColor = new ColorPicker();
		collectionIconButtonColor.setPrefWidth(181);
		collectionIconButtonColor.setValue(convertStringToColor(mainTheme.getCollectionIconButtonColor()));
		collectionIconButtonColor.setOnAction(event -> {
			finalNewTheme.setCollectionIconButtonColor(formatColorCode(collectionIconButtonColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionIconButtonColorLabel = new Label("Series Card Button");
		collectionIconButtonColorLabel.setLabelFor(collectionIconButtonColor);
		collectionIconButtonColorLabel.setId("SettingsTextStyling");

		VBox collectionIconButtonColorRoot = new VBox(collectionIconButtonColorLabel, collectionIconButtonColor);
		collectionIconButtonColorRoot.setSpacing(2);

		ColorPicker collectionNormalIconColor = new ColorPicker();
		collectionNormalIconColor.setPrefWidth(181);
		collectionNormalIconColor.setValue(convertStringToColor(mainTheme.getCollectionNormalIconColor()));
		collectionNormalIconColor.setOnAction(event -> {
			finalNewTheme.setCollectionNormalIconColor(formatColorCode(collectionNormalIconColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionNormalIconColorLabel = new Label("Series Card Icon");
		collectionNormalIconColorLabel.setLabelFor(collectionNormalIconColor);
		collectionNormalIconColorLabel.setId("SettingsTextStyling");

		VBox collectionNormalIconColorRoot = new VBox(collectionNormalIconColorLabel, collectionNormalIconColor);
		collectionNormalIconColorRoot.setSpacing(2);

		ColorPicker collectionHoverIconColor = new ColorPicker();
		collectionHoverIconColor.setPrefWidth(181);
		collectionHoverIconColor.setValue(convertStringToColor(mainTheme.getCollectionHoverIconColor()));
		collectionHoverIconColor.setOnAction(event -> {
			finalNewTheme.setCollectionHoverIconColor(formatColorCode(collectionHoverIconColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionHoverIconColorLabel = new Label("Series Card Icon (Hover)");
		collectionHoverIconColorLabel.setLabelFor(collectionHoverIconColor);
		collectionHoverIconColorLabel.setId("SettingsTextStyling");

		VBox collectionHoverIconColorRoot = new VBox(collectionHoverIconColorLabel, collectionHoverIconColor);
		collectionHoverIconColorRoot.setSpacing(2);

		ColorPicker collectionProgressBarColor = new ColorPicker();
		collectionProgressBarColor.setPrefWidth(181);
		collectionProgressBarColor.setValue(convertStringToColor(mainTheme.getCollectionProgressBarColor()));
		collectionProgressBarColor.setOnAction(event -> {
			finalNewTheme.setCollectionProgressBarColor(formatColorCode(collectionProgressBarColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionProgressBarColorLabel = new Label("Progress Bar");
		collectionProgressBarColorLabel.setLabelFor(collectionProgressBarColor);
		collectionProgressBarColorLabel.setId("SettingsTextStyling");

		VBox collectionProgressBarColorRoot = new VBox(collectionProgressBarColorLabel, collectionProgressBarColor);
		collectionProgressBarColorRoot.setSpacing(2);

		ColorPicker collectionProgressBarBorderColor = new ColorPicker();
		collectionProgressBarBorderColor.setPrefWidth(181);
		collectionProgressBarBorderColor.setValue(convertStringToColor(mainTheme.getCollectionProgressBarBorderColor()));
		collectionProgressBarBorderColor.setOnAction(event -> {
			finalNewTheme.setCollectionProgressBarBorderColor(formatColorCode(collectionProgressBarBorderColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionProgressBarBorderColorLabel = new Label("Progress Bar Border");
		collectionProgressBarBorderColorLabel.setLabelFor(collectionProgressBarBorderColor);
		collectionProgressBarBorderColorLabel.setId("SettingsTextStyling");

		VBox collectionProgressBarBorderColorRoot = new VBox(collectionProgressBarBorderColorLabel, collectionProgressBarBorderColor);
		collectionProgressBarBorderColorRoot.setSpacing(2);

		ColorPicker collectionProgressBarBGColor = new ColorPicker();
		collectionProgressBarBGColor.setPrefWidth(181);
		collectionProgressBarBGColor.setValue(convertStringToColor(mainTheme.getCollectionProgressBarBGColor()));
		collectionProgressBarBGColor.setOnAction(event -> {
			finalNewTheme.setCollectionProgressBarBGColor(formatColorCode(collectionProgressBarBGColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionProgressBarBGColorLabel = new Label("Progress Bar BG");
		collectionProgressBarBGColorLabel.setLabelFor(collectionProgressBarBGColor);
		collectionProgressBarBGColorLabel.setId("SettingsTextStyling");

		VBox collectionProgressBarBGColorRoot = new VBox(collectionProgressBarBGColorLabel, collectionProgressBarBGColor);
		collectionProgressBarBGColorRoot.setSpacing(2);

		ColorPicker collectionNormalVolProgressTextColor = new ColorPicker();
		collectionNormalVolProgressTextColor.setPrefWidth(181);
		collectionNormalVolProgressTextColor.setValue(convertStringToColor(mainTheme.getCollectionNormalVolProgressTextColor()));
		collectionNormalVolProgressTextColor.setOnAction(event -> {
			finalNewTheme.setCollectionNormalVolProgressTextColor(formatColorCode(collectionNormalVolProgressTextColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionNormalVolProgressTextColorLabel = new Label("Vol Progress Text");
		collectionNormalVolProgressTextColorLabel.setLabelFor(collectionNormalVolProgressTextColor);
		collectionNormalVolProgressTextColorLabel.setId("SettingsTextStyling");

		VBox collectionNormalVolProgressTextColorRoot = new VBox(collectionNormalVolProgressTextColorLabel, collectionNormalVolProgressTextColor);
		collectionNormalVolProgressTextColorRoot.setSpacing(2);

		ColorPicker collectionHoverVolProgressTextColor = new ColorPicker();
		collectionHoverVolProgressTextColor.setPrefWidth(181);
		collectionHoverVolProgressTextColor.setValue(convertStringToColor(mainTheme.getCollectionHoverVolProgressTextColor()));
		collectionHoverVolProgressTextColor.setOnAction(event -> {
			finalNewTheme.setCollectionHoverVolProgressTextColor(formatColorCode(collectionHoverVolProgressTextColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionHoverVolProgressTextColorLabel = new Label("Vol Progress Text (Hover)");
		collectionHoverVolProgressTextColorLabel.setLabelFor(collectionHoverVolProgressTextColor);
		collectionHoverVolProgressTextColorLabel.setId("SettingsTextStyling");

		VBox collectionHoverVolProgressTextColorRoot = new VBox(collectionHoverVolProgressTextColorLabel, collectionHoverVolProgressTextColor);
		collectionHoverVolProgressTextColorRoot.setSpacing(2);

		ColorPicker collectionUserNotesBGColor = new ColorPicker();
		collectionUserNotesBGColor.setPrefWidth(181);
		collectionUserNotesBGColor.setValue(convertStringToColor(mainTheme.getCollectionUserNotesBGColor()));
		collectionUserNotesBGColor.setOnAction(event -> {
			finalNewTheme.setCollectionUserNotesBGColor(formatColorCode(collectionUserNotesBGColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionUserNotesBGColorLabel = new Label("Series Notes BG");
		collectionUserNotesBGColorLabel.setLabelFor(collectionUserNotesBGColor);
		collectionUserNotesBGColorLabel.setId("SettingsTextStyling");

		VBox collectionUserNotesBGColorRoot = new VBox(collectionUserNotesBGColorLabel, collectionUserNotesBGColor);
		collectionUserNotesBGColorRoot.setSpacing(2);

		ColorPicker collectionUserNotesBorderColor = new ColorPicker();
		collectionUserNotesBorderColor.setPrefWidth(181);
		collectionUserNotesBorderColor.setValue(convertStringToColor(mainTheme.getCollectionUserNotesBorderColor()));
		collectionUserNotesBorderColor.setOnAction(event -> {
			finalNewTheme.setCollectionUserNotesBorderColor(formatColorCode(collectionUserNotesBorderColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionUserNotesBorderColorLabel = new Label("Series Notes Border");
		collectionUserNotesBorderColorLabel.setLabelFor(collectionUserNotesBorderColor);
		collectionUserNotesBorderColorLabel.setId("SettingsTextStyling");

		VBox collectionUserNotesBorderColorRoot = new VBox(collectionUserNotesBorderColorLabel, collectionUserNotesBorderColor);
		collectionUserNotesBorderColorRoot.setSpacing(2);

		ColorPicker collectionUserNotesTextColor = new ColorPicker();
		collectionUserNotesTextColor.setPrefWidth(181);
		collectionUserNotesTextColor.setValue(convertStringToColor(mainTheme.getCollectionUserNotesTextColor()));
		collectionUserNotesTextColor.setOnAction(event -> {
			finalNewTheme.setCollectionUserNotesTextColor(formatColorCode(collectionUserNotesTextColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionUserNotesTextColorLabel = new Label("Series Notes Text");
		collectionUserNotesTextColorLabel.setLabelFor(collectionUserNotesTextColor);
		collectionUserNotesTextColorLabel.setId("SettingsTextStyling");

		VBox collectionUserNotesTextColorRoot = new VBox(collectionUserNotesTextColorLabel, collectionUserNotesTextColor);
		collectionUserNotesTextColorRoot.setSpacing(2);

		FlowPane collectionThemePane = new FlowPane(collectionBGColorRoot, collectionLinkNormalBGColorRoot, collectionLinkHoverBGColorRoot, collectionLinkNormalTextColorRoot, collectionLinkHoverTextColorRoot, collectionMainCardBGColorRoot, collectionTitleColorRoot, collectionPublisherColorRoot, collectionMangakaColorRoot, collectionDescColorRoot, collectionCardBottomBGColorRoot, collectionIconButtonColorRoot, collectionNormalIconColorRoot, collectionHoverIconColorRoot, collectionProgressBarColorRoot, collectionProgressBarBorderColorRoot, collectionProgressBarBGColorRoot, collectionNormalVolProgressTextColorRoot, collectionHoverVolProgressTextColorRoot, collectionUserNotesBGColorRoot, collectionUserNotesBorderColorRoot,collectionUserNotesTextColorRoot);
		collectionThemePane.setId("ThemeSettingsBox");

		Label collectionLabel = new Label("Collection Theme");
		collectionLabel.setLabelFor(collectionThemePane);
		collectionLabel.setId("SettingsLabelStyling");

		TextField enterThemeName = new TextField();
		enterThemeName.setId("MenuTextField");
		enterThemeName.textProperty().addListener((obs, oldText, newText) -> finalNewTheme.setThemeName(newText));

		ComboBox<String> userCurrentTheme = new ComboBox<>(usersSavedThemes);
		userCurrentTheme.setPromptText(mainTheme.getThemeName());
		userCurrentTheme.setOnAction((event) -> {
			curTheme = userCurrentTheme.getValue();
			mainTheme = user.setNewMainTheme(userCurrentTheme.getValue());
			collectionMasterCSS = drawTheme(mainTheme);
			try {
				menuSetup(content, primaryStage);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			collectionSetup(primaryStage);
		});

		Button deleteThemeButton = new Button("X");
		deleteThemeButton.setId("MenuButton");
		deleteThemeButton.setOnMouseClicked(event -> {
			user.removeSavedTheme(curTheme);
			userCurrentTheme.getItems().remove(curTheme);
			userCurrentTheme.setValue(usersSavedThemes.get(0));
		});

		HBox themeButtons = new HBox(userCurrentTheme, deleteThemeButton);
		themeButtons.setSpacing(5);

		Button saveNewThemeButton = new Button("Save");
		saveNewThemeButton.setId("MenuButton");
		saveNewThemeButton.disableProperty().bind(enterThemeName.textProperty().isEmpty());
		saveNewThemeButton.setOnMouseClicked(event -> {
			user.addNewTheme(finalNewTheme);
			userCurrentTheme.getItems().add(finalNewTheme.getThemeName());
			userCurrentTheme.setValue(finalNewTheme.getThemeName());
		});

		HBox newThemeRoot = new HBox(enterThemeName, saveNewThemeButton);
		newThemeRoot.setAlignment(Pos.CENTER);
		newThemeRoot.setSpacing(5);

		HBox themeChangeRoot = new HBox(newThemeRoot, themeButtons);
		themeChangeRoot.setAlignment(Pos.CENTER);
		themeChangeRoot.setSpacing(15);

		Label themeChangeRootLabel = new Label("Change or Save Theme");
		themeChangeRootLabel.setLabelFor(themeChangeRoot);
		themeChangeRootLabel.setId("SettingsLabelStyling");

		VBox themeSettingRoot = new VBox(menuLabel, menuThemeChangePane, collectionLabel, collectionThemePane, themeChangeRootLabel, themeChangeRoot);
		themeSettingRoot.setId("ThemeSettingsPane");
		themeSettingRoot.setStyle(collectionMasterCSS);

		Scene collectionSettingsScene = new Scene(themeSettingRoot);
		collectionSettingsScene.getStylesheets().add("MenuCSS.css");

		themeSettingsWindow.setHeight(940);
		themeSettingsWindow.setWidth(880);
		themeSettingsWindow.setTitle("TsundOku Theme Settings");
		themeSettingsWindow.getIcons().add(new Image("bookshelf.png"));
		themeSettingsWindow.setScene(collectionSettingsScene);
		themeSettingsWindow.setOnCloseRequest(event -> userCurrentTheme.setValue(user.getMainTheme().getThemeName()));
	}

	private String drawTheme(TsundOkuTheme newTheme){
		return  "-fx-menu-bg-color: " + newTheme.getMenuBGColor() +
				"-fx-menu-username-text-color: " + newTheme.getUserNameColor() +
				"-fx-menu-normal-user-settings-icon-color: " + newTheme.getUserNormalSettingsIconColor() +
				"-fx-menu-normal-theme-settings-icon-color: " + newTheme.getThemeNormalSettingsIconColor() +
				"-fx-menu-hover-user-settings-icon-color: " + newTheme.getUserHoverSettingsIconColor() +
				"-fx-menu-hover-theme-settings-icon-color: " + newTheme.getThemeHoverSettingsIconColor() +
				"-fx-menu-collection-search-border-color: " + newTheme.getCollectionSearchBorderColor() +
				"-fx-menu-collection-search-bg-color: " + newTheme.getCollectionSearchBGColor() +
				"-fx-menu-collection-search-text-color: " + newTheme.getCollectionSearchTextColor() +
				"-fx-menu-bottom-border-color: " + newTheme.getMenuBottomBorderColor() +
				"-fx-menu-text-color: " + newTheme.getMenuTextColor() +
				"-fx-normal-menu-button-bg-color: " + newTheme.getMenuNormalButtonBGColor() +
				"-fx-hover-menu-button-bg-color: " + newTheme.getMenuHoverButtonBGColor() +
				"-fx-normal-menu-button-border-color: " + newTheme.getMenuNormalButtonBorderColor() +
				"-fx-hover-menu-button-border-color: " + newTheme.getMenuHoverButtonBorderColor() +
				"-fx-normal-menu-button-text-color: " + newTheme.getMenuNormalButtonTextColor() +
				"-fx-hover-menu-button-text-color: " + newTheme.getMenuHoverButtonTextColor() +
				"-fx-collection-bg-color: " + newTheme.getCollectionBGColor() +
				"-fx-collection-link-normal-text-color: " + newTheme.getCollectionLinkNormalTextColor() +
				"-fx-collection-link-hover-text-color: " + newTheme.getCollectionLinkHoverTextColor() +
				"-fx-collection-link-normal-bg-color: " + newTheme.getCollectionLinkNormalBGColor() +
				"-fx-collection-link-hover-bg-color: " + newTheme.getCollectionLinkHoverBGColor() +
				"-fx-collection-card-main-bg-color: " + newTheme.getCollectionCardMainBGColor() +
				"-fx-collection-title-color: " + newTheme.getCollectionTitleColor() +
				"-fx-collection-publisher-color: " + newTheme.getCollectionPublisherColor() +
				"-fx-collection-mangaka-color: " + newTheme.getCollectionMangakaColor() +
				"-fx-collection-desc-color: " + newTheme.getCollectionDescColor() +
				"-fx-collection-bottom-card-bg-color: " + newTheme.getCollectionCardBottomBGColor() +
				"-fx-collection-button-icon-color: " + newTheme.getCollectionIconButtonColor() +
				"-fx-collection-normal-icon-color: " + newTheme.getCollectionNormalIconColor() +
				"-fx-collection-hover-icon-color: " + newTheme.getCollectionHoverIconColor() +
				"-fx-collection-progress-bar-border-color: " + newTheme.getCollectionProgressBarBorderColor() +
				"-fx-collection-progress-bar-color: " + newTheme.getCollectionProgressBarColor() +
				"-fx-collection-progress-bar-bg-color: " + newTheme.getCollectionProgressBarBGColor() +
				"-fx-collection-normal-volprogress-text-color: " + newTheme.getCollectionNormalVolProgressTextColor() +
				"-fx-collection-hover-volprogress-text-color: " + newTheme.getCollectionHoverVolProgressTextColor() +
				"-fx-collection-usernotes-bg-color: " + newTheme.getCollectionUserNotesBGColor() +
				"-fx-collection-usernotes-border-color: " + newTheme.getCollectionUserNotesBorderColor() +
				"-fx-collection-usernotes-text-color: " + newTheme.getCollectionUserNotesTextColor();
	}

	private String formatColorCode(Color newColor){
		return "rgba(" + (int) (newColor.getRed() * 255) + "," + (int) (newColor.getGreen() * 255) + "," + (int) (newColor.getBlue() * 255) + "," + String.format("%.2f", newColor.getOpacity()) + "); ";
	}

	private Color convertStringToColor(String color){
		Color convertedColor = null;
		if (color.startsWith("#")){
			convertedColor = Color.web(color.substring(0, 7));
		}
		else {
			String substring = color.substring(color.indexOf("(") + 1, color.indexOf(")"));
			if (color.startsWith("rgba")){
				String[] colArray = substring.split(",");
				convertedColor = Color.rgb(Integer.parseInt(colArray[0]), Integer.parseInt(colArray[1]), Integer.parseInt(colArray[2]), Double.parseDouble(colArray[3]));
			}
			else if (color.startsWith("rgb")){
				String[] colArray = substring.split(",");
				convertedColor = Color.rgb(Integer.parseInt(colArray[0]), Integer.parseInt(colArray[1]), Integer.parseInt(colArray[2]));
			}
		}
		return convertedColor;
	}

	private void addNewSeriesWindow(Stage primaryStage){
		AtomicReference<String> bookType = new AtomicReference<>("");

		TextField titleEnter = new TextField();
		titleEnter.setId("MenuTextField");
		titleEnter.setPrefWidth(250);

		Label inputTitleLabel = new Label("Enter Title (Copy Title From AniList)");
		inputTitleLabel.setId("MenuLabel");
		inputTitleLabel.setLabelFor(titleEnter);

		VBox inputTitleRoot = new VBox();
		inputTitleRoot.setId("SettingsLabel");
		inputTitleRoot.getChildren().addAll(inputTitleLabel, titleEnter);

		TextField publisherEnter = new TextField();
		publisherEnter.setId("MenuTextField");
		publisherEnter.setPrefWidth(250);

		Label inputPublisherLabel = new Label("Enter Publisher");
		inputPublisherLabel.setId("MenuLabel");
		inputPublisherLabel.setLabelFor(publisherEnter);

		VBox inputPublisherRoot = new VBox();
		inputPublisherRoot.setId("SettingsLabel");
		inputPublisherRoot.getChildren().addAll(inputPublisherLabel, publisherEnter);

		ToggleGroup bookTypeButtonGroup = new ToggleGroup();
		ToggleButton mangaButton = new ToggleButton("Manga");
		ToggleButton lightNovelButton = new ToggleButton("Novel");

		mangaButton.setToggleGroup(bookTypeButtonGroup);
		mangaButton.setId("MenuButton");
		mangaButton.setPrefSize(100, 10);
		mangaButton.setOnMouseClicked((MouseEvent event) -> {
			bookType.set("Manga");
			mangaButton.setDisable(true);
			lightNovelButton.setDisable(false);
		});

		lightNovelButton.setToggleGroup(bookTypeButtonGroup);
		lightNovelButton.setId("MenuButton");
		lightNovelButton.setPrefSize(100, 10);
		lightNovelButton.setOnMouseClicked((MouseEvent event) -> {
			bookType.set("Novel");
			mangaButton.setDisable(false);
			lightNovelButton.setDisable(true);
		});

		UnaryOperator<TextFormatter.Change> filter = change -> {
			String newText = change.getControlNewText();
			if (newText.matches("[0-9]*")) { return change; }
			return null;
		};

		Label bookTypeLabel = new Label("Select Book Type");
		bookTypeLabel.setId("MenuLabel");

		HBox bookTypePane = new HBox(mangaButton, lightNovelButton);
		bookTypePane.setSpacing(10);
		bookTypePane.setAlignment(Pos.CENTER);

		VBox bookTypeRoot = new VBox(bookTypeLabel, bookTypePane);
		bookTypeRoot.setAlignment(Pos.CENTER);

		TextField curVolumes = new TextField();
		curVolumes.setPrefWidth(50);
		curVolumes.setId("MenuTextField");
		curVolumes.setTextFormatter(new TextFormatter<>(filter));
		curVolumes.lengthProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.intValue() > oldValue.intValue()) {
				if (curVolumes.getText().length() >= 3) {
					curVolumes.setText(curVolumes.getText().substring(0, 3));
				}
			}
		});

		TextField maxVolumes = new TextField();
		maxVolumes.setPrefWidth(50);
		maxVolumes.setId("MenuTextField");
		maxVolumes.setTextFormatter(new TextFormatter<>(filter));
		maxVolumes.lengthProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.intValue() > oldValue.intValue()) {
				if (maxVolumes.getText().length() >= 3) {
					maxVolumes.setText(maxVolumes.getText().substring(0, 3));
				}
			}
		});

		Label curVolLabel = new Label("Cur Volumes");
		curVolLabel.setId("MenuLabel");

		Label maxVolLabel = new Label("Max Volumes");
		maxVolLabel.setId("MenuLabel");

		GridPane volProgressRoot = new GridPane();
		volProgressRoot.setVgap(5);
		volProgressRoot.setHgap(10);
		volProgressRoot.setAlignment(Pos.CENTER);
		volProgressRoot.add(curVolLabel, 0, 0);
		volProgressRoot.add(curVolumes, 0 , 1);
		volProgressRoot.add(maxVolLabel, 1, 0);
		volProgressRoot.add(maxVolumes, 1 , 1);

		Button submitButton = new Button("Add");

		submitButton.setPrefSize(60, 10);
		submitButton.setId("MenuButton");
		submitButton.disableProperty().bind(titleEnter.textProperty().isEmpty().or(publisherEnter.textProperty().isEmpty()).or(curVolumes.textProperty().isEmpty()).or(maxVolumes.textProperty().isEmpty()).or(bookTypeButtonGroup.selectedToggleProperty().isNull()).or(curVolumes.textProperty().greaterThan(maxVolumes.textProperty())));
		submitButton.setOnMouseClicked(event -> {
			if (Integer.parseInt(curVolumes.getText()) <= Integer.parseInt(maxVolumes.getText())){
				String newTitle = titleEnter.getText();
				if (userCollection.stream().noneMatch(series -> ((series.getRomajiTitle().equalsIgnoreCase(newTitle) || series.getEnglishTitle().equalsIgnoreCase(newTitle) || series.getNativeTitle().equalsIgnoreCase(newTitle)) && series.getBookType().equals(bookType.get())))){
					userCollection.add(new Series().CreateNewSeries(newTitle, publisherEnter.getText(), bookType.get(), Integer.parseInt(curVolumes.getText()), Integer.parseInt(maxVolumes.getText())));
					filteredUserCollection = FXCollections.observableArrayList(userCollection);
					collectionSetup(primaryStage);
					updateCollectionNumbers();
				}
			}
		});

		VBox newSeriesPane = new VBox(inputTitleRoot, inputPublisherRoot, bookTypeRoot, volProgressRoot, submitButton);
		newSeriesPane.setId("NewSeriesPane");
		newSeriesPane.setStyle(collectionMasterCSS);
		newSeriesPane.setCache(true);
		newSeriesPane.setCacheHint(CacheHint.SPEED);

		Scene newSeriesScene = new Scene(newSeriesPane);
		newSeriesScene.getStylesheets().add("MenuCSS.css");
		addNewSeriesWindow.setResizable(false);
		addNewSeriesWindow.getIcons().add(new Image("bookshelf.png"));
		addNewSeriesWindow.setTitle("Add New Series");
		addNewSeriesWindow.setScene(newSeriesScene);
	}

	private void collectionSetup(Stage primaryStage){
		totalVolumesCollected = 0;
		maxVolumesInCollection = 0;

		collection = new FlowPane();
		collection.setId("Collection");
		collection.setStyle(collectionMasterCSS);
		collection.setCache(true);
		collection.setCacheHint(CacheHint.SPEED);

		ScrollPane collectionScroll = new ScrollPane(collection);
		collectionScroll.setId("CollectionScroll");
		collectionScroll.setVvalue(collectionScroll.getVvalue() - 2000);
		collectionScroll.getContent().setOnScroll(scrollEvent -> {
			double deltaY = scrollEvent.getDeltaY() * 1.5;
			double contentHeight = collectionScroll.getContent().getBoundsInLocal().getHeight();
			double collectionScrollHeight = collectionScroll.getHeight();
			double diff = contentHeight - collectionScrollHeight;
			if (diff < 1) diff = 1;
			double vvalue = collectionScroll.getVvalue();
			collectionScroll.setVvalue(vvalue + -deltaY/diff);
		});
		collectionScroll.setCache(true);
		collectionScroll.setCacheHint(CacheHint.SPEED);

		for (Series series : filteredUserCollection) {
			MigPane seriesCard = new MigPane();
			seriesCard.setId("SeriesCard");
			seriesCard.setPrefSize(SERIES_CARD_WIDTH, SERIES_CARD_HEIGHT);
			seriesCard.add(leftSideCardSetup(series), "dock west");
			seriesCard.add(rightSideCardSetup(series, primaryStage), "dock east");
			seriesCard.setCache(true);
			seriesCard.setCacheHint(CacheHint.SPEED);
			collection.getChildren().add(seriesCard);
		}

		user.setTotalVolumes(totalVolumesCollected);
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

		Label bookTypeAndPrintStatus = new Label(series.getBookType()+ " | " + series.getPrintStatus());
		bookTypeAndPrintStatus.setPrefHeight(BOTTOM_CARD_HEIGHT);
		bookTypeAndPrintStatus.setPrefWidth(LEFT_SIDE_CARD_WIDTH);
		bookTypeAndPrintStatus.relocate(0, SERIES_CARD_HEIGHT - BOTTOM_CARD_HEIGHT);
		bookTypeAndPrintStatus.setId("SeriesStatus");

		Pane leftSideOfSeriesCard = new Pane();
		leftSideOfSeriesCard.relocate(0, 0);
		leftSideOfSeriesCard.setPrefSize(LEFT_SIDE_CARD_WIDTH, SERIES_CARD_HEIGHT);
		leftSideOfSeriesCard.setId("LeftSideCard");
		leftSideOfSeriesCard.getChildren().addAll(cover, bookTypeAndPrintStatus);

		Hyperlink aniListLink = new Hyperlink(series.getLink());
		aniListLink.setPrefHeight(SERIES_CARD_HEIGHT);
		aniListLink.setPrefWidth(LEFT_SIDE_CARD_WIDTH);
		aniListLink.setId("AniListLink");
		aniListLink.setOnMouseClicked((MouseEvent event) -> {
			try {
				Desktop desktop = java.awt.Desktop.getDesktop();
				URI aL_Link = new URI(aniListLink.getText());
				desktop.browse(aL_Link);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		aniListLink.setGraphic(leftSideOfSeriesCard);

		return aniListLink;
	}

	private String getCurTitle(Series series, String curLanguage){
		switch (curLanguage) {
			case "Romaji":
				return series.getRomajiTitle();
			case "English":
				return series.getEnglishTitle();
			case "Native":
				return series.getNativeTitle();
			default:
				return "Error Title\n";
		}
	}

	private String getCurMangaka(Series series, String curLanguage){
		switch (curLanguage) {
			case "English":
			case "Romaji":
				return series.getRomajiStaff();
			case "Native":
				return series.getNativeStaff();
			default:
				return "Error Mangaka";
		}
	}

	private MigPane rightSideCardSetup(Series series, Stage primaryStage){
		Integer curVolumes = series.getCurVolumes();
		Integer maxVolumes = series.getMaxVolumes();

		Text publisher = new Text(series.getPublisher());
		publisher.setId("Publisher");
		TextFlow publisherFlow = new TextFlow(publisher);
		publisherFlow.setLineSpacing(0);
		publisherFlow.setPadding(new Insets(0, 5, 2, 10));

		String curLanguage = user.getCurLanguage();
		Text seriesTitle = new Text(getCurTitle(series, curLanguage));
		seriesTitle.setWrappingWidth(RIGHT_SIDE_CARD_WIDTH - 20);
		seriesTitle.setId("SeriesTitle");
		seriesTitle.setOnMouseClicked(event -> {
			Clipboard copy = Clipboard.getSystemClipboard();
			ClipboardContent titleContent = new ClipboardContent();
			titleContent.putString(seriesTitle.getText());
			copy.setContent(titleContent);
		});
		TextFlow seriesTitleFlow = new TextFlow(seriesTitle);
		seriesTitleFlow.setLineSpacing(-5);
		seriesTitleFlow.setPadding(new Insets(-3, 5, 2, 10));
		seriesTitleFlow.setCache(true);
		seriesTitleFlow.setCacheHint(CacheHint.SPEED);

		Text mangaka = new Text(getCurMangaka(series, curLanguage));
		mangaka.setWrappingWidth(RIGHT_SIDE_CARD_WIDTH);
		mangaka.setId("Mangaka");
		TextFlow mangakaFlow = new TextFlow(mangaka);
		mangakaFlow.setLineSpacing(0);
		mangakaFlow.setPadding(new Insets(0, 5, 0, 10));
		mangakaFlow.setCache(true);
		mangakaFlow.setCacheHint(CacheHint.SPEED);

		Text desc = new Text(series.getSeriesDesc());
		desc.setId("SeriesDescriptionText");

		TextFlow descWrap = new TextFlow(desc);
		descWrap.setPrefSize(SERIES_CARD_WIDTH - 20, SERIES_CARD_HEIGHT - BOTTOM_CARD_HEIGHT);
		desc.setLineSpacing(-0.5);
		descWrap.setId("SeriesDescriptionWrap");

		ScrollPane descScroll = new ScrollPane(descWrap);
		descScroll.setId("SeriesDescriptionScroll");

		double volAmount = (double) curVolumes / maxVolumes;
		DoubleProperty volUpdate = new SimpleDoubleProperty();
		volUpdate.set(volAmount);

		Label progressTxt = new Label(curVolumes + "/" + maxVolumes);
		progressTxt.setId("VolProgressTxt");
		progressTxt.setPrefSize(RIGHT_SIDE_CARD_WIDTH - (RIGHT_SIDE_CARD_WIDTH - 70) + 20, 20);
		progressTxt.setCache(true);
		progressTxt.setCacheHint(CacheHint.SPEED);

		Button decrementButton = new Button("-");
		Button incrementButton = new Button("+");

		decrementButton.setStyle("-fx-font-size: 30; -fx-padding: -18 0 -8 0;");
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
			if (series.getCurVolumes() == 0){
				decrementButton.setDisable(true);
			}
		});
		decrementButton.setCache(true);
		decrementButton.setCacheHint(CacheHint.SPEED);

		incrementButton.setStyle("-fx-font-size: 27; -fx-padding: -12 0 -3 0;");
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
			if (series.getCurVolumes().equals(series.getMaxVolumes())){
				incrementButton.setDisable(true);
			}
		});
		incrementButton.setCache(true);
		incrementButton.setCacheHint(CacheHint.SPEED);

		if (curVolumes == 0) { decrementButton.setDisable(true); }
		else if (curVolumes.equals(maxVolumes)) { incrementButton.setDisable(true); }

		ProgressBar volProgressBar = new ProgressBar();
		volProgressBar.progressProperty().bind(volUpdate);
		volProgressBar.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 110, BOTTOM_CARD_HEIGHT);
		volProgressBar.setId("ProgressBar");
		volProgressBar.setCache(true);
		volProgressBar.setCacheHint(CacheHint.SPEED);

		FontIcon seriesSettingIcon = new FontIcon(BootstrapIcons.JOURNAL_TEXT);
		seriesSettingIcon.setIconSize(25);
		seriesSettingIcon.setId("CollectionIcon");

		Button seriesCardSettingsButton = new Button();
		seriesCardSettingsButton.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 310, BOTTOM_CARD_HEIGHT - 1);
		seriesCardSettingsButton.setGraphic(seriesSettingIcon);
		seriesCardSettingsButton.setId("CollectionIconButton");

		FontIcon backToSeriesDataIcon = new FontIcon(BootstrapIcons.CARD_HEADING);
		backToSeriesDataIcon.setId("CollectionIcon");
		backToSeriesDataIcon.setIconSize(25);

		Button backToSeriesCardDataButton = new Button();
		backToSeriesCardDataButton.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 310, BOTTOM_CARD_HEIGHT - 1);
		backToSeriesCardDataButton.setGraphic(backToSeriesDataIcon);
		backToSeriesCardDataButton.setId("CollectionIconButton");

		HBox userButtons = new HBox(decrementButton, incrementButton);
		userButtons.setAlignment(Pos.CENTER);
		userButtons.setStyle("-fx-background-radius: 0px 0px 5px 0px; -fx-border-radius: 0px 0px 5px 0px;");
		userButtons.setSpacing(6);

		BorderPane volProgress = new BorderPane();
		volProgress.setPrefSize(RIGHT_SIDE_CARD_WIDTH - (RIGHT_SIDE_CARD_WIDTH - 80), BOTTOM_CARD_HEIGHT);
		volProgress.setStyle("-fx-background-radius: 0px 0px 5px 0px; -fx-border-radius: 0px 0px 5px 0px;");
		volProgress.setTop(progressTxt);
		volProgress.setBottom(userButtons);

		BorderPane rightSideBottomPane = new BorderPane();
		rightSideBottomPane.setPrefSize(RIGHT_SIDE_CARD_WIDTH, BOTTOM_CARD_HEIGHT);
		rightSideBottomPane.setId("SeriesCardBottomPane");
		rightSideBottomPane.setLeft(seriesCardSettingsButton);
		rightSideBottomPane.setCenter(volProgressBar);
		rightSideBottomPane.setRight(volProgress);

		MigPane seriesData = new MigPane("insets 5 0 3 0, align left", "[]", "[][]-4[]2[]");
		seriesData.setMaxSize(RIGHT_SIDE_CARD_WIDTH - 20, SERIES_CARD_HEIGHT - BOTTOM_CARD_HEIGHT);
		seriesData.setStyle("-fx-border-radius: 5 5 0 0;  -fx-background-radius: 5 5 0 0;");
		seriesData.add(publisherFlow,"wrap, hmax 3.5");
		seriesData.add(seriesTitleFlow, "wrap");
		seriesData.add(mangakaFlow, "wrap");
		seriesData.add(descScroll);

		MigPane rightSideOfSeriesCard = new MigPane();
		rightSideOfSeriesCard.setId("RightSideCard");
		rightSideOfSeriesCard.setPrefSize(RIGHT_SIDE_CARD_WIDTH, SERIES_CARD_HEIGHT - BOTTOM_CARD_HEIGHT);
		rightSideOfSeriesCard.add(seriesData, "north");
		rightSideOfSeriesCard.add(rightSideBottomPane, "south");

		HBox seriesSettingsPane = seriesCardSettingsPane(series, primaryStage, progressTxt);
		seriesSettingsPane.setCache(true);
		seriesSettingsPane.setCacheHint(CacheHint.SPEED);
		seriesCardSettingsButton.setOnMouseClicked((MouseEvent event) -> {
			rightSideOfSeriesCard.remove(seriesData);
			rightSideOfSeriesCard.add(seriesSettingsPane, "north");
			rightSideBottomPane.setLeft(backToSeriesCardDataButton);
		});
		backToSeriesCardDataButton.setOnMouseClicked((MouseEvent event) -> {
			rightSideOfSeriesCard.remove(seriesSettingsPane);
			rightSideOfSeriesCard.add(seriesData);
			rightSideBottomPane.setLeft(seriesCardSettingsButton);
		});

		totalVolumesCollected += curVolumes;
		maxVolumesInCollection += maxVolumes;

		return rightSideOfSeriesCard;
	}

	private HBox seriesCardSettingsPane(Series series, Stage primaryStage, Label progressTxt){
		TextArea userNotes = new TextArea(series.getUserNotes());
		userNotes.setFocusTraversable(false);
		userNotes.setWrapText(true);
		userNotes.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 40, SERIES_CARD_HEIGHT - (2 * BOTTOM_CARD_HEIGHT));
		userNotes.textProperty().addListener((object, oldText, newText) -> series.setUserNotes(newText));

		FontIcon deleteButtonIcon = new FontIcon(BootstrapIcons.TRASH);
		deleteButtonIcon.setId("CollectionIcon");
		deleteButtonIcon.setIconSize(30);

		Button deleteSeriesButton = new Button();
		deleteSeriesButton.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 310, BOTTOM_CARD_HEIGHT - 1);
		deleteSeriesButton.setGraphic(deleteButtonIcon);
		deleteSeriesButton.setId("CollectionIconButton");
		deleteSeriesButton.setOnMouseClicked((MouseEvent event) -> {
			userCollection.removeIf(delSeries -> delSeries.getRomajiTitle().equals(series.getRomajiTitle()) && delSeries.getBookType().equals(series.getBookType()));
			try {
				Files.delete(Paths.get(series.getCover()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			collectionSetup(primaryStage);
			sortCollection();
			filteredUserCollection = FXCollections.observableArrayList(userCollection);
			updateCollectionNumbers();
		});

		UnaryOperator<TextFormatter.Change> filter = change -> {
			String newText = change.getControlNewText();
			if (newText.matches("[0-9]*")) { return change; }
			return null;
		};

		TextField maxVolChange = new TextField("0");
		TextField curVolChange = new TextField("0");

		curVolChange.setId("CollectionTextField");
		curVolChange.setTextFormatter(new TextFormatter<>(filter));
		curVolChange.lengthProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.intValue() > oldValue.intValue()) {
				if (curVolChange.getText().length() >= 3) {
					curVolChange.setText(curVolChange.getText().substring(0, 3));
				}
			}
		});

		maxVolChange.setId("CollectionTextField");
		maxVolChange.setTextFormatter(new TextFormatter<>(filter));
		maxVolChange.lengthProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.intValue() > oldValue.intValue()) {
				if (maxVolChange.getText().length() >= 3) {
					maxVolChange.setText(maxVolChange.getText().substring(0, 3));
				}
			}
		});

		FontIcon changeVolButtonIcon = new FontIcon(BootstrapIcons.ARROW_REPEAT);
		changeVolButtonIcon.setId("CollectionIcon");
		changeVolButtonIcon.setIconSize(30);

		Button changeVolCountButton = new Button();
		changeVolCountButton.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 310, BOTTOM_CARD_HEIGHT - 1);
		changeVolCountButton.setGraphic(changeVolButtonIcon);
		changeVolCountButton.setId("CollectionIconButton");
		changeVolCountButton.disableProperty().bind(maxVolChange.textProperty().isEmpty().or(curVolChange.textProperty().isEmpty()));
		changeVolCountButton.setOnMouseClicked((MouseEvent event) -> {
			if (Integer.parseInt(maxVolChange.getText()) >= Integer.parseInt(curVolChange.getText())){
				int newMaxVolumeAmount = Integer.parseInt(maxVolChange.getText());
				int newCurVolAmount = Integer.parseInt(curVolChange.getText());
				user.setTotalVolumes(user.getTotalVolumes() - series.getCurVolumes() + newCurVolAmount);
				maxVolumesInCollection = maxVolumesInCollection - series.getMaxVolumes() + newMaxVolumeAmount;
				series.setMaxVolumes(newMaxVolumeAmount);
				series.setCurVolumes(newCurVolAmount);
				progressTxt.setText(series.getCurVolumes() + "/" + series.getMaxVolumes());
				updateCollectionNumbers();
			}
		});
		VBox settingsButtons = new VBox(deleteSeriesButton, curVolChange, maxVolChange, changeVolCountButton);
		settingsButtons.setSpacing(10);
		settingsButtons.setPadding(new Insets(0, 7, 0, 0));
		settingsButtons.setAlignment(Pos.CENTER);

		HBox settingsCardPane = new HBox(userNotes, settingsButtons);
		settingsCardPane.setPadding(new Insets(5, 2, 4, 10));
		settingsCardPane.setAlignment(Pos.CENTER);
		settingsCardPane.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 20, SERIES_CARD_HEIGHT - BOTTOM_CARD_HEIGHT);
		settingsCardPane.setSpacing(10);
		return settingsCardPane;
	}
}