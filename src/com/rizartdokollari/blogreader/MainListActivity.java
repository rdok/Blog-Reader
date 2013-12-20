package com.rizartdokollari.blogreader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainListActivity extends ListActivity {

	private String[] mBlogPostTitles;
	public static final int NUMBER_OF_POSTS = 20;
	public static final String TAG = MainListActivity.class.getSimpleName();
	protected JSONObject mBlogData;

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

	public void updateList() {
		if (getmBlogData() == null) {
			// TODO: Handle error
		} else {
			try {
				JSONArray jsonPosts = mBlogData.getJSONArray("posts");
				mBlogPostTitles = new String[jsonPosts.length()];

				for (int i = 0; i < jsonPosts.length(); i++) {
					JSONObject post = jsonPosts.getJSONObject(i);
					String title = post.getString("title");
					title = Html.fromHtml(title).toString();
					mBlogPostTitles[i] = title;
				} // end for

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, mBlogPostTitles);
				setListAdapter(adapter);
			} catch (JSONException e) {
				Log.e(TAG, "Exception caught!", e);
			}
		}
	} // end updateList method

	public String[] getmAndroidNames() {
		return mBlogPostTitles;
	}

	public void setmAndroidNames(String[] mAndroidNames) {
		this.mBlogPostTitles = mAndroidNames;
	}

	public JSONObject getmBlogData() {
		return mBlogData;
	}

	public void setmBlogData(JSONObject mBlogData) {
		this.mBlogData = mBlogData;
	}

	// create a subclass
	private class GetBlogPostsTask extends AsyncTask<Object, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Object... arg0) { // ellipses zero
			// or n
			// arguments
			int responseCode = -1;
			JSONObject jsonResponse = null;
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(
					"http://blog.teamtreehouse.com/api/get_recent_summary/?count="
							+ NUMBER_OF_POSTS);

			try {
				HttpResponse response = client.execute(httpget);
				StatusLine statusLine = response.getStatusLine();
				responseCode = statusLine.getStatusCode();

				if (responseCode == HttpURLConnection.HTTP_OK) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}

					jsonResponse = new JSONObject(builder.toString());
				} else {
					Log.i(TAG, String.format("Unsuccessful HTTP response code: %d",
							responseCode));
				}
			} catch (JSONException e) {
				Log.i(TAG, String.format("JSONException: %s", responseCode));

			} catch (Exception e) {
				Log.i(TAG, String.format("JSONException: %s", responseCode));
			}

			return jsonResponse;
			/*
			 * int responseCode = -1; JSONObject jsonResponse = null;
			 * 
			 * try { URL blogFeedUrl = new URL(
			 * "http://blog.teamtreehouse.com/api/get_recent_summary/?count=" +
			 * NUMBER_OF_POSTS);
			 * 
			 * HttpURLConnection httpURLConnection = (HttpURLConnection)
			 * blogFeedUrl .openConnection(); httpURLConnection.connect();
			 * responseCode = httpURLConnection.getResponseCode();
			 * 
			 * if (responseCode == HttpURLConnection.HTTP_OK) { // successful //
			 * response InputStream inputStream =
			 * httpURLConnection.getInputStream(); Reader reader = new
			 * InputStreamReader(inputStream); int contectLength =
			 * httpURLConnection.getContentLength(); char[] charArray = new
			 * char[contectLength]; reader.read(charArray); String responseData =
			 * new String(charArray);
			 * 
			 * jsonResponse = new JSONObject(responseData); } else { Log.e(TAG,
			 * "Unsuccessful HTTP Response Code: " + responseCode); } } catch
			 * (MalformedURLException e) { Log.e(TAG,
			 * "MalformedURLException caught: ", e); } catch (IOException e) {
			 * Log.e(TAG, "IOException caught: " + e.getMessage(), e); } catch
			 * (Exception e) { Log.e(TAG, "Exception caught: ", e); } // end catch
			 * return jsonResponse;
			 */
		} // end doInBackground method

		@Override
		protected void onPostExecute(JSONObject result) {
			mBlogData = result;
			updateList();
		} // end onPostExecute method
	} // end GetBlogPostsTask subclass
}
