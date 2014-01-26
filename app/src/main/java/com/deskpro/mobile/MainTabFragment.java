package com.deskpro.mobile;

import com.deskpro.mobile.managers.TabManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainTabFragment extends Fragment {

	private TabHost mTabs;
	private TabManager mTabManager;
	
	public MainTabFragment() {

	}
	
	public static MainTabFragment newInstance() {
		return new MainTabFragment();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.main_tab_fragment, container, false);
		return parent;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		mTabs = (TabHost)view.findViewById(android.R.id.tabhost);
		mTabs.setup();
		
		mTabManager = new TabManager(this, mTabs, R.id.realtabcontent);
		
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		final int totalWidth = displayMetrics.widthPixels;
		final int tabWidth = totalWidth / 2;
		
		mTabManager.addTab(getTab(App.TAB_NOTIF, tabWidth), NotificationsFragment.class);
		mTabManager.addTab(getTab(App.TAB_TICKETS, totalWidth - tabWidth), TicketsFragment.class);
	}
	
	private TabSpec getTab(String tag, int width) {
		final View tab = LayoutInflater.from(getActivity()).inflate(R.layout.tabbtn, mTabs, false);
		
		final LayoutParams params = tab.getLayoutParams();
		params.width = width;
		tab.setLayoutParams(params);
		
		String btnText;
		if (tag.equals(App.TAB_NOTIF)) {
			btnText = getString(R.string.tab_notif);			
		} else {
			btnText = getString(R.string.tab_tickets);
		}
		
		((TextView)tab.findViewById(R.id.btntext)).setText(btnText);
		
		return mTabs.newTabSpec(tag).setIndicator(tab);
	}

	public Fragment getCurrentFragment() {
		return mTabManager.getCurrentFragment();
	}
}
