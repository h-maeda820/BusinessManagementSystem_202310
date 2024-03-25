/**
 * 排他チェック削除及び編集中、編集後を行うクラス
 */

package com.nexus.whc.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexus.whc.repository.CalendarRepository;
import com.nexus.whc.repository.ClientRepository;
import com.nexus.whc.repository.EmployeeRepository;
import com.nexus.whc.repository.LockRepository;
import com.nexus.whc.repository.UserRepository;

@Service
public class ExclusiveCheckService {

	@Autowired
	CalendarRepository calendarRepository;
	@Autowired
	ClientRepository clientRepository;
	@Autowired
	EmployeeRepository employeeRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	LockRepository lockRepository;

	public static String loockingUserId = "";//ロックに保存するuser＿idを判別用に用意

	/**
	 * 
	 * @param editSeqId 編集を行うシーケンスID（レコードID）
	 * @param login_user_id　現在ログインしているユーザーのID
	 * @param tableNumber	編集を行うテーブルを識別するための値
	 * @return　access_Result　これがtrueであればアクセスを許可する
	 */
	public boolean ExclusiveCheckEdited(String editSeqId, String loginUserId, int tableNumber) {

		//コントロールにアクセスを許可するかどうかの正誤値を渡す為の変数
		boolean accessResult = true;
		String editTableName = "";

		if (tableNumber == 1) {
			editTableName = "m_user";
		} else if (tableNumber == 2) {
			editTableName = "m_employee";
		} else if (tableNumber == 3) { //ここら辺スイッチ文にしても良いかも
			editTableName = "m_client"; //(そもそも渡す値を直接Stringの名前でもいいかも)
		} else if (tableNumber == 4) {
			editTableName = "m_calendar";
		}
		
		System.out.println("編集したいテーブル" + editTableName);

		/*
		 *	編集を行おうとしている情報がロックされているかどうかを
		 * 	上のifで持ってきたID（editSeq_id）とテーブル名（edit_Table_Name）の二つを用いて
		 *	現在保存されているロックテーブルの情報を比較して判別
		 */
		if (lockCheck(editSeqId, editTableName)) {
			/* 
			 * ロックされていたらif文の中に入る
			 * ロックされていても下のifで編集者のIDと
			 * ログインしている人のIDが一致すればtrueを返す
			 */
			System.out.println("編集しようとしているロックテーブルに保存されているユーザーID" + loockingUserId);
			System.out.println("現在編集をしようとしているユーザーのID" + loginUserId);

			if (loginUserId.equals(loockingUserId)) {
				System.out.println("ユーザーID一致、編集可能");
				accessResult = false;

				return accessResult;
			}

			//ロックされていない場合はロックテーブルにこれから編集を行う情報を保存しtrueを返す
		} else {

			System.out.println("ロックテーブルに保存されていないので編集可能");
			lockRepository.lockingRegistration(editTableName, editSeqId, loginUserId);
			accessResult = false;
		}

		return accessResult;
	}

	public void ExclusiveLockDalete(String editSeqId, String loginUserId, int tableNumber) {

		String editTableName = "";

		if (tableNumber == 1) {
			editTableName = "m_user";
		} else if (tableNumber == 2) {
			editTableName = "m_employee";
		} else if (tableNumber == 3) { //ここら辺スイッチ文にしても良いかも
			editTableName = "m_client"; //(そもそも渡す値を直接Stringの名前でもいいかも)
		} else if (tableNumber == 4) {
			editTableName = "m_calendar";
		}

		lockRepository.ExclusiveLockDalete(editSeqId, loginUserId, editTableName);

	}

	/**
	 * 削除チェック
	 * @param seq_id　チェックを行うレコードのseq_id
	 * @param tableNumber　テーブル判別用の変数
	 * @return　 accessResult　
	 */

	public boolean ExclusiveCheckDalete(String seq_id, int tableNumber) {

		boolean accessResult = false;
		Map<String, Object> map = new HashMap<>();

		try {
			if (tableNumber == 1) {
				map = userRepository.searchUser(seq_id);
			} else if (tableNumber == 2) {
				map = employeeRepository.searchEmployeeName(seq_id);
			} else if (tableNumber == 3) {
				map = clientRepository.searchClient(seq_id);
			} else if (tableNumber == 4) {
				map = calendarRepository.searchEditCalendar(seq_id);
			}

			if (map == null || map.isEmpty()) {
				/*該当データが存在しない（削除されていた）場合accessResultにtrueを代入*/
				accessResult = true;
			}
		} catch (Exception e) {
			/*該当データが存在しない（削除されていた）等でエラーが発生した場合accessResultにtrueを代入*/
			accessResult = true;
		}

		return accessResult;
	}

	//このメソッドはサービスにクラスに移動しても良いかも
	/**
	 * 編集しようとしている情報がロックテーブルに入っているかどうかの判別
	 * @param editSeq_id　編集しようとしているテーブルのシーケンスID
	 * @param editTableName編集しようとしているテーブル名
	 * @return　lock　if分を通すかどうかの正誤値
	 */
	private boolean lockCheck(String editSeqId, String editTableName) {

		boolean lock = true;

		try {

			Map<String, Object> lockikng_information = lockRepository.lockingCheck(editSeqId, editTableName);

			loockingUserId = lockikng_information.get("locking_user_id").toString();

		} catch (Exception e) {
			System.out.println("lookingUserIdに値が無い");
			lock = false;//対応する情報が無ければfaise
		}

		return lock;//対応する情報があればtrueを返す
	}

}
