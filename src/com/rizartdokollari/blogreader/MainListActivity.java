package com.rizartdokollari.blogreader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainListActivity extends ListActivity {

	private static final String KEY_AUTHOR = "author";
	private static final String KEY_TITLE = "title";

	public static final int NUMBER_OF_POSTS = 20;
	public static final String TAG = MainListActivity.class.getSimpleName();
	protected JSONObject mBlogData;
	protected ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);

		if (isNetwokAvailable()) {
			mProgressBar.setVisibility(View.VISIBLE);
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

	public void handleBlogResponse() {
		mProgressBar.setVisibility(View.INVISIBLE);

		if (getmBlogData() == null) {
			updateDisplayForError();
		} else {
			try {
				JSONArray jsonPosts = mBlogData.getJSONArray("posts");
				ArrayList<HashMap<String, String>> blogPosts = new ArrayList<HashMap<String, String>>();

				for (int i = 0; i < jsonPosts.length(); i++) {
					JSONObject post = jsonPosts.getJSONObject(i);
					String title = post.getString(KEY_TITLE);
					title = Html.fromHtml(title).toString();
					String author = post.getString(KEY_AUTHOR);
					author = Html.fromHtml(author).toString();

					HashMap<String, String> blogPost = new HashMap<String, String>();
					blogPost.put(KEY_TITLE, title);
					blogPost.put(KEY_AUTHOR, author);

					blogPosts.add(blogPost);
				} // end for

				String[] keys = { KEY_TITLE, KEY_AUTHOR };
				int[] ids = { android.R.id.text1, android.R.id.text2 };
				SimpleAdapter adapter = new SimpleAdapter(this, blogPosts,
						android.R.layout.simple_list_item_2, keys, ids);

				setListAdapter(adapter);
			} catch (JSONException e) {
				Log.e(TAG, "Exception caught!", e);
			} // end try catch
		} // end else
	} // end updateList method

	private void updateDisplayForError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.title));
		builder.setMessage(getString(R.string.error_message));
		builder.setPositiveButton(android.R.string.ok, null);
		AlertDialog dialog = builder.create();
		dialog.show();

		TextView textViewEmpty = (TextView) getListView().getEmptyView();
		textViewEmpty.setText(getString(R.string.no_items));
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
		} // end doInBackground method

		@Override
		protected void onPostExecute(JSONObject result) {
			mBlogData = result;
			handleBlogResponse();
		} // end onPostExecute method
	} // end GetBlogPostsTask subclass
}
