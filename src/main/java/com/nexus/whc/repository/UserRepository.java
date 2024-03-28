/*
 *UserListRepository.java
 * 
 * ユーザマスターー覧及びユーザーマスタ登録画面で使用するユーザー情報の一覧表示・登録・検索・編集・削除に関するSQLを
 * 作成して実行するクラス
 * 
 * @author 長澤 直人
 * @version 
 */
package com.nexus.whc.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/*
 * DBにアクセスする処理が記述されたクラス
 */
@Repository
public class UserRepository {

	/**SpringのテンプレートクラスJdbcTemplateを使用しSQL リレーショナルデータベースと JDBC を簡単に操作できるようにする為の変数**/
	@Autowired
	JdbcTemplate jdbcTemplate;

	/*検索結果の総数を格納する変数*/
	int searchCount;

	public List<Map<String, Object>> searchAll() {
		// SQL文作成
		String sql = "SELECT * from m_user WHERE delete_flg != 1";

		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		return list;
	}
	
	/**
	 * ユーザー一覧表示機能
	 * ユーザー情報から削除フラグが＝1以外を抽出してその全てをListで返すSQLを実行する
	 * @param なし
	 * @return list　削除フラグ=1以外の全ユーザー情報抽出結果のList
	 */
	public List<Map<String, Object>> userList() {

		/*SQL文作成*/
		String sql = "SELECT "
				+ "m_user.user_id, "
				+ "m_user.user_name, "
				+ "m_user.seq_id, "
				+ "m_user.mail_address, "
				+ "m_authority.auth_status "
				+ "FROM m_user "
				+ "LEFT JOIN m_authority ON m_user.auth_id = m_authority.auth_id "
				+ "WHERE m_user.delete_flg != 1 "
				+ "ORDER BY m_user.user_id ASC; ";

		/*クエリを実行*/
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		/*取得したリストを返す*/
		return list;
	}

	/**
	 * ユーザー情報取得機能
	 * ユーザー情報から削除フラグ=1以外かつシーケンスIDで紐づいたユーザー情報をを抽出してMapで返すSQLを実行する
	 * @param seq_id　シーケンスID
	 * @return list 削除フラグ=1以外かつシーケンスIDで紐づいたユーザー情報抽出結果のMap
	 */
	public Map<String, Object> searchUser(String seqId) {

		/*マップ型のインスタンスを生成*/
		Map<String, Object> userMap = new HashMap<>();

		/*新規登録の時はマップの各値に空文字を格納し返す*/
		if (seqId.equals("0")) {

			userMap.put("seq_id", "0");
			userMap.put("user_id", "");
			userMap.put("user_name", "");
			userMap.put("auth_id", "");
			userMap.put("mail_address", "");

			return userMap;
		}

		/*SQL文の作成*/
		String sql = "SELECT * FROM m_user WHERE delete_flg != 1 AND seq_id = ?";

		/*?の箇所を置換するデータの配列を定義*/
		Object[] param = { seqId };

		/*クエリを実行*/
		userMap = jdbcTemplate.queryForMap(sql, param);
		/*取得したマップを返す*/
		return userMap;
	}

	/**
	 * ユーザ検索機能
	 * @param userId	入力された検索内容
	 * @param user_name　入力された検索内容
	 * @param auth_status　入力された検索内容
	 * @param mail_address　入力された検索内容
	 * @return searchUserList 検査k条件に該当するユーザーの情報を格納
	 */
	public List<Map<String, Object>> searchActiveUserList(String userId, String userName, String authStatus,
			String mailAddress, int offset, int pageSize) {

		//SQL文の作成
		String sql = "SELECT "
				+ "m_user.user_id,"
				+ "m_user.user_name,"
				+ "m_user.seq_id,"
				+ "m_user.mail_address,"
				+ "m_user.auth_id ,"
				+ "m_authority.auth_status "
				+ "FROM m_user "
				+ "LEFT JOIN m_authority "
				+ "ON m_user.auth_id =  m_authority.auth_id "
				+ "WHERE m_user.delete_flg != 1 ";

		/*入力された検索内容をList型変数に格納する*/
		List<Object> paramList = new ArrayList<>();

		/*検索内容の判別*/
		if (!userId.equals("")) {
			sql = sql + " AND user_id=? ";
			paramList.add(userId);
		}
		if (!userName.equals("")) {
			sql += "AND m_user.user_name LIKE ?";
			paramList.add("%" + userName + "%");
		}
		if (!mailAddress.equals("")) {
			sql += "AND m_user.mail_address LIKE ?";
			paramList.add("%" + mailAddress + "%");
		}
		if (!authStatus.equals("all")) {
			sql = sql + " AND m_user.auth_id=? ";

			if (authStatus.equals("admin")) {
				authStatus = "0";
			}
			if (authStatus.equals("user")) {
				authStatus = "1";
			}
			paramList.add(authStatus);
		}

		/*?の箇所を置換するデータの配列を定義*/
		Object[] param = paramList.toArray();

		/*検索時のユーザーの総数を格納*/
		searchCount = jdbcTemplate.queryForList(sql, param).size();
		System.out.println(searchCount);
		/* ORDER BY 句を追加 */
		sql += " ORDER BY m_user.seq_id ASC LIMIT ? OFFSET ?";
		paramList.add(pageSize);
		paramList.add(offset);

		/*?の箇所を置換するデータの配列を再定義*/
		param = paramList.toArray();

		/*クエリを実行*/
		List<Map<String, Object>> searchUserList = jdbcTemplate.queryForList(sql, param);
		System.out.println(searchUserList);
		/*取得したリストを返す*/
		return searchUserList;
	}

