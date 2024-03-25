package com.nexus.whc.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.nexus.whc.services.TopService;

/*
 * TopController.java
 * topページに関するアプリケーションを制御を行うクラス
 * 
 * @author 寺島 健太
 */

@Controller
public class TopController {

	@Autowired
	TopService topService;

	@GetMapping("/SCMCM001")
	public String topList(Model model) {

		/*画面遷移先を社員マスタ情報一覧画面に指定*/
		String res = "SCMCM001";

		/*一覧表示のSQL実行、スコープに保存*/
		List<Map<String, Object>> list = topService.searchAll();
		for (Map<String, Object> publicationDate : list) {
			Object oPublicationDate = publicationDate.get("publication_date");
			String sPublicationDate = oPublicationDate.toString();

			/*元のフォーマット*/
			DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			/*新しいフォーマット*/
			DateTimeFormatter newFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

			/*日付文字列をLocalDateに変換*/
			LocalDate date = LocalDate.parse(sPublicationDate, originalFormatter);

			/*新しいフォーマットで文字列に変換*/
			String formattedDate = date.format(newFormatter);

			/*新しいフォーマットに置き換え*/
			publicationDate.put("publication_date", formattedDate);

		}
		model.addAttribute("list", list);

		return res;

	}
}