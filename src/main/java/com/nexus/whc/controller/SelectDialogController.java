package com.nexus.whc.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nexus.whc.models.CalendarData;
import com.nexus.whc.models.CalendarDetailManager;
import com.nexus.whc.models.CalendarSearchForm;
import com.nexus.whc.models.EmployeeDate;
import com.nexus.whc.models.EmployeeSearchForm;
import com.nexus.whc.models.SelectDialogForm;
import com.nexus.whc.services.CalendarService;
import com.nexus.whc.services.EmployeeService;

/*
 * CalendarController.java
 * 
 * CalendarControllerクラス
 */

/*
 * Controllerクラス
 */
@Controller
@RequestMapping("")
public class SelectDialogController {

	/* CalendarServiceクラス */
	@Autowired
	CalendarService calendarService;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	HttpSession session;

	/**
	 * 社員マスタ一覧画面の選択ダイアログで検索ボタンを押したときに実行される
	 * @param selectDialogForm
	 * @param displaySearchForm
	 * @param pageNumber
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/employee/list/searchDialog")
	public String employeeListSearchDialog(@ModelAttribute("selectDialogForm") SelectDialogForm selectDialogForm,
			@ModelAttribute("employeeSearchForm") EmployeeSearchForm displaySearchForm,
			@RequestParam(name = "pageNumber", defaultValue = "1") String pageNumber,
			RedirectAttributes redirectAttributes) {

		EmployeeSearchForm searchForm = (EmployeeSearchForm) session.getAttribute("employeeSearchForm");

		redirectAttributes.addFlashAttribute("selectDialogForm", selectDialogForm);
		redirectAttributes.addFlashAttribute("employeeSearchForm", searchForm);
		redirectAttributes.addFlashAttribute("displayEmployeeSearchForm", displaySearchForm);

		return "redirect:/employee/list?pageNumber=" + pageNumber;
	}

	/**
	 * 社員マスタの新規登録画面の選択ダイアログで検索ボタンが押されたら実行される
	 * @param selectDialogForm
	 * @param employeeDate
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/employee/regist/searchDialog")
	public String employeeRegistSearchDialog(
			@ModelAttribute("selectDialogForm") SelectDialogForm selectDialogForm,
			@ModelAttribute("employeeDate") EmployeeDate employeeDate,
			RedirectAttributes redirectAttributes) {

		redirectAttributes.addFlashAttribute("selectDialogForm", selectDialogForm);
		redirectAttributes.addFlashAttribute("employeeDate", employeeDate);

		return "redirect:/employee/regist";
	}

	/**
	 * 社員マスタの編集画面の選択ダイアログで検索ボタンが押されたら実行される
	 * @param selectDialogForm
	 * @param employeeDate
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/employee/edit/searchDialog")
	public String employeeEditSearchDialog(
			@ModelAttribute("selectDialogForm") SelectDialogForm selectDialogForm,
			@ModelAttribute("employeeDate") EmployeeDate employeeDate,
			@RequestParam("employeeId") String employeeId,
			RedirectAttributes redirectAttributes) {

		redirectAttributes.addFlashAttribute("selectDialogForm", selectDialogForm);
		redirectAttributes.addFlashAttribute("employeeDate", employeeDate);

		return "redirect:/employee/edit?employeeId=" + employeeId;
	}

	/**
	 * カレンダーマスタ一覧画面の選択ダイアログで検索ボタンを押した時に実行される
	 * @param selectDialogForm
	 * @param searchForm
	 * @param pageNumber
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/calendar/list/searchDialog")
	public String calendarLstSearchDialog(@ModelAttribute("selectDialogForm") SelectDialogForm selectDialogForm,
			@ModelAttribute("calendarSearchForm") CalendarSearchForm displaySearchForm,
			@RequestParam(name = "pageNumber", defaultValue = "1") String pageNumber,
			RedirectAttributes redirectAttributes) {

		CalendarSearchForm searchForm = (CalendarSearchForm) session.getAttribute("calendarSearchForm");

		redirectAttributes.addFlashAttribute("selectDialogForm", selectDialogForm);
		redirectAttributes.addFlashAttribute("calendarSearchForm", searchForm);
		redirectAttributes.addFlashAttribute("displayCalendarSearchForm", displaySearchForm);

		return "redirect:/calendar/list?pageNumber=" + pageNumber;
	}

	/**
	 * カレンダーマスタ新規登録画面①の選択ダイアログで検索ボタンを押した時に実行される
	 * @param selectDialogForm
	 * @param calendarData
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/calendar/create/searchDialog")
	public String calendarCreateSearchDialog(
			@ModelAttribute("selectDialogForm") SelectDialogForm selectDialogForm,
			@ModelAttribute("calendarData") CalendarData calendarData,
			RedirectAttributes redirectAttributes) {

		redirectAttributes.addFlashAttribute("selectDialogForm", selectDialogForm);
		redirectAttributes.addFlashAttribute("calendarData", calendarData);

		return "redirect:/calendar/create";
	}

	/**
	 * カレンダーマスタ新規登録画面➁の選択ダイアログで検索ボタンを押したときに実行される
	 * @param selectDialogForm
	 * @param calendarData
	 * @param CalendarDetail
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/calendar/regist/searchDialog")
	public String calendarRegistSearchDialog(
			@ModelAttribute("selectDialogForm") SelectDialogForm selectDialogForm,
			@ModelAttribute("calendarData") CalendarData calendarData,
			@RequestParam(name = "day", required = false) String[] day,
			@RequestParam(name = "holidayFlg", required = false) String[] holidayFlg,
			@RequestParam(name = "comment", required = false) String[] comment,
			RedirectAttributes redirectAttributes) {

		CalendarDetailManager calendarDetail = new CalendarDetailManager(day.length);

		calendarDetail.setDate(day, calendarData.getYearMonth());
		calendarDetail.setAndTransformHolidayFlg(holidayFlg);
		calendarDetail.setAndTransformComment(comment, calendarData.getYearMonth());

		redirectAttributes.addFlashAttribute("selectDialogForm", selectDialogForm);
		redirectAttributes.addFlashAttribute("calendarData", calendarData);
		redirectAttributes.addFlashAttribute("calendarDetail", calendarDetail);

		return "redirect:/calendar/regist";
	}

	/**
	 * カレンダーマスタ編集画面の選択ダイアログで検索ボタンを押したときに実行される
	 * @param selectDialogForm
	 * @param calendarData
	 * @param day
	 * @param holidayFlg
	 * @param comment
	 * @param seqId
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/calendar/edit/searchDialog")
	public String calendarEditSearchDialog(
			@ModelAttribute("selectDialogForm") SelectDialogForm selectDialogForm,
			@ModelAttribute("calendarData") CalendarData calendarData,
			@RequestParam(name = "day", required = false) String[] day,
			@RequestParam(name = "holidayFlg", required = false) String[] holidayFlg,
			@RequestParam(name = "comment", required = false) String[] comment,
			@RequestParam(name = "seqId", required = false) String seqId,
			RedirectAttributes redirectAttributes) {

		CalendarDetailManager calendarDetail = new CalendarDetailManager(day.length);

		calendarDetail.setDate(day, calendarData.getYearMonth());
		calendarDetail.setAndTransformHolidayFlg(holidayFlg);
		calendarDetail.setAndTransformComment(comment, calendarData.getYearMonth());

		redirectAttributes.addFlashAttribute("selectDialogForm", selectDialogForm);
		redirectAttributes.addFlashAttribute("calendarData", calendarData);
		redirectAttributes.addFlashAttribute("calendarDetail", calendarDetail);

		return "redirect:/calendar/edit?seqId=" + seqId;
	}
}
