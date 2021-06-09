package TsundOkuApp;

public class Collector {

	private String userName;
	private Integer totalVolumes;
	private char curLanguage;

	public Collector(String userName) {
		this.userName = userName;
	}

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
}
