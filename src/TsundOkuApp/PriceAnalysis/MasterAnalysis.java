package TsundOkuApp.PriceAnalysis;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import static TsundOkuApp.PriceAnalysis.BookDepository.GetBookDepositoryData;
import static TsundOkuApp.PriceAnalysis.RobertsAnimeCornerStore.GetRobertsAnimeCornerStoreData;
import static TsundOkuApp.PriceAnalysis.RightStufAnime.GetRightStufAnimeData;
import static TsundOkuApp.PriceAnalysis.InStockTrades.GetInStockTradesData;

public class MasterAnalysis {
	private static final ArrayList<ArrayList<String[]>> DATALIST_PIPELINE = new ArrayList<>();
	private static final ArrayList<Thread> WEBSITE_THREADS = new ArrayList<>();
	private static final ArrayList<PriceComparisonDataModel> COMPARED_DATA = new ArrayList<>();
	private static boolean gotAnimeMember = false;

	/**
	 * Modified On: 02 December 2021
	 *  by: Sean Njenga
	 * Description: Determine whether the two titles of the volume are similar or close enough to each other to be the same.
	 *              This is mainly because sometimes different websites display the title of the series differently even though they are the same.
	 * Parameters
	 *      titleOne | String | The first title to compare
	 *      titleTwo | String | The second title to compare
	 *      return | boolean | The resulting determination of whether the two titles are similar enough to compare prices
	 */
	private static boolean Similar(String titleOne, String titleTwo){
		int count = 0; // The amount of times that the characters and there "alignment" don't match
		int titleOnePointer = 0, titleTwoPointer = 0; // Pointers for the characters in both strings

		while (titleOnePointer < titleOne.length() && titleTwoPointer < titleTwo.length()){ // Keep traversing until u reach the end of titleOne's string
			//System.out.println(titleOnePointer + "(" + titleOne.charAt(titleOnePointer) + ")" + "\t" + titleTwoPointer + "(" + titleTwo.charAt(titleTwoPointer) + ")");
			if (titleOne.charAt(titleOnePointer) != titleTwo.charAt(titleTwoPointer)){ // Checks to see if the characters match
				int cache = titleOne.indexOf(titleOne.charAt(titleOnePointer));
				for (int z = cache; z < titleOne.length(); z++){ // Start at the index of where the characters were not the same, then traverse the other string to see if it
					//System.out.println(z + "(" + titleOne.charAt(z) + ")" + "\t" + titleTwoPointer + "(" + titleTwo.charAt(titleTwoPointer) + ")");
					titleOnePointer++;
					if (titleOne.charAt(z) == titleTwo.charAt(titleTwoPointer) && titleOne.charAt(z - 1) == titleTwo.charAt(titleTwoPointer - 1)){ // Checks to see if the character is present in the other string and is in a similar position
						break;
					}
				}
				count++; // There is 1 additional character difference
				titleOnePointer = cache;
				//System.out.println(count);
			} else { // Characters do match so just move to the next set of characters to compare in the strings
				titleOnePointer++;
			}
			titleTwoPointer++;
		}

		//System.out.println(count);
		return count <= (titleOne.length() > titleTwo.length() ? titleTwo.length() / 4 : titleOne.length() / 4); // Determine if they are similar enough by a threshold of 1/4 the size of 1 of the titles
	}

