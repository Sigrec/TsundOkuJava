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
import javafx.scene.text.FontPosture;
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
    private List<Series> userCollection;
    private List<Series> filteredUserCollection;
    private Collector user;
    private TsundOkuTheme mainTheme = new TsundOkuTheme();
    private StringBuilder currentTheme;
    private BorderPane content;
    private Scene mainScene;
    private char language = 'R';
    public static final TsundOkuTheme DEFAULT_THEME = new TsundOkuTheme("rgb(44, 45, 66);", "rgb(223, 213, 158);", "rgb(223, 213, 158);", "rgba(44, 45, 66, 0.6);", "rgba(223, 213, 158, 0.70);", "rgb(223, 213, 158);", "rgb(18, 23, 29);", "rgb(223, 213, 158);", "rgb(44, 45, 66);");

    //Menu Bar Components
    private HBox menuBar;
    private Text userName, totalVolDisplay, totalToCollect;
    private Button settingsButton;
    private Label searchLabel;
    private TextField titleSearch;
    private ToggleButton addNewSeriesButton;
    private ComboBox<String> languageSelect;
    private String menuBarCSS;

    @Override
    public void start(Stage primaryStage){
        getUsersData();
        drawTheme(user.getMainTheme());
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
        menuSetup(content, primaryStage, mainScene);

        Group root = new Group();
        Scene newSeriesScene = new Scene(root);
        newSeriesScene.getStylesheets().add("Master.css");
        root.getChildren().add(createNewSeriesWindow(primaryStage));

        newSeriesWindow.initStyle(StageStyle.UTILITY);
        newSeriesWindow.setHeight(345);
        newSeriesWindow.setWidth(290);
        //newSeriesWindow.setResizable(false);
        newSeriesWindow.setScene(newSeriesScene);
        setupCollectionSettingsWindow(primaryStage);
        primaryStage.show();
    }

    public String drawTheme(TsundOkuTheme newTheme){
        currentTheme = new StringBuilder()
                .append("-fx-menu-bg-color: ").append(newTheme.getMenuBGColor())
                .append("-fx-menu-bottom-border-color: ").append(newTheme.getMenuBottomBorderColor())
                .append("-fx-menu-text-color: ").append(newTheme.getMenuTextColor())
                .append("-fx-normal-menu-button-bg-color: ").append(newTheme.getMenuNormalButtonBGColor())
                .append("-fx-hover-menu-button-bg-color: ").append(newTheme.getMenuHoverButtonBGColor())
                .append("-fx-normal-menu-button-border-color: ").append(newTheme.getMenuNormalButtonBorderColor())
                .append("-fx-hover-menu-button-border-color: ").append(newTheme.getMenuHoverButtonBorderColor())
                .append("-fx-normal-menu-button-text-color: ").append(newTheme.getMenuNormalButtonTextColor())
                .append("-fx-hover-menu-button-text-color: ").append(newTheme.getMenuHoverButtonTextColor());
        menuBarCSS = currentTheme.toString();
        return menuBarCSS;
    }

    private String formatColorCode(Color newColor){
        return "#" + newColor.toString().substring(2, 8) + ";";
    }

    private void setupCollectionSettingsWindow(Stage primaryStage){
        TsundOkuTheme newTheme = mainTheme;
        AtomicBoolean newThemeSaved = new AtomicBoolean(false);

        ColorPicker menuBGColor = new ColorPicker();
        menuBGColor.setStyle(mainCSS_Styling);
        menuBGColor.setOnAction(event -> {
            Color newMenuBGColor = menuBGColor.getValue();
            newTheme.setMenuBGColor(formatColorCode(newMenuBGColor));
            menuBar.setBackground(new Background(new BackgroundFill(newMenuBGColor, null, null)));
        });

        Label menuBGColorLabel = new Label("Menu Background Color");
        menuBGColorLabel.setLabelFor(menuBGColor);
        menuBGColorLabel.setId("SettingsTextStyling");
        menuBGColorLabel.setStyle(mainCSS_Styling);

        VBox menuBGColorRoot = new VBox();
        menuBGColorRoot.setSpacing(5);
        menuBGColorRoot.getChildren().addAll(menuBGColorLabel, menuBGColor);

        ColorPicker menuTextColor = new ColorPicker();
        menuTextColor.setStyle(mainCSS_Styling);
        menuTextColor.setOnAction(event -> {
            Color newMenuTextColor = menuTextColor.getValue();
            newTheme.setMenuTextColor(formatColorCode(newMenuTextColor));
            userName.setFill(newMenuTextColor);
            totalVolDisplay.setFill(newMenuTextColor);
            totalToCollect.setFill(newMenuTextColor);
            searchLabel.setTextFill(newMenuTextColor);
            mainTheme.setMenuTextColor(formatColorCode(newMenuTextColor));
            titleSearch.setStyle(drawTheme(mainTheme));
        });

        Label menuTextColorLabel = new Label("Menu Text Color");
        menuTextColorLabel.setLabelFor(menuBGColor);
        menuTextColorLabel.setId("SettingsTextStyling");
        menuTextColorLabel.setStyle(mainCSS_Styling);

        VBox menuTextColorRoot = new VBox();
        menuTextColorRoot.setSpacing(5);
        menuTextColorRoot.getChildren().addAll(menuTextColorLabel, menuTextColor);

        VBox themeChangePane = new VBox();
        themeChangePane.setSpacing(20);
        themeChangePane.setPadding(new Insets(10, 0, 0, 10));
        themeChangePane.getChildren().addAll(menuBGColorRoot, menuTextColorRoot);

        Button saveNewThemeButton = new Button("Save Theme");
        saveNewThemeButton.setId("MenuButton");
        saveNewThemeButton.setStyle(menuBarCSS);
        saveNewThemeButton.setOnMouseClicked(event -> {
            newThemeSaved.set(true);
            user.addNewTheme(newTheme);
            user.setNewMainTheme(newTheme);
        });

        BorderPane collectionSettingRoot = new BorderPane();
        collectionSettingRoot.setStyle("-fx-background-color: rgb(18, 23, 29);");
        collectionSettingRoot.setTop(themeChangePane);
        collectionSettingRoot.setCenter(saveNewThemeButton);

        Scene collectionSettingsScene = new Scene(collectionSettingRoot);
        collectionSettingsScene.getStylesheets().add("Master.css");

        //user.addNewTheme(newTheme);
        Stage collectionSettingsStage = new Stage();
        collectionSettingsStage.setOnCloseRequest(event -> {
            System.out.println(newThemeSaved.get());

            if (!newThemeSaved.get()){
                drawTheme(DEFAULT_THEME);
                menuSetup(content, primaryStage, mainScene);
            }
        });
        collectionSettingsStage.setHeight(700);
        collectionSettingsStage.setWidth(500);
        collectionSettingsStage.setScene(collectionSettingsScene);
        collectionSettingsStage.show();
    }

    private void menuSetup(BorderPane content, Stage primaryStage, Scene mainScene){
        menuBar = new HBox();
        menuBar.setPrefHeight(NAV_HEIGHT);
        menuBar.setId("MenuBar");
        menuBar.setStyle(menuBarCSS);

        userName = new Text(user.getUserName());
        userName.setId("MenuTextStyling");
        userName.setStyle(menuBarCSS);

        settingsButton = new Button("Settings");
        settingsButton.setPrefWidth(135);
        settingsButton.setId("MenuButton");
        settingsButton.setStyle(menuBarCSS);

        VBox userNameAndSettingsButtonLayout = new VBox();
        userNameAndSettingsButtonLayout.setId("UserNameAndSettingsButtonLayout");
        userNameAndSettingsButtonLayout.getChildren().addAll(userName, settingsButton);

        searchLabel = new Label("Search Collection");
        searchLabel.setPrefWidth(203);
        searchLabel.setId("MenuLabelStyling");
        searchLabel.setStyle(menuBarCSS);

        titleSearch = new TextField();
        titleSearch.setId("MenuTextField");
        titleSearch.setStyle(menuBarCSS);
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
        totalVolDisplay.setId("MenuTextStyling");
        totalVolDisplay.setStyle(menuBarCSS);
        totalVolDisplay.setCache(true);
        totalVolDisplay.setCacheHint(CacheHint.DEFAULT);

        totalToCollect = new Text("Need To Collect\n" + (maxVolumesInCollection - userVolumes) + " Volumes");
        totalToCollect.setId("MenuTextStyling");
        totalToCollect.setStyle(menuBarCSS);
        totalToCollect.setCache(true);
        totalToCollect.setCacheHint(CacheHint.DEFAULT);

        addNewSeriesButton = new ToggleButton("Add New Series");
        addNewSeriesButton.setOnMouseClicked((MouseEvent event) -> newSeriesWindow.show());
        addNewSeriesButton.setId("MenuButton");
        addNewSeriesButton.setStyle(menuBarCSS);

        languageSelect = new ComboBox<>(LANGUAGE_OPTIONS);
        languageSelect.setPrefWidth(135);
        languageSelect.setPromptText("Romaji");
        languageSelect.setStyle(menuBarCSS);
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

    private Pane createNewSeriesWindow(Stage primaryStage){
        AtomicReference<String> bookType = new AtomicReference<>("");
        ArrayList<String[]> newSeriesList = new ArrayList<>();

        TextField titleEnter = new TextField();
        titleEnter.setId("TextFieldStyling");
        titleEnter.setStyle(mainCSS_Styling);
        titleEnter.setPrefWidth(250);
        titleEnter.setFont(Font.font(APP_FONT, FontWeight.SEMI_BOLD, 15));
        titleEnter.setCache(true);
        titleEnter.relocate(20, 32);
        titleEnter.setCache(true);
        titleEnter.setCacheHint(CacheHint.SPEED);

        Label titleLabel = new Label("Enter Title (Copy Title From AniList)");
        titleLabel.relocate(20, 12);
        titleLabel.setTextFill(Color.rgb(223,213,158));
        titleLabel.setFont(Font.font(APP_FONT, FontWeight.BOLD, 15));
        titleLabel.setLabelFor(titleEnter);

        TextField publisherEnter = new TextField();
        publisherEnter.setId("TextFieldStyling");
        publisherEnter.setStyle(mainCSS_Styling);
        publisherEnter.setPrefWidth(250);
        publisherEnter.setFont(Font.font(APP_FONT, FontWeight.SEMI_BOLD, 15));
        publisherEnter.setCache(true);
        publisherEnter.relocate(20, 95);
        publisherEnter.setCache(true);
        publisherEnter.setCacheHint(CacheHint.SPEED);

        Label publisherLabel = new Label("Enter Publisher");
        publisherLabel.relocate(20, 75);
        publisherLabel.setTextFill(Color.rgb(223,213,158));
        publisherLabel.setFont(Font.font(APP_FONT, FontWeight.BOLD, 15));
        publisherLabel.setLabelFor(publisherEnter);

        Tooltip enabledToolTip = new Tooltip("Enabled");
        Tooltip disabledToolTip = new Tooltip("Disabled");

        ToggleButton mangaButton = new ToggleButton("Manga");
        ToggleButton lightNovelButton = new ToggleButton("Novel");

        mangaButton.setId("BookTypeButton");
        mangaButton.setPrefSize(100, 10);
        mangaButton.setFont(Font.font(APP_FONT, FontWeight.BOLD, 15));
        mangaButton.setStyle(mainCSS_Styling);
        mangaButton.setTextFill(Color.rgb(223,213,158));
        mangaButton.setOnMouseClicked((MouseEvent event) -> {
            lightNovelButton.setTooltip(disabledToolTip);
            mangaButton.setTooltip(enabledToolTip);
            bookType.set("Manga");
            mangaButton.setDisable(true);
            lightNovelButton.setDisable(false);
        });
        mangaButton.setCache(true);
        mangaButton.setCacheHint(CacheHint.SPEED);

        lightNovelButton.setId("BookTypeButton");
        lightNovelButton.setPrefSize(100, 10);
        lightNovelButton.setFont(Font.font(APP_FONT, FontWeight.BOLD, 15));
        lightNovelButton.setStyle(mainCSS_Styling);
        lightNovelButton.setTextFill(Color.rgb(223,213,158));
        lightNovelButton.setOnMouseClicked((MouseEvent event) -> {
            mangaButton.setTooltip(disabledToolTip);
            lightNovelButton.setTooltip(enabledToolTip);
            bookType.set("Novel");
            mangaButton.setDisable(false);
            lightNovelButton.setDisable(true);
        });
        lightNovelButton.setCache(true);
        lightNovelButton.setCacheHint(CacheHint.SPEED);


        HBox bookTypeRoot = new HBox();
        bookTypeRoot.relocate(40, 145);
        bookTypeRoot.setSpacing(10);
        bookTypeRoot.getChildren().addAll(mangaButton, lightNovelButton);

        Spinner<Integer> curVolSpinner = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 999, 0, 1));
        Spinner<Integer> maxVolSpinner = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 1, 1));

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("\\\\d+")) {
                return change;
            }
            return null;
        };

        curVolSpinner.setPrefSize(100, 10);
        curVolSpinner.getEditor().setTextFormatter(new TextFormatter<>(filter));
        curVolSpinner.setEditable(true);
        curVolSpinner.setStyle(mainCSS_Styling);
        curVolSpinner.getEditor().setId("SpinnerStyling");
        curVolSpinner.getEditor().setStyle(mainCSS_Styling);

        maxVolSpinner.setPrefSize(100, 10);
        maxVolSpinner.setEditable(true);
        maxVolSpinner.getEditor().setTextFormatter(new TextFormatter<>(filter));
        maxVolSpinner.setStyle(mainCSS_Styling);
        maxVolSpinner.getEditor().setId("SpinnerStyling");
        maxVolSpinner.getEditor().setStyle(mainCSS_Styling);

        Label curVolLabel = new Label("Cur Volumes");
        curVolLabel.setPadding(new Insets(0, 0, 0, 5));
        curVolLabel.setAlignment(Pos.CENTER);
        curVolLabel.setTextFill(Color.rgb(223,213,158));
        curVolLabel.setFont(Font.font(APP_FONT, FontWeight.BOLD, 15));

        Label maxVolLabel = new Label("Max Volumes");
        maxVolLabel.setPadding(new Insets(0, 0, 0, 1));
        maxVolLabel.setAlignment(Pos.CENTER);
        maxVolLabel.setTextFill(Color.rgb(223,213,158));
        maxVolLabel.setFont(Font.font(APP_FONT, FontWeight.BOLD, 15));

        GridPane volProgressRoot = new GridPane();
        volProgressRoot.relocate(40, 190);
        volProgressRoot.setVgap(5);
        volProgressRoot.setHgap(10);
        volProgressRoot.add(curVolLabel, 0, 0);
        volProgressRoot.add(curVolSpinner, 0 , 1);
        volProgressRoot.add(maxVolLabel, 1, 0);
        volProgressRoot.add(maxVolSpinner, 1 , 1);

        ToggleButton getNewSeries = new ToggleButton("Run");
        Button submitButton = new Button("Add");
        submitButton.setPrefSize(60, 10);
        submitButton.relocate(115, 255);
        submitButton.setId("MenuButton");
        submitButton.setStyle(mainCSS_Styling);
        submitButton.setTextFill(Color.rgb(223,213,158));
        submitButton.setFont(Font.font(APP_FONT, FontWeight.BOLD, 15));
        submitButton.setOnMouseClicked(event -> {
            newSeriesList.add(new String[]{titleEnter.getText(), publisherEnter.getText(), bookType.get(), curVolSpinner.getEditor().getText(), maxVolSpinner.getEditor().getText()});
           if (newSeriesList.size() == 90) { submitButton.setDisable(true); }
        });

        getNewSeries.setPrefSize(60, 10);
        getNewSeries.relocate(115, 295);
        getNewSeries.setId("MenuButton");
        getNewSeries.setStyle(mainCSS_Styling);
        getNewSeries.setTextFill(Color.rgb(223,213,158));
        getNewSeries.setFont(Font.font(APP_FONT, FontWeight.BOLD, 15));
        getNewSeries.setOnMouseClicked(event -> {
            newSeriesList.forEach(series -> userCollection.add(new Series().CreateNewSeries(series[0], series[1], series[2], Integer.parseInt(series[3]), Integer.parseInt(series[4]))));
            filteredUserCollection = userCollection;
            collectionSetup(primaryStage);
            primaryStage.setScene(mainScene);
        });

        Pane newSeriesPane = new Pane();
        newSeriesPane.setStyle("" +
                "-fx-background-color: rgb(18, 23, 29);" +
                "-fx-border-color: rgb(223, 213, 158);" +
                "-fx-border-width: 4;"
        );
        newSeriesPane.setPrefSize(290, 345);
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

        newSeriesPane.getChildren().addAll(titleLabel, titleEnter, publisherLabel, publisherEnter, bookTypeRoot, volProgressRoot, submitButton, getNewSeries);

        return newSeriesPane;
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
        collection.setHgap(40);
        collection.setVgap(40);
        collection.setPadding(new Insets(20, 0, 20, 0));
        collection.setAlignment(Pos.CENTER);
        collection.setStyle("-fx-background-color: rgb(18, 23, 29);");
        collection.setCache(true);
        collection.setCacheHint(CacheHint.DEFAULT);

        ScrollPane collectionScroll = new ScrollPane();
        collectionScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        collectionScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        collectionScroll.setStyle("-fx-focus-color: transparent; -fx-padding: -1 0 0 0;");
        collectionScroll.setFitToHeight(true);
        collectionScroll.setFitToWidth(true);
        collectionScroll.setCache(true);
        collectionScroll.setCacheHint(CacheHint.DEFAULT);

        Pane seriesCard;
        for (Series series : filteredUserCollection) {
            seriesCard = new Pane();
            seriesCard.setStyle("" +
                    "-fx-border-radius: 5px 5px 5px 5px;" +
                    "-fx-background-radius: 5px 5px 5px 5px;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 10, 0.0, 2, 2);");
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
        Rectangle coverRound = new Rectangle(LEFT_SIDE_CARD_WIDTH, SERIES_CARD_HEIGHT);
        coverRound.setStyle("-fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 5px 5px 5px 5px;");
        coverRound.setArcWidth(12);
        coverRound.setArcHeight(12);
        coverRound.setCache(true);
        coverRound.setCacheHint(CacheHint.DEFAULT);

        ImageView cover = new ImageView("File:" + series.getCover());
        cover.setFitHeight(SERIES_CARD_HEIGHT);
        cover.setFitWidth(LEFT_SIDE_CARD_WIDTH);
        cover.setSmooth(true);
        cover.isResizable();
        cover.relocate(0, 0);
        cover.setClip(coverRound);
        cover.setCache(true);
        cover.setCacheHint(CacheHint.DEFAULT);

        Label bookTypeAndPrintStatus = new Label(series.getBookType()+ " | " + series.getPrintStatus());
        bookTypeAndPrintStatus.setPrefHeight(BOTTOM_CARD_HEIGHT);
        bookTypeAndPrintStatus.setPrefWidth(LEFT_SIDE_CARD_WIDTH);
        bookTypeAndPrintStatus.relocate(0, SERIES_CARD_HEIGHT - BOTTOM_CARD_HEIGHT);
        bookTypeAndPrintStatus.setStyle(mainCSS_Styling);
        bookTypeAndPrintStatus.setFont(Font.font(APP_FONT, FontWeight.BOLD, 17));
        bookTypeAndPrintStatus.setAlignment(Pos.CENTER);
        bookTypeAndPrintStatus.setStyle("" +
                "-fx-background-color: rgb(32, 35, 45, 0.9);" +
                "-fx-border-radius: 0px 0px 5px 5px;" +
                "-fx-background-radius: 0px 0px 5px 5px;" +
                "-fx-text-fill: rgb(223, 213, 158);" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7) , 10, 0.0, 1, 1);"
        );
        bookTypeAndPrintStatus.setCache(true);
        bookTypeAndPrintStatus.setCacheHint(CacheHint.DEFAULT);

        Pane leftSideOfSeriesCard = new Pane();
        leftSideOfSeriesCard.relocate(0, 0);
        leftSideOfSeriesCard.setPrefWidth(LEFT_SIDE_CARD_WIDTH);
        leftSideOfSeriesCard.setPrefHeight(SERIES_CARD_HEIGHT);
        leftSideOfSeriesCard.setMaxSize(LEFT_SIDE_CARD_WIDTH, SERIES_CARD_HEIGHT);
        leftSideOfSeriesCard.setCache(true);
        leftSideOfSeriesCard.setCacheHint(CacheHint.DEFAULT);
        //leftSideOfSeriesCard.setId("LeftSideCardStyling");
        leftSideOfSeriesCard.setOnMouseEntered((MouseEvent event) -> bookTypeAndPrintStatus.setStyle("" +
                "-fx-background-color: rgb(223, 213, 158, 0.95);" +
                "-fx-text-fill: rgb(44, 45, 66);" +
                "-fx-border-radius: 0px 0px 5px 5px;" +
                "-fx-background-radius: 0px 0px 5px 5px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7) , 5, 0.0, 1, 1);"));
        leftSideOfSeriesCard.setOnMouseExited((MouseEvent event) -> bookTypeAndPrintStatus.setStyle("" +
                "-fx-background-color: rgb(32, 35, 45, 0.95);" +
                "-fx-border-radius: 0px 0px 5px 5px;" +
                "-fx-background-radius: 0px 0px 5px 5px;" +
                "-fx-text-fill: rgb(223, 213, 158);" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7) , 5, 0.0, 1, 1);"));
        leftSideOfSeriesCard.getChildren().addAll(cover, bookTypeAndPrintStatus);

        Hyperlink aniListLink = new Hyperlink(series.getLink());
        aniListLink.setPrefHeight(SERIES_CARD_HEIGHT);
        aniListLink.setPrefWidth(LEFT_SIDE_CARD_WIDTH);
        aniListLink.setPadding(new Insets(0, 0, 0, -1));
        aniListLink.setStyle("-fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 5px 5px 5px 5px;");
        aniListLink.setOnMouseClicked((MouseEvent event) -> getHostServices().showDocument(aniListLink.getText()));
        aniListLink.setGraphic(leftSideOfSeriesCard);
        aniListLink.setCache(true);
        aniListLink.setCacheHint(CacheHint.DEFAULT);

        return aniListLink;
    }

    private Pane rightSideCardSetup(Series series, char language, Stage primaryStage){
        Integer curVolumes = series.getCurVolumes();
        Integer maxVolumes = series.getMaxVolumes();

        Text publisher = new Text(series.getPublisher() + "\n");
        publisher.setFill(Color.rgb(223,213,158));
        publisher.setFont(Font.font(APP_FONT, FontWeight.LIGHT, FontPosture.ITALIC,11.5));
        publisher.setCache(true);
        publisher.setCacheHint(CacheHint.SPEED);

        Text seriesTitle = new Text();
        seriesTitle.setCacheHint(CacheHint.SPEED);
        seriesTitle.setStyle("-fx-background-color: -fx-normal-background-color;\n" +
                "    -fx-fill: rgb(223,213,158);\n" +
                "    -fx-font-family: 'Segoe UI';\n" +
                "    -fx-font-size: 24;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 1) , 10, 0.0, 1, 1);");

        Text mangaka = new Text();
        mangaka.setCacheHint(CacheHint.SPEED);
        mangaka.setFill(Color.rgb(223,213,158));
        mangaka.setFont(Font.font(APP_FONT,12));

        if (language == 'R'){
            seriesTitle.setText(series.getRomajiTitle());
            mangaka.setText("\n" + series.getRomajiStaff());
        }
        else if (language == 'E'){
            seriesTitle.setText(series.getEnglishTitle());
            mangaka.setText("\n" +series.getRomajiStaff());
        }
        else if (language == 'N'){
            seriesTitle.setText(series.getNativeTitle());
            mangaka.setText("\n" +series.getNativeStaff());
        }
        else{
            seriesTitle.setText("Title Error");
            mangaka.setText("\n" + "Staff Error");
        }

        Text desc = new Text(series.getSeriesDesc());
        desc.setFill(Color.rgb(236, 236, 236, 0.8));
        desc.setLineSpacing(1.6);
        desc.setFont(Font.font(APP_FONT, FontWeight.SEMI_BOLD,14));
        desc.setCache(true);
        desc.setCacheHint(CacheHint.SPEED);

        TextFlow descWrap = new TextFlow();
        descWrap.setStyle("-fx-background-color: rgb(32, 35, 45);");
        descWrap.getChildren().add(desc);
        descWrap.setCache(true);
        descWrap.setCacheHint(CacheHint.SPEED);

        ScrollPane descScroll = new ScrollPane();
        descScroll.setStyle("-fx-background-color: transparent;");
        descScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        descScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        descScroll.setFitToHeight(true);
        descScroll.setFitToWidth(true);
        descScroll.setContent(descWrap);
        descScroll.setCache(true);
        descScroll.setCacheHint(CacheHint.SPEED);

        TextFlow topTextWrap = new TextFlow();
        topTextWrap.setLineSpacing(-2.5);
        topTextWrap.setTextAlignment(TextAlignment.LEFT);
        topTextWrap.setCache(true);
        topTextWrap.setCacheHint(CacheHint.SPEED);
        topTextWrap.getChildren().addAll(publisher, seriesTitle, mangaka);

        double volAmount = (double) curVolumes / maxVolumes;
        DoubleProperty volUpdate = new SimpleDoubleProperty();
        volUpdate.set(volAmount);

        String progTxt = String.format("%s/%s", curVolumes, maxVolumes);
        Label progressTxt = new Label(progTxt);
        progressTxt.setId("VolProgressTxt");
        progressTxt.setStyle(mainCSS_Styling);
        progressTxt.setPrefWidth(RIGHT_SIDE_CARD_WIDTH - (RIGHT_SIDE_CARD_WIDTH - 70));
        progressTxt.setAlignment(Pos.CENTER);
        progressTxt.setCache(true);
        progressTxt.setCacheHint(CacheHint.SPEED);

        Button decrementButton = new Button("-");
        Button incrementButton = new Button("+");

        decrementButton.setStyle(mainCSS_Styling + "-fx-padding: -18 0 -7 0; -fx-font-size: 30;");
        decrementButton.setId("VolProgressButton");
        decrementButton.setOnMouseClicked((MouseEvent event) -> {
            if (series.getCurVolumes() > 0){
                series.setCurVolumes(series.getCurVolumes() - 1);
                Integer seriesCurVolumes = series.getCurVolumes();
                progressTxt.setText(String.format("%s/%s", seriesCurVolumes, maxVolumes));
                volUpdate.set((double) seriesCurVolumes / maxVolumes);

                user.setTotalVolumes(user.getTotalVolumes() - 1);
                updateCollectionNumbers();

                incrementButton.setDisable(false);
            }
        });
        decrementButton.setCache(true);
        decrementButton.setCacheHint(CacheHint.SPEED);

        incrementButton.setStyle(mainCSS_Styling + "-fx-padding: -12 0 -2 0; -fx-font-size: 27;");
        incrementButton.setId("VolProgressButton");
        incrementButton.setOnMouseClicked((MouseEvent event) -> {
            if (series.getCurVolumes() < maxVolumes){
                series.setCurVolumes(series.getCurVolumes() + 1);
                Integer seriesCurVolumes = series.getCurVolumes();
                progressTxt.setText(String.format("%s/%s", seriesCurVolumes, maxVolumes));
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
        volProgressBar.setId("ProgressBarStyling");
        volProgressBar.setStyle(mainCSS_Styling);
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
        seriesSettingIcon.setIconColor(Color.rgb(223,213,158));

        Button seriesCardSettingsButton = new Button();
        seriesCardSettingsButton.setAlignment(Pos.CENTER);
        seriesCardSettingsButton.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 310, BOTTOM_CARD_HEIGHT - 1);
        seriesCardSettingsButton.setGraphic(seriesSettingIcon);
        seriesCardSettingsButton.setId("IconButton");
        seriesCardSettingsButton.setStyle(mainCSS_Styling);
        seriesCardSettingsButton.setOnMouseEntered((MouseEvent event) -> seriesSettingIcon.setIconColor(Color.rgb(44, 45, 66)));
        seriesCardSettingsButton.setOnMouseExited((MouseEvent event) -> seriesSettingIcon.setIconColor(Color.rgb(223,213,158)));
        seriesCardSettingsButton.setCache(true);
        seriesCardSettingsButton.setCacheHint(CacheHint.SPEED);

        FontIcon backToSeriesDataIcon = new FontIcon(BootstrapIcons.CARD_HEADING);
        backToSeriesDataIcon.setIconSize(25);
        backToSeriesDataIcon.setIconColor(Color.rgb(223,213,158));

        Button backToSeriesCardDataButton = new Button();
        backToSeriesCardDataButton.setAlignment(Pos.CENTER);
        backToSeriesCardDataButton.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 310, BOTTOM_CARD_HEIGHT - 1);
        backToSeriesCardDataButton.setGraphic(backToSeriesDataIcon);
        backToSeriesCardDataButton.setId("IconButton");
        backToSeriesCardDataButton.setStyle(mainCSS_Styling);
        backToSeriesCardDataButton.setCache(true);
        backToSeriesCardDataButton.setCacheHint(CacheHint.SPEED);
        backToSeriesCardDataButton.setOnMouseEntered((MouseEvent event) -> backToSeriesDataIcon.setIconColor(Color.rgb(44, 45, 66)));
        backToSeriesCardDataButton.setOnMouseExited((MouseEvent event) -> backToSeriesDataIcon.setIconColor(Color.rgb(223,213,158)));

        BorderPane rightSideBottomPane = new BorderPane();
        rightSideBottomPane.setPrefSize(RIGHT_SIDE_CARD_WIDTH, BOTTOM_CARD_HEIGHT);
        rightSideBottomPane.setStyle("-fx-background-color: rgb(44, 45, 66); -fx-background-radius: 0px 0px 5px 5px; -fx-border-radius: 0px 0px 5px 5px;");
        rightSideBottomPane.setLeft(seriesCardSettingsButton);
        rightSideBottomPane.setCenter(volProgressBar);
        rightSideBottomPane.setRight(volProgress);

        BorderPane rightSideOfSeriesCard = new BorderPane();
        rightSideOfSeriesCard.setStyle("-fx-background-color: rgb(32, 35, 45); -fx-border-radius: 5px 5px 5px 5px; -fx-background-radius: 5px 5px 5px 5px;");
        rightSideOfSeriesCard.setPrefSize(RIGHT_SIDE_CARD_WIDTH, SERIES_CARD_HEIGHT - 50);
        rightSideOfSeriesCard.setMaxSize(RIGHT_SIDE_CARD_WIDTH, SERIES_CARD_HEIGHT - 50);
        rightSideOfSeriesCard.setLayoutX(LEFT_SIDE_CARD_WIDTH);
        rightSideOfSeriesCard.setTop(rightSideTopPane);
        rightSideOfSeriesCard.setBottom(rightSideBottomPane);
        rightSideOfSeriesCard.setCache(true);
        rightSideOfSeriesCard.setCacheHint(CacheHint.SPEED);

        seriesCardSettingsButton.setOnMouseClicked((MouseEvent event) -> {
            rightSideOfSeriesCard.setTop(SeriesCardSettingPane(series, primaryStage));
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

    private HBox SeriesCardSettingPane(Series series, Stage primaryStage){
        TextArea userNotes = new TextArea(series.getUserNotes());
        userNotes.setFocusTraversable(false);
        userNotes.setStyle(mainCSS_Styling);
        userNotes.setWrapText(true);
        userNotes.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 40, SERIES_CARD_HEIGHT - (2 * BOTTOM_CARD_HEIGHT));
        userNotes.textProperty().addListener((object, oldText, newText) -> series.setUserNotes(newText));
        userNotes.setCache(true);
        userNotes.setCacheHint(CacheHint.SPEED);

        FontIcon deleteButtonIcon = new FontIcon(BootstrapIcons.TRASH);
        deleteButtonIcon.setIconSize(30);
        deleteButtonIcon.setIconColor(Color.rgb(223,213,158));

        Button deleteSeriesButton = new Button();
        deleteSeriesButton.setGraphic(deleteButtonIcon);
        deleteSeriesButton.setId("IconButtonSettings");
        deleteSeriesButton.setStyle(mainCSS_Styling);
        deleteSeriesButton.setCache(true);
        deleteSeriesButton.setCacheHint(CacheHint.SPEED);
        deleteSeriesButton.setOnMouseClicked((MouseEvent event) -> {
            userCollection.removeIf(delSeries -> delSeries.getRomajiTitle().equals(series.getRomajiTitle()));
            collectionSetup(primaryStage);
            primaryStage.setScene(mainScene);
        });
       deleteSeriesButton.setOnMouseEntered((MouseEvent event) -> deleteButtonIcon.setIconColor(Color.rgb(44, 45, 66)));
        deleteSeriesButton.setOnMouseExited((MouseEvent event) -> deleteButtonIcon.setIconColor(Color.rgb(223,213,158)));
        deleteSeriesButton.setCache(true);
        deleteSeriesButton.setCacheHint(CacheHint.SPEED);

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("[0-9]")) { return change; }
            return null;
        };

        TextField curVolChange = new TextField();
        curVolChange.setPrefWidth(50);
        curVolChange.setId("TextFieldStyling");
        curVolChange.setStyle(mainCSS_Styling);
        curVolChange.setAlignment(Pos.CENTER);
        curVolChange.setTextFormatter(new TextFormatter<>(filter));

        TextField maxVolChange = new TextField();
        maxVolChange.setPrefWidth(50);
        maxVolChange.setId("TextFieldStyling");
        maxVolChange.setStyle(mainCSS_Styling);
        maxVolChange.setAlignment(Pos.CENTER);
        maxVolChange.setTextFormatter(new TextFormatter<>(filter));

        FontIcon changeVolButtonIcon = new FontIcon(BootstrapIcons.ARROW_REPEAT);
        changeVolButtonIcon.setIconSize(30);
        changeVolButtonIcon.setIconColor(Color.rgb(223,213,158));

        Button changeVolCountButton = new Button();
        changeVolCountButton.setAlignment(Pos.CENTER);
        changeVolCountButton.setGraphic((changeVolButtonIcon));
        changeVolCountButton.setId("IconButtonSettings");
        changeVolCountButton.setStyle(mainCSS_Styling);
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
        changeVolCountButton.setOnMouseEntered((MouseEvent event) -> changeVolButtonIcon.setIconColor(Color.rgb(44, 45, 66)));
        changeVolCountButton.setOnMouseExited((MouseEvent event) -> changeVolButtonIcon.setIconColor(Color.rgb(223,213,158)));
        changeVolCountButton.setCache(true);
        changeVolCountButton.setCacheHint(CacheHint.SPEED);

        VBox settingsButtons = new VBox();
        settingsButtons.setSpacing(10);
        settingsButtons.setPadding(new Insets(0, 7, 0, 0));
        settingsButtons.setAlignment(Pos.CENTER);
        settingsButtons.getChildren().addAll(deleteSeriesButton, curVolChange, maxVolChange, changeVolCountButton);
        settingsButtons.setCache(true);
        settingsButtons.setCacheHint(CacheHint.SPEED);

        HBox settingsCardPane = new HBox();
        settingsCardPane.setStyle("-fx-padding: 5 2 4 10;");
        settingsCardPane.setAlignment(Pos.CENTER);
        settingsCardPane.setPrefSize(RIGHT_SIDE_CARD_WIDTH - 20, SERIES_CARD_HEIGHT - BOTTOM_CARD_HEIGHT);
        settingsCardPane.setSpacing(10);
        settingsCardPane.getChildren().addAll(userNotes, settingsButtons);
        settingsCardPane.setCache(true);
        settingsCardPane.setCacheHint(CacheHint.SPEED);

        return settingsCardPane;
    }

    public static void main(String[] args) { launch(args); }
}
