package tweetdependenciesbot;

import twitter4j.TwitterException;

import java.util.ArrayList;

public interface TwitterInterface
{
	/** Returns the last tweets produced by the given identity. */
	public abstract ArrayList<Tweet> getTweets(Identity id, int number) throws TwitterException;
	/** Returns the last retweets of the given identity. */
	public abstract ArrayList<Tweet> getRetweets(Identity id, int number) throws TwitterException;
	/** Returns the identity that tweeted the tweet. */
	public abstract Identity getTweeter(Tweet t) throws TwitterException;
	/** Returns the identities that retweeted the given tweet. */
	public abstract Identity getRetweeter(Tweet t) throws TwitterException;
	/** Returns the total number of retweets for the tweet. */
	public abstract int getRetweetsNumber(Tweet t) throws TwitterException;
	/** Returns random identities. */
	public ArrayList<Identity> getRandomIdentities(int number) throws TwitterException;

	/** Retweets the tweet as the logged identity. */
	public abstract void retweet(Tweet t) throws TwitterException;
}
