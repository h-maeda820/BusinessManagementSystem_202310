/**
 * UserServices.java
 * ユーザー管理機能で使用するユーザー情報の一覧表示・登録・検索・編集・削除に関する処理を
 * UserRepositoryクラスからUserControllerクラスに提供する
 *
 * @author 長澤 直人
 * @version 
 *
 */
package com.nexus.whc.services;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexus.whc.repository.UserRepository;

/*
 * Servicesクラス
 */
@Service
public class UserServices {
	/*DaoクラスであるUserListDaoの中のオブジェクトを呼び出すための変数*/
	@Autowired
	UserRepository userRepository;

	/**
	 * ユーザー情報から削除フラグが＝1以外を抽出してその全てをListで返す
	 * @param なし
	 * @return list 削除フラグ=1以外の全ユーザー情報抽出結果のList
	 */
	public List<Map<String, Object>> userList() {

		List<Map<String, Object>> list = userRepository.userList();

		/*取得したリストを返す*/
		return list;
	}

	/**
	 * ユーザー情報から削除フラグが＝1以外かつシーケンスIDで紐づいたユーザー情報をを抽出してMapで返す
	 * @param seq_id　シーケンスID
	 * @return list 削除フラグ=1以外かつシーケンスIDで紐づいたユーザー情報のMap
	 */
	public synchronized Map<String, Object> searchUser(String seqId) {

		Map<String, Object> userMap = userRepository.searchUser(seqId);

		/*取得したマップを返す*/
		return userMap;
	}

	/**
	 * 検索条件に合致するアクティブなユーザー情報を返すメソッド
	 * @param user_id　入力された検索内容
	 * @param user_name　入力された検索内容
	 * @param auth_status　入力された検索内容
	 * @param mail_address　入力された検索内容
	 * @param pageNumber　現在のページ
	 * @param pageSize　ページ上限
	 * @return
	 */
	public List<Map<String, Object>> searchActiveUserList(String userId, String userName, String authStatus,
			String mailAddress, int pageNumber, int pageSize) {

		/*オフセット数を定義*/
		int offset = pageNumber * pageSize;

		/*検索内容に合致かつアクティブなユーザーを取得*/
		List<Map<String, Object>> searchUserList = userRepository.searchActiveUserList(userId, userName, authStatus,
				mailAddress, offset, pageSize);

		/*取得したリストを返す*/
		return searchUserList;
	}

	/**
	 * ユーザー情報削除機能
	 * @param selectCheck　選択したユーザーのシーケンスIDを持つ変数
	 */
	public int daleteUser(String seqId) {

		int result = userRepository.daleteUser(seqId);
		return result;
	}

	/**
	 * ユーザー新規登録機能
	 * @param userId　登録するユーザーID
	 * @param userName　登録するユーザー名
	 * @param authStatus 登録する権限情報
	 * @param mailaddress　登録するメールアドレス
	 */
	public void createUser(String userId, String userName, String authStatus, String mailaddress) {
		String auth_id = "";
		if (authStatus.equals("user")) {
			auth_id = "1";
		} else {
			auth_id = "0";
		}

		/*ランダムでPWを生成*/
		//		Random random = new Random();
		//		int randomPass = random.nextInt(10000);

		String password = RandomStringUtils.randomAlphanumeric(12);

		userRepository.createUser(userId, userName, password, auth_id, mailaddress);

	}

	/**
	 * ユーザー情報更新機能
	 * @param seq_id　更新はしないが必要なので仕方なく持ってきたシーケンスID
	 * @param user_id　更新はしないが必要なので仕方なく持ってきたユーザー名
	 * @param user_name　更新するユーザー名
	 * @param authStatus 更新する権限情報
	 * @param mailaddress　更新するメールアドレス
	 */
	public void updateUser(String seqId, String userId, String userName, String authStatus, String mailaddress) {

		/*引数のauthStatusに応じてauthIdに値を代入する*/
		String authId = "";
		if (authStatus.equals("user")) {
			authId = "1";
		} else {
			authId = "0";
		}

		userRepository.updateUser(seqId, userId, userName, authId, mailaddress);
	}

	/**
	 * ページネーション
	 * 引数にページネーションで使用するオフセット数とページサイズを設定
	 * delete_flg=falseのユーザ情報を抽出するSQLを実行する。
	 * @return list 抽出結果のlist
	 */
	public List<Map<String, Object>> searchActive(int pageNumber, int pageSize) {

		/*オフセット数を定義*/
		int offset = pageNumber * pageSize;

		/*クエリを実行*/
		List<Map<String, Object>> list = userRepository.searchActive(offset, pageSize);

		/*取得したリストを返す*/
		return list;
	}

	/**
	 * 検索した際の総数を引き出すためのメソッド
	 * @return　searchCount　検索結果の総数を保持する変数
	 */
	public int searchCount() {

		int searchCount = userRepository.searchCount();

		return searchCount;

	}

}
