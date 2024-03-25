package com.nexus.whc.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexus.whc.repository.TopRepository;

/*
 * TopService.java
 * 
 * TopServiceクラス
 */

/*
 * Serviceクラス
 */
@Service
public class TopService {
	
	@Autowired
	TopRepository topRepositoty;
	
	/**
	 * delete_flg=falseファイルリンクを全件を抽出するSQLを実行する
	 * @return list 抽出結果のlist
	 */
	public List<Map<String,Object>> searchAll(){
		
		/*クエリを実行*/
		List<Map<String,Object>> list = topRepositoty.searchAll();
		
		/*取得したリストを返す*/
		return list;
		
	}
}