package com.nexus.whc.models;

import lombok.Data;

@Data
public class SelectDialogForm {

	private String dialogSearchClient;
	private String dialogClientId;
	private String dialogClientName;

	private String dialogSearchEmployee;
	private String dialogEmployeeId;
	private String dialogEmployeeName;

	public SelectDialogForm() {
		dialogSearchClient = "";
		dialogClientId = "";
		dialogClientName = "";
		dialogSearchEmployee = "";
		dialogEmployeeId = "";
		dialogEmployeeName = "";
	}
}
