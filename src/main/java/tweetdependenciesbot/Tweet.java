package tweetdependenciesbot;

public class Tweet
{
	public Tweet(long id)
	{
		this.id = id;
	}

	public Tweet(long id, long sender, String text)
	{
		this.id = id;
		this.sender = sender;
		this.text = text;
	}

	public Tweet(long id, long sender, long receiver)
	{
		this.id = id;
		this.sender = sender;
		this.receiver = receiver;
	}

	public boolean equals(Tweet t)
	{
		return t.getId() == getId();
	}

	public long getId()
	{
		return id;
	}

	public String getText()
	{
		return text;
	}

	public long getSender()
	{
		return sender;
	}

	public long getReceiver()
	{
		return receiver;
	}

	private long id;
	private long sender;
	private long receiver;
	private String text;
}
