package TsundOkuApp.PriceAnalysis;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

import static TsundOkuApp.PriceAnalysis.RobertsAnimeCornerStore.GetRobertsAnimeCornerStoreData;
import static TsundOkuApp.PriceAnalysis.RightStufAnime.GetRightStufAnimeData;
import static TsundOkuApp.PriceAnalysis.InStockTrades.GetInStockTradesData;

public class MasterAnalysis {
	private static ArrayList<String[]> inStockTradesData = new ArrayList<>();
	private static final ArrayList<ArrayList<String[]>> DATALIST_PIPELINE = new ArrayList<>();
	private static final String DEFAULT_TITLE_PATTERN = "@(\\d+$)";
	private static String bookTitle, websiteList;
	private static char bookType;

	private static ArrayList<String[]> PriceComparison(ArrayList<String[]> biggerList, ArrayList<String[]> smallerList){
		ArrayList<String[]> FinalData = new ArrayList<>();
		boolean sameVolumeCheck;
		int pos = 0;
		for (String[] biggerListData : biggerList)
		{
			sameVolumeCheck = false;
			if (pos != smallerList.size())
			{
				int getListOneVolNum = Integer.parseInt(biggerListData[0].substring(bookTitle.length()).replaceFirst(".*?(\\d+).*", "$1"));
				for (int y = pos; y < smallerList.size(); y++)
				{
					if (getListOneVolNum == Integer.parseInt(smallerList.get(y)[0].substring(bookTitle.length()).replaceFirst(".*?(\\d+).*", "$1")))
					{
						FinalData.add(Float.parseFloat(biggerListData[1].substring(1)) > Float.parseFloat(smallerList.get(y)[1].substring(1)) ? smallerList.get(y) : biggerListData);
						pos = y + 1;
						sameVolumeCheck = true;
					}
				}
				if (!sameVolumeCheck)
				{
					FinalData.add(biggerListData);
				}
			}
			else
			{
				FinalData.add(biggerListData);
			}
		}

		if (pos != smallerList.size()) // Smaller list has volumes that are not present in the bigger list and are volumes that have a volume # greater than the greatest volume # in the bigger list
		{
			for (int x = pos; x < smallerList.size(); x++){
				FinalData.add(smallerList.get(x));
			}
		}
		return FinalData;
	}

	private static Thread CreateRightStufAnimeThread(){
		return new Thread(() -> {
			try {
				DATALIST_PIPELINE.add(GetRightStufAnimeData(bookTitle, bookType, true, (byte) 1));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
	}

	private static Thread CreateRobertsAnimeCornerStoreThread(){
		return new Thread(() -> {
			try {
				DATALIST_PIPELINE.add(GetRobertsAnimeCornerStoreData(bookTitle, bookType));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
	}

	private static Thread CreateInStockTradesThread(){
		return new Thread(() -> {
			try {
				DATALIST_PIPELINE.add(GetInStockTradesData(bookTitle, bookType, (byte) 1));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
	}

	public static void main (String[] args) throws FileNotFoundException, InterruptedException {
		int processors = Runtime.getRuntime().availableProcessors();
		System.out.println(processors);
		System.setProperty("webdriver.edge.driver", "resources/DriverExecutables/msedgedriver.exe");

		Scanner userInput = new Scanner(System.in);
		System.out.print("What is the Manga/Light Novel Title: ");
		bookTitle = userInput.nextLine();

		System.out.print("Are u searching for a Manga (M) or Light Novel (N): ");
		bookType = userInput.next().charAt(0);

//		System.out.print("Are You a GotAnime Member? ->  ");
//		bookTitle = userInput.nextLine();

		final double startTime = System.currentTimeMillis();

		Thread rightStufAnimeThread = CreateRightStufAnimeThread();
		Thread robertsAnimeCornerStoreThread = CreateRobertsAnimeCornerStoreThread();
		Thread inStockTradesThread = CreateInStockTradesThread();
		rightStufAnimeThread.start();
		robertsAnimeCornerStoreThread.start();
		inStockTradesThread.start();
		rightStufAnimeThread.join();
		robertsAnimeCornerStoreThread.join();
		inStockTradesThread.join();

		DATALIST_PIPELINE.sort(Comparator.comparing(ArrayList::size));

		int pos = 0; // The position of the new lists of data after comparing
		int numListsOfData = DATALIST_PIPELINE.size();
		int threadCount = numListsOfData % 2 == 0 ? numListsOfData : numListsOfData - 1; // Tracks the "status" of the data lists that need to be compared, essentially tracks needed thread count
		Thread[] threadList; // Generates the threads for execution
		while (threadCount > 0) // While there is still 2 or more lists of data to compare prices
		{
			threadList = new Thread[threadCount / 2];
			for (int curThread = 0; curThread < threadList.length; curThread++) // Create all of the threads for processing
			{
				int finalX = curThread;
				int finalPos = pos;
				threadList[curThread] = new Thread(() -> DATALIST_PIPELINE.set(finalPos, PriceComparison(DATALIST_PIPELINE.get(finalX + 1), DATALIST_PIPELINE.get(finalX))));
				pos++;
			}

			for (Thread t : threadList){
				t.start();
				t.join();
			}
			threadCount -= 2;
			pos = 0;
		}

		if (numListsOfData % 2 != 0) // If the number of websites the user wants data from is odd do 1 more comparison
		{
			DATALIST_PIPELINE.set(0, PriceComparison(DATALIST_PIPELINE.get(numListsOfData - 1), DATALIST_PIPELINE.get(0)));
		}

		final double endTime = System.currentTimeMillis();
		System.out.println("Time in Seconds: " + ((endTime - startTime) / (double) 1000));

		PrintWriter masterDataFile = new PrintWriter("src/TsundOkuApp/PriceAnalysis/Data/MasterData.txt");
		for (String[] data : DATALIST_PIPELINE.get(0)){
			masterDataFile.println(Arrays.toString(data));
		}
		masterDataFile.flush();
		masterDataFile.close();
		DATALIST_PIPELINE.clear();
	}
}
