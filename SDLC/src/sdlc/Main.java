package sdlc;

/**
 * Importing needed packages
 */
import java.io.File;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;

/**
 * This is a program for taking a Poem and analyzing how many times words occur
 * @author Keegan
 *
 */
public class Main {

	/**
	 * Declaring connection variable that will be used throughout the code to connect to the database
	 */
	public static Connection con;
	
	/**
	 * Main method for running the wordCount function where all the computing is done
	 * @param arg
	 * @throws IOException
	 */
	public static void main(String[] arg) throws Exception {
		
		/**
		 * Initializing connection variable with single instance of connection rather then connecting each time
		 */
		con = getConnection();
		
		
		wordCount();
		get();
	}
	
	/**
	 * Main function that analyzes and sorts the word counts based on times they occur
	 * @return 2D Array
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	public static String[][] wordCount() throws NumberFormatException, Exception {
		
		/**
		 * Initializing required variables
		 */
		File poem = new File("C:\\Users\\Keega\\Desktop\\School\\Software Development\\SDLC\\Poem.htm");
		BufferedReader fileScanner = new BufferedReader(new FileReader(poem));
		
		boolean start = false;
		boolean end = false;
		String startTxt = "<h1>The Raven</h1>";
		String endTxt = "*** END OF THE PROJECT GUTENBERG EBOOK THE RAVEN ***";
		
		String data;
		String results = "";

		/**
		 * While loop to store words to string based on start and ending of poem
		 */
		while ((data = fileScanner.readLine()) != null && end == false) {
			if(start == false && (data.indexOf(startTxt) != -1)) {
				start = true;
			}
			else if (end == false && (data.indexOf(endTxt) != -1)) {
				end = true;
			}
			if(start == true && end == false) {
				if(!data.equals("")) {
					results += data.toLowerCase();
				}
			}
		}
		fileScanner.close();
		
		/**
		 * Initializing string builder to be able to remove all tags used in html code
		 */
		StringBuilder resultEdit = new StringBuilder(results);
		int removeStart = 0;
		int removeEnd = 0;
		
		/**
		 * While loop to remove all characters between "<>"
		 */
		while ((removeStart != -1) && (removeEnd != -1)) {
			removeStart = resultEdit.indexOf("<");
			removeEnd = resultEdit.indexOf(">");
			if ((removeStart != -1) && (removeEnd != -1)) {
				resultEdit.delete(removeStart, removeEnd+1);
			}
		}
		/**
		 * Removing specific excess symbols to make string better to read 
		 */
		results = resultEdit.toString();
		results = results.replaceAll("â", "");
		results = results.replaceAll("€", "");
		results = results.replaceAll("™", "");
		results = results.replaceAll("œ", "");
		results = results.replaceAll("&mdash", "");
		results = results.replaceAll(",", " ");
		results = results.replaceAll(";", " ");
		results = results.replaceAll("!", "");
		results = results.replaceAll("\\?", " ");
		results = results.replaceAll("\\.", " ");
		results = results.replaceAll("  ", " ");
		
		/**
		 * Separating all words to different indexes for cataloging
		 */
		String splitResults[] = results.split(" ");
		/**
		 * Defining 2 different array lists for words and word count to be stored
		 */
		ArrayList<String> word = new ArrayList<String>();
		ArrayList<Integer> wordCount = new ArrayList<Integer>();
		/**
		 * Initializing array list
		 */
		word.add(splitResults[0]);
		wordCount.add(1);
		
		/**
		 * Going through words and determining if word is already cataloged or if it needs to be added
		 */
		String temp = "";
		boolean match = false;
		int w = 0;
		for (int i = 0; i < splitResults.length; i++) {
			if(splitResults[i].matches("[a-zA-Z]+")) {
				temp = splitResults[i];
				while (match == false && w < word.size()) {
					if(word.get(w).equals(temp)) {
						match = true;
					}
					else {
						w++;
					}
				}
				if(match == true) {
					wordCount.set(w, wordCount.get(w)+1);
					match = false;
				}
				else {
					word.add(temp);
					wordCount.add(1);
				}
				w = 0;
			}
		}
		
		/**
		 * Sorting words based on word count
		 */
		int tempCount = 0;
		for (int i = 1; i < word.size(); i++) {
			for (int r = 0; r < i; r++) {
				if(wordCount.get(i) > wordCount.get(r)) {
					temp = word.get(r);
					tempCount = wordCount.get(r);
					
					word.set(r, word.get(i));
					wordCount.set(r, wordCount.get(i));
					
					word.set(i, temp);
					wordCount.set(i, tempCount);
				}
			}
		}
		String[][] finalCount = new String[word.size()][2];
		
		/**
		 * Putting both arrays into 2d array
		 */
		for (int i = 0; i < word.size(); i++) {
			finalCount[i][0] = word.get(i);
			finalCount[i][1] = wordCount.get(i).toString();
		}

		/**
		 * Putting Sorted words in database
		 */
		for (int i = 0; i < word.size(); i++) {
			insert(finalCount[i][0], Integer.parseInt(finalCount[i][1]));	
		}
		
		return finalCount;
	}
	
	
	
	/**
	 * Displays all items in database
	 * @throws Exception
	 */
	public static void get() throws Exception{
		try{  
			PreparedStatement statement = con.prepareStatement("SELECT wordID, wordValue FROM word");
			ResultSet result = statement.executeQuery();
			while(result.next()) {
				System.out.print(result.getString("wordID"));
				System.out.print(" ");
				System.out.println(result.getInt("wordValue") );
			}
			} catch(Exception e){ System.out.println(e);}  
	}
	
	/**
	 * Inserts given word and value into database
	 * @param word
	 * @param number
	 * @throws Exception
	 */
	public static void insert(String word, int number) throws Exception{
		try{  
			PreparedStatement statement = con.prepareStatement("INSERT INTO word (wordID, wordValue) VALUES ('" + word + "', " + number +");");
			statement.executeUpdate();
			
			} catch(Exception e){ System.out.println(e);}  
	} 
	
	/**
	 * Gets connection for database
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection() throws Exception{
		try{  
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/word occurrences";
			String username = "root";
			String password = "root";
			Class.forName(driver);
			
			Connection conn = DriverManager.getConnection(url, username, password);
			System.out.println("Connected");
			return conn;
			} catch(Exception e){ System.out.println(e);}  
		
		return null;
	} 
}