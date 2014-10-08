package com.calpoly.polyschedule;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ShortBuildingList extends ListActivity {
	
	final static String SHORTBUILDING = "SHORTBUILDING";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview);
		String[] values = getResources().getStringArray(R.array.short_building_names_and_numbers);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
		
		// Accepts one selection
		ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setFocusableInTouchMode(true);
	}
	
	@Override
	public void onListItemClick (ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent data = new Intent();
		data.putExtra(SHORTBUILDING, (String)l.getAdapter().getItem(position));
		setResult(Activity.RESULT_OK, data);
		finish();
	}
}
