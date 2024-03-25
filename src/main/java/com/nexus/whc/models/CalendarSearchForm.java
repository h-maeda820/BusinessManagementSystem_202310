package com.nexus.whc.models;

import lombok.Data;

@Data
public class CalendarSearchForm {

	//社員ID
	private Integer employeeId;
	//社員名
	private String employeeName;
	//顧客名
	private String clientName;
	//年/年月検索オプション
	private Integer yearMonthOption;
	//検索開始年月
	private String startYearMonth;
	//検索終了年月
	private String endYearMonth;

	public CalendarSearchForm() {
		employeeId = null;
		employeeName = "";
		clientName = "";
		yearMonthOption = null;
		startYearMonth = "";
		endYearMonth = "";
	}

	//初期化された状態の場合trueを返す
	public boolean isInitialized() {
		return employeeId == null &&
				employeeName.equals("") &&
				clientName.equals("") &&
				yearMonthOption == null &&
				startYearMonth.equals("") &&
				endYearMonth.equals("");
	}
}
