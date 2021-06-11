package TsundOkuApp;

public class TsundOkuTheme implements java.io.Serializable{

	private String menuBGColor, menuBottomBorderColor, menuTextColor, menuNormalButtonBGColor, menuHoverButtonBGColor, menuNormalButtonBorderColor, menuHoverButtonBorderColor, menuNormalButtonTextColor, menuHoverButtonTextColor;

	public TsundOkuTheme(){ }

	public TsundOkuTheme(String menuBGColor, String menuBottomBorderColor, String menuTextColor, String menuNormalButtonBGColor, String menuHoverButtonBGColor, String menuNormalButtonBorderColor, String menuHoverButtonBorderColor, String menuNormalButtonTextColor, String menuHoverButtonTextColor) {
		this.menuBGColor = menuBGColor;
		this.menuBottomBorderColor = menuBottomBorderColor;
		this.menuTextColor = menuTextColor;
		this.menuNormalButtonBGColor = menuNormalButtonBGColor;
		this.menuHoverButtonBGColor = menuHoverButtonBGColor;
		this.menuNormalButtonBorderColor = menuNormalButtonBorderColor;
		this.menuHoverButtonBorderColor = menuHoverButtonBorderColor;
		this.menuNormalButtonTextColor = menuNormalButtonTextColor;
		this.menuHoverButtonTextColor = menuHoverButtonTextColor;
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
}
