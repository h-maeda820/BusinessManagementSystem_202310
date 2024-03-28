/*
 * UserController.java
 * 
 * ユーザ情報管理機能に関するアプリケーション制御を行う為のコントローラー
 *
 * @author 長澤 直人
 * @version 1.0
 */
package com.nexus.whc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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

import com.nexus.whc.services.ExclusiveCheckService;
import com.nexus.whc.services.UserServices;

/**
 * ユーザ情報管理機能に関するアプリケーション制御を行う為のコントローラークラス
 **/
@Controller
@RequestMapping("/user")
public class UserController {

	/*サービスクラスであるUserListServicesの中のオブジェクトを呼び出すための変数*/
	@Autowired
	UserServices userServices;

	/*セッションを扱う変数*/
	@Autowired
	HttpSession session;

	/*排他チェックサービスクラスであるExclusiveCheckServiceの中のオブジェクトを呼び出すための変数*/
	@Autowired
	ExclusiveCheckService exclusiveCheck;

	/*
	 * 排他チェック用のクラス変数
	 */

	//排他チェックで使用するテーブル判別用変数
	final int TABLE_NUMBER = 1;

	//排他チェックで使用するユーザー判別用変数
	final String LOGIN_USER_ID = "nexus001";

	/*
	 * 画面遷移用のクラス変数
	 */

	//	ユーザー一覧画面
	final String LIST_PAGE = "SMSUS001";

	//	ユーザー一覧画面（リダイレクト）
	final String REDIRECT_LIST_PAGE = "redirect:/user/list";

	//	ユーザー登録画面
	final String REGIST_PAGE = "SMSUS002";

	//	ユーザー登録画面（リダイレクト）
	final String REDIRECT_SEQ_ID = "redirect:/user/regist?seq_id=";

	//	ユーザー登録画面（リダイレクト）
	final String REDIRECT_SEQ_ID_0 = "redirect:/user/regist?seq_id=0";

	/**
	 * リスト
	 * @param userId
	 * @param userName
	 * @param authStatus
	 * @param mailAddress
	 * @param pageNumber
	 * @param model
	 * @return
	 */
	@GetMapping("/list")
	public String userList(@ModelAttribute("user_id") String userId,
			@ModelAttribute("user_name") String userName,
			@ModelAttribute("permission") String authStatus,
			@ModelAttribute("mail_address") String mailAddress,
			@RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
			Model model) {

		int pageSize = 20;

		if (authStatus.equals("")) {
			authStatus = "all";
		}

		/*入力された情報を検索するSQLを実行*/
		List<Map<String, Object>> searchActiveUserList = userServices.searchActiveUserList(userId, userName,
				authStatus,
				mailAddress, pageNumber - 1, pageSize);

		/*もし検索結果が0件だったらエラーメッセージをスコープに保存*/
		if (searchActiveUserList.isEmpty()) {
			List<Map<String, Object>> listAll = userServices.searchAll();
			if (listAll.isEmpty()) {
				model.addAttribute("result", "ユーザー一覧の検索結果は0件です。条件を変更し、再度検索してください。");
			}
		}

		model.addAttribute("user_id", userId);
		model.addAttribute("user_name", userName);
		model.addAttribute("permission", authStatus);
		model.addAttribute("mail_address", mailAddress);
		model.addAttribute("list", searchActiveUserList);

		session.setAttribute("user_id", userId);
		session.setAttribute("user_name", userName);
		session.setAttribute("permission", authStatus);
		session.setAttribute("mail_address", mailAddress);

		//ページネーション用
		//アクティブなデータの総数
		int totalClient = userServices.searchCount();

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

		return LIST_PAGE;
	}

