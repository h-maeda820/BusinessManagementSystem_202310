//package jp.co.nexus;
//
//import static org.junit.Assert.*;
//import static org.mockito.Mockito.*;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import com.nexus.whc.repository.CalendarRepository;
//import com.nexus.whc.services.CalendarService;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ContextConfiguration(classes = CalendarService.class)
//@TestPropertySource(locations = "classpath:application.properties")
//@SpringBootApplication
//public class CalendarServiceTest {
//
//	@TestConfiguration
//	static class CalendarRepositoryTestMock {
//
//		@Bean
//		public CalendarRepository calendarRepository() {
//			return mock(CalendarRepository.class);
//		}
//	}
//
//	@MockBean
//	private CalendarRepository mockCalendarRepository;
//
//	@Autowired
//	private CalendarService target;
//
//	/**
//	 * searchCalendarAll(Listを取得できる場合)
//	 * 正常系テストケース
//	 * mockCalendarRepository.searchCalendarAllはmockListの値を返す
//	 * 期待値
//	 * ・一覧に表示するための情報を返し、List0番目のseq_idが1であること(データが1件の場合)
//	 * 
//	 * テストソース作成者 長澤
//	 */
//	@Test
//	public void searchCalendarAllTest01() {
//		
//		int offset = 0;
//		int pageSize = 20;
//		int pageNumber = 0;
//		
//		/*モック用listの作成*/
//		List<Map<String, Object>> mockList = new ArrayList<>();
//		Map<String, Object> map = new HashMap<>();
//		map.put("seq_id", 1);
//		map.put("client_id", "101");
//		map.put("employee_id", "1");
//		map.put("year_month", "2023-08-01");
//		map.put("monthly_holidays", 11);
//		map.put("monthly_prescribed_days", 20);
//		map.put("delete_flg", 0);
//		map.put("comment", null);
//		map.put("created_at", "2023-11-13 13:04:47");
//		map.put("created_user", "nexus01");
//		map.put("updated_at", "2023-11-13 13:04:47");
//		map.put("updated_user", "nexus01");
//
//		mockList.add(map);
//
//		/*CalendarServiceのsearchCalendarAll内で使用されるメソッドのモックを作成*/
//		Mockito.when(mockCalendarRepository.searchCalendarAll(offset, pageSize)).thenReturn(mockList);
//		
//		/*テスト対象メソッドを実行*/
//		List<Map<String, Object>> result = target.searchCalendarAll(pageNumber, pageSize);
//		
//		/*実行結果の値を取得*/
//		String seqId = result.get(0).get("seq_id").toString();
//		
//		/*比較を行いテスト結果を確認*/
//		assertEquals("1", seqId);
//	}
//
//	/**
//	 * searchCalendarAll(Listを取得できる場合)
//	 * 正常系テストケース
//	 * mockCalendarRepository.searchCalendarAllはmockListの値を返す
//	 * 期待値
//	 * ・一覧に表示するための情報を返し、List1番目のseq_idが2であること(データが2件の場合)
//	 * 
//	 * テストソース作成者 長澤
//	 */
//	@Test
//	public void searchCalendarAllTest02() {
//		
//		int offset = 0;
//		int pageSize = 20;
//		int pageNumber = 0;
//		
//		/*モック用listの作成*/
//		List<Map<String, Object>> mockList = new ArrayList<>();
//
//		/*1つ目のマップ*/
//		Map<String, Object> map1 = new HashMap<>();
//		map1.put("seq_id", 1);
//		map1.put("client_id", "101");
//		map1.put("employee_id", "1");
//		map1.put("year_month", "2023-08-01");
//		map1.put("monthly_holidays", 11);
//		map1.put("monthly_prescribed_days", 20);
//		map1.put("delete_flg", 0);
//		map1.put("comment", null);
//		map1.put("created_at", "2023-11-13 13:04:47");
//		map1.put("created_user", "nexus01");
//		map1.put("updated_at", "2023-11-13 13:04:47");
//		map1.put("updated_user", "nexus01");
//
//		/*2つ目のマップ*/
//		Map<String, Object> map2 = new HashMap<>();
//		map2.put("seq_id", 2);
//		map2.put("client_id", "102");
//		map2.put("employee_id", "2");
//		map2.put("year_month", "2023-08-01");
//		map2.put("monthly_holidays", 11);
//		map2.put("monthly_prescribed_days", 20);
//		map2.put("delete_flg", 0);
//		map2.put("comment", null);
//		map2.put("created_at", "2023-11-13 13:04:47");
//		map2.put("created_user", "nexus01");
//		map2.put("updated_at", "2023-11-13 13:04:47");
//		map2.put("updated_user", "nexus01");
//
//		mockList.add(map1);
//		mockList.add(map2);
//
//		
//		/*CalendarServiceのsearchCalendarAll内で使用されるメソッドのモックを作成*/
//		Mockito.when(mockCalendarRepository.searchCalendarAll(offset, pageSize)).thenReturn(mockList);
//		
//		/*テスト対象メソッドを実行*/
//		List<Map<String, Object>> result = target.searchCalendarAll(pageNumber, pageSize);
//		
//		/*実行結果の値を取得*/
//		String seqId = result.get(1).get("seq_id").toString();
//		
//		/*比較を行いテスト結果を確認*/
//		assertEquals("2", seqId);
//	}
//
//	/**
//	 * searchCalendarAll(Listを取得できる場合)
//	 * 正常系テストケース
//	 * mockCalendarRepository.searchCalendarAllはmockListの値を返す
//	 * 期待値
//	 * ・一覧に表示するための情報を返し、List1番目のseq_idが2であること(データが0件の場合)
//	 * 
//	 * テストソース作成者 長澤
//	 */
//	@Test
//	public void searchCalendarAllTest03() {
//		List<Map<String, Object>> mockList = new ArrayList<>();
//
//		int offset = 0;
//		int pageSize = 20;
//		int pageNumber = 0;
//		
//		/*CalendarServiceのsearchCalendarAll内で使用されるメソッドのモックを作成*/
//		Mockito.when(mockCalendarRepository.searchCalendarAll(offset, pageSize)).thenReturn(mockList);
//		/*テスト対象メソッドを実行*/
//		List<Map<String, Object>> result = target.searchCalendarAll(pageNumber, pageSize);
//		
//		/*比較を行いテスト結果を確認*/
//		assertTrue(result.isEmpty());
//	}
//
//	/**
//	 * searchCalendarAll(Listを取得できる場合)
//	 * 正常系テストケース
//	 * mockCalendarRepository.searchCalendarAllはmockListの値を返す
//	 * 期待値
//	 * ・一覧に表示するための情報を返し、List21番目のseq_idが1であること(データが21件の場合)
//	 * 
//	 * テストソース作成者 長澤
//	 */
//	@Test
//	public void searchCalendarAllTest04() {
//		
//		int offset = 20;
//		int pageSize = 20;
//		int pageNumber = 1;
//		
//		/*モック用listの作成*/
//		List<Map<String, Object>> mockList = new ArrayList<>();
//
//		Map<String, Object> map = new HashMap<>();
//		map.put("seq_id", 21);
//		map.put("client_id", "101");
//		map.put("employee_id", "1");
//		map.put("year_month", "2023-08-01");
//		map.put("monthly_holidays", 11);
//		map.put("monthly_prescribed_days", 20);
//		map.put("delete_flg", 0);
//		map.put("comment", null);
//		map.put("created_at", "2023-11-13 13:04:47");
//		map.put("created_user", "nexus01");
//		map.put("updated_at", "2023-11-13 13:04:47");
//		map.put("updated_user", "nexus01");
//
//		mockList.add(map);
//
//		/*CalendarServiceのsearchCalendarAll内で使用されるメソッドのモックを作成*/
//		Mockito.when(mockCalendarRepository.searchCalendarAll(offset, pageSize)).thenReturn(mockList);
//		
//		/*テスト対象メソッドを実行*/
//		List<Map<String, Object>> result = target.searchCalendarAll(pageNumber, pageSize);
//		
//		/*実行結果の値を取得*/
//		String seqId = result.get(0).get("seq_id").toString();
//		
//		/*比較を行いテスト結果を確認*/
//		assertEquals("21", seqId);
//	}
//
//	/**
//	 * countCalendar（指定された顧客IDのデータ（0）を取得できる場合）
//	 * ※指定された顧客IDの削除フラグ=0のデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得した件数が1件であること
//	 * 
//	 * テストソース作成者 長澤
//	 **/
//	@Test
//	public void countCalendarTest01() {
//		
//		/*CalendarServiceのcountCalendar内で使用されるメソッドのモックを作成*/
//		Mockito.when(mockCalendarRepository.countCalendar()).thenReturn(1);
//		
//		/*テスト対象メソッドを実行*/
//		int result = target.countCalendar();
//		
//		/*比較を行いテスト結果を確認*/
//		assertEquals(1, result);
//
//	}
//
//	/**
//	 * countCalendar（指定された顧客IDのデータ（0）を取得できる場合）
//	 * ※指定された顧客IDの削除フラグ=0のデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得した件数が2件であること
//	 * 
//	 * テストソース作成者 長澤
//	 **/
//	@Test
//	public void countCalendarTest02() {
//		
//		/*CalendarServiceのcountCalendar内で使用されるメソッドのモックを作成*/
//		Mockito.when(mockCalendarRepository.countCalendar()).thenReturn(2);
//		
//		/*テスト対象メソッドを実行*/
//		int result = target.countCalendar();
//		
//		/*比較を行いテスト結果を確認*/
//		assertEquals(2, result);
//
//	}
//
//	/**
//	 * countCalendar（指定された顧客IDのデータ（0）を取得できる場合）
//	 * ※指定された顧客IDの削除フラグ=0のデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得した件数が0件であること
//	 * 
//	 * テストソース作成者 長澤
//	 **/
//	@Test
//	public void countCalendarTest03() {
//		
//		/*CalendarServiceのcountCalendar内で使用されるメソッドのモックを作成*/
//		Mockito.when(mockCalendarRepository.countCalendar()).thenReturn(0);
//		
//		/*テスト対象メソッドを実行*/
//		int result = target.countCalendar();
//		
//		/*比較を行いテスト結果を確認*/
//		assertEquals(0, result);
//
//	}
//
//	/**
//	 * countCalendar（指定された顧客IDのデータ（0）を取得できる場合）
//	 * ※指定された顧客IDの削除フラグ=0のデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得した件数が21件であること
//	 * 
//	 * テストソース作成者 長澤
//	 **/
//	@Test
//	public void countCalendarTest04() {
//		
//		/*CalendarServiceのcountCalendar内で使用されるメソッドのモックを作成*/
//		Mockito.when(mockCalendarRepository.countCalendar()).thenReturn(21);
//		
//		/*テスト対象メソッドを実行*/
//		int result = target.countCalendar();
//		
//		/*比較を行いテスト結果を確認*/
//		assertEquals(21, result);
//
//	}
//
//	/**
//	 * deleteCalendar（指定された社員IDのデータを取得できる場合）
//	 * ※data.sqlに指定された社員IDの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得した削除件数が1件であること
//	 * 
//	 * テストソース作成者 長澤
//	 **/
//	@Test
//	public void deleteCalendarTest01() {
//
//		String updateUser = "nexus001";
//		String[] seq_id = { "1" };
//		
//		/*CalendarServiceのdeleteCalendar内で使用されるメソッドのモックを作成*/
//		Mockito.when(mockCalendarRepository.deleteCalendar(seq_id, updateUser)).thenReturn(1);
//		
//		/*テスト対象メソッドを実行*/
//		int result = target.deleteCalendar(seq_id, updateUser);
//		
//		/*比較を行いテスト結果を確認*/
//		assertEquals(1, result);
//	}
//
//	/**
//	 * deleteCalendar（指定された社員IDのデータを取得できる場合）
//	 * ※data.sqlに指定された社員IDの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得した削除件数が複数件であること
//	 * 
//	 * テストソース作成者 長澤
//	 **/
//	@Test
//	public void deleteCalendarTest02() {
//		
//		String updateUser = "nexus001";
//		String[] seq_id = { "1", "2" };
//		
//		/*CalendarServiceのdeleteCalendar内で使用されるメソッドのモックを作成*/
//		Mockito.when(mockCalendarRepository.deleteCalendar(seq_id, updateUser)).thenReturn(2);
//		
//		/*テスト対象メソッドを実行*/
//		int result = target.deleteCalendar(seq_id, updateUser);
//		
//		/*比較を行いテスト結果を確認*/
//		assertEquals(2, result);
//	}
//
//	/**
//	 * deleteCalendar（指定された社員IDのデータを取得できる場合）
//	 * ※data.sqlに指定された社員IDの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得した削除件数が0件であること
//	 * 
//	 * テストソース作成者 長澤
//	 **/
//	@Test
//	public void deleteCalendarTest03() {
//		
//		String updateUser = "nexus001";
//		String[] seq_id = { "" };
//		
//		/*CalendarServiceのdeleteCalendar内で使用されるメソッドのモックを作成*/
//		Mockito.when(mockCalendarRepository.deleteCalendar(seq_id, updateUser)).thenReturn(0);
//
//		/*テスト対象メソッドを実行*/
//		int result = target.deleteCalendar(seq_id, updateUser);
//		
//		/*比較を行いテスト結果を確認*/
//		assertEquals(0, result);
//	}
//
//}