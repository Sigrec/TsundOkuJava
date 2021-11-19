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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RobertsAnimeCornerStore {

	private static final String[] links = new String[2];
	private static final ArrayList<String[]> dataList = new ArrayList<>();
	private static final HashMap<String, String> urlMapDict = new HashMap<>() {
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
			for (String urlMatch : urlMapDict.keySet()){
				if (Pattern.compile(urlMapDict.get(urlMatch)).matcher(curLink).find()){
					url = "https://www.animecornerstore.com/" + urlMatch + ".html";
					links[0] = url;
					break;
				}
			}
			links[0] = url;
		}
		else{ //Gets the actual page that houses the data that will be scraped from
			url = "https://www.animecornerstore.com/" + curLink;
			links[1] = url;
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
			Pattern pattern = Pattern.compile("#[\\d]+");
			Matcher titleMatch;
			for (int x = 0; x < titleData.size(); x++){
				currTitle = titleData.get(x).text().replaceAll(",|#", "").replaceAll("[ ]{2,}", " ");
				titleMatch = pattern.matcher(currTitle);
				if (titleMatch.find()){ // Replace all of the big whitespace
					currTitle = currTitle.substring(0, titleMatch.end()).replaceAll("\\s+", " ");
				}

				//System.out.println(currTitle.substring(0, currTitle.indexOf(" Graphic")));
				dataList.add(new String[]{currTitle.substring(0, currTitle.indexOf(" Graphic")), priceData.get(x).text().trim(), currTitle.contains("Pre Order") ? "PO" : "IS", "RobertsAnimeCornerStore"});
			}
			driver.quit();

			PrintWriter robertsAnimeCornerStoreFile = new PrintWriter("src/TsundOkuApp/PriceAnalysis/Data/RobertsAnimeCornerStoreData.txt");
			if (!dataList.isEmpty())
			{
				for (String[] volumeData : dataList){
					robertsAnimeCornerStoreFile.println(Arrays.toString(volumeData));
				}
			}
			else
			{
				robertsAnimeCornerStoreFile.println(bookTitle + " " + bookType + " Does Not Exist at Roberts Anime Corner Store\n");
			}
			robertsAnimeCornerStoreFile.flush();
			robertsAnimeCornerStoreFile.close();

			for (String link : links){
				System.out.println(link);
			}
		}
		return dataList;
	}

	public static void main (String[] args) throws FileNotFoundException {
		System.setProperty("webdriver.edge.driver", "resources/DriverExecutables/msedgedriver.exe");
		GetRobertsAnimeCornerStoreData("Overlord", 'M');
	}
}
