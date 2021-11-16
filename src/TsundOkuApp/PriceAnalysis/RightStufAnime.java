/*
	Created by Prem
 */

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


public class RightStufAnime {
	private static ArrayList<String> rightStufLinks = new ArrayList<>();
	private static ArrayList<String[]> dataList = new ArrayList<>();

	private static final HashMap<String, String> regexTitleFilterList = new HashMap<String, String>() {
		{
			put(" ", "%20");
			put("&", "%26");
		}
	};

	private static String filterBookTitle(String bookTitle){
		for (String curChar : regexTitleFilterList.keySet()){
			bookTitle = bookTitle.replace(curChar, regexTitleFilterList.get(curChar));
		}
		return bookTitle;
	}

	private static String checkBookType(char bookType){
		if (bookType == 'M'){
			return "Manga";
		}
		else if (bookType == 'N'){
			return "Novels";
		}
		System.out.println("Invalid Book Type, must be a Manga (M) or Light Novel (LN)");
		return "Error";
	}

	private static String getUrl(String bookTitle, char bookType, int currPageNum){
		String url = "https://www.rightstufanime.com/category/" + checkBookType(bookType) + "?page=" + currPageNum + "&show=96&keywords=" + filterBookTitle(bookTitle);
		rightStufLinks.add(url);
		return url;
	}

	public static List<String[]> getRightStufAnimeData(String bookTitle, char bookType, boolean memberStatus, int currPageNum) {
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
		driver.get(getUrl(bookTitle, bookType, currPageNum));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Document rightStufLink = Jsoup.parse(driver.getPageSource());
		Elements titleData = rightStufLink.select("span[itemprop='name']");
		Elements priceData = rightStufLink.select("span[itemprop='price']");
		Elements stockStatusData = rightStufLink.select("div[class='product-line-stock-container '], span[class='product-line-stock-msg-out-text']");
		Element pageCheck = rightStufLink.selectFirst("li[class='global-views-pagination-next']");

		BigDecimal GotAnimeDiscount = new BigDecimal("0.05");
		BigDecimal priceVal;
		String priceTxt, stockStatus, currTitle;
		for (int x = 0; x < titleData.size(); x++) {
			currTitle = titleData.get(x).text();
			//Checks to see if the title parsed from the website matches to the title the user wants
			if (currTitle.toLowerCase().replaceAll("[^a-z']", "").contains(bookTitle.toLowerCase().replaceAll("[^a-z']", ""))) {
				priceVal = new BigDecimal(priceData.get(x).text().substring(1));
				priceTxt = "$" + (memberStatus ? priceVal.subtract(priceVal.multiply(GotAnimeDiscount)).round(new MathContext(3, RoundingMode.UP)) : priceVal.round(new MathContext(3, RoundingMode.UP)));
				stockStatus = stockStatusData.get(x).text();
				if (stockStatus.contains("In Stock")) {
					stockStatus = "IS";
				} else if (stockStatus.contains("Out of Stock")) {
					stockStatus = "OOS";
				} else if (stockStatus.contains("Pre-Order")) {
					stockStatus = "PO";
				} else {
					stockStatus = "OOP";
				}

				dataList.add(new String[]{currTitle, priceTxt.trim(), stockStatus, "RightStufAnime"});
			}
		}

		if (pageCheck != null) {
			currPageNum++;
			getRightStufAnimeData(bookTitle, bookType, memberStatus, currPageNum);
		} else {
			driver.quit();
			//dataList.sort(Comparator.comparing(o -> o[0]));
			for (String[] data : dataList) {
				System.out.println(Arrays.toString(data));
			}
			for (String link : rightStufLinks) {
				System.out.println(link);
			}
		}

		return dataList;
	}

	public static void main(String[] args) {
		System.setProperty("webdriver.edge.driver", "resources/DriverExecutables/msedgedriver.exe");
		getRightStufAnimeData("JuJutsu Kaisen", 'M', true, 1);
	}
}
