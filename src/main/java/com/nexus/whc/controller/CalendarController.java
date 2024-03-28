package com.nexus.whc.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nexus.whc.models.CalendarData;
import com.nexus.whc.models.CalendarDetail;
import com.nexus.whc.models.CalendarDetailManager;
import com.nexus.whc.models.CalendarSearchForm;
import com.nexus.whc.models.SelectDialogForm;
import com.nexus.whc.services.CalendarService;
import com.nexus.whc.services.ExclusiveCheckService;

/*
 * CalendarController.java
 * 
 * CalendarControllerクラス
 */

/*
 * Controllerクラス
 */
@Controller
@RequestMapping("/calendar")
public class CalendarController {

	/* CalendarServiceクラス */
	@Autowired
	CalendarService calendarService;

	@Autowired
	HttpSession session;

	@Autowired
	ExclusiveCheckService exclusiveCheck;

	// 排他チェックで使用するテーブル判別用番号
	final int tableNumber = 4;
	// ユーザーID
	final String userId = "nexus001";

	@GetMapping("/list")
	public String calendarList(
			@ModelAttribute("calendarSearchForm") CalendarSearchForm searchForm,
			@ModelAttribute("displayCalendarSearchForm") CalendarSearchForm displaySearchForm,
			@ModelAttribute("selectDialogForm") SelectDialogForm selectDialogForm,
			@RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
			Model model) {

		int pageSize = 20;

		List<Map<String, Object>> calendarList = calendarService.searchCalendar(pageNumber, pageSize, searchForm);
		//取得数が0ならエラーメッセージを保存
		if (calendarList.size() == 0) {
			String result = "";

			//日付TOがFROM以前の日付でないかチェック
			if (!searchForm.getStartYearMonth().isEmpty() && !searchForm.getEndYearMonth().isEmpty()) {
				try {
					SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM");
					Date from = sdFormat.parse(searchForm.getStartYearMonth());
					Date to = sdFormat.parse(searchForm.getEndYearMonth());

					if (from.after(to)) {
						result = "登録年(From)と登録年(To)の入力内容の前後関係が正しくありません";
					}
				} catch (ParseException e) {
					result = "登録年(From)と登録年(To)の入力内容の前後関係が正しくありません。";
				}
			}
			if (result.equals("")) {
				List<Map<String, Object>> listAll = calendarService.searchAll();
				if (!listAll.isEmpty()) {
					result = "カレンダー一覧の検索結果は0件です。条件を変更し、再度検索してください。";
				}
			}
			model.addAttribute("result", result);

		} else { //エラーではない時
			// 一覧表示用リストの全要素分繰り返す
			for (Map<String, Object> list : calendarList) {

				// 年間休日数を取得
				int holidaysCount = totalAnnualHolidays(list);

				// calendarListの休日数カラムを上書き
				list.put("monthly_holidays", holidaysCount);

				//顧客番号を3桁に変換
				String formattedClientId = String.format("%03d", (Integer) list.get("client_id"));
				list.put("client_id", formattedClientId);

				//社員番号を4桁に変換
				String formattedEmployeeId = "";
				if (list.get("employee_id") != null) {
					formattedEmployeeId = String.format("%04d", (Integer) list.get("employee_id"));
				}
				list.put("employee_id", formattedEmployeeId);
			}
			model.addAttribute("calendarList", calendarList);
		}

		if (displaySearchForm.isInitialized()) {
			model.addAttribute("calendarSearchForm", searchForm);
		} else {
			model.addAttribute("calendarSearchForm", displaySearchForm);
		}

		//ダイアログ表示処理
		dialogProcess(selectDialogForm, model);
		//ページネーションとダイアログ検索ボタン押下時用にセッションスコープにも保存
		session.setAttribute("calendarSearchForm", searchForm);

		model.addAttribute("mode", "list");

		//ページネーション用
		//アクティブなデータの総数
		int totalCount = calendarService.countSearchCalendar(searchForm);

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

		return "SMSCD001";
	}

	/**
	 * カレンダーマスタ一覧
	 * 検索ボタンが押された時に実行
	 * @param searchForm
	 * @param selectDialogForm
	 * @param pageNumber
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/search")
	public String searchCalendarPost(@ModelAttribute("calendarSearchForm") CalendarSearchForm searchForm,
			@ModelAttribute("displayCalendarSearchForm") CalendarSearchForm displaySearchForm,
			@ModelAttribute("selectDialogForm") SelectDialogForm selectDialogForm,
			@RequestParam(name = "pageNumber", defaultValue = "1") String pageNumber,
			RedirectAttributes redirectAttributes) {

		redirectAttributes.addFlashAttribute("calendarSearchForm", searchForm);
		redirectAttributes.addFlashAttribute("displayCalendarSearchForm", displaySearchForm);
		redirectAttributes.addFlashAttribute("selectDialogForm", selectDialogForm);

		return "redirect:/calendar/list?pageNumber=" + pageNumber;
	}

	/**
	 * カレンダーマスタ一覧
	 * ページネーションが押された時に実行
	 * @param pageNumber
	 * @param redirectAttributes
	 * @return
	 */
	@GetMapping("/pageList")
	public String pageListCalendarGet(@RequestParam(name = "pageNumber", defaultValue = "1") String pageNumber,
			RedirectAttributes redirectAttributes) {

		CalendarSearchForm searchForm = (CalendarSearchForm) session.getAttribute("calendarSearchForm");

		redirectAttributes.addFlashAttribute("calendarSearchForm", searchForm);

		return "redirect:/calendar/list?pageNumber=" + pageNumber;
	}

