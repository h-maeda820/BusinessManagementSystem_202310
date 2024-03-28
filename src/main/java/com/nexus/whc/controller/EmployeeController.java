package com.nexus.whc.controller;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nexus.whc.models.EmployeeDate;
import com.nexus.whc.models.EmployeeSearchForm;
import com.nexus.whc.models.SelectDialogForm;
import com.nexus.whc.services.EmployeeService;
import com.nexus.whc.services.ExclusiveCheckService;

/**
* EmployeeController.java
* 社員マスタに関するアプリケーション制御を行うクラス
*
* @author 寺島 健太
*
*/
@Controller
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	EmployeeService employeeService;

	@Autowired
	ExclusiveCheckService exclusiveCheck;

	@Autowired
	HttpSession session;

	/*ここから変数を定義*/

	/*tableNumber2 m_employee 排他チェック用*/
	int tableNumber = 2;
	int editTableName = 2;

	/*ページサイズを設定*/
	int pageSize = 20;

	/*遷移先を変数に格納(社員マスタ一覧画面)*/
	String res1 = "SMSEM001";
	/*遷移先を変数に格納(社員マスタ登録画面)*/
	String res2 = "SMSEM002";

	/*新規追加モードの判別用*/
	String registMode = "r";
	/*更新モードの判別用*/
	String updateMode = "u";

	/**
	 * 一覧
	 * @param pageNumber
	 * @param searchForm
	 * @param displaySearchForm
	 * @param selectDialogForm
	 * @param model
	 * @return
	 */
	@GetMapping("/list")
	public String employeeList(@RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber,
			@ModelAttribute("employeeSearchForm") EmployeeSearchForm searchForm,
			@ModelAttribute("displayEmployeeSearchForm") EmployeeSearchForm displaySearchForm,
			@ModelAttribute("selectDialogForm") SelectDialogForm selectDialogForm,
			Model model) {

		/**現在のページとページサイズを引数に設定
		* 1ページずれるためpageを-1
		*/
		List<Map<String, Object>> list = employeeService.searchEmployeeActive(searchForm.getEmployeeId(),
				searchForm.getEmployeeName(), searchForm.getClientId(),
				searchForm.getClientName(), pageNumber - 1, pageSize);

		List<Map<String, Object>> empList = new ArrayList<>();

		List<Map<String, Object>> listAll = employeeService.searchActive();
		List<Map<String, Object>> countList = new ArrayList<>();

		/*空だったらエラーメッセージとセッションスコープに””を入れる*/
		if (list.isEmpty()) {
			List<Map<String, Object>> all = employeeService.searchAll();
			if (!all.isEmpty()) {
				model.addAttribute("searchError", "社員一覧の検索結果は0件です。条件を変更し、再度検索してください。");
			}
		} else {

			//ステータスを設定する
			for (Map<String, Object> status : list) {

				//有給残日数(当年度分)を取得　
				Integer remaindThisYear = Integer.parseInt(status.get("remaind_this_year").toString());
				//有給残日数(前年度分)を取得
				Integer remaindLastYear = Integer.parseInt(status.get("remaind_last_year").toString());
				//有休基準日を取得
				LocalDate paidHolidayStd = LocalDate.parse(status.get("paid_holiday_std").toString());

				//ステータスを取得して保存
				String attributeValue = getStatus(remaindThisYear, remaindLastYear, paidHolidayStd);
				status.put("application_class", attributeValue);

				//顧客番号を3桁に変換
				String formattedClientId = String.format("%03d", (Integer) status.get("client_id"));
				status.put("client_id", formattedClientId);

				//社員番号を4桁に変換
				String formattedEmployeeId = String.format("%04d", (Integer) status.get("employee_id"));
				status.put("employee_id", formattedEmployeeId);
			}
			//ページ数カウント用listにもステータスを設定
			for (Map<String, Object> map : listAll) {
				//有給残日数(当年度分)を取得　
				Integer remaindThisYear = Integer.parseInt(map.get("remaind_this_year").toString());
				//有給残日数(前年度分)を取得
				Integer remaindLastYear = Integer.parseInt(map.get("remaind_last_year").toString());
				//有休基準日を取得
				LocalDate paidHolidayStd = LocalDate.parse(map.get("paid_holiday_std").toString());

				//ステータスを取得して保存
				String attributeValue = getStatus(remaindThisYear, remaindLastYear, paidHolidayStd);
				map.put("application_class", attributeValue);
			}

			//ステータスのどれかにチェックがついているとき
			if (searchForm.isNoPaidHoliday() || searchForm.isNotificationPaidHoliday()
					|| searchForm.isAttentionPaidHoliday() || searchForm.isWarningPaidHoliday()) {
				//ステータス検索
				for (Map<String, Object> map : list) {

					if (searchForm.isNoPaidHoliday() && map.get("application_class").toString().equals("有給残日数なし")) {
						empList.add(map);
					}
					if (searchForm.isNotificationPaidHoliday()
							&& map.get("application_class").toString().equals("有給取得日数不足(通知)")) {
						empList.add(map);
					}
					if (searchForm.isAttentionPaidHoliday()
							&& map.get("application_class").toString().equals("有給取得日数不足(注意)")) {
						empList.add(map);
					}
					if (searchForm.isWarningPaidHoliday()
							&& map.get("application_class").toString().equals("有給取得日数不足(警告)")) {
						empList.add(map);
					}
				}
				//カウント用list
				for (Map<String, Object> map : listAll) {

					if (searchForm.isNoPaidHoliday() && map.get("application_class").toString().equals("有給残日数なし")) {
						empList.add(map);
					}
					if (searchForm.isNotificationPaidHoliday()
							&& map.get("application_class").toString().equals("有給取得日数不足(通知)")) {
						empList.add(map);
					}
					if (searchForm.isAttentionPaidHoliday()
							&& map.get("application_class").toString().equals("有給取得日数不足(注意)")) {
						empList.add(map);
					}
					if (searchForm.isWarningPaidHoliday()
							&& map.get("application_class").toString().equals("有給取得日数不足(警告)")) {
						empList.add(map);
					}
				}
			} else {
				empList = list;
				countList = listAll;
			}
		}

		if (displaySearchForm.isInitialized()) {
			model.addAttribute("employeeSearchForm", searchForm);
		} else {
			model.addAttribute("employeeSearchForm", displaySearchForm);
		}

		//ダイアログ表示処理
		dialogProcess(selectDialogForm, model);
		//ダイアログ検索ボタン押下時用にセッションスコープにも保存
		session.setAttribute("employeeSearchForm", searchForm);

		model.addAttribute("list", empList);

		//ページネーション用
		//アクティブなデータの総数
		int totalCount = countList.size();

		//20件ずつ表示したときのページ数
		int totalPage = (int) Math.ceil((double) totalCount / pageSize);
		boolean pageDisplay = true;

		List<String> pageList = new ArrayList<>();
		for (int i = 0; i < totalPage; i++) {
			pageList.add(String.valueOf(i + 1));
		}
		//ページ数が1の場合ページネーション表示なし
		if (pageList.size() <= 1) {
			pageDisplay = false;
		}

		session.setAttribute("pageList", pageList);

		//表示ページ数セット
		List<String> pageList2 = new ArrayList<>();

		//最初のページ番号指定
		if (pageNumber > pageList.indexOf("3") && pageNumber < pageList.size() && pageNumber != 1) {
			for (int i = pageNumber - 2; i < pageList.size(); i++) {
				pageList2.add(String.valueOf(i + 1));
				session.setAttribute("pageList2", pageList2);
			}
		} else if (pageNumber == pageList.size() && pageNumber > 3) {
			for (int i = pageNumber - 3; i < pageList.size(); i++) {
				pageList2.add(String.valueOf(i + 1));
				session.setAttribute("pageList2", pageList2);
			}
		} else if (pageNumber <= 2) {
			session.removeAttribute("pageList2");
		}

		//一つ前のページ
		if (pageNumber != 1) {
			model.addAttribute("previous_page", pageNumber - 1);
		}
		//一つ先のページ
		if (pageNumber != pageList.size()) {
			model.addAttribute("next_page", pageNumber + 1);
		} else {
			model.addAttribute("next_page", pageNumber);
		}

		model.addAttribute("pageDisplay", pageDisplay);
		model.addAttribute("pageNumber", pageNumber);
		model.addAttribute("totalPage", totalPage);

		return res1;
	}

	/**
	 * 検索
	 * @param searchForm
	 * @param displaySearchForm
	 * @param selectDialogForm
	 * @param pageNumber
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/search")
	public String searchCalendar(@ModelAttribute("employeeSearchForm") EmployeeSearchForm searchForm,
			@ModelAttribute("displayEmployeeSearchForm") EmployeeSearchForm displaySearchForm,
			@ModelAttribute("selectDialogForm") SelectDialogForm selectDialogForm,
			@RequestParam(name = "pageNumber", defaultValue = "1") String pageNumber,
			RedirectAttributes redirectAttributes) {

		redirectAttributes.addFlashAttribute("employeeSearchForm", searchForm);
		redirectAttributes.addFlashAttribute("displayEmployeeSearchForm", displaySearchForm);
		redirectAttributes.addFlashAttribute("selectDialogForm", selectDialogForm);

		return "redirect:/employee/list?pageNumber=" + pageNumber;
	}

	/**
	 * ページネーション
	 * @param pageNumber
	 * @param redirectAttributes
	 * @return
	 */
	@GetMapping("/pageList")
	public String pageListCalendarGet(@RequestParam(name = "pageNumber", defaultValue = "1") String pageNumber,
			RedirectAttributes redirectAttributes) {

		EmployeeSearchForm searchForm = (EmployeeSearchForm) session.getAttribute("employeeSearchForm");

		redirectAttributes.addFlashAttribute("employeeSearchForm", searchForm);

		return "redirect:/employee/list?pageNumber=" + pageNumber;
	}


	/**
	 * SESEM001 社員マスタ選択行削除処理
	 * ★選択行削除時に実行
	 * 以下の時はエラーを発出
	 * ・選択されていないの場合
	 * ・排他チェックエラー時
	 */
	@PostMapping("selectDelete")
	public String selectDelete(@RequestParam(name = "selectCheck", required = false) String[] selectCheck,
			RedirectAttributes attr, @ModelAttribute EmployeeDate employeeDate) {

		/*排他チェック用のログインId*/
		String loginUserId = "nexus001";

		/*EmployeeDateを格納*/
		String updatedUser = employeeDate.getUpdatedUser();

		/*削除件数用の変数を定義*/
		int deleteResult = 0;

		/*エラーメッセージ格納用の変数を宣言*/
		String result;

		if (selectCheck != null) {
			/*排他チェック*/
			for (String dEmpId : selectCheck) {

				/*排他チェック処理削除チェック*/
				if (exclusiveCheck.ExclusiveCheckDalete(dEmpId, tableNumber)) {

					result = "該当データはすでに削除されています。";
					attr.addFlashAttribute("result", result);

					return "redirect:/employee/list";
				}

				/*排他チェック（編集中）*/
				if (exclusiveCheck.ExclusiveCheckEdited(dEmpId, loginUserId, editTableName)) {
					result = "該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。";
					attr.addFlashAttribute("result", result);

					return "redirect:/employee/list";
				}

				/*排他チェック（編集中ロック解除）*/
				exclusiveCheck.ExclusiveLockDalete(dEmpId, loginUserId, tableNumber);

				/*排他チェックに問題がなければ社員マスタと有給休暇マスタの削除SQLを実行*/
				deleteResult += employeeService.deleteSelectEmployee(dEmpId, updatedUser);
				employeeService.deleteSelectPaidVacation(dEmpId, updatedUser);
				attr.addFlashAttribute("result", deleteResult + "件削除しました。");
			}
		} else {
			attr.addFlashAttribute("result", "対象が選択されていません。対象を選択してください。");
		}

		return "redirect:/employee/list";
	}


	/**
	 * SESEM001 社員マスタ選択行帳票出力処理
	 * ★選択行帳票出力時に実行
	 * 以下の時はエラーを発出
	 * ・選択されていないの場合
	 * ・排他チェックエラー時
	 */
	@PostMapping("formOutput")
	public String formOutput(@RequestParam(name = "selectCheck", required = false) String dEmpId,
			RedirectAttributes attr, Model model, @ModelAttribute EmployeeDate employeeDate) {

		/*排他チェック用のログインId*/
		String loginUserId = "nexus001";

		/*エラーメッセージ格納用の変数を宣言*/
		String result;

		/*チェックがされていたら*/
		if (dEmpId != null) {

			/*排他チェック処理削除チェック*/
			if (exclusiveCheck.ExclusiveCheckDalete(dEmpId, tableNumber)) {

				result = "該当データはすでに削除されています。";
				attr.addFlashAttribute("result", result);

				return "redirect:/employee/list";
			}

			/*排他チェック（編集中）*/
			if (exclusiveCheck.ExclusiveCheckEdited(dEmpId, loginUserId, editTableName)) {
				result = "該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。";
				attr.addFlashAttribute("result", result);

				return "redirect:/employee/list";
			}

			/*チェックがされていなかったら*/
		} else {
			attr.addFlashAttribute("result", "対象が選択されていません。対象を選択してください。");
			return "redirect:/employee/list";
		}

		/*クリックされた社員番号で該当の社員情報の検索をするSQL実行*/
		EmployeeDate empData = employeeService.searchEmployeeName(dEmpId);

		/*listの情報をスコープに保存*/
		model.addAttribute("employeeDate", empData);

		/*現在の年を取得*/
		Year currentYear = Year.now();

		/*変数に格納*/
		Integer year = currentYear.getValue();

		/*一昨年、昨年、今年の年数をスコープに保存*/
		model.addAttribute("nowYear", year + "年");
		model.addAttribute("lastYear", (year - 1) + "年");
		model.addAttribute("twoYearsAgo", (year - 2) + "年");

		/*一昨年、昨年、今年の有休取得数取得しスコープに保存*/
		int countTwoYearsAgo = employeeService.vacationCount(dEmpId, year - 2);
		model.addAttribute("countTwoYearsAgo", countTwoYearsAgo);
		int countLastYear = employeeService.vacationCount(dEmpId, year - 1);
		model.addAttribute("countLastYear", countLastYear);
		int countYear = employeeService.vacationCount(dEmpId, year);
		model.addAttribute("countYear", countYear);

		/*日付を"MM/dd"の形式にフォーマットするためのfomatterを定義*/
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");

		/*一昨年の有給取得日を取得*/
		List<Map<String, Object>> vacationTwoYearsAgoList = employeeService.vacationList(dEmpId, year - 2);
		List<Map<String, Object>> vacationLastYearList = employeeService.vacationList(dEmpId, year - 1);
		List<Map<String, Object>> vacationNowYearList = employeeService.vacationList(dEmpId, year);

		/*vacationTwoYearsAgoList(一昨年)の件数分ループ処理*/
		for (int i = 0; i < vacationTwoYearsAgoList.size(); i++) {

			/*vacationList(一昨年)の各要素を取得*/
			Map<String, Object> vacationTwoYearsAgo = vacationTwoYearsAgoList.get(i);

			/*holiday_dateで格納されている日付を取得*/
			Date sqlDate = (Date) vacationTwoYearsAgo.get("holiday_date");

			/*データが入っている場合の処理*/
			if (sqlDate != null) {

				/*date型からLocalDate型に変換*/
				LocalDate date = sqlDate.toLocalDate();

				/*TwoYearsAgoVacation1,TwoYearsAgoVacation2...とフォーマットした日付をスコープに保存*/
				model.addAttribute("TwoYearsAgoVacation" + (i + 1), date.format(formatter));

				/*データがない場合の処理*/
			} else {

				/*TwoYearsAgoVacation1,TwoYearsAgoVacation2...と空をスコープに保存*/
				model.addAttribute("TwoYearsAgoVacation" + (i + 1), "");
			}
		}

		/*vacationLastYearList(昨年)の件数分ループ処理*/
		for (int i = 0; i < vacationLastYearList.size(); i++) {

			/*vacationLastList(一昨年)の各要素を取得*/
			Map<String, Object> vacationTwoYearsAgo = vacationLastYearList.get(i);

			/*holiday_dateで格納されている日付を取得*/
			Date sqlDateL = (Date) vacationTwoYearsAgo.get("holiday_date");

			/*データが入っている場合の処理*/
			if (sqlDateL != null) {

				/*date型からLocalDate型に変換*/
				LocalDate dateL = sqlDateL.toLocalDate();

				/*lastYearVacation1,lastYearVacation2...とフォーマットした日付をスコープに保存*/
				model.addAttribute("lastYearVacation" + (i + 1), dateL.format(formatter));

				/*データがない場合の処理*/
			} else {

				/*lastYearVacation1,lastYearVacation2...と空をスコープに保存*/
				model.addAttribute("lastYearVacation" + (i + 1), "");
			}
		}

		/*vacationNowYearList(今年)の件数分ループ処理*/
		for (int i = 0; i < vacationNowYearList.size(); i++) {

			/*vacationList(一昨年)の各要素を取得*/
			Map<String, Object> vacationNowYear = vacationNowYearList.get(i);

			/*holiday_dateで格納されている日付を取得*/
			Date sqlDateN = (Date) vacationNowYear.get("holiday_date");

			/*データが入っている場合の処理*/
			if (sqlDateN != null) {

				/*date型からLocalDate型に変換*/
				LocalDate dateN = sqlDateN.toLocalDate();

				/*yearVacation1,yearsVacation2...とフォーマットした日付をスコープに保存*/
				model.addAttribute("yearVacation" + (i + 1), dateN.format(formatter));

				/*データがない場合の処理*/
			} else {

				/*yearVacation1,yearVacation2...と空をスコープに保存*/
				model.addAttribute("yearVacation" + (i + 1), "");
			}
		}

		return "PPV001";
	}

	/**
	 * SMSEM002 社員マスタ登録画面遷移
	 * 社員マスタ登録画面に遷移する
	 */
	@GetMapping("regist")
	public String registEmployee(@ModelAttribute("employeeDate") EmployeeDate employeeDate,
			@ModelAttribute("selectDialogForm") SelectDialogForm selectDialogForm,
			Model model) {

		/*新規登録モードで遷移*/
		model.addAttribute("registMode", registMode);
		model.addAttribute("holidayCount", 0);

		//ダイアログ表示処理
		dialogProcess(selectDialogForm, model);

		return res2;
	}

	/**
	 * SESEM002 社員マスタ登録処理
	 * ★新規登録時に実行
	 * 以下の時はエラーを発出
	 * ・各項目未入力
	 * ・フォーマット違い
	 * ・社員番号、社員氏名重複
	 */
	@PostMapping("regist")
	public String registEmployee(@Validated @ModelAttribute("employeeDate") EmployeeDate employeeDate,
			BindingResult bindingResult,
			@RequestParam(name = "action", defaultValue = "") String action,
			RedirectAttributes attr, Model model) {

		/*エラーメッセージ用*/
		String attributeValue = null;
		String duplicationValue = null;

		/*EmployeeDateを格納する変数を定義*/
		String employeeId = employeeDate.getEmployeeId();
		String employeeName = employeeDate.getEmployeeName();
		String clientId = employeeDate.getClientId();
		String clientName = employeeDate.getClientName();
		boolean hourlyWage = employeeDate.getHourlyWage();
		String paidHolidayStd = employeeDate.getPaidHolidayStd();
		Integer remaindThisYear = employeeDate.getRemaindThisYear();
		Integer remaindLastYear = employeeDate.getRemaindLastYear();
		Integer seqId = employeeDate.getSeqId();
		String createdUser = employeeDate.getCreatedUser();
		String updatedUser = employeeDate.getUpdatedUser();

		/*スコープに保存*/
		model.addAttribute("employeeId", employeeId);
		model.addAttribute("employeeName", employeeName);
		model.addAttribute("clientId", clientId);
		model.addAttribute("clientName", clientName);
		model.addAttribute("hourlyWage", hourlyWage);
		model.addAttribute("paidHolidayStd", paidHolidayStd);
		model.addAttribute("remaindThisYear", remaindThisYear);
		model.addAttribute("remaindLastYear", remaindLastYear);
		model.addAttribute("holidayCount", 0);

		/*フォーマットチェック、有給基準日が空でない場合*/
		if (!paidHolidayStd.equals("")) {
			try {
				/* SimpleDateFormat を準備 */
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				/* 厳密な検証を行う*/
				dateFormat.setLenient(false);

				/* 文字列を Date オブジェクトに変換 */
				java.util.Date parsedDate = dateFormat.parse(paidHolidayStd);

				/* 有給休暇日付が指定されたフォーマットでない場合(ParseExceptionが発生しなかった時用) */
				if (!paidHolidayStd.equals(dateFormat.format(parsedDate))) {
					attributeValue = "有給基準日(2023-11-23など)で入力してください。";
					model.addAttribute("dResultPaidHolidayStd", attributeValue);
				}
			} catch (ParseException e) {
				/*ParseException(Dateへ変換時yyyy-MM-dd出なかったら発生) が発生した場合の処理*/
				attributeValue = "有給基準日(2023-11-23など)で入力してください。";
				model.addAttribute("dResultPaidHolidayStd", attributeValue);
			}
		}

		/*バリデーションチェック*/
		if (bindingResult.hasErrors() || attributeValue != null) {
			/*新規追加モード、更新モード判定用、スコープに保存*/
			model.addAttribute("registMode", registMode);
			model.addAttribute("regist", "regist");

			return res2;
		}

		/*社員番号、社員氏名に重複がないかチェックするSQLを実行*/
		attributeValue = employeeService.registJudge(employeeId, employeeName, clientId, hourlyWage, paidHolidayStd,
				createdUser, updatedUser);

		/*重複があれば各項目ごとにエラーメッセージを作成、スコープに保存*/
		if (attributeValue.equals("社員番号と社員氏名に入力した入力内容は社員マスタにすでに存在しています。")) {
			duplicationValue = "社員番号と社員指名に入力した" + employeeId + "と" + employeeName + "は社員マスタにすでに存在しています。";
			model.addAttribute("duplicationEmpIdName", duplicationValue);
			duplicationValue = "社員氏名に入力した" + employeeName + "は社員マスタにすでに存在しています。";
			model.addAttribute("duplicationEmployeeName", duplicationValue);
		} else if (attributeValue.equals("社員番号に入力した入力内容は社員マスタにすでに存在しています。")) {
			duplicationValue = "社員番号に入力した" + employeeId + "は社員マスタにすでに存在しています。";
			model.addAttribute("duplicationEmployeeId", duplicationValue);
		} else if (attributeValue.equals("社員氏名に入力した入力内容は社員マスタにすでに存在しています。")) {
			duplicationValue = "社員氏名に入力した" + employeeName + "は社員マスタにすでに存在しています。";
			model.addAttribute("duplicationEmployeeName", duplicationValue);
		}

		/*重複のエラーメッセージがあれば入力内容をスコープに保存してリダイレクト*/
		if (duplicationValue != null) {
			/*新規追加モード、スコープに保存*/
			model.addAttribute("registMode", registMode);
			model.addAttribute("regist", "regist");

			return res2;
		}

		/*有給休暇マスタの新規登録のSQLを実行*/
		employeeService.registPaidVacation(seqId, employeeId, paidHolidayStd, remaindThisYear, remaindLastYear,
				createdUser, updatedUser);

		/*送信時(登録ボタンまたは登録して次へ)のボタンを判定して遷移先指定*/
		if ("regist".equals(action)) {
			return "redirect:/employee/list";
		} else {
			/*登録して次へボタンの場合*/
			attr.addFlashAttribute("registNext", "registNext");
			return "redirect:/employee/regist";
		}

	}

	/**
	 * SESEM002 社員マスタ削除処理
	 * ★編集画面時に実行
	 */
	@PostMapping("delete")
	public String deletePostEmployee(@ModelAttribute("employeeDate") EmployeeDate employeeDate,
			@RequestParam(name = "employeeId", defaultValue = "") String[] empId, RedirectAttributes attr,
			Model model) {

		/*排他チェック用のログインId*/
		String loginUserId = "nexus001";

		/*EmployeeDateを格納*/
		String updatedUser = employeeDate.getUpdatedUser();

		/*排他チェック*/
		for (String dEmpId : empId) {

			/*排他チェック処理削除チェック*/
			if (exclusiveCheck.ExclusiveCheckDalete(dEmpId, tableNumber)) {

				String result = "該当データはすでに削除されています。";
				attr.addFlashAttribute("result", result);

				return "redirect:/employee/list";
			}

			/*排他チェック（編集中）*/
			if (exclusiveCheck.ExclusiveCheckEdited(dEmpId, loginUserId, editTableName)) {
				String result = "該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。";
				attr.addFlashAttribute("result", result);

				return "redirect:/employee/list";
			}

			/*排他チェック（編集中ロック解除）*/
			exclusiveCheck.ExclusiveLockDalete(dEmpId, loginUserId, tableNumber);

			/*排他チェックに問題がなければ社員マスタと有給休暇マスタの削除SQLを実行*/
			int result = employeeService.deleteEmployee(empId, updatedUser);
			employeeService.deletePaidVacation(empId, updatedUser);

			attr.addFlashAttribute("result", result + "件削除しました。");

		}

		return "redirect:/employee/list";
	}

	/**
	 * SMSEM002 社員マスタ編集画面遷移
	 * 社員マスタ編集画面に遷移する
	 */
	@GetMapping("edit")
	public String editGetEmployee(@ModelAttribute("employeeDate") EmployeeDate employeeDate,
			@RequestParam(name = "employeeId", defaultValue = "") String empId,
			@ModelAttribute("selectDialogForm") SelectDialogForm selectDialogForm,
			Model model, RedirectAttributes attr) {

		/*排他チェック用のログインId*/
		String loginUserId = "nexus001";

		/*排他チェック処理削除チェック*/

		/*排他チェック（削除）*/
		if (exclusiveCheck.ExclusiveCheckDalete(empId, tableNumber)) {

			String result = "該当データはすでに削除されています。";
			attr.addFlashAttribute("result", result);

			return "redirect:/employee/list";
		}

		/*排他チェック（編集中）*/
		if (exclusiveCheck.ExclusiveCheckEdited(empId, loginUserId, editTableName)) {
			String result = "該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。";
			attr.addFlashAttribute("result", result);

			return "redirect:/employee/list";
		}

		EmployeeDate empData;

		if (!employeeDate.getClientId().equals("")) {
			empData = employeeDate;
		} else {
			/*クリックされた社員番号で該当の社員情報の検索をするSQL実行*/
			empData = employeeService.searchEmployeeName(empId);

			//			/*empListの中身をスコープに保存*/
			//			model.addAttribute("employeeId", empList.get("employee_id"));
			//			model.addAttribute("employeeName", empList.get("employee_name"));
			//			model.addAttribute("clientId", empList.get("client_id"));
			//			model.addAttribute("clientName", empList.get("client_name"));
			//			model.addAttribute("hourlyWage", empList.get("hourly_wage"));
			//			model.addAttribute("paidHolidayStd", empList.get("paid_holiday_std"));
			//			model.addAttribute("remaindThisYear", empList.get("remaind_this_year"));
			//			model.addAttribute("remaindLastYear", empList.get("remaind_last_year"));
			//			model.addAttribute("holidayCount", empList.get("holiday_count"));
		}
		model.addAttribute("employeeDate", empData);

		/*有給残日数(当年度分)を取得　Object→String→Integerに変換*/
		Integer remaindThisYear = Integer.parseInt(empData.getRemaindThisYear().toString());

		/*有給残日数(前年度分)を取得　Object→String→Integerに変換*/
		Integer remaindLastYear = Integer.parseInt(empData.getRemaindLastYear().toString());

		//有休基準日を取得
		LocalDate paidHolidayStd = LocalDate.parse(empData.getPaidHolidayStd());

		//ステータスを取得
		String status = getStatus(remaindThisYear, remaindLastYear, paidHolidayStd);

		//保存
		model.addAttribute("status", status);

		//ダイアログ表示処理
		dialogProcess(selectDialogForm, model);
		model.addAttribute("edit", updateMode);

		return res2;
	}

	/**
	 * SESEM002 社員マスタ編集処理
	 * ★編集時に実行
	 * 以下の時はエラーを発出
	 * ・各項目未入力
	 * ・フォーマット違い
	 */
	@PostMapping("edit")
	public String editPostEmployee(@Validated @ModelAttribute("employeeDate") EmployeeDate employeeDate,
			BindingResult bindingResult,
			@RequestParam(name = "employeeId", defaultValue = "") String[] empId,
			@RequestParam(name = "action", defaultValue = "") String action, RedirectAttributes attr, Model model) {

		System.out.println(empId);

		/*排他チェック用のログインId*/
		String loginUserId = "nexus001";

		/*エラーメッセージ用*/
		String attributeValue = null;

		/*EmployeeDateを格納する変数を定義*/
		String employeeId = employeeDate.getEmployeeId();
		String employeeName = employeeDate.getEmployeeName();
		String clientId = employeeDate.getClientId();
		String clientName = employeeDate.getClientName();
		boolean hourlyWage = employeeDate.getHourlyWage();
		String paidHolidayStd = employeeDate.getPaidHolidayStd();
		Integer remaindThisYear = employeeDate.getRemaindThisYear();
		Integer remaindLastYear = employeeDate.getRemaindLastYear();
		String updatedUser = employeeDate.getUpdatedUser();
		Integer holidayCount = employeeDate.getHolidayCount();

		/*排他チェック処理削除チェック*/
		for (String dEmpId : empId) {

			/*排他チェック（削除）*/
			if (exclusiveCheck.ExclusiveCheckDalete(dEmpId, tableNumber)) {

				String result = "該当データはすでに削除されています。";
				attr.addFlashAttribute("result", result);

				return "redirect:/employee/list";
			}

			/*排他チェック（編集）*/
			if (exclusiveCheck.ExclusiveCheckEdited(dEmpId, loginUserId, editTableName)) {
				String result = "該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。";
				attr.addFlashAttribute("result", result);

				return "redirect:/employee/list";
			}

			/*排他チェック（編集中ロック解除）*/
			exclusiveCheck.ExclusiveLockDalete(dEmpId, loginUserId, tableNumber);
		}

		/*変数をスコープに保存*/
		model.addAttribute("employeeId", employeeId);
		model.addAttribute("employeeName", employeeName);
		model.addAttribute("clientId", clientId);
		model.addAttribute("clientName", clientName);
		model.addAttribute("hourlyWage", hourlyWage);
		model.addAttribute("paidHolidayStd", paidHolidayStd);
		model.addAttribute("remaindThisYear", remaindThisYear);
		model.addAttribute("remaindLastYear", remaindLastYear);
		model.addAttribute("holidayCount", holidayCount);

		/*フォーマットチェック、有給基準日が空でない場合*/
		if (!paidHolidayStd.equals("")) {
			try {
				/* SimpleDateFormat を準備 */
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				/* 厳密な検証を行う*/
				dateFormat.setLenient(false);

				/* 文字列を Date オブジェクトに変換 */
				java.util.Date parsedDate = dateFormat.parse(paidHolidayStd);

				/* 有給休暇日付が指定されたフォーマットでない場合(ParseExceptionが発生しなかった時用) */
				if (!paidHolidayStd.equals(dateFormat.format(parsedDate))) {
					attributeValue = "有給基準日(2023-11-23など)で入力してください。";
					model.addAttribute("dResultPaidHolidayStd", attributeValue);
				}

			} catch (ParseException e) {
				/*ParseException(Dateへ変換時yyyy-MM-dd出なかったら発生) が発生した場合の処理*/
				attributeValue = "有給基準日(2023-11-23など)で入力してください。";
				model.addAttribute("dResultPaidHolidayStd", attributeValue);
			}
		}

		/*バリデーションチェック*/
		if (bindingResult.hasErrors() || attributeValue != null) {

			/*更新モード判定用、スコープに保存*/
			model.addAttribute("edit", updateMode);

			return res2;
		}

		/*社員マスタと有給休暇マスタの更新のSQLを実行*/
		employeeService.updateEmployee(employeeName, clientId, hourlyWage, paidHolidayStd, updatedUser, employeeId);
		employeeService.updatePaidVacation(remaindThisYear, remaindLastYear, updatedUser, employeeId);

		return "redirect:/employee/list";
	}

	/**
	 * SESEM002 社員マスタ検索処理
	 * ページネーションを実装
	 * ★検索時に実行
	 * 以下の時はエラーを発出
	 * ・検索結果が0件の時
	 */
	//	@PostMapping("/search")
	//	public String searchCalendar(@RequestParam(name = "page", defaultValue = "1") int page,
	//			@RequestParam(name = "noPaidHoliday", defaultValue = "false") boolean noPaidHoliday,
	//			@RequestParam(name = "notificationPaidHoliday", defaultValue = "false") boolean notificationPaidHoliday,
	//			@RequestParam(name = "attentionPaidHoliday", defaultValue = "false") boolean attentionPaidHoliday,
	//			@RequestParam(name = "warningPaidHoliday", defaultValue = "false") boolean warningPaidHoliday,
	//			@RequestParam(name = "searchClientButton", defaultValue = "") String searchClientButton, //顧客ダイアログで検索ボタン押したか
	//			@RequestParam(name = "searchClientId", defaultValue = "") String searchClientId, //顧客ダイアログに入力した顧客番号
	//			@RequestParam(name = "searchClientName", defaultValue = "") String searchClientName,
	//			RedirectAttributes attr, @ModelAttribute EmployeeDate employeedate,
	//			Model model) {
	//
	//		/*検索時入力項目を格納する変数を定義*/
	//		String employeeId = employeedate.getEmployeeId();
	//		String employeeName = employeedate.getEmployeeName();
	//		String clientId = employeedate.getClientId();
	//		String clientName = employeedate.getClientName();
	//
	//		//顧客選択用
	//		List<Map<String, Object>> clientList = employeeService.getClient(searchClientId, searchClientName);
	//		model.addAttribute("client_list", clientList);
	//		model.addAttribute("clientId", searchClientId);//検索欄に入力したid
	//		model.addAttribute("clientName", searchClientName);//検索欄に入力したname
	//		//顧客ダイアログの検索ボタン押されていたら顧客ダイアログを表示
	//		if (searchClientButton.equals("open")) {
	//			model.addAttribute("clientDialog", 1);//1:表示 0:非表示
	//		} else {
	//			model.addAttribute("clientDialog", 0);
	//		}
	//
	//		/**現在のページとページサイズを引数に設定
	//		* 1ページずれるためpageを-1
	//		*/
	//		List<Map<String, Object>> list = employeeService.searchEmployeeActive(employeeId, employeeName, clientId,
	//				clientName, page - 1, pageSize);
	//
	//		int totalClients = 0;
	//
	//		/*空だったらエラーメッセージとセッションスコープに””を入れる*/
	//		if (list.isEmpty()) {
	//			model.addAttribute("searchError", "社員一覧の検索結果は0件です。条件を変更し、再度検索してください。");
	//			session.setAttribute("employeeId", "");
	//			session.setAttribute("emoloyeeName", "");
	//			session.setAttribute("clientId", "");
	//			session.setAttribute("clientName", "");
	//		}
	//
	//		else {
	//			totalClients = employeeService.searchEmpCount(employeeId, employeeName, clientId,
	//					clientName);
	//			if (totalClients == 0) {
	//				totalClients = employeeService.countEmployee();
	//			}
	//			model.addAttribute("list", list);
	//			session.setAttribute("employeeId", employeeId);
	//			session.setAttribute("employeeName", employeeName);
	//			session.setAttribute("clientId", clientId);
	//			session.setAttribute("clientName", clientName);
	//		}
	//
	//		/*ここで総データ数を取得するロジックを実装し、総ページ数を計算*/
	//		int totalPages = (int) Math.ceil((double) totalClients / pageSize);
	//		/*1を加えて0ベースを1ベースに変更*/
	//		int correctedPageNumber = page + 1;
	//
	//		List<Map<String, Object>> elementsToRemove = new ArrayList<>();
	//
	//		/*ステータスの文言を条件によって変更するためのfor文*/
	//		for (Map<String, Object> status : list) {
	//
	//			/*今年の休暇数を取得　Object→String→Integerに変換*/
	//			Object oHolidayCount = status.get("holiday_count");
	//			String sHolidayCount = oHolidayCount.toString();
	//			Integer holidayCount = new Integer(sHolidayCount).intValue();
	//
	//			/*有給残日数(当年度分)を取得　Object→String→Integerに変換*/
	//			Object oRemaindThisYear = status.get("remaind_this_year");
	//			String sRemaindThisYear = oRemaindThisYear.toString();
	//			Integer remaindThisYear = new Integer(sRemaindThisYear).intValue();
	//
	//			/*有給残日数(前年度分)を取得　Object→String→Integerに変換*/
	//			Object oRemaindLastYear = status.get("remaind_last_year");
	//			String sRemaindLastYear = oRemaindLastYear.toString();
	//			Integer remaindLastYear = new Integer(sRemaindLastYear).intValue();
	//
	//			/*ステータスの文言を初期化*/
	//			String attributeValue = "";
	//			String paidAttributeValue = "";
	//
	//			//条件が違うから変更する 未完成
	//			/*有給取得が5日未満の場合、日数に応じてステータスの文言を変更*/
	//			if (remaindThisYear + remaindLastYear - holidayCount <= 0) {
	//				attributeValue = "有給残日数なし";
	//			} else if (remaindThisYear != 0 && holidayCount == 0 || holidayCount == 1) {
	//				attributeValue = "有給取得日数不足(警告)";
	//			} else if (remaindThisYear != 0 && holidayCount == 2 || holidayCount == 3) {
	//				attributeValue = "有給取得日数不足(注意)";
	//			} else if (remaindThisYear != 0 && holidayCount == 4) {
	//				attributeValue = "有給取得日数不足(通知)";
	//			}
	//
	//			/*ステータスの文言に置き換え*/
	//			status.put("application_class", attributeValue);
	//
	//			//sqlのpageSizeを変えればいけるか？？未完成
	//			if (noPaidHoliday == true) {
	//				if (paidAttributeValue == "有給残日数なし") {
	//					elementsToRemove.add(status);
	//				}
	//			}
	//			if (notificationPaidHoliday == true) {
	//				if (paidAttributeValue == "有給取得日数不足(通知)") {
	//					elementsToRemove.add(status);
	//				}
	//			}
	//			if (attentionPaidHoliday == true) {
	//				if (paidAttributeValue == "有給取得日数不足(注意)") {
	//					elementsToRemove.add(status);
	//				}
	//			}
	//			if (warningPaidHoliday == true) {
	//				if (paidAttributeValue == "有給取得日数不足(警告)") {
	//					elementsToRemove.add(status);
	//				}
	//			}
	//		}
	//
	//		//未完成
	//		if (!(elementsToRemove.isEmpty())) {
	//			list = elementsToRemove;
	//			model.addAttribute("list", list);
	//			totalClients = list.size();
	//			totalPages = (int) Math.ceil((double) totalClients / pageSize);
	//		}
	//
	//		/*スコープに保存*/
	//		model.addAttribute("currentPage", correctedPageNumber);
	//		model.addAttribute("totalPages", totalPages);
	//
	//		/*ページネーションのHTMLをsearchで判定用にスコープに保存*/
	//		model.addAttribute("searchPage", true);
	//
	//		return res1;
	//	}
	//
	//	/**
	//	 * SESEM002 社員マスタ検索処理
	//	 * ページネーションを実装
	//	 * ★検索時に実行
	//	 * 以下の時はエラーを発出
	//	 * ・検索結果が0件の時
	//	 */
	//	@GetMapping("/search")
	//	public String searchCalendar(@RequestParam(name = "page", defaultValue = "1") int page,
	//			RedirectAttributes attr,
	//			Model model) {
	//
	//		/*検索項目を定義(””を代入しないとエラーになるため)*/
	//		String employeeId = "";
	//		String employeeName = "";
	//		String clientId = "";
	//		String clientName = "";
	//
	//		//顧客選択用
	//		List<Map<String, Object>> clientList = employeeService.getClient();
	//		model.addAttribute("client_list", clientList);
	//		model.addAttribute("clientDialog", 0);
	//
	//		try {
	//			/*検索項目のセッションスコープ呼び出し*/
	//			employeeId = session.getAttribute("employeeId").toString();
	//			employeeName = session.getAttribute("employeeName").toString();
	//			clientId = session.getAttribute("clientId").toString();
	//			clientName = session.getAttribute("clientName").toString();
	//
	//		} catch (NullPointerException e) {
	//
	//		}
	//
	//		/**現在のページとページサイズを引数に設定
	//		* 1ページずれるためpageを-1
	//		*/
	//		List<Map<String, Object>> list = employeeService.searchEmployeeActive(employeeId, employeeName, clientId,
	//				clientName, page - 1, pageSize);
	//
	//		int totalClients = 0;
	//		/*空だったらエラーメッセージとセッションスコープに””を入れる*/
	//		if (list.isEmpty()) {
	//			model.addAttribute("searchError", "社員一覧の検索結果は0件です。条件を変更し、再度検索してください。");
	//			session.setAttribute("employeeId", "");
	//			session.setAttribute("employeeName", "");
	//			session.setAttribute("clientId", "");
	//			session.setAttribute("clientName", "");
	//
	//			/*空じゃなかったらリクエストスコープにリストを、セッションスコープに検索条件を保存*/
	//		} else {
	//			model.addAttribute("list", list);
	//			session.setAttribute("employeeId", employeeId);
	//			session.setAttribute("employeeName", employeeName);
	//			session.setAttribute("clientId", clientId);
	//			session.setAttribute("clientName", clientName);
	//			System.out.println(clientId);
	//
	//			/*検索条件が空欄だったらページネーションのためのカウントを実施*/
	//			if (employeeId.isEmpty() && employeeName.isEmpty() && clientId.isEmpty() && clientName.isEmpty()) {
	//				/*ここで総データ数を取得するロジックを実装し、総ページ数を計算*/
	//				totalClients = employeeService.countEmployee();
	//				/*検索条件が空欄でない場合検索結果のページネーションのためのカウントを実施*/
	//			} else {
	//				/*ここで総データ数を取得するロジックを実装し、総ページ数を計算*/
	//				totalClients = employeeService.searchEmpCount(employeeId, employeeName, clientId,
	//						clientName);
	//
	//			}
	//		}
	//
	//		int totalPages = (int) Math.ceil((double) totalClients / pageSize);
	//		/*1を加えて0ベースを1ベースに変更*/
	//		int correctedPageNumber = page + 1;
	//
	//		/*ステータスの文言を条件によって変更するためのfor文*/
	//		for (Map<String, Object> status : list) {
	//			/*今年の休暇数を取得　Object→String→Integerに変換*/
	//			Object oHolidayCount = status.get("holiday_count");
	//			String sHolidayCount = oHolidayCount.toString();
	//			Integer holidayCount = new Integer(sHolidayCount).intValue();
	//
	//			/*有給残日数(当年度分)を取得　Object→String→Integerに変換*/
	//			Object oRemaindThisYear = status.get("remaind_this_year");
	//			String sRemaindThisYear = oRemaindThisYear.toString();
	//			Integer remaindThisYear = new Integer(sRemaindThisYear).intValue();
	//
	//			/*有給残日数(前年度分)を取得　Object→String→Integerに変換*/
	//			Object oRemaindLastYear = status.get("remaind_last_year");
	//			String sRemaindLastYear = oRemaindLastYear.toString();
	//			Integer remaindLastYear = new Integer(sRemaindLastYear).intValue();
	//
	//			/*ステータスの文言を初期化*/
	//			String attributeValue = "";
	//
	//			/*有給取得が5日未満の場合、日数に応じてステータスの文言を変更*/
	//			if (remaindThisYear + remaindLastYear - holidayCount <= 0) {
	//				attributeValue = "有給残日数なし";
	//			} else if (remaindThisYear != 0 && holidayCount == 0 || holidayCount == 1) {
	//				attributeValue = "有給取得日数不足(警告)";
	//			} else if (remaindThisYear != 0 && holidayCount == 2 || holidayCount == 3) {
	//				attributeValue = "有給取得日数不足(注意)";
	//			} else if (remaindThisYear != 0 && holidayCount == 4) {
	//				attributeValue = "有給取得日数不足(通知)";
	//			}
	//
	//			/*ステータスの文言に置き換え*/
	//			status.put("application_class", attributeValue);
	//		}
	//
	//		/*スコープに保存*/
	//		model.addAttribute("currentPage", correctedPageNumber);
	//		model.addAttribute("totalPages", totalPages);
	//
	//		/*ページネーションのHTMLをsearchで判定用にスコープに保存*/
	//		model.addAttribute("searchPage", true);
	//
	//		return res1;
	//	}

	//キャンセルボタン押下
	@PostMapping("/cancel")
	public String userCancel(@RequestParam(name = "employeeId", defaultValue = "") String employeeId) {
		String loginUserId = "nexus001";
		exclusiveCheck.ExclusiveLockDalete(employeeId, loginUserId, tableNumber);

		return "redirect:/employee/list";
	}

	/**
	 * SESEM003 有給休暇詳細画面遷移
	 * 遷移のみ
	 */
	@GetMapping("paidDetails")
	public String paidDetails() {

		return "SMSEM003";
	}

	/**
	 * ダイアログに必要な処理をまとめたメソッド
	 * @param selectDialogForm
	 * @param model
	 */
	private void dialogProcess(SelectDialogForm selectDialogForm, Model model) {

		//顧客選択用
		List<Map<String, Object>> clientList = employeeService.getClient(selectDialogForm.getDialogClientId(),
				selectDialogForm.getDialogClientName());
		model.addAttribute("client_list", clientList);
		//顧客ダイアログの検索ボタン押されていたら顧客ダイアログを表示
		if (selectDialogForm.getDialogSearchClient().equals("open")) {
			model.addAttribute("clientDialog", "open");
		} else {
			model.addAttribute("clientDialog", "close");
		}

		model.addAttribute("selectDialogForm", selectDialogForm);
	}

	/**
	 * ステータスを返すメソッド
	 * @param remaindThisYear
	 * @param remaindLastYear
	 * @param paidHolidayStd
	 * @return
	 */
	private String getStatus(Integer remaindThisYear, Integer remaindLastYear, LocalDate paidHolidayStd) {

		/*ステータスの文言を初期化*/
		String attributeValue = "";

		//今日の日付
		LocalDate today = LocalDate.now();

		//今日の日付が基準日の前かどうか
		if (paidHolidayStd.isAfter(today)) {

			//有休残日数が0か
			if (remaindThisYear == 0 && remaindLastYear == 0) {

				attributeValue = "有給残日数なし";
			} else {

				// 月日の差を計算
				long daysDifference = Math.abs(ChronoUnit.DAYS.between(paidHolidayStd, today));
				long monthsDifference = Math.abs(ChronoUnit.MONTHS.between(paidHolidayStd, today));

				// 条件に応じて出力
				if (daysDifference <= 7) {
					attributeValue = "有給取得日数不足(警告)";
				} else if (daysDifference <= 31) { // 1ヶ月の場合
					attributeValue = "有給取得日数不足(注意)";
				} else if (monthsDifference <= 6) { // 6ヶ月の場合
					attributeValue = "有給取得日数不足(通知)";
				}
			}
		}

		return attributeValue;
	}

}