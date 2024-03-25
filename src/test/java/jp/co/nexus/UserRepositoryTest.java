package jp.co.nexus;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.nexus.whc.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class) // DBUnitでCSVファイルを使えるよう指定。＊CsvDataSetLoaderクラスは自作します（後述）
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class, // このテストクラスでDIを使えるように指定
		TransactionDbUnitTestExecutionListener.class // @DatabaseSetupや＠ExpectedDatabaseなどを使えるように指定
})
@ContextConfiguration(classes = UserRepository.class)
@TestPropertySource(locations = "classpath:application.properties")
@SpringBootApplication
@Transactional // @DatabaseSetupで投入するデータをテスト処理と同じトランザクション制御とする。（テスト後に投入データもロールバックできるように）
public class UserRepositoryTest {

	@Autowired
	private UserRepository target;

	@Autowired
	JdbcTemplate jdbcTemplate;

	/**
	 * ★一覧表示機能
	 * ・searchActive
	 * ・serachCount
	 * *削除機能
	 * ・daleteUser
	 */

	/**
	 * searchActive（ユーザ情報のリストを取得できる場合）
	 * ※削除フラグ=falseのデータが1件存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得したリストの0番目のseq_idが101であること
	 **/
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/UserRepositoryTest/case01/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証
	public void searchActive01() {

		int offset = 0;
		int pageSize = 20;

		List<Map<String, Object>> list = target.searchActive(offset, pageSize);
		Object result = list.get(0).get("seq_id");
		assertEquals(101, result);
	}

	/**
	 * searchActive（ユーザ情報のリストを取得できる場合）
	 * ※削除フラグ=falseのデータが2件存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得したリストの0番目のseq_idが102であること
	 **/
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case02/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/UserRepositoryTest/case02/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証
	public void searchActive02() {
		int offset = 0;
		int pageSize = 20;

		List<Map<String, Object>> list = target.searchActive(offset, pageSize);
		System.out.println(list);
		Object result = list.get(1).get("seq_id");
		assertEquals(102, result);
	}

	/**
	 * searchActive（ユーザ情報のリストを取得できる場合）
	 * ※削除フラグ=falseのデータが存在しないこと
	 * 正常系テストケース
	 * 期待値
	 * ・取得したリストが空であること
	 **/
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case03/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/UserRepositoryTest/case03/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証
	public void searchActive03() {
		int offset = 0;
		int pageSize = 20;

		List<Map<String, Object>> userList = target.searchActive(offset, pageSize);
		assertTrue(userList.isEmpty());
	}

	/**
	 * searchActive（ユーザ情報のリストを取得できる場合）
	 * ※削除フラグ=falseのデータが21件存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得したリストの0番目のseq_idが121であること
	 **/
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case04/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/UserRepositoryTest/case04/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証
	public void searchActive04() {
		int offset = 20;
		int pageSize = 20;

		List<Map<String, Object>> list = target.searchActive(offset, pageSize);
		Object result = list.get(0).get("seq_id");
		assertEquals(121, result);
	}

	/**
	 * searchCount（指定されたユーザIDのデータを取得できる場合）
	 * ※指定されたユーザIDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した件数が1件であること
	 **/
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/UserRepositoryTest/case01/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証
	public void searchCount01() {

		int result = target.userList().size();
		assertEquals(1, result);
	}

	/**
	 * searchCount（指定されたユーザIDのデータを取得できる場合）
	 * ※指定されたユーザIDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した件数が2件であること
	 **/
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case02/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/UserRepositoryTest/case02/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証
	public void searchCount02() {

		int result = target.userList().size();
		assertEquals(2, result);
	}

	/**
	 * searchCount（指定されたユーザIDのデータを取得できる場合）
	 * ※指定されたユーザIDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した件数が0件であること
	 **/
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case03/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/UserRepositoryTest/case03/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証
	public void searchCount03() {

		int result = target.userList().size();
		assertEquals(0, result);
	}

	/**
	 * searchCount（指定されたユーザIDのデータを取得できる場合）
	 * ※指定されたユーザIDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した件数が21件であること
	 **/
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case04/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/UserRepositoryTest/case04/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証
	public void searchCount04() {

		int result = target.userList().size();
		assertEquals(21, result);
	}

	/**
	 * userDelete（指定されたユーザIDのデータを取得できる場合）
	 * ※指定されたユーザIDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した削除件数が1件であること
	 **/
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case01/init-delete-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/UserRepositoryTest/case01/after-delete-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証
	public void daleteUser01() {
		String seqId = "101";
		int deleteResult = target.daleteUser(seqId);
		assertEquals(1, deleteResult);
	}

	/**
	 * userDelete（指定されたユーザIDのデータを取得できる場合）
	 * ※指定されたユーザIDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した削除件数が2件であること
	 **/
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case02/init-delete-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/UserRepositoryTest/case02/after-delete-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証
	public void daleteUser02() {
		String seqId1 = "101";
		String seqId2 = "102";
		int deleteResult = target.daleteUser(seqId1);
		deleteResult += target.daleteUser(seqId2);
		assertEquals(2, deleteResult);
	}

	/**
	 * userDelete（指定されたユーザIDのデータを取得できる場合）
	 * ※指定されたユーザIDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した削除件数が0件であること
	 **/
	@Test
	@DatabaseSetup("/testdata/UserRepositoryTest/case03/init-delete-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/UserRepositoryTest/case03/init-delete-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証
	public void daleteUser03() {
		String seqId = "101";
		int deleteResult = target.daleteUser(seqId);
		assertEquals(0, deleteResult);
	}
}
