package com.rizartdokollari.blogreader;

import android.app.ListActivity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;

public class MainListActivity extends ListActivity {

	private String[] mAndroidNames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);

		Resources resources = getResources();
		setmAndroidNames(resources.getStringArray(R.array.funny_sentences));

		// <> Specifies data type.
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, getmAndroidNames());
		setListAdapter(adapter);

		// String message = getString(R.string.no_items);
		// Toast.makeText(this, message , Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_list, menu);
		return true;
	}

	public String[] getmAndroidNames() {
		return mAndroidNames;
	}

	public void setmAndroidNames(String[] mAndroidNames) {
		this.mAndroidNames = mAndroidNames;
	}

}
