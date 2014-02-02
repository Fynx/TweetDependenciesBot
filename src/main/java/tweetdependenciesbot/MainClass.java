package tweetdependenciesbot;

import twitter4j.TwitterException;
import twitterlogger.Logger;

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
		if (args.length >= 1 && args[0].equals("log")) {
			Logger logger = new Logger();
			logger.log();
			return;
		}

		DependenciesDataBase dataBase;
		TwitterInterfaceImpl twitter;

		InputHandler inputHandler = new InputHandler();
		if (inputHandler.getUser().equals(""))
			System.out.println("No data no program.");

		System.out.println("Checking connection with database");
		dataBase = new DependenciesDataBase(inputHandler.getUser(), inputHandler.getPassword());

		System.out.println("Logging into Twitter");
		twitter  = new TwitterInterfaceImpl();

		System.out.println("\nSynchronizing...");
		dataBase.synchronize(twitter);
		System.out.println(String.format("done. (synchronization difference: %d)\n",
		                                 dataBase.getSynchronizationDifference()));
		System.out.println("\nUpdating...");
		dataBase.update(twitter);
		System.out.println("done.\n");

		System.out.println("Closing database.");
		dataBase.close();
	}

}