	/**
	 * Modified On: 02 December 2021
	 *  by: Sean Njenga
	 * Description: Compares the prices of all the volumes that the two websites both have, and outputs the resulting list containing
	 *              the lowest prices for each available volume between the websites. If one website does not have a volume that the other
	 *              does then that volumes data set defaults to the "smallest" and is added to the list.
	 * Parameters:
	 *      biggerList | String[] | The bigger list of data sets between the two websites
	 *      smallerList | String[] | The smaller list of data sets between the two websites
	 *      return | ArrayList<String[]> | The final list of data containing all available lowest price volumes between the two websites
	 */
	private static ArrayList<String[]> PriceComparison(ArrayList<String[]> biggerList, ArrayList<String[]> smallerList, String bookTitle){
		ArrayList<String[]> finalData = new ArrayList<>(); // The final list of data containing all available volumes for the series from the website with the lowest price
		boolean sameVolumeCheck;                            // Determines whether a match has been found where the 2 volumes are the same to compare prices for
		int pos = 0;                                       // The position of the next volume and then proceeding volumes to check if there is a volume to compare
		int getListOneVolNum;                              // The current vol number from the website with the bigger list of volumes that is being checked
		String[] smallerListData;                          // The current volume data set that is being compared against from the smaller data list

		for (String[] biggerListData : biggerList){
			sameVolumeCheck = false; // Reset the check to determine if two volumes with the same number has been found to false
			if (biggerListData[0].contains("Box Set")){
				getListOneVolNum = Integer.parseInt(biggerListData[0].substring(biggerListData[0].indexOf(" Box Set") + " Box Set".length()).replaceFirst(".*?(\\d+).*", "$1"));
			} else {
				getListOneVolNum = Integer.parseInt(biggerListData[0].substring(biggerListData[0].indexOf(" Vol") + " Vol".length()).replaceFirst(".*?(\\d+).*", "$1"));
			}
			if (pos != smallerList.size()) { // Only need to check for a comparison if there are still volumes to compare in the "smallerList"
				for (int y = pos; y < smallerList.size(); y++){ // Check every volume in the smaller list, skipping over volumes that have already been checked
					smallerListData = smallerList.get(y);
					// Check to see if the titles are the same and if not, if they are similar enough, if they aren't similar enough then go to the next volume
					if (!smallerListData[0].equals(biggerListData[0]) && !Similar(smallerListData[0], biggerListData[0])){
						continue;
					}

					// If the vol numbers are the same and the titles are similar or the same from the if check above, add the lowest price volume to the list".*?(\\d+).*", "$1"));
					if (getListOneVolNum == (biggerListData[0].contains("Box Set") ? Integer.parseInt(smallerListData[0].substring(smallerListData[0].indexOf(" Box Set") + " Box Set".length()).replaceFirst(".*?(\\d+).*", "$1")) : Integer.parseInt(smallerListData[0].substring(smallerListData[0].indexOf(" Vol") + " Vol".length()).replaceFirst(".*?(\\d+).*", "$1")))){
						finalData.add(Float.parseFloat(biggerListData[1].substring(1)) > Float.parseFloat(smallerListData[1].substring(1)) ? smallerListData : biggerListData); // Get the lowest price between the two then add the lowest dataset
						pos = y + 1; // Increment the position in which the next volumes to compare from the smaller list starts essentially "shrinking" the number of comparisons needed whenever a valid comparison is found by 1
						sameVolumeCheck = true;
						break;
					}
				}
			}

			if (!sameVolumeCheck){ // If the current volume number in the bigger list has no match in the smaller list (same volume number and name) then add it
				finalData.add(biggerListData);
			}
		}

		if (pos != smallerList.size()) // Smaller list has volumes that are not present in the bigger list and are volumes that have a volume # greater than the greatest volume # in the bigger list
		{
			for (int x = pos; x < smallerList.size(); x++){
				finalData.add(smallerList.get(x));
			}
		}
		return finalData;
	}

