package com.deskpro.mobile;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.deskpro.mobile.dpapi.DpApi;
import com.deskpro.mobile.models.ApiSession;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.LoggerManager;

import java.lang.reflect.Field;

public class App extends Application
{
	private static final Logger logger = LoggerManager.getLogger(App.class.getSimpleName());

	public static final String TAB_NOTIF      = "tab_notif";
	public static final String TAB_TICKETS    = "tab_tickets";
	public static final String ACTIVITY_NOTIF = "notif";

	private Gson gson;
	private ApiSession apiSession;
	private DpApi dpApi;

	public static App from(Context context)
	{
		return (App)context.getApplicationContext();
	}

	public void onCreate()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		apiSession = new ApiSession(
			prefs.getString("apiToken", null),
			prefs.getString("apiUrl", null),
			prefs.getString("email", null),
			prefs.getString("name", null),
			prefs.getString("helpdeskUrl", null),
			prefs.getString("cloudName", null)
		);

		if (logger.isEnabled(Logger.Level.DEBUG)) {
			if (apiSession.getApiToken() != null) {
				logger.d("(Prefs) Saved API token: %s", apiSession.getApiToken());
			} else {
				logger.d("(Prefs) No saved API token");
			}
		}
	}

	public ApiSession getApiSession()
	{
		return apiSession;
	}

	public void setApiSession(ApiSession apiSession)
	{
		if (logger.isEnabled(Logger.Level.DEBUG)) logger.d("Saving new API token: %s", apiSession.getApiToken());

		this.apiSession = apiSession;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString("apiToken", apiSession.getApiToken());
		edit.putString("apiUrl", apiSession.getApiUrl());
		edit.putString("email", apiSession.getEmail());
		edit.putString("name", apiSession.getName());
		edit.putString("helpdeskUrl", apiSession.getHelpdeskUrl());
		edit.putString("cloudName", apiSession.getCloudName());
		edit.commit();

		dpApi = null;
	}

	public DpApi getDpApi()
	{
		if (dpApi == null) {
			if (apiSession.getApiUrl() != null && apiSession.getApiToken() != null) {
				dpApi = DpApi.newWithToken(apiSession.getApiUrl(), apiSession.getApiToken());
			} else if (apiSession.getApiUrl() != null) {
				dpApi = DpApi.newAnonymous(apiSession.getApiUrl());
			} else {
				dpApi = null;
			}
		}

		return dpApi;
	}
}
