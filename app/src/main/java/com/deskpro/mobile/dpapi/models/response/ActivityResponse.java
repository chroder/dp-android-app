package com.deskpro.mobile.dpapi.models.response;

import java.util.LinkedList;

public class ActivityResponse
{
	private int lastId;
	private LinkedList<ActivityItem> alerts;

	public ActivityResponse(int lastId, LinkedList<ActivityItem> alerts)
	{
		this.lastId = lastId;
		this.alerts = alerts;
	}

	public int getLastId()
	{
		return lastId;
	}

	public LinkedList<ActivityItem> getAlerts()
	{
		return alerts;
	}
}
