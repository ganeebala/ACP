package com.uoa.acpanalysis.model;

import java.util.Calendar;
import java.util.Date;

public class DailyUsagePlot {
	
	private String title;
	
	private String xAxisText;
	
	private String yAxisText;
	
	private Date assg1Time;
	
	private Date assg2Time;
	
	private Date examTime;
	
	private int timeBand;
	
	private String chartType;

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
		//Adding 12 hours to the day
		assg2Time = assg2Time+64800000L;
		this.assg2Time = new Date(assg2Time);
	}

	public Date getExamTime() {
		return examTime;
	}

	public void setExamTime(Long examTime) {
		//Adding 12 hours to the day
		examTime = examTime+64800000L;
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
		//Adding 12 hours to the day
		assg1Time = assg1Time+64800000L;
		this.assg1Time = new Date(assg1Time);
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

}
