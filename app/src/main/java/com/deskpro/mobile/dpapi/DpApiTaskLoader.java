package com.deskpro.mobile.dpapi;

import android.content.Context;
import android.util.Log;

import com.deskpro.mobile.util.GenericAsyncTaskLoader;
import com.noveogroup.android.log.LoggerManager;

import java.io.IOException;

public class DpApiTaskLoader<T> extends GenericAsyncTaskLoader
{
	private Class<T> responseClass;
	private DpApiLoader<T> dpApiLoader;

	public DpApiTaskLoader(Context context, Class<T> responseClass, DpApiLoader<T> dpApiLoader)
	{
		super(context);
		this.responseClass = responseClass;
		this.dpApiLoader = dpApiLoader;
	}

	@Override
	public T loadInBackground()
	{
		try {
			return dpApiLoader.getResponseModel();
		} catch (IOException e) {
			LoggerManager.getLogger().e(e);
		}

		return null;
	}
}