	/**
	 * Modified On: 02 December 2021
	 *  by: Sean Njenga
	 * Description: Creates the thread for getting data from RightStufAnime
	 * Parameters:
	 *      return | Thread | The thread that when executed gets data from RightStufAnime
	 */
	private static Thread CreateRightStufAnimeThread(String bookTitle, char bookType){
		return new Thread(() -> {
			try {
				DATALIST_PIPELINE.add(GetRightStufAnimeData(bookTitle, bookType, gotAnimeMember, (byte) 1));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Modified On: 02 December 2021
	 *  by: Sean Njenga
	 * Description: Creates the thread for getting data from RobertsAnimeCornerStore
	 * Parameters:
	 *      return | Thread | The thread that when executed gets data from RobertsAnimeCornerStore
	 */
	private static Thread CreateRobertsAnimeCornerStoreThread(String bookTitle, char bookType){
		return new Thread(() -> {
			try {
				DATALIST_PIPELINE.add(GetRobertsAnimeCornerStoreData(bookTitle, bookType));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Modified On: 02 December 2021
	 *  by: Sean Njenga
	 * Description: Creates the thread for getting data from InStockTrades
	 * Parameters:
	 *      return | Thread | The thread that when executed gets data from InStockTrades
	 */
	private static Thread CreateInStockTradesThread(String bookTitle, char bookType){
		return new Thread(() -> {
			try {
				DATALIST_PIPELINE.add(GetInStockTradesData(bookTitle, bookType, (byte) 1));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Modified On: 20 November 2021
	 *  by: Sean Njenga
	 * Description: Creates the thread for getting data from BookDepository
	 * Parameters:
	 *      return | Thread | The thread that when executed gets data from BookDepository
	 */
	private static Thread CreateBookDepositoryThread(String bookTitle, char bookType){
		return new Thread(() -> {
			try {
				DATALIST_PIPELINE.add(GetBookDepositoryData(bookTitle, bookType, (byte) 1));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Modified On: 20 November 2021
	 *  by: Sean Njenga
	 * Description: Starts the the threads from the website the user wants data from, then compares all the data
	 *              and outputs the final data to a final.
	 * Parameters:
	 *      throws | InterruptedException | Thrown when a thread is interrupted
	 *      throws | FileNotFoundException | Thrown when a file trying to be opened doesn't exist
	 */
	public static ArrayList<PriceComparisonDataModel> ComparePricing(String bookTitle, char bookType, ObservableList<String> websiteList) throws InterruptedException, FileNotFoundException {
		COMPARED_DATA.clear();
		System.setProperty("webdriver.edge.driver", "resources/DriverExecutables/msedgedriver.exe");
		final double startTime = System.currentTimeMillis();

		for (String website : websiteList){
			switch(website){
				case "RS" -> WEBSITE_THREADS.add(CreateRightStufAnimeThread(bookTitle, bookType));
				case "R" -> WEBSITE_THREADS.add(CreateRobertsAnimeCornerStoreThread(bookTitle, bookType));
				case "IST" -> WEBSITE_THREADS.add(CreateInStockTradesThread(bookTitle, bookType));
			}
		}

		for (Thread webThread : WEBSITE_THREADS){
			webThread.start();
		}
		for (Thread webThread : WEBSITE_THREADS){
			webThread.join();
		}
		WEBSITE_THREADS.clear();

		DATALIST_PIPELINE.removeIf(List::isEmpty);
		DATALIST_PIPELINE.sort(Comparator.comparing(List::size)); // Sort the list of data sets from the websites by size

		// Need to add better scheduling later
		int pos = 0; // The position of the new lists of data after comparing
		int numListsOfData = DATALIST_PIPELINE.size();
		int threadCount = numListsOfData % 2 == 0 ? numListsOfData : numListsOfData - 1; // Tracks the "status" of the data lists that need to be compared, essentially tracks needed thread count
		Thread[] threadList; // Generates the threads for execution
		while (threadCount > 0) // While there is still 2 or more lists of data to compare prices
		{
			threadList = new Thread[threadCount / 2];
			for (int curThread = 0; curThread < threadList.length; curThread++) { // Create all of the threads for processing
				int finalX = curThread;
				int finalPos = pos;
				threadList[curThread] = new Thread(() -> DATALIST_PIPELINE.set(finalPos, PriceComparison(DATALIST_PIPELINE.get(finalX + 1), DATALIST_PIPELINE.get(finalX), bookTitle)));
				pos++;
			}

			for (Thread t : threadList) {
				t.start();
				t.join();
			}
			threadCount -= 2;
			pos = 0;
		}

		if (numListsOfData % 2 != 0 && numListsOfData > 1) { // If the number of websites the user wants data from is odd do 1 more comparison
			DATALIST_PIPELINE.set(0, PriceComparison(DATALIST_PIPELINE.get(numListsOfData - 1), DATALIST_PIPELINE.get(0), bookTitle));
		}

		final double endTime = System.currentTimeMillis();
		System.out.println("Time in Seconds: " + ((endTime - startTime) / (double) 1000));

		PrintWriter masterDataFile = new PrintWriter("src/TsundOkuApp/PriceAnalysis/Data/MasterData.txt");
		if (!DATALIST_PIPELINE.get(0).isEmpty()){
			for (String[] data : DATALIST_PIPELINE.get(0)){
				masterDataFile.println(Arrays.toString(data));
				COMPARED_DATA.add(new PriceComparisonDataModel(new SimpleStringProperty(data[0]), new SimpleStringProperty(data[1]), new SimpleStringProperty(data[2]), new SimpleStringProperty(data[3])));
				System.out.println(Arrays.toString(data));
			}
			masterDataFile.flush();
			masterDataFile.close();
		}

		for (int x = 0; x < DATALIST_PIPELINE.size(); x++){
			DATALIST_PIPELINE.get(x).clear();
		}

		return COMPARED_DATA;
	}

	public static void main (String[] args) throws InterruptedException, FileNotFoundException {
//		int processors = Runtime.getRuntime().availableProcessors();
//		System.out.println(processors);

//		Scanner userInput = new Scanner(System.in);
//		System.out.print("What is the Manga/Light Novel Title: ");
//		String bookTitle = userInput.nextLine();
//
//		System.out.print("Are u searching for a Manga (M) or Light Novel (N): ");
//		char bookType = userInput.nextLine().charAt(0);
//
//		System.out.print("Are You a GotAnime Member Yes (Y) or No (N): ");
//		gotAnimeMember = userInput.nextLine().charAt(0) == 'Y';
//
//		ComparePricing(bookTitle, bookType);

//		Similar("One Piece Vol 2","One Piece Box Set 2");
	}
}
