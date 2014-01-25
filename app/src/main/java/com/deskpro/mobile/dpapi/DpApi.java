package com.deskpro.mobile.dpapi;

import android.util.Log;

import com.deskpro.mobile.dpapi.models.request.RequestModel;
import com.deskpro.mobile.dpapi.models.response.HttpResponse;
import com.deskpro.mobile.util.Strings;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class DpApi
{
	public static final String TAG = DpApi.class.getSimpleName();

	/**
	 * The root API url. This should include the '/api/' bit.
	 * Example: http://example.com/index.php/api/
	 */
	private String url;

	/**
	 * API key
	 */
	private	String apiKey;

	/**
	 * Used with the apiKey to perform actions in the context of someone else.
	 */
	private int agentId;

	/**
	 * API token (used instead of an apiKey)
	 */
	private String apiToken;

	/**
	 * Number of times to retry failed API calls.
	 */
	private int retryLimit = 3;

	/**
	 * Time to wait between retries
	 */
	private int retryWaitTime = 100;

	/**
	 * The client we use to open connections
	 */
	private OkHttpClient httpClient;

	/**
	 * GSON loader
	 */
	private Gson gson;


	/**
	 * Create a new DpApi instance with anonymous access credentials.
	 * Use this to access public API methods, including negotiating for a API token.
	 *
	 * @param url  The root API url
	 * @return
	 */
	public static final DpApi createAnon(String url)
	{
		return new DpApi(url, null, null, 0);
	}


	/**
	 * Create a new DpApi instance using an API key.
	 *
	 * @param url    The root API url
	 * @param apiKey The API key
	 * @return
	 */
	public static final DpApi createWithKey(String url, String apiKey)
	{
		return new DpApi(url, apiKey, null, 0);
	}


	/**
	 * Createa a new DpApi instance using an API key with a different agent context.
	 * For API keys that belong to admins, it is possible to change the agentId context
	 * so any calls are performed using that agents account (including permissions etc).
	 *
	 * @param url      The root API url
	 * @param apiKey   The API key (must be an admin)
	 * @param agentId  The ID of the agent to run all calls as
	 * @return
	 */
	public static final DpApi createWithKeyAndAgentContext(String url, String apiKey, int agentId)
	{
		return new DpApi(url, apiKey, null, agentId);
	}


	/**
	 *
	 * @param url
	 * @param apiToken
	 * @return
	 */
	public static final DpApi createWithToken(String url, String apiToken)
	{
		return new DpApi(url, null, apiToken, 0);
	}


	/**
	 * Anonymous access to the API without credentials. For example, to negotiate a token.
	 *
	 * @param url
	 */
	private DpApi(String url, String apiKey, String apiToken, int agentId)
	{
		this.url      = Strings.trim(url, '/', Strings.TRIM_RIGHT) + '/';
		this.apiKey   = apiKey;
		this.apiToken = apiToken;
		this.agentId  = agentId;
	}


	/**
	 * Set how many times to retry failed API calls.
	 *
	 * @param retryLimit Retry limit or 0 for none
	 */
	public void setRetryLimit(int retryLimit)
	{
		this.retryLimit = retryLimit;
	}


	/**
	 * Set the time to wait between each retry
	 *
	 * @param retryWaitTime Wait timouet in seconds
	 */
	public void setRetryWaitTime(int retryWaitTime)
	{
		this.retryWaitTime = retryWaitTime;
	}


	/**
	 * Sends the requests and reads the response.
	 *
	 * @param model  The request to send
	 * @return The response
	 * @throws IOException
	 */
	public HttpResponse sendRequest(RequestModel model) throws IOException, IllegalArgumentException
	{
		HttpURLConnection connection = createConnection(model);
		IOException firstException = null;

		int tryNum = 0;
		String sendJson = model.getJson();

		do {
			try {
				return doSendRequest(connection, sendJson);
			} catch (IOException e) {
				if (firstException == null) {
					firstException = e;
				}
				Log.e(TAG, String.format("Exception: %s", e.getMessage()));
			}

			Log.e(TAG, String.format("Retrying in %d ...", retryWaitTime));
			try {
				Thread.sleep(retryWaitTime);
			} catch (InterruptedException ie) {
				break;
			}
			Log.e(TAG, String.format("Retrying: Attempt #%d", tryNum+1));
		} while(tryNum++ < retryLimit);

		throw firstException;
	}

	private HttpResponse doSendRequest(HttpURLConnection connection, String sendJson) throws IOException
	{
		InputStream       istream  = null;
		OutputStream      ostream  = null;
		InputStreamReader isr      = null;
		HttpResponse      response;

		try {
			if (sendJson != null) {
				ostream = connection.getOutputStream();
				ostream.write(sendJson.getBytes("UTF-8"));
				ostream.close();
				ostream = null;
			}

			istream = connection.getInputStream();
			isr = new InputStreamReader(istream, "UTF-8");
			String content = isr.toString();

			response = new HttpResponse(
				connection.getResponseCode(),
				connection.getResponseMessage(),
				connection.getHeaderFields(),
				content
			);

			istream.close();
			isr.close();
			connection.disconnect();

			return response;
		} finally {
			if (istream != null)    try { istream.close(); } catch (IOException e) {}
			if (ostream != null)    try { ostream.close(); } catch (IOException e) {}
			if (isr != null)        try { isr.close(); }     catch (IOException e) {}
			if (connection != null) connection.disconnect();
		}
	}


	/**
	 * Create a new HttpURLConnection for a model
	 *
	 * @param model
	 * @return
	 * @throws IllegalArgumentException
	 */
	private HttpURLConnection createConnection(RequestModel model) throws IllegalArgumentException
	{
		HttpURLConnection connection = getClient().open(createModelUrl(model));
		
		try {
			connection.setRequestMethod(model.getMethod());
		} catch (ProtocolException e) {
			Log.e(TAG, String.format("createConnection(%s): Invalid method: %s: %s", model.getClass().getSimpleName(), model.getMethod(), e.getMessage()));
			throw new IllegalArgumentException();
		}

		if (apiToken != null) {
			connection.addRequestProperty("X-DeskPRO-API-Token", apiToken);
		} else {
			connection.addRequestProperty("X-DeskPRO-API-Key", apiKey);
			if (agentId != 0) {
				connection.addRequestProperty("X-DeskPRO-Agent-ID", Integer.toString(agentId));
			}
		}

		return connection;
	}


	/**
	 * Creates a URL based on the API root, the request model endpoint and query params.
	 *
	 * @param model
	 * @return
	 */
	private URL createModelUrl(RequestModel model)
	{
		String fullUrlString = url + model.getEndpoint();
		String queryString = model.getQueryString();

		if (queryString != null) {
			fullUrlString += "?" + queryString;
		}

		URL fullUrl;
		try {
			fullUrl = new URL(fullUrlString);
		} catch (MalformedURLException e) {
			Log.e(TAG, String.format("buildModelUrl(%s): Invalid URL: %s: %s", model.getClass().getSimpleName(), fullUrlString, e.getMessage()));
			throw new IllegalArgumentException();
		}

		return fullUrl;
	}


	/**
	 * @return The HTTP client
	 */
	private OkHttpClient getClient()
	{
		if (httpClient == null) {
			httpClient = new OkHttpClient();
		}

		return httpClient;
	}


	/**
	 * Gets GSON loader
	 *
	 * @return
	 */
	public Gson getGson()
	{
		if (gson == null) {
			gson = new Gson();
		}

		return gson;
	}
}
