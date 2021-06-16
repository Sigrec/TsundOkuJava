package TsundOkuApp;

import java.util.HashMap;
import java.util.List;

public class Collector implements java.io.Serializable{

	private String userName;
	private Integer totalVolumes;
	private String curLanguage;
	private TsundOkuTheme mainTheme;
	private HashMap<String, TsundOkuTheme> savedThemes;
	private List<Series> collection;

	public Collector() { }

	public Collector(String userName, String curLanguage, TsundOkuTheme mainTheme, HashMap<String, TsundOkuTheme> savedThemes, List<Series> collection) {
		this.userName = userName;
		this.curLanguage = curLanguage;
		this.mainTheme = mainTheme;
		this.savedThemes = savedThemes;
		this.collection = collection;
	}

	public String getCurLanguage() {
		return curLanguage;
	}

	public void setCurLanguage(String curLanguage) {
		this.curLanguage = curLanguage;
	}

	public String getUserName() { return userName; }

	public void setUserName(String userName) { this.userName = userName; }

	public Integer getTotalVolumes() { return totalVolumes;  }

	public void setTotalVolumes(Integer totalVolumes) { this.totalVolumes = totalVolumes; }

	public HashMap<String, TsundOkuTheme>  getSavedThemes() { return savedThemes; }

	public void addNewTheme(TsundOkuTheme newTheme) { this.savedThemes.put(newTheme.getThemeName(), newTheme); }

	public void removeSavedTheme(String curTheme) {
		this.savedThemes.entrySet().removeIf(entry -> (curTheme.equals(entry.getKey())));
	}

	public TsundOkuTheme getMainTheme(){ return this.mainTheme; }

	public TsundOkuTheme setNewMainTheme(String themeName) {
		this.mainTheme = this.savedThemes.get(themeName);
		return mainTheme;
	}

	public List<Series> getCollection() { return collection; }

	public void setCollection(List<Series> collection) { this.collection = collection; }
}
