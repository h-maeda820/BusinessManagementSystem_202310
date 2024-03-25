package com.nexus.whc.models;

import java.sql.Timestamp;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Entity;

@Entity
public class ClientData {
	
	//	日時の取得
	Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
	
	@Id
	@NotBlank(message = "顧客IDは必ず入力してください.")
	private String client_id;
	
	@NotBlank(message = "顧客名は必ず入力してください.")
	private String client_name;
	
	@NotBlank(message = "始業時刻は必ず入力してください.")
	@Pattern(regexp = "^([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]|^([01][0-9]|2[0-3]):[0-5][0-9]$", message = "始業時刻(13:00など)で入力してください。")
	private String open_time;
	
	@NotBlank(message = "終業時刻は必ず入力してください.")
	@Pattern(regexp = "^([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]|^([01][0-9]|2[0-3]):[0-5][0-9]$", message = "終業時刻(13:00など)で入力してください。")
	private String close_time;
	
	@NotBlank(message = "作業時間は必ず入力してください.")
	@Pattern(regexp = "^^([01]?[0-9]|2[0-3]).([0-5][0-9])$", message = "作業時間(8.00など)で入力してください。")
	private String working_time;
	
	@NotBlank(message = "休憩1開始は必ず入力してください.")
	@Pattern(regexp = "^([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]|^([01][0-9]|2[0-3]):[0-5][0-9]$", message = "休憩1開始(13:00など)で入力してください。")
	private String rest1_start;
	
	@NotBlank(message = "休憩1終了は必ず入力してください.")
	@Pattern(regexp = "^([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]|^([01][0-9]|2[0-3]):[0-5][0-9]$", message = "休憩1終了(13:00など)で入力してください。")
	private String rest1_end;
	
//	@Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", message = "休憩2開始(13:00など)で入力してください。")
	private String rest2_start;
	
//	@Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", message = "休憩2終了(13:00など)で入力してください。")
	private String rest2_end ;
	
//	@Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", message = "休憩3開始(13:00など)で入力してください。")
	private String rest3_start ;
	
//	@Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", message = "休憩3終了(13:00など)で入力してください。")
	private String rest3_end ;
	
//	@Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", message = "休憩4開始(13:00など)で入力してください。")
	private String rest4_start ;
	
//	@Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", message = "休憩4終了(13:00など)で入力してください。")
	private String rest4_end ;
	
//	@Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", message = "休憩5開始(13:00など)で入力してください。")
	private String rest5_start ;
	
//	@Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", message = "休憩5終了(13:00など)で入力してください。")
	private String rest5_end ;
	
//	@Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", message = "休憩6開始(13:00など)で入力してください。")
	private String rest6_start ;
	
//	@Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", message = "休憩6終了(13:00など)で入力してください。")
	private String rest6_end ;
	
//	@Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", message = "調整休憩時間開始(13:00など)で入力してください。")
	private String adjust_rest_time_start ;
	
//	@Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", message = "調整休憩時間終了(13:00など)で入力してください。")
	private String adjust_rest_time_end  ;
	
	private String comment;
	
	private int delete_flg = 0;
	
	private Timestamp created_at = date;
	
	private final String created_user = "system";
	
	private Timestamp updated_at = date;
	
	private  final String updated_user = "sysdate";
	

	
	public String getClient_id() {
		return client_id;
	}
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	public String getClient_name() {
		return client_name;
	}
	public void setClient_name(String client_name) {
		this.client_name = client_name;
	}
	public String getOpen_time() {
		return open_time;
	}
	public void setOpen_time(String open_time) {
		this.open_time = open_time;
	}
	public String getClose_time() {
		return close_time;
	}
	public void setClose_time(String close_time) {
		this.close_time = close_time;
	}
	public String getWorking_time() {
		return working_time;
	}
	public void setWorking_time(String working_time) {
		this.working_time = working_time;
	}
	public String getRest1_start() {
		return rest1_start;
	}
	public void setRest1_start(String rest1_start) {
		this.rest1_start = rest1_start;
	}
	public String getRest1_end() {
		return rest1_end;
	}
	public void setRest1_end(String rest1_end) {
		this.rest1_end = rest1_end;
	}
	public String getRest2_start() {
		return rest2_start;
	}
	public void setRest2_start(String rest2_start) {
		this.rest2_start = rest2_start;
	}
	public String getRest2_end() {
		return rest2_end;
	}
	public void setRest2_end(String rest2_end) {
		this.rest2_end = rest2_end;
	}
	public String getRest3_start() {
		return rest3_start;
	}
	public void setRest3_start(String rest3_start) {
		this.rest3_start = rest3_start;
	}
	public String getRest3_end() {
		return rest3_end;
	}
	public void setRest3_end(String rest3_end) {
		this.rest3_end = rest3_end;
	}
	public String getRest4_start() {
		return rest4_start;
	}
	public void setRest4_start(String rest4_start) {
		this.rest4_start = rest4_start;
	}
	public String getRest4_end() {
		return rest4_end;
	}
	public void setRest4_end(String rest4_end) {
		this.rest4_end = rest4_end;
	}
	public String getRest5_start() {
		return rest5_start;
	}
	public void setRest5_start(String rest5_start) {
		this.rest5_start = rest5_start;
	}
	public String getRest5_end() {
		return rest5_end;
	}
	public void setRest5_end(String rest5_end) {
		this.rest5_end = rest5_end;
	}
	public String getRest6_start() {
		return rest6_start;
	}
	public void setRest6_start(String rest6_start) {
		this.rest6_start = rest6_start;
	}
	public String getRest6_end() {
		return rest6_end;
	}
	public void setRest6_end(String rest6_end) {
		this.rest6_end = rest6_end;
	}
	public String getAdjust_rest_time_start() {
		return adjust_rest_time_start;
	}
	public void setAdjust_rest_time_start(String adjust_rest_time_start) {
		this.adjust_rest_time_start = adjust_rest_time_start;
	}
	public String getAdjust_rest_time_end() {
		return adjust_rest_time_end;
	}
	public void setAdjust_rest_time_end(String adjust_rest_time_end) {
		this.adjust_rest_time_end = adjust_rest_time_end;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public int getDelete_flg() {
		return delete_flg;
	}
	public void setDelete_flg(int delete_flg) {
		this.delete_flg = delete_flg;
	}
	public Timestamp getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Timestamp created_at) {
		this.created_at = created_at;
	}
	public String getCreated_user() {
		return created_user;
	}
	public Timestamp getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(Timestamp updated_at) {
		this.updated_at = updated_at;
	}
	public String getUpdated_user() {
		return updated_user;
	}
}
