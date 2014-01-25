package com.deskpro.mobile.dpapi.models.response;

import java.util.List;
import java.util.Map;

public class HttpResponse
{
	private int reponseCode;
	private String responseString;
	private Map<String, List<String>> responseHeaders;
	private String content;

	public HttpResponse(int reponseCode, String responseString, Map<String, List<String>> responseHeaders, String content)
	{
		this.responseHeaders = responseHeaders;
		this.reponseCode     = reponseCode;
		this.responseString  = responseString;
		this.content         = content;
	}

	public int getReponseCode()
	{
		return reponseCode;
	}

	public String getResponseString()
	{
		return responseString;
	}

	public Map<String, List<String>> getResponseHeaders()
	{
		return responseHeaders;
	}

	public String getContent()
	{
		return content;
	}
}
