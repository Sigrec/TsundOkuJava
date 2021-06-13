/*
    "Icons made by Freepik from www.flaticon.com"
 */

package TsundOkuApp;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

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
import javafx.scene.layout.BorderPane;
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

public class TsundOkuGUI{
	//Menu B& Settings Window Components
	private Text totalVolDisplay, totalToCollect;
	private String collectionMasterCSS;
	private Stage addNewSeriesWindow = new Stage();
	private Stage settingsWindow;
	private TsundOkuTheme finalNewTheme;

	//Component Size Details
	private static final int SERIES_CARD_WIDTH = 515;
	private static final int SERIES_CARD_HEIGHT = 245;
	private static final int LEFT_SIDE_CARD_WIDTH = 165;
	private static final int RIGHT_SIDE_CARD_WIDTH = SERIES_CARD_WIDTH - LEFT_SIDE_CARD_WIDTH;
	private static final int MAX_SERIES_VOLUME_AMOUNT = 999;
	private static final int NAV_HEIGHT = 100;
	private static final int BOTTOM_CARD_HEIGHT = 38;
	private static final double WINDOW_HEIGHT = Screen.getPrimary().getBounds().getHeight();
	private static final double WINDOW_WIDTH = Screen.getPrimary().getBounds().getWidth();
	private static final ObservableList<String> LANGUAGE_OPTIONS = FXCollections.observableArrayList("Romaji", "English", "日本語");

	//Users Main Data
	private Integer totalVolumesCollected = 0, maxVolumesInCollection = 0;
	private List<Series> userCollection = new ArrayList<>();
	private List<Series> filteredUserCollection;
	private Collector user;
	private TsundOkuTheme mainTheme;
	private BorderPane content;
	private Scene mainScene;
	private char language;
	private ObservableList<String> usersSavedThemes;

	//Other
	private static double xOffset = 0;
	private static double yOffset = 0;

	public TsundOkuGUI() { }

	protected void setupTsundOkuGUI(Stage primaryStage){
		getUsersData();
		usersSavedThemes = FXCollections.observableArrayList(user.getSavedThemes().keySet());
		language = user.getCurLanguage();
		mainTheme = user.getMainTheme();
		collectionMasterCSS = drawTheme(mainTheme);

		content = new BorderPane();
		content.setCache(true);
		content.setCacheHint(CacheHint.DEFAULT);

		mainScene = new Scene(content);
		mainScene.getStylesheets().addAll("CollectionCSS.css", "MenuCSS.css");
		collectionSetup(primaryStage);
		menuSetup(content, primaryStage, mainScene);

		primaryStage.setMinWidth(SERIES_CARD_WIDTH + 520);
		primaryStage.setMinHeight(SERIES_CARD_HEIGHT + NAV_HEIGHT + 75);
		primaryStage.setMaxWidth(WINDOW_WIDTH);
		primaryStage.setMaxHeight(WINDOW_HEIGHT);
		primaryStage.setTitle("TsundOku V1.0.0 Beta");
		primaryStage.getIcons().add(new Image("File:src/main/resources/bookshelf.png"));
		primaryStage.setResizable(true);
		primaryStage.setOnCloseRequest((WindowEvent event) -> {
			storeUserData();
			addNewSeriesWindow.close();
		});
		primaryStage.initStyle(StageStyle.UNIFIED);
		primaryStage.setScene(mainScene);
		primaryStage.show();
	}

