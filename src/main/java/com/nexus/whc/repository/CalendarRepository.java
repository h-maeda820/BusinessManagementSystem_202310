package com.nexus.whc.repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexus.whc.models.CalendarData;
import com.nexus.whc.models.CalendarSearchForm;

/*
 * CalendarRepository.java
 * 
 * CalendarRepositoryクラス
 */

/*
 * Repositoryクラス
 */
@Repository
public class CalendarRepository {

	/* JdbcTemplate */
	private final JdbcTemplate jdbcTemplate;

	/* CalendarRepositoryクラス */
	@Autowired
	public CalendarRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * カレンダー一覧
	 * カレンダー情報を全件取得してListで返すSQLを実行する
	 * @param なし
	 * @return list 全件取得結果のList
	 */
	public List<Map<String, Object>> searchCalendarAll(int offset, int pageSize) {

		// SQL文作成
		String sql = "SELECT\n"
				+ "    m_calendar.seq_id,\n"
				+ "    m_calendar.client_id,\n"
				+ "    m_client.client_name,\n"
				+ "    m_calendar.employee_id,\n"
				+ "    m_employee.employee_name AS employee_name,\n"
				+ "    m_calendar.year_month,\n"
				+ "    m_calendar.monthly_holidays,\n"
				+ "    m_calendar.monthly_prescribed_days,\n"
				+ "    m_calendar.comment,\n"
				+ "    m_calendar.created_at,\n"
				+ "    created_user.user_name AS created_user,\n"
				+ "    m_calendar.updated_at,\n"
				+ "    updated_user.user_name AS updated_user\n"
				+ "FROM\n"
				+ "    m_calendar\n"
				+ "INNER JOIN\n"
				+ "    m_client ON m_calendar.client_id = m_client.client_id\n"
				+ "LEFT JOIN\n"
				+ "    m_employee ON m_calendar.employee_id = m_employee.employee_id\n"
				+ "LEFT JOIN\n"
				+ "    m_user AS created_user ON m_calendar.created_user = created_user.user_id\n"
				+ "LEFT JOIN\n"
				+ "    m_user AS updated_user ON m_calendar.updated_user = updated_user.user_id\n"
				+ "WHERE\n"
				+ "    m_calendar.delete_flg != 1\n"
				+ "    AND NOT EXISTS (\n"
				+ "        SELECT 1\n"
				+ "        FROM m_client\n"
				+ "        WHERE m_client.client_id = m_calendar.client_id\n"
				+ "        AND m_client.delete_flg = 1\n"
				+ "    )\n"
				+ "ORDER BY\n"
				+ "    m_calendar.seq_id\n"
				+ "LIMIT ? OFFSET ?\n";

		Object[] param = { pageSize, offset };

		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, param);

