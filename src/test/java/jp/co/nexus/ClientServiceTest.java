package jp.co.nexus;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.nexus.whc.repository.ClientRepository;
import com.nexus.whc.services.ClientService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = ClientService.class)
@TestPropertySource(locations = "classpath:application.properties")
@SpringBootApplication
public class ClientServiceTest {

	@TestConfiguration
	static class ClientRepositoryTestMock {

		@Bean
		public ClientRepository clientRepository() {
			return mock(ClientRepository.class);
		}
	}

	@MockBean
	private ClientRepository mockClientRepository;

	@Autowired
	private ClientService target;

	/**
	 * countClient（指定された顧客IDのデータ（false）を取得できる場合）
	 * 指定された顧客IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した件数が1件であること
	 **/
	@Test
	public void countClient01() {
		
		/*1を返すように設定*/
		Mockito.when(mockClientRepository.countClient()).thenReturn(1);
		
		/*sqlを実行、件数をresultに代入*/
		int result = target.countClient();
		
		/*期待値と実際値の確認*/
		assertEquals(1, result);
	}

	/**
	 * countClient（指定された顧客IDのデータ（false）を取得できる場合）
	 * ※指定された顧客IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した件数が2件であること
	 **/
	@Test
	public void countClient02() {
		
		/*2を返すように設定*/
		Mockito.when(mockClientRepository.countClient()).thenReturn(2);
		
		/*sqlを実行、件数をresultに代入*/
		int result = target.countClient();
		
		/*期待値と実際の値が一致することを確認*/
		assertEquals(2, result);
	}

	/**
	 * countClient（指定された顧客IDのデータ（false）を取得できない場合）
	 * ※指定された顧客IDの削除フラグ=falseのデータが存在すること
	 * 異常系テストケース
	 * 期待値
	 * ・取得した件数が0件であること
	 **/
	@Test
	public void countCleient03() {
		
		/*0を返すように設定*/
		Mockito.when(mockClientRepository.countClient()).thenReturn(0);
		
		/*sqlを実行、件数をresultに代入*/
		int result = target.countClient();
		
		/*期待値と実際の値が一致することを確認*/
		assertEquals(0, result);
	}
	
	/**
	 * countClient（指定された顧客IDのデータ（false）を取得できる場合）
	 * ※指定された顧客IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した件数が21件であること
	 **/
	@Test
	public void countCleient04() {

		/*21を返すように設定*/
		Mockito.when(mockClientRepository.countClient()).thenReturn(21);
		
		/*sqlを実行、件数をresultに代入*/
		int result = target.countClient();
		
		/*期待値と実際の値が一致することを確認*/
		assertEquals(21, result);
	}
	
	/**
	 * ClientSerchActive（指定された顧客IDのデータ（false）を取得できる場合）
	 * ※指定された顧客IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した顧客Idが101であること(データが1件の時)
	 **/
	@Test
	public void searchActive01() {
		
		/*ページサイズとオフセットを定義*/
		int offset = 0;
		int pageSize = 20;
		
		/*mockListの作成*/
		List<Map<String, Object>> mockList = new ArrayList<>();
		
		/*mapの作成*/
		Map<String, Object> map = new HashMap<>();
		map.put("client_id", 101);
		map.put("client_name", "株式会社エクサス");
		map.put("open_time", "09:00:00");
		map.put("close_time", "18:00:00");
		map.put("rest1_start", "12:00:00");
		map.put("rest1_end", "13:00:00");
		map.put("comment", "コメント4");
		map.put("delete_flg", 0);
		map.put("created_at", "2023-01-01 00:00:00");
		map.put("reated_user", "nexus001");
		map.put("updated_at", "2023-01-01 00:00:00");
		map.put("updated_user", "nexus001");
		
		/*mockListにmapを追加*/
		mockList.add(map);
		

		/*mockListを返すように設定*/
		Mockito.when(mockClientRepository.searchActive(offset, pageSize)).thenReturn(mockList);
		
		/*sqlを実行、リストをlistに代入*/
		List<Map<String, Object>> list = target.searchActive(offset, pageSize);
		
		/*取得したリストの0番目の顧客IDを取得*/
		String clientId = list.get(0).get("client_id").toString();
		
		/*期待値と実際の値が一致することを確認*/
		assertEquals("101", clientId);
	}
	
	/**
	 * ClientSerchActive（指定された顧客IDのデータ（false）を取得できる場合）
	 * ※指定された顧客IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した顧客Idが102であること(データが2件の時)
	 **/
	@Test
	public void searchActive02() {
		
		/*ページサイズとオフセットを定義*/
		int offset = 0;
		int pageSize = 20;
		
		/*mockListの作成*/
		List<Map<String, Object>> mockList = new ArrayList<>();
		
		/*map1の作成*/
		Map<String, Object> map1 = new HashMap<>();
		map1.put("client_id", 101);
		map1.put("client_name", "株式会社エクサス");
		map1.put("open_time", "09:00:00");
		map1.put("close_time", "18:00:00");
		map1.put("rest1_start", "12:00:00");
		map1.put("rest1_end", "13:00:00");
		map1.put("comment", "コメント4");
		map1.put("delete_flg", 0);
		map1.put("created_at", "2023-01-01 00:00:00");
		map1.put("reated_user", "nexus001");
		map1.put("updated_at", "2023-01-01 00:00:00");
		map1.put("updated_user", "nexus001");
		
		/*map2の作成*/
		Map<String, Object> map2 = new HashMap<>();
		map2.put("client_id", 102);
		map2.put("client_name", "株式会社エクサス1");
		map2.put("open_time", "09:00:00");
		map2.put("close_time", "18:00:00");
		map2.put("rest1_start", "12:00:00");
		map2.put("rest1_end", "13:00:00");
		map2.put("comment", "コメント4");
		map2.put("delete_flg", 0);
		map2.put("created_at", "2023-01-01 00:00:00");
		map2.put("reated_user", "nexus001");
		map2.put("updated_at", "2023-01-01 00:00:00");
		map2.put("updated_user", "nexus001");
		
		/*mockListにmap1とmap2を追加*/
		mockList.add(map1);
		mockList.add(map2);

		/*mockListを返すように設定*/
		Mockito.when(mockClientRepository.searchActive(offset, pageSize)).thenReturn(mockList);
		
		/*sqlを実行、リストをlistに代入*/
		List<Map<String, Object>> list = target.searchActive(offset, pageSize);
		
		/*取得したリストの1番目の顧客IDを取得*/
		String clientId = list.get(1).get("client_id").toString();
		
		/*期待値と実際の値が一致することを確認*/
		assertEquals("102", clientId);
	}
	
