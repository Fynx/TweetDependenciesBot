package tweetdependenciesbot;

import twitter4j.TwitterException;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DependenciesDataBase extends DependenciesBase
{
	public final int NAMES_IN_BASE_NUMBER = 5;

	public DependenciesDataBase(String user, String password) throws SQLException, ClassNotFoundException, IOException
	{
		this.dataBase = new DataBase(user, password);
	}

	/** This function must be called before the end of the program. */
	public void close() throws SQLException
	{
		dataBase.close();
	}

	@Override
	public ArrayList<Identity> getIdentitiesInBase() throws SQLException
	{
		ResultSet res =
			dataBase.query("SELECT Base.iid, nick FROM Base JOIN Identity ON Base.iid = Identity.iid");
		ArrayList<Identity> ids = new ArrayList<Identity>();

		while (res.next())
			ids.add(new Identity(res.getLong(1), res.getString(2)));

		return ids;
	}

	@Override
	public ArrayList<Tweet> getTweets(Identity id) throws SQLException
	{
		ResultSet res = dataBase.query("SELECT tid, sender, text FROM Tweet WHERE sender = " + id.getId());
		ArrayList<Tweet> ts = new ArrayList<Tweet>();

		while (res.next())
			ts.add(new Tweet(res.getLong(1), res.getLong(2), res.getString(3)));
		return ts;
	}

	@Override
	public ArrayList<Tweet> getRetweets(Identity id) throws SQLException
	{
		ResultSet res = dataBase.query("SELECT tid, sender, receiver FROM Retweet WHERE receiver = "
		                               + id.getId());
		ArrayList<Tweet> rts = new ArrayList<Tweet>();

		while (res.next())
			rts.add(new Tweet(res.getLong(1), res.getLong(2), res.getLong(3)));
		return rts;
	}

	/**
	 * Synchronizes the database with Twitter. The number of tweets created by the identities from the base is
	 * recorded as the difference from the last synchronization.
	 */
	public void synchronize(TwitterInterface twitter) throws SQLException, TwitterException
	{
		ArrayList<Identity> ids = getIdentitiesInBase();
		for (Identity id : ids) {
			System.out.println("Finding tweets...");
			ArrayList<Tweet> ts  = getTweets(id);
			ArrayList<Tweet> rts = getRetweets(id);

			System.out.println("Fetching tweets...");
			ArrayList<Tweet> newts = twitter.getTweets(id, TWEETS_MEMORY_SIZE);
			ArrayList<Tweet> newrts = twitter.getRetweets(id, TWEETS_MEMORY_SIZE);

			for (Tweet t : newts)
				System.out.println(String.valueOf(t.getSender()) + ": " + t.getText());
			System.out.println();

			for (Tweet newt : newts) {
				ResultSet tweetInBase = dataBase.query("SELECT COUNT(*) FROM Tweet WHERE tid = "
				                                       + newt.getId());
				boolean tweetExists = false;
				while (tweetInBase.next())
					if (tweetInBase.getInt(1) > 0)
						tweetExists = true;
				if (!ts.contains(newt) && !tweetExists) {
					twitter.retweet(newt);
					dataBase.insert(String
						.format("INSERT INTO Tweet (tid, sender, text) VALUES (%d, %d, '%s')",
						        newt.getId(),
						        newt.getSender(),
						        getTrimmedString(newt.getText())));
					++synchronizationDifference;
				}
			}

			for (Tweet newrt : newrts) {
				ResultSet tweetInBase = dataBase.query("SELECT COUNT(*) FROM Retweet WHERE tid = "
					+ newrt.getId());
				boolean tweetExists = false;
				while (tweetInBase.next())
					if (tweetInBase.getInt(1) > 0)
						tweetExists = true;

				if (!rts.contains(newrt) && !tweetExists) {
					twitter.retweet(newrt);
					dataBase.insert(String
						.format("INSERT INTO Retweet (tid, sender, receiver)"
							+ " VALUES (%d, %d, %d)",
						        newrt.getId(),
						        newrt.getSender(),
						        newrt.getReceiver()));
					++synchronizationDifference;
				}
			}
		}
	}

	/**
	 * Updates the database according to statistics from Twitter. If synchronization provided more changes to
	 * database that it is allowed, then the identities' base change. The worst identity is put out and the next one
	 * is put in its place.
	 */
	public void update(TwitterInterface twitter) throws SQLException, IOException, TwitterException
	{
		ArrayList<Identity> ids = getIdentitiesInBase();

		while (ids.size() > NAMES_IN_BASE_NUMBER)
			removeIdentityFromTheBase(getWorstIdentity(twitter));

		if (ids.size() == NAMES_IN_BASE_NUMBER && getSynchronizationDifference() > TWEET_UPDATE_COUNT) {
			synchronizationDifference = 0;
			removeIdentityFromTheBase(getWorstIdentity(twitter));
			ids = getIdentitiesInBase();
		}

		candidates = new ArrayList<Identity>();

		while (ids.size() < NAMES_IN_BASE_NUMBER) {
			Identity candidate = null;
			do {
				try {
					candidate = getCandidate(twitter, candidate);
				} catch (CandidatesExploitedException e) {
					e.printStackTrace();
					dataBase.clear();
				}
			} while (isIdentityRemembered(candidate) || candidate == null);
			addIdentityToTheBase(candidate);
			ids.add(candidate);
		}
	}

	private final int GLOBAL_MULT = 1;
	private final int LOCAL_MULT  = 3;

	/**
	 * Returns the value of the identity which is:
	 * GLOBAL_MULT * number of retweets of all tweets +
	 * LOCAL_MULT * number of retweets by identities in base of all tweets,
	 * where GLOBAL_MULT and LOCAL_MULT are constants.
	 */
	private int getIdentityValue(TwitterInterface twitter, Identity id) throws SQLException, TwitterException
	{
		ResultSet tweets = dataBase.query("SELECT tid FROM Tweet WHERE sender = " + id.getId());

		int globalValue = 0;
		while (tweets.next()) {
			long tid = tweets.getLong(1);
			globalValue += twitter.getRetweetsNumber(new Tweet(tid));
		}

		ResultSet rtcnt = dataBase.query("SELECT COUNT(*) FROM Retweet WHERE sender = " + id.getId());
		int localValue = 0;

		if (rtcnt.next())
			localValue = rtcnt.getInt(1);

		ResultSet tcnt  = dataBase.query("SELECT receiver FROM Retweet WHERE sender = " + id.getId());

		while (tcnt.next()) {
			long rid = tweets.getLong(1);
			if (getIdentitiesInBase().contains(new Identity(rid)))
				++localValue;
		}

		return LOCAL_MULT * localValue + GLOBAL_MULT * globalValue;
	}

	/** Returns currently the worst identity in the identities' base. */
	private Identity getWorstIdentity(TwitterInterface twitter) throws SQLException, TwitterException
	{
		ArrayList<Identity> ids = getIdentitiesInBase();
		for (Identity id : ids)
			id.setPopularity(getIdentityValue(twitter, id));

		Identity worst = ids.get(0);
		for (Identity id : ids)
			if (id.getPopularity() < worst.getPopularity())
				worst = id;
		return worst;
	}

	private final int CANDIDATES_MAX_SIZE = 20;
	ArrayList<Identity> candidates;

	private Identity getCandidate(TwitterInterface twitter, Identity id)
		throws CandidatesExploitedException, SQLException, TwitterException
	{
		if (candidates.isEmpty()) {
			ArrayList<Identity> cnds = new ArrayList<Identity>();
			for (Identity i : getIdentitiesInBase()) {
				for (Tweet t : getTweets(i)) {
					Identity retweeter = twitter.getRetweeter(t);
					if (retweeter != null)
						cnds.add(twitter.getRetweeter(t));
				}
				for (Tweet t : getRetweets(i))
					cnds.add(twitter.getTweeter(t));
			}

			for (Identity cnd : cnds)
				if (!candidates.contains(cnd) && candidates.size() < CANDIDATES_MAX_SIZE)
					candidates.add(cnd);

			System.out.println("Adding random candidates.");
			if (candidates.size() < CANDIDATES_MAX_SIZE) {
				ArrayList<Identity> random =
					twitter.getRandomIdentities(CANDIDATES_MAX_SIZE - candidates.size());
				for (Identity cnd : random)
					candidates.add(cnd);
			}
		}

		if (candidates.isEmpty())
			throw new CandidatesExploitedException();

		if (id == null)
			return candidates.get(0);

		id.setPopularity(getIdentityValue(twitter, id));
		boolean idOccurred = false;
		for (Identity cand : candidates) {
			if (cand.equals(id))
				idOccurred = true;
			else if (idOccurred && cand != null)
				return cand;
		}
		throw new CandidatesExploitedException();
	}

	/** Adds the identity to the identities' base. */
	private void addIdentityToTheBase(Identity id) throws SQLException
	{
		System.out.println("Adding identity " + id.getName());
		dataBase.insert(String.format("INSERT INTO Identity (iid, nick) VALUES (%d, '%s')",
		                              id.getId(), getTrimmedString(id.getName())));
		dataBase.insert(String.format("INSERT INTO Base (iid) VALUES (%d)", id.getId()));
	}

	/** Removes the identity from the identities' base. */
	private void removeIdentityFromTheBase(Identity id) throws SQLException
	{
		System.out.println("Removing identity " + id.getName());
		dataBase.insert(String.format("DELETE FROM Base WHERE iid = %d", id.getId()));
	}

	/** Returns true if the identity ever was in identities' base. */
	private boolean isIdentityRemembered(Identity id) throws SQLException
	{
		ResultSet res = dataBase.query("SELECT COUNT(*) FROM Base WHERE iid = " + id.getId());
		while (res.next())
			return res.getInt(1) > 0;
		return false;
	}

	/** Database class that provides the necessary data. */
	private DataBase dataBase;

	/** Number of tweets by which the versions of database before and now differ. */
	private int synchronizationDifference = 0;

	public int getSynchronizationDifference()
	{
		return synchronizationDifference;
	}

	private String getTrimmedString(String s)
	{
		char cs[] = s.toCharArray();
		for (int i = 0; i < cs.length; ++i)
			if (cs[i] == '\'' || cs[i] == '\"')
				cs[i] = '.';
		return new String(cs);
	}

	/** Exceptions */

	private class CandidatesExploitedException extends Exception
	{
		CandidatesExploitedException()
		{
			super("CandidatesExploitedException");
		}
	}
}