		return list;
	}

	public List<Map<String, Object>> searchAll() {
		// SQL文作成
		String sql = "SELECT * from m_calendar WHERE delete_flg != 1";

		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		return list;
	}

	/**
	 * カレンダー情報を全件を取得してresultで返すSQLを実行する
	 * ページネーションで使用
	 * @param なし
	 * @return list 全件取得結果のList
	 */
	public int countCalendar() {

		// 実行結果
		int result = 0;

		// SQL文作成
		String sql = "SELECT COUNT(*)\n"
				+ "FROM (\n"
				+ "    SELECT\n"
				+ "        m_calendar.seq_id,\n"
				+ "        m_calendar.client_id,\n"
				+ "        m_client.client_name,\n"
				+ "        m_calendar.employee_id,\n"
				+ "        m_employee.employee_name AS employee_name,\n"
				+ "        m_calendar.year_month,\n"
				+ "        m_calendar.monthly_holidays,\n"
				+ "        m_calendar.monthly_prescribed_days,\n"
				+ "        m_calendar.comment,\n"
				+ "        m_calendar.created_at,\n"
				+ "        created_user.user_name AS created_user,\n"
				+ "        m_calendar.updated_at,\n"
				+ "        updated_user.user_name AS updated_user\n"
				+ "    FROM\n"
				+ "        m_calendar\n"
				+ "    INNER JOIN\n"
				+ "        m_client ON m_calendar.client_id = m_client.client_id\n"
				+ "    LEFT JOIN\n"
				+ "        m_employee ON m_calendar.employee_id = m_employee.employee_id\n"
				+ "    LEFT JOIN\n"
				+ "        m_user AS created_user ON m_calendar.created_user = created_user.user_id\n"
				+ "    LEFT JOIN\n"
				+ "        m_user AS updated_user ON m_calendar.updated_user = updated_user.user_id\n"
				+ "    WHERE\n"
				+ "        m_calendar.delete_flg != 1\n"
				+ "        AND NOT EXISTS (\n"
				+ "            SELECT 1\n"
				+ "            FROM m_client\n"
				+ "            WHERE m_client.client_id = m_calendar.client_id\n"
				+ "            AND m_client.delete_flg = 1\n"
				+ "        )\n"
				+ ") AS subquery;\n"
				+ "";

		// クエリを実行
		result += jdbcTemplate.queryForObject(sql, Integer.class);

		// 取得した件数を返すF
		return result;
	}

	/**
	 * カレンダー検索
	 * 指定された値を含むListを抽出するSQLを実行する
	 * @param e_id 社員番号
	 * @param c_name 顧客名
	 * @param year_month 年月選択
	 * @param from 年月(～から)
	 * @param to 年月(～まで)
	 * @return
	 */
	public List<Map<String, Object>> searchCalendar(int offset, int pageSize, CalendarSearchForm searchForm) {

		// SQL文作成
		String sql = "SELECT "
				+ "    m_calendar.seq_id, "
				+ "    m_calendar.client_id, "
				+ "    m_client.client_name, "
				+ "    m_calendar.employee_id, "
				+ "    m_employee.employee_name AS employee_name, "
				+ "    m_calendar.year_month, "
				+ "    m_calendar.monthly_holidays, "
				+ "    m_calendar.monthly_prescribed_days, "
				+ "    m_calendar.comment, "
				+ "    m_calendar.created_at, "
				+ "    created_user.user_name AS created_user, "
				+ "    m_calendar.updated_at, "
				+ "    updated_user.user_name AS updated_user "
				+ "FROM "
				+ "    m_calendar "
				+ "INNER JOIN "
				+ "    m_client ON m_calendar.client_id = m_client.client_id "
				+ "LEFT JOIN "
				+ "    m_employee ON m_calendar.employee_id = m_employee.employee_id "
				+ "LEFT JOIN "
				+ "    m_user AS created_user ON m_calendar.created_user = created_user.user_id "
				+ "LEFT JOIN "
				+ "    m_user AS updated_user ON m_calendar.updated_user = updated_user.user_id "
				+ "WHERE "
				+ "    m_calendar.delete_flg != 1 ";

		String sql2 = "AND NOT EXISTS ( "
				+ "      SELECT 1 "
				+ "      FROM m_client "
				+ "      WHERE m_client.client_id = m_calendar.client_id "
				+ "      AND m_client.delete_flg = 1 "
				+ "   ) "
				+ "LIMIT ? OFFSET ? ;";

		List<Object> paramList = new ArrayList<>();

		//年月
		if (!searchForm.getStartYearMonth().isEmpty() || !searchForm.getEndYearMonth().isEmpty()) {

			//フォーマットをデータベースと同じものに変換
			String inputFormatPattern = "yyyy/MM";
			String outputFormatPattern = "";

			if (searchForm.getYearMonthOption() == 1) {
				outputFormatPattern = "yyyy";

			} else if (searchForm.getYearMonthOption() == 2) {
				outputFormatPattern = "yyyy-MM";
			}

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputFormatPattern);
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormatPattern);

			if (!searchForm.getStartYearMonth().isEmpty()) {
				String formattedFrom = YearMonth.parse(searchForm.getStartYearMonth(), inputFormatter).atDay(1)
						.format(outputFormatter);
				sql += " AND m_calendar.year_month >= ? ";
				paramList.add(formattedFrom);
			}
			if (!searchForm.getEndYearMonth().isEmpty()) {
				String formattedTo = YearMonth.parse(searchForm.getEndYearMonth(), inputFormatter).atEndOfMonth()
						.format(outputFormatter);
				sql += " AND m_calendar.year_month <= ? ";
				paramList.add(formattedTo);
			}

		}

		//社員id
		if (searchForm.getEmployeeId() != null) {
			sql += "AND m_calendar.employee_id = ? ";
			paramList.add(searchForm.getEmployeeId());
		}

		//社員名
		if (!searchForm.getEmployeeName().isEmpty()) {
			sql += "AND m_employee.employee_name LIKE ? ";
			paramList.add("%" + searchForm.getEmployeeName() + "%");
		}

		//顧客名
		if (!searchForm.getClientName().isEmpty()) {
			sql += "AND m_client.client_name LIKE ? ";
			paramList.add("%" + searchForm.getClientName() + "%");
		}

		sql += sql2;
		paramList.add(pageSize);
		paramList.add(offset);

		Object[] param = paramList.toArray();

		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, param);

		return list;
	}

	/**
	 * カレンダー検索
	 * 指定された値を含むListを抽出するSQLを実行する
	 * @param e_id 社員番号
	 * @param c_name 顧客名
	 * @param year_month 年月選択
	 * @param from 年月(～から)
	 * @param to 年月(～まで)
	 * @return
	 */
	public int countSearchCalendar(CalendarSearchForm searchForm) {

		// SQL文作成
		String sql = "SELECT "
				+ "    m_calendar.seq_id, "
				+ "    m_calendar.client_id, "
				+ "    m_client.client_name, "
				+ "    m_calendar.employee_id, "
				+ "    m_employee.employee_name AS employee_name, "
				+ "    m_calendar.year_month, "
				+ "    m_calendar.monthly_holidays, "
				+ "    m_calendar.monthly_prescribed_days, "
				+ "    m_calendar.comment, "
				+ "    m_calendar.created_at, "
				+ "    created_user.user_name AS created_user, "
				+ "    m_calendar.updated_at, "
				+ "    updated_user.user_name AS updated_user "
				+ "FROM "
				+ "    m_calendar "
				+ "INNER JOIN "
				+ "    m_client ON m_calendar.client_id = m_client.client_id "
				+ "LEFT JOIN "
				+ "    m_employee ON m_calendar.employee_id = m_employee.employee_id "
				+ "LEFT JOIN "
				+ "    m_user AS created_user ON m_calendar.created_user = created_user.user_id "
				+ "LEFT JOIN "
				+ "    m_user AS updated_user ON m_calendar.updated_user = updated_user.user_id "
				+ "WHERE "
				+ "    m_calendar.delete_flg != 1 ";

		String sql2 = "AND NOT EXISTS ( "
				+ "      SELECT 1 "
				+ "      FROM m_client "
				+ "      WHERE m_client.client_id = m_calendar.client_id "
				+ "      AND m_client.delete_flg = 1 "
				+ "   ) ";

		List<Object> paramList = new ArrayList<>();

		//年月
		if (!searchForm.getStartYearMonth().isEmpty() || !searchForm.getEndYearMonth().isEmpty()) {

			//フォーマットをデータベースと同じものに変換
			String inputFormatPattern = "yyyy/MM";
			String outputFormatPattern = "";

			if (searchForm.getYearMonthOption() == 1) {
				outputFormatPattern = "yyyy";

			} else if (searchForm.getYearMonthOption() == 2) {
				outputFormatPattern = "yyyy-MM";
			}

			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputFormatPattern);
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormatPattern);

			if (!searchForm.getStartYearMonth().isEmpty()) {
				String formattedFrom = YearMonth.parse(searchForm.getStartYearMonth(), inputFormatter).atDay(1)
						.format(outputFormatter);
				sql += " AND m_calendar.year_month >= ? ";
				paramList.add(formattedFrom);
			}
			if (!searchForm.getEndYearMonth().isEmpty()) {
				String formattedTo = YearMonth.parse(searchForm.getEndYearMonth(), inputFormatter).atEndOfMonth()
						.format(outputFormatter);
				sql += " AND m_calendar.year_month <= ? ";
				paramList.add(formattedTo);
			}

		}

		//社員id
		if (searchForm.getEmployeeId() != null) {
			sql += "AND m_calendar.employee_id = ? ";
			paramList.add(searchForm.getEmployeeId());
		}

		//社員名
		if (!searchForm.getEmployeeName().isEmpty()) {
			sql += "AND m_employee.employee_name LIKE ? ";
			paramList.add("%" + searchForm.getEmployeeName() + "%");
		}

		//顧客名
		if (!searchForm.getClientName().isEmpty()) {
			sql += "AND m_client.client_name LIKE ? ";
			paramList.add("%" + searchForm.getClientName() + "%");
		}

		sql += sql2;

		Object[] param = paramList.toArray();

		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, param);

		int result = list.size();

		return result;
	}

	/**
	 * 排他チェック用
	 * @param seqId
	 * @return
	 */
	public Map<String, Object> searchEditCalendar(String seqId) {

		Map<String, Object> map = new HashMap<String, Object>();

		String sql = "SELECT * FROM m_calendar WHERE delete_flg = 0 AND seq_id = ?;";
		Object[] param = { seqId };

		map = jdbcTemplate.queryForMap(sql, param);

		return map;

	}

	/**
	 * カレンダー削除
	 * 指定されたカレンダー情報を論理削除するSQLを実行する
	 * @param seq_id シーケンスID
	 * @return 削除件数
	 */
	public int deleteCalendar(String seq_id, String updated_user) {

		int result = 0;

		String sql = "UPDATE m_calendar\n"
				+ "SET delete_flg = 1,\n"
				+ "    updated_at = NOW(),\n"
				+ "    updated_user = ? \n"
				+ "WHERE seq_id = ?;";

		Object[] param = { updated_user, seq_id };

		result += jdbcTemplate.update(sql, param);

		return result;
	}

	/**
	 * カレンダー詳細削除
	 * 指定されたカレンダー詳細情報を論理削除するSQLを実行する
	 * @param seq_id シーケンスID
	 * @return 削除件数
	 */
	public int deleteCalendarDetail(String seq_id, String updated_user) {

		int result = 0;

		String sql = "UPDATE m_calendar_detail\n"
				+ "SET delete_flg = 1,\n"
				+ "    updated_at = NOW(),\n"
				+ "    updated_user = ? \n"
				+ "WHERE parent_seq_id = ?;\n"
				+ "";

		Object[] param = { updated_user, seq_id };

		result += jdbcTemplate.update(sql, param);
		return result;
	}

	/**
	 * 指定した顧客の、登録されている月と休日数を取得
	 * @param clientId
	 * @param employeeId
	 * @param startOfYear
	 * @param endOfYear
	 * @return
	 */
	public List<Map<String, Object>> searchHolidayCount(Integer clientId, Integer employeeId, String startOfYear,
			String endOfYear) {

		List<Object> paramList = new ArrayList<>();

		// SQL文作成
		String sql = "SELECT DISTINCT\n"
				+ "    m_calendar.year_month,\n"
				+ "    m_calendar.monthly_holidays\n"
				+ "FROM\n"
				+ "    m_calendar\n"
				+ "INNER JOIN\n"
				+ "    m_client ON m_calendar.client_id = m_client.client_id\n"
				+ "WHERE\n"
				+ "    m_calendar.delete_flg != 1 \n";

		//社員idが登録されている場合
		if (employeeId != null) {
			sql += "AND m_calendar.employee_id = ? ";
			paramList.add(employeeId);
		} else {
			sql += "AND m_calendar.employee_id IS NULL ";
		}

		sql += "    AND m_calendar.client_id = ?\n"
				+ "    AND NOT EXISTS (\n"
				+ "        SELECT 1\n"
				+ "        FROM m_client\n"
				+ "        WHERE m_client.client_id = m_calendar.client_id\n"
				+ "        AND m_client.delete_flg = 1\n"
				+ "    )\n"
				+ "    AND m_calendar.year_month BETWEEN ? AND ?;";

		// 他のパラメータを設定
		paramList.add(clientId);
		paramList.add(startOfYear);
		paramList.add(endOfYear);

		Object[] param = paramList.toArray();

		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, param);

		return list;
	}

	/**
	 * 顧客社員が一致する、年度内のデータの通年備考を取得する
	 * @param clientId
	 * @param employeeId
	 * @param startOfYear
	 * @param endOfYear
	 * @return
	 */
	public List<Map<String, Object>> searchAllYearRoundComment(CalendarData calendarData, String startOfYear,
			String endOfYear) {

		List<Object> paramList = new ArrayList<>();

		// SQL文作成
		String sql = "SELECT DISTINCT \n"
				+ "    m_calendar.year_month, "
				+ "    m_calendar.comment "
				+ "FROM\n"
				+ "    m_calendar\n"
				+ "INNER JOIN\n"
				+ "    m_client ON m_calendar.client_id = m_client.client_id\n"
				+ "WHERE\n"
				+ "    m_calendar.delete_flg != 1 \n";

		//社員idが登録されている場合
		if (calendarData.getEmployeeId() != null) {
			sql += "AND m_calendar.employee_id = ? ";
			paramList.add(calendarData.getEmployeeId());
		} else {
			sql += "AND m_calendar.employee_id IS NULL ";
		}

		sql += "    AND m_client.client_name = ?\n"
				+ "    AND NOT EXISTS (\n"
				+ "        SELECT 1\n"
				+ "        FROM m_client\n"
				+ "        WHERE m_client.client_id = m_calendar.client_id\n"
				+ "        AND m_client.delete_flg = 1\n"
				+ "    )\n"
				+ "    AND m_calendar.year_month BETWEEN ? AND ?;";

		// 他のパラメータを設定
		paramList.add(calendarData.getClientName());
		paramList.add(startOfYear);
		paramList.add(endOfYear);

		Object[] param = paramList.toArray();

		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, param);

		return list;
	}

	/**
	 * 通年備考を更新する
	 * @param calendarData
	 * @param yearMonth
	 * @param comment
	 * @param userId
	 */
	public void updateAllYearRoundComment(CalendarData calendarData, LocalDate yearMonth, String userId) {

		String sql = "UPDATE m_calendar\r\n"
				+ "INNER JOIN m_client ON m_calendar.client_id = m_client.client_id\r\n"
				+ "SET \r\n"
				+ "    m_calendar.COMMENT = ? , "
				+ "    m_calendar.updated_at = CURRENT_TIMESTAMP ,"
				+ "    m_calendar.updated_user = ? "
				+ "WHERE  \r\n"
				+ "    m_client.client_name = ? "
				+ "    AND m_calendar.`year_month` = ? ";

		List<Object> paramList = new ArrayList<>();

		paramList.add(calendarData.getAllYearRoundComment());
		paramList.add(userId);
		paramList.add(calendarData.getClientName());
		paramList.add(yearMonth);

		//社員idが登録されている場合
		if (calendarData.getEmployeeId() != null) {
			sql += "AND m_calendar.employee_id = ? ";
			paramList.add(calendarData.getEmployeeId());
		} else {
			sql += "AND m_calendar.employee_id IS NULL ";
		}

		Object[] param = paramList.toArray();

		jdbcTemplate.update(sql, param);

	}

	/**
	 * カレンダー新規登録
	 * 顧客名からidを検索するSQLを実行する
	 * @param client_name
	 * @return
	 */
	public int searchClientId(String clientName) {

		String sql = "SELECT\n"
				+ "    client_id\n"
				+ "FROM\n"
				+ "    m_client\n"
				+ "WHERE\n"
				+ "    client_name = ?;";

		int id = jdbcTemplate.queryForObject(sql, Integer.class, clientName);

		return id;

	}

	/**
	 * カレンダー新規登録
	 * シーケンスidの最大を取得するSQLを実行する
	 * @return
	 */
	public int searchMaxSeqId() {
		String sql = "SELECT MAX(seq_id) FROM m_calendar;";

		int id = jdbcTemplate.queryForObject(sql, Integer.class);

		return id;
	}

	/**
	 * カレンダー新規登録
	 * カレンダー情報をINSERTするSQLを実行する
	 * @param client_id
	 * @param employee_id
	 * @param year_month
	 * @param monthly_holidays
	 * @param monthly_prescribed_days
	 * @param comment
	 * @param created_user
	 * @return シーケンス
	 */
	public int createCalendar(int clientId, Integer employeeId, LocalDate yearMonth, int monthlyHolidays,
			int monthlyPrescribedDays, String comment, String createdUser) {

		int result = 0;

		String sql = "INSERT INTO m_calendar "
				+ "(client_id,"
				+ "employee_id,"
				+ " `year_month`,"
				+ "monthly_holidays,"
				+ "monthly_prescribed_days,"
				+ "comment,"
				+ "delete_flg,"
				+ "created_at,"
				+ "created_user,"
				+ "updated_at,"
				+ "updated_user)"
				+ "VALUES(?,?,?,?,?,?,0,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP,?)";

		Object[] param = { clientId, employeeId, yearMonth,
				monthlyHolidays, monthlyPrescribedDays, comment,
				createdUser, createdUser };

		result += jdbcTemplate.update(sql, param);

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

		int result = 0;

		String sql = "INSERT INTO m_calendar_detail \n"
				+ "    (parent_seq_id, \n"
				+ "	  date, \n"
				+ "	  holiday_flg, \n"
				+ "	  comment, \n"
				+ "	  delete_flg, \n"
				+ "	  created_at, \n"
				+ "	  created_user, \n"
				+ "	  updated_at, \n"
				+ "	  updated_user)\n"
				+ "VALUES \n"
				+ "( ?, ?, ?,?, 0, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?);";

		for (int i = 0; i < day.length; i++) {
			LocalDate date = LocalDate.parse(day[i]);
			Object[] param = { parentSeqId, date, holidayFlg[i], comment[i], createdUser, createdUser };

			result += jdbcTemplate.update(sql, param);
		}

		return result;
	}

	/**
	 * カレンダー閲覧
	 * 指定されたカレンダー情報を抽出するSQLを実行する
	 * @param seq_id
	 * @return 抽出結果のlist
	 */
	public Map<String, Object> searchCalendarData(String seqId) {

		// SQL文作成
		String sql = "SELECT\r\n"
				+ "    m_client.client_name,\r\n"
				+ "    m_calendar.employee_id,\r\n"
				+ "    m_employee.employee_name AS employee_name,\r\n"
				+ "    m_calendar.year_month,\r\n"
				+ "    m_calendar.comment\r\n"
				+ "FROM\r\n"
				+ "    m_calendar\r\n"
				+ "LEFT JOIN\r\n"
				+ "    m_client ON m_calendar.client_id = m_client.client_id\r\n"
				+ "LEFT JOIN\r\n"
				+ "    m_employee ON m_calendar.employee_id = m_employee.employee_id\r\n"
				+ "LEFT JOIN\r\n"
				+ "    m_user AS created_user ON m_calendar.created_user = created_user.user_id\r\n"
				+ "LEFT JOIN\r\n"
				+ "    m_user AS updated_user ON m_calendar.updated_user = updated_user.user_id\r\n"
				+ "WHERE\r\n"
				+ "    m_calendar.seq_id = ?;";

		// パラメータを設定
		Object[] param = { seqId };

		Map<String, Object> list = jdbcTemplate.queryForMap(sql, param);

		return list;
	}

	/**
	 * カレンダー閲覧
	 * 指定されたカレンダー詳細情報を抽出するSQLを実行する
	 * @param seq_id シーケンスID
	 * @return 抽出結果のlist
	 */
	public List<Map<String, Object>> searchCalendarDetails(String seqId) {

		// SQL文作成
		String sql = "SELECT DATE, holiday_flg, comment FROM m_calendar_detail WHERE parent_seq_id = ? ";

		// パラメータを設定
		Object[] param = { seqId };

		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, param);

		return list;
	}

	/**
	 * カレンダー編集
	 * カレンダー情報を更新するSQLを実行する
	 * @param なし
	 * @return 更新件数
	 */
	public int updateCalendar(String seq_id, Integer empId, int monthly_holidays, int monthly_prescribed_days,
			String comment, String updated_user) {

		int result = 0;

		String sql = "UPDATE m_calendar\n"
				+ "SET \n"
				+ "employee_id = ?,\n"
				+ "monthly_holidays = ?,\n"
				+ "monthly_prescribed_days = ?,\n"
				+ "COMMENT = ?,\n"
				+ "updated_at = NOW(),\n"
				+ "updated_user = ? "
				+ "WHERE \n"
				+ "seq_id = ?;\n";

		Object[] param = { empId, monthly_holidays, monthly_prescribed_days, comment, updated_user, seq_id };

		result += jdbcTemplate.update(sql, param);

		return result;
	}

	/**
	 * カレンダー編集
	 * カレンダー詳細情報を更新するSQLを実行する
	 * @param なし
	 * @return 更新件数
	 */
	public int updateCalendarDetails(boolean holiday_flg[], String comment[], String updated_user, String seq_id,
			String date[]) {

		int result = 0;

		for (int i = 0; i < date.length; i++) {

			String sql = "UPDATE m_calendar_detail\n"
					+ "SET \n"
					+ "    holiday_flg = ?,\n"
					+ "    comment = ?,\n"
					+ "    updated_at = NOW(),\n"
					+ "    updated_user = ? \n"
					+ "WHERE \n"
					+ "    parent_seq_id = ?\n"
					+ "    AND DATE = ? \n";

			Object[] param = { holiday_flg[i], comment[i], updated_user, seq_id, date[i] };

			result += jdbcTemplate.update(sql, param);

		}
		return result;
	}

	//重複チェック
	public Integer duplicateCheck(String clientName, String empId, LocalDate year_month) {

		String sql = "SELECT seq_id FROM m_calendar\r\n"
				+ "LEFT JOIN m_client ON m_calendar.client_id = m_client.client_id\r\n"
				+ "WHERE m_client.client_name = ? \r\n"
				+ "AND m_calendar.`year_month` = ? \r\n"
				+ "AND m_calendar.delete_flg = 0 ";

		List<Object> paramList = new ArrayList<>();
		paramList.add(clientName);
		paramList.add(year_month);

		if (!empId.isEmpty()) {
			sql += "AND m_calendar.employee_id = ? ";
			paramList.add(empId);
		} else {
			sql += "AND m_calendar.employee_id IS NULL ";
		}

		Object[] param = paramList.toArray();

		try {
			return jdbcTemplate.queryForObject(sql, param, Integer.class);

		} catch (EmptyResultDataAccessException e) {
			return -1;
		}
	}

	//シーケンスidを取得する
	public Integer getSeqId(CalendarData calendarData, LocalDate year_month) {

		String sql = "SELECT m_calendar.seq_id\n"
				+ "FROM m_calendar \n"
				+ "LEFT JOIN m_client ON m_calendar.client_id = m_client.client_id \n"
				+ "WHERE m_client.client_name= ? \n"
				+ "AND  m_calendar.employee_id = ? \n"
				+ "AND  m_calendar.`year_month` = ? \n"
				+ "AND m_calendar.delete_flg = false ";

		Object[] param = { calendarData.getClientName(), calendarData.getEmployeeId(), year_month };
		try {
			return jdbcTemplate.queryForObject(sql, param, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			return -1;
		}
	}

	//前月を取得する
	public Integer getLastMonth(CalendarData calendarData) {

		String sql = "SELECT m_calendar.seq_id\n"
				+ "FROM m_calendar \n"
				+ "LEFT JOIN m_client ON m_calendar.client_id = m_client.client_id \n"
				+ "WHERE m_client.client_name= ? \n"
				+ "AND  m_calendar.employee_id = ? \n"
				+ "AND  m_calendar.`year_month`< ? \n"
				+ "AND m_calendar.delete_flg = false "
				+ "ORDER BY m_calendar.`year_month` DESC \n"
				+ "LIMIT 1;";

		Object[] param = { calendarData.getClientName(), calendarData.getEmployeeId(), calendarData.getYearMonth() };
		try {
			return jdbcTemplate.queryForObject(sql, param, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			return -1;
		}
	}

	//翌月を取得する
	public Integer getFollowingMonth(CalendarData calendarData) {

		String sql = "SELECT m_calendar.seq_id\n"
				+ "FROM m_calendar \n"
				+ "LEFT JOIN m_client ON m_calendar.client_id = m_client.client_id \n"
				+ "WHERE m_client.client_name= ? \n"
				+ "AND  m_calendar.employee_id = ? \n"
				+ "AND  m_calendar.`year_month`> ? \n"
				+ "AND  m_calendar.delete_flg = false "
				+ "ORDER BY m_calendar.`year_month` ASC \n"
				+ "LIMIT 1;";

		Object[] param = { calendarData.getClientName(), calendarData.getEmployeeId(), calendarData.getYearMonth() };
		try {
			return jdbcTemplate.queryForObject(sql, param, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			return -1;
		}
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

	/**
	 * 社員選択ダイアログ用検索
	 */
	public List<Map<String, Object>> getEmployee(String id, String name) {

		// SQL文作成
		String sql = "SELECT employee_id,employee_name \r\n"
				+ "FROM m_employee \r\n"
				+ "WHERE delete_flg = 0 ";

		List<Object> paramList = new ArrayList<>();

		if (!id.isEmpty()) {
			sql += "AND employee_id = ? ";
			paramList.add(id);
		}
		if (!name.isEmpty()) {
			sql += "AND employee_name LIKE ? ";
			paramList.add("%" + name + "%");
		}

		Object[] param = paramList.toArray();

		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, param);

		return list;
	}

}