package com.deskpro.mobile.models;

public class ApiSession
{
	private String apiToken;
	private String email;
	private String apiUrl;
	private String helpdeskUrl;
	private String cloudName;

	public ApiSession(String apiToken, String email, String apiUrl, String helpdeskUrl, String cloudName)
	{
		this.apiToken = apiToken;
		this.email = email;
		this.apiUrl = apiUrl;
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
