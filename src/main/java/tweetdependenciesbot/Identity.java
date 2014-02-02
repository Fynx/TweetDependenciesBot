package tweetdependenciesbot;

public class Identity
{
	public Identity(long id)
	{
		this.iid = id;
	}

	public Identity(long id, String name)
	{
		this.iid = id;
		this.name = name;
		popularity = 0;
	}

	public boolean equals(Identity id)
	{
		return iid == id.getId();
	}

	public long getId()
	{
		return iid;
	}

	private long iid;

	public String getName()
	{
		return name;
	}

	private String name;

	public void setPopularity(int p)
	{
		popularity = p;
	}

	public int getPopularity()
	{
		return popularity;
	}

	private int popularity;
}
