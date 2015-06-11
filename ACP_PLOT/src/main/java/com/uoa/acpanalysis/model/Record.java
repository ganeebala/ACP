package com.uoa.acpanalysis.model;

import java.util.Date;

public class Record {
	
	//Change to private
	public String univ;
	public String user;
	public int course;
	public boolean isStudent;
	public ActionType type;
	public String projectName;
	public String projectGroupName;
	public int projectNumber;
	public int version;
	public Date date;
	public Record(String univ,String user, int course, boolean isStudent, ActionType type, String projectName, int projectNumber, int version, Date date) {
		this.univ = univ;
		this.user = user;
		this.course = course;
		this.isStudent = isStudent;
		this.type = type;
		this.projectName = projectName;
		this.projectNumber = projectNumber;
		this.version = version;
		this.date = date;
	}
}
