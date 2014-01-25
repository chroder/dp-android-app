package com.deskpro.mobile.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class RemoteJsonLoader<T> extends GenericAsyncTaskLoader<T>
{
	private static final String TAG = RemoteJsonLoader.class.getSimpleName();

	private final Class<T> clazz;
	private final String url;

	public RemoteJsonLoader(Context context, Class<T> clazz, String url)
	{
		super(context);
		this.clazz = clazz;
		this.url = url;
	}

	@Override
	public T loadInBackground()
	{
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			Context appContext = getContext().getApplicationContext();
			if(!(appContext instanceof GsonRetriever)){
				throw new GsonNotProvided();
			}
			int retryCount = 5;
			int retryTimeout = 1000;
			while (retryCount > 0) {
				try{
					conn = (HttpURLConnection)new URL(url).openConnection();
					//conn.addRequestProperty("X-Candy-Platform", "desktop");
					conn.addRequestProperty("Accept", "application/json");
					Log.d(TAG, "setDoInput to " + url);
					conn.setDoInput(true);
					int responseCode = conn.getResponseCode();
					if (responseCode == 403 || responseCode / 100 == 5) {
						throw new IOException("Server borked: " + conn.getResponseMessage());
					}
					is = conn.getInputStream();
					InputStreamReader isr = new InputStreamReader(is, "UTF-8");
					Gson gson = ((GsonRetriever)appContext).getGson();
					return (T)gson.fromJson(isr, clazz);
				} catch (IOException e) {
					Log.e(TAG, "Exception: ", e);
				} finally {
					if(is != null){
						is.close();
					}
					if(conn != null){
						conn.disconnect();
					}
				}
				retryCount--;
				if (retryCount > 0) {
					try {
						Log.d(TAG, "Retrying in " + retryTimeout);
						Thread.sleep(retryTimeout);
						retryTimeout *= 2;
						Log.d(TAG, "Retrying - " + retryCount + " retries remaining");
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		} catch(GsonNotProvided e) {
			Log.e(TAG, "Gson not provided by application context.", e);
		} catch(JsonSyntaxException e) {
			Log.e(TAG, "Remote JSON syntax issues: " + e.getMessage(), e);
		} catch(IOException e) {
			Log.e(TAG, "Failed to load data: " + e.getMessage(), e);
		}
		return null;
	}

	public static interface GsonRetriever
	{
		public Gson getGson();
	}

	public static class GsonNotProvided extends Exception
	{
		private static final long serialVersionUID = 8877763940196544330L;
	}
}