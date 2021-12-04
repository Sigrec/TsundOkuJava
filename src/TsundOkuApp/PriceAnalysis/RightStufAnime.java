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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Arrays;

public class RightStufAnime {
	private static final ArrayList<String> RIGHT_STUF_ANIME_LINK = new ArrayList<>();
	private static final ArrayList<String[]> DATA_LIST = new ArrayList<>();

	private static String filterBookTitle(String bookTitle){
		return bookTitle.replaceAll(" ", "%20").replaceAll("&", "%26");
	}

	private static String checkBookType(char bookType){
		switch (bookType) {
			case 'M':
				return "Manga";
			case 'N':
				return "Novels";
		};
		return "Error";
	}

	private static String getUrl(String bookTitle, char bookType, int currPageNum){
		String url = "https://www.rightstufanime.com/category/" + checkBookType(bookType) + "?page=" + currPageNum + "&show=96&keywords=" + filterBookTitle(bookTitle);
		RIGHT_STUF_ANIME_LINK.add(url);
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
				if (stockStatus.contains("In Stock")) {
					stockStatus = "In Stock";
				}
				else if (stockStatus.contains("Out of Stock")) {
					stockStatus = "Out of Stock";
				}
				else if (stockStatus.contains("Pre-Order")) {
					stockStatus = "Pre-Order";
				}
				else {
					stockStatus = "Out of Print";
				}
				DATA_LIST.add(new String[]{currTitle.replaceAll("Volume", "Vol").replaceAll(" Manga| Edition", ""), priceTxt.trim(), stockStatus, "RightStufAnime"});

			}
		}

		if (pageCheck != null) {
			currPageNum++;
			GetRightStufAnimeData(bookTitle, bookType, memberStatus, currPageNum);
		} else {
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

			PrintWriter rightStufDataFile = new PrintWriter("src/TsundOkuApp/PriceAnalysis/Data/RightStufAnimeData.txt");
			if (!DATA_LIST.isEmpty()){
				for (String[] volumeData : DATA_LIST){
					rightStufDataFile.println(Arrays.toString(volumeData));
				}
			}
			else
			{
				rightStufDataFile.println(bookTitle + " " + bookType + " Does Not Exist at RightStufAnime\n");
			}
			rightStufDataFile.flush();
			rightStufDataFile.close();

			for (String link : RIGHT_STUF_ANIME_LINK) {
				System.out.println(link);
			}
			RIGHT_STUF_ANIME_LINK.clear();
		}

		return DATA_LIST;
	}

//	public static void main(String[] args) throws FileNotFoundException {
//		System.setProperty("webdriver.edge.driver", "resources/DriverExecutables/msedgedriver.exe");
//		GetRightStufAnimeData("One Piece", 'M', true, (byte) 1);
//	}
}
