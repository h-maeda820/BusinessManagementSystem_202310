//package jp.co.nexus;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.util.List;
//import java.util.Map;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.TestExecutionListeners;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
//import com.github.springtestdbunit.annotation.DatabaseSetup;
//import com.github.springtestdbunit.annotation.DbUnitConfiguration;
//import com.github.springtestdbunit.annotation.ExpectedDatabase;
//import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
//import com.nexus.whc.repository.CalendarRepository;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class) // DBUnitでCSVファイルを使えるよう指定。＊CsvDataSetLoaderクラスは自作します（後述）
//@TestExecutionListeners({
//		DependencyInjectionTestExecutionListener.class, // このテストクラスでDIを使えるように指定
//		TransactionDbUnitTestExecutionListener.class // @DatabaseSetupや＠ExpectedDatabaseなどを使えるように指定
//})
//@ContextConfiguration(classes = CalendarRepository.class)
//@TestPropertySource(locations = "classpath:application.properties")
//@SpringBootApplication
//@Transactional // @DatabaseSetupで投入するデータをテスト処理と同じトランザクション制御とする。（テスト後に投入データもロールバックできるように）
//public class CalendarRepositoryTest {
//
//	@Autowired
//	private CalendarRepository target;
//
//	@Autowired
//	JdbcTemplate jdbcTemplate;
//
//	/**
//	 * searchCalendarAll（指定されたseq_idのデータ（false）を取得できる場合）
//	 * ※指定されたseq_idの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得したseq_idが1であること(データが1件の時)
//	 * 
//	 * テストソース作成者 長澤
//	 **/
//	@Test
//	@DatabaseSetup("/testdata/CalendarRepositoryTest/case1/init-list-data") // テスト実行前に初期データを投入
//	@ExpectedDatabase(value = "/testdata/CalendarRepositoryTest/case1/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
//	public void searchActiveTest01() {
//
//		int offSet = 0;
//		int pageSize = 20;
//		
//		/*テスト対象メソッドを実行*/
//		List<Map<String, Object>> list = target.searchCalendarAll(offSet, pageSize);
//		
//		/*実行結果の各値を取得*/
//		String seqId = list.get(0).get("seq_id").toString();
//		
//		/*比較を行いテスト結果を確認*/
//		assertEquals("1", seqId);
//	}
//
//	/**
//	 * searchCalendarAll（指定されたseq_idのデータ（false）を取得できる場合）
//	 * ※指定されたseq_idの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得したseq_idが2であること(データが2件の時)
//	 * 
//	 * テストソース作成者 長澤
//	 **/
//	@Test
//	@DatabaseSetup("/testdata/CalendarRepositoryTest/case5/init-list-data") // テスト実行前に初期データを投入
//	@ExpectedDatabase(value = "/testdata/CalendarRepositoryTest/case5/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
//	public void searchActiveTest02() {
//
//		int offSet = 0;
//		int pageSize = 20;
//		
//		/*テスト対象メソッドを実行*/
//		List<Map<String, Object>> list = target.searchCalendarAll(offSet, pageSize);
//		
//		/*実行結果の各値を取得*/
//		String seqId = list.get(1).get("seq_id").toString();
//		
//		/*比較を行いテスト結果を確認*/
//		assertEquals("2", seqId);
//	}
//
//	/**
//	 * searchCalendarAll（指定されたseq_idのデータ（false）を取得できない場合）
//	 * ※指定されたseq_idの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得したリストが空であること(データが0件の時)
//	 * 
//	 * テストソース作成者 長澤
//	 **/
//	@Test
//	@DatabaseSetup("/testdata/CalendarRepositoryTest/case6/init-list-data") // テスト実行前に初期データを投入
//	@ExpectedDatabase(value = "/testdata/CalendarRepositoryTest/case6/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
//	public void searchActiveTest03() {
//
//		int offSet = 0;
//		int pageSize = 20;
//		
//		/*テスト対象メソッドを実行*/
//		List<Map<String, Object>> list = target.searchCalendarAll(offSet, pageSize);
//		
//		/*比較を行いテスト結果を確認*/
//		assertTrue(list.isEmpty());
//	}
//
//	/**
//	 * searchCalendarAll（指定されたseq_idのデータ（false）を取得できる場合）
//	 * ※指定されたseq_idの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得したseq_idが21であること(データが21件の時)
//	 * 
//	 * テストソース作成者 長澤
//	 **/
//	@Test
//	@DatabaseSetup("/testdata/CalendarRepositoryTest/case7/init-list-data") // テスト実行前に初期データを投入
//	@ExpectedDatabase(value = "/testdata/CalendarRepositoryTest/case7/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
//	public void searchActiveTest04() {
//
//		int offSet = 20;
//		int pageSize = 20;
//		
//		/*テスト対象メソッドを実行*/
//		List<Map<String, Object>> list = target.searchCalendarAll(offSet, pageSize);
//		
//		/*実行結果の各値を取得*/
//		String seqId = list.get(0).get("seq_id").toString();
//		
//		/*比較を行いテスト結果を確認*/
//		assertEquals("21", seqId);
//	}
//	
//	
//	/**
//	 * countCalendarTest（指定されたseq_idのデータを取得できる場合）
//	 * ※指定されたseq_idの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得した件数が1件であること
//	 **/
//	@Test
//	@DatabaseSetup("/testdata/CalendarRepositoryTest/case1/init-list-data") // テスト実行前に初期データを投入
//	@ExpectedDatabase(value = "/testdata/CalendarRepositoryTest/case1/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
//	public void countCalendarTest01() {
//		
//
//		int result = target.countCalendar();
//		assertEquals(1, result);
//	}
//	
//	/**
//	 * countCalendarTest（指定されたseq_idのデータを取得できる場合）
//	 * ※指定されたseq_idの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得した件数が2件であること
//	 **/
//	@Test
//	@DatabaseSetup("/testdata/CalendarRepositoryTest/case5/init-list-data") // テスト実行前に初期データを投入
//	@ExpectedDatabase(value = "/testdata/CalendarRepositoryTest/case5/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
//	public void countCalendarTest02() {
//
//		int result = target.countCalendar();
//		assertEquals(2, result);
//	}
//	
//	/**
//	 * countCalendarTest（指定されたseq_idのデータを取得できない場合）
//	 * ※指定されたseq_idの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得した件数が0件であること
//	 **/
//	@Test
//	@DatabaseSetup("/testdata/CalendarRepositoryTest/case6/init-list-data") // テスト実行前に初期データを投入
//	@ExpectedDatabase(value = "/testdata/CalendarRepositoryTest/case6/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
//	public void countCalendarTest03() {
//
//		int result = target.countCalendar();
//		assertEquals(0, result);
//	}
//	
//	/**
//	 * countCalendarTest（指定されたseq_idのデータを取得できる場合）
//	 * ※指定されたseq_idの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得した件数が21件であること
//	 **/
//	@Test
//	@DatabaseSetup("/testdata/CalendarRepositoryTest/case7/init-list-data") // テスト実行前に初期データを投入
//	@ExpectedDatabase(value = "/testdata/CalendarRepositoryTest/case7/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
//	public void countCalendarTest04() {
//
//		int result = target.countCalendar();
//		assertEquals(21, result);
//	}
//	
//	/**
//	 * deleteCalendar（指定されたseq_idのデータを取得できる場合）
//	 * ※指定されたseq_idの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得した削除件数が1件であること
//	 * 
//	 * テストソース作成者 長澤
//	 **/
//	@Test
//	@DatabaseSetup("/testdata/CalendarRepositoryTest/case2/init-data") // テスト実行前に初期データを投入
//	@ExpectedDatabase(value = "/testdata/CalendarRepositoryTest/case2/after-dalete-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証
//	public void deleteCalendarTest01() {
//		
//		String[] id = { "1" };
//		String updateUsername = "sysdate";
//		
//		/*テスト対象のメソッドの実行*/
//		int deleteResult = target.deleteCalendar(id, updateUsername);
//		
//		/*削除フラグの取得*/
//		boolean deleteFlg = getDeleteFlg(id[0]);
//
//		/*削除件数の確認*/
//		assertEquals(1, deleteResult);
//
//		/*削除が成功しているかの確認*/
//		assertTrue(deleteFlg);
//
//	}
//
//	/**
//	 * deleteCalendar（指定されたseq_idのデータを取得できる場合）
//	 * ※指定されたseq_idの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得した削除件数が2件であること
//	 * 
//	 * テストソース作成者 長澤
//	 **/
//	@Test
//	@DatabaseSetup("/testdata/CalendarRepositoryTest/case3/init-data") // テスト実行前に初期データを投入
//	@ExpectedDatabase(value = "/testdata/CalendarRepositoryTest/case3/after-dalete-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証
//	public void deleteCalendarTest02() {
//		
//		String[] id = { "1", "2" };
//		String updateUsername = "sysdate";
//		
//		/*テスト対象のメソッドの実行*/
//		int deleteResult = target.deleteCalendar(id, updateUsername);
//		
//		/*削除フラグの取得*/
//		boolean deleteFlg1 = getDeleteFlg(id[0]);
//		boolean deleteFlg2 = getDeleteFlg(id[1]);
//
//		/*削除件数の確認*/
//		assertEquals(2, deleteResult);
//
//		/*削除が成功しているかの確認*/
//		assertTrue(deleteFlg1);
//		assertTrue(deleteFlg2);
//	}
//
//	/**
//	 * deleteCalendar（指定されたseq_idのデータを取得できない場合）
//	 * ※指定されたseq_idの削除フラグ=trueのデータが存在すること
//	 * 異常系テストケース
//	 * 期待値
//	 * ・取得した削除件数が0件であること
//	 * 
//	 * テストソース作成者 長澤
//	 **/
//	@Test
//	@DatabaseSetup("/testdata/CalendarRepositoryTest/case4/init-data") // テスト実行前に初期データを投入
//	@ExpectedDatabase(value = "/testdata/CalendarRepositoryTest/case4/after-dalete-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証
//	public void deleteCalendarTest03() {
//		String[] id = { "0" };
//		String updateUsername = "sysdate";
//		int deleteResult = target.deleteCalendar(id, updateUsername);
//		assertEquals(0, deleteResult);
//	}
//	/**
//	 * 削除フラグ取得用メソッド
//	 * @param seqId　検索条件のseq_id
//	 * @return 削除できているかどうかの正誤値
//	 * 
//	 * テストソース作成者 前田
//	 */
//	public boolean getDeleteFlg(String seqId) {
//
//		String sql = "SELECT delete_flg FROM m_calendar WHERE seq_id=?";
//
//		Object[] param = { seqId };
//
//		boolean result = jdbcTemplate.queryForObject(sql, param, boolean.class);
//
//		return result;
//
//	}
//
//}
