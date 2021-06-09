package TsundOkuApp;

import org.json.JSONObject;
import org.json.JSONArray;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.FutureTask;

public class Series implements java.io.Serializable, Comparable<Series> {

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

		GraphQLQuery seriesQuery = new GraphQLQuery("query ($title: String, $type: MediaFormat) {\n" +
				"  Media(search: $title, format: $type) {\n" +
				"    countryOfOrigin\n" +
				"    title {\n" +
				"      romaji\n" +
				"      english\n" +
				"      native\n" +
				"    }\n" +
				"    staff(sort: ROLE) {\n" +
				"      edges {\n" +
				"        role\n" +
				"        node {\n" +
				"          name {\n" +
				"            full\n" +
				"            native\n" +
				"            alternative\n" +
				"          }\n" +
				"        }\n" +
				"      }\n" +
				"    }\n" +
				"    description\n" +
				"    status(version: 2)\n" +
				"    siteUrl\n" +
				"    coverImage {\n" +
				"      extraLarge\n" +
				"    }\n" +
				"  }\n" +
				"}\n").withVariable("title", title).withVariable("type", bookType.toUpperCase());

		JSONObject mediaJson = null;
		try {
			FutureTask<String> task = seriesQuery.submit();
			task.run();
			mediaJson = new JSONObject(task.get()).getJSONObject("data").getJSONObject("Media");
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject titleJson = mediaJson.getJSONObject("title");
		JSONArray staffJsonArray = mediaJson.getJSONObject("staff").getJSONArray("edges");
		String jsonTitle = titleJson.optString("english");
		String romajiTitle = titleJson.getString("romaji");


		Series newSeries = new Series(
				bookType.equals("Manga") ? getCorrectComicFormat(mediaJson.getString("countryOfOrigin")) : bookType,
				getCardStatus(mediaJson.getString("status")),
				mediaJson.getString("siteUrl"),
				saveNewCoverImage(mediaJson.getJSONObject("coverImage").getString("extraLarge"), romajiTitle, bookType),
				publisher,
				romajiTitle,
				jsonTitle.trim().equals("") ? romajiTitle : jsonTitle,
				titleJson.getString("native"),
				getSeriesStaff(staffJsonArray, "full"),
				getSeriesStaff(staffJsonArray, "native"),
				mediaJson.getString("description").replaceAll("<.*>", "").replaceFirst("\\(Source: [\\S\\s]+", "").trim(),
				"Edit Notes:",
				curVolumes,
				maxVolumes);

		seriesQuery.reset();
		return newSeries;
	}

	public static String getSeriesStaff(JSONArray staffJsonArray, String nameType){
		StringBuilder staffNames = new StringBuilder();
		String staffRole;
		JSONObject staff;

		int count = 0;
		while (count < staffJsonArray.length()){
			staff = staffJsonArray.getJSONObject(count);
			staffRole = staff.getString("role");
			if (staffRole.equals("Story & Art") | staffRole.equals("Story") | staffRole.equals("Art") | staffRole.equals("Original Creator") | staffRole.equals("Character Design") | staffRole.equals("Illustration") | staffRole.equals("Mechanical Design")){
				staffNames.append(staff.getJSONObject("node").getJSONObject("name").getString(nameType)).append(" | ");
			}
			count++;
		}

		return staffNames.substring(0, staffNames.length() - 2).trim();
	}

	public static String getCorrectComicFormat(String jsonCountryOfOrigin){
		switch (jsonCountryOfOrigin){
			case "KR":
				return "Manhwa";
			case "CN":
				return "Manhua";
			case "JP":
			default:
				return "Manga";
		}
	}

	public static String getCardStatus(String jsonStatus){
		switch (jsonStatus){
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
		String newPath = "src/main/resources/Covers/" + title.replaceAll("[^A-Za-z\\d]", "") + "_" + bookType + "." + imgFormat;
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
		return "Series{" +
				"bookType='" + bookType + '\'' +
				", publisher='" + publisher + '\'' +
				", printStatus='" + printStatus + '\'' +
				", link='" + link + '\'' +
				", cover='" + cover + '\'' +
				", romajiTitle='" + romajiTitle + '\'' +
				", englishTitle='" + englishTitle + '\'' +
				", nativeTitle='" + nativeTitle + '\'' +
				", romajiStaff='" + romajiStaff + '\'' +
				", nativeStaff='" + nativeStaff + '\'' +
				", seriesDesc='" + seriesDesc + '\'' +
				", userNotes='" + userNotes + '\'' +
				", curVolumes=" + curVolumes +
				", maxVolumes=" + maxVolumes +
				'}';
	}

	@Override
	public int compareTo(Series series){
		if (getRomajiTitle() == null || series.getRomajiTitle() == null) { return 0; }
		return getRomajiTitle().compareTo(series.getRomajiTitle());
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

