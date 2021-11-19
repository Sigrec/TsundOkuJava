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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

// Can only do manga prob
public class BookDepository {
	public static ArrayList<String> bookDepositoryLinks = new ArrayList<>();
	private static final ArrayList<String[]> dataList = new ArrayList<>();

	//https://www.bookdepository.com/search?searchTerm=Overlord+Novel&searchLang=123&ageRangesTotal=1&searchSortBy=pubdate_low_high&page=1
	//https://www.bookdepository.com/search/?searchTerm=Overlord&searchSortBy=pubdate_low_high&ageRangesTotal=0&category=2634&page=1
	//https://www.bookdepository.com/search/?searchTerm=Overlord&searchLang=123&ageRangesTotal=0&category=2634
	private static String GetUrl(byte currPageNum, String bookTitle, char bookType){
		String url = "";
		bookDepositoryLinks.add(url);
		return url;
	}

	public static ArrayList<String[]> GetAmazonAmericaData(String bookTitle, char bookType, byte currPageNum) throws FileNotFoundException {
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
		List<Element> titleData = bookDepositoryLink.select("div[class='title']");
		List<Element> priceData = bookDepositoryLink.select("div[class='price']");
		Element pageCheck = bookDepositoryLink.selectFirst("a[class='btn hotaction']");

		for (int x = 0; x < titleData.size(); x++)
		{
			dataList.add(new String[]{titleData.get(x).text().trim(), priceData.get(x).text().trim(), "IS", "InStockTrades"});
		}

		if (pageCheck != null)
		{
			currPageNum++;
			GetAmazonAmericaData(bookTitle, bookType, currPageNum);
		}
		else{
			driver.quit();
			for (String link : bookDepositoryLinks)
			{
				System.out.println(link);
			}
		}

		dataList.sort(Comparator.comparing(o -> Integer.parseInt(o[0].substring(o[0].indexOf("Vol")).replaceFirst(".*?(\\d+).*", "$1"))));
		PrintWriter bookDepositoryDataFile = new PrintWriter("src/TsundOkuApp/PriceAnalysis/Data/BookDepostiryData.txt");
		if (!dataList.isEmpty())
		{
			for (String[] data : dataList){
				bookDepositoryDataFile.println(Arrays.toString(data));
			}
		}
		else
		{
			bookDepositoryDataFile.println(bookTitle + " " + bookType + " Does Not Exist at InStockTrades\n");
		}
		bookDepositoryDataFile.flush();
		bookDepositoryDataFile.close();
		return dataList;
	}

		public static void main (String[] args) throws FileNotFoundException {
			System.setProperty("webdriver.edge.driver", "resources/DriverExecutables/msedgedriver.exe");
			GetAmazonAmericaData("World Trigger", 'M', (byte) 1);
		}
}
