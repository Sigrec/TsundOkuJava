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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

// Can only do manga prob
public class BookDepository {
	public static ArrayList<String> bookDepositoryLinks = new ArrayList<>();
	private static final ArrayList<String[]> dataList = new ArrayList<>();

	//https://www.bookdepository.com/search/?searchTerm=World+Trigger&searchLang=123&ageRangesTotal=0&category=2633&searchSortBy=pubdate_low_high&page=1
	//https://www.bookdepository.com/search?searchTerm=Overlord+Light+Novel&searchLang=123&ageRangesTotal=0&searchSortBy=pubdate_low_high&page=1
	private static String GetUrl(byte currPageNum, String bookTitle, char bookType){
		String url = "https://www.bookdepository.com/search?searchTerm=" + (bookType == 'N' ? bookTitle + "Light+Novel" : bookTitle) + "&searchLang=123&ageRangesTotal=0&searchSortBy=pubdate_low_high&page=" + currPageNum;
		bookDepositoryLinks.add(url);
		return url;
	}

	public static ArrayList<String[]> GetBookDepositoryData(String bookTitle, char bookType, byte currPageNum) throws FileNotFoundException {
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

		driver.get(GetUrl(currPageNum, bookTitle, bookType));
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Document bookDepositoryLink = Jsoup.parse(driver.getPageSource());

		// Get the page data from the HTML doc
		List<Element> titleData = bookDepositoryLink.select("h3[class='title']");
		List<Element> priceData = bookDepositoryLink.select("p[class='price']");
		List<Element> stockStatusData = bookDepositoryLink.select("div[class='btn-wrap'] > a");
		Element pageCheck = bookDepositoryLink.selectFirst("li[id='next-top']");

		String stockStatus, curTitle;
		for (int x = 0; x < titleData.size(); x++) {
			curTitle = titleData.get(x).text();
			stockStatus = stockStatusData.get(x).text();
			if (stockStatus.contains("Add to basket")) {
				stockStatus = "IS";
			}
			else if (stockStatus.contains("Notify me")) {
				stockStatus = "OOS";
			}
			else if (stockStatus.contains("Pre-order")) {
				stockStatus = "PO";
			}
			dataList.add(new String[]{curTitle.replaceAll("Volume", "Vol").replace(" Manga", ""), priceData.get(x).text().substring(3), "BookDepository"});
		}

		if (pageCheck != null) {
			currPageNum++;
			GetBookDepositoryData(bookTitle, bookType, currPageNum);
		} else {
			driver.quit();
			Comparator<String[]> test = Comparator.comparing(volData -> volData[0].substring(0, volData[0].indexOf("Vol")));
			dataList.sort(test.thenComparing(volData -> Integer.parseInt(volData[0].substring(volData[0].indexOf("Vol")).replaceFirst(".*?(\\d+).*", "$1"))));

			PrintWriter rightStufDataFile = new PrintWriter("src/TsundOkuApp/PriceAnalysis/Data/RightStufAnimeData.txt");
			if (!dataList.isEmpty()){
				for (String[] volumeData : dataList){
					rightStufDataFile.println(Arrays.toString(volumeData));
				}
			}
			else
			{
				rightStufDataFile.println(bookTitle + " " + bookType + " Does Not Exist at RightStufAnime\n");
			}
			rightStufDataFile.flush();
			rightStufDataFile.close();

			for (String link : bookDepositoryLinks) {
				System.out.println(link);
			}
		}
		return dataList;
	}

		public static void main (String[] args) throws FileNotFoundException {
			System.setProperty("webdriver.edge.driver", "resources/DriverExecutables/msedgedriver.exe");
			GetBookDepositoryData("World Trigger", 'M', (byte) 1);
		}
}
