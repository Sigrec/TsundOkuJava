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
import java.util.stream.Collectors;

public class InStockTrades {
	public static final ArrayList<String> IN_STOCK_TRADES_LINK = new ArrayList<>(1);
	private static final ArrayList<String[]> DATA_LIST = new ArrayList<>();

	//https://www.instocktrades.com/search?term=world+trigger
	//https://www.instocktrades.com/search?pg=1&title=World+Trigger&publisher=&writer=&artist=&cover=&ps=true
	private static String GetUrl(byte currPageNum, String bookTitle, char bookType){
		String url = "https://www.instocktrades.com/search?pg=" + currPageNum +"&title=" + (bookType == 'N' ? bookTitle.replace(' ', '+') + "+Novel" : bookTitle.replace(' ', '+')) + "&publisher=&writer=&artist=&cover=&ps=true";
		IN_STOCK_TRADES_LINK.add(url);
		return url;
	}

	/**
	 *
	 * @param bookTitle
	 * @param currPageNum
	 * @return
	 * @throws FileNotFoundException
	 */
	public static ArrayList<String[]> GetInStockTradesData(String bookTitle, char bookType, byte currPageNum) throws FileNotFoundException {
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
		Document inStockTradesLink = Jsoup.parse(driver.getPageSource());

		// Get the page data from the HTML doc
		List<Element> titleData = inStockTradesLink.select("div[class='detail clearfix']:not(:has(.damage)) > div[class='title']").parallelStream().filter(data -> data.text().contains("Vol")).collect(Collectors.toList());
		List<Element> priceData = inStockTradesLink.select("div[class='price']");
		Element pageCheck = inStockTradesLink.selectFirst("a[class='btn hotaction']");

		if (bookType == 'M'){
			titleData.removeIf(data -> data.text().contains("Novel"));
		}

		for (int x = 0; x < titleData.size(); x++){
			DATA_LIST.add(new String[]{titleData.get(x).text().replaceAll(" GN| Manga| TP|\\(.*?\\)", "").replace("3In1", "Omnibus").replaceFirst("0.*?(\\d+).*", "$1").trim(), priceData.get(x).text().trim(), "In Stock", "InStockTrades"});
		}

		if (pageCheck != null) {
			currPageNum++;
			GetInStockTradesData(bookTitle, bookType, currPageNum);
		}
		else{
			driver.quit();
			for (String link : IN_STOCK_TRADES_LINK){
				System.out.println(link);
			}
			IN_STOCK_TRADES_LINK.clear();
		}

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

		PrintWriter inStockTradesDataFile = new PrintWriter("src/TsundOkuApp/PriceAnalysis/Data/InStockTradesData.txt");
		if (!DATA_LIST.isEmpty())
		{
			for (String[] data : DATA_LIST){
				inStockTradesDataFile.println(Arrays.toString(data));
			}
		}
		else
		{
			inStockTradesDataFile.println(bookTitle + " " + bookType + " Does Not Exist at InStockTrades\n");
		}
		inStockTradesDataFile.flush();
		inStockTradesDataFile.close();

		return DATA_LIST;
	}

//	public static void main (String[] args) throws FileNotFoundException {
//		System.setProperty("webdriver.edge.driver", "resources/DriverExecutables/msedgedriver.exe");
//		GetInStockTradesData("One Piece", 'M', (byte) 1);
//	}
}
