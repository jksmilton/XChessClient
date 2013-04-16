package com.jksmilton.xchessclient.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jksmilton.xchessclient.R;
import com.jksmilton.xchessclient.model.ChessUser;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		List<Fragment> fragments = new ArrayList<Fragment>();
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Intent intent = getIntent();
		
		String userStr =  intent.getStringExtra(getResources().getString(R.string.userjson));
		
		Gson gson = new Gson();
		
		ChessUser user = gson.fromJson(userStr, ChessUser.class);
		
		Toast.makeText(this, user.getHandle(), Toast.LENGTH_SHORT).show();
		
		SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.user_data), Context.MODE_PRIVATE);
		
		
		
		
		if(user.getXauth().equals("xxx")) {
			
			String key = sharedPref.getString(getString(R.string.key), "");
			user.setXauth(key);
			
		} else {
			
			String key = user.getXauth();
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(getString(R.string.key), key);
			editor.commit();
			
		}
		
		//create fragment views, and populate them.
		
		// Create the adapter that will return a fragment for each of the two
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), fragments);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		private List<Fragment> fragments;
		
		public SectionsPagerAdapter(FragmentManager fm, List<Fragment> inFrags) {
			super(fm);
			fragments = inFrags;
		}

		public void updateFragments(){
			
			
			
		}
		
		@Override
		public Fragment getItem(int position) {
			
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return fragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			
			}
			return null;
		}
	}

	public abstract static class SectionFragment extends Fragment {
		public static final String CLIENT_CHESS_USER = "chess_user";
		protected ChessUser user;
		
		public SectionFragment() {
		
		
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			

			user = (ChessUser) getArguments().get(CLIENT_CHESS_USER);
			
			return getView(inflater, container);
		}
		
		protected abstract View getView(LayoutInflater inflater, ViewGroup container);
	}
	
	/**
	 * Fragment representing the view for ongoing games and pending game requests and such
	 */
	public static class GameSectionFragment extends SectionFragment {

		@Override
		protected View getView(LayoutInflater inflater, ViewGroup container) {
			View rootView = inflater.inflate(R.layout.fragment_main_games,
					container, false);
			
			
			
			return rootView;
		}
		
		
	}

}
