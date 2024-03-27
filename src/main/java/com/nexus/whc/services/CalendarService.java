package com.nexus.whc.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexus.whc.models.CalendarData;
import com.nexus.whc.models.CalendarDetail;
import com.nexus.whc.models.CalendarDetailManager;
import com.nexus.whc.models.CalendarSearchForm;
import com.nexus.whc.repository.CalendarRepository;

/*
 * CalendarService.java
 * 
 * CalendarServiceクラス
 */

/*
 * Serviceクラス
 */
@Service
public class CalendarService {

	/* CalendarRepositoryクラス */
	private final CalendarRepository calendarRepository;

	/* CalendarServiceクラス */
	@Autowired
	public CalendarService(CalendarRepository calendarRepository) {
		this.calendarRepository = calendarRepository;
	}

	/**
	 * カレンダー一覧
	 * カレンダー情報を全件取得してListで返す
	 * @param なし
	 * @return list 全件取得結果のList
	 */
	public List<Map<String, Object>> searchCalendarAll(int pageNumber, int pageSize) {
		//オフセット数を定義
		int offset = pageNumber * pageSize;

		List<Map<String, Object>> list = calendarRepository.searchCalendarAll(offset, pageSize);
		return list;

	}

	/**
	 * カレンダー一覧情報の件数を取得
	 * ページネーションで使用
	 * @return 
	 */
	public int countCalendar() {
		int result = calendarRepository.countCalendar();
		return result;
	}

	/**
	 * カレンダー検索情報の件数を取得
	 * ページネーションで使用
	 * @return 
	 */
	public int countSearchCalendar(CalendarSearchForm searchForm) {

		int result = calendarRepository.countSearchCalendar(searchForm);
		return result;
	}

	/**
	 * カレンダー一覧検索
	 * 指定された値を含むListを抽出する
	 * @param e_id 社員番号
	 * @param c_name 顧客名
	 * @param year_month 年月選択
	 * @param from 年月(～から)
	 * @param to 年月(～まで)
	 * @return
	 */
	public List<Map<String, Object>> searchCalendar(int pageNumber, int pageSize, CalendarSearchForm searchForm) {

		//オフセット数を定義
		int offset = (pageNumber - 1) * pageSize;

		List<Map<String, Object>> list = calendarRepository.searchCalendar(offset, pageSize, searchForm);
		return list;
	}

	/**
	 * カレンダー削除
	 * 指定されたカレンダー情報を論理削除する
	 * @param seq_id シーケンスID
	 * @return 削除件数
	 */
	public int deleteCalendar(String seq_id, String updated_user) {

		int result = calendarRepository.deleteCalendar(seq_id, updated_user);
		return result;
	}

	/**
	 * カレンダー詳細削除
	 * 指定されたカレンダー詳細情報を論理削除する
	 * @param seq_id シーケンスID
	 * @return 削除件数
	 */
	public int deleteCalendarDetail(String seq_id, String updated_user) {

		int result = calendarRepository.deleteCalendarDetail(seq_id, updated_user);
		return result;
	}

	/**
	 * 指定した顧客の、登録されている月と休日数を取得
	 * @param client_id
	 * @return
	 */
	public List<Map<String, Object>> searchHolidayCount(Map<String, Object> map, String startOfYear,
			String endOfYear) {

		Integer clientId = Integer.parseInt(map.get("client_id").toString());
		Integer employeeId = null;

		if (map.get("employee_id") != null) {
			employeeId = Integer.parseInt(map.get("employee_id").toString());
		}

		List<Map<String, Object>> list = calendarRepository.searchHolidayCount(clientId, employeeId, startOfYear,
				endOfYear);

		//月が重複していたら排除
		Set<String> uniqueYearMonths = new HashSet<>();
		List<Map<String, Object>> resultList = new ArrayList<>();

		for (Map<String, Object> entry : list) {
			String yearMonth = entry.get("year_month").toString();

			// 重複していない年月のみをリストに追加
			if (uniqueYearMonths.add(yearMonth)) {
				resultList.add(entry);
			}
		}

		return resultList;
	}

