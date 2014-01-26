package com.deskpro.mobile.dpapi;

import android.content.Context;

import com.deskpro.mobile.dpapi.models.request.RequestModel;
import com.deskpro.mobile.dpapi.models.response.HttpResponse;
import com.deskpro.mobile.util.Strings;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.LoggerManager;
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
	private static final Logger logger = LoggerManager.getLogger(DpApi.class.getSimpleName());

	/**
	 * The root API url. This should include the '/api' bit.
	 * Example: http://example.com/index.php/api
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
	public static final DpApi newAnonymous(String url)
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
	public static final DpApi newWithKey(String url, String apiKey)
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
	public static final DpApi newWithKeyAndAgentContext(String url, String apiKey, int agentId)
	{
		return new DpApi(url, apiKey, null, agentId);
	}


	/**
	 *
	 * @param url
	 * @param apiToken
	 * @return
	 */
	public static final DpApi newWithToken(String url, String apiToken)
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
		this.url      = Strings.trim(url, '/', Strings.TRIM_RIGHT);
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
		IOException firstException = null;

		int tryNum = 0;
		String sendJson = model.getJson();

		do {
			try {
				HttpURLConnection connection = createConnection(model);
				return doSendRequest(connection, sendJson);
			} catch (IOException e) {
				if (firstException == null) {
					firstException = e;
				}
				if (logger.isEnabled(Logger.Level.DEBUG)) logger.d(e);
			}

			if (logger.isEnabled(Logger.Level.DEBUG)) logger.d("Retrying in %d", retryWaitTime);
			try {
				Thread.sleep(retryWaitTime);
			} catch (InterruptedException ie) {
				break;
			}
			if (logger.isEnabled(Logger.Level.DEBUG)) logger.d("Retrying: Attempt %d", tryNum+1);
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
			if (logger.isEnabled(Logger.Level.VERBOSE)) logger.v("[REQ] Sending %s %s", connection.getRequestMethod(), connection.getURL().toString());
			if (sendJson != null) {
				if (logger.isEnabled(Logger.Level.VERBOSE)) logger.v("[REQ] Sending JSON payload: %s", sendJson);

				byte[] sendJsonBytes = sendJson.getBytes("UTF-8");

				connection.addRequestProperty("Content-type", "application/json");
				connection.addRequestProperty("Content-length", String.valueOf(sendJsonBytes.length));
				ostream = connection.getOutputStream();
				ostream.write(sendJsonBytes);
				ostream.close();
				ostream = null;
				if (logger.isEnabled(Logger.Level.VERBOSE)) logger.v("[REQ] Done sending JSON payload", sendJson);
			}

			if (logger.isEnabled(Logger.Level.VERBOSE)) logger.v("[REQ] Reading response", sendJson);
			istream = connection.getInputStream();
			isr = new InputStreamReader(istream, "UTF-8");
			String content = CharStreams.toString(isr);

			if (logger.isEnabled(Logger.Level.VERBOSE)) logger.v("[REQ] Response %d %s: %s", connection.getResponseCode(), connection.getResponseMessage(), content);

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
			logger.a(e, "createConnection(%s): Invalid method");
			throw new IllegalArgumentException();
		}

		if (apiToken != null) {
			connection.addRequestProperty("X-DeskPRO-API-Token", apiToken);
		} else if (apiKey != null) {
			connection.addRequestProperty("X-DeskPRO-API-Key", apiKey);
			if (agentId != 0) {
				connection.addRequestProperty("X-DeskPRO-Agent-ID", Integer.toString(agentId));
			}
		}

		connection.addRequestProperty("Connection", "Close");

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
			logger.a(e, "buildModelUrl(%s): Invalid URL");
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


	/**
	 * Create a loader
	 * @param responseClass  The response model class
	 * @param requestModel   The request model class
	 * @param <T>            Type of response model
	 * @return DpApiLoader
	 */
	public <T> DpApiLoader<T> createLoader(Class<T> responseClass, RequestModel requestModel)
	{
		return new DpApiLoader<T>(responseClass, this, requestModel);
	}


	/**
	 * Creates a task loader.
	 *
	 * @param context        The android context
	 * @param responseClass  The response model class
	 * @param requestModel   The request model
	 * @param <T>            Type of response model
	 * @return DpApiTaskLoader
	 */
	public <T> DpApiTaskLoader<T> createTaskLoader(Context context, Class<T> responseClass, RequestModel requestModel)
	{
		DpApiLoader<T> apiLoader = new DpApiLoader<T>(responseClass, this, requestModel);
		return new DpApiTaskLoader<T>(context, responseClass, apiLoader);
	}
}
