package com.calpoly.polyschedule;

public class Course {
	
	private String courseSubject;
	private String courseNumber;
	private String building;
	private String roomNumber;
	private String startTime;
	private String endTime;
	private String days;
	private String professor;
	private String[] courseData = new String[8];
	
	public Course(String[] courseData) {
		// Initialize all fields of Course
		setCourseSubject(courseData[0]);
		setCourseNumber(courseData[1]);
		setBuilding(courseData[2]);
		setRoomNumber(courseData[3]);
		setDays(courseData[4]);	
		setStartTime(courseData[5]);
		setEndTime(courseData[6]);
		setProfessor(courseData[7]);
		this.courseData = courseData.clone();
	}
	
	public Course(String csvData) {
		String[] courseDataArray = csvData.split(",");
		setCourseSubject(courseDataArray[0]);
		setCourseNumber(courseDataArray[1]);
		setBuilding(courseDataArray[2]);
		setRoomNumber(courseDataArray[3]);
		setDays(courseDataArray[4]);	
		setStartTime(courseDataArray[5]);
		setEndTime(courseDataArray[6]);
		setProfessor(courseDataArray[7]);
		courseData = courseDataArray.clone();
	}
	
	public String getBuildingName() {
		String[] arr = building.split(" - ");
		return arr[1];
	}
	
	public String getBuildingNumber() {
		String[] arr = building.split(" - ");
		return arr[0];
	}
	
	public String getCourseTitle() {
		return courseSubject + " - " + courseNumber;
	}
	
	public String getTimeInterval() {
		return startTime + " - " + endTime;
	}
	
	public String getLocation() {
		return getBuildingNumber() + " - " + roomNumber;
	}

	public String getCourseSubject() {
		return courseSubject;
	}

	public void setCourseSubject(String courseSubject) {
		this.courseSubject = courseSubject;
	}

	public String getCourseNumber() {
		return courseNumber;
	}

	public void setCourseNumber(String courseNumber) {
		this.courseNumber = courseNumber;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String[] toArray() {
		return courseData;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 8; i++) {			
			if (i == 7) {
				sb.append(courseData[i]);
			} else {
				sb.append(courseData[i] + ",");
			}			
		}
		return sb.toString();
	}

	public String getProfessor() {
		return professor;
	}

	public void setProfessor(String professor) {
		this.professor = professor;
	}
}
