package com.nexus.whc.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.nexus.whc.models.ClientData;

/**
 * ClientDao.java
 * 顧客管理機能で使用する顧客情報の登録・検索・編集・削除に関するSQLを
 * 作成して実行するクラス
 *
 * @author 榊原 孝司
 * @version 
 *
 */

@Repository
public class ClientRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	/**
	 * 顧客情報を全件取得してListで返すSQLを実行する
	 * @param なし
	 * @return list 全件取得結果のList
	 */
	public List<Map<String, Object>> searchAll() {

		// SQL文作成
		String sql = "SELECT * from m_client WHERE delete_flg != 1";

		// クエリを実行
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		// 取得したリストを返す
		return list;
	}

	/**
	 * 顧客一覧表示機能
	 * 顧客情報の削除フラグ=FALSE以外を抽出してListで返すSQLを実行する
	 * 引数にページネーションで使用するオフセット数とページサイズを設定
	 * @param offset　オフセット数
	 * @param pageSize　ページ数
	 * @return list 削除フラグ=FALSE以外の顧客情報抽出結果のList
	 */
	public List<Map<String, Object>> searchActive(int offset, int pageSize) {

		// SQL文作成
		String sql = "SELECT * FROM m_client "
				+ "WHERE delete_flg != 1 "
				+ "LIMIT ? OFFSET ? ";
		// クエリを実行
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, pageSize, offset);
		// 取得したリストを返す
		return list;
	}

	/**
	 * 顧客件数を全件を取得してresultで返すSQLを実行する
	 * ページネーションで使用
	 * @param なし
	 * @return result 登録件数
	 */
	public int countClient() {

		// 実行結果
		int result = 0;

		// SQL文作成
		String sql = "SELECT COUNT(*) FROM m_client WHERE delete_flg != 1";

		// クエリを実行
		result += jdbcTemplate.queryForObject(sql, Integer.class);

		// 取得した件数を返す
		return result;
	}

	/**
	 * 顧客件数の検索条件に合致している件数を取得してresultで返すSQLを実行する
	 * ページネーションで使用
	 * @param clientId　顧客ID
	 * @param clientName 顧客名
	 * @return result 登録件数
	 */
	public int countSearchClient(String clientId, String clientName) {

		// SQL文作成
		String sql = "SELECT m_client.client_id,m_client.client_name"
				+ " FROM m_client WHERE delete_flg != 1";

		/*SQLの条件部分を構築するためのパラメータリスト*/
		List<Object> paramList = new ArrayList<>();

		if (!clientId.isEmpty() || !clientName.isEmpty()) {

			if (!clientId.isEmpty() && !clientName.isEmpty()) {
				sql += " AND client_id = ? AND client_name LIKE ? ";
				paramList.add(clientId);
				paramList.add("%" + clientName + "%");
			} else if (!clientId.isEmpty()) {
				sql += " AND client_id = ? ";
				paramList.add(clientId);
			} else if (!clientName.isEmpty()) {
				sql += " AND client_name LIKE ? ";
				paramList.add("%" + clientName + "%");
			}
		}

		/*パラメータリストを配列に変換*/
		Object[] param = paramList.toArray();
		/*クエリを実行*/
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, param);
		// listをint件数に変換
		int result = list.size();
		/* 取得したデータを返す*/
		return result;

	}

	/**
	 * 指定された顧客情報を抽出するSQLを実行する。
	 * 排他チェック(削除)用でも使用。
	 * @param clientId 抽出対象の顧客IDのString
	 * @return clientMap 抽出結果のMap
	 */
	public Map<String, Object> searchClient(String clientId) {

		Map<String, Object> clientMap = new HashMap<>();
		// SQL文作成
		String sql = "SELECT * FROM m_client"
				+ " WHERE m_client.client_id= ? "
				+ " AND delete_flg != 1;";

		// ？の箇所を置換するデータの配列を定義
		Object[] param = { clientId };

		// queryForMapではなくqueryForListを使用
		List<Map<String, Object>> clientList = jdbcTemplate.queryForList(sql, param);

		// リストが空でない場合、最初の結果を取得
		if (!clientList.isEmpty()) {
			clientMap = clientList.get(0);
		}
		// 取得したデータを返す
		return clientMap;
	}

	/**
	 * 指定された顧客の登録情報を抽出するSQLを実行する。
	 * 排他チェック(削除)用でも使用。
	 * @param clientId 抽出対象の顧客IDのInteger
	 * @return clientMap 抽出結果のMap
	 */
	public Map<String, Object> searchEditClient(String clientId) {
		// SQL文作成
		String sql = "SELECT client_id,client_name,"
				+ " CAST(open_time, VARCHAR) AS open_time ,"
				+ " CAST(close_time, VARCHAR) AS close_time ,"
				+ " CAST(working_time, VARCHAR) AS working_time ,"
				+ " CAST(rest1_start, VARCHAR) AS rest1_start ,"
				+ " CAST(rest1_end, VARCHAR) AS rest1_end ,"
				+ " CAST(rest2_start, VARCHAR) AS rest2_start ,"
				+ " CAST(rest2_end, VARCHAR) AS rest2_end ,"
				+ " CAST(rest3_start, VARCHAR) AS rest3_start ,"
				+ " CAST(rest3_end, VARCHAR) AS rest3_end ,"
				+ " CAST(rest4_start, VARCHAR) AS rest4_start ,"
				+ " CAST(rest4_end, VARCHAR) AS rest4_end ,"
				+ " CAST(rest5_start, VARCHAR) AS rest5_start ,"
				+ " CAST(rest5_end, VARCHAR) AS rest5_end ,"
				+ " CAST(rest6_start, VARCHAR) AS rest6_start ,"
				+ " CAST(rest6_end, VARCHAR) AS rest6_end ,"
				+ " CAST(adjust_rest_time_start, VARCHAR) AS adjust_rest_time_start ,"
				+ " CAST(adjust_rest_time_end, VARCHAR) AS adjust_rest_time_end ,"
				+ " comment,"
				+ " FROM m_client"
				+ " WHERE m_client.client_id= ? "
				+ " AND delete_flg != 1;";

		// ？の箇所を置換するデータの配列を定義
		Object[] param = { clientId };

		// queryForMapではなくqueryForListを使用
		Map<String, Object> clientMap = jdbcTemplate.queryForMap(sql, param);
		// リストが空でない場合、最初の結果を取得
		// 取得したデータを返す
		return clientMap;
	}

	/**
	 * 指定された顧客情報を抽出するSQLを実行する。
	 * 顧客名のみの検索時に使用
	 * @param clientName 抽出対象の顧客IDのInteger
	 * @return clientList 抽出結果のList
	 */
	public List<Map<String, Object>> searchClientName(String clientName) {
		// SQL文作成
		String sql = "SELECT client_name FROM m_client"
				+ " WHERE m_client.client_name= ?";

		// ？の箇所を置換するデータの配列を定義
		Object[] param = { clientName };

		// queryForMapではなくqueryForListを使用
		List<Map<String, Object>> clientList = jdbcTemplate.queryForList(sql, param);

		// 取得したデータを返す
		return clientList;
	}

	/**
	 * 
	 *顧客リストID,顧客名検索
	 * 顧客情報の削除フラグ=0以外を抽出してListで返すSQLを実行する
	 * @param clientId 顧客ID
	 * @param clientName 顧客名
	 * @param offset オフセット数
	 * @param pageSize ページ数
	 * @return list 削除フラグ=FALSE以外の顧客情報抽出結果のList
	 */
	public List<Map<String, Object>> searchActiveClient(
			String clientId, String clientName, int offset, int pageSize) {
		// SQL文作成
		String sql = "SELECT client_id,client_name,"
				+ "open_time,close_time,working_time,"
				+ "rest1_start,rest1_end, rest2_start,rest2_end,"
				+ "rest3_start,rest3_end,rest4_start,rest4_end,"
				+ "rest5_start,rest5_end,rest6_start,rest6_end,"
				+ "adjust_rest_time_start,adjust_rest_time_end,"
				+ "comment,delete_flg"
				+ " FROM m_client"
				+ " WHERE delete_flg != 1 ";

		// ？の箇所を置換する配列に代入する為のListを定義
		List<Object> paramList = new ArrayList<>();

		if (!clientId.isEmpty() && !clientName.isEmpty()) {
			sql += "AND client_id = ? AND client_name LIKE ? ";
			paramList.add(clientId);
			paramList.add("%" + clientName + "%");
		} else if (!clientId.isEmpty()) {
			sql += "AND client_id = ? ";
			paramList.add(clientId);
		} else if (!clientName.isEmpty()) {
			sql += "AND client_name LIKE ? ";
			paramList.add("%" + clientName + "%");
		} 
		

		/**
		 * 削除フラグとページネーションのSQL文の追加
		 */
		sql += "LIMIT ? OFFSET ?";
		paramList.add(pageSize);
		paramList.add(offset);
		//?の箇所を置換するデータを追加されたlistを配列に代入
		Object[] param = paramList.toArray();
		// クエリを実行
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, param);
		// 取得したリストを返す
		return list;
	}

	/**
	 * 指定された顧客情報を論理削除するSQLを実行する
	 * @param clientId 削除対象の顧客IDのString配列
	 * @return result 削除件数
	 */
	public int deleteClient(String clientId) {

		// 実行結果
		int result = 0;
		// SQL文作成
		String sql = "UPDATE m_client"
				+ " SET delete_flg = TRUE"
				+ " WHERE m_client.client_id = ? ";

		// ？の箇所を置換するデータの配列を定義
		Object[] param = { clientId };

		// クエリを実行
		result += jdbcTemplate.update(sql, param);
		// 実行件数を返す
		return result;
	}

	/**
	 * 指定された顧客情報を論理削除するSQLを実行する
	 * @param clientId 削除対象の顧客IDのString配列
	 * @return result 削除件数
	 */
	//編集時の削除機能
	public void deleteEdit(String clientId) {
		// SQL文作成
		String sql = "UPDATE m_client"
				+ " SET delete_flg = 1"
				+ " WHERE m_client.client_id = ? ";

		// ？の箇所を置換するデータの配列を定義
		Object[] param = { clientId };
		// クエリを実行
		jdbcTemplate.update(sql, param);
	}

	/**
	 * 登録された顧客情報をINSERTするSQLを実行する
	 * @param clientData INSERT後の顧客名のrepository
	 */

	public  void registClient(@ModelAttribute ClientData clientData) {

		// SQL文作成
		String sql = "INSERT INTO m_client "
				+ "(client_id, client_name,"
				+ "open_time,close_time,"
				+ "working_time,"
				+ "rest1_start,rest1_end,"
				+ "delete_flg,comment,"
				+ "created_at,created_user";

		String sql2 = "VALUES(?,?,?,?,?,?,?,?,?,?,?";

		List<Object> paramList = new ArrayList<>(
				Arrays.asList(
						clientData.getClient_id(),
						clientData.getClient_name(),
						clientData.getOpen_time(),
						clientData.getClose_time(),
						clientData.getWorking_time(),
						clientData.getRest1_start(),
						clientData.getRest1_end(),
						clientData.getComment(),
						clientData.getDelete_flg(),
						clientData.getCreated_at(),
						clientData.getCreated_user()));

		//任意項目情報の追加
		if (!clientData.getRest2_start().equals("")) {
			sql += ",rest2_start";
			sql2 += ",?";
			paramList.add(clientData.getRest2_start());
		}
		if (!clientData.getRest2_end().equals("")) {
			sql += ",rest2_end";
			sql2 += ",?";
			paramList.add(clientData.getRest2_end());
		}
		if (!clientData.getRest3_start().equals("")) {
			sql += ",rest3_start";
			sql2 += ",?";
			paramList.add(clientData.getRest3_start());
		}
		if (!clientData.getRest3_end().equals("")) {
			sql += ",rest3_end";
			sql2 += ",?";
			paramList.add(clientData.getRest3_end());
		}
		if (!clientData.getRest4_start().equals("")) {
			sql += ", rest4_start";
			sql2 += ",?";
			paramList.add(clientData.getRest4_start());
		}
		if (!clientData.getRest4_end().equals("")) {
			sql += ", rest4_end";
			sql2 += ",?";
			paramList.add(clientData.getRest4_end());
		}
		if (!clientData.getRest5_start().equals("")) {
			sql += ", rest5_start";
			sql2 += ",?";
			paramList.add(clientData.getRest5_start());
		}
		if (!clientData.getRest5_end().equals("")) {
			sql += ", rest5_end";
			sql2 += ",?";
			paramList.add(clientData.getRest5_end());
		}
		if (!clientData.getRest6_start().equals("")) {
			sql += ", rest6_start";
			sql2 += ",?";
			paramList.add(clientData.getRest6_start());
		}
		if (!clientData.getRest6_end().equals("")) {
			sql += ", rest6_end;";
			sql2 += ",?";
			paramList.add(clientData.getRest6_end());
		}
		sql += ")";
		sql2 += ")";
		sql += sql2;
		// ？の箇所を置換の為に代入する配列を定義
		Object[] param = paramList.toArray(new Object[paramList.size()]);
		// クエリを実行
		jdbcTemplate.update(sql, param);

	}

	/**
	 * 登録された顧客情報をUPDATEするSQLを実行する
	 * @param c_name UPDATE後の顧客名のString
	 * @param c_id UPDATE対象の顧客IDのString
	 */
	public void editClient(ClientData clientData) {
		// 編集した件数
		// SQL文作成
		String sql = "UPDATE m_client"
				+ " SET open_time = ?,"
				+ " close_time = ?,"
				+ " working_time = ?,"
				+ " rest1_start = ?,"
				+ " rest1_end = ? ";

		// ？の箇所を置換する配列に代入する為のListを定義
		List<Object> paramList = new ArrayList<>(
				Arrays.asList(
						clientData.getOpen_time(),
						clientData.getClose_time(),
						clientData.getWorking_time(),
						clientData.getRest1_start(),
						clientData.getRest1_end()));
		// 任意項目情報の追加
		if (!clientData.getRest2_start().equals("")) {
			sql += " ,rest2_start = ? ";
			paramList.add(clientData.getRest2_start());
		}
		if (!clientData.getRest2_end().equals("")) {
			sql += " ,rest2_end = ? ";
			paramList.add(clientData.getRest2_end());
		}
		if (!clientData.getRest3_start().equals("")) {
			sql += " ,rest3_start = ? ";
			paramList.add(clientData.getRest3_start());
		}
		if (!clientData.getRest3_end().equals("")) {
			sql += " ,rest3_end = ? ";
			paramList.add(clientData.getRest3_end());
		}
		if (!clientData.getRest4_start().equals("")) {
			sql += " , rest4_start = ? ";
			paramList.add(clientData.getRest4_start());
		}
		if (!clientData.getRest4_end().equals("")) {
			sql += " , rest4_end = ? ";
			paramList.add(clientData.getRest4_end());
		}
		if (!clientData.getRest5_start().equals("")) {
			sql += " , rest5_start = ? ";
			paramList.add(clientData.getRest5_start());
		}
		if (!clientData.getRest5_end().equals("")) {
			sql += " , rest5_end = ? ";
			paramList.add(clientData.getRest5_end());
		}
		if (!clientData.getRest6_start().equals("")) {
			sql += " , rest6_start = ? ";
			paramList.add(clientData.getRest6_start());
		}
		if (!clientData.getRest6_end().equals("")) {
			sql += " , rest6_end = ? ";
			paramList.add(clientData.getRest6_end());
		}
		sql += " , updated_at = ?, updated_user = ? WHERE client_id = ?";

		paramList.addAll(
				Arrays.asList(
						clientData.getUpdated_at(),
						clientData.getCreated_user(),
						clientData.getClient_id()));
		// ？の箇所を置換の為に代入する配列を定義
		Object[] param = paramList.toArray(new Object[paramList.size()]);
		// クエリを実行
		jdbcTemplate.update(sql, param);
	}

}
