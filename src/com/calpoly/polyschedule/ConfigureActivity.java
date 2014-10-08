package com.calpoly.polyschedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class ConfigureActivity extends Activity implements OnClickListener{
	
	/* Static Vars */
	final static String PSPREFS = "PSPREFS";
	final static String COURSE_COUNT = "COURSE_COUNT";
	final static String COURSE = "COURSE";
	final static String PREFIX = "PREFIX";
	final static String BUILDING = "BUILDING";
	final static String SHORTBUILDING = "SHORTBUILDING";
	final static String END_TIME = "END_TIME";
	final static String START_TIME ="START_TIME";
	final static String EDIT_DATA = "EDIT_DATA";
	final static String DB_NUMBER = "DB_NUMBER";
	final static String POSITION = "POSITION";
	final static int ACTIVITY_COURSE_PREFIX = 1;
	final static int ACTIVITY_BUILDINGS = 2;
	final static int ACTIVITY_START_TIME = 3;
	final static int ACTIVITY_END_TIME = 4;	
	/* Widget Vars */
	Button saveButton, backButton, selectSubjectButton, selectBuildingButton, startTimeButton, endTimeButton, coursePrefix, buildingName;
	TextView courseNumber, roomNumber;
	CheckBox monday, tuesday, wednesday, thursday, friday;
	AutoCompleteTextView autoComplete;
	View scrollView;
	/* Vars */	
	private int courseCount;
	private String buildingData;
	private String[] courseDataArray;
	private String editData;
	private boolean isEditMode;
	private Course[] courseData;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calender_configure);              
       
        // Stops the Android On-Screen keyboard from opening on activity start
        getWindow().setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Initialize Widgets
        autoComplete = (AutoCompleteTextView)findViewById(R.id.editText_Professor_Name);
        String[] professors = getResources().getStringArray(R.array.Professors);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, professors);
        autoComplete.setAdapter(adapter);
        startTimeButton = (Button)findViewById(R.id.start_Time_Button);
        startTimeButton.setOnClickListener(this);
        endTimeButton = (Button)findViewById(R.id.end_Time_Button);
        endTimeButton.setOnClickListener(this);
        selectBuildingButton = (Button)findViewById(R.id.select_Building_Button);
        selectBuildingButton.setOnClickListener(this);
        selectSubjectButton = (Button)findViewById(R.id.select_Subject_Button);
        selectSubjectButton.setOnClickListener(this);
        saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        coursePrefix = (Button)findViewById(R.id.select_Subject_Button);
        buildingName = (Button)findViewById(R.id.select_Building_Button);
        courseNumber = (TextView)findViewById(R.id.editText_Course_Number);
        roomNumber = (TextView)findViewById(R.id.editText_Room_Number);
        roomNumber.setRawInputType(Configuration.KEYBOARD_QWERTY);
        scrollView = (View)findViewById(R.id.MainLayout);
              
        // Check Boxes
        monday = (CheckBox)findViewById(R.id.checkBox1);
        tuesday = (CheckBox)findViewById(R.id.checkBox2);
        wednesday = (CheckBox)findViewById(R.id.checkBox3);
        thursday = (CheckBox)findViewById(R.id.checkBox4);
        friday = (CheckBox)findViewById(R.id.checkBox5);
        
      //If not editMode, load prefs
        if (!loadEditData()) {
        	loadPreferences();
        }  
        
        backButton = (Button)this.findViewById(R.id.backButton);
        backButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		finish();
        	}
        });
        	}

	public void onClick(View v) {
		//Fired when object is clicked
		//Determines which button/control was clicked on, proceeds from there
		if (v == saveButton) {
			if (saveData()) {
				if (isEditMode) {
					finish();
				} else {
					clearForm();
					courseNumber.requestFocus();
					scrollView.scrollTo(0, 0);
					InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
				    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				}				
			}							
		}
		if (v == selectSubjectButton) {
			Intent coursePrefixList = new Intent(getApplicationContext(), CoursePrefixList.class);
			this.startActivityForResult(coursePrefixList, ACTIVITY_COURSE_PREFIX);
		}
		if (v == selectBuildingButton) {
			Intent selectBuildingButton = new Intent(getApplicationContext(), BuildingList.class);
			this.startActivityForResult(selectBuildingButton, ACTIVITY_BUILDINGS);
		}
		if (v == startTimeButton) {
			Intent startTimeButton = new Intent(getApplicationContext(), StartTimeList.class);
			this.startActivityForResult(startTimeButton, ACTIVITY_START_TIME);
		}
		if (v == endTimeButton) {
			Intent endTimeButton = new Intent(getApplicationContext(), EndTimeList.class);
			this.startActivityForResult(endTimeButton, ACTIVITY_END_TIME);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		//Switches between requestCodes to determine which ListView was ended
		//Sets text according to which ListView was closed
		switch (requestCode) {
			case (ACTIVITY_COURSE_PREFIX) : {
				if (data != null) {
					coursePrefix.setText(parseText(data.getExtras().getString(PREFIX)));
				}
				break;
			}
			case (ACTIVITY_BUILDINGS) : {
				if (data != null) {
					buildingName.setText(getShortName(data.getExtras().getInt(POSITION)));
					buildingData = data.getExtras().getString(BUILDING);	
				}
				break;
			}
			case (ACTIVITY_START_TIME) : {
				if (data != null) {
					startTimeButton.setText(data.getExtras().getString(START_TIME));
					if (!isTimeSane()) {
						alert.setTitle("Selected Time Error");
						alert.setMessage("Ending Time Occurs Before Start Time");
						alert.setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
			                public void onClick(DialogInterface dialog, int id) {                	
			        			scrollView.scrollTo(0, 450);
			                	dialog.cancel(); 
			                }
			            });			
						AlertDialog dialog = alert.create();
						dialog.show();
				        Button pos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
				        pos.setBackgroundDrawable(getResources().getDrawable(R.layout.customalertbutton));
					}
				}
				break;
			}
			case (ACTIVITY_END_TIME) : {
				if (data != null) {
					endTimeButton.setText(data.getExtras().getString(END_TIME));
					if (!isTimeSane()) {
						alert.setTitle("Selected Time Error");
						alert.setMessage("Ending Time Occurs Before Start Time");
						alert.setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
			                public void onClick(DialogInterface dialog, int id) {                	
			        			scrollView.scrollTo(0, 450);
			                	dialog.cancel(); 
			                }
			            });			
						AlertDialog dialog = alert.create();
						dialog.show();
				        Button pos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
				        pos.setBackgroundDrawable(getResources().getDrawable(R.layout.customalertbutton));
					}
				}
				break;
			}
		}
	}	

	private boolean saveData() {		
		if (checkForErrors()) {			
			Course course = new Course(getCourseData());
			savePreferences(course.toString());
			return true;
		} else {
			return false;
		}	
	}
	
	private String[] getCourseData() {
		String[] courseData = new String[8];
		courseData[0] = coursePrefix.getText().toString();
		courseData[1] = courseNumber.getText().toString().replace(",", "");
		courseData[2] = buildingData;
		courseData[3] = roomNumber.getText().toString().replace(",", "");
		courseData[4] = getCheckedDays();
		courseData[5] = startTimeButton.getText().toString();
		courseData[6] = endTimeButton.getText().toString();
		courseData[7] = autoComplete.getText().toString().replace(",", "");
		courseDataArray = courseData.clone();
		return courseData;		
	}
	
	private String getCheckedDays() {
		StringBuilder sb = new StringBuilder();
		if (monday.isChecked()) {
			sb.append("M");
		}
		if (tuesday.isChecked()) {
			sb.append("T");
		}
		if (wednesday.isChecked()) {
			sb.append("W");
		}
		if (thursday.isChecked()) {
			sb.append("R");
		}
		if (friday.isChecked()) {
			sb.append("F");
		}
		return sb.toString();		
	}

	private boolean checkForErrors() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		//Subject Button Blank
		if (coursePrefix.getText().equals("Select Subject")) {
			// Create AlertDialog -- Show whatever error was thrown
			alert.setTitle("No Subject Selected");
			alert.setMessage("Select a Course Subject");
			alert.setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {                	
        			scrollView.scrollTo(0, 0);
                	dialog.cancel(); 
                }
            });			
			AlertDialog dialog = alert.create();
			dialog.show();
	        Button pos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
	        pos.setBackgroundDrawable(getResources().getDrawable(R.layout.customalertbutton));	        
			return false;
			//CourseNumber TextView blank
		} else if (courseNumber.getText().toString().equals("")){
			alert.setTitle("No Course Number");
			alert.setMessage("Enter a Course Number");
			alert.setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {                	
        			scrollView.scrollTo(0, 0);
                	dialog.cancel(); 
                }
            });			
			AlertDialog dialog = alert.create();
			dialog.show();
	        Button pos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
	        pos.setBackgroundDrawable(getResources().getDrawable(R.layout.customalertbutton));
			return false;
			//BuildingName Button blank
		} else if (buildingName.getText().toString().equals("Select Building")) {
			alert.setTitle("No Building Selected");
			alert.setMessage("Select a Building");
			alert.setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {                	
        			scrollView.scrollTo(0, 250);
                	dialog.cancel(); 
                }
            });			
			AlertDialog dialog = alert.create();
			dialog.show();
	        Button pos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
	        pos.setBackgroundDrawable(getResources().getDrawable(R.layout.customalertbutton));
			return false;
			//RoomNumber TextView blank
		} else if (roomNumber.getText().toString().equals("")) {
			alert.setTitle("No Room Number");
			alert.setMessage("Enter a Room Number");
			alert.setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {                	
        			scrollView.scrollTo(0, 250);
                	dialog.cancel(); 
                }
            });			
			AlertDialog dialog = alert.create();
			dialog.show();
	        Button pos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
	        pos.setBackgroundDrawable(getResources().getDrawable(R.layout.customalertbutton));
			return false;
			//Start Time Button blank
		} else if (startTimeButton.getText().toString().equals("Start Time")) {
			alert.setTitle("No Start Time Selected");
			alert.setMessage("Select a Start Time");
			alert.setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {                	
        			scrollView.scrollTo(0, 450);
                	dialog.cancel(); 
                }
            });			
			AlertDialog dialog = alert.create();
			dialog.show();
	        Button pos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
	        pos.setBackgroundDrawable(getResources().getDrawable(R.layout.customalertbutton));
			return false;
			//End Time Button blank
		} else if (endTimeButton.getText().toString().equals("End Time")) {
			alert.setTitle("No End Time Selected");
			alert.setMessage("Select an End Time");
			alert.setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {                	
        			scrollView.scrollTo(0, 450);
                	dialog.cancel(); 
                }
            });			
			AlertDialog dialog = alert.create();
			dialog.show();
	        Button pos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
	        pos.setBackgroundDrawable(getResources().getDrawable(R.layout.customalertbutton));
			return false;
			//Professor TextView blank
		} else if (autoComplete.getText().toString().equals("")) {
			alert.setTitle("No Professor Entered");
			alert.setMessage("Enter a Professor's Name");
			alert.setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {                	
        			scrollView.scrollTo(0, 550);
                	dialog.cancel(); 
                }
            });			
			AlertDialog dialog = alert.create();
			dialog.show();
	        Button pos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
	        pos.setBackgroundDrawable(getResources().getDrawable(R.layout.customalertbutton));
			return false;
			//No checkboxes checked
		} else if (checkBoxesChecked() == false) {
			alert.setTitle("No Days Selected");
			alert.setMessage("Select Class Day(s)");
			alert.setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {                	
                  dialog.cancel(); 
                }
            });			
			AlertDialog dialog = alert.create();
			dialog.show();
	        Button pos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
	        pos.setBackgroundDrawable(getResources().getDrawable(R.layout.customalertbutton));
			return false;
		} else if (isTimeSane() == false) {
			alert.setTitle("Selected Time Error");
			alert.setMessage("Ending Time Occurs Before Start Time");
			alert.setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {                	
        			scrollView.scrollTo(0, 450);
                	dialog.cancel(); 
                }
            });			
			AlertDialog dialog = alert.create();
			dialog.show();
	        Button pos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
	        pos.setBackgroundDrawable(getResources().getDrawable(R.layout.customalertbutton));
			return false;
		} else {
			return true;
		}		
	}
	
	public boolean isTimeSane() {
		// Checking if either start or end has not been assigned yet
		if (startTimeButton.getText().toString().equals(getString(R.string.start_time)) || endTimeButton.getText().toString().equals(getString(R.string.end_time))) {
			// Checking if buttons contain default text
			// If so, return true so we don't detect an error
			return true;
		} else {
			// Create a date formatter in terms of the date we use (hh:mmAM/PM)
			SimpleDateFormat sf = new SimpleDateFormat("hh:mmaa", Locale.ENGLISH);
			sf.setTimeZone(TimeZone.getDefault());
			// Attempt to parse into a real Date
			// Compare two intervals for sanity
			try {
				Date start = sf.parse(startTimeButton.getText().toString());
				Date end = sf.parse(endTimeButton.getText().toString());
				if (start.before(end)) {
					return true;
				} else {
					return false;
				}
			} catch (ParseException e) {
				//Parse Error Occurred - Return false
				return false;
			}
		}		
	}
	
	public boolean checkBoxesChecked() {
		//Check if any day is checked -> return true -> else false
		if (monday.isChecked() || tuesday.isChecked() || wednesday.isChecked() || thursday.isChecked() || friday.isChecked() ){		
			return true;
		} else {
			return false;				
		}
	}
	
    public void savePreferences(String courseData) {
    	SharedPreferences psShared = getSharedPreferences(PSPREFS, Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = psShared.edit();
    	if (isEditMode) {
    		editor.putString(COURSE + courseCount, courseData);    	
        	editor.commit();
        	Toast.makeText(this, "Course was successfully updated.", Toast.LENGTH_LONG).show();
    	} else {    		
    		editor.putInt(COURSE_COUNT, courseCount + 1);
    		courseCount++;
        	this.courseData[courseCount] = new Course(courseData);
        	courseCount++;
        	for (int i = 0; i < courseCount; i++) {
        		editor.putString(COURSE + i, sortCourses().get(i));
        	}        	
        	editor.commit();
        	AlertDialog.Builder alert = new AlertDialog.Builder(this);
        	alert.setTitle("Course Saved");
        	alert.setMessage("Your course titled " + courseDataArray[0] + " - " + courseDataArray[1] + " was saved successfully.");
        	alert.setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {                	
                  dialog.cancel(); 
                }
            });	
        	AlertDialog dialog = alert.create();
			dialog.show();
	        Button pos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
	        pos.setBackgroundDrawable(getResources().getDrawable(R.layout.customalertbutton));        	
        	loadPreferences();
    	}    	
    }
    
    public ArrayList<String> sortCourses() {
    	ArrayList<String> courseSort = new ArrayList<String>();
    	for (int i = 0; i < courseCount; i++) {
    		// Start off list with first item
    		if (i == 0) {
    			courseSort.add(courseData[i].toString());
    		} else {
        		// Begin comparison checks
        		if (isBeforeTime(courseData[i - 1].getStartTime(), courseData[i].getStartTime())) {
        			courseSort.add(0, courseData[i].toString());
        		} else {
        			courseSort.add(courseData[i].toString());
        		}
    		}
    	}
    	return courseSort;
    }
    
    private boolean isBeforeTime(String compareTime, String newTime) {
    	SimpleDateFormat format = new SimpleDateFormat("hh:mmaa", Locale.ENGLISH);
    	try {
    	Date old = format.parse(compareTime);
    	Date curr = format.parse(newTime);
    	if (old.after(curr)) {
    		return true;
    	} else {
    		return false;
    	}    	
    	} catch (Exception e) {    		
    		return false;
    	}    	
    }
    
    public void loadPreferences() {
    	//Load Number of Current Courses
    	SharedPreferences psShared = getSharedPreferences(PSPREFS, Context.MODE_PRIVATE);
    	courseCount = psShared.getInt(COURSE_COUNT, -1);
    	if (courseCount == -1) {
    		courseData = new Course[1];
    	} else {
        	courseData = new Course[courseCount + 2];
        	for (int i = 0; i < courseCount + 2; i++) {
        		if (psShared.getString(COURSE + i, null) != null) {
        			courseData[i] = new Course(psShared.getString(COURSE + i, null));
        		}    		
        	} 
    	}    	
    }
   
    public String parseText(String data) {
    	String[] arr = data.split(" - ");
    	return arr[0];    	
    }
    
    public void clearForm() {
    	coursePrefix.setText("Select Subject");
    	courseNumber.setText("");
    	buildingName.setText("Select Building");
    	roomNumber.setText("");
    	monday.setChecked(false);
    	tuesday.setChecked(false);
    	wednesday.setChecked(false);
    	thursday.setChecked(false);
    	friday.setChecked(false);
    	startTimeButton.setText("Start Time");
    	endTimeButton.setText("End Time");
    	autoComplete.setText("");
    }
    
    private boolean loadEditData() {    	
    	if (getIntent().hasExtra(EDIT_DATA) && getIntent().hasExtra(DB_NUMBER)) {    			
    		editData = getIntent().getExtras().getString(EDIT_DATA);    		
    		courseCount = getIntent().getExtras().getInt(DB_NUMBER);    		
    		isEditMode = true;    		
    		Course course = new Course(editData);    		
    		selectSubjectButton.setText(course.getCourseSubject());
    		courseNumber.setText(course.getCourseNumber());
    		selectBuildingButton.setText(getShortBuilding(course.getBuilding()));
    		buildingData = course.getBuilding();
    		roomNumber.setText(course.getRoomNumber());
    		startTimeButton.setText(course.getStartTime());
    		endTimeButton.setText(course.getEndTime());
    		autoComplete.setText(course.getProfessor());
    		if (course.getDays().contains("M")) {
    			monday.setChecked(true);
    		}
    		if (course.getDays().contains("T")) {
    			tuesday.setChecked(true);
    		}
    		if (course.getDays().contains("W")) {
    			wednesday.setChecked(true);
    		}
    		if (course.getDays().contains("R")) {
    			thursday.setChecked(true);
    		}
    		if (course.getDays().contains("F")){
    			friday.setChecked(true);
    		}
    		return true;
    	} else {
    		isEditMode = false;
    		return false;
    	}
    }
    
    public String getShortName(int position) {
    	String[] shortBuilding = getResources().getStringArray(R.array.short_building_names_and_numbers);
    	return shortBuilding[position];
    }
    
    public String getShortBuilding(String buildingData) {
    	String[] buildings = getResources().getStringArray(R.array.building_names_and_numbers);
    	String[] shortNames = getResources().getStringArray(R.array.short_building_names_and_numbers);
    	String[] arr = buildingData.split(" - ");
    	for (int i = 0; i < buildings.length; i++) {
    		if (buildings[i].contains(arr[0])) {
    			return shortNames[i];
    		}
    	}
		return buildingData;    	
    }        
}

