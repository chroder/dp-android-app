package com.deskpro.mobile.dpapi.models.response;

public class TestResponse
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
