package com.deskpro.mobile;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.deskpro.mobile.util.RemoteJsonLoader;

import java.lang.reflect.Field;

public class App extends Application implements RemoteJsonLoader.GsonRetriever
{
	public static final String TAB_NOTIF      = "tab_notif";
	public static final String TAB_TICKETS    = "tab_tickets";
	public static final String ACTIVITY_NOTIF = "notif";

	private Gson gson;

	public static App from(Context context)
	{
		return (App)context.getApplicationContext();
	}

	@Override
	public Gson getGson()
	{
		if (gson == null) {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.setFieldNamingStrategy(new GsonFieldStrategy());
			gson = gsonBuilder.create();
		}

		return gson;
	}

	private static final class GsonFieldStrategy implements FieldNamingStrategy
	{
		public static final String TAG = GsonFieldStrategy.class.getSimpleName();

		@Override
		public String translateName(Field f)
		{
			String fieldName = f.getName();
			String apiName = fieldName.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase();

			Log.i(TAG, String.format("GsonFieldStrategy: %s -> %s", fieldName, apiName));
			return apiName;
		}
	}
}
