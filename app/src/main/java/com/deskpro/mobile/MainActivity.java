package com.deskpro.mobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

public class MainActivity extends FragmentActivity {
	
	public static Intent createIntent(Context context) {
		return new Intent(context.getApplicationContext(), MainActivity.class);
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null && getSupportFragmentManager().findFragmentById(R.id.content_frame) == null) {
        	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        	
        	MainTabFragment frag = MainTabFragment.newInstance();

        	ft.add(R.id.content_frame, frag, App.ACTIVITY_NOTIF);
        	ft.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
