/*
	Created by Prem
 */

package TsundOkuApp.PriceAnalysis;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RobertsAnimeCornerStore {

	private static final ArrayList<String> ROBERTS_ANIME_CORNER_STORE_LINK = new ArrayList<>(1);
	private static final ArrayList<String[]> DATA_LIST = new ArrayList<>();
	private static final HashMap<String, String> URL_MAP_DICT = new HashMap<>() {
		{
			put("mangrapnovag", "^[a-bA-B\\d]");
			put("mangrapnovhp", "^[c-dC-D]");
			put("mangrapnovqz", "^[e-gE-G]");
			put("magrnomo", "^[h-kH-K]");
			put("magrnops", "^[l-nL-N]");
			put("magrnotz", "^[o-qO-Q]");
			put("magrnors", "^[r-sR-S]");
			put("magrnotv", "^[t-vT-V]");
			put("magrnowz", "^[w-zW-Z]");
		}
	};

	private static String getUrl(String curLink, boolean pageExists){
		String url = "Empty";
		if (!pageExists){ // Gets the starting page based on first letter
			for (String urlMatch : URL_MAP_DICT.keySet()){
				if (Pattern.compile(URL_MAP_DICT.get(urlMatch)).matcher(curLink).find()){
					url = "https://www.animecornerstore.com/" + urlMatch + ".html";
					//ROBERTS_ANIME_CORNER_STORE_LINK.add(0, url);
					break;
				}
			}
			//ROBERTS_ANIME_CORNER_STORE_LINK.add(0, url);
		}
		else{ //Gets the actual page that houses the data that will be scraped from
			url = "https://www.animecornerstore.com/" + curLink;
			ROBERTS_ANIME_CORNER_STORE_LINK.add(0, url);
		}
		return url;
	}

	private static String getPageData(WebDriver driver, String bookTitle, char bookType){
		driver.get(getUrl(bookTitle, false));
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Element seriesLink = null;
		switch (bookType) {
			case 'M' -> seriesLink = Jsoup.parse(driver.getPageSource()).selectFirst("a[href]:contains(" + bookTitle + " Graphic Novels)");
			case 'N' -> seriesLink = Jsoup.parse(driver.getPageSource()).selectFirst("a[href]:contains(" + bookTitle + " Novels)");
		}

		return seriesLink == null ? "DNE" : getUrl(seriesLink.attr("href"), true);
	}

	public static ArrayList<String[]> GetRobertsAnimeCornerStoreData(String bookTitle, char bookType) throws FileNotFoundException {
		EdgeOptions edgeOptions = new EdgeOptions();
		edgeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);
		edgeOptions.addArguments("headless");
		edgeOptions.addArguments("enable-automation");
		edgeOptions.addArguments("no-sandbox");
		edgeOptions.addArguments("disable-infobars");
		edgeOptions.addArguments("disable-dev-shm-usage");
		edgeOptions.addArguments("disable-browser-side-navigation");
		edgeOptions.addArguments("disable-gpu");
		edgeOptions.addArguments("disable-extensions");
		edgeOptions.addArguments("inprivate");
		WebDriver driver = new EdgeDriver(edgeOptions);

		String linkPage = getPageData(driver, bookTitle.replaceAll("-", " "), bookType);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (linkPage.equals("DNE")){
			driver.quit();
			System.out.println(bookTitle + " does not exist at Roberts Anime Corner Store");
		}
		else{
			// Start scraping the URL where the data is found
			driver.get(linkPage);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Get the HTML and perform scraping
			Document robertsLink = Jsoup.parse(driver.getPageSource());
			String finalBookTitle = bookTitle.toLowerCase().replaceAll("-", " ");
			List<Element> titleData = robertsLink.select("font[face='dom bold, arial, helvetica'] > b").parallelStream().filter(title -> title.text().toLowerCase().contains(finalBookTitle)).collect(Collectors.toList());
			List<Element> priceData = robertsLink.select("font[color^=#ffcc33]:contains($)").parallelStream().filter(price -> price.text().contains("$")).collect(Collectors.toList());

			String currTitle;
			for (int x = 0; x < titleData.size(); x++){
				currTitle = titleData.get(x).text().replaceAll(",|#|Graphic Novel| :", "").replaceAll("[ ]{2,}", " ").replaceAll("\\(.*?\\)", "").trim();
				if (currTitle.contains("Omnibus")){
					if (currTitle.contains("One Piece") && currTitle.contains("Vol 10-12")){ // Fix naming issue with one piece
						currTitle = currTitle.substring(0, currTitle.indexOf(" Vol")) + " 4";
					}
					else{
						currTitle = currTitle.substring(0, currTitle.indexOf(" Vol"));
					}
					currTitle = currTitle.substring(0, currTitle.indexOf("Omnibus ") + "Omnibus ".length()) + "Vol " + currTitle.substring(currTitle.indexOf("Omnibus ") + "Omnibus ".length());
				}

				DATA_LIST.add(new String[]{currTitle, priceData.get(x).text().trim(), titleData.get(x).text().contains("Pre Order") ? "Pre-Order" : "In Stock", "RobertsAnimeCornerStore"});
			}
			driver.quit();

			DATA_LIST.sort(new Comparator<String[]>() {
				public int compare(String[] o1, String[] o2) {
					if (o1[0].replaceAll("\\d+", "").equalsIgnoreCase(o2[0].replaceAll("\\d+", ""))) {
						return extractInt(o1[0]) - extractInt(o2[0]);
					}
					return o1[0].compareTo(o2[0]);
				}

				int extractInt(String s) {
					return Integer.parseInt(s.substring(bookTitle.length()).replaceAll(".*?(\\d+).*", "$1"));
				}
			});

			PrintWriter robertsAnimeCornerStoreFile = new PrintWriter("src/TsundOkuApp/PriceAnalysis/Data/RobertsAnimeCornerStoreData.txt");
			if (!DATA_LIST.isEmpty())
			{
				for (String[] volumeData : DATA_LIST){
					robertsAnimeCornerStoreFile.println(Arrays.toString(volumeData));
				}
			}
			else
			{
				robertsAnimeCornerStoreFile.println(bookTitle + " " + bookType + " Does Not Exist at Roberts Anime Corner Store\n");
			}
			robertsAnimeCornerStoreFile.flush();
			robertsAnimeCornerStoreFile.close();

			for (String link : ROBERTS_ANIME_CORNER_STORE_LINK){
				System.out.println(link);
			}
			ROBERTS_ANIME_CORNER_STORE_LINK.clear();
		}

		return DATA_LIST;
	}

//	public static void main (String[] args) throws FileNotFoundException {
//		System.setProperty("webdriver.edge.driver", "resources/DriverExecutables/msedgedriver.exe");
//		GetRobertsAnimeCornerStoreData("One Piece", 'M');
//	}
}
