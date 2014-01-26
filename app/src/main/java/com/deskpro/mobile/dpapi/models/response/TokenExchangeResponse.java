package com.deskpro.mobile.dpapi.models.response;

public class TokenExchangeResponse
{
	private String apiToken;
	private String apiUrl;
	private HelpdeskInfo helpdeskInfo;
	private PersonInfo personInfo;

	public String getApiToken()
	{
		return apiToken;
	}

	public String getApiUrl()
	{
		return apiUrl;
	}

	public HelpdeskInfo getHelpdeskInfo()
	{
		return helpdeskInfo;
	}

	public PersonInfo getPersonInfo()
	{
		return personInfo;
	}

	public static class HelpdeskInfo
	{
		private String url;

		public String getUrl()
		{
			return url;
		}
	}

	public static class PersonInfo
	{
		private int id;
		private String pictureUrl;
		private boolean isAgent;
		private String name;
		private String primaryEmail;

		public int getId()
		{
			return id;
		}

		public String getPictureUrl()
		{
			return pictureUrl;
		}

		public boolean isAgent()
		{
			return isAgent;
		}

		public String getName()
		{
			return name;
		}

		public String getPrimaryEmail()
		{
			return primaryEmail;
		}
	}
}
