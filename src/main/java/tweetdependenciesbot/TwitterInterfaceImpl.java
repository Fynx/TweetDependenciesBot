package tweetdependenciesbot;

import twitter4j.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class TwitterInterfaceImpl implements TwitterInterface
{

	private Twitter twitter;

	public TwitterInterfaceImpl() throws TwitterException, IOException
	{
		twitter = TwitterFactory.getSingleton();
	}

	private ResponseList<Status> currentResponseList;
	long currentId = 0;

	private ResponseList<Status> fetchTweets(Identity id) throws TwitterException
	{
		if (currentId != id.getId()) {
			currentResponseList = twitter.getUserTimeline(id.getId());
			currentId = id.getId();
		}
		return currentResponseList;
	}

	private String getTrimmedString(String s)
	{
		char cs[] = s.toCharArray();
		for (int i = 0; i < cs.length; ++i)
			if (cs[i] == '\'' || cs[i] == '\"')
				cs[i] = '.';
		return new String(cs);
	}

	/** Returns only up to 20 most recent tweets. */
	@Override
	public ArrayList<Tweet> getTweets(Identity id, int number) throws TwitterException
	{
		ResponseList<Status> res = fetchTweets(id);
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();

		for (Status s : res)
			if (!s.isRetweet() && tweets.size() < number)
				tweets.add(new Tweet(s.getId(), id.getId(), getTrimmedString(s.getText())));

		return tweets;
	}

	@Override
	public ArrayList<Tweet> getRetweets(Identity id, int number) throws TwitterException
	{
		ResponseList<Status> res = fetchTweets(id);
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();

		for (Status s : res)
			if (s.isRetweet() && tweets.size() < number)
				tweets.add(new Tweet(s.getId(), s.getRetweetedStatus().getUser().getId(), id.getId()));

		return tweets;
	}

	@Override
	public Identity getTweeter(Tweet t) throws TwitterException
	{
		return new Identity(twitter.showStatus(t.getId()).getUser().getId());
	}

	@Override
	public Identity getRetweeter(Tweet t) throws TwitterException
	{
		Status retweet = twitter.showStatus(t.getId()).getRetweetedStatus();
		if (retweet == null)
			return null;
		else
			return new Identity(retweet.getUser().getId());
	}

	@Override
	public int getRetweetsNumber(Tweet t) throws TwitterException
	{
		return twitter.showStatus(t.getId()).getRetweetCount();
	}

	@Override
	public ArrayList<Identity> getRandomIdentities(int number) throws TwitterException
	{
		/**
		 * The algorithm of finding the new identities is simple.
		 * Twitter Query enables to search with a given phrase, which may be any string possible.
		 * Why not use numbers then?
		 */
		Random random = new Random();
		int randomNumber = random.nextInt() % 5000;
		if (randomNumber < 0)
			randomNumber *= -1;

		Query query = new Query(String.valueOf(randomNumber));
		query.setCount(number);

		ArrayList<Identity> ids = new ArrayList<Identity>();
		QueryResult qr = twitter.search(query);

		for (Status tweet : qr.getTweets())
			ids.add(new Identity(tweet.getUser().getId(), tweet.getUser().getName()));

		return ids;
	}

	@Override
	public void retweet(Tweet t) throws TwitterException
	{
		if (t.getText() != null)
			System.out.println(String.format("Retwitting: @%d: %s", t.getSender(), t.getText()));
		//twitter.retweetStatus(t.getId());
	}
}