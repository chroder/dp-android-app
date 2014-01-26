package com.deskpro.mobile.dpapi.models.request;

/**
 * Created by chroder on 26/01/2014.
 */
public class ActivityRequest implements RequestModel
{
	private int lastId;

	public ActivityRequest(int lastId)
	{
		this.lastId = lastId;
	}

	public int getLastId()
	{
		return lastId;
	}

	@Override
	public String getEndpoint()
	{
		return "/activity/" + String.valueOf(lastId);
	}

	@Override
	public String getMethod()
	{
		return "GET";
	}

	@Override
	public String getJson()
	{
		return null;
	}

	@Override
	public String getQueryString()
	{
		return null;
	}
}
