package com.nexus.whc.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.nexus.whc.models.EmployeeDate;
import com.nexus.whc.repository.EmployeeRepository;

/**
 * EmployeetService.java
 * 社員マスタで使用する社員情報の登録・検索・編集・削除に関する処理を
 * EmployeeDaoクラスからEmployeeControllerクラスに提供する
 *
 * @author 寺島 健太
 *
 */
@Service
public class EmployeeService {

	@Autowired
	EmployeeRepository employeeRepository;

	/**
	 * ページネーション
	 * 引数にページネーションで使用するオフセット数とページサイズを設定
	 * delete_flg=falseの社員情報を抽出するSQLを実行する。
	 * @return list 抽出結果のlist
	 */
	public List<Map<String, Object>> searchActive() {

		/*クエリを実行*/
		List<Map<String, Object>> list = employeeRepository.searchActive();

		/*取得したリストを返す*/
		return list;
	}

	public List<Map<String, Object>> searchAll() {
		/*クエリを実行*/
		List<Map<String, Object>> list = employeeRepository.searchAll();

		/*取得したリストを返す*/
		return list;

	}

	/**
	 * 社員情報を全件を取得してresultで返すSQLを実行する
	 * ページネーションで使用
	 * @return result 全件取得結果のresult
	 */
	public int countEmployee() {

		/*クエリを実行*/
		int result = employeeRepository.countEmployee();

		/*実行件数を返す*/
		return result;
	}

	/**
	 * 指定された社員情報を抽出するSQLを実行する。
	 * @return emp 抽出結果のemp
	 */
	public List<Map<String, Object>> searchEmployee(Integer employeeId, String employeeName,
			Integer clientId, String clientName) {

		/*クエリを実行*/
		List<Map<String, Object>> emp = employeeRepository.searchEmployee(employeeId, employeeName,
				clientId, clientName);

		/* 取得したデータを返す*/
		return emp;
	}

	/**
	 * 指定された社員情報を新規登録するSQLを実行する。
	 * @return result 登録件数
	 */
	public int registEmployee(String employeeId, String employeeName, String clientId, boolean hourlyWage,
			String paidHolidayStd, String createdUser, String updatedUser) {

		/*クエリを実行*/
		int result = employeeRepository.registEmployee(employeeId, employeeName, clientId, hourlyWage, paidHolidayStd,
				createdUser, updatedUser);

		/*実行件数を返す*/
		return result;
	}

	/**
	 * 指定された有給休暇情報を新規登録するSQLを実行する。
	 * @return result 登録件数
	 */
	public int registPaidVacation(Integer seqId, String employeeId, String paidHolidayStd, Integer remaindThisYear,
			Integer remaindLastYear, String createdUser, String updatedUser) {

		/*クエリを実行*/
		int result = employeeRepository.registPaidVacation(seqId, employeeId, paidHolidayStd, remaindThisYear,
				remaindLastYear, createdUser, updatedUser);

		/*実行件数を返す*/
		return result;
	}

	/**
	 * 指定された社員情報を論理削除するSQLを実行する
	 * @return result 削除件数
	 */
	public int deleteEmployee(String[] employeeId, String updatedUser) {

		/*クエリを実行*/
		int result = employeeRepository.deleteEmployee(employeeId, updatedUser);

		/*実行件数を返す*/
		return result;
	}

	/**
	 * 指定された有給休暇マスタを論理削除するSQLを実行する
	 * @param employeeId 削除対象の社員IDのString配列
	 * @return result 削除件数
	 */
	public int deletePaidVacation(String[] employeeId, String updatedUser) {

		/*クエリを実行*/
		int result = employeeRepository.deletePaidVacation(employeeId, updatedUser);

		/*実行件数を返す*/
		return result;
	}

	/**
	 * 社員情報を新規登録、登録済み社員氏名、社員番号の重複チェック。
	 * @return attributeValue エラーメッセージ
	 */
	public String registJudge(String employeeId, String employeeName, String clientId, boolean hourlyWage,
			String paidHolidayStd, String createdUser, String updatedUser) {

		/*エラーメッセージを格納する変数を定義*/
		String attributeValue = "";
		/*社員番号の重複判定、重複の場合はtrueを返す*/
		boolean empIdDuplicate = false;
		/*社員氏名の重複判定、重複の場合はtrueを返す*/
		boolean empNameDuplicate = false;

		try {
			/*重複する社員番号がないか検索  重複時true 重複なしfalse*/
			empIdDuplicate = employeeRepository.isDuplicateEmpId(employeeId);
			/*重複する社員氏名がないか検索 重複時true 重複なしfalse*/
			empNameDuplicate = employeeRepository.isDuplicateEmpName(employeeName);

			/*出力情報を登録処理*/
			employeeRepository.registEmployee(employeeId, employeeName, clientId, hourlyWage, paidHolidayStd,
					createdUser, updatedUser);

			/*社員氏名、社員番号が重複している場合の例外処理*/
		} catch (DuplicateKeyException e) {

			/*社員番号、社員氏名　両方重複の場合*/
			if (empIdDuplicate == true && empNameDuplicate == true) {
				attributeValue = "社員番号と社員氏名に入力した入力内容は社員マスタにすでに存在しています。";
				/*社員番号のみ重複の場合*/
			} else if (empIdDuplicate == true) {
				attributeValue = "社員番号に入力した入力内容は社員マスタにすでに存在しています。";
				/*社員氏名のみ重複の場合*/
			} else if (empNameDuplicate == true) {
				attributeValue = "社員氏名に入力した入力内容は社員マスタにすでに存在しています。";
			}
		}

		/*エラーメッセージを返す*/
		return attributeValue;
	}

