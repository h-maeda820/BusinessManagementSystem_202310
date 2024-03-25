package com.nexus.whc.models;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

/**
 * 社員マスタ(一覧)で使用する検索条件を管理するクラス
 * SeachClientForm.java
 *
 * @author 寺島 健太
 *
 */

@Component
public class EmployeeDate {

	@NotBlank(message = "社員番号は必ず入力してください。")
	private String employeeId; /*社員番号*/

	@NotBlank(message = "社員氏名は必ず入力してください。")
	private String employeeName; /*社員氏名*/

	@NotBlank(message = "担当顧客番号は必ず入力してください。")
	private String clientId;

	private String clientName;
	private boolean hourlyWage;

	@NotBlank(message = "有給基準日は必ず入力してください。")
	private String paidHolidayStd;

	@NotNull(message = "有給残日数(当年度分)は必ず入力してください。")
	@Min(value = 0, message = "有給残日数(当年度分)(0~20の範囲)で入力してください。")
	@Max(value = 20, message = "有給残日数(当年度分)(0~20の範囲)で入力してください。")
	private Integer remaindThisYear;

	@NotNull(message = "有給残日数(前年度分)は必ず入力してください。")
	@Min(value = 0, message = "有給残日数(前年度分)(0~20の範囲)で入力してください。")
	@Max(value = 20, message = "有給残日数(前年度分)(0~20の範囲)で入力してください。")
	private Integer remaindLastYear;

	private String status;
	private Integer holidayCount;

	private Integer seqId;
	private String createdAt;
	private String createdUser = "system";
	private String updatedAt;
	private String updatedUser = "sysdate";

	public EmployeeDate() {
		employeeId = "";
		employeeName = "";
		clientId = "";
		clientName = "";
		hourlyWage = false;
		paidHolidayStd = "";
		remaindThisYear = null;
		remaindLastYear = null;
		status = "";
		holidayCount = null;
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

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public boolean getHourlyWage() {
		return hourlyWage;
	}

	public void setHourlyWage(boolean hourlyWage) {
		this.hourlyWage = hourlyWage;
	}

	public String getPaidHolidayStd() {
		return paidHolidayStd;
	}

	public void setPaidHolidayStd(String paidHolidayStd) {
		this.paidHolidayStd = paidHolidayStd;
	}

	public Integer getRemaindThisYear() {
		return remaindThisYear;
	}

	public void setRemaindThisYear(Integer remaindThisYear) {
		this.remaindThisYear = remaindThisYear;
	}

	public Integer getRemaindLastYear() {
		return remaindLastYear;
	}

	public void setRemaindLastYear(Integer remaindLastYear) {
		this.remaindLastYear = remaindLastYear;
	}

	public String getStatus() {
		return status;
	}

	public void setStasus(String status) {
		this.status = status;
	}

	public Integer getHolidayCount() {
		return holidayCount;
	}

	public void setHolidayCount(Integer holidayCount) {
		this.holidayCount = holidayCount;
	}

	public Integer getSeqId() {
		return seqId;
	}

	public void setSeqId(Integer seqId) {
		this.seqId = seqId;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getUpdatedUser() {
		return updatedUser;
	}

	public void setUpdatedUser(String updatedUser) {
		this.updatedUser = updatedUser;
	}

}
