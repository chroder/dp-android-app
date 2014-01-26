package com.deskpro.mobile.dpapi.models.response;

import java.util.LinkedList;

public class ActivityResponse
{
	private LinkedList<ActivityItem> activity;

	public ActivityResponse(LinkedList<ActivityItem> activity)
	{
		this.activity = activity;
	}

	public LinkedList<ActivityItem> getActivity()
	{
		return activity;
	}
}
