package com.calpoly.polyschedule;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.format.Time;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener, OnLongClickListener{
	
	/* Static Vars */
	final static String PSPREFS = "PSPREFS";
	final static String COURSE_COUNT = "COURSE_COUNT";
	final static String COURSE = "COURSE";
	final static String EDIT_DATA = "EDIT_DATA";
	final static String DB_NUMBER = "DB_NUMBER";	
	/* Vars */
	private int courseCount = 0;	
	final Context context = this;
	private String tempCourseData;
	private String dayChar;
	@SuppressWarnings("unused")
	private String today;
	Typeface OLD_SANS;
	private Time t = new Time(Time.getCurrentTimezone());
	/* Widget Vars */
	ImageView headerLogo;
	Button configureBtn;
	Button resetBtn;
	TextView headerText;
	LinearLayout ll;
	Button[] courseButtons;
	Course[] courseData;
	Button forwardDayBtn;
	Button backDayBtn;
	int counter;
	int index;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// Call functions onCreate
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateHeaderText(null);
        // Initialize Typeface
        OLD_SANS = Typeface.createFromAsset(getAssets(),"fonts/OldSansBlack.ttf");
        // Initialize Widgets
        headerLogo = (ImageView)findViewById(R.id.psLogo);
        configureBtn = (Button)findViewById(R.id.configureBtn);
        resetBtn = (Button)findViewById(R.id.resetBtn);
        headerText = (TextView)findViewById(R.id.headerText);
        forwardDayBtn = (Button)findViewById(R.id.forwardDayButton);
        backDayBtn = (Button)findViewById(R.id.backDayButton);
        ll = (LinearLayout)findViewById(R.id.buttonLayout);
        // Set onClickListeners
        headerLogo.setOnClickListener(this);
        configureBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);        
        forwardDayBtn.setOnClickListener(this);        
        backDayBtn.setOnClickListener(this);        
		// Set font for HeaderText
        headerText.setTypeface(OLD_SANS);
        
        // Example Notification
       // mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
      //  final Notification notifyDetails = new Notification(R.drawable.btnbackday,"New Alert, Click Me!",System.currentTimeMillis());
      //  Context context = getApplicationContext();
     //   CharSequence contentTitle = "Notification Details...";
     //   CharSequence contentText = "Browse Android Official Site by clicking me";
       // Intent notifyIntent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse("http://www.android.com"));
   //     PendingIntent intent = 
     //         PendingIntent.getActivity(MainActivity.this, 0, 
    //          notifyIntent, android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
    //    notifyDetails.setLatestEventInfo(context, contentTitle, contentText, intent);
    //    mNotificationManager.notify(SIMPLE_NOTFICATION_ID, notifyDetails);
        
    }
    
    public void setToday() {    	
    	t.setToNow();
    	today = t.format("%A");    	
    }
    
    public boolean checkTime(String startTime, String endTime) {
    	String formatString;    	
    	t.setToNow();
    	formatString = t.format("%m/%d/%Y");
    	SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mmaa", Locale.ENGLISH);
    	Date now = new Date();    	  	
    	try {
    	Date start = format.parse(formatString + " " + startTime);
    	Date end = format.parse(formatString + " " + endTime);    	
    	if (now.after(start) && now.before(end)) {
    		return true;
    	} else {
    		return false;
    	}
    	} catch (Exception e) {    		
    		return false;
    	}    	
    }
    
    public void addCourses(int courseCount) {    	
    	SharedPreferences psShared = getSharedPreferences(PSPREFS, Activity.MODE_PRIVATE);
    	t.setToNow();
    	if (courseCount == -1) {
    		addButton("No Classes Found\nAdd Some Courses\nBy Clicking Add Class", 0, false);
    	} else {    		
    		for (int x = 0; x < courseCount; x++) {
        		tempCourseData = psShared.getString(COURSE + x, "Failed to load course");
        		// Make sure course didn't fail to load
        		if (!tempCourseData.equals("Failed to load course")) {
        			// Define a new Course
        			courseData[x] = new Course(tempCourseData);
        			//Check for same-day        			
        			if (courseData[x].getDays().contains(dayChar)) {
        				// Make sure it's the current day, then make 'em yellow and GREEEN
        				if (headerText.getText().toString().contains(t.format("%A"))) {
        					// Check for current class, if so, make yellow
        					if (checkTime(courseData[x].getStartTime(), courseData[x].getEndTime())) {
            					addButton(courseData[x].getCourseTitle() + "\n" + courseData[x].getTimeInterval(), x, true);
            				} else {
            					addButton(courseData[x].getCourseTitle() + "\n" + courseData[x].getTimeInterval(), x, false);
            				}
        				} else {
        					// Not current day, just make 'em all GREEEN
        					addButton(courseData[x].getCourseTitle() + "\n" + courseData[x].getTimeInterval(), x, false);
        				}
        			}
        		}        		
        	}
    	}    	    	
    }
    
    public void addButton(String btnText, int idNumber, boolean isCurrentClass) {
    	courseButtons[idNumber] = new Button(this);
    	if (isCurrentClass) {
    		courseButtons[idNumber].setBackgroundResource(R.drawable.coursebuttonyellow);
    	} else {
    		courseButtons[idNumber].setBackgroundResource(R.drawable.coursebuttongreen);
    	}    	    	
    	courseButtons[idNumber].setId(idNumber);
    	courseButtons[idNumber].setText(btnText);
    	courseButtons[idNumber].setOnClickListener(this);
    	courseButtons[idNumber].setLongClickable(true);
    	courseButtons[idNumber].setOnLongClickListener(this);
    	courseButtons[idNumber].setTextColor(Color.BLACK);
    	courseButtons[idNumber].setTextSize(20.0f);   	
    	courseButtons[idNumber].setTypeface(OLD_SANS, Typeface.NORMAL);
    	ll.addView(courseButtons[idNumber]);
    }
    
    public void loadPreferences() {
    	//Load Number of Current Courses
    	SharedPreferences psShared = getSharedPreferences(PSPREFS, Activity.MODE_PRIVATE);
    	courseCount = psShared.getInt(COURSE_COUNT, -1);    	
    	if (courseCount != -1) {    		
    		courseCount++;
    		courseButtons = new Button[courseCount];
    		courseData = new Course[courseCount];
    	} else {
    		courseButtons = new Button[1];
    	}
    	addCourses(courseCount);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {        
        return true;
    }

	public void onClick(View view) {
		AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this,android.R.style.Theme_Dialog));		
		if (view == resetBtn) {
			alert.setTitle(getString(R.string.clear_data));
			alert.setMessage(getString(R.string.confirm_clear));
			// Setting Positive "Yes" Button
	        alert.setPositiveButton((getString(R.string.yes)), new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog,int which) {
	            	//Clear Courses...
	            	removePreferences();
	            	onResume();
	            }
	        });	 
	        // Setting Negative "NO" Button
	        alert.setNegativeButton((getString(R.string.no)), new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	//Cancel dialog - Do nothing	            	
	            dialog.cancel();
	            }
	        });	        
	        AlertDialog dialog = alert.create();
	        dialog.show();
	        Button neg = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
	        neg.setBackgroundDrawable(getResources().getDrawable(R.layout.customalertbutton));
	        Button pos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
	        pos.setBackgroundDrawable(getResources().getDrawable(R.layout.customalertbutton));	        
		} else if (view == configureBtn) {
			// Configure button is chosen -> launch ConfigureActivity
			Intent configureScreen = new Intent(this, ConfigureActivity.class);
			this.startActivity(configureScreen);
		} else if (view == forwardDayBtn) {
			// NextDay button is chosen -> advance the day
			advanceDay();			
		} else if (view == backDayBtn) {
			// Previous Day button is chosen -> go back a day
			previousDay();
		} else if (view == headerLogo) {
			Intent helpScreen = new Intent(this, HelpScreen.class);
			this.startActivity(helpScreen);
		} else {
			//Checking if a courseBtn was clicked
			// Checking if instruction button was clicked
			if (courseData == null) {
				Intent configureScreen = new Intent(this, ConfigureActivity.class);
				this.startActivity(configureScreen);
			} else {
				StringBuilder sb = new StringBuilder();
				counter = 0;
				for (int i = 0; i < courseCount; i++) {
					if (view == courseButtons[i]) {
						counter = i;
						// Create alert dialog with information about course
						alert.setTitle(courseData[i].getCourseTitle());
						sb.append("Professor: " + courseData[i].getProfessor()
								+ "\n");
						sb.append("Days: " + courseData[i].getDays() + "\n");
						sb.append("Time: " + courseData[i].getTimeInterval()
								+ "\n");
						sb.append("Location: " + courseData[i].getLocation());
						alert.setMessage(sb.toString());
						alert.setPositiveButton((getString(R.string.okButton)),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
									}
								});
						alert.setNegativeButton(getString(R.string.editButton),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										editCourse(courseData[counter], counter);
										dialog.cancel();
									}
								});
						AlertDialog dialog = alert.create();
						dialog.show();
						Button neg = dialog
								.getButton(DialogInterface.BUTTON_NEGATIVE);
						 neg.setBackgroundDrawable(getResources().getDrawable(R.layout.customalertbutton));
						Button pos = dialog
								.getButton(DialogInterface.BUTTON_POSITIVE);
						 pos.setBackgroundDrawable(getResources().getDrawable(R.layout.customalertbutton));
					}
				}

			}
		}				
	}
	
	@Override
	public void onResume() {
		super.onResume();
		ll = (LinearLayout)findViewById(R.id.buttonLayout);
		ll.removeAllViews();
		loadPreferences();
	}
	
	public void removePreferences() {
		SharedPreferences psShared = getSharedPreferences(PSPREFS, Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = psShared.edit();
    	editor.remove(COURSE_COUNT);
    	for (int i = 0; i < courseCount; i++) {
    		editor.remove(COURSE + i);
    	}
    	editor.commit();
    	courseData = null;
	}
	
	public void advanceDay() {
		if (headerText.getText().toString().contains(getString(R.string.monday))) {
			updateHeaderText(getString(R.string.tuesday));
		} else if (headerText.getText().toString().contains(getString(R.string.tuesday))) {
			updateHeaderText(getString(R.string.wednesday));
		} else if (headerText.getText().toString().contains(getString(R.string.wednesday))) {
			updateHeaderText(getString(R.string.thursday));
		} else if (headerText.getText().toString().contains(getString(R.string.thursday))) {			
			updateHeaderText(getString(R.string.friday));
		} else if (headerText.getText().toString().contains(getString(R.string.friday))) {
			updateHeaderText(getString(R.string.monday));
		} else if (headerText.getText().toString().contains(getString(R.string.saturday))) {
			updateHeaderText(getString(R.string.monday));
		} else {			
			updateHeaderText(getString(R.string.monday));
		}
		onResume();		
	}
	
	public void previousDay() {
		if (headerText.getText().toString().contains(getString(R.string.monday))) {
			updateHeaderText(getString(R.string.friday));
		} else if (headerText.getText().toString().contains(getString(R.string.tuesday))) {
			updateHeaderText(getString(R.string.monday));
		} else if (headerText.getText().toString().contains(getString(R.string.wednesday))) {
			updateHeaderText(getString(R.string.tuesday));
		} else if (headerText.getText().toString().contains(getString(R.string.thursday))) {
			updateHeaderText(getString(R.string.wednesday));
		} else if (headerText.getText().toString().contains(getString(R.string.friday))) {
			updateHeaderText(getString(R.string.thursday));
		} else if (headerText.getText().toString().contains(getString(R.string.saturday))) {
			updateHeaderText(getString(R.string.friday));
		} else {			
			updateHeaderText(getString(R.string.friday));
		}
		onResume();
	}
	
	public void updateHeaderText(String dayString) {
		// To avoid nullPointers - Initializing headerText within this method \\
		String s;
		headerText = (TextView)findViewById(R.id.headerText);
		Time t = new Time(Time.getCurrentTimezone());
		t.setToNow();
		s = t.format("%A");		
		//Check if we're changing days
		if (dayString == null) {
			//Not changing days, write (Today) next to current day
			//Update day character to current day
			if (s.contains(getString(R.string.monday))) {
				headerText.setText("Your Schedule For: " + "\n" + getString(R.string.monday) + " (Today)");
				dayChar = "M";
			} else if (s.contains(getString(R.string.tuesday))) {
				headerText.setText("Your Schedule For: " + "\n" + getString(R.string.tuesday) + " (Today)");
				dayChar = "T";
			} else if (s.contains(getString(R.string.wednesday))) {
				headerText.setText("Your Schedule For: " + "\n" + getString(R.string.wednesday) + " (Today)");
				dayChar = "W";
			} else if (s.contains(getString(R.string.thursday))) {
				headerText.setText("Your Schedule For: " + "\n" + getString(R.string.thursday) + " (Today)");
				dayChar = "R";
			} else if (s.contains(getString(R.string.friday))) {
				headerText.setText("Your Schedule For: " + "\n" + getString(R.string.friday) + " (Today)");
				dayChar = "F";
			} else if (s.contains(getString(R.string.saturday))) {
				headerText.setText("Your Schedule For: " + "\n" + getString(R.string.saturday) + " (Today)");
				dayChar = "S";
			} else {
				headerText.setText("Your Schedule For: " + "\n" + getString(R.string.sunday) + " (Today)");
				dayChar = "S";				
			}
		} else {
			//Changing days, update headerText accordingly
			//Update dayChar with proper abbreviation
			//Check if switching to current day, if so add (Today)
			if (s.equals(dayString)) {
				//Checking for current day -> Add (Today)
				headerText.setText("Your Schedule For: " + "\n" + dayString + " (Today)");
				//Update dayChar -> Check for instance of Thursday
				if (dayString.contains(getString(R.string.thursday))) {
					dayChar ="R";
				} else {
					dayChar = dayString.substring(0, 1);
				}
			} else {
				//Changing days -> Update headerText accordingly
				headerText.setText("Your Schedule For: " + "\n" + dayString);
				//Update dayChar -> Check for instance of Thursday
				if (dayString.contains(getString(R.string.thursday))) {					
					dayChar ="R";
				} else {					
					dayChar = dayString.substring(0, 1);
				}
			}
		}
	}
	
	public void editCourse(Course course, int dbNum) {		
		Intent edit = new Intent(context, ConfigureActivity.class);
		edit.putExtra(EDIT_DATA, course.toString());
		edit.putExtra(DB_NUMBER, dbNum);		
		this.startActivity(edit);
	}

	public boolean onLongClick(View view) {		
		AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this,android.R.style.Theme_Dialog));		
		for (int i = 0; i < courseCount; i++) {				
			if (view == courseButtons[i]) {
				index = i;
				alert.setTitle("Delete " + courseData[i].getCourseTitle());
				alert.setMessage("Are you sure you want to delete this course?");
				alert.setPositiveButton((getString(R.string.yes)), new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {		            	
		            	removeCourse(index);
		            }
		        });
				alert.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {						
					public void onClick(DialogInterface dialog, int which) {						
						dialog.cancel();							
					}
				});
				AlertDialog dialog = alert.create();
		        dialog.show();
		        Button neg = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		        neg.setBackgroundDrawable(getResources().getDrawable(R.layout.customalertbutton));
		        Button pos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		        pos.setBackgroundDrawable(getResources().getDrawable(R.layout.customalertbutton));
			}				
		}
		return false;
	}
	
	public void removeCourse(int index) {
		ArrayList<String> courses = new ArrayList<String>();
		int currCourseAmount = courseCount - 2;
		SharedPreferences psShared = getSharedPreferences(PSPREFS, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = psShared.edit();
		for (int i = 0; i < courseCount; i++) {
			if (psShared.getString(COURSE + i, null) != null) {
				courses.add(psShared.getString(COURSE + i, null));
			}			
		}
		courses.remove(index);
		removePreferences();
		editor.putInt(COURSE_COUNT, currCourseAmount);
		for (int i = 0; i < courses.size(); i++) {
			editor.putString(COURSE + i, courses.get(i));
		}
		editor.commit();
		onResume();
	}
}