	/**
	 * クリックされた社員情報を抽出するSQLを実行する
	 * @return empList 抽出結果のempList
	 */
	public EmployeeDate searchEmployeeName(String employeeId) {

		/*クエリを実行*/
		Map<String, Object> list = employeeRepository.searchEmployeeName(employeeId);

		EmployeeDate employeeDate = new EmployeeDate();
		employeeDate.setEmployeeId(list.get("employee_id").toString());
		employeeDate.setEmployeeName(list.get("employee_name").toString());
		employeeDate.setClientId(list.get("client_id").toString());
		employeeDate.setClientName(list.get("client_name").toString());
		employeeDate.setPaidHolidayStd(list.get("paid_holiday_std").toString());
		employeeDate.setRemaindThisYear(Integer.parseInt(list.get("remaind_this_year").toString()));
		employeeDate.setRemaindLastYear(Integer.parseInt(list.get("remaind_last_year").toString()));
		employeeDate.setHolidayCount(Integer.parseInt(list.get("holiday_count").toString()));

		if (list.get("hourly_wage").toString().equals("true")) {
			employeeDate.setHourlyWage(true);
		} else {
			employeeDate.setHourlyWage(false);
		}

		return employeeDate;
	}

	/**
	 * 社員情報を更新するSQLを実行する
	 * @return result 更新件数
	 */
	public int updateEmployee(String employeeName, String clientId, boolean hourlyWage, String paidHolidayStd,
			String updatedUser, String employeeId) {

		/*クエリを実行*/
		int result = employeeRepository.updateEmployee(employeeName, clientId, hourlyWage, paidHolidayStd, updatedUser,
				employeeId);

		/*実行件数を返す*/
		return result;

	}

	/**
	 * 有給休暇情報を更新するSQLを実行する
	 * @return result 更新件数
	 */
	public int updatePaidVacation(Integer remaindThisYear, Integer remaindLastYear, String updatedUser,
			String employeeId) {

		/*更新した件数*/
		int result = employeeRepository.updatePaidVacation(remaindThisYear, remaindLastYear, updatedUser, employeeId);

		/*実行件数を返す*/
		return result;
	}

	public List<Map<String, Object>> searchEmployeeActive(Integer employeeId, String employeeName,
			Integer clientId, String clientName, int pageNumber, int pageSize) {

		/*オフセット数を定義*/
		int offset = pageNumber * pageSize;

		/*クエリを実行*/
		List<Map<String, Object>> emp = employeeRepository.searchEmployeeActive(employeeId, employeeName,
				clientId, clientName, offset, pageSize);

		/* 取得したデータを返す*/
		return emp;
	}

	/**
	 * 指定された社員情報の件数を取得する。
	 * @return count 抽出結果のcount
	 */
	public int searchEmpCount(Integer employeeId, String employeeName, Integer clientId,
			String clientName) {

		/*件数を初期化*/
		int count = 0;

		/*クエリを実行*/
		List<Map<String, Object>> list = searchEmployee(employeeId, employeeName, clientId,
				clientName);

		/*クエリの実行件数を代入*/
		count = list.size();

		/*実行件数を返す*/
		return count;

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

		/* クエリを実行 */
		result += employeeRepository.deleteSelectEmployee(dempId, updatedUser);

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

		/* クエリを実行 */
		result += employeeRepository.deleteSelectPaidVacation(dEmpId, updatedUser);

		/* 実行件数を返す */
		return result;
	}

	//追加 うまくいってない
	public int vacationCount(String employeeId, Integer year) {

		/*クエリを実行*/
		int count = employeeRepository.vacationCount(employeeId, year);

		/*実行件数を返す*/
		return count;
	}

	public List<Map<String, Object>> vacationList(String employeeId, Integer year) {

		List<Map<String, Object>> map = employeeRepository.vacationList(employeeId, year);

		return map;

	}

	/**
	 * 顧客選択ダイアログ用検索
	 */
	public List<Map<String, Object>> getClient(String id, String name) {

		List<Map<String, Object>> list = employeeRepository.getClient(id, name);

		return list;
	}

}
