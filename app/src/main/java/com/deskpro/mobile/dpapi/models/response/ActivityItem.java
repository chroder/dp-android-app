package com.deskpro.mobile.dpapi.models.response;

public class ActivityItem
{
	private int id;
	private String type;
	private String browserRendered;

	public ActivityItem(int id, String type, String browserRendered)
	{
		this.id = id;
		this.type = type;
		this.browserRendered = browserRendered;
	}

	public int getId()
	{
		return id;
	}

	public String getType()
	{
		return type;
	}

	public String getBrowserRendered()
	{
		return browserRendered;
	}
}
