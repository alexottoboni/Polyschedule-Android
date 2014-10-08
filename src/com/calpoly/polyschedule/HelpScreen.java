package com.calpoly.polyschedule;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HelpScreen extends Activity implements OnClickListener{

	//Widget Vars
	Button okBtn;
	
	public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_screen);
        //Set-Up Widgets
        okBtn = (Button)findViewById(R.id.closeButton);
        okBtn.setOnClickListener(this);
	}
	
	public void onClick(View view) {
		if (view == okBtn) {
			finish();
		}		
	}

}