	/**
	 * 検索
	 * @param pageNumber
	 * @param userId
	 * @param userName
	 * @param authStatus
	 * @param mailAddress
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("search")
	public String userList(@RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
			@RequestParam(name = "user_id", required = false) String userId,
			@RequestParam(name = "user_name", required = false) String userName,
			@RequestParam(name = "permission", required = false) String authStatus,
			@RequestParam(name = "mail_address", required = false) String mailAddress,
			RedirectAttributes redirectAttributes) {

		redirectAttributes.addFlashAttribute("user_id", userId);
		redirectAttributes.addFlashAttribute("user_name", userName);
		redirectAttributes.addFlashAttribute("permission", authStatus);
		redirectAttributes.addFlashAttribute("mail_address", mailAddress);

		return REDIRECT_LIST_PAGE + "?pageNumber=" + pageNumber;
	}

	/**
	 * ページネーション
	 * @param pageNumber
	 * @param redirectAttributes
	 * @return
	 */
	@GetMapping("/pageList")
	public String pageListuserGet(@RequestParam(name = "pageNumber", defaultValue = "1") String pageNumber,
			RedirectAttributes redirectAttributes) {

		String userId = session.getAttribute("user_id").toString();
		String userName = session.getAttribute("user_name").toString();
		String authStatus = session.getAttribute("permission").toString();
		String mailAddress = session.getAttribute("mail_address").toString();

		redirectAttributes.addFlashAttribute("user_id", userId);
		redirectAttributes.addFlashAttribute("user_name", userName);
		redirectAttributes.addFlashAttribute("permission", authStatus);
		redirectAttributes.addFlashAttribute("mail_address", mailAddress);

		return REDIRECT_LIST_PAGE + "?pageNumber=" + pageNumber;
	}

