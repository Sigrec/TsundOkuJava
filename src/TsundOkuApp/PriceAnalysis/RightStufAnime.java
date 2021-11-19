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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RightStufAnime {
	private static final ArrayList<String> rightStufLinks = new ArrayList<>();
	private static final ArrayList<String[]> dataList = new ArrayList<>();

	private static String filterBookTitle(String bookTitle){
		return bookTitle.replaceAll(" ", "%20").replaceAll("&", "%26");
	}

	private static String checkBookType(char bookType){
		switch (bookType) {
			case 'M' -> {
				return "Manga";
			}
			case 'N' -> {
				return "Novels";
			}
		};
		return "Error";
	}

	private static String getUrl(String bookTitle, char bookType, int currPageNum){
		String url = "https://www.rightstufanime.com/category/" + checkBookType(bookType) + "?page=" + currPageNum + "&show=96&keywords=" + filterBookTitle(bookTitle);
		rightStufLinks.add(url);
		return url;
	}

	/**
	 * Main method that invokes driver to load the pages to get the HTML data to scrape for information based on what series and book type the user wants data for.
	 * Last Edited: 11/17/2021
	 * @param bookTitle [String], the title of the series the user wants data for
	 * @param bookType [char], the type of book the user wants either a Manga or a Light Novel
	 * @param memberStatus [boolean], whether the user is a GotAnime member at RightStufAnime to determine whether to apply the discount
	 * @param currPageNum [byte], the current page number that is being traversed for data
	 * @return [List<String[]>], the list of all the data pulled from the website
	 */
	public static ArrayList<String[]> GetRightStufAnimeData(String bookTitle, char bookType, boolean memberStatus, byte currPageNum) throws FileNotFoundException {
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
			Thread.sleep(3000);
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
		DecimalFormat priceRound = new DecimalFormat("$0.00");
		for (int x = 0; x < titleData.size(); x++) {
			currTitle = titleData.get(x).text();
			//Checks to see if the title parsed from the website matches to the title the user wants
			if (currTitle.toLowerCase().replaceAll("[^a-z']", "").contains(bookTitle.toLowerCase().replaceAll("[^a-z']", ""))) {
				priceVal = new BigDecimal(priceData.get(x).text().substring(1));
				priceTxt = memberStatus ? priceRound.format(priceVal.subtract(priceVal.multiply(GotAnimeDiscount)).round(new MathContext(4, RoundingMode.UP))) : priceRound.format(priceVal.round(new MathContext(4, RoundingMode.UP)));
				stockStatus = stockStatusData.get(x).text();
				if (stockStatus.contains("In Stock"))
				{
					stockStatus = "IS";
				}
				else if (stockStatus.contains("Out of Stock"))
				{
					stockStatus = "OOS";
				}
				else if (stockStatus.contains("Pre-Order"))
				{
					stockStatus = "PO";
				}
				else
				{
					stockStatus = "OOP";
				}
				dataList.add(new String[]{currTitle.replaceAll("Volume", "Vol").replace(" Manga", ""), priceTxt.trim(), stockStatus, "RightStufAnime"});
			}
		}

		if (pageCheck != null) {
			currPageNum++;
			GetRightStufAnimeData(bookTitle, bookType, memberStatus, currPageNum);
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

			for (String link : rightStufLinks) {
				System.out.println(link);
			}
		}
		return dataList;
	}

	public static void main(String[] args) throws FileNotFoundException {
		System.setProperty("webdriver.edge.driver", "resources/DriverExecutables/msedgedriver.exe");
		GetRightStufAnimeData("Overlord", 'M', true, (byte) 1);
	}
}
