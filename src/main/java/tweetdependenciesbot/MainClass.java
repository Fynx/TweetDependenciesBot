package tweetdependenciesbot;

import twitter4j.TwitterException;

import java.io.IOException;
import java.sql.SQLException;

public class MainClass
{
	/**
	 * Connects with the database, Twitter (with graphical interface gently asking for a password). Synchronizes
	 * the database with the Twitter account and updates the data.
	 */
	public static void main(String args[]) throws SQLException, ClassNotFoundException, IOException, TwitterException
	{
		DependenciesDataBase dataBase;
		TwitterInterfaceImpl twitter;

		/*InputHandler inputHandler = new InputHandler();
		if (inputHandler.getUser().equals(""))
			System.out.println("No data no program.");*/
		Blabla inputHandler = new Blabla();

		dataBase = new DependenciesDataBase(inputHandler.getUser(), inputHandler.getPassword());
		twitter  = new TwitterInterfaceImpl();

		System.out.println("Synchronizing...");
		dataBase.synchronize(twitter);
		System.out.println(String.format("done. (synchronization difference: %d)\n",
		                                 dataBase.getSynchronizationDifference()));
		System.out.println("Updating...");
		dataBase.update(twitter);
		System.out.println("done.\n");

		System.out.println("Closing database.");
		dataBase.close();
	}

	static class Blabla {
		public static String getPassword() {return "";}
		public static String getUser() {return "root";}
	}

}