	//	/**
	//	 * サービスから一覧の情報を受け取り画面に表示させるメソッド
	//	 * @param model　スコープに保存する為の引数
	//	 * @param page 現在のページ数を受け取る引数
	//	 * @return res　HTMLファイル名を格納したString型変数
	//	 */
	//	@GetMapping("/list")
	//	public String userList(@RequestParam(defaultValue = "1") int page, Model model) {
	//
	//		/*
	//		 * ︎ページネーションの実装
	//		 */
	//
	//		/*ページサイズを設定*/
	//		int pageSize = 20;
	//
	//		/*現在のページとページサイズを引数に設定*/
	//		List<Map<String, Object>> list = userServices.searchActive(page - 1, pageSize);
	//
	//		/*ここで総データ数を取得するロジックを実装し、総ページ数を計算*/
	//		int totalClients = userServices.userList().size();
	//		int totalPages = (int) Math.ceil((double) totalClients / pageSize);
	//		int pageNumber = page + 1;
	//
	//		/*スコープに保存*/
	//		model.addAttribute("list", list);
	//		model.addAttribute("currentPage", pageNumber);
	//		model.addAttribute("totalPages", totalPages);
	//
	//		model.addAttribute("mode", 1);//一覧(モード1)
	//
	//		/*一覧画面へ遷移*/
	//		return LIST_PAGE;
	//	}
	//
	//	/**
	//	 * 
	//	 * 画面からユーザーの情報を受け取り検索を行い結果を表示させるメソッド
	//	 * 
	//	 * @param userId ユーザー情報のuser_idを格納した変数
	//	 * @param user_name　ユーザー情報のuser_nameを格納した変数
	//	 * @param auth_id  ユーザー情報のauth_idを格納した変数
	//	 * @param mailaddress ユーザー情報のmailaddressを格納した変数
	//	 * @param model　スコープに保存する為の引数
	//	 * 
	//	 * @return listPage　HTMLファイル名を格納したString型変数
	//	 * 
	//	 */
	//	@PostMapping("search")
	//	public String userList(@RequestParam(defaultValue = "1") int page,
	//			@RequestParam(name = "user_id", required = false) String userId,
	//			@RequestParam(name = "user_name", required = false) String userName,
	//			@RequestParam(name = "permission", required = false) String authStatus,
	//			@RequestParam(name = "mail_address", required = false) String mailAddress,
	//			Model model) {
	//
	//		/*ページサイズを設定*/
	//		int pageSize = 20;
	//
	//		/*入力された情報を検索するSQLを実行*/
	//		List<Map<String, Object>> searchActiveUserList = userServices.searchActiveUserList(userId, userName,
	//				authStatus,
	//				mailAddress, page - 1, pageSize);
	//
	//		/*もし検索結果が0件だったらエラーメッセージをスコープに保存*/
	//		if (searchActiveUserList.isEmpty()) {
	//
	//			model.addAttribute("result", "ユーザー一覧の検索結果は0件です。条件を変更し、再度検索してください。");
	//			session.setAttribute("user_id", "");
	//			session.setAttribute("client_name", "");
	//			session.setAttribute("user_name", "");
	//			session.setAttribute("permission", "auth_status");
	//			session.setAttribute("mail_address", "");
	//
	//		}
	//
	//		model.addAttribute("list", searchActiveUserList);
	//		session.setAttribute("user_id", userId);
	//		session.setAttribute("user_name", userName);
	//		session.setAttribute("permission", authStatus);
	//		session.setAttribute("mail_address", mailAddress);
	//
	//		/*ここで総データ数を取得するロジックを実装し、総ページ数を計算*/
	//		int totalUsers = userServices.searchCount();
	//		int totalPages = (int) Math.ceil((double) totalUsers / pageSize);
	//
	//		int pageNumber = page + 1;
	//
	//		model.addAttribute("currentPage", pageNumber);
	//		model.addAttribute("totalPages", totalPages);
	//
	//		model.addAttribute("mode", 2);//一覧(モード2)
	//
	//		/*一覧画面へ遷移*/
	//		return LIST_PAGE;
	//	}
	//
	//	@GetMapping("/search")
	//	public String searchCalendar(@RequestParam(name = "page", defaultValue = "1") int page, Model model) {
	//
	//		String userId = "";
	//		String userName = "";
	//		String permission = "";
	//		String mailAddress = "";
	//
	//		try {
	//			userId = session.getAttribute("user_id").toString();
	//			userName = session.getAttribute("user_name").toString();
	//			permission = session.getAttribute("permission").toString();
	//			mailAddress = session.getAttribute("mail_address").toString();
	//
	//		} catch (NullPointerException e) {
	//
	//		}
	//
	//		/*ページサイズを設定*/
	//		int pageSize = 20;
	//
	//		/*入力された情報を検索するSQLを実行*/
	//		List<Map<String, Object>> searchActiveUserList = userServices.searchActiveUserList(userId, userName,
	//				permission, mailAddress, page - 1, pageSize);
	//
	//		/*もし検索結果が0件だったらエラーメッセージをスコープに保存*/
	//		if (searchActiveUserList.isEmpty()) {
	//
	//			model.addAttribute("result", "ユーザー一覧の検索結果は0件です。条件を変更し、再度検索してください。");
	//
	//			session.setAttribute("user_id", "");
	//			session.setAttribute("client_name", "");
	//			session.setAttribute("user_name", "");
	//			session.setAttribute("permission", "all");
	//			session.setAttribute("mail_address", "");
	//		} else {
	//
	//			model.addAttribute("list", searchActiveUserList);
	//
	//			session.setAttribute("user_id", userId);
	//			session.setAttribute("user_name", userName);
	//			session.setAttribute("permission", permission);
	//			session.setAttribute("mail_address", mailAddress);
	//		}
	//
	//		/*ここで総データ数を取得するロジックを実装し、総ページ数を計算*/
	//		int totalUsers = userServices.searchCount();
	//		int totalPages = (int) Math.ceil((double) totalUsers / pageSize);
	//
	//		int pageNumber = page + 1;
	//
	//		model.addAttribute("currentPage", pageNumber);
	//		model.addAttribute("totalPages", totalPages);
	//
	//		model.addAttribute("mode", 2);//一覧(モード2)
	//
	//		/*一覧画面へ遷移*/
	//		return LIST_PAGE;
	//	}

