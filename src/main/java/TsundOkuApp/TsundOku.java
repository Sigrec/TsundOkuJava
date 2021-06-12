package TsundOkuApp;

import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

public class TsundOku extends Application {
    private static final int SERIES_CARD_WIDTH = 515;
    private static final int SERIES_CARD_HEIGHT = 245;
    private static final int LEFT_SIDE_CARD_WIDTH = 165;
    private static final int RIGHT_SIDE_CARD_WIDTH = SERIES_CARD_WIDTH - LEFT_SIDE_CARD_WIDTH;
    private static final double NAV_HEIGHT = 100;
    private static final double BOTTOM_CARD_HEIGHT = 40;
    private static final int MAX_SERIES_VOLUME_AMOUNT = 999;
    private static final String APP_FONT = "Segoe UI";
    private static final ObservableList<String> LANGUAGE_OPTIONS = FXCollections.observableArrayList("Romaji", "English", "日本語");
    private static final double WINDOW_HEIGHT = Screen.getPrimary().getBounds().getHeight();
    private static final double WINDOW_WIDTH = Screen.getPrimary().getBounds().getWidth();
    private final Stage newSeriesWindow = new Stage();

    private static String mainCSS_Styling =
            "-fx-normal-background-color: rgb(44, 45, 66, 0.6);" +
            "-fx-hover-background-color: rgb(223, 213, 158, 0.8);" +
            "-fx-normal-text-color: rgb(223,213,158);" +
            "-fx-hover-text-color: rgb(44, 45, 66);" +
            "-fx-normal-border-color: rgb(223,213,158);" +
            "-fx-hover-border-color: rgb(44, 45, 66);";
    private static double xOffset = 0;
    private static double yOffset = 0;

    //Users main data
    private Integer totalVolumesCollected = 0, maxVolumesInCollection = 0;
    private List<Series> userCollection = FXCollections.observableArrayList();
    private List<Series> filteredUserCollection;
    private Collector user;
    private TsundOkuTheme mainTheme = new TsundOkuTheme();
    private BorderPane content;
    private Scene mainScene;
    private char language = 'R';
    public static final TsundOkuTheme DEFAULT_THEME = new TsundOkuTheme("rgb(44,45,66); ", "rgb(223,213,158); ", "rgb(223,213,158); ", "rgba(18,23,29,0.6); ", "rgba(223,213,158,0.70); ", "rgb(223,213,158); ", "rgb(18,23,29); ", "rgb(223,213,158); ", "rgb(18,23,29); ", "rgb(18,23,29); ", "rgb(223,213,158); ", "rgb(44,45,66); ", "rgba(32,35,45,0.95); ", "rgba(223,213,158,0.95); ", "rgb(32,35,45); ", "rgb(223,213,158); ", "rgb(223,213,158); ", "rgba(236,236,236,0.9); ", "rgb(44,45,66); ", "rgb(223,213,158); ", "rgb(44,45,66); ", "rgb(223,213,158); ", "rgb(223,213,158); ", "rgb(18,23,29); ", "rgb(223,213,158); ", "rgb(44,45,66); ");

    //Menu Bar Components
    private HBox menuBar;
    private Text userName, totalVolDisplay, totalToCollect;
    private Button settingsButton;
    private Label searchLabel;
    private TextField titleSearch;
    private ToggleButton addNewSeriesButton;
    private ComboBox<String> languageSelect;
    private String collectionMasterCSS;

