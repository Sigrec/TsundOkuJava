package TsundOkuApp;

public class TsundOkuTheme implements java.io.Serializable, Cloneable{

	private String themeName;
	private String menuBGColor;
	private String userNameColor;
	private String userNormalSettingsIconColor;
	private String themeNormalSettingsIconColor;
	private String userHoverSettingsIconColor;
	private String themeHoverSettingsIconColor;
	private String collectionSearchBorderColor;
	private String collectionSearchBGColor;
	private String collectionSearchTextColor;
	private String menuBottomBorderColor;
	private String menuTextColor;
	private String menuNormalButtonBGColor;
	private String menuHoverButtonBGColor;
	private String menuNormalButtonBorderColor;
	private String menuHoverButtonBorderColor;
	private String menuNormalButtonTextColor;
	private String menuHoverButtonTextColor;
	private String collectionBGColor;
	private String collectionLinkNormalTextColor;
	private String collectionLinkHoverTextColor;
	private String collectionLinkNormalBGColor;
	private String collectionLinkHoverBGColor;
	private String collectionCardMainBGColor;
	private String collectionTitleColor;
	private String collectionPublisherColor;
	private String collectionMangakaColor;
	private String collectionDescColor;
	private String collectionCardBottomBGColor;
	private String collectionIconButtonColor;
	private String collectionNormalIconColor;
	private String collectionHoverIconColor;
	private String collectionProgressBarBorderColor;
	private String collectionProgressBarColor;
	private String collectionProgressBarBGColor;
	private String collectionNormalVolProgressTextColor;
	private String collectionHoverVolProgressTextColor;
	private String collectionUserNotesBGColor;
	private String collectionUserNotesBorderColor;
	private String collectionUserNotesTextColor;

	public static final TsundOkuTheme DEFAULT_THEME = new TsundOkuTheme("Default Theme", "rgb(44,45,66); ", "rgb(223,213,158); ", "rgb(223,213,158); ", "rgb(223,213,158); ", "rgb(18,23,29); ", "rgb(18,23,29); ", "rgb(223,213,158); ", "rgb(18,23,29); ", "rgb(223,213,158); ", "rgb(223,213,158); ", "rgb(223,213,158); ", "rgba(18,23,29,0.6); ", "rgba(223,213,158,0.70); ", "rgb(223,213,158); ", "rgb(18,23,29); ", "rgb(223,213,158); ", "rgb(18,23,29); ", "rgb(18,23,29); ", "rgb(223,213,158); ", "rgb(44,45,66); ", "rgba(32,35,45,0.95); ", "rgba(223,213,158,0.95); ", "rgb(32,35,45); ", "rgb(223,213,158); ", "rgb(223,213,158); ", "rgb(223,213,158); ",  "rgba(236,236,236,0.9); ", "rgb(44,45,66); ", "rgb(44,45,66); ","rgb(223,213,158); ", "rgb(44,45,66); ", "rgb(223,213,158); ", "rgb(223,213,158); ", "rgb(18,23,29); ", "rgb(223,213,158); ", "rgb(18,23,29); ", "rgb(18,23,29); ", "rgb(223,213,158); ", "rgb(223,213,158); ");

	public TsundOkuTheme(){ }