	/**
	 * 画面からユーザーの情報を受け取り受け取った情報を元に論理削除するメソッド
	 * 
	 * @param selectCheck 一覧で選択したユーザー情報のシーケンスidを格納した変数
	 * @param daleteAction 編集画面から受け取ったシーケンスID
	 * @param attr 削除した後リダイレクトでListを表示させる為の変数
	 * @return res　HTMLファイル名を格納したString型変数
	 * 
	 */
	@PostMapping("dalete")
	public String daleteUser(@RequestParam(name = "selectCheck", required = false) String[] selectCheck,
			@RequestParam(name = "action", required = false) String[] daleteAction,
			RedirectAttributes attr) {

		/*選択行削除か編集画面からの削除かを判別*/
		if (daleteAction != null && selectCheck == null) {

			String seqId = daleteAction[0];

			/*排他チェック（削除）*/
			if (exclusiveCheck.ExclusiveCheckDalete(seqId, TABLE_NUMBER)) {

				String result = "該当データはすでに削除されています。";

				/*エラーメッセージをスコープに保存*/
				attr.addFlashAttribute("result", result);

				/*一覧画面へリダイレクト*/
				return REDIRECT_LIST_PAGE;

			}

			/*選択したユーザー情報削除*/
			int result = userServices.daleteUser(seqId);
			attr.addFlashAttribute("result", result + "件削除しました。");

			/*排他チェック（編集中後）*/
			exclusiveCheck.ExclusiveLockDalete(seqId, LOGIN_USER_ID, TABLE_NUMBER);

		} else if (selectCheck != null && daleteAction == null) {

			for (String seq_id : selectCheck) {

				/*排他チェック（削除）*/
				if (exclusiveCheck.ExclusiveCheckDalete(seq_id, TABLE_NUMBER)) {

					String result = "該当データはすでに削除されています。";

					/*エラーメッセージをスコープに保存*/
					attr.addFlashAttribute("result", result);

					/*一覧画面へリダイレクト*/
					return REDIRECT_LIST_PAGE;
				}

				/*排他チェック（編集中）*/
				if (exclusiveCheck.ExclusiveCheckEdited(seq_id, LOGIN_USER_ID, TABLE_NUMBER)) {

					String result = "該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。";

					/*エラーメッセージをスコープに保存*/
					attr.addFlashAttribute("result", result);

					/*排他チェック（編集中後）*/
					exclusiveCheck.ExclusiveLockDalete(seq_id, LOGIN_USER_ID, TABLE_NUMBER);

					/*一覧画面へリダイレクト*/
					return REDIRECT_LIST_PAGE;
				}

				/*選択したユーザー情報削除*/
				int result = userServices.daleteUser(seq_id);
				attr.addFlashAttribute("result", result + "件削除しました。");

				/*排他チェック（編集中後）*/
				exclusiveCheck.ExclusiveLockDalete(seq_id, LOGIN_USER_ID, TABLE_NUMBER);
			}
		} else {

			/*削除件数が0件の場合*/
			String result = "対象が選択されていません。対象を選択してください。";

			/*エラーメッセージをスコープに保存*/
			attr.addFlashAttribute("result", result);
		}

		/*一覧画面へリダイレクト*/
		return REDIRECT_LIST_PAGE;
	}