    @Override
    public void start(Stage primaryStage){
        getUsersData();
        collectionMasterCSS = drawTheme(user.getMainTheme());
        Application.setUserAgentStylesheet(STYLESHEET_MODENA);

        content = new BorderPane();
        content.setCache(true);
        content.setCacheHint(CacheHint.DEFAULT);

        mainScene = new Scene(content);
        mainScene.getStylesheets().add("Master.css");

        primaryStage.setMinWidth(SERIES_CARD_WIDTH + 520);
        primaryStage.setMinHeight(SERIES_CARD_HEIGHT + NAV_HEIGHT + 75);
        primaryStage.setMaxWidth(WINDOW_WIDTH);
        primaryStage.setMaxHeight(WINDOW_HEIGHT);
        primaryStage.setTitle("TsundOku");
        primaryStage.setResizable(true);
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            storeUserData();
            newSeriesWindow.close();
        });
        //primaryStage.setFullScreen(true);
        //primaryStage.setMaximized(true);
        primaryStage.initStyle(StageStyle.UNIFIED);
        primaryStage.setScene(mainScene);

        collectionSetup(primaryStage);
        createNewSeriesWindow(primaryStage);
        menuSetup(content, primaryStage, mainScene);

        //setupCollectionSettingsWindow(primaryStage);
        primaryStage.show();
    }

    public String drawTheme(TsundOkuTheme newTheme){
        return "-fx-menu-bg-color: " + newTheme.getMenuBGColor() +
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
                "-fx-collection-normal-icon-color: " + newTheme.getCollectionNormalIconColor() +
                "-fx-collection-hover-icon-color: " + newTheme.getCollectionHoverIconColor() +
                "-fx-collection-progress-bar-border-color: " + newTheme.getCollectionProgressBarBorderColor() +
                "-fx-collection-progress-bar-color: " + newTheme.getCollectionProgressBarColor() +
                "-fx-collection-progress-bar-bg-color: " + newTheme.getCollectionProgressBarBGColor() +
                "-fx-collection-normal-volprogress-text-color: " + newTheme.getCollectionNormalVolProgressTextColor() +
                "-fx-collection-hover-volprogress-text-color: " + newTheme.getCollectionHoverVolProgressTextColor();
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

    private void setupCollectionSettingsWindow(Stage primaryStage){
        TsundOkuTheme newTheme = null;
        try {
            newTheme = (TsundOkuTheme) mainTheme.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        TsundOkuTheme finalNewTheme = newTheme;
        AtomicBoolean newThemeSaved = new AtomicBoolean(false);

        ColorPicker menuBGColor = new ColorPicker();
        menuBGColor.setPrefWidth(181);
        menuBGColor.setValue(convertStringToColor(mainTheme.getMenuBGColor()));
        menuBGColor.setOnAction(event -> {
            Color newMenuBGColor = menuBGColor.getValue();
            finalNewTheme.setMenuBGColor(formatColorCode(newMenuBGColor));
            menuBar.setBackground(new Background(new BackgroundFill(newMenuBGColor, CornerRadii.EMPTY, Insets.EMPTY)));
        });

        Label menuBGColorLabel = new Label("Menu Background Color");
        menuBGColorLabel.setLabelFor(menuBGColor);
        menuBGColorLabel.setId("SettingsTextStyling");

        VBox menuBGColorRoot = new VBox();
        menuBGColorRoot.setSpacing(5);
        menuBGColorRoot.getChildren().addAll(menuBGColorLabel, menuBGColor);

        ColorPicker menuBottomBorderColor = new ColorPicker();
        menuBottomBorderColor.setPrefWidth(181);
        menuBottomBorderColor.setValue(convertStringToColor(mainTheme.getMenuBottomBorderColor()));
        menuBottomBorderColor.setOnAction(event -> {
            Color newBottomBorderColor = menuBottomBorderColor.getValue();
            finalNewTheme.setMenuBottomBorderColor(formatColorCode(newBottomBorderColor));
            menuBar.setBorder(new Border(new BorderStroke(newBottomBorderColor, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 2, 0))));
        });

        Label menuBottomBorderColorLabel = new Label("Divider Color");
        menuBottomBorderColorLabel.setLabelFor(menuBGColor);
        menuBottomBorderColorLabel.setId("SettingsTextStyling");

        VBox menuBottomBorderColorRoot = new VBox();
        menuBottomBorderColorRoot.setSpacing(5);
        menuBottomBorderColorRoot.getChildren().addAll(menuBottomBorderColorLabel, menuBottomBorderColor);

        ColorPicker menuTextColor = new ColorPicker();
        menuTextColor.setPrefWidth(181);
        menuTextColor.setValue(convertStringToColor(mainTheme.getMenuTextColor()));
        menuTextColor.setOnAction(event -> {
            Color newMenuTextColor = menuTextColor.getValue();
            finalNewTheme.setMenuTextColor(formatColorCode(newMenuTextColor));
            userName.setFill(newMenuTextColor);
            totalVolDisplay.setFill(newMenuTextColor);
            totalToCollect.setFill(newMenuTextColor);
            searchLabel.setTextFill(newMenuTextColor);
        });

        Label menuTextColorLabel = new Label("Menu Text Color");
        menuTextColorLabel.setLabelFor(menuBGColor);
        menuTextColorLabel.setId("SettingsTextStyling");

        VBox menuTextColorRoot = new VBox();
        menuTextColorRoot.setSpacing(5);
        menuTextColorRoot.getChildren().addAll(menuTextColorLabel, menuTextColor);

        ColorPicker menuNormalButtonBGColor = new ColorPicker();
        menuNormalButtonBGColor.setPrefWidth(181);
        menuNormalButtonBGColor.setValue(convertStringToColor(mainTheme.getMenuNormalButtonBGColor()));
        menuNormalButtonBGColor.setOnAction(event -> {
            Color newMenuButtonBGColor = menuNormalButtonBGColor.getValue();
            finalNewTheme.setMenuNormalButtonBGColor(formatColorCode(newMenuButtonBGColor));
            String newCSS = drawTheme(finalNewTheme);
            addNewSeriesButton.setStyle(newCSS);
            settingsButton.setStyle(newCSS);
            languageSelect.setStyle(newCSS);
            titleSearch.setStyle(newCSS);
        });

        Label menuNormalButtonBGColorLabel = new Label("Menu Button BG Color");
        menuNormalButtonBGColorLabel.setLabelFor(menuBGColor);
        menuNormalButtonBGColorLabel.setId("SettingsTextStyling");

        VBox menuNormalButtonBGColorRoot = new VBox();
        menuNormalButtonBGColorRoot.setSpacing(5);
        menuNormalButtonBGColorRoot.getChildren().addAll(menuNormalButtonBGColorLabel, menuNormalButtonBGColor);

        ColorPicker menuHoverButtonBGColor = new ColorPicker();
        menuHoverButtonBGColor.setPrefWidth(181);
        menuHoverButtonBGColor.setValue(convertStringToColor(mainTheme.getMenuHoverButtonBGColor()));
        menuHoverButtonBGColor.setOnAction(event -> {
            Color newMenuHoverButtonBGColor = menuHoverButtonBGColor.getValue();
            finalNewTheme.setMenuHoverButtonBGColor(formatColorCode(newMenuHoverButtonBGColor));
            String newCSS = drawTheme(finalNewTheme);
            addNewSeriesButton.setStyle(newCSS);
            settingsButton.setStyle(newCSS);
            languageSelect.setStyle(newCSS);
            titleSearch.setStyle(newCSS);
        });

        Label menuHoverButtonBGColorLabel = new Label("Menu Button BG Color (Hover)");
        menuHoverButtonBGColorLabel.setLabelFor(menuBGColor);
        menuHoverButtonBGColorLabel.setId("SettingsTextStyling");

        VBox menuHoverButtonBGColorRoot = new VBox();
        menuHoverButtonBGColorRoot.setSpacing(5);
        menuHoverButtonBGColorRoot.getChildren().addAll(menuHoverButtonBGColorLabel, menuHoverButtonBGColor);

        ColorPicker menuNormalButtonBorderColor = new ColorPicker();
        menuNormalButtonBorderColor.setPrefWidth(181);
        menuNormalButtonBorderColor.setValue(convertStringToColor(mainTheme.getMenuNormalButtonBorderColor()));
        menuNormalButtonBorderColor.setOnAction(event -> {
            Color newMenuNormalButtonBorderColor = menuNormalButtonBorderColor.getValue();
            finalNewTheme.setMenuNormalButtonBorderColor(formatColorCode(newMenuNormalButtonBorderColor));
            String newCSS = drawTheme(finalNewTheme);
            addNewSeriesButton.setStyle(newCSS);
            settingsButton.setStyle(newCSS);
            languageSelect.setStyle(newCSS);
            titleSearch.setStyle(newCSS);
        });

        Label menuNormalButtonBorderColorLabel = new Label("Menu Button Border Color");
        menuNormalButtonBorderColorLabel.setLabelFor(menuBGColor);
        menuNormalButtonBorderColorLabel.setId("SettingsTextStyling");

        VBox menuNormalButtonBorderColorRoot = new VBox();
        menuNormalButtonBorderColorRoot.setSpacing(5);
        menuNormalButtonBorderColorRoot.getChildren().addAll(menuNormalButtonBorderColorLabel, menuNormalButtonBorderColor);

        ColorPicker menuHoverButtonBorderColor = new ColorPicker();
        menuHoverButtonBorderColor.setPrefWidth(181);
        menuHoverButtonBorderColor.setValue(convertStringToColor(mainTheme.getMenuHoverButtonBorderColor()));
        menuHoverButtonBorderColor.setOnAction(event -> {
            Color newMenuHoverButtonBorderColor = menuHoverButtonBorderColor.getValue();
            finalNewTheme.setMenuHoverButtonBorderColor(formatColorCode(newMenuHoverButtonBorderColor));
            String newCSS = drawTheme(finalNewTheme);
            addNewSeriesButton.setStyle(newCSS);
            settingsButton.setStyle(newCSS);
            languageSelect.setStyle(newCSS);
            titleSearch.setStyle(newCSS);
        });

        Label menuHoverButtonBorderColorLabel = new Label("Menu Button Border Color (Hover)");
        menuHoverButtonBorderColorLabel.setLabelFor(menuBGColor);
        menuHoverButtonBorderColorLabel.setId("SettingsTextStyling");

        VBox menuHoverButtonBorderColorRoot = new VBox();
        menuHoverButtonBorderColorRoot.setSpacing(5);
        menuHoverButtonBorderColorRoot.getChildren().addAll(menuHoverButtonBorderColorLabel, menuHoverButtonBorderColor);

        ColorPicker menuNormalButtonTextColor = new ColorPicker();
        menuNormalButtonTextColor.setPrefWidth(181);
        menuNormalButtonTextColor.setValue(convertStringToColor(mainTheme.getMenuNormalButtonTextColor()));
        menuNormalButtonTextColor.setOnAction(event -> {
            System.out.println(menuHoverButtonBorderColorLabel.getWidth());
            Color newMenuNormalButtonTextColor = menuNormalButtonTextColor.getValue();
            finalNewTheme.setMenuNormalButtonTextColor(formatColorCode(newMenuNormalButtonTextColor));
            String newCSS = drawTheme(finalNewTheme);
            addNewSeriesButton.setStyle(newCSS);
            settingsButton.setStyle(newCSS);
            languageSelect.setStyle(newCSS);
            titleSearch.setStyle(newCSS);
        });

        Label menuNormalButtonTextColorLabel = new Label("Menu Button Text Color");
        menuNormalButtonTextColorLabel.setLabelFor(menuBGColor);
        menuNormalButtonTextColorLabel.setId("SettingsTextStyling");

        VBox menuNormalButtonTextColorRoot = new VBox();
        menuNormalButtonTextColorRoot.setSpacing(5);
        menuNormalButtonTextColorRoot.getChildren().addAll(menuNormalButtonTextColorLabel, menuNormalButtonTextColor);

        ColorPicker menuHoverButtonTextColor = new ColorPicker();
        menuHoverButtonTextColor.setPrefWidth(181);
        menuHoverButtonTextColor.setValue(convertStringToColor(mainTheme.getMenuHoverButtonTextColor()));
        menuHoverButtonTextColor.setOnAction(event -> {
            Color newMenuHoverButtonTextColor = menuHoverButtonTextColor.getValue();
            finalNewTheme.setMenuHoverButtonTextColor(formatColorCode(newMenuHoverButtonTextColor));
            String newCSS = drawTheme(finalNewTheme);
            addNewSeriesButton.setStyle(newCSS);
            settingsButton.setStyle(newCSS);
            languageSelect.setStyle(newCSS);
            titleSearch.setStyle(newCSS);
        });

        Label menuHoverButtonTextColorLabel = new Label("Menu Button Text Color (Hover)");
        menuHoverButtonTextColorLabel.setLabelFor(menuBGColor);
        menuHoverButtonTextColorLabel.setId("SettingsTextStyling");

        VBox menuHoverButtonTextColorRoot = new VBox();
        menuHoverButtonTextColorRoot.setSpacing(5);
        menuHoverButtonTextColorRoot.getChildren().addAll(menuHoverButtonTextColorLabel, menuHoverButtonTextColor);

        FlowPane menuThemeChangePane = new FlowPane();
        menuThemeChangePane.setId("ThemeSettingsBox");
        menuThemeChangePane.getChildren().addAll(menuBGColorRoot, menuBottomBorderColorRoot, menuTextColorRoot, menuNormalButtonBGColorRoot, menuHoverButtonBGColorRoot, menuNormalButtonBorderColorRoot, menuHoverButtonBorderColorRoot, menuNormalButtonTextColorRoot, menuHoverButtonTextColorRoot);

        Label menuLabel = new Label("Menu Bar Theme");
        menuLabel.setLabelFor(menuThemeChangePane);
        menuLabel.setId("SettingsLabelStyling");

        ColorPicker collectionBGColor = new ColorPicker();
        collectionBGColor.setPrefWidth(181);
        collectionBGColor.setValue(convertStringToColor(mainTheme.getMenuHoverButtonTextColor()));
        collectionBGColor.setOnAction(event -> {
            Color newMenuHoverButtonTextColor = collectionBGColor.getValue();
            finalNewTheme.setMenuHoverButtonTextColor(formatColorCode(newMenuHoverButtonTextColor));
            String newCSS = drawTheme(finalNewTheme);
            addNewSeriesButton.setStyle(newCSS);
            settingsButton.setStyle(newCSS);
            languageSelect.setStyle(newCSS);
            titleSearch.setStyle(newCSS);
        });

        Label collectionBGColorLabel = new Label("Collection BG");
        collectionBGColorLabel.setLabelFor(collectionBGColor);
        collectionBGColorLabel.setId("SettingsTextStyling");

        VBox collectionBGColorRoot = new VBox();
        collectionBGColorRoot.setSpacing(5);
        collectionBGColorRoot.getChildren().addAll(collectionBGColorLabel, collectionBGColor);

        FlowPane collectionThemePane = new FlowPane();
        collectionThemePane.setId("ThemeSettingsBox");
        //collectionThemePane.getChildren().addAll(menuBGColorRoot, menuBottomBorderColorRoot, menuTextColorRoot, menuNormalButtonBGColorRoot, menuHoverButtonBGColorRoot, menuNormalButtonBorderColorRoot, menuHoverButtonBorderColorRoot, menuNormalButtonTextColorRoot, menuHoverButtonTextColorRoot);

        Label collectionLabel = new Label("Collection Theme");
        collectionLabel.setLabelFor(collectionThemePane);
        collectionLabel.setId("SettingsLabelStyling");

        Button saveNewThemeButton = new Button("Save Theme");
        saveNewThemeButton.setId("MenuButton");
        saveNewThemeButton.setOnMouseClicked(event -> {
            newThemeSaved.set(true);
            user.addNewTheme(finalNewTheme);
            user.setNewMainTheme(finalNewTheme);
        });

        VBox collectionSettingRoot = new VBox();
        collectionSettingRoot.setId("ThemeSettingsPane");
        collectionSettingRoot.setStyle(collectionMasterCSS);
        collectionSettingRoot.getChildren().addAll(menuLabel, menuThemeChangePane, collectionLabel, collectionThemePane, saveNewThemeButton);

        Scene collectionSettingsScene = new Scene(collectionSettingRoot);
        collectionSettingsScene.getStylesheets().add("Master.css");

        Stage collectionSettingsStage = new Stage();
        collectionSettingsStage.setOnCloseRequest(event -> {
            if (!newThemeSaved.get()){
                collectionMasterCSS = drawTheme(mainTheme);
                menuSetup(content, primaryStage, mainScene);
                primaryStage.setScene(mainScene);
            }
        });
        collectionSettingsStage.setHeight(700);
        collectionSettingsStage.setWidth(850);
        collectionSettingsStage.setScene(collectionSettingsScene);
        collectionSettingsStage.show();
    }

    private void menuSetup(BorderPane content, Stage primaryStage, Scene mainScene){
        menuBar = new HBox();
        menuBar.setPrefHeight(NAV_HEIGHT);
        menuBar.setId("MenuBar");
        menuBar.setStyle(collectionMasterCSS);

        userName = new Text(user.getUserName());
        userName.setId("MenuText");

        settingsButton = new Button("Settings");
        settingsButton.setPrefWidth(135);
        settingsButton.setId("MenuButton");

        VBox userNameAndSettingsButtonLayout = new VBox();
        userNameAndSettingsButtonLayout.setId("UserNameAndSettingsButtonLayout");
        userNameAndSettingsButtonLayout.getChildren().addAll(userName, settingsButton);

        searchLabel = new Label("Search Collection");
        searchLabel.setPrefWidth(203);
        searchLabel.setId("MenuLabel");

        titleSearch = new TextField();
        titleSearch.setId("MenuTextField");
        titleSearch.textProperty().addListener((obs, oldText, newText) -> {
            filteredUserCollection = userCollection.parallelStream().filter(series ->
                    containsIgnoreCase(series.getRomajiTitle(), newText) |
                            containsIgnoreCase(series.getEnglishTitle(), newText) |
                            containsIgnoreCase(series.getNativeTitle(), newText) |
                            containsIgnoreCase(series.getRomajiStaff(), newText) |
                            containsIgnoreCase(series.getNativeStaff(), newText)
            ).collect(Collectors.toList());
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

        addNewSeriesButton = new ToggleButton("Add New Series");
        addNewSeriesButton.setOnMouseClicked((MouseEvent event) -> newSeriesWindow.show());
        addNewSeriesButton.setId("MenuButton");

        languageSelect = new ComboBox<>(LANGUAGE_OPTIONS);
        languageSelect.setPrefWidth(135);
        languageSelect.setPromptText("Romaji");
        languageSelect.setOnAction((event) -> {
            switch(languageSelect.getValue()){
                case "English":
                    language = 'E';
                    break;
                case "日本語":
                    language = 'N';
                    break;
                case "Romaji":
                default:
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
            user = new Collector();
            user.setUserName("Prem");
            user.setSavedThemes(new ArrayList<>());
            user.addNewTheme(DEFAULT_THEME);
            //user.setCollection(new ArrayList<Series>());
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

    private void createNewSeriesWindow(Stage primaryStage){
        AtomicReference<String> bookType = new AtomicReference<>("");
        ArrayList<String[]> newSeriesList = new ArrayList<>();

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
            newSeriesList.add(new String[]{titleEnter.getText(), publisherEnter.getText(), bookType.get(), curVolumes.getText(), maxVolumes.getText()});
           if (newSeriesList.size() == 90) { submitButton.setDisable(true); }
        });

        Button getNewSeries = new Button("Run");
        getNewSeries.setPrefSize(60, 10);
        getNewSeries.relocate(115, 295);
        getNewSeries.setId("MenuButton");
        getNewSeries.setOnMouseClicked(event -> {
            newSeriesList.forEach(series -> {
                userCollection.add(new Series().CreateNewSeries(series[0], series[1], series[2], Integer.parseInt(series[3]), Integer.parseInt(series[4])));
                System.out.println("Series Added");
            });
            filteredUserCollection = userCollection;
            collectionSetup(primaryStage);
            primaryStage.setScene(mainScene);
        });

        VBox newSeriesPane = new VBox();
        newSeriesPane.setId("NewSeriesPane");
        newSeriesPane.setStyle(collectionMasterCSS);
        newSeriesPane.setOnMousePressed(event -> {
            xOffset = newSeriesWindow.getX() - event.getScreenX();
            yOffset = newSeriesWindow.getY() - event.getScreenY();
        });
        newSeriesPane.setOnMouseDragged(event -> {
            newSeriesWindow.setX(event.getScreenX() + xOffset);
            newSeriesWindow.setY(event.getScreenY() + yOffset);
        });
        newSeriesPane.setCache(true);
        newSeriesPane.setCacheHint(CacheHint.SPEED);

        newSeriesPane.getChildren().addAll(inputTitleRoot, inputPublisherRoot, bookTypeRoot, volProgressRoot, submitButton, getNewSeries);

        Group root = new Group();
        Scene newSeriesScene = new Scene(root);
        newSeriesScene.getStylesheets().add("Master.css");
        root.getChildren().add(newSeriesPane);

        newSeriesWindow.initStyle(StageStyle.UNDECORATED);
        newSeriesWindow.setHeight(447);
        newSeriesWindow.setWidth(418);
        newSeriesWindow.setScene(newSeriesScene);
    }

    private void updateCollectionNumbers(){
        Integer userVolumes = user.getTotalVolumes();
        totalVolDisplay.setText("Collected\n" + userVolumes + " Volumes");
        totalToCollect.setText("Need To Collect\n" + (maxVolumesInCollection - userVolumes) + " Volumes");
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

        Label bookTypeAndPrintStatus = new Label(series.getBookType()+ " | " + series.getPrintStatus());
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

        BorderPane rightSideBottomPane = new BorderPane();
        rightSideBottomPane.setPrefSize(RIGHT_SIDE_CARD_WIDTH, BOTTOM_CARD_HEIGHT);
        rightSideBottomPane.setId("SeriesCardBottomPane");
        rightSideBottomPane.setStyle(collectionMasterCSS);
        rightSideBottomPane.setLeft(seriesCardSettingsButton);
        rightSideBottomPane.setCenter(volProgressBar);
        rightSideBottomPane.setRight(volProgress);

        BorderPane rightSideOfSeriesCard = new BorderPane();
        rightSideOfSeriesCard.setId("RightSideCard");
        rightSideOfSeriesCard.setStyle(collectionMasterCSS);
        rightSideOfSeriesCard.setPrefSize(RIGHT_SIDE_CARD_WIDTH, SERIES_CARD_HEIGHT - 50);
        rightSideOfSeriesCard.setMaxSize(RIGHT_SIDE_CARD_WIDTH, SERIES_CARD_HEIGHT - 50);
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
        userNotes.setStyle(collectionMasterCSS);
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

    public static void main(String[] args) { launch(args); }
}