	/**
	 * SMSCD001 カレンダーマスタ削除処理
	 * ★選択行削除ボタン押下時に実行
	 * 以下の時はエラーを発出
	 * ・削除対象未選択
	 * ・
	 */
	@PostMapping("/delete")
	public String deleteCalendarPost(@RequestParam(name = "seqId", required = false) String[] selectCheck,
			RedirectAttributes attr) {

		/* 2．削除件数が１行以上か判別 */
		if (selectCheck != null) {

			int count = 0;

			for (String seqId : selectCheck) {
				/* 3.排他チェック（削除） */
				if (exclusiveCheck.ExclusiveCheckDalete(seqId, tableNumber)) {
					String result = "該当データはすでに削除されています。";
					attr.addFlashAttribute("result", result);

					return "redirect:/calendar/list";

				}
				/* 4.排他チェック（編集中） */
				if (exclusiveCheck.ExclusiveCheckEdited(seqId, userId, tableNumber)) {
					String result = "該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。";
					attr.addFlashAttribute("result", result);

					/* 6.排他チェック（編集中後） */
					exclusiveCheck.ExclusiveLockDalete(seqId, userId, tableNumber);

					return "redirect:/calendar/list";
				}

				/* 5.選択したカレンダー情報削除 */
				// 論理削除実行
				count += calendarService.deleteCalendar(seqId, userId);
				calendarService.deleteCalendarDetail(seqId, userId);

				/* 6.排他チェック（編集中後） */
				exclusiveCheck.ExclusiveLockDalete(seqId, userId, tableNumber);
			}

			attr.addFlashAttribute("result", count + "件削除しました。");
		} else {
			/* 8.削除件数が0件の場合 */
			attr.addFlashAttribute("result", "対象が選択されていません。対象を選択してください。");
		}

		return "redirect:/calendar/list";
	}

	/**
	 * 新規登録①表示
	 * @param model modelデータ
	 * @return SMSCD002画面
	 */
	@GetMapping("/create")
	public String createCalendarGet(@ModelAttribute("calendarData") CalendarData calendarData,
			@ModelAttribute("selectDialogForm") SelectDialogForm selectDialogForm,
			Model model) {

		String mode = "create";

		//選択ダイアログ表示処理
		dialogProcess(selectDialogForm, model);

		model.addAttribute("calendarData", calendarData);
		model.addAttribute("mode", mode);

		return "SMSCD002";
	}

	/**
	 * 新規登録①から➁に遷移する
	 * @param calendarData
	 * @param attr
	 * @param model
	 * @return
	 */
	@PostMapping("/create")
	public String createCalendarGet(@ModelAttribute("calendarData") CalendarData calendarData,
			RedirectAttributes attr,
			Model model) {

		// エラーメッセージ格納用List
		List<String> result = new ArrayList<>();

		// 未入力チェック(必須チェック)
		if (calendarData.getClientName().isEmpty()) {
			result.add("顧客名は必ず入力してください。");
		}

		if (calendarData.getYear().isEmpty()) {
			result.add("登録年は必ず入力してください。");
		} else if (!calendarData.getYear().matches("^\\d{4}$")) { // 4桁の数字かどうかをチェック
			result.add("登録年はyyyyで入力してください。");
		}
		if (calendarData.getMonth().isEmpty()) {
			result.add("登録月は必ず入力してください。");
		} else if (!calendarData.getMonth().matches("^(0[1-9]|1[0-2])$")) { // 01から12までの2桁の数字かどうかをチェック
			result.add("登録月はmmで入力してください。");
		}

		//エラー用文字列がnullだから未入力ではない
		if (result.isEmpty()) {
			//重複チェック
			if (calendarService.duplicateCheck(calendarData.getClientName(), calendarData.getEmployeeId(),
					calendarData.getYearMonth()) != -1) {

				if (calendarData.getEmployeeId().isEmpty()) {
					result.add("顧客名、年月に入力した「"
							+ calendarData.getClientName() + "」「"
							+ calendarData.getYear() + "/" + calendarData.getMonth()
							+ "」はカレンダーマスタにすでに存在しています。");
				} else {
					result.add("顧客名、社員名、社員番号、年月に入力した「"
							+ calendarData.getClientName() + "」「"
							+ calendarData.getEmployeeId() + "」「"
							+ calendarData.getEmployeeName() + "」「"
							+ calendarData.getYear() + "/" + calendarData.getMonth()
							+ "」はカレンダーマスタにすでに存在しています。");
				}
			}
		}

		attr.addFlashAttribute("calendarData", calendarData);

		// エラー用文字列が上書きされてたらエラー判定、新規登録にリダイレクト
		if (!result.isEmpty()) {

			attr.addFlashAttribute("result", result);
			return "redirect:/calendar/create";
		}

		//エラーが無ければ次の画面に遷移
		return "redirect:/calendar/regist";
	}

	/**
	 * 新規登録➁を表示
	 * @param calendarData
	 * @param selectDialogForm
	 * @param attr
	 * @param model
	 * @return
	 */
	@GetMapping("/regist")
	public String registCalendarGet(
			@ModelAttribute("calendarData") CalendarData calendarData,
			@ModelAttribute("calendarDatail") CalendarDetailManager calendarDetail,
			@ModelAttribute("selectDialogForm") SelectDialogForm selectDialogForm,
			@ModelAttribute("mode") String cMode,
			RedirectAttributes attr,
			Model model) {

		//選択行コピーからの遷移のとき、modeを"copyにする。"
		String mode = "";
		if (cMode.isEmpty()) {
			mode = "regist";
		} else {
			mode = cMode;
		}

		CalendarDetailManager details;

		if (calendarDetail.getAll().isEmpty()) {
			details = getDataAndHoliday(calendarData.getYearMonth());
		} else {
			//スコープから取得できた場合(ダイアログの検索ボタンが押されていた時または選択行コピー)
			details = calendarDetail;
		}

		//カレンダーに表示できる形に整形
		List<List<CalendarDetail>> formatLists = formatCalendarList(details, calendarData.getYearMonth());

		//通年備考を取得して保存
		String comment = searchAllYearRoundComment(calendarData);
		calendarData.setAllYearRoundComment(comment);

		//スコープに保存
		model.addAttribute("calendarData", calendarData);
		model.addAttribute("calendarDatail", formatLists);

		model.addAttribute("mode", mode);

		//ダイアログ表示処理
		dialogProcess(selectDialogForm, model);

		return "SMSCD002";
	}

