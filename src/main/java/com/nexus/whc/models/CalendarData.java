package com.nexus.whc.models;

import java.time.LocalDate;

public class CalendarData {

	private String clientName;

	private String employeeId;
	private String employeeName;

	private String year;
	private String month;

	private LocalDate yearMonth;

	private String allYearRoundComment;

	//暦通り登録用from-to
	private String startYearMonth;
	private String endYearMonth;

	public CalendarData() {
		clientName = "";
		employeeId = null;
		employeeName = "";
		year = "";
		month = "";
		employeeName = "";
		yearMonth = null;
		allYearRoundComment = "";

		startYearMonth = "";
		endYearMonth = "";

	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	//	public LocalDate getYearMonth() {
	//		return yearMonth;
	//	}
	//
	//	public void setYearMonth(LocalDate yearMonth) {
	//		this.yearMonth = yearMonth;
	//	}

	public String getAllYearRoundComment() {
		return allYearRoundComment;

	}

	public void setAllYearRoundComment(String allYearRoundComment) {
		this.allYearRoundComment = allYearRoundComment;
	}

	public String getStartYearMonth() {
		return startYearMonth;
	}

	public void setStartYearMonth(String startYearMonth) {
		this.startYearMonth = startYearMonth;
	}

	public String getEndYearMonth() {
		return endYearMonth;
	}

	public void setEndYearMonth(String endYearMonth) {
		this.endYearMonth = endYearMonth;
	}

	public void setYearAndMonth(String year, String month) {
		this.year = year;
		this.month = month;
		this.yearMonth = LocalDate.parse(this.year + "-" + this.month + "-01");
	}

	public LocalDate getYearMonth() {
		if (yearMonth != null) {
			return yearMonth;
		} else {
			this.yearMonth = LocalDate.parse(this.year + "-" + this.month + "-01");
			return yearMonth;
		}
	}

	public void setYearMonth(LocalDate yearMonth) {
		this.yearMonth = yearMonth;
		year = String.valueOf(yearMonth.getYear());
		month = String.format("%02d", yearMonth.getMonthValue());
	}

}
