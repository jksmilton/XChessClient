package com.jksmilton.xchessclient.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jksmilton.xchessclient.R;
import com.jksmilton.xchessclient.listhandlers.FriendHandler;
import com.jksmilton.xchessclient.listhandlers.GameHandler;
import com.jksmilton.xchessclient.model.ChessUser;
import com.jksmilton.xchessclient.model.Game;
import com.jksmilton.xchessclient.model.URLAccessor;
import com.jksmilton.xchessclient.model.XpandableListAdapter;

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
	private SectionsPagerAdapter mSectionsPagerAdapter;
	
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		List<SectionFragment> fragments = new ArrayList<SectionFragment>();
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.user_data), Context.MODE_PRIVATE);
		
		String userStr =  sharedPref.getString(getResources().getString(R.string.userjson), "");
		
		Gson gson = new Gson();
		
		ChessUser user = gson.fromJson(userStr, ChessUser.class);
				
		if(user.getXauth().equals("xxx")) {
			
			String key = sharedPref.getString(getString(R.string.key), "");
			user.setXauth(key);
			
		} else {
			
			String key = user.getXauth();
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(getString(R.string.key), key);
			editor.commit();
			
		}
		
		SectionFragment game = new GameSectionFragment();
		SectionFragment friends = new FriendSectionFragment();
		
		game.setUser(user);
		game.setActivity(this);
		
		friends.setUser(user);
		friends.setActivity(this);
		
		//create fragment views, and populate them.
		
		fragments.add(game);
		fragments.add(friends);
		
		
		// Create the adapter that will return a fragment for each of the two
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), fragments, user.getXauth(), this);
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
		
		return true;
		
	}
	
	
	public void createNewFriend(View v) {

		Intent addFriend = new Intent(this, AddFriendActivity.class);
		
		startActivity(addFriend);
		
	}
	
	public void createNewGame(View v) {
		
		Intent newGame = new Intent(this, CreateGameActivity.class);
		startActivity(newGame);
		
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		
		switch(item.getItemId()){
		
		case R.id.action_refresh:
			mSectionsPagerAdapter.updateFragments();
		
		}
		
		return true;		
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		private List<SectionFragment> fragments;
		private String uKey;
		private Activity activity;
		
		public SectionsPagerAdapter(FragmentManager fm, List<SectionFragment> fragments2, String key, Activity parentActivity) {
			super(fm);
			fragments = fragments2;
			uKey = key;
			activity = parentActivity;
		}

		public void updateFragments(){
			
			Log.d("Update Data", "Getting user data");
			
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        
	        if (networkInfo == null || !networkInfo.isConnected()) {
	            
	        	
	        	Toast.makeText(activity, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
	        	Log.d("Update Connection", "No connection found");
	        	
	        } else {
	        	String url = getResources().getString(R.string.login_key) + uKey + "/" + getResources().getString(R.string.appID);
	            Updater accessor = new Updater();
	            
	            accessor.execute(url);
	            
	            
	        }
			
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
		
		protected class Updater extends URLAccessor {
			
			@Override
	        protected void onPostExecute(Object result) {
	            
				Gson gson = new Gson();
				
				ChessUser user = gson.fromJson((String) result, ChessUser.class);
				user.setXauth(uKey);
				
				for(SectionFragment f : fragments){
					
					f.setUser(user);
					f.update();
					
				}
				
				SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources().getString(R.string.user_data), MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				
				editor.putString(getResources().getString(R.string.userjson), gson.toJson(user));
				editor.commit();
				
	       }
		
		}
		
	}

	public abstract static class SectionFragment extends Fragment {
		protected ChessUser user;
		protected Activity parentActivity;
		public static final String GROUP_ITEM = "group_item";
		public static final String CHILD_ITEM = "child_item";
		
		public void setUser(ChessUser player){
			user = player;
		}
		
		public void setActivity(Activity parent){
			parentActivity = parent;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
						
			return getView(inflater, container);
		}
		
		protected abstract View getView(LayoutInflater inflater, ViewGroup container);
	
		protected abstract void handleResult(String result);
		
		
		protected class DataUpdater extends com.jksmilton.xchessclient.model.URLAccessor {
			
			@Override
	        protected void onPostExecute(Object result) {
	            
				handleResult((String) result);
				
	       }
		
		}
		
		protected abstract void update();
		
	}
	
	
	public static class FriendSectionFragment extends SectionFragment{

		@Override
		protected View getView(LayoutInflater inflater, ViewGroup container) {
			View rootView = inflater.inflate(R.layout.fragment_main_friends,
					container, false);
			
			update();
			
			return rootView;
		}
		private List<String> createGroup(int numPending){
			List<String> groups = new ArrayList<String>();
			
			groups.add("Pending Friend Requests (" + numPending + ")");
			groups.add("Friends");
			
			return groups;
			
		}
		
		private List<List<Object>> createChildList(List<String> pending){
			
			List<List <Object>> children = new ArrayList<List <Object>>();
			
			List<Object> pendingO = new ArrayList<Object>();
			pendingO.addAll(pending);
			children.add(pendingO);
			
			List<Object> friends = new ArrayList<Object>();
			friends.addAll(user.getFriends());
			children.add(friends);
			
			return children;
			
		}
		
		@Override		
		protected void handleResult(String result) {
			
			View rootView = this.getView();
			Gson gson = new Gson();
			
			List<String> pendingRequests = Arrays.asList(gson.fromJson(result, String[].class));
			
			ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.expandableListViewFriends);
			
			XpandableListAdapter expListAdapter = new XpandableListAdapter(
					getActivity(),
					createGroup(pendingRequests.size()),
					createChildList(pendingRequests)
					);
			listView.setOnChildClickListener(new FriendHandler((FragmentActivity) getActivity(), user.getXauth()));
			listView.setAdapter(expListAdapter);
			
		}
		
		
		
		@Override
		protected void update() {

			DataUpdater accessor = new DataUpdater();
			String url = getResources().getString(R.string.jksmilton_pendingFriends) + user.getXauth() + "/" + getResources().getString(R.string.appID);
			accessor.execute(url);
			
		}
		
	}

	/**
	 * Fragment representing the view for ongoing games and pending game requests and such
	 */
	public static class GameSectionFragment extends SectionFragment {

		@Override
		protected View getView(LayoutInflater inflater, ViewGroup container) {
			View rootView = inflater.inflate(R.layout.fragment_main_games,
					container, false);
			
			update();
			
			return rootView;
		}

		

		@Override
		protected void handleResult(String result) {
			if(result.startsWith("[")){
				View rootView = this.getView();
				Gson gson = new Gson();
				
				List<Game> pendingRequests = Arrays.asList(gson.fromJson(result, Game[].class));
				
				ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.expandableListViewGames);
				
				XpandableListAdapter expListAdapter = new XpandableListAdapter(
						getActivity(),
						createGroup(pendingRequests.size()),
						createChildList(pendingRequests)
						);
						
				listView.setOnChildClickListener(new GameHandler((FragmentActivity) parentActivity, user));
				listView.setAdapter(expListAdapter);
			}
		}
		
		private List<String> createGroup(int numPending){
			List<String> groups = new ArrayList<String>();
			
			groups.add("Pending Game Requests (" + numPending + ")");
			groups.add("Games");
			
			return groups;
			
		}
		private List<List<Object>> createChildList(List<Game> pending){
			
			List<List <Object>> children = new ArrayList<List <Object>>();
			
			List<Object> pendingO = new ArrayList<Object>();
			pendingO.addAll(pending);
			children.add(pendingO);
			
			List<Object> games = new ArrayList<Object>();
			games.addAll(user.getGames());
			children.add(games);
			
			return children;
			
		}


		@Override
		protected void update() {

			DataUpdater accessor = new DataUpdater();
			String url = getResources().getString(R.string.jksmilton_pendingGames) + user.getXauth() + "/" + getResources().getString(R.string.appID);
			accessor.execute(url);
			
		}

	}
}