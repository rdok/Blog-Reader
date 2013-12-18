package com.rizartdokollari.blogreader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class MainListActivity extends ListActivity {

	private String[] mBlogPostTitles;
	public static final int NUMBER_OF_POSTS = 20;
	public static final String TAG = MainListActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);

		if (isNetwokAvailable()) {
			GetBlogPostsTask getBlogPostsTask = new GetBlogPostsTask();
			getBlogPostsTask.execute();
		} else {
			Toast.makeText(this, "Network is unavailable", Toast.LENGTH_LONG)
					.show();
		}

		// String message = getString(R.string.no_items);
		// Toast.makeText(this, message , Toast.LENGTH_LONG).show();
	}

	private boolean isNetwokAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		boolean isAvailable = false;
		if (networkInfo != null && networkInfo.isConnected()) { // check if
																// network is
																// Present
			isAvailable = true;
		}

		return isAvailable;
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

				if (responseCode == HttpURLConnection.HTTP_OK) { // successful
																	// response
					InputStream inputStream = httpURLConnection
							.getInputStream();
					Reader reader = new InputStreamReader(inputStream);
					int contectLength = httpURLConnection.getContentLength();
					char[] charArray = new char[contectLength];
					reader.read(charArray);
					String responseData = new String(charArray);

					JSONObject jsonResponse = new JSONObject(responseData);
					String status = jsonResponse.getString("status");
					Log.v(TAG, status);

					JSONArray jsonPosts = jsonResponse.getJSONArray("posts");
					for (int i = 0; i < jsonPosts.length(); i++) {
						JSONObject jsonPost = jsonPosts.getJSONObject(i);
						String title = jsonPost.getString("title");
						Log.v(TAG, "Post " + i + ": " + title);
					} // end for
				} else {
					Log.e(TAG, "Unsuccessful HTTP Response Code: "
							+ responseCode);
				}
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
