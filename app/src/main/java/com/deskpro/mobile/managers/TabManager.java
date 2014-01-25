package com.deskpro.mobile.managers;

import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TabHost;

public class TabManager implements TabHost.OnTabChangeListener
{
	private final Fragment mFragment;
	private final TabHost mTabHost;
	private final int mContainerId;
	private final HashMap<String, TabManager.TabInfo> mTabs = new HashMap<String, TabManager.TabInfo>();
	TabManager.TabInfo mLastTab;
	
	static final class TabInfo
	{
		private final String tag;
		private final Class<?> clss;
		private final Bundle args;
		private Fragment fragment;
		
		
		TabInfo(String _tag, Class<?> _class, Bundle _args)
		{
			tag = _tag;
			clss = _class;
			args = _args;
		}
	}
	
	static class DummyTabFactory implements TabHost.TabContentFactory
	{
		private final Context mContext;
		
		
		public DummyTabFactory(Context context)
		{
			mContext = context;
		}
		
		@Override
		public View createTabContent(String tag)
		{
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}
	}
	
	
	public TabManager(Fragment fragment, TabHost tabHost, int containerId)
	{
		mFragment = fragment;
		mTabHost = tabHost;
		mContainerId = containerId;
		mTabHost.setOnTabChangedListener(this);
	}
	
	public void addTab(TabHost.TabSpec tabSpec, Class<?> clss)
	{
		tabSpec.setContent(new DummyTabFactory(mFragment.getActivity()));
		String tag = tabSpec.getTag();
		
		TabManager.TabInfo info = new TabInfo(tag, clss, null);
		info.fragment = mFragment.getChildFragmentManager().findFragmentByTag(tag);
		if(info.fragment != null && !info.fragment.isDetached()){
			FragmentTransaction ft = mFragment.getChildFragmentManager().beginTransaction();
			ft.detach(info.fragment);
			ft.commit();
		}
		
		mTabs.put(tag, info);
		mTabHost.addTab(tabSpec);
	}
	
	@Override
	public void onTabChanged(String tabId)
	{
		TabManager.TabInfo newTab = mTabs.get(tabId);
		if(mLastTab != newTab){
			FragmentTransaction ft = mFragment.getChildFragmentManager().beginTransaction();
			if(mLastTab != null){
				if(mLastTab.fragment != null){
					ft.detach(mLastTab.fragment);
				}
			}
			if(newTab != null){
				if(newTab.fragment == null){
					newTab.fragment = Fragment.instantiate(mFragment.getActivity(), newTab.clss.getName(), newTab.args);
					ft.add(mContainerId, newTab.fragment, newTab.tag);
				}
				else{
					ft.attach(newTab.fragment);
				}
			}
			
			mLastTab = newTab;
			ft.commit();
		}
	}
}