	public TsundOkuTheme(String themeName, String menuBGColor, String userNameColor, String userNormalSettingsIconColor, String themeNormalSettingsIconColor, String userHoverSettingsIconColor, String themeHoverSettingsIconColor, String collectionSearchBorderColor, String collectionSearchBGColor, String collectionSearchTextColor, String menuBottomBorderColor, String menuTextColor, String menuNormalButtonBGColor, String menuHoverButtonBGColor, String menuNormalButtonBorderColor, String menuHoverButtonBorderColor, String menuNormalButtonTextColor, String menuHoverButtonTextColor, String collectionBGColor, String collectionLinkNormalTextColor, String collectionLinkHoverTextColor, String collectionLinkNormalBGColor, String collectionLinkHoverBGColor, String collectionCardMainBGColor, String collectionTitleColor, String collectionPublisherColor, String collectionMangakaColor, String collectionDescColor, String collectionCardBottomBGColor, String collectionIconButtonColor, String collectionNormalIconColor, String collectionHoverIconColor, String collectionProgressBarBorderColor, String collectionProgressBarColor, String collectionProgressBarBGColor, String collectionNormalVolProgressTextColor, String collectionHoverVolProgressTextColor, String collectionUserNotesBGColor, String collectionUserNotesBorderColor, String collectionUserNotesTextColor) {
		this.themeName = themeName;
		this.menuBGColor = menuBGColor;
		this.userNameColor = userNameColor;
		this.userNormalSettingsIconColor = userNormalSettingsIconColor;
		this.themeNormalSettingsIconColor = themeNormalSettingsIconColor;
		this.userHoverSettingsIconColor = userHoverSettingsIconColor;
		this.themeHoverSettingsIconColor = themeHoverSettingsIconColor;
		this.collectionSearchBorderColor = collectionSearchBorderColor;
		this.collectionSearchBGColor = collectionSearchBGColor;
		this.collectionSearchTextColor = collectionSearchTextColor;
		this.menuBottomBorderColor = menuBottomBorderColor;
		this.menuTextColor = menuTextColor;
		this.menuNormalButtonBGColor = menuNormalButtonBGColor;
		this.menuHoverButtonBGColor = menuHoverButtonBGColor;
		this.menuNormalButtonBorderColor = menuNormalButtonBorderColor;
		this.menuHoverButtonBorderColor = menuHoverButtonBorderColor;
		this.menuNormalButtonTextColor = menuNormalButtonTextColor;
		this.menuHoverButtonTextColor = menuHoverButtonTextColor;
		this.collectionBGColor = collectionBGColor;
		this.collectionLinkNormalTextColor = collectionLinkNormalTextColor;
		this.collectionLinkHoverTextColor = collectionLinkHoverTextColor;
		this.collectionLinkNormalBGColor = collectionLinkNormalBGColor;
		this.collectionLinkHoverBGColor = collectionLinkHoverBGColor;
		this.collectionCardMainBGColor = collectionCardMainBGColor;
		this.collectionTitleColor = collectionTitleColor;
		this.collectionPublisherColor = collectionPublisherColor;
		this.collectionMangakaColor = collectionMangakaColor;
		this.collectionDescColor = collectionDescColor;
		this.collectionCardBottomBGColor = collectionCardBottomBGColor;
		this.collectionIconButtonColor = collectionIconButtonColor;
		this.collectionNormalIconColor = collectionNormalIconColor;
		this.collectionHoverIconColor = collectionHoverIconColor;
		this.collectionProgressBarBorderColor = collectionProgressBarBorderColor;
		this.collectionProgressBarColor = collectionProgressBarColor;
		this.collectionProgressBarBGColor = collectionProgressBarBGColor;
		this.collectionNormalVolProgressTextColor = collectionNormalVolProgressTextColor;
		this.collectionHoverVolProgressTextColor = collectionHoverVolProgressTextColor;
		this.collectionUserNotesBGColor = collectionUserNotesBGColor;
		this.collectionUserNotesBorderColor = collectionUserNotesBorderColor;
		this.collectionUserNotesTextColor = collectionUserNotesTextColor;
	}

	protected Object clone() throws CloneNotSupportedException{
		return super.clone();
	}

