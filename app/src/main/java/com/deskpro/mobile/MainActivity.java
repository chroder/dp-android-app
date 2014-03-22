package com.deskpro.mobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.deskpro.mobile.dpapi.DpApiTaskLoader;
import com.deskpro.mobile.dpapi.models.request.ActivityRequest;
import com.deskpro.mobile.dpapi.models.response.ActivityItem;
import com.deskpro.mobile.dpapi.models.response.ActivityResponse;
import com.deskpro.mobile.models.ApiSession;
import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.LoggerManager;

import java.util.LinkedHashMap;

import butterknife.OnClick;

public class MainActivity extends FragmentActivity
{
	private static final Logger logger = LoggerManager.getLogger(MainActivity.class.getSimpleName());
	MainTabFragment tabFragment;
	private App app;
	private MenuItem refreshMenuItem;
	private boolean isRefreshingNotifs = false;
	private boolean isRefreshingTickets = false;
	private boolean isRefreshIcon = false;

	public static Intent createIntent(Context context)
	{
		return new Intent(context.getApplicationContext(), MainActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		app = (App)getApplication();

		if (savedInstanceState == null && getSupportFragmentManager().findFragmentById(R.id.content_frame) == null) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			tabFragment = MainTabFragment.newInstance();

			ft.add(R.id.content_frame, tabFragment, App.ACTIVITY_NOTIF);
			ft.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		refreshMenuItem = menu.findItem(R.id.action_refresh);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		switch (item.getItemId()) {
			case R.id.action_logout:
				App app = (App) getApplication();
				ApiSession newApiSession = new ApiSession(null, null, null, null, null, null);
				app.setApiSession(newApiSession);

				startActivity(LoginActivity.createIntent(MainActivity.this));
				finish();
				return true;

			case R.id.action_refresh:
				Fragment currentFragment = tabFragment.getCurrentFragment();
				if (currentFragment instanceof NotificationsFragment) {
					refreshNotifs();
				} else if (currentFragment instanceof TicketsFragment) {
					((TicketsFragment) currentFragment).refresh();
				}
		}

		return super.onMenuItemSelected(featureId, item);
	}

	//#########################################################################
	//# Notification Loading
	//#########################################################################

	private final int NOTIF_LOADER_ID = 10;
	private int lastNotifId = 0;
	private LinkedHashMap<Integer, ActivityItem> notifs = new LinkedHashMap<Integer, ActivityItem>();

	public void refreshNotifs()
	{
		getSupportLoaderManager().restartLoader(NOTIF_LOADER_ID, null, new LoaderManager.LoaderCallbacks<ActivityResponse>()
		{
			@Override
			public Loader<ActivityResponse> onCreateLoader(int i, Bundle bundle)
			{
				MainActivity.this.setIsRefreshingNotifs(true);
				MainActivity.this.updateRefreshingIcon();

				ActivityRequest req = new ActivityRequest(lastNotifId);
				DpApiTaskLoader<ActivityResponse> loader = app.getDpApi().createTaskLoader(MainActivity.this, ActivityResponse.class, req);
				return loader;
			}

			@Override
			public void onLoadFinished(Loader<ActivityResponse> activityResponseLoader, ActivityResponse activityResponse)
			{
				MainActivity.this.setIsRefreshingNotifs(false);
				MainActivity.this.updateRefreshingIcon();

				if (activityResponse != null) {
					for (ActivityItem a : activityResponse.getAlerts()) {
						if (notifs.containsKey(a.getId())) {
							notifs.remove(a.getId());
						}
						notifs.put(a.getId(), a);
					}
				}
			}

			@Override
			public void onLoaderReset(Loader<ActivityResponse> activityResponseLoader)
			{

			}
		});
	}

	public void updateRefreshingIcon()
	{
		if (isRefreshingNotifs || isRefreshingTickets) {
			if (isRefreshIcon) return;
			isRefreshIcon = true;

			ImageView iv = (ImageView) getLayoutInflater().inflate(R.layout.loading_action_view, null);
			Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
			rotation.setRepeatCount(Animation.INFINITE);
			iv.startAnimation(rotation);
			refreshMenuItem.setActionView(iv);
		} else {
			if (!isRefreshIcon) return;
			isRefreshIcon = false;
			refreshMenuItem.getActionView().clearAnimation();
			refreshMenuItem.setActionView(null);
		}
	}

	public boolean isRefreshingNotifs()
	{
		return isRefreshingNotifs;
	}

	public void setIsRefreshingNotifs(boolean isRefreshingNotifs)
	{
		this.isRefreshingNotifs = isRefreshingNotifs;
	}
}
