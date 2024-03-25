package com.nexus.whc.repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/*
 * TopRepository.java
 * 
 * TopRepositoryクラス
 */

/*
 * Repositoryクラス
 */
@Repository
public class TopRepository {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	/**
	 * delete_flg=falseファイルリンクを全件を抽出するSQLを実行する
	 * @param なし
	 * @return list 抽出結果のlist
	 */
	public List<Map<String,Object>> searchAll(){
		
		String sql = "SELECT publication_date, title, content FROM s_filelink WHERE delete_flg=false";
		
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		
		return list;
		
	}
	
	
}