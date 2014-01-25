package com.deskpro.mobile.models.api;

public class TestModel
{
	private int apiVersion;
	private String apiUrl;

	public int getApiVersion()
	{
		return apiVersion;
	}

	public String getApiUrl()
	{
		return apiUrl;
	}

	@Override
	public String toString ()
	{
		return "TestModel{" +
				"apiVersion=" + apiVersion +
				", apiUrl='" + apiUrl + '\'' +
				'}';
	}
}
