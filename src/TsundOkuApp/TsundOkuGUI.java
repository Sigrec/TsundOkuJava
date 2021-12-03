/*
    "Icons made by Freepik from www.flatpngn.com"
    Createed by Prem
 */

package TsundOkuApp;

import java.awt.Desktop;
import java.io.*;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import TsundOkuApp.PriceAnalysis.PriceComparisonDataModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.tbee.javafx.scene.layout.MigPane;

import static TsundOkuApp.PriceAnalysis.MasterAnalysis.ComparePricing;

public class TsundOkuGUI{
	// Menu BG Settings Window Components
	private SimpleStringProperty totalVolDisplayUpdate, totalToCollectUpdate;
	private String collectionMasterCSS;
	private final Stage addNewSeriesWindow = new Stage();
	private final Stage themeSettingsWindow = new Stage();
	private final Stage userSettingsWindow = new Stage();
	private final Stage priceComparisonWindow = new Stage();
	private TsundOkuTheme finalNewTheme;
	private HBox menuBar;
	private ComboBox<String> languageSelect;

	// Collection Components
	private FlowPane collection;
	private ScrollPane collectionScroll;

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
	private static ObservableList<PriceComparisonDataModel> priceComparisonData = FXCollections.observableArrayList(new PriceComparisonDataModel(new SimpleStringProperty("Overlord : The Undead King Oh! Vol 1"), new SimpleStringProperty("$9.99"), new SimpleStringProperty("In Stock"), new SimpleStringProperty("RobertsAnimeCornerStore")));

	// Users Main Data
	private int totalVolumesCollected = 0, maxVolumesInCollection = 0;
	private List<Series> userCollection = new ArrayList<>();
	private ObservableList<Series> filteredUserCollection;
	private Collector user;
	private TsundOkuTheme mainTheme;
	private BorderPane content;
	private String curTheme = "";
	private ObservableList<String> usersSavedThemes;
	private static final ObservableList<String> SELECTED_WEBSITES = FXCollections.observableArrayList();

	protected void setupTsundOkuGUI(Stage primaryStage) throws CloneNotSupportedException {
		GetUsersData();
		filteredUserCollection = FXCollections.observableArrayList(userCollection);
		usersSavedThemes = FXCollections.observableArrayList(user.getSavedThemes().keySet());
		mainTheme = user.getMainTheme();
		collectionMasterCSS = DrawTheme(mainTheme);

		content = new BorderPane();
		content.setMaxSize(WINDOW_WIDTH - 100, WINDOW_HEIGHT - 100);

		Scene mainScene = new Scene(content);
		mainScene.getStylesheets().addAll("CollectionCSS.css", "MenuCSS.css");

		userSettingsWindow.initStyle(StageStyle.UNIFIED);
		addNewSeriesWindow.initStyle(StageStyle.UNIFIED);
		themeSettingsWindow.initStyle(StageStyle.UNIFIED);
		SortCollection();
		CollectionSetup(primaryStage);
		MenuSetup(content, primaryStage);

		primaryStage.setMinWidth(SERIES_CARD_WIDTH + 550);
		primaryStage.setMinHeight(SERIES_CARD_HEIGHT + NAV_HEIGHT + 75);
		primaryStage.setTitle("TsundOku");
		primaryStage.getIcons().add(new Image("bookshelf.png"));
		primaryStage.setResizable(true);
		primaryStage.setOnCloseRequest((WindowEvent event) -> {
			StoresUsersData();
			if (addNewSeriesWindow.isShowing()) { addNewSeriesWindow.close(); }
			if (themeSettingsWindow.isShowing()) { themeSettingsWindow.close(); }
			if (userSettingsWindow.isShowing()) { userSettingsWindow.close(); }
		});
		primaryStage.initStyle(StageStyle.UNIFIED);
		primaryStage.setScene(mainScene);
		primaryStage.show();
	}

	private void SortCollection(){
		switch (user.getCurLanguage()) {
			case "English":
				userCollection.sort(Series::compareByEnglishTitle);
			case "Native":
				userCollection.sort(Series::compareByNativeTitle);
			default:
				userCollection.sort(Series::compareByRomajiTitle);
		}
	}

