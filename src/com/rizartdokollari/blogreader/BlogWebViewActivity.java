package com.rizartdokollari.blogreader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class BlogWebViewActivity extends Activity {
	private String mUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blog_web_view);

		Intent intent = getIntent();
		Uri blogUri = intent.getData();

		mUrl = blogUri.toString();

		// set web url
		WebView webView = (WebView) findViewById(R.id.webView1);
		webView.loadUrl(mUrl);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.blog_web_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemID = item.getItemId();

		if (itemID == R.id.action_share) {
			sharePost();
		} // end if

		return super.onOptionsItemSelected(item);
	}

	private void sharePost() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, getmUrl());
		startActivity(Intent.createChooser(shareIntent,
				getString(R.string.share_chooser_title)));
	}

	public String getmUrl() {
		return mUrl;
	}

}
