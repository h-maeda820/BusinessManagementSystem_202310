package com.nexus.whc.models;

import lombok.Data;

@Data
public class EmployeeSearchForm {

	//社員ID
	private Integer employeeId;
	//社員名
	private String employeeName;
	//顧客ID
	private Integer clientId;
	//顧客名
	private String clientName;

	//有給残日数なし
	private boolean noPaidHoliday;
	//有給取得日数不足(通知)
	private boolean notificationPaidHoliday;
	//有給取得日数不足(注意)
	private boolean attentionPaidHoliday;
	//有給取得日数不足(警告)
	private boolean warningPaidHoliday;

	public EmployeeSearchForm() {
		employeeId = null;
		employeeName = "";
		clientId = null;
		clientName = "";
		noPaidHoliday = false;
		notificationPaidHoliday = false;
		attentionPaidHoliday = false;
		warningPaidHoliday = false;
	}

	//初期化された状態の場合trueを返す
	public boolean isInitialized() {
		return employeeId == null &&
				employeeName.isEmpty() &&
				clientId == null &&
				clientName.isEmpty() &&
				!noPaidHoliday &&
				!notificationPaidHoliday &&
				!attentionPaidHoliday &&
				!warningPaidHoliday;
	}

}
