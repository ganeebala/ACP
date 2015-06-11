package com.uoa.acpanalysis.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.ScopedProxyMode;

@Component
@Scope(value = "session",  proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6224827856340712333L;

	private List<Record> records = new ArrayList<Record>();
	
	/*
	 * Lecture Start times for the course
	 */
	private List<Date> lectureStartTime = new ArrayList<Date>();
	
	/*
	 * Lecture End times for the course
	 */
	private List<Date> lectureEndTime = new ArrayList<Date>();
	
	private int temp =8 ;
	
	private Map<String, User> users = new HashMap<String, User>();

	public List<Record> getRecords() {
		return records;
	}

	public void setRecords(List<Record> records) {
		this.records = records;
	}

	public int getTemp() {
		return temp;
	}

	public void setTemp(int temp) {
		this.temp = temp;
	}	
	
	public List<Date> getLectureStartTime() {
		return lectureStartTime;
	}

	public void setLectureStartTime(List<Date> lectureStartTime) {
		this.lectureStartTime = lectureStartTime;
	}

	public List<Date> getLectureEndTime() {
		return lectureEndTime;
	}

	public void setLectureEndTime(List<Date> lectureEndTime) {
		this.lectureEndTime = lectureEndTime;
	}

	public Map<String, User> getUsers() {
		return users;
	}

	public void setUsers(Map<String, User> users) {
		this.users = users;
	}

}