	/**
	 * ClientSerchActive（指定された顧客IDのデータ（false）を取得できる場合）
	 * ※指定された顧客IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した顧客Idが空であること(データが0件の時)
	 **/
	@Test
	public void searchActive03() {
		
		/*ページサイズとオフセットを定義*/
		int offset = 0;
		int pageSize = 20;
		
		/*mockListを返すように設定*/
		Mockito.when(mockClientRepository.searchActive(offset, pageSize)).thenReturn(Collections.emptyList());
		
		/*sqlを実行、リストをlistに代入*/
		List<Map<String, Object>> list = target.searchActive(offset, pageSize);
		
		/*期待値と実際の値が一致することを確認*/
		assertTrue(list.isEmpty());
	}
	
//	できてませんンンンンン
	/**
	 * ClientSerchActive（指定された顧客IDのデータ（false）を取得できる場合）
	 * ※指定された顧客IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した顧客Idが121であること(データが21件の時)
	 **/
	@Test
	public void searchActive04() {
		
		/*ページサイズとオフセットとページ数を定義*/
		int pageSize = 20;
		int offset =20;
		int page =1;
		
		/*mockListの作成*/
		List<Map<String, Object>> mockList = new ArrayList<>();
		
		/*mapの作成*/
		Map<String, Object> map = new HashMap<>();
		map.put("client_id", 121);
		map.put("client_name", "株式会社エクサス21");
		map.put("open_time", "09:00:00");
		map.put("close_time", "18:00:00");
		map.put("rest1_start", "12:00:00");
		map.put("rest1_end", "13:00:00");
		map.put("comment", "コメント4");
		map.put("delete_flg", 0);
		map.put("created_at", "2023-01-01 00:00:00");
		map.put("reated_user", "nexus001");
		map.put("updated_at", "2023-01-01 00:00:00");
		map.put("updated_user", "nexus001");
		
		/*mockListにmapを追加*/
		mockList.add(map);
		
		/*mockListを返すように設定*/
		Mockito.when(mockClientRepository.searchActive(offset, pageSize)).thenReturn(mockList);
		
		/*sqlを実行、リストをlistに代入*/
		List<Map<String, Object>> list = target.searchActive(page, pageSize);
		
		/*取得したリストの0番目の顧客IDを取得*/
		String clientId = list.get(0).get("client_id").toString();
		
		/*期待値と実際の値が一致することを確認*/
		assertEquals("121", clientId);
	}
	
	/**
	 * deleteClient（指定された顧客IDのデータを取得できる場合）
	 * ※指定された顧客IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した削除件数が1件であること
	 **/
	@Test
	public void deleteClient01() {
		
		/*clientIdを定義*/
		String clientId = "101";
		
		/*mockListを返すように設定*/
		Mockito.when(mockClientRepository.deleteClient(clientId)).thenReturn(1);
		
		/*sqlを実行、件数をresultに代入*/
		int result =  target.deleteClient(clientId);
		
		/*期待値と実際の値が一致することを確認*/
		assertEquals(1,result);
	}
	
	/**
	 * deleteClient（指定された顧客IDのデータを取得できる場合）
	 * ※指定された顧客IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した削除件数が2件であること
	 **/
	@Test
	public void deleteClient02() {
		
		/*clientIdを定義*/
		String clientId = "101";
		String clientId2 = "102";
		
		/*mockListを返すように設定*/
		Mockito.when(mockClientRepository.deleteClient(clientId)).thenReturn(2);
		
		/*sqlを実行、件数をresultに代入*/
		int result =  target.deleteClient(clientId);
		result +=  target.deleteClient(clientId2);
		
		/*期待値と実際の値が一致することを確認*/
		assertEquals(2,result);
	}
	
	/**
	 * deleteClient（指定された顧客IDのデータを取得できない場合）
	 * ※指定された顧客IDの削除フラグ=falseのデータが存在しないこと
	 * 異常系テストケース
	 * 期待値
	 * ・取得した削除件数が0件であること
	 **/
	@Test
	public void deleteClient03() {
		
		/*clientIdを定義*/
		String clientId ="999";
		
		/*mockListを返すように設定*/
		Mockito.when(mockClientRepository.deleteClient(clientId)).thenReturn(0);
		
		/*sqlを実行、件数をresultに代入*/
		int result =  target.deleteClient(clientId);
		
		/*期待値と実際の値が一致することを確認*/
		assertEquals(0,result);
	}
}
