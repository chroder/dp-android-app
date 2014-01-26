package com.deskpro.mobile.models;

public class ApiSession
{
	private String apiToken;
	private String apiUrl;
	private String email;
	private String name;
	private String helpdeskUrl;
	private String cloudName;

	public ApiSession(String apiToken, String apiUrl, String email, String name, String helpdeskUrl, String cloudName)
	{
		this.apiToken = apiToken;
		this.apiUrl = apiUrl;
		this.email = email;
		this.name = name;
		this.helpdeskUrl = helpdeskUrl;
		this.cloudName = cloudName;
	}

	public String getApiToken()
	{
		return apiToken;
	}

	public String getEmail()
	{
		return email;
	}

	public String getName()
	{
		return name;
	}

	public String getApiUrl()
	{
		return apiUrl;
	}

	public String getHelpdeskUrl()
	{
		return helpdeskUrl;
	}

	public String getCloudName()
	{
		return cloudName;
	}
}
