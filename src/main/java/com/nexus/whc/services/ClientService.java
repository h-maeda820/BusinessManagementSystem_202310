package com.nexus.whc.services;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexus.whc.models.ClientData;
import com.nexus.whc.repository.ClientRepository;

/**
 * ClientService.java
 * 顧客管理機能で使用する顧客情報の登録・検索・編集・削除に関する処理を
 * ClientDaoクラスからClientControllerクラスに提供する
 *
 * @author 榊原 孝司
 * @version 
 *
 */
@Service
public class ClientService {

	@Autowired
	ClientRepository clientRepository;

	/**
	 * 顧客情報の全件を取得
	 * ページネーションで使用
	 * @return result　実行件数
	 */
	public int countClient() {
		int result = clientRepository.countClient();
		return result;
	}

	/**
	 * 顧客情報の全件を取得
	 * ページネーションで使用
	 * @return clientData 実行件数
	 */
	public int countSearchClient(String clientId, String clientName) {
		/*クエリを実行*/
		int result = clientRepository.countSearchClient(clientId, clientName);
		/*実行件数を返す*/
		return result;
	}

	/**
	 * 顧客情報を全件取得してListで返す。
	 * @param なし
	 * @return list 顧客情報全件取得結果のList
	 */
	public List<Map<String, Object>> searchAll() {
		List<Map<String, Object>> list = clientRepository.searchAll();
		return list;
	}

	/**
	 * 顧客情報の削除フラグ=FALSE以外を抽出してListで返す。
	 * ページネーションの引数を設定
	 * @param なし
	 * @return list 削除フラグ=FALSE以外の顧客情報抽出結果のList
	 */
	public List<Map<String, Object>> searchActive(int pageNumber, int pageSize) {

		//オフセット数を定義
		int offset = pageNumber * pageSize;

		List<Map<String, Object>> list = clientRepository.searchActive(offset, pageSize);
		return list;
	}

	/**
	 * 顧客情報の削除フラグ=FALSE以外を抽出してListで返す。
	 * @param なし
	 * @return list 削除フラグ=FALSE以外の顧客情報抽出結果のList
	 */
	public List<Map<String, Object>> searchActiveClient(String clientId, String clientName, int pageNumber,
			int pageSize) {
		/*オフセット数を定義*/
		int offset = pageNumber * pageSize;

		List<Map<String, Object>> list = clientRepository.searchActiveClient(clientId, clientName, offset, pageSize);
		return list;
	}

	/**
	 * 顧客IDで指定された顧客情報を返す。
	 * @param clientId 抽出対象の顧客IDのString
	 * @return map 抽出結果のMap
	 */
	public Map<String, Object> searchClient(String clientId) {
		Map<String, Object> map = clientRepository.searchClient(clientId);
		return map;
	}

	/**
	 * 指定された顧客情報を検索する。
	 * DBに登録されている内容をセット
	 * @param clientId 削除対象の顧客IDのString
	 * @return clientData 該当の検索データ
	 */
	public ClientData searchEditClient(String clientId) {
		Map<String, Object> map = clientRepository.searchClient(clientId);
		// DBからの情報をセットする変数
		ClientData clientData = new ClientData();
		// Time型をStringに変換する変数
		SimpleDateFormat times = new SimpleDateFormat("HH:mm:ss");
		// DBに登録されている内容をclientDataにセット
		clientData.setClient_id(String.valueOf(map.get("client_id")));
		clientData.setClient_name((String) map.get("client_name"));
		clientData.setOpen_time(times.format((map.get("open_time"))));
		clientData.setClose_time(times.format((map.get("close_time"))));
		clientData.setWorking_time(map.get("working_time").toString());
		clientData.setRest1_start(times.format((map.get("rest1_start"))));
		clientData.setRest1_end(times.format((map.get("rest1_end"))));
		clientData.setRest2_start(((map.get("rest2_start") == null) ? "" : times.format((map.get("rest2_start")))));
		clientData.setRest2_end(((map.get("rest2_end") == null) ? "" : times.format((map.get("rest2_end")))));
		clientData.setRest3_start(((map.get("rest3_start") == null) ? "" : times.format((map.get("rest3_start")))));
		clientData.setRest3_end(((map.get("rest3_end") == null) ? "" : times.format((map.get("rest3_end")))));
		clientData.setRest4_start(((map.get("rest4_start") == null) ? "" : times.format((map.get("rest4_start")))));
		clientData.setRest4_end(((map.get("rest4_end") == null) ? "" : times.format((map.get("rest4_end")))));
		clientData.setRest5_start(((map.get("rest5_start") == null) ? "" : times.format((map.get("rest5_start")))));
		clientData.setRest5_end(((map.get("rest5_end") == null) ? "" : times.format((map.get("rest5_end")))));
		clientData.setRest6_start(((map.get("rest6_start") == null) ? "" : times.format((map.get("rest6_start")))));
		clientData.setRest6_end(((map.get("rest6_end") == null) ? "" : times.format((map.get("rest6_end")))));
		clientData.setAdjust_rest_time_start(
				((map.get("adjust_rest_time_start") == null) ? "" : times.format((map.get("adjust_rest_time_start")))));
		clientData.setAdjust_rest_time_end(
				((map.get("adjust_rest_time_end") == null) ? "" : times.format((map.get("adjust_rest_time_end")))));
		clientData.setComment((String) (map.get("comment")));
		return clientData;
	}

	/**
	 * 指定された顧客情報を検索する。
	 * @param clientName 削除対象の顧客名のString
	 * @return list 該当の検索データ
	 */
	public List<Map<String, Object>> searchClientName(String clientName) {
		List<Map<String, Object>> list = clientRepository.searchClientName(clientName);
		return list;
	}

	/**
	 * 指定された顧客情報を論理削除する。
	 * @param clientId 削除対象の顧客IDのString
	 * @return result 削除件数
	 */
	public int deleteClient(String clientId) {
		// 削除した件数
		int result = 0;
		result = clientRepository.deleteClient(clientId);
		return result;
	}

//	/**
//	 * 指定された顧客情報を論理削除する。
//	 * @param clientId 削除対象の顧客IDのString配列
//	 */
//	public int deleteEdit(String clientId) {
//		int result = 0;
//		result = clientRepository.deleteClient(clientId);
//		return result;
//	}

	/**
	 * 登録された顧客情報を登録する。
	 * @param clientData 登録後の顧客データのClientData
	 */
	public ClientData registClient(ClientData clientData) {
		clientRepository.registClient(clientData);
		return clientData;
	}

	/**
	 * 登録された顧客情報を編集する。
	 * @param clientData 編集後の顧客データのClientData
	 */
	public void editClient(ClientData clientData) {
		clientRepository.editClient(clientData);
	}
}
