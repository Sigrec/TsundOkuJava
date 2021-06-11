package TsundOkuApp;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Collector implements java.io.Serializable{

	private String userName;
	private Integer totalVolumes;
	private char curLanguage;
	private ArrayList<TsundOkuTheme> savedThemes;
	private List<Series> collection;

	public Collector() { }

	public char getCurLanguage() {
		return curLanguage;
	}

	public void setCurLanguage(char curLanguage) {
		this.curLanguage = curLanguage;
	}

	public String getUserName() { return userName; }

	public void setUserName(String userName) { this.userName = userName; }

	public Integer getTotalVolumes() { return totalVolumes;  }

	public void setTotalVolumes(Integer totalVolumes) { this.totalVolumes = totalVolumes; }

	public void setSavedThemes(ArrayList<TsundOkuTheme> savedThemes) { this.savedThemes = savedThemes; }

	public ArrayList<TsundOkuTheme> getSavedThemes() { return savedThemes; }

	public void addNewTheme(TsundOkuTheme newTheme) { this.savedThemes.add(newTheme); }

	public TsundOkuTheme getMainTheme(){ return this.savedThemes != null ? this.savedThemes.get(0) : null; }

	public void setNewMainTheme(TsundOkuTheme newMainTheme) { Collections.swap(this.savedThemes, this.savedThemes.indexOf(newMainTheme),0); }

	public List<Series> getCollection() { return collection; }

	public void setCollection(List<Series> collection) { this.collection = collection; }
}
