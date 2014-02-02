package twitterlogger;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;
import java.util.Scanner;

public class Logger
{
	private static String CONSUMER_KEY;
	private static String CONSUMER_SECRET;

	/** This function uses the example from the web. To be more specific: exactly everywhere the same one. */
	public static void main(String argv[]) throws TwitterException, IOException
	{
		System.out.println("Enter consumer key:");
		Scanner in = new Scanner(System.in);
		CONSUMER_KEY = in.nextLine();
		System.out.println("Enter consumer secret:");
		CONSUMER_SECRET = in.nextLine();

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
			.setOAuthConsumerKey(CONSUMER_KEY)
			.setOAuthConsumerSecret(CONSUMER_SECRET);

		TwitterFactory tf = new TwitterFactory(cb.build());

		twitter = tf.getInstance();

		RequestToken requestToken = twitter.getOAuthRequestToken();
		System.out.println("Got request token.");
		System.out.println("Request token: " + requestToken.getToken());
		System.out.println("Request token secret: " + requestToken.getTokenSecret());
		AccessToken accessToken = null;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (null == accessToken) {
			System.out.println("Open the following URL and grant access to your account:");
			System.out.println(requestToken.getAuthorizationURL());
			System.out.print("Enter the PIN(if available) and hit enter after you granted access.[PIN]:");
			String pin = br.readLine();

			if (pin.length() > 0)
				accessToken = twitter.getOAuthAccessToken(requestToken, pin);
			else
				accessToken = twitter.getOAuthAccessToken(requestToken);
		}
		System.out.println("Got access token.");
		System.out.println("Access token: " + accessToken.getToken());
		System.out.println("Access token secret: " + accessToken.getTokenSecret());

		if (!twitter.getAuthorization().isEnabled()) {
			System.out.println("OAuth consumer key/secret is not set.");
			System.exit(-1);
		}

		/** Saving to file */

		String path = System.getProperty("user.dir");
		PrintWriter out = new PrintWriter(path + "/twitter4j.properties");

		out.println("oauth.consumerKey=" + CONSUMER_KEY);
		out.println("oauth.consumerSecret=" + CONSUMER_SECRET);
		out.println("oauth.accessToken=" + accessToken.getToken());
		out.println("oauth.accessTokenSecret=" + accessToken.getTokenSecret());

		out.close();

		System.out.println("Properties saved.");
	}

	private static Twitter twitter;
}
