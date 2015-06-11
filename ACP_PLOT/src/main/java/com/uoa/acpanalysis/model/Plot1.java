package com.uoa.acpanalysis.model;

import java.util.Calendar;
import java.util.Date;

public class Plot1 {
	
	private String title;
	
	private String xAxisText;
	
	private String yAxisText;
	
	private Date assg1Time;
	
	private Date assg2Time;
	
	private Date examTime;
	
	private int timeBand;

	public int getTimeBand() {
		return timeBand;
	}

	public void setTimeBand(int timeBand) {
		this.timeBand = timeBand;
	}

	public Date getAssg2Time() {
		return assg2Time;
	}

	public void setAssg2Time(Long assg2Time) {
		this.assg2Time = new Date(assg2Time);
	}

	public Date getExamTime() {
		return examTime;
	}

	public void setExamTime(Long examTime) {
		this.examTime = new Date(examTime);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getxAxisText() {
		return xAxisText;
	}

	public void setxAxisText(String xAxisText) {
		this.xAxisText = xAxisText;
	}

	public String getyAxisText() {
		return yAxisText;
	}

	public void setyAxisText(String yAxisText) {
		this.yAxisText = yAxisText;
	}

	public Date getAssg1Time() {
		return assg1Time;
	}

	public void setAssg1Time(Long assg1Time) {
		this.assg1Time = new Date(assg1Time);
	}

}