	/**
	 * 顧客社員が一致する、年度内のデータの通年備考を取得する
	 * @param clientId
	 * @param employeeId
	 * @param startOfYear
	 * @param endOfYear
	 */
	public String searchAllYearRoundComment(CalendarData calendarData, String startOfYear,
			String endOfYear) {

		String comment = "";

		List<Map<String, Object>> list = calendarRepository.searchAllYearRoundComment(calendarData,
				startOfYear, endOfYear);

		//取得できた場合
		if (!list.isEmpty()) {
			//リスト先頭のコメント(通年備考)を取得
			comment = list.get(0).get("comment").toString();
		}

		return comment;
	}

	/**
	 * 通年備考を更新する
	 * @param calendarData
	 * @param yearMonth
	 * @param comment
	 * @param userId
	 */
	public void updateAllYearRoundComment(CalendarData calendarData, LocalDate yearMonth, String userId) {

		calendarRepository.updateAllYearRoundComment(calendarData, yearMonth, userId);
	}

	/**
	 * カレンダー新規登録
	 * 顧客名からidを検索する
	 * @param clientName
	 * @return
	 */
	public int searchClientId(String clientName) {

		int id = calendarRepository.searchClientId(clientName);

		return id;
	}

	/**
	 * シーケンスidの最大を取得するSQLを実行する
	 * @return
	 */
	public int searchMaxSeqId() {
		int id = calendarRepository.searchMaxSeqId();
		return id;
	}

	/**
	 * カレンダー新規登録
	 * カレンダー情報をINSERTする
	 * @param client_id
	 * @param employee_id
	 * @param year_month
	 * @param monthly_holidays
	 * @param monthly_prescribed_days
	 * @param comment
	 * @param created_user
	 * @return　
	 */
	public int createCalendar(int clientId, String employeeId, LocalDate yearMonth, int monthlyHolidays,
			int monthlyPrescribedDays, String comment, String createdUser) {

		Integer empId;
		if (employeeId.equals("")) {
			empId = null;
		} else {
			empId = Integer.parseInt(employeeId);
		}

		int result = calendarRepository.createCalendar(clientId, empId, yearMonth,
				monthlyHolidays, monthlyPrescribedDays, comment, createdUser);
		return result;
	}

	/**
	 * カレンダー新規登録
	 * カレンダー詳細情報をINSERTするSQLを実行する
	 * @param parent_seq_id
	 * @param date
	 * @param created_user
	 * @return
	 */
	public int createCalendarDetail(int parentSeqId, String[] day, boolean[] holidayFlg, String[] comment,
			String createdUser) {

		int result = calendarRepository.createCalendarDetail(parentSeqId, day, holidayFlg, comment, createdUser);

		return result;
	}

	/**
	 * カレンダー閲覧
	 * 指定されたカレンダー情報を抽出する
	 * @param seq_id
	 * @return 抽出結果のlist
	 */
	public CalendarData searchCalendarData(String seq_id) {

		Map<String, Object> result = calendarRepository.searchCalendarData(seq_id);

		CalendarData calendarData = new CalendarData();

		calendarData.setClientName(result.get("client_name").toString());
		calendarData.setYearMonth(LocalDate.parse(result.get("year_month").toString()));

		if (result.get("comment") == null) {
			calendarData.setAllYearRoundComment("");
		} else {
			calendarData.setAllYearRoundComment(result.get("comment").toString());
		}

		if (result.get("employee_id") != null) {

			//社員番号を4桁に変換
			String formattedEmployeeId = String.format("%04d", Integer.parseInt(result.get("employee_id").toString()));

			calendarData.setEmployeeId(formattedEmployeeId);
			calendarData.setEmployeeName(result.get("employee_name").toString());
		}

		return calendarData;

	}

