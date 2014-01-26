package com.deskpro.mobile;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TicketsFragment extends ListFragment {
	public TicketsFragment() {
	
	}
	
	public static TicketsFragment createInstance() {
		TicketsFragment notif = new TicketsFragment();
		return notif;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		NotificationsAdapter adapter = new NotificationsAdapter();
		setListAdapter(adapter);
	}

	public void refresh()
	{

	}

	public static class NotificationsAdapter extends BaseAdapter {
		
		private LayoutInflater mInflater;
		
		@Override
		public int getCount() {
			return 100;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (mInflater == null) {
				mInflater = LayoutInflater.from(parent.getContext());
			}
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.li_ticket_row, parent, false);
			}
			
			TextView text = (TextView)convertView.findViewById(R.id.item_id);
			text.setText("#" + position);
			
			return convertView;
		}
	}
}
