/**
 * s_lockテーブルにアクセスするためのクラス
 */

package com.nexus.whc.repository;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LockRepository {
	@Autowired
	JdbcTemplate jdbcTemplate;

	/**
	 * s_lockの情報を引き出すメソッド
	 * @param editSeqId
	 * @param editTableName
	 * @return　lockikngInformation　引き出した情報を格納する変数
	 */
	public Map<String, Object> lockingCheck(String editSeqId, String editTableName) {
		
		try {
			
			/*SQL文の作成*/
			String sql = "SELECT locking_table_name, locking_record_id, locking_user_id "
					+ "FROM s_lock "
					+ "WHERE locking_table_name =  ?   AND locking_record_id = ?";
			
			/*?の箇所を置換するデータの配列を定義*/
			Object[] param = { editTableName, editSeqId };

			/*クエリを実行*/
			Map<String, Object> lockikngInformation = jdbcTemplate.queryForMap(sql, param);
			
			/*ロックテーブルに情報があれば返す*/
			return lockikngInformation;

		} catch (Exception e) {
			
			System.out.println(e.getMessage());
			System.out.println("ロック検索該当なし");

			Map<String, Object> lockikng_information = Collections.emptyMap();
			/*Collections.emptyList()*/
			return lockikng_information;
		}

	}

	/**
	 * 
	 * @param editTableName 登録するテーブルの名前
	 * @param editSeqId	レコードIDに登録する編集するテーブルのseq_id
	 * @param loginUserId
	 */
	public void lockingRegistration(String editTableName, String editSeqId, String loginUserId) {
		long seqId = 0;
		try {
			//最大値＋１を計算
			//SQL文の作成
			String getMaxSeqId = "SELECT IFNULL(MAX(seq_id), 0) + 1 FROM  s_lock";
			//クエリを実行
			seqId = jdbcTemplate.queryForObject(getMaxSeqId, Long.class);
		} catch (Exception e) {
			// エラー処理
			e.printStackTrace();
			System.out.println("最大値取得失敗");
		}
		try {
			//新しい情報を挿入
			String insertSql = "INSERT INTO s_lock VALUES (?, ?, ?, ?)";
			Object[] params = { seqId, editTableName, editSeqId, loginUserId };
			jdbcTemplate.update(insertSql, params);
			System.out.println("ロック完了｛"+editTableName+"},{"+editSeqId+"},{"+loginUserId+"}");
		} catch (Exception e) {
			// エラー処理
			System.out.println("登録エラー");
			e.printStackTrace();
		}
	}

	//排他チェック編集済　ロック解除？(0209コメント追記)
	public void ExclusiveLockDalete(String editSeqId, String loginUserId, String editTableName) {
		String sql = "DELETE FROM s_lock WHERE locking_table_name=? AND locking_record_id=? AND locking_user_id=?";
		Object[] param = { editTableName, editSeqId, loginUserId };
		jdbcTemplate.update(sql, param);
	}

}
