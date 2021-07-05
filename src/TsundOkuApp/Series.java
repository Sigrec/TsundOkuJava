/*
	Created by Prem
 */

package TsundOkuApp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.FutureTask;

import javax.imageio.ImageIO;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class Series implements java.io.Serializable {

	private String bookType, publisher, printStatus, link, cover, romajiTitle, englishTitle, nativeTitle, romajiStaff, nativeStaff, seriesDesc, userNotes;
	private Integer curVolumes, maxVolumes;

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.66 Safari/537.36";

	public Series(){ }

	public Series (String bookType, String printStatus, String link, String cover, String publisher, String romajiTitle, String englishTitle, String nativeTitle, String romajiStaff, String nativeStaff, String seriesDesc, String userNotes, Integer curVolumes, Integer maxVolumes){
		this.bookType = bookType;
		this.printStatus = printStatus;
		this.link = link;
		this.cover = cover;
		this.publisher = publisher;
		this.romajiTitle = romajiTitle;
		this.englishTitle = englishTitle;
		this.nativeTitle = nativeTitle;
		this.romajiStaff = romajiStaff;
		this.nativeStaff = nativeStaff;
		this.seriesDesc = seriesDesc;
		this.userNotes = userNotes;
		this.curVolumes = curVolumes;
		this.maxVolumes = maxVolumes;
	}

	public Series CreateNewSeries(String title, String publisher, String bookType, Integer curVol, Integer maxVol){
		curVolumes = curVol;
		maxVolumes = maxVol;
		String seriesObj = "Empty";

		GraphQLQuery seriesQuery = new GraphQLQuery("query ($title: String, $type: MediaFormat) {\n" +
				"Media(search: $title, format: $type) {\n" +
				"  countryOfOrigin\n" +
				"  title {\n" +
				"    romaji\n" +
				"    english\n" +
				"    native\n" +
				"  }\n" +
				"  staff(sort: ROLE) {\n" +
				"    edges {\n" +
				"      role\n" +
				"      node {\n" +
				"        name {\n" +
				"          full\n" +
				"          native\n" +
				"          alternative\n" +
				"        }\n" +
				"      }\n" +
				"    }\n" +
				"  }\n" +
				"  description\n" +
				"  status(version: 2)\n" +
				"  siteUrl\n" +
				"  coverImage {\n" +
				"    extraLarge\n" +
				"  }\n" +
				"}\n" +
				"}").withVariable("title", title).withVariable("type", bookType.toUpperCase());

		try {
			FutureTask<String> task = seriesQuery.submit();
			task.run();
			if (task.isDone()) { seriesObj = task.get(); }
		} catch (Exception e) {
			e.printStackTrace();
		}


		JsonObject mediaJson = JsonParser.parseString(seriesObj).getAsJsonObject().getAsJsonObject("data").getAsJsonObject("Media");
		JsonObject titleJson = mediaJson.getAsJsonObject("title");
		JsonArray staffJsonArray = mediaJson.getAsJsonObject("staff").getAsJsonArray("edges");
		String romajiTitle = titleJson.get("romaji").getAsString();
		JsonElement englishTitle = titleJson.get("english");

		if (!romajiTitle.equalsIgnoreCase(title) && (englishTitle.isJsonNull() || !englishTitle.getAsString().equalsIgnoreCase(title)) && !titleJson.get("native").getAsString().equalsIgnoreCase(title)) { // AL has the series
			try {
				mediaJson = new Gson().toJsonTree(new Gson().fromJson(new BufferedReader(new FileReader("ExtraSeries.json")), HashMap.class).get(title.toLowerCase())).getAsJsonObject().getAsJsonObject("Media");
				titleJson = mediaJson.getAsJsonObject("title");
				staffJsonArray = mediaJson.getAsJsonObject("staff").getAsJsonArray("edges");
				romajiTitle = titleJson.get("romaji").getAsString();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Series newSeries = new Series(
				bookType.equals("Manga") ? getCorrectComicFormat(mediaJson.get("countryOfOrigin").getAsString()) : bookType,
				getCardStatus(mediaJson.get("status").getAsString()),
				mediaJson.get("siteUrl").getAsString(),
				saveNewCoverImage(mediaJson.getAsJsonObject("coverImage").get("extraLarge").getAsString(), romajiTitle, bookType),
				publisher,
				romajiTitle,
				englishTitle.isJsonNull() ? romajiTitle : titleJson.get("english").getAsString(),
				titleJson.get("native").getAsString(),
				getSeriesStaff(staffJsonArray, "full"),
				getSeriesStaff(staffJsonArray, "native"),
				mediaJson.get("description").isJsonNull() ? "" : mediaJson.get("description").getAsString().replaceAll("\\<(.*?)\\>", "").replaceAll("&#9472;&#9472;", "──").replaceFirst("\\(Source: [\\S\\s]+", "").trim(),
				"Edit Notes:",
				curVolumes,
				maxVolumes);

		seriesQuery.reset();
		return newSeries;
	}

	public static String getSeriesStaff(JsonArray staffJsonArray, String nameType){
		StringBuilder staffNames = new StringBuilder();
		String staffRole;
		JsonObject staff;

		int count = 0;
		while (count < staffJsonArray.size()){
			staff = staffJsonArray.get(count).getAsJsonObject();
			staffRole = staff.get("role").getAsString().trim();
			if (staffRole.equals("Story & Art") | staffRole.equals("Story") | staffRole.equals("Art") | staffRole.equals("Original Creator") | staffRole.equals("Character Design") | staffRole.equals("Illustration") | staffRole.equals("Mechanical Design")){
				JsonElement newStaff = staff.getAsJsonObject("node").getAsJsonObject("name").get(nameType);
				if (!newStaff.isJsonNull()){
					staffNames.append(newStaff.getAsString()).append(" | ");
				}
				else{
					if (nameType.equals("native")){ // Native name is null
						staffNames.append(staff.getAsJsonObject("node").getAsJsonObject("name").get("full").getAsString()).append(" | ");
					}
					else { // Full name is null
						staffNames.append(staff.getAsJsonObject("node").getAsJsonObject("name").get(nameType).getAsString()).append(" | ");
					}
				}
			}
			count++;
		}

		return staffNames.substring(0, staffNames.length() - 2).trim();
	}

	public static String getCorrectComicFormat(String jsonCountryOfOrigin){
		switch (jsonCountryOfOrigin) {
			case "KR":
				return "Manhwa";
			case "CN":
				return "Manhua";
			case "FR":
				return "Manfra";
			default:
				return "Manga";
		}
	}

	public static String getCardStatus(String jsonStatus){
		switch (jsonStatus) {
			case "RELEASING":
				return "Ongoing";
			case "FINISHED":
				return "Complete";
			case "CANCELLED":
				return "Cancelled";
			case "HIATUS":
				return "Hiatus";
			case "NOT_YET_RELEASED":
				return "Coming Soon";
			default:
				return "Error";
		}
	}

	public static String saveNewCoverImage(String coverLink, String title, String bookType){
		String imgFormat = coverLink.substring(coverLink.length() - 3);
		String newPath = "Covers/" + title.replaceAll("[^A-Za-z\\d]", "") + "_" + bookType + "." + imgFormat;
		try {
			URL url = new URL(coverLink);
			HttpURLConnection httpURLCon = (HttpURLConnection) url.openConnection();
			httpURLCon.addRequestProperty("User-Agent", USER_AGENT);
			File file = new File(newPath);
			ImageIO.write(ImageIO.read(httpURLCon.getInputStream()), imgFormat, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newPath;
	}

	@Override
	public String toString() {
		return "Series{" + "\n" +
				"bookType='" + bookType + '\'' + "\n" +
				", publisher='" + publisher + '\'' + "\n" +
				", printStatus='" + printStatus + '\'' + "\n" +
				", link='" + link + '\'' + "\n" +
				", cover='" + cover + '\'' + "\n" +
				", romajiTitle='" + romajiTitle + '\'' + "\n" +
				", englishTitle='" + englishTitle + '\'' + "\n" +
				", nativeTitle='" + nativeTitle + '\'' + "\n" +
				", romajiStaff='" + romajiStaff + '\'' + "\n" +
				", nativeStaff='" + nativeStaff + '\'' + "\n" +
				", seriesDesc='" + seriesDesc + '\'' + "\n" +
				", userNotes='" + userNotes + '\'' + "\n" +
				", curVolumes=" + curVolumes + '\'' + "\n" +
				", maxVolumes=" + maxVolumes + '\'' + "\n" +
				'}';
	}

	public static int compareByEnglishTitle(Series series1, Series series2){
		if (series1.getEnglishTitle() == null || series2.getEnglishTitle() == null) { return 0; }
		return series1.getEnglishTitle().compareTo(series2.getEnglishTitle());
	}

	public static int compareByNativeTitle(Series series1, Series series2) {
		if (series1.getNativeTitle() == null || series2.getNativeTitle() == null) { return 0; }
		return series1.getNativeTitle().compareTo(series2.getNativeTitle());
	}

	public static int compareByRomajiTitle(Series series1, Series series2){
		if (series1.getRomajiTitle() == null || series2.getRomajiTitle() == null) { return 0; }
		return series1.getRomajiTitle().compareTo(series2.getRomajiTitle());
	}

	public String getUserNotes() { return userNotes; }

	public void setUserNotes(String userNotes) { this.userNotes = userNotes; }

	public String getPublisher() {
		return publisher;
	}

	public String getPrintStatus() {
		return printStatus;
	}

	public String getLink() {
		return link;
	}

	public String getCover() {
		return cover;
	}

	public String getRomajiTitle() {
		return romajiTitle;
	}

	public String getEnglishTitle() {
		return englishTitle;
	}

	public String getNativeTitle() {
		return nativeTitle;
	}

	public String getRomajiStaff() {
		return romajiStaff;
	}

	public String getNativeStaff() {
		return nativeStaff;
	}

	public String getSeriesDesc() {
		return seriesDesc;
	}

	public String getBookType() { return bookType; }

	public Integer getCurVolumes() {
		return curVolumes;
	}

	public void setCurVolumes(Integer curVolumes) {
		this.curVolumes = curVolumes;
	}

	public Integer getMaxVolumes() {
		return maxVolumes;
	}

	public void setMaxVolumes(Integer maxVolumes) {
		this.maxVolumes = maxVolumes;
	}
}

