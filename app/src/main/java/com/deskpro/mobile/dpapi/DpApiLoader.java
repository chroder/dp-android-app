package com.deskpro.mobile.dpapi;

import android.util.Log;

import com.deskpro.mobile.dpapi.models.request.RequestModel;
import com.deskpro.mobile.dpapi.models.response.HttpResponse;
import com.google.gson.Gson;

import java.io.IOException;

public class DpApiLoader<T>
{
	private DpApi api;
	private HttpResponse httpResponse;
	private RequestModel requestModel;
	private Class<T> responseClass;
	private T responseModel;

	public DpApiLoader(Class<T> responseClass, DpApi api, RequestModel requestModel)
	{
		this.api = api;
		this.requestModel = requestModel;
		this.responseClass = responseClass;
	}

	public RequestModel getRequestModel()
	{
		return requestModel;
	}

	public HttpResponse getHttpResponse() throws IOException
	{
		if (httpResponse == null) {
			httpResponse = api.sendRequest(requestModel);
		}

		return httpResponse;
	}

	public T getResponseModel() throws IOException
	{
		if (responseModel == null) {
			HttpResponse response = getHttpResponse();
			Gson gson = api.getGson();
			responseModel = gson.fromJson(response.getContent(), responseClass);
		}

		return responseModel;
	}
}
