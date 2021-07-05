package TsundOkuApp.PriceAnalysis;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RobertsAnimeCornerStore {

	public static String[] links = new String[2];
	private static ArrayList<String[]> dataList = new ArrayList<>();
	private static final HashMap<String, String> urlMapDict = new HashMap<String, String>()
	{
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

	private static String getUrl(String bookTitle, boolean pageExists){
		String url = "Empty";
		if (!pageExists){ // Gets the starting page based on first letter
			for (String urlMatch : urlMapDict.keySet()){
				if (Pattern.compile(urlMapDict.get(urlMatch)).matcher(bookTitle).find()){
					url = "https://www.animecornerstore.com/" + urlMatch + ".html";
					links[0] = url;
					break;
				}
			}
		}
		else{ //Gets the actual page that houses the data that will be scraped from
			url = "https://www.animecornerstore.com/" + bookTitle;
			links[1] = url;
		}
		return url;
	}

	private static String getPageData(WebDriver driver, String bookTitle, char bookType){
		String link = "DNE";
		driver.get(getUrl(bookTitle, false));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Element seriesTitle = Jsoup.parse(driver.getPageSource()).selectFirst("a[href]:contains(" + bookTitle + ")");

		bookTitle = bookTitle.toLowerCase();
		String currTitle = seriesTitle.text().toLowerCase();
		if (currTitle.contains(bookTitle)){
			if ((bookType == 'M') && (currTitle.contains("graphic"))){
				link = getUrl(seriesTitle.attr("href"), true);
			}
			else if ((bookType == 'N') && (!currTitle.contains("graphic"))){
				link = getUrl(seriesTitle.attr("href"), true);
			}
		}
		return link;
	}

	public static ArrayList<String[]> getRobertsAnimeCornerStoreData(String bookTitle, char bookType){
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

		String linkPage = getPageData(driver, bookTitle, bookType);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (linkPage.equals("DNE")){
			System.out.println(bookTitle + " does not exist at this website");
		}
		else{
			// Start scraping the URL where the data is found
			driver.get(linkPage);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Get the HTML and perform scraping
			Document rightStufLink = Jsoup.parse(driver.getPageSource());
			List<Element> titleData = rightStufLink.select("font[face='dom bold, arial, helvetica'] > b").parallelStream().filter(title -> title.text().toLowerCase().contains(bookTitle.toLowerCase())).collect(Collectors.toList());
			List<Element> priceData = rightStufLink.select("font[color^=#ffcc33]:contains($)").parallelStream().filter(price -> price.text().contains("$")).collect(Collectors.toList());

			String currTitle, groupMatch;
			Pattern pattern = Pattern.compile("#[\\d]+");
			Matcher titleMatch;
			for (int x = 0; x < titleData.size(); x++){
				currTitle = titleData.get(x).text().replace(",", "");
				titleMatch = pattern.matcher(currTitle);
				if (titleMatch.find()){
					currTitle = currTitle.substring(0, titleMatch.end()).replaceAll("\\s+", " ");
				}

				dataList.add(new String[]{currTitle, priceData.get(x).text().trim(), currTitle.contains("Pre Order") ? "PO" : "IS", "RobertsAnimeCornerStore"});
			}

			for (String[] data : dataList){
				System.out.println(Arrays.toString(data));
			}
			for (String link : links){
				System.out.println(link);
			}
		}
		driver.quit();
		return dataList;
	}

	public static void main (String[] args){
		System.setProperty("webdriver.edge.driver", "resources/DriverExecutables/msedgedriver.exe");
		getRobertsAnimeCornerStoreData("World Trigger", 'M');
	}
}