	private void StoresUsersData(){
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

	private void GetUsersData(){
		File collectionFile = new File("UserData.dat");
		if (!collectionFile.exists()) {
			new File("Covers").mkdir();
			user = new Collector("Default UserName", "Romaji", TsundOkuTheme.DEFAULT_THEME, new HashMap<>(), new ArrayList<>());
			user.addNewTheme(TsundOkuTheme.DEFAULT_THEME);
			StoresUsersData();
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

	private void MenuSetup(BorderPane content, Stage primaryStage) throws CloneNotSupportedException {
		SetupUserSettingsWindow(primaryStage);
		SetupCollectionSettingsWindow(primaryStage);
		SetupPriceComparisonWindow();
		SetupAddNewSeriesWindow(primaryStage);
		Text userName = new Text(user.getUserName());
		userName.setId("UserName");

		FontIcon userSettingsIcon = new FontIcon(BootstrapIcons.PERSON_CIRCLE);
		userSettingsIcon.setIconSize(20);
		userSettingsIcon.setId("UserSettingsIcon");

		Button userSettingsButton = new Button();
		userSettingsButton.setPrefWidth(45);
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
		themeSettingsButton.setPrefWidth(45);
		themeSettingsButton.setId("MenuButton");
		themeSettingsButton.setGraphic(themeSettingsIcon);
		themeSettingsButton.setOnMouseClicked(event -> {
			if (themeSettingsWindow.isShowing()){
				themeSettingsWindow.toFront();
			} else {
				themeSettingsWindow.show();
			}
		});

		FontIcon priceComparisonIcon = new FontIcon(BootstrapIcons.KANBAN);
		priceComparisonIcon.setIconSize(20);
		priceComparisonIcon.setId("ThemeSettingsIcon");

		Button priceComparisonButton = new Button();
		priceComparisonButton.setPrefWidth(45);
		priceComparisonButton.setId("MenuButton");
		priceComparisonButton.setGraphic(priceComparisonIcon);
		priceComparisonButton.setOnMouseClicked(event -> {
			if (priceComparisonWindow.isShowing()){
				priceComparisonWindow.toFront();
			} else {
				priceComparisonWindow.show();
			}
		});

		HBox settingsRoot = new HBox(userSettingsButton, themeSettingsButton, priceComparisonButton);
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
				filteredUserCollection = FXCollections.observableArrayList(userCollection.parallelStream().filter(series -> ContainsIgnoresCase(series.getRomajiTitle(), newText) | ContainsIgnoresCase(series.getEnglishTitle(), newText) | ContainsIgnoresCase(series.getNativeTitle(), newText) | ContainsIgnoresCase(series.getRomajiStaff(), newText) | ContainsIgnoresCase(series.getNativeStaff(), newText) | ContainsIgnoresCase(series.getPublisher(), newText) | ContainsIgnoresCase(series.getBookType(), newText) | ContainsIgnoresCase(series.getPrintStatus(), newText)).collect(Collectors.toList()));
			} else { // Reset back to the full list when field is blank and user presses enter
				filteredUserCollection = FXCollections.observableArrayList(userCollection);
			}
			CollectionSetup(primaryStage);
			UpdateCollectionNumbers();
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
			SortCollection();
			filteredUserCollection = FXCollections.observableArrayList(userCollection);
			CollectionSetup(primaryStage);
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

	private void SetupPriceComparisonWindow(){
		TextField titleEnter = new TextField();
		titleEnter.setId("MenuTextField");
		titleEnter.setPrefWidth(250);

		Label inputTitleLabel = new Label("Enter Title");
		inputTitleLabel.setId("MenuLabel");
		inputTitleLabel.setLabelFor(titleEnter);

		VBox inputTitleRoot = new VBox();
		inputTitleRoot.setId("SettingsLabel");
		inputTitleRoot.getChildren().addAll(inputTitleLabel, titleEnter);

		AtomicReference<Character> bookType = new AtomicReference<>('0');
		ToggleGroup bookTypeButtonGroup = new ToggleGroup();
		ToggleButton mangaButton = new ToggleButton("Manga");
		ToggleButton lightNovelButton = new ToggleButton("Novel");

		mangaButton.setToggleGroup(bookTypeButtonGroup);
		mangaButton.setId("MenuButton");
		mangaButton.setPrefSize(100, 10);
		mangaButton.setOnMouseClicked((MouseEvent event) -> {
			bookType.set('M');
			mangaButton.setDisable(true);
			lightNovelButton.setDisable(false);
		});

		lightNovelButton.setToggleGroup(bookTypeButtonGroup);
		lightNovelButton.setId("MenuButton");
		lightNovelButton.setPrefSize(100, 10);
		lightNovelButton.setOnMouseClicked((MouseEvent event) -> {
			bookType.set('N');
			mangaButton.setDisable(false);
			lightNovelButton.setDisable(true);
		});

		Label bookTypeLabel = new Label("Select Book Type");
		bookTypeLabel.setId("MenuLabel");

		HBox bookTypePane = new HBox(mangaButton, lightNovelButton);
		bookTypePane.setSpacing(10);
		bookTypePane.setAlignment(Pos.CENTER);

		VBox bookTypeRoot = new VBox(bookTypeLabel, bookTypePane);
		bookTypeRoot.setSpacing(5);
		bookTypeRoot.setAlignment(Pos.CENTER);

		ToggleButton rightStufButton = new ToggleButton("RightStuf");
		rightStufButton.setId("MenuToggleButton");
		rightStufButton.setPrefSize(100, 10);
		rightStufButton.setOnMouseClicked((MouseEvent event) -> {
			if (rightStufButton.isSelected()){
				SELECTED_WEBSITES.add("RS");
			} else {
				SELECTED_WEBSITES.remove("RS");
			}
			System.out.println(SELECTED_WEBSITES);
		});


		ToggleButton robertsAnimeCornerStoreButton = new ToggleButton("Rob");
		robertsAnimeCornerStoreButton.setId("MenuToggleButton");
		robertsAnimeCornerStoreButton.setPrefSize(100, 10);
		robertsAnimeCornerStoreButton.setOnMouseClicked((MouseEvent event) -> {
			if (robertsAnimeCornerStoreButton.isSelected()){
				SELECTED_WEBSITES.add("R");
			} else {
				SELECTED_WEBSITES.remove("R");
			}
			System.out.println(SELECTED_WEBSITES);
		});

		ToggleButton inStockTradesButton = new ToggleButton("IST");
		inStockTradesButton.setId("MenuToggleButton");
		inStockTradesButton.setPrefSize(100, 10);
		inStockTradesButton.setOnMouseClicked((MouseEvent event) -> {
			if (inStockTradesButton.isSelected()){
				SELECTED_WEBSITES.add("IST");
			} else {
				SELECTED_WEBSITES.remove("IST");
			}
			System.out.println(SELECTED_WEBSITES);
		});

		Label websiteLabel = new Label("Select Websites");
		websiteLabel.setId("MenuLabel");

		HBox websitePane = new HBox(rightStufButton, robertsAnimeCornerStoreButton, inStockTradesButton);
		websitePane.setSpacing(10);
		websitePane.setAlignment(Pos.CENTER);

		VBox websiteRoot = new VBox(websiteLabel, websitePane);
		websiteRoot.setSpacing(5);
		websiteRoot.setAlignment(Pos.CENTER);

		TableColumn<PriceComparisonDataModel, String> itemCol = new TableColumn<>("Volume");
		itemCol.setId("MenuTextLabel");
		itemCol.setCellValueFactory(new PropertyValueFactory<>("title"));
		itemCol.setSortable(false);

		TableColumn<PriceComparisonDataModel, String> priceCol = new TableColumn<>("Price ($USD)");
		priceCol.setId("MenuTextLabel");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
		priceCol.setPrefWidth(95);
		priceCol.setResizable(false);
		priceCol.setSortable(false);

		TableColumn<PriceComparisonDataModel, String> stockStatusCol = new TableColumn<>("Stock Status");
		stockStatusCol.setId("MenuTextLabel");
		stockStatusCol.setCellValueFactory(new PropertyValueFactory<>("stockStatus"));
		stockStatusCol.setPrefWidth(97);
		stockStatusCol.setResizable(false);
		stockStatusCol.setSortable(false);

		TableColumn<PriceComparisonDataModel, String> websiteCol = new TableColumn<>("Website");
		websiteCol.setId("MenuTextLabel");
		websiteCol.setCellValueFactory(new PropertyValueFactory<>("website"));
		websiteCol.setPrefWidth(160);
		websiteCol.setResizable(false);
		websiteCol.setSortable(false);

		TableView<PriceComparisonDataModel> table = new TableView<>();
		table.setEditable(false);
		table.setItems(priceComparisonData);
		table.autosize();
		table.setId("MenuTextLabel");
		table.setPrefWidth(700);
		table.autosize();
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		table.getColumns().addAll(itemCol, priceCol, stockStatusCol, websiteCol);

		Button runButton = new Button("Run");
		runButton.setPrefSize(60, 10);
		runButton.setId("MenuButton");
		runButton.disableProperty().bind(titleEnter.textProperty().isEmpty().or(bookTypeButtonGroup.selectedToggleProperty().isNull()).or(Bindings.isEmpty(SELECTED_WEBSITES)));
		runButton.setOnMouseClicked(event -> {
			runButton.setDisable(true);
			try {
				priceComparisonData = ComparePricing(titleEnter.getText().trim(), bookType.get(), SELECTED_WEBSITES);
				table.setItems(priceComparisonData);
				table.autosize();
			} catch (InterruptedException | FileNotFoundException e) {
				e.printStackTrace();
			}
			runButton.setDisable(false);
		});

		VBox priceComparisonPane = new VBox(inputTitleRoot, bookTypeRoot, websiteRoot, table, runButton);
		priceComparisonPane.setSpacing(100);
		priceComparisonPane.setId("NewSeriesPane");
		priceComparisonPane.setStyle(collectionMasterCSS);

		Scene priceComparisonScene = new Scene(priceComparisonPane);
		priceComparisonScene.getStylesheets().add("MenuCSS.css");
		priceComparisonWindow.setResizable(false);
		priceComparisonWindow.getIcons().add(new Image("bookshelf.png"));
		priceComparisonWindow.setTitle("Price Analysis");
		priceComparisonWindow.setScene(priceComparisonScene);
	}

	private void UpdateCollectionNumbers(){
		totalVolDisplayUpdate.set("Collected\n" + user.getTotalVolumes() + " Volumes");
		totalToCollectUpdate.set("Need To Collect\n" + (maxVolumesInCollection - user.getTotalVolumes()) + " Volumes");
	}

	private void SetupUserSettingsWindow(Stage primaryStage){
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
				MenuSetup(content, primaryStage);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		});

		HBox changeUserNameRoot = new HBox(enterUserName, saveUserNameButton);
		changeUserNameRoot.setSpacing(5);

		VBox userNameRoot = new VBox(enterUserNameLabel, changeUserNameRoot);
		userNameRoot.setId("SettingsLabel");

		Button exportToExcelButton = new Button("Export to Excel");
		exportToExcelButton.setId("MenuButton");
		exportToExcelButton.setOnMouseClicked(event -> {
			exportToExcelButton.setDisable(true);

			try {
				XSSFWorkbook workBook = new XSSFWorkbook();
				XSSFSheet excelCollection = workBook.createSheet(user.getUserName() + "_Collection");

				Row dataRow = excelCollection.createRow(0);
				Cell titleCell, bookTypeCell, printStatusCell, staffCell, publisherCell, curVolumesCell, maxVolumesCell, userNotesCell, linkCell;
				dataRow.createCell(0, CellType.STRING).setCellValue("Title");
				dataRow.createCell(1, CellType.STRING).setCellValue("Book Type");
				dataRow.createCell(2, CellType.STRING).setCellValue("Print Status");
				dataRow.createCell(3, CellType.STRING).setCellValue("Staff");
				dataRow.createCell(4, CellType.STRING).setCellValue("Publisher");
				dataRow.createCell(5, CellType.STRING).setCellValue("Current # of Volumes");
				dataRow.createCell(6, CellType.STRING).setCellValue("Max # of Volumes");
				dataRow.createCell(7, CellType.STRING).setCellValue("Notes");
				dataRow.createCell(8, CellType.STRING).setCellValue("AniList Link");

				CellStyle numStyle = workBook.createCellStyle();
				numStyle.setAlignment(HorizontalAlignment.CENTER);
				numStyle.setVerticalAlignment(VerticalAlignment.CENTER);

				CellStyle verticalAlign = workBook.createCellStyle();
				verticalAlign.setVerticalAlignment(VerticalAlignment.CENTER);

				CellStyle userNotesStyle = workBook.createCellStyle();
				userNotesStyle.setWrapText(true);
				userNotesStyle.setVerticalAlignment(VerticalAlignment.CENTER);

				switch (user.getCurLanguage()){
					case "Native" -> {
						for (int rowNum = 1; rowNum < userCollection.size(); rowNum++){
							Series curSeries = userCollection.get(rowNum);
							dataRow = excelCollection.createRow(rowNum);

							titleCell = dataRow.createCell(0, CellType.STRING);
							titleCell.setCellValue(curSeries.getNativeTitle());
							titleCell.setCellStyle(verticalAlign);
							excelCollection.autoSizeColumn(titleCell.getColumnIndex());

							bookTypeCell = dataRow.createCell(1, CellType.STRING);
							bookTypeCell.setCellValue(curSeries.getBookType());
							bookTypeCell.setCellStyle(numStyle);
							excelCollection.autoSizeColumn(bookTypeCell.getColumnIndex());

							printStatusCell = dataRow.createCell(2, CellType.STRING);
							printStatusCell.setCellValue(curSeries.getPrintStatus());
							printStatusCell.setCellStyle(numStyle);
							excelCollection.autoSizeColumn(printStatusCell.getColumnIndex());

							staffCell = dataRow.createCell(3, CellType.STRING);
							staffCell.setCellValue(curSeries.getNativeStaff());
							staffCell.setCellStyle(verticalAlign);
							excelCollection.autoSizeColumn(staffCell.getColumnIndex());

							publisherCell = dataRow.createCell(4, CellType.STRING);
							publisherCell.setCellValue(curSeries.getPublisher());
							publisherCell.setCellStyle(verticalAlign);
							excelCollection.autoSizeColumn(publisherCell.getColumnIndex());

							curVolumesCell = dataRow.createCell(5, CellType.NUMERIC);
							curVolumesCell.setCellValue(curSeries.getCurVolumes());
							curVolumesCell.setCellStyle(numStyle);
							excelCollection.autoSizeColumn(curVolumesCell.getColumnIndex());

							maxVolumesCell = dataRow.createCell(6, CellType.NUMERIC);
							maxVolumesCell.setCellValue(curSeries.getMaxVolumes());
							maxVolumesCell.setCellStyle(numStyle);
							excelCollection.autoSizeColumn(maxVolumesCell.getColumnIndex());

							userNotesCell = dataRow.createCell(7, CellType.STRING);
							userNotesCell.setCellValue(curSeries.getUserNotes().equals("Edit Notes:") ? "" : curSeries.getUserNotes());
							userNotesCell.setCellStyle(userNotesStyle);
							excelCollection.autoSizeColumn(userNotesCell.getColumnIndex());

							linkCell = dataRow.createCell(8, CellType.STRING);
							linkCell.setCellValue(curSeries.getLink());
							linkCell.setCellStyle(verticalAlign);
							excelCollection.autoSizeColumn(linkCell.getColumnIndex());
						}
					}
					case "English" -> {
						for (int rowNum = 1; rowNum < userCollection.size(); rowNum++){
							Series curSeries = userCollection.get(rowNum);
							dataRow = excelCollection.createRow(rowNum);

							titleCell = dataRow.createCell(0, CellType.STRING);
							titleCell.setCellValue(curSeries.getEnglishTitle());
							titleCell.setCellStyle(verticalAlign);
							excelCollection.autoSizeColumn(titleCell.getColumnIndex());

							bookTypeCell = dataRow.createCell(1, CellType.STRING);
							bookTypeCell.setCellValue(curSeries.getBookType());
							bookTypeCell.setCellStyle(numStyle);
							excelCollection.autoSizeColumn(bookTypeCell.getColumnIndex());

							printStatusCell = dataRow.createCell(2, CellType.STRING);
							printStatusCell.setCellValue(curSeries.getPrintStatus());
							printStatusCell.setCellStyle(numStyle);
							excelCollection.autoSizeColumn(printStatusCell.getColumnIndex());

							staffCell = dataRow.createCell(3, CellType.STRING);
							staffCell.setCellValue(curSeries.getRomajiStaff());
							staffCell.setCellStyle(verticalAlign);
							excelCollection.autoSizeColumn(staffCell.getColumnIndex());

							publisherCell = dataRow.createCell(4, CellType.STRING);
							publisherCell.setCellValue(curSeries.getPublisher());
							publisherCell.setCellStyle(verticalAlign);
							excelCollection.autoSizeColumn(publisherCell.getColumnIndex());

							curVolumesCell = dataRow.createCell(5, CellType.NUMERIC);
							curVolumesCell.setCellValue(curSeries.getCurVolumes());
							curVolumesCell.setCellStyle(numStyle);
							excelCollection.autoSizeColumn(curVolumesCell.getColumnIndex());

							maxVolumesCell = dataRow.createCell(6, CellType.NUMERIC);
							maxVolumesCell.setCellValue(curSeries.getMaxVolumes());
							maxVolumesCell.setCellStyle(numStyle);
							excelCollection.autoSizeColumn(maxVolumesCell.getColumnIndex());

							userNotesCell = dataRow.createCell(7, CellType.STRING);
							userNotesCell.setCellValue(curSeries.getUserNotes().equals("Edit Notes:") ? "" : curSeries.getUserNotes());
							userNotesCell.setCellStyle(userNotesStyle);
							excelCollection.autoSizeColumn(userNotesCell.getColumnIndex());

							linkCell = dataRow.createCell(8, CellType.STRING);
							linkCell.setCellValue(curSeries.getLink());
							linkCell.setCellStyle(verticalAlign);
							excelCollection.autoSizeColumn(linkCell.getColumnIndex());
						}
					}
					default -> {
						for (int rowNum = 1; rowNum < userCollection.size(); rowNum++){
							Series curSeries = userCollection.get(rowNum);
							dataRow = excelCollection.createRow(rowNum);

							titleCell = dataRow.createCell(0, CellType.STRING);
							titleCell.setCellValue(curSeries.getRomajiTitle());
							titleCell.setCellStyle(verticalAlign);
							excelCollection.autoSizeColumn(titleCell.getColumnIndex());

							bookTypeCell = dataRow.createCell(1, CellType.STRING);
							bookTypeCell.setCellValue(curSeries.getBookType());
							bookTypeCell.setCellStyle(numStyle);
							excelCollection.autoSizeColumn(bookTypeCell.getColumnIndex());

							printStatusCell = dataRow.createCell(2, CellType.STRING);
							printStatusCell.setCellValue(curSeries.getPrintStatus());
							printStatusCell.setCellStyle(numStyle);
							excelCollection.autoSizeColumn(printStatusCell.getColumnIndex());

							staffCell = dataRow.createCell(3, CellType.STRING);
							staffCell.setCellValue(curSeries.getRomajiStaff());
							staffCell.setCellStyle(verticalAlign);
							excelCollection.autoSizeColumn(staffCell.getColumnIndex());

							publisherCell = dataRow.createCell(4, CellType.STRING);
							publisherCell.setCellValue(curSeries.getPublisher());
							publisherCell.setCellStyle(verticalAlign);
							excelCollection.autoSizeColumn(publisherCell.getColumnIndex());

							curVolumesCell = dataRow.createCell(5, CellType.NUMERIC);
							curVolumesCell.setCellValue(curSeries.getCurVolumes());
							curVolumesCell.setCellStyle(numStyle);
							excelCollection.autoSizeColumn(curVolumesCell.getColumnIndex());

							maxVolumesCell = dataRow.createCell(6, CellType.NUMERIC);
							maxVolumesCell.setCellValue(curSeries.getMaxVolumes());
							maxVolumesCell.setCellStyle(numStyle);
							excelCollection.autoSizeColumn(maxVolumesCell.getColumnIndex());

							userNotesCell = dataRow.createCell(7, CellType.STRING);
							userNotesCell.setCellValue(curSeries.getUserNotes().equals("Edit Notes:") ? "" : curSeries.getUserNotes());
							userNotesCell.setCellStyle(userNotesStyle);
							excelCollection.autoSizeColumn(userNotesCell.getColumnIndex());

							linkCell = dataRow.createCell(8, CellType.STRING);
							linkCell.setCellValue(curSeries.getLink());
							linkCell.setCellStyle(verticalAlign);
							excelCollection.autoSizeColumn(linkCell.getColumnIndex());
						}
					}
				}

				FileOutputStream out = new FileOutputStream(user.getUserName() + "_Collection.xlsx");
				workBook.write(out);
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			exportToExcelButton.setDisable(false);
		});

		Button deleteCollectionButton = new Button("Delete Collection");
		deleteCollectionButton.setId("MenuButton");
		deleteCollectionButton.setOnMouseClicked(event -> {
			userCollection.clear();
			filteredUserCollection = FXCollections.observableArrayList(userCollection);
			CollectionSetup(primaryStage);
		});

		VBox userSettingsPane = new VBox(userNameRoot, exportToExcelButton, deleteCollectionButton);
		userSettingsPane.setSpacing(100);
		userSettingsPane.setId("NewSeriesPane");
		userSettingsPane.setStyle(collectionMasterCSS);

		Scene userSettingsScene = new Scene(userSettingsPane);
		userSettingsScene.getStylesheets().add("MenuCSS.css");

		userSettingsWindow.setResizable(false);
		userSettingsWindow.getIcons().add(new Image("bookshelf.png"));
		userSettingsWindow.setTitle(user.getUserName() + " Settings");
		userSettingsWindow.setScene(userSettingsScene);
	}

	private static boolean ContainsIgnoresCase(String str, String searchStr){
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

	private void SetupAddNewSeriesWindow(Stage primaryStage){
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

		Button submitButton = new Button("Add Series");
		submitButton.setPrefSize(60, 10);
		submitButton.setId("MenuButton");
		submitButton.disableProperty().bind(titleEnter.textProperty().isEmpty().or(publisherEnter.textProperty().isEmpty()).or(curVolumes.textProperty().isEmpty()).or(maxVolumes.textProperty().isEmpty()).or(bookTypeButtonGroup.selectedToggleProperty().isNull()).or(curVolumes.textProperty().greaterThan(maxVolumes.textProperty())));
		submitButton.setOnMouseClicked(event -> {
			if (Integer.parseInt(curVolumes.getText()) <= Integer.parseInt(maxVolumes.getText())){
				String newTitle = titleEnter.getText().trim();
				if (userCollection.stream().noneMatch(series -> ((series.getRomajiTitle().trim().equalsIgnoreCase(newTitle) || series.getEnglishTitle().trim().equalsIgnoreCase(newTitle) || series.getNativeTitle().trim().equalsIgnoreCase(newTitle)) && series.getBookType().equals(bookType.get())))){
					userCollection.add(new Series().CreateNewSeries(newTitle, publisherEnter.getText(), bookType.get(), Integer.parseInt(curVolumes.getText()), Integer.parseInt(maxVolumes.getText())));
					filteredUserCollection = FXCollections.observableArrayList(userCollection);
					CollectionSetup(primaryStage);
					UpdateCollectionNumbers();
				}
			}
		});

		VBox newSeriesPane = new VBox(inputTitleRoot, inputPublisherRoot, bookTypeRoot, volProgressRoot, submitButton);
		newSeriesPane.setId("NewSeriesPane");
		newSeriesPane.setStyle(collectionMasterCSS);

		Scene newSeriesScene = new Scene(newSeriesPane);
		newSeriesScene.getStylesheets().add("MenuCSS.css");
		addNewSeriesWindow.setResizable(false);
		addNewSeriesWindow.getIcons().add(new Image("bookshelf.png"));
		addNewSeriesWindow.setTitle("Add New Series");
		addNewSeriesWindow.setScene(newSeriesScene);
	}

	private void SetupCollectionSettingsWindow(Stage primaryStage) throws CloneNotSupportedException {
		finalNewTheme = (TsundOkuTheme) mainTheme.clone();

		ColorPicker menuBGColor = new ColorPicker();
		menuBGColor.setPrefWidth(181);
		menuBGColor.setValue(ConvertStringToColor(mainTheme.getMenuBGColor()));
		menuBGColor.setOnAction(event -> {
			finalNewTheme.setMenuBGColor(FormatColorCode(menuBGColor.getValue()));
			menuBar.setStyle(DrawTheme(finalNewTheme));
		});

		Label menuBGColorLabel = new Label("Menu BG");
		menuBGColorLabel.setLabelFor(menuBGColor);
		menuBGColorLabel.setId("SettingsTextStyling");

		VBox menuBGColorRoot = new VBox(menuBGColorLabel, menuBGColor);
		menuBGColorRoot.setSpacing(2);

		ColorPicker userNameColor = new ColorPicker();
		userNameColor.setPrefWidth(181);
		userNameColor.setValue(ConvertStringToColor(mainTheme.getUserNameColor()));
		userNameColor.setOnAction(event -> {
			finalNewTheme.setUserNameColor(FormatColorCode(userNameColor.getValue()));
			menuBar.setStyle(DrawTheme(finalNewTheme));
		});

		Label userNameColorLabel = new Label("User Name");
		userNameColorLabel.setLabelFor(userNameColor);
		userNameColorLabel.setId("SettingsTextStyling");

		VBox userNameColorRoot = new VBox(userNameColorLabel, userNameColor);
		userNameColorRoot.setSpacing(2);

		ColorPicker userNormalSettingsIconColor = new ColorPicker();
		userNormalSettingsIconColor.setPrefWidth(181);
		userNormalSettingsIconColor.setValue(ConvertStringToColor(mainTheme.getUserNormalSettingsIconColor()));
		userNormalSettingsIconColor.setOnAction(event -> {
			finalNewTheme.setUserNormalSettingsIconColor(FormatColorCode(userNormalSettingsIconColor.getValue()));
			menuBar.setStyle(DrawTheme(finalNewTheme));
		});

		Label userNormalSettingsIconColorLabel = new Label("User Settings Icon");
		userNormalSettingsIconColorLabel.setLabelFor(userNormalSettingsIconColor);
		userNormalSettingsIconColorLabel.setId("SettingsTextStyling");

		VBox userNormalSettingsIconColorRoot = new VBox(userNormalSettingsIconColorLabel, userNormalSettingsIconColor);
		userNormalSettingsIconColorRoot.setSpacing(2);

		ColorPicker userHoverSettingsIconColor = new ColorPicker();
		userHoverSettingsIconColor.setPrefWidth(181);
		userHoverSettingsIconColor.setValue(ConvertStringToColor(mainTheme.getUserHoverSettingsIconColor()));
		userHoverSettingsIconColor.setOnAction(event -> {
			finalNewTheme.setUserHoverSettingsIconColor(FormatColorCode(userHoverSettingsIconColor.getValue()));
			menuBar.setStyle(DrawTheme(finalNewTheme));
		});

		Label userHoverSettingsIconColorLabel = new Label("User Settings Icon (Hover)");
		userHoverSettingsIconColorLabel.setLabelFor(userHoverSettingsIconColor);
		userHoverSettingsIconColorLabel.setId("SettingsTextStyling");

		VBox userHoverSettingsIconColorRoot = new VBox(userHoverSettingsIconColorLabel, userHoverSettingsIconColor);
		userHoverSettingsIconColorRoot.setSpacing(2);

		ColorPicker themeNormalSettingsIconColor = new ColorPicker();
		themeNormalSettingsIconColor.setPrefWidth(181);
		themeNormalSettingsIconColor.setValue(ConvertStringToColor(mainTheme.getThemeNormalSettingsIconColor()));
		themeNormalSettingsIconColor.setOnAction(event -> {
			finalNewTheme.setThemeNormalSettingsIconColor(FormatColorCode(themeNormalSettingsIconColor.getValue()));
			menuBar.setStyle(DrawTheme(finalNewTheme));
		});

		Label themeNormalSettingsIconColorLabel = new Label("Theme Settings Icon");
		themeNormalSettingsIconColorLabel.setLabelFor(themeNormalSettingsIconColor);
		themeNormalSettingsIconColorLabel.setId("SettingsTextStyling");

		VBox themeNormalSettingsIconColorRoot = new VBox(themeNormalSettingsIconColorLabel, themeNormalSettingsIconColor);
		themeNormalSettingsIconColorRoot.setSpacing(2);

		ColorPicker themeHoverSettingsIconColor = new ColorPicker();
		themeHoverSettingsIconColor.setPrefWidth(181);
		themeHoverSettingsIconColor.setValue(ConvertStringToColor(mainTheme.getThemeHoverSettingsIconColor()));
		themeHoverSettingsIconColor.setOnAction(event -> {
			finalNewTheme.setThemeHoverSettingsIconColor(FormatColorCode(themeHoverSettingsIconColor.getValue()));
			menuBar.setStyle(DrawTheme(finalNewTheme));
		});

		Label themeHoverSettingsIconColorLabel = new Label("Theme Settings Icon (Hover)");
		themeHoverSettingsIconColorLabel.setLabelFor(themeHoverSettingsIconColor);
		themeHoverSettingsIconColorLabel.setId("SettingsTextStyling");

		VBox themeHoverSettingsIconColorRoot = new VBox(themeHoverSettingsIconColorLabel, themeHoverSettingsIconColor);
		themeHoverSettingsIconColorRoot.setSpacing(2);

		ColorPicker menuCollectionSearchBorderColor = new ColorPicker();
		menuCollectionSearchBorderColor.setPrefWidth(181);
		menuCollectionSearchBorderColor.setValue(ConvertStringToColor(mainTheme.getCollectionSearchBorderColor()));
		menuCollectionSearchBorderColor.setOnAction(event -> {
			finalNewTheme.setCollectionSearchBorderColor(FormatColorCode(menuCollectionSearchBorderColor.getValue()));
			menuBar.setStyle(DrawTheme(finalNewTheme));
		});

		Label menuCollectionSearchBorderColorLabel = new Label("Collection Search Border");
		menuCollectionSearchBorderColorLabel.setLabelFor(menuCollectionSearchBorderColor);
		menuCollectionSearchBorderColorLabel.setId("SettingsTextStyling");

		VBox menuCollectionSearchBorderColorRoot = new VBox(menuCollectionSearchBorderColorLabel, menuCollectionSearchBorderColor);
		menuCollectionSearchBorderColorRoot.setSpacing(2);

		ColorPicker menuCollectionSearchBGColor = new ColorPicker();
		menuCollectionSearchBGColor.setPrefWidth(181);
		menuCollectionSearchBGColor.setValue(ConvertStringToColor(mainTheme.getCollectionSearchBGColor()));
		menuCollectionSearchBGColor.setOnAction(event -> {
			finalNewTheme.setCollectionSearchBGColor(FormatColorCode(menuCollectionSearchBGColor.getValue()));
			menuBar.setStyle(DrawTheme(finalNewTheme));
		});

		Label menuCollectionSearchBGColorLabel = new Label("Collection Search BG");
		menuCollectionSearchBGColorLabel.setLabelFor(menuCollectionSearchBGColor);
		menuCollectionSearchBGColorLabel.setId("SettingsTextStyling");

		VBox menuCollectionSearchBGColorRoot = new VBox(menuCollectionSearchBGColorLabel, menuCollectionSearchBGColor);
		menuCollectionSearchBGColorRoot.setSpacing(2);

		ColorPicker menuCollectionSearchTextColor = new ColorPicker();
		menuCollectionSearchTextColor.setPrefWidth(181);
		menuCollectionSearchTextColor.setValue(ConvertStringToColor(mainTheme.getCollectionSearchTextColor()));
		menuCollectionSearchTextColor.setOnAction(event -> {
			finalNewTheme.setCollectionSearchTextColor(FormatColorCode(menuCollectionSearchTextColor.getValue()));
			menuBar.setStyle(DrawTheme(finalNewTheme));
		});

		Label menuCollectionSearchTextColorLabel = new Label("Collection Search Text");
		menuCollectionSearchTextColorLabel.setLabelFor(menuCollectionSearchTextColor);
		menuCollectionSearchTextColorLabel.setId("SettingsTextStyling");

		VBox menuCollectionSearchTextColorRoot = new VBox(menuCollectionSearchTextColorLabel, menuCollectionSearchTextColor);
		menuCollectionSearchTextColorRoot.setSpacing(2);

		ColorPicker menuBottomBorderColor = new ColorPicker();
		menuBottomBorderColor.setPrefWidth(181);
		menuBottomBorderColor.setValue(ConvertStringToColor(mainTheme.getMenuBottomBorderColor()));
		menuBottomBorderColor.setOnAction(event -> {
			finalNewTheme.setMenuBottomBorderColor(FormatColorCode(menuBottomBorderColor.getValue()));
			menuBar.setStyle(DrawTheme(finalNewTheme));
		});

		Label menuBottomBorderColorLabel = new Label("Divider");
		menuBottomBorderColorLabel.setLabelFor(menuBottomBorderColor);
		menuBottomBorderColorLabel.setId("SettingsTextStyling");

		VBox menuBottomBorderColorRoot = new VBox(menuBottomBorderColorLabel, menuBottomBorderColor);
		menuBottomBorderColorRoot.setSpacing(2);

		ColorPicker menuTextColor = new ColorPicker();
		menuTextColor.setPrefWidth(181);
		menuTextColor.setValue(ConvertStringToColor(mainTheme.getMenuTextColor()));
		menuTextColor.setOnAction(event -> {
			finalNewTheme.setMenuTextColor(FormatColorCode(menuTextColor.getValue()));
			menuBar.setStyle(DrawTheme(finalNewTheme));
		});

		Label menuTextColorLabel = new Label("Menu Text");
		menuTextColorLabel.setLabelFor(menuTextColor);
		menuTextColorLabel.setId("SettingsTextStyling");

		VBox menuTextColorRoot = new VBox(menuTextColorLabel, menuTextColor);
		menuTextColorRoot.setSpacing(2);

		ColorPicker menuNormalButtonBGColor = new ColorPicker();
		menuNormalButtonBGColor.setPrefWidth(181);
		menuNormalButtonBGColor.setValue(ConvertStringToColor(mainTheme.getMenuNormalButtonBGColor()));
		menuNormalButtonBGColor.setOnAction(event -> {
			finalNewTheme.setMenuNormalButtonBGColor(FormatColorCode(menuNormalButtonBGColor.getValue()));
			menuBar.setStyle(DrawTheme(finalNewTheme));
		});

		Label menuNormalButtonBGColorLabel = new Label("Menu Button BG");
		menuNormalButtonBGColorLabel.setLabelFor(menuNormalButtonBGColor);
		menuNormalButtonBGColorLabel.setId("SettingsTextStyling");

		VBox menuNormalButtonBGColorRoot = new VBox(menuNormalButtonBGColorLabel, menuNormalButtonBGColor);
		menuNormalButtonBGColorRoot.setSpacing(2);

		ColorPicker menuHoverButtonBGColor = new ColorPicker();
		menuHoverButtonBGColor.setPrefWidth(181);
		menuHoverButtonBGColor.setValue(ConvertStringToColor(mainTheme.getMenuHoverButtonBGColor()));
		menuHoverButtonBGColor.setOnAction(event -> {
			finalNewTheme.setMenuHoverButtonBGColor(FormatColorCode(menuHoverButtonBGColor.getValue()));
			menuBar.setStyle(DrawTheme(finalNewTheme));
		});

		Label menuHoverButtonBGColorLabel = new Label("Menu Button BG Color (Hover)");
		menuHoverButtonBGColorLabel.setLabelFor(menuHoverButtonBGColor);
		menuHoverButtonBGColorLabel.setId("SettingsTextStyling");

		VBox menuHoverButtonBGColorRoot = new VBox(menuHoverButtonBGColorLabel, menuHoverButtonBGColor);
		menuHoverButtonBGColorRoot.setSpacing(2);

		ColorPicker menuNormalButtonBorderColor = new ColorPicker();
		menuNormalButtonBorderColor.setPrefWidth(181);
		menuNormalButtonBorderColor.setValue(ConvertStringToColor(mainTheme.getMenuNormalButtonBorderColor()));
		menuNormalButtonBorderColor.setOnAction(event -> {
			finalNewTheme.setMenuNormalButtonBorderColor(FormatColorCode(menuNormalButtonBorderColor.getValue()));
			menuBar.setStyle(DrawTheme(finalNewTheme));
		});

		Label menuNormalButtonBorderColorLabel = new Label("Menu Button Border");
		menuNormalButtonBorderColorLabel.setLabelFor(menuNormalButtonBorderColor);
		menuNormalButtonBorderColorLabel.setId("SettingsTextStyling");

		VBox menuNormalButtonBorderColorRoot = new VBox(menuNormalButtonBorderColorLabel, menuNormalButtonBorderColor);
		menuNormalButtonBorderColorRoot.setSpacing(2);

		ColorPicker menuHoverButtonBorderColor = new ColorPicker();
		menuHoverButtonBorderColor.setPrefWidth(181);
		menuHoverButtonBorderColor.setValue(ConvertStringToColor(mainTheme.getMenuHoverButtonBorderColor()));
		menuHoverButtonBorderColor.setOnAction(event -> {
			finalNewTheme.setMenuHoverButtonBorderColor(FormatColorCode(menuHoverButtonBorderColor.getValue()));
			menuBar.setStyle(DrawTheme(finalNewTheme));
		});

		Label menuHoverButtonBorderColorLabel = new Label("Menu Button Border Color (Hover)");
		menuHoverButtonBorderColorLabel.setLabelFor(menuHoverButtonBorderColor);
		menuHoverButtonBorderColorLabel.setId("SettingsTextStyling");

		VBox menuHoverButtonBorderColorRoot = new VBox(menuHoverButtonBorderColorLabel, menuHoverButtonBorderColor);
		menuHoverButtonBorderColorRoot.setSpacing(2);

		ColorPicker menuNormalButtonTextColor = new ColorPicker();
		menuNormalButtonTextColor.setPrefWidth(181);
		menuNormalButtonTextColor.setValue(ConvertStringToColor(mainTheme.getMenuNormalButtonTextColor()));
		menuNormalButtonTextColor.setOnAction(event -> {
			finalNewTheme.setMenuNormalButtonTextColor(FormatColorCode(menuNormalButtonTextColor.getValue()));
			menuBar.setStyle(DrawTheme(finalNewTheme));
		});

		Label menuNormalButtonTextColorLabel = new Label("Menu Button Text");
		menuNormalButtonTextColorLabel.setLabelFor(menuNormalButtonTextColor);
		menuNormalButtonTextColorLabel.setId("SettingsTextStyling");

		VBox menuNormalButtonTextColorRoot = new VBox(menuNormalButtonTextColorLabel, menuNormalButtonTextColor);
		menuNormalButtonTextColorRoot.setSpacing(2);

		ColorPicker menuHoverButtonTextColor = new ColorPicker();
		menuHoverButtonTextColor.setPrefWidth(181);
		menuHoverButtonTextColor.setValue(ConvertStringToColor(mainTheme.getMenuHoverButtonTextColor()));
		menuHoverButtonTextColor.setOnAction(event -> {
			finalNewTheme.setMenuHoverButtonTextColor(FormatColorCode(menuHoverButtonTextColor.getValue()));
			menuBar.setStyle(DrawTheme(finalNewTheme));
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
		collectionBGColor.setValue(ConvertStringToColor(mainTheme.getCollectionBGColor()));
		collectionBGColor.setOnAction(event -> {
			finalNewTheme.setCollectionBGColor(FormatColorCode(collectionBGColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionBGColorLabel = new Label("Collection BG");
		collectionBGColorLabel.setLabelFor(collectionBGColor);
		collectionBGColorLabel.setId("SettingsTextStyling");

		VBox collectionBGColorRoot = new VBox(collectionBGColorLabel, collectionBGColor);
		collectionBGColorRoot.setSpacing(2);

		ColorPicker collectionLinkNormalBGColor = new ColorPicker();
		collectionLinkNormalBGColor.setPrefWidth(181);
		collectionLinkNormalBGColor.setValue(ConvertStringToColor(mainTheme.getCollectionLinkNormalBGColor()));
		collectionLinkNormalBGColor.setOnAction(event -> {
			finalNewTheme.setCollectionLinkNormalBGColor(FormatColorCode(collectionLinkNormalBGColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionLinkNormalBGColorLabel = new Label("Link BG");
		collectionLinkNormalBGColorLabel.setLabelFor(collectionLinkNormalBGColor);
		collectionLinkNormalBGColorLabel.setId("SettingsTextStyling");

		VBox collectionLinkNormalBGColorRoot = new VBox(collectionLinkNormalBGColorLabel, collectionLinkNormalBGColor);
		collectionLinkNormalBGColorRoot.setSpacing(2);

		ColorPicker collectionLinkHoverBGColor = new ColorPicker();
		collectionLinkHoverBGColor.setPrefWidth(181);
		collectionLinkHoverBGColor.setValue(ConvertStringToColor(mainTheme.getCollectionLinkHoverBGColor()));
		collectionLinkHoverBGColor.setOnAction(event -> {
			finalNewTheme.setCollectionLinkHoverBGColor(FormatColorCode(collectionLinkHoverBGColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionLinkHoverBGColorLabel = new Label("Link BG Color (Hover)");
		collectionLinkHoverBGColorLabel.setLabelFor(collectionLinkHoverBGColor);
		collectionLinkHoverBGColorLabel.setId("SettingsTextStyling");

		VBox collectionLinkHoverBGColorRoot = new VBox(collectionLinkHoverBGColorLabel, collectionLinkHoverBGColor);
		collectionLinkHoverBGColorRoot.setSpacing(2);

		ColorPicker collectionLinkNormalTextColor = new ColorPicker();
		collectionLinkNormalTextColor.setPrefWidth(181);
		collectionLinkNormalTextColor.setValue(ConvertStringToColor(mainTheme.getCollectionLinkNormalTextColor()));
		collectionLinkNormalTextColor.setOnAction(event -> {
			finalNewTheme.setCollectionLinkNormalTextColor(FormatColorCode(collectionLinkNormalTextColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionLinkNormalTextColorLabel = new Label("Print & Book Type Text");
		collectionLinkNormalTextColorLabel.setLabelFor(collectionLinkNormalTextColor);
		collectionLinkNormalTextColorLabel.setId("SettingsTextStyling");

		VBox collectionLinkNormalTextColorRoot = new VBox(collectionLinkNormalTextColorLabel, collectionLinkNormalTextColor);
		collectionLinkNormalTextColorRoot.setSpacing(2);

		ColorPicker collectionLinkHoverTextColor = new ColorPicker();
		collectionLinkHoverTextColor.setPrefWidth(181);
		collectionLinkHoverTextColor.setValue(ConvertStringToColor(mainTheme.getCollectionLinkHoverTextColor()));
		collectionLinkHoverTextColor.setOnAction(event -> {
			finalNewTheme.setCollectionLinkHoverTextColor(FormatColorCode(collectionLinkHoverTextColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionLinkHoverTextColorLabel = new Label("Print & Book Type Text (Hover)");
		collectionLinkHoverTextColorLabel.setLabelFor(collectionLinkHoverTextColor);
		collectionLinkHoverTextColorLabel.setId("SettingsTextStyling");

		VBox collectionLinkHoverTextColorRoot = new VBox(collectionLinkHoverTextColorLabel, collectionLinkHoverTextColor);
		collectionLinkHoverTextColorRoot.setSpacing(2);

		ColorPicker collectionMainCardBGColor = new ColorPicker();
		collectionMainCardBGColor.setPrefWidth(181);
		collectionMainCardBGColor.setValue(ConvertStringToColor(mainTheme.getCollectionCardMainBGColor()));
		collectionMainCardBGColor.setOnAction(event -> {
			finalNewTheme.setCollectionCardMainBGColor(FormatColorCode(collectionMainCardBGColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionMainCardBGColorLabel = new Label("Series Card BG");
		collectionMainCardBGColorLabel.setLabelFor(collectionMainCardBGColor);
		collectionMainCardBGColorLabel.setId("SettingsTextStyling");

		VBox collectionMainCardBGColorRoot = new VBox(collectionMainCardBGColorLabel, collectionMainCardBGColor);
		collectionMainCardBGColorRoot.setSpacing(2);

		ColorPicker collectionTitleColor = new ColorPicker();
		collectionTitleColor.setPrefWidth(181);
		collectionTitleColor.setValue(ConvertStringToColor(mainTheme.getCollectionTitleColor()));
		collectionTitleColor.setOnAction(event -> {
			finalNewTheme.setCollectionTitleColor(FormatColorCode(collectionTitleColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionTitleColorLabel = new Label("Series Title");
		collectionTitleColorLabel.setLabelFor(collectionTitleColor);
		collectionTitleColorLabel.setId("SettingsTextStyling");

		VBox collectionTitleColorRoot = new VBox(collectionTitleColorLabel, collectionTitleColor);
		collectionTitleColorRoot.setSpacing(2);

		ColorPicker collectionPublisherColor = new ColorPicker();
		collectionPublisherColor.setPrefWidth(181);
		collectionPublisherColor.setValue(ConvertStringToColor(mainTheme.getCollectionPublisherColor()));
		collectionPublisherColor.setOnAction(event -> {
			finalNewTheme.setCollectionPublisherColor(FormatColorCode(collectionPublisherColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionPublisherColorLabel = new Label("Publisher");
		collectionPublisherColorLabel.setLabelFor(collectionPublisherColor);
		collectionPublisherColorLabel.setId("SettingsTextStyling");

		VBox collectionPublisherColorRoot = new VBox(collectionPublisherColorLabel, collectionPublisherColor);
		collectionPublisherColorRoot.setSpacing(2);

		ColorPicker collectionMangakaColor = new ColorPicker();
		collectionMangakaColor.setPrefWidth(181);
		collectionMangakaColor.setValue(ConvertStringToColor(mainTheme.getCollectionMangakaColor()));
		collectionMangakaColor.setOnAction(event -> {
			finalNewTheme.setCollectionMangakaColor(FormatColorCode(collectionMangakaColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionMangakaColorLabel = new Label("Mangaka");
		collectionMangakaColorLabel.setLabelFor(collectionMangakaColor);
		collectionMangakaColorLabel.setId("SettingsTextStyling");

		VBox collectionMangakaColorRoot = new VBox(collectionMangakaColorLabel, collectionMangakaColor);
		collectionMangakaColorRoot.setSpacing(2);

		ColorPicker collectionDescColor = new ColorPicker();
		collectionDescColor.setPrefWidth(181);
		collectionDescColor.setValue(ConvertStringToColor(mainTheme.getCollectionDescColor()));
		collectionDescColor.setOnAction(event -> {
			finalNewTheme.setCollectionDescColor(FormatColorCode(collectionDescColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionDescColorLabel = new Label("Series Description");
		collectionDescColorLabel.setLabelFor(collectionDescColor);
		collectionDescColorLabel.setId("SettingsTextStyling");

		VBox collectionDescColorRoot = new VBox(collectionDescColorLabel, collectionDescColor);
		collectionDescColorRoot.setSpacing(2);

		ColorPicker collectionCardBottomBGColor = new ColorPicker();
		collectionCardBottomBGColor.setPrefWidth(181);
		collectionCardBottomBGColor.setValue(ConvertStringToColor(mainTheme.getCollectionCardBottomBGColor()));
		collectionCardBottomBGColor.setOnAction(event -> {
			finalNewTheme.setCollectionCardBottomBGColor(FormatColorCode(collectionCardBottomBGColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionCardBottomBGColorLabel = new Label("Progress BG");
		collectionCardBottomBGColorLabel.setLabelFor(collectionCardBottomBGColor);
		collectionCardBottomBGColorLabel.setId("SettingsTextStyling");

		VBox collectionCardBottomBGColorRoot = new VBox(collectionCardBottomBGColorLabel, collectionCardBottomBGColor);
		collectionCardBottomBGColorRoot.setSpacing(2);

		ColorPicker collectionIconButtonColor = new ColorPicker();
		collectionIconButtonColor.setPrefWidth(181);
		collectionIconButtonColor.setValue(ConvertStringToColor(mainTheme.getCollectionIconButtonColor()));
		collectionIconButtonColor.setOnAction(event -> {
			finalNewTheme.setCollectionIconButtonColor(FormatColorCode(collectionIconButtonColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionIconButtonColorLabel = new Label("Series Card Button");
		collectionIconButtonColorLabel.setLabelFor(collectionIconButtonColor);
		collectionIconButtonColorLabel.setId("SettingsTextStyling");

		VBox collectionIconButtonColorRoot = new VBox(collectionIconButtonColorLabel, collectionIconButtonColor);
		collectionIconButtonColorRoot.setSpacing(2);

		ColorPicker collectionNormalIconColor = new ColorPicker();
		collectionNormalIconColor.setPrefWidth(181);
		collectionNormalIconColor.setValue(ConvertStringToColor(mainTheme.getCollectionNormalIconColor()));
		collectionNormalIconColor.setOnAction(event -> {
			finalNewTheme.setCollectionNormalIconColor(FormatColorCode(collectionNormalIconColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionNormalIconColorLabel = new Label("Series Card Icon");
		collectionNormalIconColorLabel.setLabelFor(collectionNormalIconColor);
		collectionNormalIconColorLabel.setId("SettingsTextStyling");

		VBox collectionNormalIconColorRoot = new VBox(collectionNormalIconColorLabel, collectionNormalIconColor);
		collectionNormalIconColorRoot.setSpacing(2);

		ColorPicker collectionHoverIconColor = new ColorPicker();
		collectionHoverIconColor.setPrefWidth(181);
		collectionHoverIconColor.setValue(ConvertStringToColor(mainTheme.getCollectionHoverIconColor()));
		collectionHoverIconColor.setOnAction(event -> {
			finalNewTheme.setCollectionHoverIconColor(FormatColorCode(collectionHoverIconColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionHoverIconColorLabel = new Label("Series Card Icon (Hover)");
		collectionHoverIconColorLabel.setLabelFor(collectionHoverIconColor);
		collectionHoverIconColorLabel.setId("SettingsTextStyling");

		VBox collectionHoverIconColorRoot = new VBox(collectionHoverIconColorLabel, collectionHoverIconColor);
		collectionHoverIconColorRoot.setSpacing(2);

		ColorPicker collectionProgressBarColor = new ColorPicker();
		collectionProgressBarColor.setPrefWidth(181);
		collectionProgressBarColor.setValue(ConvertStringToColor(mainTheme.getCollectionProgressBarColor()));
		collectionProgressBarColor.setOnAction(event -> {
			finalNewTheme.setCollectionProgressBarColor(FormatColorCode(collectionProgressBarColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionProgressBarColorLabel = new Label("Progress Bar");
		collectionProgressBarColorLabel.setLabelFor(collectionProgressBarColor);
		collectionProgressBarColorLabel.setId("SettingsTextStyling");

		VBox collectionProgressBarColorRoot = new VBox(collectionProgressBarColorLabel, collectionProgressBarColor);
		collectionProgressBarColorRoot.setSpacing(2);

		ColorPicker collectionProgressBarBorderColor = new ColorPicker();
		collectionProgressBarBorderColor.setPrefWidth(181);
		collectionProgressBarBorderColor.setValue(ConvertStringToColor(mainTheme.getCollectionProgressBarBorderColor()));
		collectionProgressBarBorderColor.setOnAction(event -> {
			finalNewTheme.setCollectionProgressBarBorderColor(FormatColorCode(collectionProgressBarBorderColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionProgressBarBorderColorLabel = new Label("Progress Bar Border");
		collectionProgressBarBorderColorLabel.setLabelFor(collectionProgressBarBorderColor);
		collectionProgressBarBorderColorLabel.setId("SettingsTextStyling");

		VBox collectionProgressBarBorderColorRoot = new VBox(collectionProgressBarBorderColorLabel, collectionProgressBarBorderColor);
		collectionProgressBarBorderColorRoot.setSpacing(2);

		ColorPicker collectionProgressBarBGColor = new ColorPicker();
		collectionProgressBarBGColor.setPrefWidth(181);
		collectionProgressBarBGColor.setValue(ConvertStringToColor(mainTheme.getCollectionProgressBarBGColor()));
		collectionProgressBarBGColor.setOnAction(event -> {
			finalNewTheme.setCollectionProgressBarBGColor(FormatColorCode(collectionProgressBarBGColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionProgressBarBGColorLabel = new Label("Progress Bar BG");
		collectionProgressBarBGColorLabel.setLabelFor(collectionProgressBarBGColor);
		collectionProgressBarBGColorLabel.setId("SettingsTextStyling");

		VBox collectionProgressBarBGColorRoot = new VBox(collectionProgressBarBGColorLabel, collectionProgressBarBGColor);
		collectionProgressBarBGColorRoot.setSpacing(2);

		ColorPicker collectionNormalVolProgressTextColor = new ColorPicker();
		collectionNormalVolProgressTextColor.setPrefWidth(181);
		collectionNormalVolProgressTextColor.setValue(ConvertStringToColor(mainTheme.getCollectionNormalVolProgressTextColor()));
		collectionNormalVolProgressTextColor.setOnAction(event -> {
			finalNewTheme.setCollectionNormalVolProgressTextColor(FormatColorCode(collectionNormalVolProgressTextColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionNormalVolProgressTextColorLabel = new Label("Vol Progress Text");
		collectionNormalVolProgressTextColorLabel.setLabelFor(collectionNormalVolProgressTextColor);
		collectionNormalVolProgressTextColorLabel.setId("SettingsTextStyling");

		VBox collectionNormalVolProgressTextColorRoot = new VBox(collectionNormalVolProgressTextColorLabel, collectionNormalVolProgressTextColor);
		collectionNormalVolProgressTextColorRoot.setSpacing(2);

		ColorPicker collectionHoverVolProgressTextColor = new ColorPicker();
		collectionHoverVolProgressTextColor.setPrefWidth(181);
		collectionHoverVolProgressTextColor.setValue(ConvertStringToColor(mainTheme.getCollectionHoverVolProgressTextColor()));
		collectionHoverVolProgressTextColor.setOnAction(event -> {
			finalNewTheme.setCollectionHoverVolProgressTextColor(FormatColorCode(collectionHoverVolProgressTextColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionHoverVolProgressTextColorLabel = new Label("Vol Progress Text (Hover)");
		collectionHoverVolProgressTextColorLabel.setLabelFor(collectionHoverVolProgressTextColor);
		collectionHoverVolProgressTextColorLabel.setId("SettingsTextStyling");

		VBox collectionHoverVolProgressTextColorRoot = new VBox(collectionHoverVolProgressTextColorLabel, collectionHoverVolProgressTextColor);
		collectionHoverVolProgressTextColorRoot.setSpacing(2);

		ColorPicker collectionUserNotesBGColor = new ColorPicker();
		collectionUserNotesBGColor.setPrefWidth(181);
		collectionUserNotesBGColor.setValue(ConvertStringToColor(mainTheme.getCollectionUserNotesBGColor()));
		collectionUserNotesBGColor.setOnAction(event -> {
			finalNewTheme.setCollectionUserNotesBGColor(FormatColorCode(collectionUserNotesBGColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionUserNotesBGColorLabel = new Label("Series Notes BG");
		collectionUserNotesBGColorLabel.setLabelFor(collectionUserNotesBGColor);
		collectionUserNotesBGColorLabel.setId("SettingsTextStyling");

		VBox collectionUserNotesBGColorRoot = new VBox(collectionUserNotesBGColorLabel, collectionUserNotesBGColor);
		collectionUserNotesBGColorRoot.setSpacing(2);

		ColorPicker collectionUserNotesBorderColor = new ColorPicker();
		collectionUserNotesBorderColor.setPrefWidth(181);
		collectionUserNotesBorderColor.setValue(ConvertStringToColor(mainTheme.getCollectionUserNotesBorderColor()));
		collectionUserNotesBorderColor.setOnAction(event -> {
			finalNewTheme.setCollectionUserNotesBorderColor(FormatColorCode(collectionUserNotesBorderColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
		});

		Label collectionUserNotesBorderColorLabel = new Label("Series Notes Border");
		collectionUserNotesBorderColorLabel.setLabelFor(collectionUserNotesBorderColor);
		collectionUserNotesBorderColorLabel.setId("SettingsTextStyling");

		VBox collectionUserNotesBorderColorRoot = new VBox(collectionUserNotesBorderColorLabel, collectionUserNotesBorderColor);
		collectionUserNotesBorderColorRoot.setSpacing(2);

		ColorPicker collectionUserNotesTextColor = new ColorPicker();
		collectionUserNotesTextColor.setPrefWidth(181);
		collectionUserNotesTextColor.setValue(ConvertStringToColor(mainTheme.getCollectionUserNotesTextColor()));
		collectionUserNotesTextColor.setOnAction(event -> {
			finalNewTheme.setCollectionUserNotesTextColor(FormatColorCode(collectionUserNotesTextColor.getValue()));
			collection.setStyle(DrawTheme(finalNewTheme));
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
			collectionMasterCSS = DrawTheme(mainTheme);
			try {
				MenuSetup(content, primaryStage);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			CollectionSetup(primaryStage);
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

		themeSettingsWindow.setHeight(1000);
		themeSettingsWindow.setWidth(880);
		themeSettingsWindow.setTitle("TsundOku Theme Settings");
		themeSettingsWindow.getIcons().add(new Image("bookshelf.png"));
		themeSettingsWindow.setScene(collectionSettingsScene);
		themeSettingsWindow.setOnCloseRequest(event -> userCurrentTheme.setValue(user.getMainTheme().getThemeName()));
	}

	private String DrawTheme(TsundOkuTheme newTheme){
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

	private String FormatColorCode(Color newColor){
		return "rgba(" + (int) (newColor.getRed() * 255) + "," + (int) (newColor.getGreen() * 255) + "," + (int) (newColor.getBlue() * 255) + "," + String.format("%.2f", newColor.getOpacity()) + "); ";
	}

	private Color ConvertStringToColor(String color){
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

	private void CollectionSetup(Stage primaryStage){
		totalVolumesCollected = 0;
		maxVolumesInCollection = 0;

		collection = new FlowPane();
		collection.setId("Collection");
		collection.setStyle(collectionMasterCSS);

		collectionScroll = new ScrollPane(collection);
		collectionScroll.setId("CollectionScroll");
		collectionScroll.setVvalue(collectionScroll.getVvalue() - 2000);
		collectionScroll.getContent().setOnScroll(scrollEvent -> {
				double deltaY = scrollEvent.getDeltaY() * 1.5;
				double contentHeight = collectionScroll.getContent().getBoundsInParent().getHeight();
				double collectionScrollHeight = collectionScroll.getHeight();
				double diff = contentHeight - collectionScrollHeight;
				if (diff < 1) diff = 1;
				double vvalue = collectionScroll.getVvalue();
				collectionScroll.setVvalue(vvalue + (-deltaY / diff));
		});

		for (Series series : filteredUserCollection) {
			MigPane seriesCard = new MigPane();
			seriesCard.setId("SeriesCard");
			seriesCard.setPrefSize(SERIES_CARD_WIDTH, SERIES_CARD_HEIGHT);
			seriesCard.add(CreateLeftSideOfSeriesCard(series), "dock west");
			seriesCard.add(CreateRightSideOfSeriesCard(series, primaryStage), "dock east");
			collection.getChildren().add(seriesCard);
		}
		user.setTotalVolumes(totalVolumesCollected);
		content.setCenter(collectionScroll);
	}

	private Hyperlink CreateLeftSideOfSeriesCard(Series series){
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

	private String GetCurTitle(Series series, String curLanguage){
		return switch (curLanguage) {
			case "Romaji" -> series.getRomajiTitle();
			case "English" -> series.getEnglishTitle();
			case "Native" -> series.getNativeTitle();
			default -> "Error Title";
		};
	}

	private String GetCurMangaka(Series series, String curLanguage){
		return switch (curLanguage) {
			case "English", "Romaji" -> series.getRomajiStaff();
			case "Native" -> series.getNativeStaff();
			default -> "Error Mangaka";
		};
	}

	private MigPane CreateRightSideOfSeriesCard(Series series, Stage primaryStage){
		int curVolumes = series.getCurVolumes();
		int maxVolumes = series.getMaxVolumes();

		Text publisher = new Text(series.getPublisher());
		publisher.setId("Publisher");
		TextFlow publisherFlow = new TextFlow(publisher);
		publisherFlow.setPadding(new Insets(2, 5, 0, 10));

		String curLanguage = user.getCurLanguage();
		Text seriesTitle = new Text(GetCurTitle(series, curLanguage));
		seriesTitle.setWrappingWidth(RIGHT_SIDE_CARD_WIDTH - 20);
		seriesTitle.setId("SeriesTitle");
		seriesTitle.setOnMouseClicked(event -> {
			Clipboard copy = Clipboard.getSystemClipboard();
			ClipboardContent titleContent = new ClipboardContent();
			titleContent.putString(seriesTitle.getText());
			copy.setContent(titleContent);
		});
		TextFlow seriesTitleFlow = new TextFlow(seriesTitle);
		seriesTitleFlow.setLineSpacing(-6.5);
		seriesTitleFlow.setPadding(new Insets(1, 5, 0, 10));

		Text mangaka = new Text(GetCurMangaka(series, curLanguage));
		mangaka.setWrappingWidth(RIGHT_SIDE_CARD_WIDTH);
		mangaka.setId("Mangaka");
		TextFlow mangakaFlow = new TextFlow(mangaka);
		mangakaFlow.setPadding(new Insets(2, 5, 0, 10));

		Text desc = new Text(series.getSeriesDesc());
		desc.setId("SeriesDescriptionText");

		TextFlow descWrap = new TextFlow(desc);
		descWrap.setPrefSize(SERIES_CARD_WIDTH - 20, SERIES_CARD_HEIGHT - BOTTOM_CARD_HEIGHT);
		descWrap.setId("SeriesDescriptionWrap");

		ScrollPane descScroll = new ScrollPane(descWrap);
		descScroll.setId("SeriesDescriptionScroll");

		double volAmount = (double) curVolumes / maxVolumes;
		SimpleDoubleProperty volUpdate = new SimpleDoubleProperty();
		volUpdate.set(volAmount);

		Label progressTxt = new Label(curVolumes + "/" + maxVolumes);
		progressTxt.setId("VolProgressTxt");
		progressTxt.setPrefSize(RIGHT_SIDE_CARD_WIDTH - (RIGHT_SIDE_CARD_WIDTH - 70) + 20, 20);

		Button decrementButton = new Button("-");
		Button incrementButton = new Button("+");

		decrementButton.setStyle("-fx-font-size: 30; -fx-padding: -18 0 -8 0;");
		decrementButton.setId("VolProgressButton");
		decrementButton.setOnMouseClicked((MouseEvent event) -> {
			if (series.getCurVolumes() > 0){
				int seriesCurVolumes = series.getCurVolumes();
				int seriesMaxVolumes = series.getMaxVolumes();
				series.setCurVolumes(seriesCurVolumes - 1);
				seriesCurVolumes = series.getCurVolumes();
				progressTxt.setText(seriesCurVolumes + "/" + seriesMaxVolumes);
				volUpdate.set((double) seriesCurVolumes / seriesMaxVolumes);
				user.setTotalVolumes(user.getTotalVolumes() - 1);
				UpdateCollectionNumbers();
				incrementButton.setDisable(false);
			}
			if (series.getCurVolumes() == 0){
				decrementButton.setDisable(true);
			}
		});

		incrementButton.setStyle("-fx-font-size: 27; -fx-padding: -12 0 -3 0;");
		incrementButton.setId("VolProgressButton");
		incrementButton.setOnMouseClicked((MouseEvent event) -> {
			if (series.getCurVolumes() < series.getMaxVolumes()){
				int seriesCurVolumes = series.getCurVolumes();
				int seriesMaxVolumes = series.getMaxVolumes();
				series.setCurVolumes(seriesCurVolumes + 1);
				seriesCurVolumes = series.getCurVolumes();
				progressTxt.setText(seriesCurVolumes + "/" + seriesMaxVolumes);
				volUpdate.set((double) seriesCurVolumes / seriesMaxVolumes);
				user.setTotalVolumes(user.getTotalVolumes() + 1);
				UpdateCollectionNumbers();
				decrementButton.setDisable(false);
			}
			if (series.getCurVolumes().equals(series.getMaxVolumes())){
				incrementButton.setDisable(true);
			}
		});

		if (curVolumes == 0) { decrementButton.setDisable(true); }
		else if (curVolumes == maxVolumes) { incrementButton.setDisable(true); }

		ProgressBar volProgressBar = new ProgressBar();
		volProgressBar.progressProperty().bind(volUpdate);
		volProgressBar.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 110, BOTTOM_CARD_HEIGHT);
		volProgressBar.setId("ProgressBar");

		FontIcon seriesSettingIcon = new FontIcon(BootstrapIcons.JOURNAL_TEXT);
		seriesSettingIcon.setIconSize(25);
		seriesSettingIcon.setId("CollectionIcon");

		AtomicReference<Button> seriesCardSettingsButton = new AtomicReference<>(new Button());
		seriesCardSettingsButton.get().setPrefSize(RIGHT_SIDE_CARD_WIDTH - 310, BOTTOM_CARD_HEIGHT - 1);
		seriesCardSettingsButton.get().setGraphic(seriesSettingIcon);
		seriesCardSettingsButton.get().setId("CollectionIconButton");

		FontIcon backToSeriesDataIcon = new FontIcon(BootstrapIcons.CARD_HEADING);
		backToSeriesDataIcon.setId("CollectionIcon");
		backToSeriesDataIcon.setIconSize(25);

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
		rightSideBottomPane.setId("SeriesCardBottomPane");
		rightSideBottomPane.setLeft(seriesCardSettingsButton.get());
		rightSideBottomPane.setCenter(volProgressBar);
		rightSideBottomPane.setRight(volProgress);

		MigPane seriesData = new MigPane("insets 5 0 3 0, align left", "[]", "[][]-4[]2[]");
		seriesData.setStyle("-fx-border-radius: 5 5 0 0;  -fx-background-radius: 5 5 0 0;");
		seriesData.add(publisherFlow,"wrap, hmax 3.5");
		seriesData.add(seriesTitleFlow, "wrap");
		seriesData.add(mangakaFlow, "wrap");
		seriesData.add(descScroll);

		MigPane rightSideOfSeriesCard = new MigPane();
		rightSideOfSeriesCard.setId("RightSideCard");
		rightSideOfSeriesCard.setPrefSize(RIGHT_SIDE_CARD_WIDTH, SERIES_CARD_HEIGHT - BOTTOM_CARD_HEIGHT);
		rightSideOfSeriesCard.add(seriesData, "north");
		rightSideOfSeriesCard.add(rightSideBottomPane);

		HBox seriesSettingsPane = CreateSeriesCardSettingPane(series, primaryStage, progressTxt, volUpdate, decrementButton, incrementButton);
		seriesCardSettingsButton.get().setOnMouseClicked(event -> {
			Button seriesButton = seriesCardSettingsButton.get();
			if (seriesButton.getGraphic() == seriesSettingIcon){
				rightSideOfSeriesCard.remove(seriesData);
				rightSideOfSeriesCard.add(seriesSettingsPane, "north");
				seriesButton.setGraphic(backToSeriesDataIcon);
			}
			else{
				rightSideOfSeriesCard.remove(seriesSettingsPane);
				rightSideOfSeriesCard.add(seriesData, "north");
				seriesButton.setGraphic(seriesSettingIcon);
			}
		});

		totalVolumesCollected += curVolumes;
		maxVolumesInCollection += maxVolumes;

		return rightSideOfSeriesCard;
	}

	private HBox CreateSeriesCardSettingPane(Series series, Stage primaryStage, Label progressTxt, SimpleDoubleProperty volUpdate, Button decrementButton, Button incrementButton){
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
			filteredUserCollection = FXCollections.observableArrayList(userCollection);
			CollectionSetup(primaryStage);
			user.setTotalVolumes(user.getTotalVolumes() - series.getCurVolumes());
			UpdateCollectionNumbers();
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
				volUpdate.set((double) newCurVolAmount / newMaxVolumeAmount); //Update progress bar
				progressTxt.setText(newCurVolAmount + "/" + newMaxVolumeAmount);
				UpdateCollectionNumbers();
				if (newCurVolAmount == 0) {
					decrementButton.setDisable(true);
					incrementButton.setDisable(false);
				}
				else if (newCurVolAmount == newMaxVolumeAmount) {
					decrementButton.setDisable(false);
					incrementButton.setDisable(true);
				}
				else{
					incrementButton.setDisable(false);
					decrementButton.setDisable(false);
				}
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