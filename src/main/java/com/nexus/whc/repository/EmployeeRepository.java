package com.nexus.whc.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * EmployeeDao.java
 * 社員マスタで使用する社員情報の登録・検索・編集・削除に関するSQLを
 * 作成して実行するクラス
 *
 * @author 寺島 健太
 *
 */
@Repository
public class EmployeeRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	/**
	 * ページネーション
	 * 引数にページネーションで使用するオフセット数とページサイズを設定
	 * delete_flg=falseの社員情報を抽出するSQLを実行する。
	 * 
	 * @return list 抽出結果のlist
	 */
	public List<Map<String, Object>> searchActive() {

		/* SQL文作成 */
		String sql = "SELECT m_employee.employee_id, m_employee.employee_name, m_employee.client_id,"
				+ " m_client.client_name, m_employee.hourly_wage,"
				+ " m_employee.paid_holiday_std, m_employee_paid_vacation.remaind_this_year, m_employee_paid_vacation.remaind_last_year,"
				/*現在の年数の場合THENで1をCASEWHENYEARに返す、一致しない場合はNULLを返す*/
				//count部分現在の年の有給ではなく有給基準日から1年に変更しないとダメ 未完成
				+ " COUNT(CASE WHEN YEAR(t_work_leave_application.holiday_date)=YEAR(NOW()) THEN 1 END)AS holiday_count, t_work_leave_application.application_class"
				+ " FROM m_employee"
				+ " LEFT JOIN m_client ON m_employee.client_id = m_client.client_id"
				+ " LEFT JOIN m_employee_paid_vacation ON m_employee.employee_id = m_employee_paid_vacation.employee_id"
				+ " LEFT JOIN t_work_leave_application ON m_employee.employee_id = t_work_leave_application.employee_id"
				+ " WHERE m_employee.delete_flg = false"
				+ " GROUP BY m_employee.employee_id,m_employee.employee_name,m_employee.client_id,"
				+ " m_client.client_name,m_employee.hourly_wage,m_employee.paid_holiday_std,m_employee_paid_vacation.remaind_this_year,"
				+ " m_employee_paid_vacation.remaind_last_year,t_work_leave_application.application_class"
				+ " ORDER BY m_employee.employee_id ASC";

		/* クエリを実行 */
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		System.out.println(list);

		/* 取得したリストを返す */
		return list;
	}

	public List<Map<String, Object>> searchAll() {
		// SQL文作成
		String sql = "SELECT * from m_employee WHERE delete_flg != 1";

		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		return list;
	}
	
	/**
	 * 社員情報を全件を取得してresultで返すSQLを実行する
	 * ページネーションで使用
	 * 
	 * @return result 全件取得結果のresult
	 */
	public int countEmployee() {

		/* 実行件数を初期化 */
		int result = 0;

		/* SQL文作成 */
		String sql = "SELECT COUNT(*) FROM m_employee WHERE delete_flg != 1";

		/* クエリを実行 */
		result += jdbcTemplate.queryForObject(sql, Integer.class);

		/* 取得した件数を返す */
		return result;
	}

	/**
	 * 指定された社員情報を抽出するSQLを実行する。
	 * 
	 * @param employeeId 抽出対象の社員番号のString, employeeName 抽出対象の社員氏名のString, clientId
	 *                   抽出対象の担当顧客番号のString, clientName 抽出対象の担当顧客名のString
	 * @return emp 抽出結果のemp
	 */
	public List<Map<String, Object>> searchEmployee(Integer employeeId, String employeeName, Integer clientId,
			String clientName) {

		/* SQL文作成 */
		String sql = "SELECT m_employee.employee_id, m_employee.employee_name, m_employee.client_id,"
				+ " m_client.client_name, m_employee.hourly_wage,"
				+ " m_employee.paid_holiday_std, m_employee_paid_vacation.remaind_this_year, m_employee_paid_vacation.remaind_last_year,"
				/*現在の年数の場合THENで1をCASEWHENYEARに返す、一致しない場合はNULLを返す*/
				//count部分現在の年の有給ではなく有給基準日から1年に変更しないとダメ 未完成
				+ " COUNT(CASE WHEN YEAR(t_work_leave_application.holiday_date)=YEAR(NOW()) THEN 1 END)AS holiday_count, t_work_leave_application.application_class"
				+ " FROM m_employee "
				+ " LEFT JOIN m_client ON m_employee.client_id = m_client.client_id"
				+ " LEFT JOIN m_employee_paid_vacation ON m_employee.employee_id = m_employee_paid_vacation.employee_id"
				+ " LEFT JOIN t_work_leave_application ON m_employee.employee_id = t_work_leave_application.employee_id"
				+ " WHERE m_employee.delete_flg = false";

		/* SQLの条件部分を構築するためのパラメータリスト */
		List<String> params = new ArrayList<String>();

		/* 社員番号が指定されている場合、条件に追加 */
		if (employeeId != null) {
			sql += " AND m_employee.employee_id = ?";
			params.add(employeeId.toString());
		}

		/* 社員氏名が指定されている場合、条件に追加 */
		if (!employeeName.equals("")) {
			sql += " AND m_employee.employee_name LIKE ?";
			params.add("%" + employeeName + "%");
		}

		/* 担当顧客番号が指定されている場合、条件に追加 */
		if (clientId != null) {
			sql += " AND m_employee.client_id = ?";
			params.add(clientId.toString());
		}

		/* 担当顧客名が指定されている場合、条件に追加 */
		if (!clientName.equals("")) {
			sql += " AND m_client.client_name LIKE ?";
			params.add("%" + clientName + "%");
		}

		sql += " GROUP BY m_employee.employee_id,m_employee.employee_name,m_employee.client_id,"
				+ " m_client.client_name,m_employee.hourly_wage,m_employee.paid_holiday_std,m_employee_paid_vacation.remaind_this_year,"
				+ " m_employee_paid_vacation.remaind_last_year,t_work_leave_application.application_class";

		/* ORDER BY 句を追加 */
		sql += " ORDER BY m_employee.employee_id ASC";

		/* パラメータリストを配列に変換 */
		Object[] param = params.toArray(new String[0]);

		/* クエリを実行 */
		List<Map<String, Object>> emp = jdbcTemplate.queryForList(sql, param);

		/* 取得したデータを返す */
		return emp;
	}

	/**
	 * 新規登録重複チェック時に重複の社員番号がないか検索するSQLを実行する
	 * 
	 * @param employeeId 検索対象の社員IDのString
	 * @return result 更新件数
	 */
	public boolean isDuplicateEmpId(String employeeId) {

		/* SQL文作成 */
		String sql = "SELECT COUNT(*) FROM m_employee WHERE employee_id=?";

		/* ? の箇所を置換するデータの配列を定義 */
		Object[] param = { employeeId };

		/* クエリを実行 */
		int count = jdbcTemplate.queryForObject(sql, param, Integer.class);

		/* 結果が1以上なら重複あり、0なら重複なし */
		return count > 0;
	}

	/**
	 * 新規登録重複チェック時に重複の社員氏名がないか検索するSQLを実行する
	 * 
	 * @param employeeName 検索対象の社員氏名のString
	 * @return result 更新件数
	 */
	public boolean isDuplicateEmpName(String employeeName) {

		/* SQL文作成 */
		String sql = "SELECT COUNT(*) FROM m_employee WHERE employee_name=?";

		/* ? の箇所を置換するデータの配列を定義 */
		Object[] param = { employeeName };

		/* クエリを実行 */
		int count = jdbcTemplate.queryForObject(sql, param, Integer.class);

		/* 結果が1以上なら重複あり、0なら重複なし */
		return count > 0;
	}

	/**
	 * 指定された社員情報を新規登録するSQLを実行する。
	 * 
	 * @param employeeId  登録対象の社員番号のString, employeeName 登録対象の社員氏名のString, clientId
	 *                    登録対象の担当顧客番号のString,
	 * @param employeeId  登録対象の時給(boolean), paidHolidayStd 登録対象の有給基準日のString,
	 * @param createdUser 登録対象のレコード作成ユーザIDのString, updatedUser 登録対象のレコード最終更新ユーザID
	 * @return result 登録件数
	 */
	public int registEmployee(String employeeId, String employeeName, String clientId, boolean hourlyWage,
			String paidHolidayStd, String createdUser, String updatedUser) {

		/* 登録した件数を初期化 */
		int result = 0;

		/* SQL文の作成 */
		String sql = "INSERT INTO m_employee (employee_id, employee_name, client_id, hourly_wage, paid_holiday_std, delete_flg,"
				+ " created_at, created_user, updated_at, updated_user)VALUES(?,?,?,?,?,false,NOW(),?,NOW(),?)";

		/* ? の箇所を置換するデータの配列を定義 */
		Object[] param = { employeeId, employeeName, clientId, hourlyWage, paidHolidayStd, createdUser, updatedUser };

		/* クエリを実行 */
		result = jdbcTemplate.update(sql, param);

		/* 実行件数を返す */
		return result;
	}

	/**
	 * 指定された有給休暇情報を新規登録するSQLを実行する。
	 * 
	 * @param seqId           , emoloyeeId 登録対象の社員番号のString, PaidHolidayStd
	 *                        登録対象の有給基準日のString
	 * @param ramaindThisYear 登録対象の有給残日数(当年度分)のString, remaindLastYear
	 *                        登録対象の有給残日数(前年度文)のString
	 * @param createdUser     登録対象のレコード作成ユーザIDのString, updatedUser
	 *                        登録対象のレコード最終更新ユーザID
	 * @return result 登録件数
	 */
	public int registPaidVacation(Integer seqId, String employeeId, String paidHolidayStd, Integer remaindThisYear,
			Integer remaindLastYear, String createdUser, String updatedUser) {

		/* 登録した件数を初期化 */
		int result = 0;

		/* SQL文の作成 */
		String sql = "INSERT INTO m_employee_paid_vacation (seq_id, employee_id, year, remaind_this_year, remaind_last_year, delete_flg,"
				+ " created_at, created_user, updated_at, updated_user)VALUES(?,?,?,?,?,false,NOW(),?,NOW(),?)";

		/* ? の箇所を置換するデータの配列を定義 */
		Object[] param = { seqId, employeeId, paidHolidayStd, remaindThisYear, remaindLastYear, createdUser,
				updatedUser };

		/* クエリを実行 */
		result = jdbcTemplate.update(sql, param);

		/* 実行件数を返す */
		return result;
	}

	/**
	 * 指定された社員情報を論理削除するSQLを実行する
	 * 
	 * @param employeeId 削除対象の社員IDのString配列, updatedUser 削除対象のレコード最終更新ユーザID
	 * @return result 削除件数
	 */
	public int deleteEmployee(String[] employeeId, String updatedUser) {

		/* 削除した件数を初期化 */
		int result = 0;

		for (String empId : employeeId) {

			/* SQL文の作成 */
			String sql = "UPDATE m_employee SET delete_flg=1, updated_at=CURRENT_TIMESTAMP, updated_user=? WHERE employee_id=? AND delete_flg=0";

			Object[] param = { updatedUser, empId };

			/* クエリを実行 */
			result += jdbcTemplate.update(sql, param);
		}

		/* 実行件数を返す */
		return result;
	}

	/**
	 * 指定された有給休暇情報を論理削除するSQLを実行する
	 * 
	 * @param employeeId 削除対象の社員IDのString配列, updatedUser 削除対象のレコード最終更新ユーザID,
	 *                   paidHoliday 削除対象の有給基準日
	 * @return result 削除件数
	 */
	public int deletePaidVacation(String[] employeeId, String updatedUser) {

		/* 削除した件数を初期化 */
		int result = 0;

		for (String empId : employeeId) {

			/* SQL文の作成 */
			String sql = "UPDATE m_employee_paid_vacation SET delete_flg=1, updated_at=NOW(), updated_user=?"
					+ " WHERE employee_id=? AND delete_flg=false";

			/* ? の箇所を置換するデータの配列を定義 */
			Object[] param = { updatedUser, empId };

			/* クエリを実行 */
			result += jdbcTemplate.update(sql, param);
		}

		/* 実行件数を返す */
		return result;
	}

	/**
	 * クリックされた社員情報を抽出するSQLを実行する
	 * 
	 * @param employeeId 抽出対象の社員IDのString
	 * @return empList 抽出結果のempList
	 */
	public Map<String, Object> searchEmployeeName(String employeeId) {

		Map<String, Object> empMap = null;
		try {
			/* SQL文作成 */
			String sql = "SELECT m_employee.employee_id, m_employee.employee_name, m_client.client_id, m_client.client_name,"
					+ " m_employee.hourly_wage, m_employee.paid_holiday_std, m_employee_paid_vacation.remaind_this_year, m_employee_paid_vacation.remaind_last_year,"
					/*現在の年数の場合THENで1をCASEWHENYEARに返す、一致しない場合はNULLを返す*/
					//count部分現在の年の有給ではなく有給基準日から1年に変更しないとダメ 未完成
					+ " COUNT(CASE WHEN YEAR(t_work_leave_application.holiday_date)=YEAR(NOW()) THEN 1 END)AS holiday_count, t_work_leave_application.application_class"
					+ " FROM m_employee"
					+ " LEFT JOIN m_client ON m_employee.client_id = m_client.client_id"
					+ " LEFT JOIN m_employee_paid_vacation ON m_employee.employee_id = m_employee_paid_vacation.employee_id"
					+ " LEFT JOIN t_work_leave_application ON m_employee.employee_id = t_work_leave_application.employee_id"
					+ " WHERE m_employee.employee_id=? AND m_employee.delete_flg=false"
					+ " GROUP BY m_employee.employee_id, m_employee.employee_name, m_client.client_id, m_client.client_name,"
					+ " m_employee.hourly_wage, m_employee.paid_holiday_std, m_employee_paid_vacation.remaind_this_year, m_employee_paid_vacation.remaind_last_year, t_work_leave_application.application_class";

			/* ? の箇所を置換するデータの配列を定義 */
			Object[] Param = { employeeId };

			/* クエリを実行 */
			// Map<String, Object> empList = jdbcTemplate.queryForMap(sql, Param);
			List<Map<String, Object>> empList = jdbcTemplate.queryForList(sql, Param);

			// リストが空でない場合、最初の結果を取得
			if (!empList.isEmpty()) {
				empMap = empList.get(0);
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("削除しようとしているレコードがありません");
		}

		/* 取得したリストを返す */
		return empMap;
	}

	/**
	 * 社員情報を更新するSQLを実行する
	 * 
	 * @param
	 * @return result 更新件数
	 */
	public int updateEmployee(String employeeName, String clientId, boolean hourlyWage, String paidHolidayStd,
			String updatedUser, String employeeId) {

		/* 更新した件数を初期化 */
		int result = 0;

		/* SQL文作成 */
		String sql = "UPDATE m_employee SET employee_name=?, client_id=?, hourly_wage=?, paid_holiday_std=?, updated_at=NOW(), updated_user=?"
				+ " WHERE employee_id=? AND delete_flg=false";

		/* ? の箇所を置換するデータの配列を定義 */
		Object[] param = { employeeName, clientId, hourlyWage, paidHolidayStd, updatedUser, employeeId };

		/* クエリを実行 */
		result = jdbcTemplate.update(sql, param);

		/* 実行件数を返す */
		return result;
	}

	/**
	 * 有給休暇情報を更新するSQLを実行する
	 * 
	 * @param
	 * @return result 更新件数
	 */
	public int updatePaidVacation(Integer remaindThisYear, Integer remaindLastYear, String updatedUser,
			String employeeId) {

		/* 更新した件数を初期化 */
		int result = 0;

		/* SQL文作成 */
		String sql = "UPDATE m_employee_paid_vacation SET remaind_this_year=?, remaind_last_year=?, updated_at=NOW(), updated_user=?"
				+ " WHERE employee_id=? AND delete_flg=false";

		/* ? の箇所を置換するデータの配列を定義 */
		Object[] param = { remaindThisYear, remaindLastYear, updatedUser, employeeId };

		/* クエリを実行 */
		result = jdbcTemplate.update(sql, param);

		/* 実行件数を返す */
		return result;
	}

	/**
	 * 指定された社員情報を抽出するSQLを実行する。
	 * ページネーション
	 * 引数にページネーションで使用するオフセット数とページサイズを設定
	 * 
	 * @param employeeId 抽出対象の社員番号のString, employeeName 抽出対象の社員氏名のString, clientId
	 *                   抽出対象の担当顧客番号のString, clientName 抽出対象の担当顧客名のString
	 * @return emp 抽出結果のemp
	 */
	public List<Map<String, Object>> searchEmployeeActive(Integer employeeId, String employeeName, Integer clientId,
			String clientName, int offset, int pageSize) {

		/* SQL文作成 */
		String sql = "SELECT m_employee.employee_id, m_employee.employee_name, m_employee.client_id,"
				+ " m_client.client_name, m_employee.hourly_wage,"
				+ " m_employee.paid_holiday_std, m_employee_paid_vacation.remaind_this_year, m_employee_paid_vacation.remaind_last_year,"
				/*現在の年数の場合THENで1をCASEWHENYEARに返す、一致しない場合はNULLを返す*/
				//count部分現在の年の有給ではなく有給基準日から1年に変更しないとダメ 未完成
				+ " COUNT(CASE WHEN YEAR(t_work_leave_application.holiday_date)=YEAR(NOW()) THEN 1 END) AS holiday_count, t_work_leave_application.application_class"
				+ " FROM m_employee "
				+ " LEFT JOIN m_client ON m_employee.client_id = m_client.client_id"
				+ " LEFT JOIN m_employee_paid_vacation ON m_employee.employee_id = m_employee_paid_vacation.employee_id"
				+ " LEFT JOIN t_work_leave_application ON m_employee.employee_id = t_work_leave_application.employee_id"
				+ " WHERE m_employee.delete_flg = false";

		/* SQLの条件部分を構築するためのパラメータリスト */
		List<Object> params = new ArrayList<>();

		/* 社員番号が指定されている場合、条件に追加 */
		if (employeeId != null) {
			sql += " AND m_employee.employee_id = ?";
			params.add(employeeId);
		}

		/* 社員氏名が指定されている場合、条件に追加 */
		if (!employeeName.isEmpty()) {
			sql += " AND m_employee.employee_name LIKE ?";
			params.add("%" + employeeName + "%");
		}

		/* 担当顧客番号が指定されている場合、条件に追加 */
		if (clientId != null) {
			sql += " AND m_employee.client_id = ?";
			params.add(clientId);
		}

		/* 担当顧客名が指定されている場合、条件に追加 */
		if (!clientName.isEmpty()) {
			sql += " AND m_client.client_name LIKE ?";
			params.add("%" + clientName + "%");
		}

		/* GROUP BY 句を追加 */
		sql += " GROUP BY m_employee.employee_id, m_employee.employee_name, m_employee.client_id,"
				+ " m_client.client_name, m_employee.hourly_wage, m_employee.paid_holiday_std, m_employee_paid_vacation.remaind_this_year,"
				+ " m_employee_paid_vacation.remaind_last_year, t_work_leave_application.application_class";

		/* ORDER BY 句を追加 */
		sql += " ORDER BY m_employee.employee_id ASC";

		/* LIMITとOFFSETのパラメータを追加 */
		sql += " LIMIT ? OFFSET ?";
		params.add(pageSize);
		params.add(offset);

		/* パラメータリストを配列に変換 */
		Object[] param = params.toArray();

		/* クエリを実行 */
		List<Map<String, Object>> emp = jdbcTemplate.queryForList(sql, param);

		/* 取得したデータを返す */
		return emp;
	}

	//追加
	/**
	 * 選択された社員情報を論理削除するSQLを実行する
	 * 
	 * @param employeeId 削除対象の社員IDのString配列, updatedUser 削除対象のレコード最終更新ユーザID
	 * @return result 削除件数
	 */
	public int deleteSelectEmployee(String dempId, String updatedUser) {

		/* 削除した件数を初期化 */
		int result = 0;

		/* SQL文の作成 */
		String sql = "UPDATE m_employee SET delete_flg=1, updated_at=CURRENT_TIMESTAMP, updated_user=? WHERE employee_id=? AND delete_flg=0";

		Object[] param = { updatedUser, dempId };

		/* クエリを実行 */
		result += jdbcTemplate.update(sql, param);

		/* 実行件数を返す */
		return result;
	}

	/**
	 * 選択された有給休暇情報を論理削除するSQLを実行する
	 * 
	 * @param employeeId 削除対象の社員IDのString配列, updatedUser 削除対象のレコード最終更新ユーザID,
	 *                   paidHoliday 削除対象の有給基準日
	 * @return result 削除件数
	 */
	public int deleteSelectPaidVacation(String dEmpId, String updatedUser) {

		/* 削除した件数を初期化 */
		int result = 0;

		/* SQL文の作成 */
		String sql = "UPDATE m_employee_paid_vacation SET delete_flg=1, updated_at=NOW(), updated_user=?"
				+ " WHERE employee_id=? AND delete_flg=0";

		/* ? の箇所を置換するデータの配列を定義 */
		Object[] param = { updatedUser, dEmpId };

		/* クエリを実行 */
		result += jdbcTemplate.update(sql, param);

		/* 実行件数を返す */
		return result;
	}

	//追加 年ごとの件数を取得 yearをコントローラで一昨年、去年、今年にする
	public int vacationCount(String employeeId, Integer year) {

		String sql = "SELECT COUNT(*) FROM t_work_leave_application WHERE employee_id=? AND holiday_date LIKE ?";

		Object[] param = { employeeId, year + "%" };

		Integer result = jdbcTemplate.queryForObject(sql, Integer.class, param);

		return result;
	}

	//追加
	public List<Map<String, Object>> vacationList(String employeeId, Integer year) {

		String sql = "SELECT holiday_date FROM t_work_leave_application WHERE employee_id=? AND holiday_date LIKE ? ORDER BY holiday_date ASC";

		Object[] param = { employeeId, year + "%" };

		List<Map<String, Object>> map = jdbcTemplate.queryForList(sql, param);

		return map;

	}

	/**
	 * 顧客選択ダイアログ用検索
	 */
	public List<Map<String, Object>> getClient(String id, String name) {

		// SQL文作成
		String sql = "SELECT client_id,client_name \r\n"
				+ "FROM m_client \r\n"
				+ "WHERE delete_flg = 0 ";

		List<Object> paramList = new ArrayList<>();

		if (!id.isEmpty()) {
			sql += "AND client_id = ? ";
			paramList.add(id);
		}
		if (!name.isEmpty()) {
			sql += "AND client_name LIKE ? ";
			paramList.add("%" + name + "%");
		}

		Object[] param = paramList.toArray();

		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, param);

		return list;
	}

}