	/**
	 * ユーザー削除機能
	 * @param selectCheck　入力されたチェックボックス内の格納された値
	 * @return result 削除件数を格納
	 */
	public int daleteUser(String seqId) {

		int result = 0;

		/*選択されたチェックボックス内の要素であるシーケンスIDを取得*/
		

			//SQL文の作成
			String sql = "UPDATE m_user SET delete_flg = 1 WHERE seq_id =? ";

			/*?の箇所を置換するデータの配列を定義*/
			Object[] param = { seqId };

			/*クエリを実行*/
			result += jdbcTemplate.update(sql, param);
		

		/*削除件数を返す*/
		return result;
	}

	/**
	 * ユーザー新規登録機能
	 * @param user_id　新規登録するユーザーID
	 * @param user_name　新規登録するユーザー名
	 * @param randomPass　サービスで生成されたランダムなPW
	 * @param auth_id　新規登録する権限
	 * @param mailaddress　新規登録するメールアドレス
	 * @return　
	 */
	public void createUser(String userId, String userName, String randomPass, String authId, String mailAddress) {

		/*シーケンスIDの最大値＋１を計算*/
		/*SQL文の作成*/
		String getMaxSeqId = "SELECT IFNULL(MAX(seq_id), 0) + 1 FROM  m_user";

		/*クエリを実行*/
		long seqId = jdbcTemplate.queryForObject(getMaxSeqId, Long.class);

		/*SQL文の作成*/
		String sql = "INSERT INTO m_user VALUES (?,?,?,?,?,?,?,NOW(),?,NOW(),?)";

		/*?の箇所を置換するデータの配列を定義*/
		Object[] param = { seqId, userId, userName, randomPass, authId, mailAddress, 0, "nexus001",
				"nexus001" };

		/*クエリを実行*/
		jdbcTemplate.update(sql, param);
	}

	/**
	 * ユーザー情報更新機能
	 * @param seq_id　更新はしないが必要なので仕方なく持ってきたシーケンスID
	 * @param user_id　更新はしないが必要なので仕方なく持ってきたユーザー名
	 * @param user_name　更新するユーザー名
	 * @param auth_id 更新する権限
	 * @param mailaddress　更新するメールアドレス
	 */
	public void updateUser(String seq_id, String user_id, String user_name, String auth_id, String mailaddress) {
		
		//SQL文を作成
		String sql = "UPDATE m_user SET user_id = ? , user_name = ? ,auth_id = ? ,mail_address = ? "
				+ "WHERE seq_id= ? ;";
		
		/*?の箇所を置換するデータの配列を定義*/
		Object[] params = { user_id, user_name, auth_id, mailaddress, seq_id };
		
		//クエリを実行
		jdbcTemplate.update(sql, params);
	}

	/**
	 * ページネーション
	 * 引数にページネーションで使用するオフセット数とページサイズを設定
	 * delete_flg=falseの社員情報を抽出するSQLを実行する。
	 * @return list 抽出結果のlist
	 */
	public List<Map<String, Object>> searchActive(int offset, int pageSize) {

		/*SQL文作成*/
		String sql = "SELECT "
				+ "m_user.user_id, "
				+ "m_user.user_name, "
				+ "m_user.seq_id, "
				+ "m_user.mail_address, "
				+ "m_authority.auth_status "
				+ "FROM m_user "
				+ "LEFT JOIN m_authority ON m_user.auth_id = m_authority.auth_id "
				+ "WHERE m_user.delete_flg != 1 "
				+ "ORDER BY m_user.user_id ASC "
				+ "LIMIT ? OFFSET ?";

		/*クエリを実行*/
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, pageSize, offset);
		System.out.println(list);
		/*取得したリストを返す*/
		return list;
	}
	/**
	 * 検索した際の総数を引き出すためのメソッド
	 * @return　SEARCH_COUNT　検索結果の総数を保持する変数
	 */
	public int searchCount() {

		return searchCount;

	}

}