	/**
	 * 特定ユーザーの情報を受け取り画面に表示させるメソッド
	 * @param model スコープに保存する為の引数
	 * @param seqId シーケンスIDで紐づいたユーザーの情報を引き出すための引数
	 * @return res HTMLファイル名を格納したString型変数
	 */
	@GetMapping("regist")
	public String UserCreate(@RequestParam(name = "seq_id") String seqId, Model model, RedirectAttributes attr) {

		/*シーケンスIDが0で新規登録モードの場合*/
		if (seqId.equals("0")) {

			/*マップの要素が全て空文字のマップを受け取る*/
			Map<String, Object> userMap = userServices.searchUser(seqId);

			/*シーケンスIDとマップをスコープに保存*/
			model.addAttribute("seq_id", seqId);
			model.addAttribute("userMap", userMap);

			/*登録画面へ遷移*/
			return REGIST_PAGE;
		}

		/*排他チェック（削除）*/
		if (exclusiveCheck.ExclusiveCheckDalete(seqId, TABLE_NUMBER)) {

			String result = "該当データはすでに削除されています。";

			/*エラーメッセージをスコープに保存*/
			attr.addFlashAttribute("result", result);

			/*一覧画面へリダイレクト*/
			return REDIRECT_LIST_PAGE;
		}

		/*排他チェック（編集中）*/
		if (exclusiveCheck.ExclusiveCheckEdited(seqId, LOGIN_USER_ID, TABLE_NUMBER)) {

			String result = "該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。";

			/*エラーメッセージをスコープに保存*/
			attr.addFlashAttribute("result", result);

			/*排他チェック（編集中後）*/
			exclusiveCheck.ExclusiveLockDalete(seqId, LOGIN_USER_ID, TABLE_NUMBER);

			/*一覧画面へリダイレクト*/
			return REDIRECT_LIST_PAGE;
		}

		/*シーケンスIDが一致するユーザー情報を取得*/
		Map<String, Object> userMap = userServices.searchUser(seqId);

		/*引数で受け取ったシーケンスIDのユーザー情報をスコープに保存*/
		model.addAttribute("seq_id", seqId);
		model.addAttribute("userMap", userMap);

		/*登録画面へ遷移*/
		return REGIST_PAGE;
	}

