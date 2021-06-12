package TsundOkuApp;

public class TsundOkuTheme implements java.io.Serializable, Cloneable{

	private String menuBGColor;
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
	private String collectionSubHeaderColor;
	private String collectionDescColor;
	private String collectionCardBottomBGColor;
	private String collectionNormalIconColor;
	private String collectionHoverIconColor;
	private String collectionProgressBarBorderColor;
	private String collectionProgressBarColor;
	private String collectionProgressBarBGColor;
	private String collectionNormalVolProgressTextColor;
	private String collectionHoverVolProgressTextColor;

	public TsundOkuTheme(){ }

	public TsundOkuTheme(String menuBGColor, String menuBottomBorderColor, String menuTextColor, String menuNormalButtonBGColor, String menuHoverButtonBGColor, String menuNormalButtonBorderColor, String menuHoverButtonBorderColor, String menuNormalButtonTextColor, String menuHoverButtonTextColor, String collectionBGColor, String collectionLinkNormalTextColor, String collectionLinkHoverTextColor, String collectionLinkNormalBGColor, String collectionLinkHoverBGColor, String collectionCardMainBGColor, String collectionTitleColor, String collectionSubHeaderColor, String collectionDescColor, String collectionCardBottomBGColor, String collectionNormalIconColor, String collectionHoverIconColor, String collectionProgressBarBorderColor, String collectionProgressBarColor, String collectionProgressBarBGColor, String collectionNormalVolProgressTextColor, String collectionHoverVolProgressTextColor) {
		this.menuBGColor = menuBGColor;
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
		this.collectionSubHeaderColor = collectionSubHeaderColor;
		this.collectionDescColor = collectionDescColor;
		this.collectionCardBottomBGColor = collectionCardBottomBGColor;
		this.collectionNormalIconColor = collectionNormalIconColor;
		this.collectionHoverIconColor = collectionHoverIconColor;
		this.collectionProgressBarBorderColor = collectionProgressBarBorderColor;
		this.collectionProgressBarColor = collectionProgressBarColor;
		this.collectionProgressBarBGColor = collectionProgressBarBGColor;
		this.collectionNormalVolProgressTextColor = collectionNormalVolProgressTextColor;
		this.collectionHoverVolProgressTextColor = collectionHoverVolProgressTextColor;
	}

	protected Object clone() throws CloneNotSupportedException{
		TsundOkuTheme clonedTheme = (TsundOkuTheme) super.clone();
		return clonedTheme;
	}

	public String getMenuBGColor() {
		return menuBGColor;
	}

	public void setMenuBGColor(String menuBGColor) {
		this.menuBGColor = menuBGColor;
	}

	public String getMenuBottomBorderColor() {
		return menuBottomBorderColor;
	}

	public void setMenuBottomBorderColor(String menuBottomBorderColor) {
		this.menuBottomBorderColor = menuBottomBorderColor;
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

	public String getCollectionSubHeaderColor() {
		return collectionSubHeaderColor;
	}

	public void setCollectionSubHeaderColor(String collectionSubHeaderColor) {
		this.collectionSubHeaderColor = collectionSubHeaderColor;
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
}
