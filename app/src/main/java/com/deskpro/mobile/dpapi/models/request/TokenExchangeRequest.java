package com.deskpro.mobile.dpapi.models.request;

import com.deskpro.mobile.dpapi.models.response.TokenExchangeResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class TokenExchangeRequest implements RequestModel
{
	private String user;
	private String password;

	public TokenExchangeRequest(String user, String password)
	{
		this.user = user;
		this.password = password;
	}

	@Override
	public String getEndpoint()
	{
		return "/token-exchange";
	}

	@Override
	public String getMethod()
	{
		return "POST";
	}

	@Override
	public String getJson()
	{
		JsonObject obj = new JsonObject();
		obj.addProperty("email", user);
		obj.addProperty("password", password);
		obj.addProperty("return_info", true);

		return new Gson().toJson(obj);
	}

	@Override
	public String getQueryString()
	{
		return null;
	}

	@Override
	public Class getResponseModelClass()
	{
		return TokenExchangeResponse.class;
	}
}