	/**
	 * 
	 * 登録画面にて入力された内容をDBに保存するメソッド
	 * @param userId 保存するユーザーID
	 * @param user_name　保存するユーザーネーム
	 * @param auth_status　保存する権限ID
	 * @param mailaddress　保存するメールアドレス
	 * @param model　スコープの保存に使用する変数
	 * @param attr	スコープの保存に使用する変数
	 * @return　リダイレクト先を指定した文字列
	 */
	@PostMapping("regist")
	public String UserCreate(
			@RequestParam(name = "seq_id") String seqId,
			@RequestParam(name = "user_id") String userId,
			@RequestParam(name = "user_name") String userName,
			@RequestParam(name = "permission") String authStatus,
			@RequestParam(name = "mailaddress") String mailAddress,
			@RequestParam(name = "action", required = false) String action,
			RedirectAttributes attr) {

		if (action.equals("cancel")) {

			/*排他チェック（編集中後）*/
			exclusiveCheck.ExclusiveLockDalete(seqId, LOGIN_USER_ID, TABLE_NUMBER);

			/*一覧画面へリダイレクト*/
			return REDIRECT_LIST_PAGE;
		}
		String res = "";

		/*必須チェック*/
		if (isEmptys(userId, userName, authStatus, mailAddress)) {

			String result = needToCheck(userId, userName, authStatus, mailAddress);

			/*エラーメッセージをスコープに保存*/
			attr.addFlashAttribute("Result", result);

			/*画面のリダイレクト先を指定*/
			if ("register".equals(action) || "registerAndNext".equals(action)) {

				res = REDIRECT_SEQ_ID_0;
			} else {

				/*シーケンスIDが一致するユーザー情報を取得*/
				Map<String, Object> userMap = userServices.searchUser(seqId);

				/*引数で受け取ったシーケンスIDのユーザー情報をスコープに保存*/
				attr.addFlashAttribute("seq_id", seqId);
				attr.addFlashAttribute("userMap", userMap);

				res = REDIRECT_SEQ_ID + seqId;
			}
			return res;

		}

		/*フォーマットチェック*/
		if (mailaddressFormatCheck(mailAddress)) {

			String result = "{メールアドレス}(｛nexus@nexus-nt.co.jp}など)で入力してください。";

			/*エラーメッセージをスコープに保存*/
			attr.addFlashAttribute("Result", result);

			/*画面のリダイレクト先を指定*/
			if ("register".equals(action) || "registerAndNext".equals(action)) {

				res = REDIRECT_SEQ_ID_0;
			} else {

				/*シーケンスIDが一致するユーザー情報を取得*/
				Map<String, Object> userMap = userServices.searchUser(seqId);

				/*引数で受け取ったシーケンスIDのユーザー情報をスコープに保存*/
				attr.addFlashAttribute("seq_id", seqId);
				attr.addFlashAttribute("userMap", userMap);

				res = REDIRECT_SEQ_ID + seqId;
			}
			return res;
		}

		/*登録モードチェック*/
		if ("register".equals(action) || "registerAndNext".equals(action)) {

			/*登録モード*/

			/*マスタ存在チェック
			 *リストを取得*/
			List<Map<String, Object>> listUserCheck = userServices.userList();

			/*リスト内の各ユーザー情報をfor文で取り出す*/
			for (Map<String, Object> mapUserCheck : listUserCheck) {

				/*ユーザー情報のuser_idを取り出す*/
				String tableUserId = mapUserCheck.get("user_id").toString();

				/*ユーザー情報のメールアドレスを取り出す*/
				String tableMailAddress = mapUserCheck.get("mail_address").toString();

				/*user_idの重複をチェック*/
				if (userId.equals(tableUserId)) {

					String result = "ユーザーIDに入力した" + tableUserId + "はm_userにすでに存在しています。";

					/*エラーメッセージをスコープに保存*/
					attr.addFlashAttribute("Result", result);

					/*登録画面にリダイレクト*/
					return REDIRECT_SEQ_ID_0;
				}

				/*メールアドレスの重複をチェック*/
				if (mailAddress.equals(tableMailAddress)) {

					String result = "ユーザー情報に入力した" + mailAddress + "はm_userにすでに存在しています。";

					/*エラーメッセージをスコープに保存*/
					attr.addFlashAttribute("Result", result);

					/*登録画面にリダイレクト*/
					return REDIRECT_SEQ_ID_0;
				}

			}

			/*ユーザー新規登録処理*/
			userServices.createUser(userId, userName, authStatus, mailAddress);

			/*排他チェック（編集中後）*/
			exclusiveCheck.ExclusiveLockDalete(seqId, LOGIN_USER_ID, TABLE_NUMBER);

			/*押下したボタンによって画面遷移を変える*/
			if ("register".equals(action)) {

				/*リダイレクト先を代入*/
				res = REDIRECT_LIST_PAGE;
			} else if ("registerAndNext".equals(action)) {

				/*リダイレクト先を代入*/
				res = REDIRECT_SEQ_ID_0;
			}

			/*一覧か登録画面にリダイレクト*/
			return res;

		} else if (action.equals("btn-update")) {

			/*更新モード*/

			/*排他チェック（削除）*/
			if (exclusiveCheck.ExclusiveCheckDalete(seqId, TABLE_NUMBER)) {

				String result = "該当データはすでに削除されています。";

				/*エラーメッセージをスコープに保存*/
				attr.addFlashAttribute("result", result);

				/*一覧画面へリダイレクト*/
				return REDIRECT_LIST_PAGE;
			}

			/*マスタ存在チェック*/
			try {

				/*リストを取得*/
				List<Map<String, Object>> listUserCheck = userServices.userList();

				/*リスト内の各ユーザー情報をfor文で取り出す*/
				for (Map<String, Object> mapUserCheck : listUserCheck) {

					/*ユーザー情報のメールアドレスを取り出す*/
					String tableMailAddress = mapUserCheck.get("mail_address").toString();

					if (mapUserCheck.get("seq_id").toString().equals(seqId)) {
						continue;
					}
					/*メールアドレスをチェック*/
					if (mailAddress.equals(tableMailAddress)) {

						String result = "ユーザー情報に入力した" + mailAddress + "はm_userにすでに存在しています。";

						/*エラーメッセージをスコープに保存*/
						attr.addFlashAttribute("Result", result);

						/*SeqIdをスコープに保存*/
						attr.addFlashAttribute("seq_id", seqId);
						return "redirect:/user/regist?seq_id=" + seqId;
					}
				}
			} catch (Exception e) {
				/*編集内容とDBの情報に重複が無ければ下の処理に移る*/
			}

			/*排他チェック（編集中）*/
			if (exclusiveCheck.ExclusiveCheckEdited(seqId, LOGIN_USER_ID, TABLE_NUMBER)) {

				String result = "該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。";

				/*エラーメッセージをスコープに保存*/
				attr.addFlashAttribute("result", result);

				/*一覧画面へリダイレクト*/
				return REDIRECT_LIST_PAGE;
			}

			/*更新処理*/
			userServices.updateUser(seqId, userId, userName, authStatus, mailAddress);

			/*排他チェック（編集中後）*/
			exclusiveCheck.ExclusiveLockDalete(seqId, LOGIN_USER_ID, TABLE_NUMBER);
		}

		/*一覧画面へリダイレクト*/
		return REDIRECT_LIST_PAGE;

	}

