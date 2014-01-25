package com.deskpro.mobile.dpapi.models.request;

public interface RequestModel
{
	/**
	 * Get the URL endpoint
	 * @return The URL
	 */
	public String getEndpoint();

	/**
	 * Get the request method
	 * @return GET, POST, PUT, DELETE
	 */
	public String getMethod();

	/**
	 * Encodes the model to JSON
	 * @return The JSON encoded data
	 */
	public String getJson();

	/**
	 * Encodes additional data to send in the query string
	 * @return Query string data
	 */
	public String getQueryString();

	/**
	 * Get the response model class
	 * @return
	 */
	public Class getResponseModelClass();
}
