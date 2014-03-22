package com.deskpro.mobile.test;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.deskpro.mobile.MainActivity;
import com.deskpro.mobile.R;
import com.deskpro.mobile.dpapi.DpApi;
import com.deskpro.mobile.dpapi.models.request.ActivityRequest;
import com.deskpro.mobile.dpapi.models.response.ActivityResponse;
import com.deskpro.mobile.dpapi.models.response.HttpResponse;
import com.google.gson.Gson;

import java.io.IOException;

public class ApiNotifTest extends ActivityInstrumentationTestCase2<MainActivity>
{
	private DpApi api;

	public ApiNotifTest()
	{
		super(MainActivity.class);
	}

	private DpApi getApi() {
		if (api == null) {
			api = DpApi.newWithKeyAndAgentContext(
				getActivity().getResources().getString(R.string.hd_url) + "api",
				getActivity().getResources().getString(R.string.hd_api_token),
				0
			);
		}

		return api;
	}

	public void testActivity() throws IOException
	{
		ActivityRequest req = new ActivityRequest(0);
		HttpResponse httpRes = getApi().sendRequest(req);

		Log.d("ApiNotifTestResult", httpRes.getContent());

		Gson gson = getApi().getGson();
		ActivityResponse res = gson.fromJson(httpRes.getContent(), ActivityResponse.class);
		Log.d("ApiNotifTest", "lastId: " + res.getLastId());
		Log.d("ApiNotifTest", "Count: " + res.getAlerts().size());
	}
}