	/**
	 * 新規登録DB追加処理
	 * @param calendarData
	 * @param day
	 * @param holidayFlg
	 * @param comment
	 * @param redirectAttributes
	 * @param model
	 * @return
	 */
	@PostMapping("/regist")
	public String editCalendarPost(@ModelAttribute("calendarData") CalendarData calendarData,
			@RequestParam(name = "day", required = false) String[] day,
			@RequestParam(name = "holidayFlg", required = false) String[] holidayFlg,
			@RequestParam(name = "comment", required = false) String[] comment,
			@RequestParam(name = "mode", required = false) String mode,
			RedirectAttributes redirectAttributes,
			Model model) {

		CalendarDetailManager details = new CalendarDetailManager(day.length);

		details.setAndTransformHolidayFlg(holidayFlg);
		details.setAndTransformComment(comment, calendarData.getYearMonth());

		//重複チェック
		if (calendarService.duplicateCheck(calendarData.getClientName(), calendarData.getEmployeeId(),
				calendarData.getYearMonth()) != -1) {
			details.setDate(day, calendarData.getYearMonth());

			if (calendarData.getEmployeeId().isEmpty()) {
				redirectAttributes.addFlashAttribute("result", "顧客名、年月に入力した「"
						+ calendarData.getClientName() + "」「"
						+ calendarData.getYear() + "/" + calendarData.getMonth()
						+ "」はカレンダーマスタにすでに存在しています。");
			} else {
				redirectAttributes.addFlashAttribute("result", "顧客名、社員名、社員番号、年月に入力した「"
						+ calendarData.getClientName() + "」「"
						+ calendarData.getEmployeeId() + "」「"
						+ calendarData.getEmployeeName() + "」「"
						+ calendarData.getYear() + "/" + calendarData.getMonth()
						+ "」はカレンダーマスタにすでに存在しています。");
			}
			redirectAttributes.addFlashAttribute("calendarData", calendarData);
			redirectAttributes.addFlashAttribute("calendarDetail", details);
			redirectAttributes.addFlashAttribute("mode", mode);
			return "redirect:/calendar/regist";
		} else {
			details.setAndTransformDate(day, calendarData.getYearMonth());
		}

		// clientNameからidを取得
		int clientId = calendarService.searchClientId(calendarData.getClientName());

		// 最終日を取得
		LocalDate endDate = calendarData.getYearMonth().plusMonths(1).minusDays(1);
		// 月間所定日数
		int monthlyPrescribedDays = endDate.getDayOfMonth() - details.holidayCount();

		//通年備考を更新
		updateAllYearRoundComment(calendarData, calendarData.getYearMonth());

		// カレンダーテーブルに登録
		calendarService.createCalendar(clientId, calendarData.getEmployeeId(), calendarData.getYearMonth(),
				details.holidayCount(), monthlyPrescribedDays,
				calendarData.getAllYearRoundComment(), userId);

		// シーケンスidの最大を取得
		int seqId = calendarService.searchMaxSeqId();

		// カレンダー詳細テーブルに登録
		calendarService.createCalendarDetail(seqId, details.getDate(), details.isHolidayFlag(), details.getComment(),
				userId);

		// 閲覧に遷移 シーケンスid渡す
		return "redirect:/calendar/browse?seqId=" + seqId;

	}