	private String drawTheme(TsundOkuTheme newTheme){
		return  "-fx-menu-bg-color: " + newTheme.getMenuBGColor() +
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
				"-fx-collection-subheader-color: " + newTheme.getCollectionSubHeaderColor() +
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

	private Stage setupCollectionSettingsWindow(Stage primaryStage, FlowPane collection){
		TsundOkuTheme newTheme = null;
		try {
			newTheme = (TsundOkuTheme) mainTheme.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		finalNewTheme = newTheme;

		ColorPicker menuBGColor = new ColorPicker();
		menuBGColor.setPrefWidth(181);
		menuBGColor.setValue(convertStringToColor(mainTheme.getMenuBGColor()));
		menuBGColor.setOnAction(event -> {
			finalNewTheme.setMenuBGColor(formatColorCode(menuBGColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label menuBGColorLabel = new Label("Menu Background Color");
		menuBGColorLabel.setLabelFor(menuBGColor);
		menuBGColorLabel.setId("SettingsTextStyling");

		VBox menuBGColorRoot = new VBox();
		menuBGColorRoot.setSpacing(2);
		menuBGColorRoot.getChildren().addAll(menuBGColorLabel, menuBGColor);

		ColorPicker menuBottomBorderColor = new ColorPicker();
		menuBottomBorderColor.setPrefWidth(181);
		menuBottomBorderColor.setValue(convertStringToColor(mainTheme.getMenuBottomBorderColor()));
		menuBottomBorderColor.setOnAction(event -> {
			finalNewTheme.setMenuBottomBorderColor(formatColorCode(menuBottomBorderColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label menuBottomBorderColorLabel = new Label("Divider Color");
		menuBottomBorderColorLabel.setLabelFor(menuBGColor);
		menuBottomBorderColorLabel.setId("SettingsTextStyling");

		VBox menuBottomBorderColorRoot = new VBox();
		menuBottomBorderColorRoot.setSpacing(2);
		menuBottomBorderColorRoot.getChildren().addAll(menuBottomBorderColorLabel, menuBottomBorderColor);

		ColorPicker menuTextColor = new ColorPicker();
		menuTextColor.setPrefWidth(181);
		menuTextColor.setValue(convertStringToColor(mainTheme.getMenuTextColor()));
		menuTextColor.setOnAction(event -> {
			finalNewTheme.setMenuTextColor(formatColorCode(menuTextColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label menuTextColorLabel = new Label("Menu Text Color");
		menuTextColorLabel.setLabelFor(menuBGColor);
		menuTextColorLabel.setId("SettingsTextStyling");

		VBox menuTextColorRoot = new VBox();
		menuTextColorRoot.setSpacing(2);
		menuTextColorRoot.getChildren().addAll(menuTextColorLabel, menuTextColor);

		ColorPicker menuNormalButtonBGColor = new ColorPicker();
		menuNormalButtonBGColor.setPrefWidth(181);
		menuNormalButtonBGColor.setValue(convertStringToColor(mainTheme.getMenuNormalButtonBGColor()));
		menuNormalButtonBGColor.setOnAction(event -> {
			finalNewTheme.setMenuNormalButtonBGColor(formatColorCode(menuNormalButtonBGColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label menuNormalButtonBGColorLabel = new Label("Menu Button BG Color");
		menuNormalButtonBGColorLabel.setLabelFor(menuBGColor);
		menuNormalButtonBGColorLabel.setId("SettingsTextStyling");

		VBox menuNormalButtonBGColorRoot = new VBox();
		menuNormalButtonBGColorRoot.setSpacing(2);
		menuNormalButtonBGColorRoot.getChildren().addAll(menuNormalButtonBGColorLabel, menuNormalButtonBGColor);

		ColorPicker menuHoverButtonBGColor = new ColorPicker();
		menuHoverButtonBGColor.setPrefWidth(181);
		menuHoverButtonBGColor.setValue(convertStringToColor(mainTheme.getMenuHoverButtonBGColor()));
		menuHoverButtonBGColor.setOnAction(event -> {
			finalNewTheme.setMenuHoverButtonBGColor(formatColorCode(menuHoverButtonBGColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label menuHoverButtonBGColorLabel = new Label("Menu Button BG Color (Hover)");
		menuHoverButtonBGColorLabel.setLabelFor(menuBGColor);
		menuHoverButtonBGColorLabel.setId("SettingsTextStyling");

		VBox menuHoverButtonBGColorRoot = new VBox();
		menuHoverButtonBGColorRoot.setSpacing(2);
		menuHoverButtonBGColorRoot.getChildren().addAll(menuHoverButtonBGColorLabel, menuHoverButtonBGColor);

		ColorPicker menuNormalButtonBorderColor = new ColorPicker();
		menuNormalButtonBorderColor.setPrefWidth(181);
		menuNormalButtonBorderColor.setValue(convertStringToColor(mainTheme.getMenuNormalButtonBorderColor()));
		menuNormalButtonBorderColor.setOnAction(event -> {
			finalNewTheme.setMenuNormalButtonBorderColor(formatColorCode(menuNormalButtonBorderColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label menuNormalButtonBorderColorLabel = new Label("Menu Button Border Color");
		menuNormalButtonBorderColorLabel.setLabelFor(menuBGColor);
		menuNormalButtonBorderColorLabel.setId("SettingsTextStyling");

		VBox menuNormalButtonBorderColorRoot = new VBox();
		menuNormalButtonBorderColorRoot.setSpacing(2);
		menuNormalButtonBorderColorRoot.getChildren().addAll(menuNormalButtonBorderColorLabel, menuNormalButtonBorderColor);

		ColorPicker menuHoverButtonBorderColor = new ColorPicker();
		menuHoverButtonBorderColor.setPrefWidth(181);
		menuHoverButtonBorderColor.setValue(convertStringToColor(mainTheme.getMenuHoverButtonBorderColor()));
		menuHoverButtonBorderColor.setOnAction(event -> {
			finalNewTheme.setMenuHoverButtonBorderColor(formatColorCode(menuHoverButtonBorderColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label menuHoverButtonBorderColorLabel = new Label("Menu Button Border Color (Hover)");
		menuHoverButtonBorderColorLabel.setLabelFor(menuBGColor);
		menuHoverButtonBorderColorLabel.setId("SettingsTextStyling");

		VBox menuHoverButtonBorderColorRoot = new VBox();
		menuHoverButtonBorderColorRoot.setSpacing(2);
		menuHoverButtonBorderColorRoot.getChildren().addAll(menuHoverButtonBorderColorLabel, menuHoverButtonBorderColor);

		ColorPicker menuNormalButtonTextColor = new ColorPicker();
		menuNormalButtonTextColor.setPrefWidth(181);
		menuNormalButtonTextColor.setValue(convertStringToColor(mainTheme.getMenuNormalButtonTextColor()));
		menuNormalButtonTextColor.setOnAction(event -> {
			finalNewTheme.setMenuNormalButtonTextColor(formatColorCode(menuNormalButtonTextColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label menuNormalButtonTextColorLabel = new Label("Menu Button Text Color");
		menuNormalButtonTextColorLabel.setLabelFor(menuBGColor);
		menuNormalButtonTextColorLabel.setId("SettingsTextStyling");

		VBox menuNormalButtonTextColorRoot = new VBox();
		menuNormalButtonTextColorRoot.setSpacing(2);
		menuNormalButtonTextColorRoot.getChildren().addAll(menuNormalButtonTextColorLabel, menuNormalButtonTextColor);

		ColorPicker menuHoverButtonTextColor = new ColorPicker();
		menuHoverButtonTextColor.setPrefWidth(181);
		menuHoverButtonTextColor.setValue(convertStringToColor(mainTheme.getMenuHoverButtonTextColor()));
		menuHoverButtonTextColor.setOnAction(event -> {
			finalNewTheme.setMenuHoverButtonTextColor(formatColorCode(menuHoverButtonTextColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label menuHoverButtonTextColorLabel = new Label("Menu Button Text Color (Hover)");
		menuHoverButtonTextColorLabel.setLabelFor(menuBGColor);
		menuHoverButtonTextColorLabel.setId("SettingsTextStyling");

		VBox menuHoverButtonTextColorRoot = new VBox();
		menuHoverButtonTextColorRoot.setSpacing(2);
		menuHoverButtonTextColorRoot.getChildren().addAll(menuHoverButtonTextColorLabel, menuHoverButtonTextColor);

		FlowPane menuThemeChangePane = new FlowPane();
		menuThemeChangePane.setId("ThemeSettingsBox");
		menuThemeChangePane.getChildren().addAll(menuBGColorRoot, menuBottomBorderColorRoot, menuTextColorRoot, menuNormalButtonBGColorRoot, menuHoverButtonBGColorRoot, menuNormalButtonBorderColorRoot, menuHoverButtonBorderColorRoot, menuNormalButtonTextColorRoot, menuHoverButtonTextColorRoot);

		Label menuLabel = new Label("Menu Bar Theme");
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

		VBox collectionBGColorRoot = new VBox();
		collectionBGColorRoot.setSpacing(2);
		collectionBGColorRoot.getChildren().addAll(collectionBGColorLabel, collectionBGColor);

		ColorPicker collectionLinkNormalBGColor = new ColorPicker();
		collectionLinkNormalBGColor.setPrefWidth(181);
		collectionLinkNormalBGColor.setValue(convertStringToColor(mainTheme.getCollectionLinkNormalBGColor()));
		collectionLinkNormalBGColor.setOnAction(event -> {
			finalNewTheme.setCollectionLinkNormalBGColor(formatColorCode(collectionLinkNormalBGColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionLinkNormalBGColorLabel = new Label("Link BG Color");
		collectionLinkNormalBGColorLabel.setLabelFor(collectionLinkNormalBGColor);
		collectionLinkNormalBGColorLabel.setId("SettingsTextStyling");

		VBox collectionLinkNormalBGColorRoot = new VBox();
		collectionLinkNormalBGColorRoot.setSpacing(2);
		collectionLinkNormalBGColorRoot.getChildren().addAll(collectionLinkNormalBGColorLabel, collectionLinkNormalBGColor);

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

		VBox collectionLinkHoverBGColorRoot = new VBox();
		collectionLinkHoverBGColorRoot.setSpacing(2);
		collectionLinkHoverBGColorRoot.getChildren().addAll(collectionLinkHoverBGColorLabel, collectionLinkHoverBGColor);

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

		VBox collectionLinkNormalTextColorRoot = new VBox();
		collectionLinkNormalTextColorRoot.setSpacing(2);
		collectionLinkNormalTextColorRoot.getChildren().addAll(collectionLinkNormalTextColorLabel, collectionLinkNormalTextColor);

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

		VBox collectionLinkHoverTextColorRoot = new VBox();
		collectionLinkHoverTextColorRoot.setSpacing(2);
		collectionLinkHoverTextColorRoot.getChildren().addAll(collectionLinkHoverTextColorLabel, collectionLinkHoverTextColor);

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

		VBox collectionMainCardBGColorRoot = new VBox();
		collectionMainCardBGColorRoot.setSpacing(2);
		collectionMainCardBGColorRoot.getChildren().addAll(collectionMainCardBGColorLabel, collectionMainCardBGColor);

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

		VBox collectionTitleColorRoot = new VBox();
		collectionTitleColorRoot.setSpacing(2);
		collectionTitleColorRoot.getChildren().addAll(collectionTitleColorLabel, collectionTitleColor);

		ColorPicker collectionSubHeaderColor = new ColorPicker();
		collectionSubHeaderColor.setPrefWidth(181);
		collectionSubHeaderColor.setValue(convertStringToColor(mainTheme.getCollectionSubHeaderColor()));
		collectionSubHeaderColor.setOnAction(event -> {
			finalNewTheme.setCollectionSubHeaderColor(formatColorCode(collectionSubHeaderColor.getValue()));
			collection.setStyle(drawTheme(finalNewTheme));
		});

		Label collectionSubHeaderColorLabel = new Label("Publisher & Mangaka");
		collectionSubHeaderColorLabel.setLabelFor(collectionSubHeaderColor);
		collectionSubHeaderColorLabel.setId("SettingsTextStyling");

		VBox collectionSubHeaderColorRoot = new VBox();
		collectionSubHeaderColorRoot.setSpacing(2);
		collectionSubHeaderColorRoot.getChildren().addAll(collectionSubHeaderColorLabel, collectionSubHeaderColor);

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

		VBox collectionDescColorRoot = new VBox();
		collectionDescColorRoot.setSpacing(2);
		collectionDescColorRoot.getChildren().addAll(collectionDescColorLabel, collectionDescColor);

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

		VBox collectionCardBottomBGColorRoot = new VBox();
		collectionCardBottomBGColorRoot.setSpacing(2);
		collectionCardBottomBGColorRoot.getChildren().addAll(collectionCardBottomBGColorLabel, collectionCardBottomBGColor);

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

		VBox collectionIconButtonColorRoot = new VBox();
		collectionIconButtonColorRoot.setSpacing(2);
		collectionIconButtonColorRoot.getChildren().addAll(collectionIconButtonColorLabel, collectionIconButtonColor);

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

		VBox collectionNormalIconColorRoot = new VBox();
		collectionNormalIconColorRoot.setSpacing(2);
		collectionNormalIconColorRoot.getChildren().addAll(collectionNormalIconColorLabel, collectionNormalIconColor);

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

		VBox collectionHoverIconColorRoot = new VBox();
		collectionHoverIconColorRoot.setSpacing(2);
		collectionHoverIconColorRoot.getChildren().addAll(collectionHoverIconColorLabel, collectionHoverIconColor);

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

		VBox collectionProgressBarColorRoot = new VBox();
		collectionProgressBarColorRoot.setSpacing(2);
		collectionProgressBarColorRoot.getChildren().addAll(collectionProgressBarColorLabel, collectionProgressBarColor);

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

		VBox collectionProgressBarBorderColorRoot = new VBox();
		collectionProgressBarBorderColorRoot.setSpacing(2);
		collectionProgressBarBorderColorRoot.getChildren().addAll(collectionProgressBarBorderColorLabel, collectionProgressBarBorderColor);

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

		VBox collectionProgressBarBGColorRoot = new VBox();
		collectionProgressBarBGColorRoot.setSpacing(2);
		collectionProgressBarBGColorRoot.getChildren().addAll(collectionProgressBarBGColorLabel, collectionProgressBarBGColor);

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

		VBox collectionNormalVolProgressTextColorRoot = new VBox();
		collectionNormalVolProgressTextColorRoot.setSpacing(2);
		collectionNormalVolProgressTextColorRoot.getChildren().addAll(collectionNormalVolProgressTextColorLabel, collectionNormalVolProgressTextColor);

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

		VBox collectionHoverVolProgressTextColorRoot = new VBox();
		collectionHoverVolProgressTextColorRoot.setSpacing(2);
		collectionHoverVolProgressTextColorRoot.getChildren().addAll(collectionHoverVolProgressTextColorLabel, collectionHoverVolProgressTextColor);

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

		VBox collectionUserNotesBGColorRoot = new VBox();
		collectionUserNotesBGColorRoot.setSpacing(2);
		collectionUserNotesBGColorRoot.getChildren().addAll(collectionUserNotesBGColorLabel, collectionUserNotesBGColor);

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

		VBox collectionUserNotesBorderColorRoot = new VBox();
		collectionUserNotesBorderColorRoot.setSpacing(2);
		collectionUserNotesBorderColorRoot.getChildren().addAll(collectionUserNotesBorderColorLabel, collectionUserNotesBorderColor);

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

		VBox collectionUserNotesTextColorRoot = new VBox();
		collectionUserNotesTextColorRoot.setSpacing(2);
		collectionUserNotesTextColorRoot.getChildren().addAll(collectionUserNotesTextColorLabel, collectionUserNotesTextColor);

		FlowPane collectionThemePane = new FlowPane();
		collectionThemePane.setId("ThemeSettingsBox");
		collectionThemePane.getChildren().addAll(collectionBGColorRoot, collectionLinkNormalBGColorRoot, collectionLinkHoverBGColorRoot, collectionLinkNormalTextColorRoot, collectionLinkHoverTextColorRoot, collectionMainCardBGColorRoot, collectionTitleColorRoot, collectionSubHeaderColorRoot, collectionDescColorRoot, collectionCardBottomBGColorRoot, collectionIconButtonColorRoot, collectionNormalIconColorRoot, collectionHoverIconColorRoot, collectionProgressBarColorRoot, collectionProgressBarBorderColorRoot, collectionProgressBarBGColorRoot, collectionNormalVolProgressTextColorRoot, collectionHoverVolProgressTextColorRoot, collectionUserNotesBGColorRoot, collectionUserNotesBorderColorRoot,collectionUserNotesTextColorRoot);

		Label collectionLabel = new Label("Collection Theme");
		collectionLabel.setLabelFor(collectionThemePane);
		collectionLabel.setId("SettingsLabelStyling");

		TextField enterThemeName = new TextField("Theme Name");
		enterThemeName.setId("MenuTextField");
		enterThemeName.textProperty().addListener((obs, oldText, newText) -> finalNewTheme.setThemeName(newText));

		ComboBox<String> userCurrentTheme = new ComboBox<>(usersSavedThemes);
		userCurrentTheme.setPromptText(mainTheme.getThemeName());
		userCurrentTheme.setOnAction((event) -> {
			mainTheme = user.setNewMainTheme(userCurrentTheme.getValue());
			collectionMasterCSS = drawTheme(mainTheme);
			menuSetup(content, primaryStage, mainScene);
			collectionSetup(primaryStage);
			primaryStage.setScene(mainScene);
		});

		Button saveNewThemeButton = new Button("Save");
		saveNewThemeButton.setId("MenuButton");
		saveNewThemeButton.setOnMouseClicked(event -> {
			user.addNewTheme(finalNewTheme);
			userCurrentTheme.getItems().add(finalNewTheme.getThemeName());
			userCurrentTheme.setValue(finalNewTheme.getThemeName());
		});

		HBox newThemeRoot = new HBox();
		newThemeRoot.setAlignment(Pos.CENTER);
		newThemeRoot.setSpacing(5);
		newThemeRoot.getChildren().addAll(enterThemeName, saveNewThemeButton);

		HBox themeChangeRoot = new HBox();
		themeChangeRoot.setAlignment(Pos.CENTER);
		themeChangeRoot.setSpacing(15);
		themeChangeRoot.getChildren().addAll(newThemeRoot, userCurrentTheme);

		Label themeChangeRootLabel = new Label("Change or Save Theme");
		themeChangeRootLabel.setLabelFor(themeChangeRoot);
		themeChangeRootLabel.setId("SettingsLabelStyling");

		VBox themeSettingRoot = new VBox();
		themeSettingRoot.setId("ThemeSettingsPane");
		themeSettingRoot.setStyle(collectionMasterCSS);
		themeSettingRoot.getChildren().addAll(menuLabel, menuThemeChangePane, collectionLabel, collectionThemePane, themeChangeRootLabel, themeChangeRoot);

		Scene collectionSettingsScene = new Scene(themeSettingRoot);
		collectionSettingsScene.getStylesheets().addAll("MenuCSS.css");

		Stage collectionSettingsStage = new Stage();
		collectionSettingsStage.setHeight(850);
		collectionSettingsStage.setWidth(880);
		collectionSettingsStage.setTitle("TsundOku Theme Settings");
		collectionSettingsStage.getIcons().add(new Image("File:src/main/resources/bookshelf.png"));
		collectionSettingsStage.setOnCloseRequest(event -> {
			menuSetup(content, primaryStage, mainScene);
			collectionSetup(primaryStage);
			primaryStage.setScene(mainScene);
		});
		collectionSettingsStage.setScene(collectionSettingsScene);

		return collectionSettingsStage;
	}

	private void menuSetup(BorderPane content, Stage primaryStage, Scene mainScene){
		addNewSeriesWindow = createNewSeriesWindow(primaryStage);
		HBox menuBar = new HBox();
		menuBar.setPrefHeight(NAV_HEIGHT);
		menuBar.setId("MenuBar");
		menuBar.setStyle(collectionMasterCSS);

		Text userName = new Text(user.getUserName());
		userName.setId("MenuText");

		FontIcon userSettingsIcon = new FontIcon(BootstrapIcons.PERSON_CIRCLE);
		userSettingsIcon.setIconSize(20);
		userSettingsIcon.setId("CollectionIcon");

		Button userSettingsButton = new Button();
		userSettingsButton.setPrefWidth(67.5);
		userSettingsButton.setGraphic(userSettingsIcon);
		userSettingsButton.setId("MenuButton");

		FontIcon themeSettingsIcon = new FontIcon(BootstrapIcons.PALETTE2);
		themeSettingsIcon.setIconSize(20);
		themeSettingsIcon.setId("CollectionIcon");

		Button themeSettingsButton = new Button();
		themeSettingsButton.setPrefWidth(67.5);
		themeSettingsButton.setId("MenuButton");
		themeSettingsButton.setGraphic(themeSettingsIcon);
		themeSettingsButton.setOnMouseClicked(event -> settingsWindow.show());

		HBox settingsRoot = new HBox();
		settingsRoot.setPrefWidth(135);
		settingsRoot.setSpacing(2);
		settingsRoot.getChildren().addAll(userSettingsButton, themeSettingsButton);

		VBox userNameAndSettingsButtonLayout = new VBox();
		userNameAndSettingsButtonLayout.setId("UserNameAndSettingsButtonLayout");
		userNameAndSettingsButtonLayout.getChildren().addAll(userName, settingsRoot);

		Label searchLabel = new Label("Search Collection");
		searchLabel.setPrefWidth(203);
		searchLabel.setId("MenuLabel");

		TextField titleSearch = new TextField();
		titleSearch.setId("MenuTextField");
		titleSearch.textProperty().addListener((obs, oldText, newText) -> {
			filteredUserCollection = userCollection.parallelStream().filter(series -> containsIgnoreCase(series.getRomajiTitle(), newText) | containsIgnoreCase(series.getEnglishTitle(), newText) | containsIgnoreCase(series.getNativeTitle(), newText) | containsIgnoreCase(series.getRomajiStaff(), newText) | containsIgnoreCase(series.getNativeStaff(), newText) | containsIgnoreCase(series.getPublisher(), newText) | containsIgnoreCase(series.getBookType(), newText)).collect(Collectors.toList());
			collectionSetup(primaryStage);
			updateCollectionNumbers();
			primaryStage.setScene(mainScene);
		});
		titleSearch.setCache(true);
		titleSearch.setCacheHint(CacheHint.SPEED);

		GridPane searchLayout = new GridPane();
		searchLayout.setAlignment(Pos.CENTER);
		searchLayout.add(searchLabel, 0, 0);
		searchLayout.add(titleSearch, 0 ,1);

		Integer userVolumes = user.getTotalVolumes();

		totalVolDisplay = new Text("Collected\n" + userVolumes + " Volumes");
		totalVolDisplay.setId("MenuText");
		totalVolDisplay.setCache(true);
		totalVolDisplay.setCacheHint(CacheHint.DEFAULT);

		totalToCollect = new Text("Need To Collect\n" + (maxVolumesInCollection - userVolumes) + " Volumes");
		totalToCollect.setId("MenuText");
		totalToCollect.setCache(true);
		totalToCollect.setCacheHint(CacheHint.DEFAULT);

		ToggleButton addNewSeriesButton = new ToggleButton("Add New Series");
		addNewSeriesButton.setOnMouseClicked((MouseEvent event) -> addNewSeriesWindow.show());
		addNewSeriesButton.setId("MenuButton");

		ComboBox<String> languageSelect = new ComboBox<>(LANGUAGE_OPTIONS);
		languageSelect.setPrefWidth(135);
		languageSelect.setPromptText("Romaji");
		languageSelect.setOnAction((event) -> {
			switch(languageSelect.getValue()){
				case "English":
					user.setCurLanguage('E');
					language = 'E';
					break;
				case "日本語":
					user.setCurLanguage('N');
					language = 'N';
					break;
				case "Romaji":
				default:
					user.setCurLanguage('R');
					language = 'R';
					break;
			}
			collectionSetup(primaryStage);
			primaryStage.setScene(mainScene);
		});
		languageSelect.setCache(true);
		languageSelect.setCacheHint(CacheHint.DEFAULT);

		VBox addSeriesAndLanguageLayout = new VBox();
		addSeriesAndLanguageLayout.setAlignment(Pos.CENTER);
		addSeriesAndLanguageLayout.setSpacing(5);
		addSeriesAndLanguageLayout.getChildren().addAll(addNewSeriesButton, languageSelect);

		menuBar.getChildren().addAll(userNameAndSettingsButtonLayout, totalVolDisplay, searchLayout, totalToCollect, addSeriesAndLanguageLayout);
		content.setTop(menuBar);
	}

	private void storeUserData(){
		try {
			ObjectOutputStream outputNewSeries = new ObjectOutputStream(new FileOutputStream("src/main/resources/UsersData.bin"));
			user.setCollection(userCollection);
			outputNewSeries.writeObject(user);
			outputNewSeries.flush();
			outputNewSeries.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void getUsersData(){
		File collectionFile = new File("src/main/resources/UsersData.bin");
		if (!collectionFile.exists()) {
			user = new Collector("Prem", 'R', TsundOkuTheme.DEFAULT_THEME, new HashMap<>(), new ArrayList<>());
			user.addNewTheme(TsundOkuTheme.DEFAULT_THEME);
			storeUserData();
		}
		try {
			ObjectInputStream getUserObject = new ObjectInputStream(new FileInputStream("src/main/resources/UsersData.bin"));
			user = (Collector) getUserObject.readObject();
			userCollection = user.getCollection() == null ? new ArrayList<>() : user.getCollection();
			mainTheme = user.getMainTheme();
			getUserObject.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
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

	private Stage createNewSeriesWindow(Stage primaryStage){
		AtomicReference<String> bookType = new AtomicReference<>("");

		TextField titleEnter = new TextField();
		titleEnter.setId("MenuTextField");
		titleEnter.setPrefWidth(250);
		titleEnter.relocate(20, 32);

		Label inputTitleLabel = new Label("Enter Title (Copy Title From AniList)");
		inputTitleLabel.relocate(20, 12);
		inputTitleLabel.setId("MenuLabel");
		inputTitleLabel.setLabelFor(titleEnter);

		VBox inputTitleRoot = new VBox();
		inputTitleRoot.setId("SettingsLabel");
		inputTitleRoot.getChildren().addAll(inputTitleLabel, titleEnter);

		TextField publisherEnter = new TextField();
		publisherEnter.setId("MenuTextField");
		publisherEnter.setPrefWidth(250);
		publisherEnter.relocate(20, 95);

		Label inputPublisherLabel = new Label("Enter Publisher");
		inputPublisherLabel.relocate(20, 75);
		inputPublisherLabel.setId("MenuLabel");
		inputPublisherLabel.setLabelFor(publisherEnter);

		VBox inputPublisherRoot = new VBox();
		inputPublisherRoot.setId("SettingsLabel");
		inputPublisherRoot.getChildren().addAll(inputPublisherLabel, publisherEnter);

		ToggleButton mangaButton = new ToggleButton("Manga");
		ToggleButton lightNovelButton = new ToggleButton("Novel");

		mangaButton.setId("MenuButton");
		mangaButton.setPrefSize(100, 10);
		mangaButton.setOnMouseClicked((MouseEvent event) -> {
			bookType.set("Manga");
			mangaButton.setDisable(true);
			lightNovelButton.setDisable(false);
		});

		lightNovelButton.setId("MenuButton");
		lightNovelButton.setPrefSize(100, 10);
		lightNovelButton.setOnMouseClicked((MouseEvent event) -> {
			bookType.set("Novel");
			mangaButton.setDisable(false);
			lightNovelButton.setDisable(true);
		});

		UnaryOperator<TextFormatter.Change> filter = change -> {
			String text = change.getText();
			if (text.matches("[0-9]")) { return change; }
			return null;
		};

		Label bookTypeLabel = new Label("Select Book Type");
		bookTypeLabel.setId("MenuLabel");

		HBox bookTypePane = new HBox();
		bookTypePane.relocate(40, 145);
		bookTypePane.setSpacing(10);
		bookTypePane.setAlignment(Pos.CENTER);
		bookTypePane.getChildren().addAll(mangaButton, lightNovelButton);

		VBox bookTypeRoot = new VBox();
		bookTypeRoot.setId("SettingsLabel");
		bookTypeRoot.setAlignment(Pos.CENTER);
		bookTypeRoot.getChildren().addAll(bookTypeLabel, bookTypePane);

		TextField curVolumes = new TextField();
		curVolumes.setPrefWidth(50);
		curVolumes.setId("MenuTextField");
		curVolumes.setTextFormatter(new TextFormatter<>(filter));

		TextField maxVolumes = new TextField();
		maxVolumes.setPrefWidth(50);
		maxVolumes.setId("MenuTextField");
		maxVolumes.setTextFormatter(new TextFormatter<>(filter));

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
		submitButton.relocate(115, 255);
		submitButton.setId("MenuButton");
		submitButton.setOnMouseClicked(event -> {
			userCollection.add(new Series().CreateNewSeries(titleEnter.getText(), publisherEnter.getText(), bookType.get(), Integer.parseInt(curVolumes.getText()), Integer.parseInt(maxVolumes.getText())));
			collectionSetup(primaryStage);
			updateCollectionNumbers();
			primaryStage.setScene(mainScene);
		});

		Button closeButton = new Button("X");
		closeButton.setId("MenuButton");
		closeButton.setOnMouseClicked(event -> addNewSeriesWindow.close());

		VBox newSeriesPane = new VBox();
		newSeriesPane.setId("NewSeriesPane");
		newSeriesPane.setStyle(collectionMasterCSS);
		newSeriesPane.setOnMousePressed(event -> {
			xOffset = addNewSeriesWindow.getX() - event.getScreenX();
			yOffset = addNewSeriesWindow.getY() - event.getScreenY();
		});
		newSeriesPane.setOnMouseDragged(event -> {
			addNewSeriesWindow.setX(event.getScreenX() + xOffset);
			addNewSeriesWindow.setY(event.getScreenY() + yOffset);
		});
		newSeriesPane.setCache(true);
		newSeriesPane.setCacheHint(CacheHint.SPEED);

		newSeriesPane.getChildren().addAll(inputTitleRoot, inputPublisherRoot, bookTypeRoot, volProgressRoot, submitButton, closeButton);

		Group root = new Group();
		Scene newSeriesScene = new Scene(root);
		newSeriesScene.getStylesheets().addAll("MenuCSS.css");
		root.getChildren().add(newSeriesPane);

		addNewSeriesWindow.initStyle(StageStyle.UNDECORATED);
		addNewSeriesWindow.setHeight(447);
		addNewSeriesWindow.setWidth(418);
		addNewSeriesWindow.setScene(newSeriesScene);

		return addNewSeriesWindow;
	}

	private void collectionSetup(Stage primaryStage){
		userCollection = userCollection.stream().sorted().collect(Collectors.toList());
		filteredUserCollection = userCollection;
		totalVolumesCollected = 0;
		maxVolumesInCollection = 0;

		FlowPane collection = new FlowPane();
		collection.setId("Collection");
		collection.setStyle(collectionMasterCSS);
		collection.setCache(true);
		collection.setCacheHint(CacheHint.DEFAULT);
		settingsWindow = setupCollectionSettingsWindow(primaryStage, collection);

		ScrollPane collectionScroll = new ScrollPane();
		collectionScroll.setId("CollectionScroll");
		collectionScroll.setCache(true);
		collectionScroll.setCacheHint(CacheHint.DEFAULT);

		for (Series series : filteredUserCollection) {
			Pane seriesCard = new Pane();
			seriesCard.setId("SeriesCard");
			seriesCard.setMinSize(SERIES_CARD_WIDTH, SERIES_CARD_HEIGHT);
			seriesCard.getChildren().addAll(leftSideCardSetup(series), rightSideCardSetup(series, language, primaryStage));
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

	private Pane rightSideCardSetup(Series series, char language, Stage primaryStage){
		Integer curVolumes = series.getCurVolumes();
		Integer maxVolumes = series.getMaxVolumes();

		Text publisher = new Text(series.getPublisher() + "\n");
		publisher.setId("Publisher");

		Text seriesTitle = new Text();
		seriesTitle.setId("SeriesTitle");

		Text mangaka = new Text();
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

		Text desc = new Text(series.getSeriesDesc());
		desc.setId("SeriesDescriptionText");

		TextFlow descWrap = new TextFlow();
		descWrap.setId("SeriesDescriptionWrap");
		descWrap.getChildren().add(desc);

		ScrollPane descScroll = new ScrollPane();
		descScroll.setId("SeriesDescriptionScroll");
		descScroll.setStyle("-fx-background-color: transparent;");
		descScroll.setContent(descWrap);

		TextFlow topTextWrap = new TextFlow();
		topTextWrap.setLineSpacing(-2.5);
		topTextWrap.setTextAlignment(TextAlignment.LEFT);
		topTextWrap.getChildren().addAll(publisher, seriesTitle, mangaka);

		double volAmount = (double) curVolumes / maxVolumes;
		DoubleProperty volUpdate = new SimpleDoubleProperty();
		volUpdate.set(volAmount);

		String progTxt = curVolumes + "/" + maxVolumes;
		Label progressTxt = new Label(progTxt);
		progressTxt.setId("VolProgressTxt");
		progressTxt.setPrefWidth(RIGHT_SIDE_CARD_WIDTH - (RIGHT_SIDE_CARD_WIDTH - 70));
		progressTxt.setCache(true);
		progressTxt.setCacheHint(CacheHint.SPEED);

		Button decrementButton = new Button("-");
		Button incrementButton = new Button("+");

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

		ProgressBar volProgressBar = new ProgressBar();
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

		FontIcon seriesSettingIcon = new FontIcon(BootstrapIcons.JOURNAL_TEXT);
		seriesSettingIcon.setIconSize(25);
		seriesSettingIcon.setId("CollectionIcon");

		Button seriesCardSettingsButton = new Button();
		seriesCardSettingsButton.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 310, BOTTOM_CARD_HEIGHT);
		seriesCardSettingsButton.setGraphic(seriesSettingIcon);
		seriesCardSettingsButton.setId("CollectionIconButton");

		FontIcon backToSeriesDataIcon = new FontIcon(BootstrapIcons.CARD_HEADING);
		backToSeriesDataIcon.setId("CollectionIcon");
		backToSeriesDataIcon.setIconSize(25);

		Button backToSeriesCardDataButton = new Button();
		backToSeriesCardDataButton.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 310, BOTTOM_CARD_HEIGHT);
		backToSeriesCardDataButton.setGraphic(backToSeriesDataIcon);
		backToSeriesCardDataButton.setId("CollectionIconButton");

		BorderPane rightSideBottomPane = new BorderPane();
		rightSideBottomPane.setPrefSize(RIGHT_SIDE_CARD_WIDTH, BOTTOM_CARD_HEIGHT);
		rightSideBottomPane.setId("SeriesCardBottomPane");
		rightSideBottomPane.setLeft(seriesCardSettingsButton);
		rightSideBottomPane.setCenter(volProgressBar);
		rightSideBottomPane.setRight(volProgress);

		BorderPane rightSideOfSeriesCard = new BorderPane();
		rightSideOfSeriesCard.setId("RightSideCard");
		rightSideOfSeriesCard.setPrefSize(RIGHT_SIDE_CARD_WIDTH, SERIES_CARD_HEIGHT - BOTTOM_CARD_HEIGHT);
		rightSideOfSeriesCard.setLayoutX(LEFT_SIDE_CARD_WIDTH);
		rightSideOfSeriesCard.setTop(rightSideTopPane);
		rightSideOfSeriesCard.setBottom(rightSideBottomPane);

		seriesCardSettingsButton.setOnMouseClicked((MouseEvent event) -> {
			rightSideOfSeriesCard.setTop(seriesCardSettingsPane(series, primaryStage));
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
			userCollection.removeIf(delSeries -> delSeries.getRomajiTitle().equals(series.getRomajiTitle()));
			collectionSetup(primaryStage);
			updateCollectionNumbers();
			primaryStage.setScene(mainScene);
		});

		UnaryOperator<TextFormatter.Change> filter = change -> {
			String text = change.getText();
			if (text.matches("[0-9]")) { return change; }
			return null;
		};

		TextField curVolChange = new TextField();
		curVolChange.setId("CollectionTextField");
		curVolChange.setTextFormatter(new TextFormatter<>(filter));

		TextField maxVolChange = new TextField();
		maxVolChange.setId("CollectionTextField");
		maxVolChange.setTextFormatter(new TextFormatter<>(filter));

		FontIcon changeVolButtonIcon = new FontIcon(BootstrapIcons.ARROW_REPEAT);
		changeVolButtonIcon.setId("CollectionIcon");
		changeVolButtonIcon.setIconSize(30);

		Button changeVolCountButton = new Button();
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
			updateCollectionNumbers();
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

	private void updateCollectionNumbers(){
		Integer userVolumes = user.getTotalVolumes();
		totalVolDisplay.setText("Collected\n" + userVolumes + " Volumes");
		totalToCollect.setText("Need To Collect\n" + (maxVolumesInCollection - userVolumes) + " Volumes");
	}
}