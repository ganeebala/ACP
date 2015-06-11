package com.uoa.acpanalysis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
	
	private String userId;	
	
	private int totalUsage;
	
	public Map<String, Integer> categoryWiseUsage = new HashMap<String, Integer>();	

	private List<Marks> marks = new ArrayList<Marks>();
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getTotalUsage() {
		return totalUsage;
	}

	public void setTotalUsage(int totalUsage) {
		this.totalUsage = totalUsage;
	}

	public List<Marks> getMarks() {
		return marks;
	}

	public void setMarks(List<Marks> marks) {
		this.marks = marks;
	}

}
