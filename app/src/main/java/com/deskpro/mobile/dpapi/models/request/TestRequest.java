package com.deskpro.mobile.dpapi.models.request;

import com.deskpro.mobile.dpapi.models.response.TestResponse;

public class TestRequest implements RequestModel
{
	@Override
	public String getEndpoint()
	{
		return "/test";
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

	@Override
	public Class getResponseModelClass()
	{
		return TestResponse.class;
	}
}
