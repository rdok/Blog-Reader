package com.rizartdokollari.blogreader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainListActivity extends ListActivity {

	private String[] mBlogPostTitles;
	public static final int NUMBER_OF_POSTS = 20;
	public static final String TAG = MainListActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);

		GetBlogPostsTask getBlogPostsTask = new GetBlogPostsTask();
		getBlogPostsTask.execute();

		// String message = getString(R.string.no_items);
		// Toast.makeText(this, message , Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_list, menu);
		return true;
	}

	// create a subclass
	private class GetBlogPostsTask extends AsyncTask<Object, Void, String> {

		@Override
		protected String doInBackground(Object... arg0) { // ellipses zero or n
															// arguments

			int responseCode = -1;
			try {
				URL blogFeedUrl = new URL(
						"http://blog.teamtreehouse.com/api/get_recent_summary/?count="
								+ NUMBER_OF_POSTS);

				HttpURLConnection httpURLConnection = (HttpURLConnection) blogFeedUrl
						.openConnection();
				httpURLConnection.connect();

				responseCode = httpURLConnection.getResponseCode();
				Log.e(TAG, "Response Code: " + responseCode);
			} catch (MalformedURLException e) {
				Log.e(TAG, "MalformedURLException caught: ", e);
			} catch (IOException e) {
				Log.e(TAG, "IOException caught: " + e.getMessage(), e);
			} catch (Exception e) {
				Log.e(TAG, "Exception caught: ", e);

			}
			return "Code: " + responseCode;

		}

	}

	public String[] getmAndroidNames() {
		return mBlogPostTitles;
	}

	public void setmAndroidNames(String[] mAndroidNames) {
		this.mBlogPostTitles = mAndroidNames;
	}

}
