package com.petar.pal;

import java.util.Calendar;

public class AverageTableRow {
	
	private String averageWeight;
	private Calendar startDate, endDate;
	
	public AverageTableRow() {
		this.startDate = null;
		this.averageWeight = "";
		this.endDate = null;
	}
	
	public AverageTableRow(Calendar startDate, String averageWeight, Calendar endDate) {
		this.startDate = startDate;
		this.averageWeight = averageWeight;
		this.endDate = endDate;
	}
	
	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}
	
	public Calendar getStartDate() {
		return startDate;
	}
	
	public void setAverageWeight(String averageWeight) {
		this.averageWeight = averageWeight;
	}
	
	public String getAverageWeight() {
		return averageWeight;
	}
	
	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}
	
	public Calendar getEndDate() {
		return endDate;
	}
	

}
