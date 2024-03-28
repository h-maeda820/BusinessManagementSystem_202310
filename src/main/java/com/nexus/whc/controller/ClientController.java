package com.nexus.whc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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

import com.nexus.whc.models.ClientData;
import com.nexus.whc.repository.ClientRepository;
import com.nexus.whc.services.ClientService;
import com.nexus.whc.services.ExclusiveCheckService;

/*
 * ClientController.java
 * 
 * 顧客情報管理機能に関するアプリケーション制御を行うクラス
 * @author 榊原 孝司
 * @version 
 */
/*
 * Controllerクラス
 */
@Controller
@RequestMapping("/client")
public class ClientController {

	@Autowired
	ClientService clientService;

	@Autowired
	ClientRepository clientRepository;

	@Autowired
	HttpSession session;

	@Autowired
	MessageSource messages;

	/*排他チェック
	 * 排他チェックサービスクラスであるExclusiveCheckServiceの中のオブジェクトを呼び出すための変数
	 */
	@Autowired
	ExclusiveCheckService exclusiveCheck;

	// テーブル判別用番号
	// tableNumber3 m_client 排他チェック用
	final int tableNumber = 3;
	// ログインID
	final String loginUserId = "nexus001";
	// テーブル名
	int editTableName = 3;

	/*ページネーション*/
	// ページサイズを20に設定
	int pageSize = 20;

	//遷移先を変数に格納(社員マスタ一覧画面)*/
	String res1 = "SMSCL001";
	//遷移先を変数に格納(社員マスタ登録画面)*/
	String res2 = "SMSCL002";
	// エラーメッセージを格納する変数をインスタンス化 
	String attributeValue = "";
	String attributeValue2 = "";
	String attributeValue3 = "";

