package tweetdependenciesbot;

import java.sql.SQLException;
import java.util.ArrayList;

public abstract class DependenciesBase
{
	public final int TWEETS_MEMORY_SIZE = 5;
	public final int TWEET_UPDATE_COUNT = TWEETS_MEMORY_SIZE;

	/** Returns the identities currently in base. */
	public abstract ArrayList<Identity> getIdentitiesInBase() throws SQLException;
	/** Returns the tweets of the identity. */
	public abstract ArrayList<Tweet> getTweets(Identity id) throws SQLException;
	/** Returns the retweets of the identity. */
	public abstract ArrayList<Tweet> getRetweets(Identity id) throws SQLException;
}