	/**
	 * * SMSCD002 カレンダーマスタ暦通り登録 各項目check
	 * ★暦通り登録ボタン押下時に実行
	 * 以下の時はエラーを発出
	 * ・各項目未入力
	 */
	@PostMapping("/accordingCalendar")
	public String creatPerCalendarPost(@ModelAttribute("calendarData") CalendarData calendarData,
			RedirectAttributes attr,
			Model model) {

		// 未入力チェック(必須チェック)
		// エラーメッセージ格納用List
		List<String> result = new ArrayList<>();

		if (calendarData.getClientName().isEmpty()) {
			result.add("顧客名は必ず入力してください。");
		}
		if (calendarData.getStartYearMonth().isEmpty()) {
			result.add("登録年月(From)は必ず入力してください。");
		}
		if (calendarData.getEndYearMonth().isEmpty()) {
			result.add("登録年月(To)は必ず入力してください。");
		}

		//日付TOがFROM以前の日付でないかチェック
		if (!calendarData.getStartYearMonth().isEmpty() && !calendarData.getEndYearMonth().isEmpty()) {
			try {
				SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM");
				Date from = sdFormat.parse(calendarData.getStartYearMonth());
				Date to = sdFormat.parse(calendarData.getEndYearMonth());

				if (from.after(to)) {
					result.add("登録年(From)と登録年(To)の入力内容の前後関係が正しくありません。");
				}
			} catch (ParseException e) {
				result.add("登録年(From)と登録年(To)の入力内容の前後関係が正しくありません。");
			}
		}

		attr.addFlashAttribute("calendarData", calendarData);

		// エラー用文字列が上書きされてたらエラー判定、新規登録にリダイレクト
		if (!result.isEmpty()) {

			attr.addFlashAttribute("result", result);

			return "redirect:/calendar/create";
		}

		//String isDuplication = "";
		//String isExclusion = "";
		String accordingCalendarFlag = "";

		List<String> duplicationList = new ArrayList<>();
		List<String> exclusionList = new ArrayList<>();

		//重複チェック

		// 登録年月(from,to)をyyyy/mmからyyyy-mm-ddにフォーマット
		String inputFormatPattern = "yyyy/MM";
		String outputFormatPattern = "yyyy-MM";

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputFormatPattern);
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormatPattern);

		String formattedFrom = YearMonth.parse(calendarData.getStartYearMonth(), inputFormatter).atDay(1)
				.format(outputFormatter) + "-01";
		String formattedTo = YearMonth.parse(calendarData.getEndYearMonth(), inputFormatter).atEndOfMonth()
				.format(outputFormatter) + "-01";

		LocalDate startDate = LocalDate.parse(formattedFrom);
		LocalDate endDate = LocalDate.parse(formattedTo);

		for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusMonths(1)) {
			//重複チェック
			if (calendarService.duplicateCheck(calendarData.getClientName(), calendarData.getEmployeeId(),
					date) != -1) {

				// 年と月の情報を取得
				int year = date.getYear();
				int month = date.getMonthValue();

				// 年月を指定された形式で文字列として連結
				String formattedDate = String.format("%d/%02d", year, month);

				//リストに追加
				duplicationList.add(formattedDate);
				accordingCalendarFlag = "duplication";

				String seqId = calendarService.getSeqId(calendarData, date).toString();

				//排他チェック(編集中)
				if (exclusiveCheck.ExclusiveCheckEdited(seqId, userId, tableNumber)) {

					exclusionList.add(formattedDate);
					accordingCalendarFlag = "exclusion";
				}
				/* 排他チェック（編集済） */
				exclusiveCheck.ExclusiveLockDalete(seqId, userId, tableNumber);
			}
		}

		//編集中
		if (accordingCalendarFlag.equals("exclusion")) {
			attr.addFlashAttribute("exclusionList", exclusionList);
		}
		//重複している
		else if (accordingCalendarFlag.equals("duplication")) {
			attr.addFlashAttribute("duplicationList", duplicationList);
		}
		//それ以外
		else {
			accordingCalendarFlag = "regist";
		}

		attr.addFlashAttribute("accordingCalendarFlag", accordingCalendarFlag);

		return "redirect:/calendar/create";
	}

	/**
	 * 暦通り登録 DB登録処理
	 * @param calendarData
	 * @param attr
	 * @param model
	 * @return
	 */
	@PostMapping("/accordingCalendar/regist")
	public String creatPerCalendarPost2(@ModelAttribute("calendarData") CalendarData calendarData,
			RedirectAttributes attr,
			Model model) {

		// from,toをフォーマット
		String inputFormatPattern = "yyyy/MM";
		String outputFormatPattern = "yyyy-MM";

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputFormatPattern);
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormatPattern);

		String formattedFrom = YearMonth.parse(calendarData.getStartYearMonth(), inputFormatter).atDay(1)
				.format(outputFormatter) + "-01";
		String formattedTo = YearMonth.parse(calendarData.getEndYearMonth(), inputFormatter).atEndOfMonth()
				.format(outputFormatter) + "-01";

		LocalDate startDate = LocalDate.parse(formattedFrom);
		LocalDate endDate = LocalDate.parse(formattedTo);

		//Map<String, String> duplicationList = new HashMap<>();

		// 登録
		for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusMonths(1)) {

			// nameからidを取得
			int clientId = calendarService.searchClientId(calendarData.getClientName());

			// この月の最終日を取得
			LocalDate monthEndDate = date.plusMonths(1).minusDays(1);

			// 1ヵ月分のデータを取得
			CalendarDetailManager details = getDataAndHoliday(date);

			//日付を変換
			details.transformDate(date);

			// 休日数を取得
			int monthlyHoliday = details.holidayCount();

			// 月間所定日数
			int monthlyPrescribedDays = monthEndDate.getDayOfMonth() - monthlyHoliday;

			// 最終日を取得
			int lastDayOfMonth = date.lengthOfMonth();

			//重複してたらseqIdを取得
			Integer duplicationSeqId = calendarService.duplicateCheck(calendarData.getClientName(),
					calendarData.getEmployeeId(), date);

			//重複している場合 更新
			if (duplicationSeqId != -1) {

				//通年備考を更新
				updateAllYearRoundComment(calendarData, date);
				// 更新
				calendarService.updateCalendar(duplicationSeqId.toString(), calendarData.getEmployeeId(),
						details.holidayCount(),
						lastDayOfMonth - details.holidayCount(),
						"", userId);

				// 詳細更新
				calendarService.updateCalendarDetails(details.isHolidayFlag(), details.getComment(), userId,
						duplicationSeqId.toString(),
						details.getDate());

				//重複していない場合新規登録
			} else {

				//通年備考を更新
				updateAllYearRoundComment(calendarData, date);
				// カレンダーテーブルに登録
				calendarService.createCalendar(clientId, calendarData.getEmployeeId(), date, monthlyHoliday,
						monthlyPrescribedDays,
						"", userId);

				// シーケンスidの最大を取得
				int seqId = calendarService.searchMaxSeqId();

				// カレンダー詳細テーブルに登録
				calendarService.createCalendarDetail(seqId, details.getDate(), details.isHolidayFlag(),
						details.getComment(),
						userId);
			}
		}
		return "redirect:/calendar/list";
	}

	/**
	 * 閲覧処理
	 * @param calendarSeqIdIndex チェックボックス
	 * @param eventStatus ステータス
	 * @param seqId シーケンスID
	 * @param model modelデータ
	 * @return SMSCD002画面
	 */
	@GetMapping("/browse")
	public String browseCalender(@RequestParam(name = "seqId", required = false) String seqId,
			RedirectAttributes attr,
			Model model) {

		//排他チェック（削除)
		if (exclusiveCheck.ExclusiveCheckDalete(seqId, tableNumber)) {
			attr.addFlashAttribute("result", "該当データはすでに削除されています。");

			return "redirect:/calendar/list";
		}

		String mode = "view";

		// 顧客、社員、年月情報取得
		CalendarData calendarData = calendarService.searchCalendarData(seqId);

		// カレンダー詳細情報取得
		CalendarDetailManager calendarList = calendarService.searchCalendarDetails(seqId);

		// カレンダーリストを整形して表示用のリストを取得
		List<List<CalendarDetail>> formatLists = formatCalendarList(calendarList, calendarData.getYearMonth());

		//前月を取得
		Integer lastMonth = calendarService.getLastMonth(calendarData);
		if (lastMonth == -1) {
			//取得できなかったら
			//ボタン出さない
			model.addAttribute("lastMonth", false);
		} else {
			//ボタン出す
			model.addAttribute("lastMonth", true);
		}

		//次月を取得
		Integer followingMonth = calendarService.getFollowingMonth(calendarData);
		if (followingMonth == -1) {
			//取得できなかったら(-1なら)
			//ボタン出さない
			model.addAttribute("followingMonth", false);
		} else {
			//ボタン出す
			model.addAttribute("followingMonth", true);
		}

		model.addAttribute("calendarData", calendarData);
		model.addAttribute("calendarDatail", formatLists);

		model.addAttribute("seqId", seqId);
		model.addAttribute("mode", mode);

		return "SMSCD002";
	}

	/**
	 * SMSCD002 カレンダーマスタ編集画面遷移
	 * ★編集ボタン押下時に実行
	 */
	@GetMapping("/edit")
	public String editCalendarGet(
			@ModelAttribute("calendarData") CalendarData calendarData,
			@ModelAttribute("calendarDetail") CalendarDetailManager CalendarDetail,
			@ModelAttribute("selectDialogForm") SelectDialogForm selectDialogForm,
			@RequestParam(name = "seqId", required = false) String seqId,
			RedirectAttributes attr,
			Model model) {

		String mode = "edit";

		// 排他チェック(削除)
		if (exclusiveCheck.ExclusiveCheckDalete(seqId, 4)) {
			String result = "該当データはすでに削除されています。";
			attr.addFlashAttribute("result", result);
			// エラー(仮)
			return "redirect:/calendar/list"; // エラー時にtrue帰る様にする予定
		}

		/* 排他チェック（編集中） */
		if (exclusiveCheck.ExclusiveCheckEdited(seqId, userId, tableNumber)) {

			String result = "該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。";

			/* エラーメッセージをスコープに保存 */
			attr.addFlashAttribute("result", result);

			/* 一覧画面へリダイレクト */
			return "redirect:/calendar/list";
		}

		CalendarData data;
		CalendarDetailManager details;

		if (calendarData == null || CalendarDetail.getAll().isEmpty()) {
			// 顧客、社員、年月情報取得
			data = calendarService.searchCalendarData(seqId);
			// カレンダー詳細情報取得
			details = calendarService.searchCalendarDetails(seqId);
		} else {
			//スコープから取得できた場合(ダイアログの検索ボタンが押されていた時)
			data = calendarData;
			details = CalendarDetail;
		}

		// カレンダーリストを整形して表示用のリストを取得
		List<List<CalendarDetail>> formatLists = formatCalendarList(details, data.getYearMonth());

		model.addAttribute("calendarData", data);
		model.addAttribute("calendarDatail", formatLists);

		//ダイアログ表示処理
		dialogProcess(selectDialogForm, model);

		model.addAttribute("seqId", seqId);
		model.addAttribute("mode", mode);

		return "SMSCD002";

	}

	/**
	 * SMSCD002 カレンダーマスタ編集処理
	 * ★更新ボタン押下時に実行
	 */
	@PostMapping("/edit")
	public String editCalendarPost(@RequestParam(name = "seqId", required = false) String seqId,
			@RequestParam(name = "day", required = false) String[] day,
			@RequestParam(name = "holidayFlg", required = false) String[] holidayFlg,
			@RequestParam(name = "comment", required = false) String[] comment,
			@ModelAttribute("calendarData") CalendarData updateData,
			RedirectAttributes attr,
			Model model) {

		// 排他チェック(削除)
		if (exclusiveCheck.ExclusiveCheckDalete(seqId, tableNumber)) {
			String result = "該当データはすでに削除されています。";
			attr.addFlashAttribute("result", result);

			return "redirect:/calendar/list";
		}
		/* 排他チェック（編集中） */
		if (exclusiveCheck.ExclusiveCheckEdited(seqId, userId, tableNumber)) {

			String result = "該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。";

			/* エラーメッセージをスコープに保存 */
			attr.addFlashAttribute("result", result);

			/* 一覧画面へリダイレクト */
			return "redirect:/calendar/list";
		}

		//現在表示してるカレンダー情報をテーブルから取得
		CalendarData calendarData = calendarService.searchCalendarData(seqId);

		CalendarDetailManager details = new CalendarDetailManager(day.length);

		details.setAndTransformHolidayFlg(holidayFlg);
		details.setAndTransformComment(comment, calendarData.getYearMonth());

		//テーブルから取得した社員idと入力されている社員idを比較
		//違ったら、社員が更新されている
		if (!updateData.getEmployeeId().isEmpty()) {

			if (!updateData.getEmployeeId().equals(calendarData.getEmployeeId())) {

				//重複チェック
				if (calendarService.duplicateCheck(calendarData.getClientName(), updateData.getEmployeeId(),
						calendarData.getYearMonth()) != -1) {
					attr.addFlashAttribute("result", "顧客名、社員名、社員番号、年月に入力した「"
							+ calendarData.getClientName() + "」「"
							+ updateData.getEmployeeId() + "」「"
							+ updateData.getEmployeeName() + "」「"
							+ calendarData.getYear() + "/" + calendarData.getMonth()
							+ "」はカレンダーマスタにすでに存在しています。");
					details.setDate(day, calendarData.getYearMonth());

					attr.addFlashAttribute("calendarData", updateData);
					attr.addFlashAttribute("calendarDetail", details);
					return "redirect:/calendar/edit?seqId=" + seqId;
				}
			}
		}

		details.setAndTransformDate(day, calendarData.getYearMonth());

		// 年月をフォーマット
		YearMonth yearMonth = YearMonth.parse(updateData.getYear() + "-" + updateData.getMonth(),
				DateTimeFormatter.ofPattern("yyyy-MM"));
		// 最終日を取得
		int lastDayOfMonth = yearMonth.lengthOfMonth();

		// 更新
		calendarService.updateCalendar(seqId, updateData.getEmployeeId(), details.holidayCount(),
				lastDayOfMonth - details.holidayCount(),
				updateData.getAllYearRoundComment(), userId);

		//通年備考を更新
		updateAllYearRoundComment(updateData, updateData.getYearMonth());

		// 詳細更新
		calendarService.updateCalendarDetails(details.isHolidayFlag(), details.getComment(), userId, seqId,
				details.getDate());

		/* 排他チェック（編集済） */
		exclusiveCheck.ExclusiveLockDalete(seqId, userId, tableNumber);

		return "redirect:/calendar/list";
	}

	//選択行コピー
	@PostMapping("/copy")
	public String copyCalender(@RequestParam(name = "seqId", required = false) String[] seqId,
			RedirectAttributes attr,
			Model model) {

		//選択されているか
		if (seqId == null) {
			attr.addFlashAttribute("result", "対象が選択されていません。対象を選択してください。");
			return "redirect:/calendar/list";
		}
		//選択数が1件か
		else if (seqId.length != 1) {
			attr.addFlashAttribute("result", "コピー対象が複数選択されています。");
			return "redirect:/calendar/list";
		}
		// 排他チェック(削除)
		else if (exclusiveCheck.ExclusiveCheckDalete(seqId[0], tableNumber)) {
			attr.addFlashAttribute("result", "該当データはすでに削除されています。");
			return "redirect:/calendar/list";
		}

		// 顧客、社員、年月情報取得
		CalendarData calendarData = calendarService.searchCalendarData(seqId[0]);

		//社員情報消す？

		// カレンダー詳細情報取得
		CalendarDetailManager calendarList = calendarService.searchCalendarDetails(seqId[0]);

		attr.addFlashAttribute("calendarData", calendarData);
		attr.addFlashAttribute("calendarDatail", calendarList);

		attr.addFlashAttribute("mode", "copy");

		return "redirect:/calendar/regist";
	}

	/**
	 *「＜」ボタンが押された時に、現在表示してるのと一致する顧客、社員の先月のカレンダーを表示する
	 * @return
	 */
	@PostMapping("/lastMonth")
	private String petLastMonth(@ModelAttribute("calendarData") CalendarData calendarData,
			Model model) {

		//前月のseqIdを取得
		Integer seqId = calendarService.getLastMonth(calendarData);

		return "redirect:/calendar/browse?seqId=" + seqId;
	}

	/**
	 *「＜」ボタンが押された時に、現在表示してるのと一致する顧客、社員の翌月のカレンダーを表示する
	 * @return
	 */
	@PostMapping("/followingMonth")
	private String postFollowingMonth(@ModelAttribute("calendarData") CalendarData calendarData,
			Model model) {

		//前月のseqIdを取得
		Integer seqId = calendarService.getFollowingMonth(calendarData);

		return "redirect:/calendar/browse?seqId=" + seqId;
	}

	/**
	 * 戻るボタン用メソッド
	 * 排他チェック(編集済)を実行したい
	 * @param seqId
	 * @param model
	 * @return
	 */
	@PostMapping("/back")
	public String calendarListBack(
			@RequestParam(name = "seqId", required = false) String seqId,
			@RequestParam(name = "mode", required = false) String mode,
			Model model) {

		//新規追加➁だった場合は新規追加①に遷移
		if (mode.equals("regist")) {
			return "redirect:/calendar/create";
		}

		//編集時のみ排他チェック(編集済)を実行
		if (mode.equals("edit")) {
			exclusiveCheck.ExclusiveLockDalete(seqId, userId, tableNumber);
			System.out.println("/back : " + seqId);
		}

		return "redirect:/calendar/list";
	}

	/**
	 * ダイアログに必要な処理をまとめたメソッド
	 * @param selectDialogForm
	 * @param model
	 */
	private void dialogProcess(SelectDialogForm selectDialogForm, Model model) {

		//顧客選択用
		List<Map<String, Object>> clientList = calendarService.getClient(selectDialogForm.getDialogClientId(),
				selectDialogForm.getDialogClientName());
		model.addAttribute("client_list", clientList);
		//顧客ダイアログの検索ボタン押されていたら顧客ダイアログを表示
		if (selectDialogForm.getDialogSearchClient().equals("open")) {
			model.addAttribute("clientDialog", "open");
		} else {
			model.addAttribute("clientDialog", "close");
		}

		//社員選択用
		List<Map<String, Object>> employeeList = calendarService.getEmployee(selectDialogForm.getDialogEmployeeId(),
				selectDialogForm.getDialogEmployeeName());
		model.addAttribute("employee_list", employeeList);
		//社員ダイアログの検索ボタン押されていたら社員ダイアログを表示
		if (selectDialogForm.getDialogSearchEmployee().equals("open")) {
			model.addAttribute("employeeDialog", "open");
		} else {
			model.addAttribute("employeeDialog", "close");
		}

		model.addAttribute("selectDialogForm", selectDialogForm);
	}

	/**
	 * 日付と休日が暦通りに登録された詳細データを返す
	 * @param yearMonth 開始日
	 * @return
	 */
	private CalendarDetailManager getDataAndHoliday(LocalDate yearMonth) {

		// 日付と休日フラグのlist
		CalendarDetailManager details = new CalendarDetailManager(yearMonth.lengthOfMonth());

		// 次の日が振替休日か判定した結果を格納するフラグ
		boolean substitute = false;

		// 最終日を取得
		LocalDate endDate = yearMonth.plusMonths(1).minusDays(1);

		// 1日ずつListにaddする
		for (LocalDate date = yearMonth; !date.isAfter(endDate); date = date.plusDays(1)) {
			CalendarDetail datail = new CalendarDetail();

			//日付を表示する形に変換
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d");
			String formattedDay = date.format(formatter);

			//曜日を取得
			DayOfWeek dayOfWeek = date.getDayOfWeek();

			// 土日かチェック
			boolean isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
			// 祝日判定
			boolean isHoliday = false;
			String holidayName = getJapaneseHolidayName(date);

			if (!holidayName.equals("")) {
				isHoliday = true;
			}

			// 振替休日判定
			// 日曜が祝日ならflagをtrue
			if (dayOfWeek == DayOfWeek.SUNDAY && isHoliday) {
				substitute = true;
			} // 月曜を祝日判定にする
			if (dayOfWeek == DayOfWeek.MONDAY && substitute == true) {
				isHoliday = true;
				substitute = false;
				holidayName = "振替休日";
			}

			if (isWeekend || isHoliday) {
				datail.setHolidayFlg(true);
			} else {
				datail.setHolidayFlg(false);
			}

			datail.setDate(formattedDay);
			datail.setDayOfWeek(dayOfWeek.getValue());
			datail.setComment(holidayName);
			details.addDetails(datail, Integer.parseInt(formattedDay) - 1);
		}

		return details;
	}

	/**
	 * 年間休日数を計算するメソッド
	 * 
	 * @param list
	 * @return
	 */
	public int totalAnnualHolidays(Map<String, Object> list) {
		// 文字列を LocalDate に変換
		LocalDate yearMonth = LocalDate.parse(list.get("year_month").toString(),
				DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		// 年度の始め、終わりを求める
		LocalDate startOfYear = LocalDate.of(yearMonth.getYear(), Month.APRIL, 1);
		if (yearMonth.isBefore(startOfYear)) {
			startOfYear = startOfYear.minusYears(1); // 前年の年度の始まりに補正
		}
		LocalDate endOfYear = startOfYear.plusMonths(12).minusDays(1);

		// 既存の月、休日数を格納したリスト
		List<Map<String, Object>> holidayList = calendarService.searchHolidayCount(
				list, startOfYear.toString(), endOfYear.toString());

		// 既存の月だけを格納したリスト
		List<String> holidayMonth = new ArrayList<>();
		for (Map<String, Object> hmList : holidayList) {
			holidayMonth.add(hmList.get("year_month").toString());
		}

		// 年間休日数保存用
		int holidayCount = 0;

		// その年度の3月から4月までを月単位で繰り返す
		for (LocalDate date = startOfYear; !date.isAfter(endOfYear); date = date.plusMonths(1)) {

			// dateをString型に変換
			String strDate = date.toString();

			// 登録されている月の休日数を取得
			// 取得した休日数リストの要素分繰り返す
			for (Map<String, Object> hList : holidayList) {

				// dateがholidayListにある月なら
				if (hList.get("year_month").toString().equals(strDate)) {
					// 休日数を年間休日数に加算
					holidayCount += (int) hList.get("monthly_holidays");
				}
			}
			// 登録されていない月の休日数をカウント
			if (!holidayMonth.contains(strDate)) {

				// 日付と休日フラグのlist
				CalendarDetailManager details = getDataAndHoliday(date);

				// 休日数をカウント
				holidayCount += details.holidayCount();

			}

		}

		return holidayCount;
	}

	/**
	 * 新規登録時
	 * 顧客、社員が一致する、年度内のデータの通年備考を取得する
	 * @param calendarData
	 * @return
	 */
	private String searchAllYearRoundComment(CalendarData calendarData) {

		//年月を取得
		LocalDate yearMonth = calendarData.getYearMonth();

		// 年度の始め、終わりを求める
		LocalDate startOfYear = LocalDate.of(yearMonth.getYear(), Month.APRIL, 1);
		if (yearMonth.isBefore(startOfYear)) {
			startOfYear = startOfYear.minusYears(1); // 前年の年度の始まりに補正
		}
		LocalDate endOfYear = startOfYear.plusMonths(12).minusDays(1);

		//コメントを取得
		String comment = calendarService.searchAllYearRoundComment(calendarData,
				startOfYear.toString(), endOfYear.toString());

		return comment;
	}

	/**
	 *同年度内の通年備考を更新する 
	 * @param calendarData
	 */
	private void updateAllYearRoundComment(CalendarData calendarData, LocalDate yearMonth) {

		// 年度の始め、終わりを求める
		LocalDate startOfYear = LocalDate.of(yearMonth.getYear(), Month.APRIL, 1);
		if (yearMonth.isBefore(startOfYear)) {
			startOfYear = startOfYear.minusYears(1); // 前年の年度の始まりに補正
		}
		LocalDate endOfYear = startOfYear.plusMonths(12).minusDays(1);

		// その年度の3月から4月までを月単位で繰り返す
		for (LocalDate date = startOfYear; !date.isAfter(endOfYear); date = date.plusMonths(1)) {

			//更新処理
			calendarService.updateAllYearRoundComment(calendarData, date, userId);
		}
	}

	/**
	 * カレンダーリスト表示用に整形するメソッド
	 * @param calendarList シーケンスidから取得したカレンダーリスト
	 * @return List<List<Map<String, Object>>> 表示用に整形したカレンダーリスト
	 */
	private List<List<CalendarDetail>> formatCalendarList(CalendarDetailManager details, LocalDate yearMonth) {

		List<CalendarDetail> calendarList = details.getAll();

		List<List<CalendarDetail>> formatLists = new ArrayList<>();

		List<CalendarDetail> list1 = new ArrayList<>();
		List<CalendarDetail> list2 = new ArrayList<>();
		List<CalendarDetail> list3 = new ArrayList<>();
		List<CalendarDetail> list4 = new ArrayList<>();
		List<CalendarDetail> list5 = new ArrayList<>();
		List<CalendarDetail> list6 = new ArrayList<>();

		//List<CalendarDetail> calendarList = new ArrayList<>();

		//1日目の曜日を取得
		int firstDayOfWeek = yearMonth.getDayOfWeek().getValue();

		// カレンダー表示時に先頭に入れる空白の数
		int topBlank = firstDayOfWeek;
		if (topBlank == 7) {
			topBlank = 0;
		}

		// カレンダー表示時に末尾にいれる空白の数
		int bottomBlank = 0;
		if (calendarList.size() + topBlank <= 35) {
			bottomBlank = 35 - (calendarList.size() + topBlank);
		} else {
			bottomBlank = 42 - (calendarList.size() + topBlank);
		}

		//表示用に整形
		int count = topBlank;
		for (int i = 0; i < calendarList.size() + topBlank + bottomBlank; i++) {

			int input = i - topBlank;

			if (i < 7) {

				if (count >= 1) {
					list1.add(null);
					count--;
				} else {
					list1.add(calendarList.get(input));
				}
			} else if (i >= 7 && i < 14) {
				list2.add(calendarList.get(input));
			} else if (i >= 14 && i < 21) {
				list3.add(calendarList.get(input));
			} else if (i >= 21 && i < 28) {
				list4.add(calendarList.get(input));
			} else if ((i >= 28 && i < 35)) {

				if (i >= calendarList.size() + topBlank) {
					list5.add(null);
				} else {
					list5.add(calendarList.get(input));
				}

			} else {
				if (i >= calendarList.size() + topBlank) {
					list6.add(null);
				} else {
					list6.add(calendarList.get(input));
				}
			}

		}

		formatLists.add(list1);
		formatLists.add(list2);
		formatLists.add(list3);
		formatLists.add(list4);
		formatLists.add(list5);
		formatLists.add(list6);

		return formatLists;
	}

	/**
	 * 祝日判定用メソッド
	 * 
	 * @param date
	 * @return
	 */
	//	private static boolean isJapaneseHoliday(LocalDate date) {
	//		int year = date.getYear();
	//		Month month = date.getMonth();
	//		int day = date.getDayOfMonth();
	//
	//		// 祝日の判定
	//		if ((month == Month.JANUARY && day == 1) || // 元日
	//				(month == Month.JANUARY && date.getDayOfWeek() == DayOfWeek.MONDAY && day >= 8 && day <= 14) || // 成人の日（1月の第2月曜日）
	//				(month == Month.FEBRUARY && day == 11) || // 建国記念日
	//				(month == Month.FEBRUARY && day == 23 && year >= 2020) || // 天皇誕生日
	//				(month == Month.MARCH && day == calculateVernalEquinoxDay(year)) || // 春分の日
	//				(month == Month.APRIL && day == 29) || // 昭和の日
	//				(month == Month.MAY && day == 3) || // 憲法記念日
	//				(month == Month.MAY && day == 4) || // みどりの日
	//				(month == Month.MAY && day == 5) || // こどもの日
	//				(month == Month.JULY
	//						&& date.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY)).plusDays(14).isEqual(date))
	//				|| // 海の日（7月の第3月曜日）
	//				(month == Month.AUGUST && day == 11 && year >= 2016) || // 山の日
	//				(month == Month.SEPTEMBER
	//						&& date.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY)).plusDays(14).isEqual(date))
	//				|| // 敬老の日（9月の第3月曜日）
	//				(month == Month.SEPTEMBER && day == calculateAutumnalEquinoxDay(year)) || // 秋分の日
	//				(month == Month.OCTOBER
	//						&& date.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY)).plusDays(7).isEqual(date))
	//				|| // 体育の日（10月の第2月曜日）
	//				(month == Month.NOVEMBER && day == 3) || // 文化の日
	//				(month == Month.NOVEMBER && day == 23) || // 勤労感謝の日
	//				((month == Month.DECEMBER && day >= 29) || (month == Month.JANUARY && day <= 3))) {// 年末年始（12月29日から1月3日）
	//			return true;
	//		}
	//
	//		return false;
	//	}

	/**
	 * 祝日名を返すメソッド
	 * @param date
	 * @return
	 */
	private static String getJapaneseHolidayName(LocalDate date) {
		int year = date.getYear();
		Month month = date.getMonth();
		int day = date.getDayOfMonth();

		// 祝日の判定
		if ((month == Month.JANUARY && day == 1)) {
			return "元日";
		} else if (month == Month.JANUARY && date.getDayOfWeek() == DayOfWeek.MONDAY && day >= 8 && day <= 14) {
			return "成人の日";
		} else if (month == Month.FEBRUARY && day == 11) {
			return "建国記念日";
		} else if (month == Month.FEBRUARY && day == 23 && year >= 2020) {
			return "天皇誕生日";
		} else if (month == Month.MARCH && day == calculateVernalEquinoxDay(year)) {
			return "春分の日";
		} else if (month == Month.APRIL && day == 29) {
			return "昭和の日";
		} else if (month == Month.MAY && day == 3) {
			return "憲法記念日";
		} else if (month == Month.MAY && day == 4) {
			return "みどりの日";
		} else if (month == Month.MAY && day == 5) {
			return "こどもの日";
		} else if (month == Month.JULY
				&& date.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY)).plusDays(14).isEqual(date)) {
			return "海の日";
		} else if (month == Month.AUGUST && day == 11 && year >= 2016) {
			return "山の日";
		} else if (month == Month.SEPTEMBER
				&& date.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY)).plusDays(14).isEqual(date)) {
			return "敬老の日";
		} else if (month == Month.SEPTEMBER && day == calculateAutumnalEquinoxDay(year)) {
			return "秋分の日";
		} else if (month == Month.OCTOBER
				&& date.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY)).plusDays(7).isEqual(date)) {
			return "スポーツの日";
		} else if (month == Month.NOVEMBER && day == 3) {
			return "文化の日";
		} else if (month == Month.NOVEMBER && day == 23) {
			return "勤労感謝の日";
		} else if ((month == Month.DECEMBER && day >= 29) || (month == Month.JANUARY && day <= 3)) {
			return "年末年始";
		} else {
			return "";
		}
	}

	private static int calculateVernalEquinoxDay(int year) {
		// 春分の日の計算式（国立天文台の提供するもの）
		return (int) (20.8431 + 0.242194 * (year - 1980) - (int) ((year - 1980) / 4));
	}

	private static int calculateAutumnalEquinoxDay(int year) {
		// 秋分の日の計算式（国立天文台の提供するもの）
		return (int) (23.2488 + 0.242194 * (year - 1980) - (int) ((year - 1980) / 4));
	}

}
