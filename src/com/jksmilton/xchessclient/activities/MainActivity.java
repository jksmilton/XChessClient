package com.jksmilton.xchessclient.activities;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
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
import android.content.res.Resources.NotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jksmilton.xchessclient.R;
import com.jksmilton.xchessclient.model.ChessUser;
import com.jksmilton.xchessclient.model.Game;

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
		List<SectionFragment> fragments = new ArrayList<SectionFragment>();
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Intent intent = getIntent();
		
		String userStr =  intent.getStringExtra(getResources().getString(R.string.userjson));
		
		Gson gson = new Gson();
		
		ChessUser user = gson.fromJson(userStr, ChessUser.class);
				
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
			
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        
	        if (networkInfo == null || !networkInfo.isConnected()) {
	            
	        	
	        	Toast.makeText(activity, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
	        	Log.d("Update Connection", "No connection found");
	        	
	        } else {
	        	String url = getResources().getString(R.string.login_key) + uKey + "/" + getResources().getString(R.string.appID);
	            URLAccessor accessor = new URLAccessor();
	            
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
		
		protected class URLAccessor extends AsyncTask<String, Object, String> {
			
			
			public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
			    Reader reader = null;
			    reader = new InputStreamReader(stream, "UTF-8");        
			    boolean ready = true;
			    String output = "";
			    while(ready){
			    	
			    	int read = reader.read();
			    	
			    	if(read < 0)
			    		ready = false;
			    	else
			    		output+= (char) read;
			    	
			    }
			    
			    return output;
			}


			@Override
			protected String doInBackground(String... params) {
				InputStream is = null;
				String returnStr = "Fail";
				
				try {
			        URL url = new URL(params[0]);
			        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			        conn.setReadTimeout(10000 /* milliseconds */);
			        conn.setConnectTimeout(15000 /* milliseconds */);
			        conn.setRequestMethod("GET");
			        conn.setDoInput(true);
			        // Starts the query
			        conn.connect();
			        int response = conn.getResponseCode();
			        Log.d("Login without key", "The response is: " + response);
			        is = conn.getInputStream();

			        // Convert the InputStream into a string
			        returnStr = readIt(is);
			        
			       
			        
			    // Makes sure that the InputStream is closed after the app is
			    // finished using it.
			    } catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					returnStr = "malformed URL";
				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					returnStr = "not found";
				} catch (ProtocolException e) {
					// TODO Auto-generated catch block
					returnStr = "protocol exception";
				} catch (IOException e) {
					returnStr = "IO Exception: " + e.getMessage();
				} finally {
			        if (is != null) {
			            try {
							is.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        } 
			    }
				return returnStr;
			}
		
			@Override
	        protected void onPostExecute(String result) {
	            
				Gson gson = new Gson();
				
				ChessUser user = gson.fromJson(result, ChessUser.class);
				
				for(SectionFragment f : fragments){
					
					f.setUser(user);
					f.update();
					
				}
				
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
		
		public abstract void createNew(View v);
		
		protected class URLAccessor extends AsyncTask<Object, Object, String> {
			
			
			public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
			    Reader reader = null;
			    reader = new InputStreamReader(stream, "UTF-8");        
			    boolean ready = true;
			    String output = "";
			    while(ready){
			    	
			    	int read = reader.read();
			    	
			    	if(read < 0)
			    		ready = false;
			    	else
			    		output+= (char) read;
			    	
			    }
			    
			    return output;
			}


			@Override
			protected String doInBackground(Object... params) {
				InputStream is = null;
				String returnStr = "Fail";
				
				try {
			        URL url = new URL((String) params[0]);
			        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			        conn.setReadTimeout(10000 /* milliseconds */);
			        conn.setConnectTimeout(15000 /* milliseconds */);
			        conn.setRequestMethod("GET");
			        conn.setDoInput(true);
			        // Starts the query
			        conn.connect();
			        int response = conn.getResponseCode();
			        Log.d("Login without key", "The response is: " + response);
			        is = conn.getInputStream();

			        // Convert the InputStream into a string
			        returnStr = readIt(is);
			        
			       
			        
			    // Makes sure that the InputStream is closed after the app is
			    // finished using it.
			    } catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					returnStr = "malformed URL";
				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					returnStr = "not found";
				} catch (ProtocolException e) {
					// TODO Auto-generated catch block
					returnStr = "protocol exception";
				} catch (IOException e) {
					returnStr = "IO Exception: " + e.getMessage();
				} finally {
			        if (is != null) {
			            try {
							is.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        } 
			    }
				return returnStr;
			}
		
			@Override
	        protected void onPostExecute(String result) {
	            
				handleResult(result);
				
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
			friends.addAll(user.getGames());
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
					
			listView.setAdapter(expListAdapter);
			
		}
		
		@Override
		public void createNew(View v) {

			
			
		}
		@Override
		protected void update() {

			URLAccessor accessor = new URLAccessor();
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
			
			View rootView = this.getView();
			Gson gson = new Gson();
			
			List<Game> pendingRequests = Arrays.asList(gson.fromJson(result, Game[].class));
			
			ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.expandableListViewGames);
			
			XpandableListAdapter expListAdapter = new XpandableListAdapter(
					getActivity(),
					createGroup(pendingRequests.size()),
					createChildList(pendingRequests)
					);
					
			listView.setAdapter(expListAdapter);
			
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
		public void createNew(View v) {

			
			
		}



		@Override
		protected void update() {

			URLAccessor accessor = new URLAccessor();
			String url = getResources().getString(R.string.jksmilton_pendingGames) + user.getXauth() + "/" + getResources().getString(R.string.appID);
			accessor.execute(url);
			
		}

	}
}