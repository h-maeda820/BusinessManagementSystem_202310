package com.nexus.whc.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//カレンダー詳細情報を管理したい
public class CalendarDetailManager {
	private List<CalendarDetail> details;

	public CalendarDetailManager() {
		details = new ArrayList<>();
	}

	public CalendarDetailManager(Integer num) {
		details = new ArrayList<>(num);

		for (Integer i = 0; i < num; i++) {
			details.add(new CalendarDetail()); // リストに要素を追加して初期化
		}
	}

	// 1か月分のCalendarDetailを追加するメソッド
	public void addMonthDetails(List<CalendarDetail> monthDetails) {
		this.details.addAll(monthDetails);
	}

	//1日分のCalendarDetailを追加するメソッド
	public void addDetails(CalendarDetail details, Integer index) {
		this.details.set(index, details);
	}

	//1ヵ月分のCalendarDetailを取得する
	public List<CalendarDetail> getAll() {
		return details;
	}

	// 特定の日のCalendarDetailを取得するメソッド
	public CalendarDetail getDetailForDate(String date) {
		for (CalendarDetail detail : details) {
			if (detail.getDate().equals(date)) {
				return detail;
			}
		}
		return null; // 該当する日付のデータが見つからない場合はnullを返す
	}

	//	//指定された要素に値を格納する
	//	public void setDate(String date, Integer index) {
	//		details.get(index).setDate(date);
	//	}
	//
	//	public void setHolidayFlag(boolean flag, Integer index) {
	//		details.get(index).setHolidayFlg(flag);
	//	}
	//
	//	public void setComment(String comment, Integer index) {
	//		details.get(index).setComment(comment);
	//	}
	//
	//	public void setDayOfWeek(Integer dayOfWeek, Integer index) {
	//		details.get(index).setDayOfWeek(dayOfWeek);
	//	}

	//日付をddの状態で格納する
	public void setDate(String[] date, LocalDate yearMonth) {

		String formatYearMonth = yearMonth.toString().substring(0, 8); // yyyy-mm- の部分を取得

		int index = 0;
		for (String day : date) {
			String inputDate = formatYearMonth + String.format("%02d", Integer.parseInt(day));

			this.details.get(index).setDate(day);
			//曜日も登録
			this.details.get(index).setDayOfWeek(LocalDate.parse(inputDate).getDayOfWeek().getValue());
			index++;
		}
	}

	//日付をddからyyyy-mm-ddに変換して格納する
	public void setAndTransformDate(String[] date, LocalDate yearMonth) {

		String formatYearMonth = yearMonth.toString().substring(0, 8); // yyyy-mm- の部分を取得

		int index = 0;
		for (String day : date) {
			String inputDate = formatYearMonth + String.format("%02d", Integer.parseInt(day));

			this.details.get(index).setDate(inputDate);
			//曜日も登録
			this.details.get(index).setDayOfWeek(LocalDate.parse(inputDate).getDayOfWeek().getValue());
			index++;
		}
	}

	public void transformDate(LocalDate yearMonth) {

		String formatYearMonth = yearMonth.toString().substring(0, 8); // yyyy-mm- の部分を取得

		int index = 0;
		for (String day : this.getDate()) {

			String inputDate = formatYearMonth + String.format("%02d", Integer.parseInt(day));
			this.details.get(index).setDate(inputDate);
			index++;
		}
	}

	//holidayFlgをboolean型に変換して格納する
	public void setAndTransformHolidayFlg(String[] isHoliday) {

		int index = 0;
		for (String flag : isHoliday) {
			this.details.get(index).setHolidayFlg(Boolean.parseBoolean(flag));
			index++;
		}
	}

	//コメントの前後にある空白を消して格納する
	public void setAndTransformComment(String[] comment, LocalDate yearMonth) {

		int indexNum = yearMonth.lengthOfMonth();

		int firstDayOfWeek = yearMonth.getDayOfWeek().getValue();
		// カレンダー表示時に先頭に入れる空白の数
		int topBlank = firstDayOfWeek;
		if (topBlank == 7) {
			topBlank = 0;
		}

		String[] formatComment = new String[indexNum];

		// 配列を空白の数だけ前にずらす
		for (int i = 0; i < indexNum; i++) {
			formatComment[i] = comment[i + topBlank];
		}

		//格納する
		for (int j = 0; j < indexNum; j++) {
			if (!formatComment[j].isEmpty()) {
				this.details.get(j).setComment(formatComment[j]);
			} else {
				this.details.get(j).setComment("");
			}
		}
	}

	public String[] getDate() {

		String day[] = new String[details.size()];
		for (int i = 0; i < details.size(); i++) {
			day[i] = details.get(i).getDate();
		}
		return day;
	}

	public boolean[] isHolidayFlag() {

		boolean isHoliday[] = new boolean[details.size()];
		for (int i = 0; i < details.size(); i++) {
			isHoliday[i] = details.get(i).isHolidayFlg();
		}
		return isHoliday;
	}

	public String[] getComment() {

		String comment[] = new String[details.size()];
		for (int i = 0; i < details.size(); i++) {
			comment[i] = details.get(i).getComment();
		}
		return comment;
	}

	public int holidayCount() {

		int count = 0;
		for (CalendarDetail date : details) {

			if (date.isHolidayFlg()) {
				count++;
			}
		}
		return count;
	}
}