	/**
	 * 一覧
	 * @param clientId
	 * @param clientName
	 * @param pageNumber
	 * @param model
	 * @return
	 */
	@GetMapping("/list")
	public String clientList(@ModelAttribute("client_id") String clientId,
			@ModelAttribute("client_name") String clientName,
			@RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
			Model model) {

		// ページサイズを定義
		int pageSize = 20;
		//現在のページとページサイズを引数に設定
		List<Map<String, Object>> list = clientService.searchActiveClient(clientId, clientName, pageNumber - 1,
				pageSize);

		//返された顧客情報が0件かチェック
		if (list.isEmpty()) {
			List<Map<String, Object>> listAll = clientService.searchAll();
			if (!listAll.isEmpty()) {
				model.addAttribute("result", "顧客一覧の検索結果は0件です。条件を変更し、再度検索してください。");
			}
		} else {
			for (Map<String, Object> map : list) {

				//顧客番号を3桁に変換
				String formattedClientId = String.format("%03d", (Integer) map.get("client_id"));
				map.put("client_id", formattedClientId);
			}
		}

		model.addAttribute("client_id", clientId);
		model.addAttribute("client_name", clientName);
		model.addAttribute("client_list", list);

		session.setAttribute("client_id", clientId);
		session.setAttribute("client_name", clientName);

		//ページネーション用
		//アクティブなデータの総数
		int totalClient = clientService.countSearchClient(clientId, clientName);

		//20件ずつ表示したときのページ数
		int totalPage = (int) Math.ceil((double) totalClient / pageSize);
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
	 * @param clientId
	 * @param clientName
	 * @param pageNumber
	 * @param attr
	 * @return
	 */
	@PostMapping("/search")
	public String clientPostList(@RequestParam(name = "client_id", defaultValue = "") String clientId,
			@RequestParam(name = "client_name", defaultValue = "") String clientName,
			@RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
			RedirectAttributes attr) {

		attr.addFlashAttribute("client_id", clientId);
		attr.addFlashAttribute("client_name", clientName);

		return "redirect:/client/list?pageNumber=" + pageNumber;

	}

	/**
	 * ページネーション
	 * @param pageNumber
	 * @param redirectAttributes
	 * @return
	 */
	@GetMapping("/pageList")
	public String pageListClientGet(@RequestParam(name = "pageNumber", defaultValue = "1") String pageNumber,
			RedirectAttributes redirectAttributes) {

		String clientId = session.getAttribute("client_id").toString();
		String clientName = session.getAttribute("client_name").toString();

		redirectAttributes.addFlashAttribute("client_id", clientId);
		redirectAttributes.addFlashAttribute("client_name", clientName);

		return "redirect:/client/list?pageNumber=" + pageNumber;
	}

	//
	//	/*SMSCL001 顧客一覧画面遷移
	//	 * ページネーション
	//	 */
	//	@GetMapping("/list")
	//	public String clientList(@RequestParam(name = "page", defaultValue = "1") int page,
	//			@ModelAttribute ClientData clientData, Model model) {
	//
	//		/**ページネーションの実装**/
	//		//現在のページとページサイズを引数に設定
	//		List<Map<String, Object>> list = clientService.searchActive(page - 1, pageSize);
	//
	//		// ここで総データ数を取得するロジックを実装し、総ページ数を計算
	//		int totalClients = clientService.countClient();
	//		int totalPages = (int) Math.ceil((double) totalClients / pageSize);
	//		//　現在のページ数
	//		int correctedNumber = page + 1;
	//
	//		// スコープに保存
	//		model.addAttribute("client_list", list);
	//		model.addAttribute("currentPage", correctedNumber);
	//		model.addAttribute("totalPages", totalPages);
	//		model.addAttribute("searchCheck", false);
	//		model.addAttribute("mode", 1);//一覧(モード1)
	//		return res1;
	//	}
	//
	//	/*SMSCL001 顧客一覧画面遷移
	//	 * ページネーション
	//	 */
	//	@PostMapping("/list")
	//	public String clientList(@RequestParam(name = "page", defaultValue = "1") int page,
	//			@ModelAttribute ClientData clientData,
	//			@RequestParam String buttonType,
	//			@RequestParam(name = "client_id", defaultValue = "") String clientId, Model model) {
	//
	//		/*送信時(キャンセル)のボタンを判定して遷移先指定*/
	//		if ("exclusiveCheck".equals(buttonType)) {
	//			/*排他チェック（編集中ロック解除）*/
	//			exclusiveCheck.ExclusiveLockDalete(clientId, loginUserId, tableNumber);
	//		}
	//		/**ページネーションの実装**/
	//		//現在のページとページサイズを引数に設定
	//		List<Map<String, Object>> list = clientService.searchActive(page - 1, pageSize);
	//
	//		// ここで総データ数を取得するロジックを実装し、総ページ数を計算
	//		int totalClients = clientService.countClient();
	//		int totalPages = (int) Math.ceil((double) totalClients / pageSize);
	//		//　現在のページ数
	//		int correctedNumber = page + 1;
	//
	//		// スコープに保存
	//		model.addAttribute("client_list", list);
	//		model.addAttribute("currentPage", correctedNumber);
	//		model.addAttribute("totalPages", totalPages);
	//		model.addAttribute("searchCheck", false);
	//		model.addAttribute("mode", 1);//一覧(モード1)
	//		return res1;
	//	}
	//
	//	/*SMSCL001 顧客検索機能
	//	 *顧客IDもしくは顧客名の検索時に使用
	//	 * ページネーション
	//	 * 以下の時はエラーを発出
	//	 * ・検索結果が0件の時
	//	 */
	//	@PostMapping("/search")
	//	public String clientPostList(@RequestParam(name = "client_id", defaultValue = "") String clientId,
	//			@RequestParam(name = "client_name", defaultValue = "") String clientName,
	//			@RequestParam(name = "page", defaultValue = "1") int page,
	//			Model model, RedirectAttributes attr) {
	//
	//		// エラーメッセージを格納する変数をインスタンス化 
	//		String attributeValue = new String();
	//
	//		// ページサイズを定義
	//		int pageSize = 20;
	//		//現在のページとページサイズを引数に設定
	//		List<Map<String, Object>> list = clientService.searchActiveClient(clientId, clientName, page - 1, pageSize);
	//		//返された顧客情報が0件かチェック
	//		if (list.isEmpty()) {
	//			attributeValue = "顧客一覧の検索結果は0件です。条件を変更し、再度検索してください。";
	//			session.setAttribute("client_id", "");
	//			session.setAttribute("client_name", "");
	//			model.addAttribute("result", attributeValue);
	//		} else {
	//			session.setAttribute("client_id", clientId);
	//			session.setAttribute("client_name", clientName);
	//		}
	//		// ここで総データ数を取得するロジックを実装し、総ページ数を計算
	//		int totalClients = clientService.countSearchClient(clientId, clientName);
	//		int totalPages = (int) Math.ceil((double) totalClients / pageSize);
	//		//　現在のページ数
	//		int correctedPageNumber = page + 1;
	//
	//		//スコープに保存
	//		model.addAttribute("client_list", list);
	//		model.addAttribute("currentPage", correctedPageNumber);
	//		model.addAttribute("totalPages", totalPages);
	//		model.addAttribute("searchCheck", true);
	//		model.addAttribute("mode", 2);//一覧(モード2)
	//		return res1;
	//	}
	//
	//	/**
	//	 * SMSCL001 顧客検索画面遷移
	//	 * 顧客検索時の一覧表示に使用
	//	 * 以下の時はエラーを発出
	//	 * ・検索結果が0件の時
	//	 */
	//	@GetMapping("/search")
	//	public String searchClient(@RequestParam(name = "page", defaultValue = "1") int page,
	//			Model model, RedirectAttributes attr) {
	//		// 顧客IDと顧客名を定義
	//		String client_id = "";
	//		String client_name = "";
	//		// エラーメッセージを格納する変数をインスタンス化 
	//		String attributeValue = "";
	//		//　検索時の顧客IDと顧客名をセッションスコープから取り出す
	//		//　例外処理
	//		try {
	//			client_id = session.getAttribute("client_id").toString();
	//			client_name = session.getAttribute("client_name").toString();
	//
	//		} catch (NullPointerException e) {
	//		}
	//
	//		/*ページネーションの実装*/
	//		// ページサイズを設定
	//		int pageSize = 20;
	//		//現在のページとページサイズを引数に設定
	//		List<Map<String, Object>> list = clientService.searchActiveClient(client_id, client_name, page - 1, pageSize);
	//
	//		//空だったらエラーメッセージとセッションスコープに””を入れる
	//		if (list.isEmpty()) {
	//			attributeValue = "顧客マスタ一覧の検索結果は0件です。条件を変更し、再度検索してください。";
	//			model.addAttribute("result", attributeValue);
	//			session.setAttribute("client_id", "");
	//			session.setAttribute("client_name", "");
	//		} else {
	//			model.addAttribute("client_list", list);
	//			session.setAttribute("client_id", client_id);
	//			session.setAttribute("client_name", client_name);
	//		}
	//
	//		// ここで総データ数を取得するロジックを実装し、総ページ数を計算
	//		int totalClients = clientService.countSearchClient(client_id, client_name);
	//		int totalPages = (int) Math.ceil((double) totalClients / pageSize);
	//		//　現在のページ数
	//		int correctedPageNumber = page + 1;
	//
	//		// スコープに保存
	//
	//		model.addAttribute("currentPage", correctedPageNumber);
	//		model.addAttribute("totalPages", totalPages);
	//		model.addAttribute("mode", 2);//一覧(モード2)
	//		return res1;
	//	}

	/**
	 *　SMSCL001　顧客削除処理
	 * ★論理削除時に実行
	 * 以下の時はエラーを発出
	 * ・チェックボックス未選択
	 * ・顧客ID、顧客名が未検出
	 * *排他チェック
	 */
	@PostMapping("/list/delete")
	public String deleteClient(@RequestParam(name = "selectCheck", required = false) String[] selectCheck,
			RedirectAttributes attr) {

		// 画面遷移先を顧客情報一覧画面へのリダイレクトに指定
		String res = "redirect:/client/list";

		// エラーメッセージを格納する変数をインスタンス化 
		String attributeValue = "";
		/*排他チェック*/
		// 1．削除件数が１行以上か判別
		if (selectCheck != null) {
			//削除件数を格納する変数
			int deleteResult = 0;
			for (String clientId : selectCheck) {
				// 2.排他チェック（削除）
				if (exclusiveCheck.ExclusiveCheckDalete(clientId, tableNumber)) {
					String result = "該当データはすでに削除されています。";
					attr.addFlashAttribute("result", result);
					return res;
				}
				/*3.排他チェック（編集中）*/
				if (exclusiveCheck.ExclusiveCheckEdited(clientId, loginUserId, editTableName)) {
					String result = "該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。";
					attr.addFlashAttribute("result", result);
					//					/*4.排他チェック（編集中後）*/
					//					exclusiveCheck.ExclusiveLockDalete(clientId, loginUserId, tableNumber);
					return res;
				}
				/*7,論理削除実行*/
				deleteResult += clientService.deleteClient(clientId);
				attributeValue = deleteResult + "件削除しました。";
				attr.addFlashAttribute("result", attributeValue);
				/*5.排他チェック（編集中後）*/
				exclusiveCheck.ExclusiveLockDalete(clientId, loginUserId, tableNumber);
			}
		} else {
			/*6.削除件数が0件の場合*/
			attributeValue = messages.getMessage("COM01W003", null, Locale.JAPAN);
			attr.addFlashAttribute("result", attributeValue);
			return res;
		}
		return res;
	}

	/**
	 * SMSCL002 登録・編集画面遷移
	 * 編集画面の場合、以前に登録した内容を表示
	 */
	@GetMapping("/regist")
	public String showRegistrationForm(@ModelAttribute ClientData clientData, Model model) {
		/* 新規登録モードの画面表示指示 */
		model.addAttribute("editJudge", false);
		return res2;
	}

	/**
	 * * SMSCL002 顧客新規登録内容DB登録
	 */
	@PostMapping("/regist")
	public String registClient(@RequestParam(name = "buttonType", defaultValue = "") String buttonType,
			@Validated @ModelAttribute ClientData clientData,
			BindingResult bindingResult, RedirectAttributes attr, Model model) {
		// 画面遷移先を編集画面に指定
		String res = "redirect:/client/list";

		System.out.println(clientData.getRest3_start());
		//エラー文の初期化
		attributeValue = "";
		attributeValue2 = "";
		attributeValue3 = "";
		//新規登録モードの画面表示指示
		model.addAttribute("editJudge", false);

		// 必須チェック、フォーマットチェック、重複チェック
		if (registCheck(clientData, bindingResult, buttonType) == false) {
			model.addAttribute("clientData", clientData);// 登録情報の表示
			model.addAttribute("error", attributeValue);
			model.addAttribute("error2", attributeValue2);
			model.addAttribute("error3", attributeValue3);
			return "SMSCL002";
		}
		//登録を実行
		clientService.registClient(clientData);
		System.out.println("登録完了");
		// ボタンタイプにより遷移先を判別
		// 結果（true：新規作成へ、false：一覧表示へ）
		if ("registNext".equals(buttonType)) {
			//登録画面へリダイレクト
			res = "redirect:/client/regist";
		}
		return res;
	}

	/**
	 * * SMSCL002 登録・編集画面遷移
	 * 編集画面の場合、以前に登録した内容を表示
	 */
	@GetMapping("/edit")
	public String showEditForm(
			//@RequestParam(name = "id") String client_id,
			@ModelAttribute ClientData clientData,
			@RequestParam(name = "id", defaultValue = "") String clientId,
			Model model, RedirectAttributes attr) {

		/*排他チェック（削除）*/
		if (exclusiveCheck.ExclusiveCheckDalete(clientId, tableNumber)) {
			//エラーメッセージの指定
			String result = "該当データはすでに削除されています。";
			//エラーメッセージをスコープに保存
			attr.addFlashAttribute("error", result);
			//一覧画面へリダイレクト
			return "redirect:/client/list";
		}
		/*排他チェック（編集）*/
		if (exclusiveCheck.ExclusiveCheckEdited(clientId, loginUserId, editTableName)) {
			//エラーメッセージの指定
			String result = "該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。";
			//エラーメッセージをスコープに保存
			attr.addFlashAttribute("result", result);
			// 一覧画面へリダイレクト
			return "redirect:/client/list";
		}
		// ClientDataにセットされている情報を検索
		ClientData editData = clientService.searchEditClient(clientId);
		// スコープに保存
		model.addAttribute("editJudge", true);//　編集モード
		model.addAttribute("client_list", editData);// 登録情報の表示
		return res2;
	}

	/**
	 * SMSCL002 顧客編集登録内容DB登録
	 */
	@PostMapping("/edit")
	public String editClient(@RequestParam(name = "client_id", defaultValue = "") String clientId,
			@RequestParam(name = "buttonType", defaultValue = "") String buttonType,
			@Validated @ModelAttribute ClientData clientData,
			BindingResult bindingResult, RedirectAttributes attr, Model model) {
		// 画面遷移先を一覧表示画面に指定
		String res = "redirect:/client/list";
		//エラー文の初期化
		attributeValue3 = "";
		//スコープを保存
		model.addAttribute("editJudge", true);//編集モード
		/* 必須チェック、フォーマットチェック*/
		if (registCheck(clientData, bindingResult, buttonType) == false) {
			// スコープに保存
			model.addAttribute("client_list", clientData);//登録情報を表示	
			model.addAttribute("error3", attributeValue3);
			return "SMSCL002";
		}
		/*排他チェック（削除）*/
		if (exclusiveCheck.ExclusiveCheckDalete(clientId, tableNumber)) {
			//エラーメッセージの指定
			String result = "該当データはすでに削除されています。";
			// スコープに保存
			model.addAttribute("error", result);
			model.addAttribute("client_list", clientData);//登録情報を表示		
			/*一覧画面へリダイレクト*/
			return "SMSCL002";
		}
		/*排他チェック（編集）*/
		if (exclusiveCheck.ExclusiveCheckEdited(clientId, loginUserId, editTableName)) {
			//エラーメッセージの指定
			String result = "該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。";
			//エラーメッセージをスコープに保存
			attr.addFlashAttribute("error", result);
			// スコープに保存
			model.addAttribute("client_list", clientData);//登録情報を表示		
			return "SMSCL002";
		}
		// 編集を実行
		clientService.editClient(clientData);
		System.out.println("更新完了");
		/*排他チェック（編集中ロック解除）*/
		exclusiveCheck.ExclusiveLockDalete(clientId, loginUserId, tableNumber);
		// スコープに保存
		model.addAttribute("client_list", clientData);//登録情報を表示		

		return res;
	}

	/**
	 * SMSCL002 顧客削除処理（編集中）
	 * ★論理削除時に実行
	 */
	@PostMapping("/edit/delete")
	public String deleteEdit(@RequestParam(name = "client_id", defaultValue = "") String clientId,
			Model model, RedirectAttributes attr) {
		// 画面遷移先を編集画面に指定
		String res = "redirect:/client/list";

		/*排他チェック（削除）*/
		if (exclusiveCheck.ExclusiveCheckDalete(clientId, tableNumber)) {

			String result = "該当データはすでに削除されています。";
			attr.addFlashAttribute("result", result);
			return res;
		}

		/*排他チェック（編集）*/
		if (exclusiveCheck.ExclusiveCheckEdited(clientId, loginUserId, editTableName)) {
			String result = "該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。";
			attr.addFlashAttribute("result", result);
			return res;
		}

		/*排他チェック（編集中ロック解除）*/
		exclusiveCheck.ExclusiveLockDalete(clientId, loginUserId, tableNumber);

		int result = clientService.deleteClient(clientId);
		attr.addFlashAttribute("result", result + "件削除しました。");

		return res;
	}

	//キャンセルボタン押下
	@PostMapping("/cancel")
	public String userCancel(@RequestParam(name = "editJudge", defaultValue = "") String isEditJudge,
			@RequestParam(name = "client_id", defaultValue = "") String clientId) {

		//編集モードの時だけ
		if (isEditJudge.equals("true")) {
			//排他チェック(編集済)
			exclusiveCheck.ExclusiveLockDalete(clientId, loginUserId, tableNumber);
		}

		return "redirect:/client/list";
	}

	/**
	 * フォーマットチェックで使用するメソッド作成
	 * @param value 検証対象の値
	 * @return 結果（true：時刻、false：時刻ではない）
	 */
	public static boolean timeFormatCheck(String... times) {
		// 時間のフォーマットチェック
		Pattern pattern = Pattern.compile("^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$");
		boolean check = false;
		for (String time : times) {
			if (!time.isEmpty() && !pattern.matcher(time).find()) {
				check = true;
			}
		}
		return check;
	}

	/**
	 * 必須チェック、フォーマットチェック、重複チェックのメソッド作成
	 * @param value 検証対象の値
	 * @return 結果（true：該当項目なし、false:該当項目有り）
	 */
	public boolean registCheck(@Validated @ModelAttribute ClientData clientData,
			BindingResult bindingResult, String buttonType) {
		boolean check = true;

		// 必須項目の未入力＆フォーマットチェック
		if (bindingResult.hasErrors()) {
			check = false;
		}
		// 任意項目のフォーマットチェック
		else if (timeFormatCheck(clientData.getRest2_start(), clientData.getRest2_end(),
				clientData.getRest3_start(), clientData.getRest3_end(),
				clientData.getRest4_start(), clientData.getRest4_end(),
				clientData.getRest5_start(), clientData.getRest5_end(),
				clientData.getRest6_start(), clientData.getRest6_end(),
				clientData.getAdjust_rest_time_start(), clientData.getAdjust_rest_time_end())) {
			// エラーメッセージを格納する変数をインスタンス化 
			attributeValue3 = "時間(13:00など)で入力してください。";
			check = false;
		}

		// 新規登録時、顧客IDと顧客名の重複チェック
		// ボタンタイプで新規作成か更新かを判別し、新規作成の場合に起動
		if ("regist".equals(buttonType)) {
			if (!clientData.getClient_id().isEmpty() ||
					!clientData.getClient_name().isEmpty()) {
				// 顧客IDチェック
				if (!clientService.searchClient(clientData.getClient_id()).isEmpty()) {
					attributeValue = messages.getMessage("COM01E011",
							new Object[] { "顧客ID", clientData.getClient_id(), "顧客" }, Locale.JAPAN);
					check = false;
				}
				// 顧客名チェック
				if (!clientService.searchClientName(clientData.getClient_name()).isEmpty()) {
					attributeValue2 = messages.getMessage("COM01E011",
							new Object[] { "顧客名", clientData.getClient_name(), "顧客" }, Locale.JAPAN);
					check = false;
				}
			}
		}
		return check;
	}

}