	/**
	 * カレンダー閲覧
	 * 指定されたカレンダー詳細情報を抽出する
	 * @param seq_id シーケンスID
	 * @return 抽出結果のlist
	 */
	public CalendarDetailManager searchCalendarDetails(String seq_id) {

		List<Map<String, Object>> list = calendarRepository.searchCalendarDetails(seq_id);

		CalendarDetailManager detail = new CalendarDetailManager(list.size());

		for (Map<String, Object> map : list) {

			CalendarDetail calendarDetail = new CalendarDetail();

			//日付を表示する形に変換
			LocalDate date = LocalDate.parse(map.get("date").toString());
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d");
			String formattedDay = date.format(formatter);

			//曜日を取得
			int dayOfWeek = date.getDayOfWeek().getValue();

			//フォームに追加
			calendarDetail.setDate(formattedDay);
			calendarDetail.setHolidayFlg((boolean) map.get("holiday_Flg"));
			calendarDetail.setComment(map.get("comment").toString());
			calendarDetail.setDayOfWeek(dayOfWeek);

			detail.addDetails(calendarDetail, Integer.parseInt(formattedDay) - 1);
		}

		return detail;
	}

	/**
	 * カレンダー編集
	 * カレンダー情報を更新する
	 * @param なし
	 * @return 更新件数
	 */
	public int updateCalendar(String seq_id, String employeeId, int monthly_holidays, int monthly_prescribed_days,
			String comment, String updated_user) {

		Integer empId;
		if (employeeId.equals("")) {
			empId = null;
		} else {
			empId = Integer.parseInt(employeeId);
		}

		int result = calendarRepository.updateCalendar(seq_id, empId, monthly_holidays, monthly_prescribed_days,
				comment,
				updated_user);
		return result;
	}

	/**
	 * カレンダー編集
	 * カレンダー詳細情報を更新する
	 * @param なし
	 * @return 更新件数
	 */
	public int updateCalendarDetails(boolean holiday_flg[], String comment[], String updated_user, String seq_id,
			String date[]) {

		int result = calendarRepository.updateCalendarDetails(holiday_flg, comment, updated_user, seq_id, date);
		return result;
	}

	//重複チェック
	public Integer duplicateCheck(String clientName, String empId, LocalDate year_month) {

		Integer result = calendarRepository.duplicateCheck(clientName, empId, year_month);
		return result;
	}

	//シーケンスidを取得する
	public Integer getSeqId(CalendarData calendarData, LocalDate year_month) {

		Integer result = calendarRepository.getSeqId(calendarData, year_month);
		return result;
	}

	//前月を取得する
	public Integer getLastMonth(CalendarData calendarData) {

		Integer id = calendarRepository.getLastMonth(calendarData);

		return id;
	}

	//翌月を取得する
	public Integer getFollowingMonth(CalendarData calendarData) {

		Integer id = calendarRepository.getFollowingMonth(calendarData);

		return id;
	}

	/**
	 * 顧客選択ダイアログ検索
	 */
	public List<Map<String, Object>> getClient(String id, String name) {

		List<Map<String, Object>> list = calendarRepository.getClient(id, name);

		for (Map<String, Object> map : list) {
			//顧客番号を3桁に変換
			String formattedClientId = String.format("%03d", (Integer) map.get("client_id"));
			map.put("client_id", formattedClientId);
		}

		return list;
	}

	/**
	 * 社員選択ダイアログ検索
	 */
	public List<Map<String, Object>> getEmployee(String id, String name) {

		List<Map<String, Object>> list = calendarRepository.getEmployee(id, name);

		for (Map<String, Object> map : list) {

			//社員番号を4桁に変換
			String formattedEmployeeId = String.format("%04d", (Integer) map.get("employee_id"));
			map.put("employee_id", formattedEmployeeId);
		}

		return list;
	}

}