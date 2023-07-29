package sdlc;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Testing the accuracy of generated results
 * @author Keegan
 */
public class wordCountTest {

	@Test
	/**
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	public void test() throws NumberFormatException, Exception {
		Main.main(null);
		int test1 = 0;
		int test2 = 0;
		for (int i = 0; i < Main.wordCount().length; i++) {
			if(Main.wordCount()[i][0].equals("my")) {
				test1 = i;
			}
			if(Main.wordCount()[i][0].equals("raven")) {
				test2 = i;
			}
			
		}
		
		assertEquals(Integer.valueOf(Main.wordCount()[test1][1]), 24);
		assertEquals(Integer.valueOf(Main.wordCount()[test2][1]), 10);
	}

}
