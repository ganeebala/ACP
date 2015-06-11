package com.uoa.acpanalysis.model.formdata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupingCategory {
	
	private String categoryName;
	
	private float threshold;
	
	private float percentageValue;
	
	private List startTimes = new ArrayList(); 
	
	private List endTimes = new ArrayList();
	
	private List<Date> startTimesDate = new ArrayList<Date>(); 
	
	private List<Date> endTimesDate = new ArrayList<Date>();

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public float getPercentageValue() {
		return percentageValue;
	}

	public void setPercentageValue(float percentageValue) {
		this.percentageValue = percentageValue;
	}

	public List getStartTimes() {
		return startTimes;
	}

	public void setStartTimes(List startTimes) {
		this.startTimes = startTimes;
	}

	public List getEndTimes() {
		return endTimes;
	}

	public void setEndTimes(List endTimes) {
		this.endTimes = endTimes;
	}

	public List<Date> getStartTimesDate() {
		return startTimesDate;
	}

	public void setStartTimesDate(List<Date> startTimesDate) {
		this.startTimesDate = startTimesDate;
	}

	public List<Date> getEndTimesDate() {
		return endTimesDate;
	}

	public void setEndTimesDate(List<Date> endTimesDate) {
		this.endTimesDate = endTimesDate;
	}
	
	

}