	public String getThemeName() {
		return themeName;
	}

	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}

	public String getMenuBGColor() {
		return menuBGColor;
	}

	public void setMenuBGColor(String menuBGColor) {
		this.menuBGColor = menuBGColor;
	}

	public String getUserNameColor() {
		return userNameColor;
	}

	public void setUserNameColor(String userNameColor) {
		this.userNameColor = userNameColor;
	}

	public String getMenuBottomBorderColor() {
		return menuBottomBorderColor;
	}

	public void setMenuBottomBorderColor(String menuBottomBorderColor) {
		this.menuBottomBorderColor = menuBottomBorderColor;
	}

	public String getUserNormalSettingsIconColor() {
		return userNormalSettingsIconColor;
	}

	public void setUserNormalSettingsIconColor(String userNormalSettingsIconColor) {
		this.userNormalSettingsIconColor = userNormalSettingsIconColor;
	}

	public String getThemeNormalSettingsIconColor() {
		return themeNormalSettingsIconColor;
	}

	public void setThemeNormalSettingsIconColor(String themeNormalSettingsIconColor) {
		this.themeNormalSettingsIconColor = themeNormalSettingsIconColor;
	}

	public String getUserHoverSettingsIconColor() {
		return userHoverSettingsIconColor;
	}

	public void setUserHoverSettingsIconColor(String userHoverSettingsIconColor) {
		this.userHoverSettingsIconColor = userHoverSettingsIconColor;
	}

	public String getThemeHoverSettingsIconColor() {
		return themeHoverSettingsIconColor;
	}

	public void setThemeHoverSettingsIconColor(String themeHoverSettingsIconColor) {
		this.themeHoverSettingsIconColor = themeHoverSettingsIconColor;
	}

	public String getCollectionSearchBorderColor() {
		return collectionSearchBorderColor;
	}

	public void setCollectionSearchBorderColor(String collectionSearchBorderColor) {
		this.collectionSearchBorderColor = collectionSearchBorderColor;
	}

	public String getCollectionSearchBGColor() {
		return collectionSearchBGColor;
	}

	public void setCollectionSearchBGColor(String collectionSearchBGColor) {
		this.collectionSearchBGColor = collectionSearchBGColor;
	}

	public String getCollectionSearchTextColor() {
		return collectionSearchTextColor;
	}

	public void setCollectionSearchTextColor(String collectionSearchTextColor) {
		this.collectionSearchTextColor = collectionSearchTextColor;
	}

	public String getMenuTextColor() {
		return menuTextColor;
	}

	public void setMenuTextColor(String menuTextColor) {
		this.menuTextColor = menuTextColor;
	}

	public String getMenuNormalButtonBGColor() {
		return menuNormalButtonBGColor;
	}

	public void setMenuNormalButtonBGColor(String menuNormalButtonBGColor) {
		this.menuNormalButtonBGColor = menuNormalButtonBGColor;
	}

	public String getMenuHoverButtonBGColor() {
		return menuHoverButtonBGColor;
	}

	public void setMenuHoverButtonBGColor(String menuHoverButtonBGColor) {
		this.menuHoverButtonBGColor = menuHoverButtonBGColor;
	}

	public String getMenuNormalButtonBorderColor() {
		return menuNormalButtonBorderColor;
	}

	public void setMenuNormalButtonBorderColor(String menuNormalButtonBorderColor) {
		this.menuNormalButtonBorderColor = menuNormalButtonBorderColor;
	}

	public String getMenuHoverButtonBorderColor() {
		return menuHoverButtonBorderColor;
	}

	public void setMenuHoverButtonBorderColor(String menuHoverButtonBorderColor) {
		this.menuHoverButtonBorderColor = menuHoverButtonBorderColor;
	}

	public String getMenuNormalButtonTextColor() {
		return menuNormalButtonTextColor;
	}

	public void setMenuNormalButtonTextColor(String menuNormalButtonTextColor) {
		this.menuNormalButtonTextColor = menuNormalButtonTextColor;
	}

	public String getMenuHoverButtonTextColor() {
		return menuHoverButtonTextColor;
	}

	public void setMenuHoverButtonTextColor(String menuHoverButtonTextColor) {
		this.menuHoverButtonTextColor = menuHoverButtonTextColor;
	}

	public String getCollectionBGColor() {
		return collectionBGColor;
	}

	public void setCollectionBGColor(String collectionBGColor) {
		this.collectionBGColor = collectionBGColor;
	}

	public String getCollectionLinkNormalTextColor() {
		return collectionLinkNormalTextColor;
	}

	public void setCollectionLinkNormalTextColor(String collectionLinkNormalTextColor) {
		this.collectionLinkNormalTextColor = collectionLinkNormalTextColor;
	}

	public String getCollectionLinkHoverTextColor() {
		return collectionLinkHoverTextColor;
	}

	public void setCollectionLinkHoverTextColor(String collectionLinkHoverTextColor) {
		this.collectionLinkHoverTextColor = collectionLinkHoverTextColor;
	}

	public String getCollectionLinkNormalBGColor() {
		return collectionLinkNormalBGColor;
	}

	public void setCollectionLinkNormalBGColor(String collectionLinkNormalBGColor) {
		this.collectionLinkNormalBGColor = collectionLinkNormalBGColor;
	}

	public String getCollectionLinkHoverBGColor() {
		return collectionLinkHoverBGColor;
	}

	public void setCollectionLinkHoverBGColor(String collectionLinkHoverBGColor) {
		this.collectionLinkHoverBGColor = collectionLinkHoverBGColor;
	}

	public String getCollectionCardMainBGColor() {
		return collectionCardMainBGColor;
	}

	public void setCollectionCardMainBGColor(String collectionCardMainBGColor) {
		this.collectionCardMainBGColor = collectionCardMainBGColor;
	}

	public String getCollectionTitleColor() {
		return collectionTitleColor;
	}

	public void setCollectionTitleColor(String collectionTitleColor) {
		this.collectionTitleColor = collectionTitleColor;
	}

	public String getCollectionPublisherColor() {
		return collectionPublisherColor;
	}

	public void setCollectionPublisherColor(String collectionPublisherColor) {
		this.collectionPublisherColor = collectionPublisherColor;
	}

	public String getCollectionMangakaColor() {
		return collectionMangakaColor;
	}

	public void setCollectionMangakaColor(String collectionMangakaColor) {
		this.collectionMangakaColor = collectionMangakaColor;
	}

	public String getCollectionDescColor() {
		return collectionDescColor;
	}

	public void setCollectionDescColor(String collectionDescColor) {
		this.collectionDescColor = collectionDescColor;
	}

	public String getCollectionCardBottomBGColor() {
		return collectionCardBottomBGColor;
	}

	public void setCollectionCardBottomBGColor(String collectionCardBottomBGColor) {
		this.collectionCardBottomBGColor = collectionCardBottomBGColor;
	}

	public String getCollectionNormalIconColor() {
		return collectionNormalIconColor;
	}

	public void setCollectionNormalIconColor(String collectionNormalIconColor) {
		this.collectionNormalIconColor = collectionNormalIconColor;
	}

	public String getCollectionHoverIconColor() {
		return collectionHoverIconColor;
	}

	public void setCollectionHoverIconColor(String collectionHoverIconColor) {
		this.collectionHoverIconColor = collectionHoverIconColor;
	}

	public String getCollectionProgressBarBorderColor() {
		return collectionProgressBarBorderColor;
	}

	public void setCollectionProgressBarBorderColor(String collectionProgressBarBorderColor) {
		this.collectionProgressBarBorderColor = collectionProgressBarBorderColor;
	}

	public String getCollectionProgressBarColor() {
		return collectionProgressBarColor;
	}

	public void setCollectionProgressBarColor(String collectionProgressBarColor) {
		this.collectionProgressBarColor = collectionProgressBarColor;
	}

	public String getCollectionProgressBarBGColor() {
		return collectionProgressBarBGColor;
	}

	public void setCollectionProgressBarBGColor(String collectionProgressBarBGColor) {
		this.collectionProgressBarBGColor = collectionProgressBarBGColor;
	}

	public String getCollectionNormalVolProgressTextColor() {
		return collectionNormalVolProgressTextColor;
	}

	public void setCollectionNormalVolProgressTextColor(String collectionNormalVolProgressTextColor) {
		this.collectionNormalVolProgressTextColor = collectionNormalVolProgressTextColor;
	}

	public String getCollectionHoverVolProgressTextColor() {
		return collectionHoverVolProgressTextColor;
	}

	public void setCollectionHoverVolProgressTextColor(String collectionHoverVolProgressTextColor) {
		this.collectionHoverVolProgressTextColor = collectionHoverVolProgressTextColor;
	}

	public String getCollectionIconButtonColor() {
		return collectionIconButtonColor;
	}

	public void setCollectionIconButtonColor(String collectionIconButtonColor) {
		this.collectionIconButtonColor = collectionIconButtonColor;
	}

	public String getCollectionUserNotesBGColor() {
		return collectionUserNotesBGColor;
	}

	public void setCollectionUserNotesBGColor(String collectionUserNotesBGColor) {
		this.collectionUserNotesBGColor = collectionUserNotesBGColor;
	}

	public String getCollectionUserNotesBorderColor() {
		return collectionUserNotesBorderColor;
	}

	public void setCollectionUserNotesBorderColor(String collectionUserNotesBorderColor) {
		this.collectionUserNotesBorderColor = collectionUserNotesBorderColor;
	}

	public String getCollectionUserNotesTextColor() {
		return collectionUserNotesTextColor;
	}

	public void setCollectionUserNotesTextColor(String collectionUserNotesTextColor) {
		this.collectionUserNotesTextColor = collectionUserNotesTextColor;
	}
}