	//キャンセルボタン押下時
	@PostMapping("/cancel")
	public String userCancel(@RequestParam(name = "seq_id", defaultValue = "") String seqId) {

		//編集モードの時だけ
		if (!seqId.equals("0")) {
			//排他チェック(編集済)
			exclusiveCheck.ExclusiveLockDalete(seqId, LOGIN_USER_ID, TABLE_NUMBER);
		}

		return "redirect:/user/list";
	}

	/**
	 * 複数個の文字列の空文字判別を簡略化させるメソッド
	 * @param strings　空文字か判別する引数
	 * @return　if文で使用する正誤値
	 */
	private boolean isEmptys(String... strings) {
		boolean result = false;

		/*配列型として生成された各引数を参照して空文字判別を行う*/
		for (int i = 0; i < strings.length; i++) {

			if (strings[i].isEmpty()) {

				result = true;
			}
		}

		return result;
	}

	/**
	 * 必須チェック用メソッド
	 * @param userId　チェックするユーザーID
	 * @param userName　チェックするユーザー名　
	 * @param authStatus　チェックする権限
	 * @param mailAddress　チェックするメールアドレス
	 * @return
	 */
	private String needToCheck(String userId, String userName, String authStatus, String mailAddress) {
		String result = "{";
		/*必須チェック*/
		if (userId.isEmpty()) {

			result += "ユーザーID、";

		}
		if (userName.isEmpty()) {

			result += "ユーザー名、";

		}
		if (mailAddress.isEmpty()) {
			result += "メールアドレス";
		}
		result += "}は必ず入力してください。";

		return result;
	}

	/**
	 * メールアドレスのフォーマットチェック用メソッド
	 * @param mailaddress　チェックするメールアドレス	
	 * @return　check フォーマットが合っていればfalseを返す
	 */
	private boolean mailaddressFormatCheck(String mailaddress) {

		boolean check;
		/*正規表現パターン*/
		String pattern = "^([a-zA-Z0-9])+([a-zA-Z0-9\\._-])*" +
				"@nexus-nt\\.co\\.jp$";

		Pattern p = Pattern.compile(pattern);

		/*メールアドレスが正しいフォーマットかチェック*/
		if (p.matcher(mailaddress).find()) {

			System.out.println(mailaddress + "はメールアドレスです");
			check = false;
		} else {
			System.out.println(mailaddress + "はメールアドレスではありません");
			check = true;

		}

		return check;
	}

}
