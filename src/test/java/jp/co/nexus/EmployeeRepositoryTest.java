package jp.co.nexus;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
import com.nexus.whc.repository.EmployeeRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class) // DBUnitでCSVファイルを使えるよう指定。＊CsvDataSetLoaderクラスは自作します（後述）
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class, // このテストクラスでDIを使えるように指定
		TransactionDbUnitTestExecutionListener.class // @DatabaseSetupや＠ExpectedDatabaseなどを使えるように指定
})
@ContextConfiguration(classes = EmployeeRepository.class)
@TestPropertySource(locations = "classpath:application.properties")
@SpringBootApplication
@Transactional // @DatabaseSetupで投入するデータをテスト処理と同じトランザクション制御とする。（テスト後に投入データもロールバックできるように）
public class EmployeeRepositoryTest {

	@Autowired
	private EmployeeRepository target;

	@Autowired
	JdbcTemplate jdbcTemplate;

	/**
	 * searchActive（社員情報のリストを取得できる場合）
	 * ※削除フラグ=falseのデータが1件存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得したリストの0番目のemployee_idが1であること
	 **/
	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case01/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case01/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void searchActive01() {

		int offset = 0;
		int pageSize = 20;

		List<Map<String, Object>> list = target.searchActive(offset, pageSize);
		Object result = list.get(0).get("employee_id");

		assertEquals(1, result);
	}

	/**
	 * searchActive（社員情報のリストを取得できる場合）
	 * ※削除フラグ=falseのデータが2件存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得したリストの1番目のemployee_idが2であること
	 **/
	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case02/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case02/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void searchActive02() {

		int offset = 0;
		int pageSize = 20;

		List<Map<String, Object>> list = target.searchActive(offset, pageSize);
		Object result = list.get(1).get("employee_id");

		assertEquals(2, result);
	}

	/**
	 * searchActive（社員情報のリストを取得できない場合）
	 * ※削除フラグ=falseのデータが存在しないこと
	 * 正常系テストケース
	 * 期待値
	 * ・取得したリストが空であること
	 **/
	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case03/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case03/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void searchActive03() {

		int offset = 0;
		int pageSize = 20;

		List<Map<String, Object>> list = target.searchActive(offset, pageSize);

		assertTrue(list.isEmpty());
	}

	/**
	 * searchActive（社員情報のリストを取得できる場合）
	 * ※削除フラグ=falseのデータが21件存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得したリストの0番目のemployee_idが21であること
	 **/
	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case04/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case04/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void searchActive04() {

		int offset = 20;
		int pageSize = 20;

		List<Map<String, Object>> list = target.searchActive(offset, pageSize);
		Object result = list.get(0).get("employee_id");

		assertEquals(21, result);
	}

	/**
	 * countEmployee（指定された社員IDのデータを取得できる場合）
	 * ※指定された社員IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した件数が1件であること
	 **/
	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case01/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case01/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void countEmployee01() {

		int result = target.countEmployee();
		assertEquals(1, result);
	}

	/**
	 * countEmployee（指定された社員IDのデータを取得できる場合）
	 * ※指定された社員IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した件数が2件であること
	 **/
	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case02/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case02/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void countEmployee02() {

		int result = target.countEmployee();
		assertEquals(2, result);
	}

	/**
	 * countEmployee（指定された社員IDのデータを取得できる場合）
	 * ※指定された社員IDの削除フラグ=falseのデータが存在しないこと
	 * 正常系テストケース
	 * 期待値
	 * ・取得した件数が0件であること
	 **/
	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case03/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case03/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void countEmployee03() {

		int result = target.countEmployee();
		assertEquals(0, result);
	}

	/**
	 * countEmployee（指定された社員IDのデータを取得できる場合）
	 * ※指定された社員IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した件数が21件であること
	 **/
	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case04/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case04/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void countEmployee04() {

		int result = target.countEmployee();
		assertEquals(21, result);
	}

	
	/**
	 * deleteEmployee（指定された社員IDのデータを取得できる場合）
	 * ※指定された社員IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した削除件数が1件であること
	 * ・delete_flgが1になっていること
	 **/
	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case01/init-delete-data")
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case01/after-delete-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void deleteEmployee01() {

		String[] employeeId = { "1" };
		String updateUser = "sysdate";

		int result = target.deleteEmployee(employeeId, updateUser);
		boolean deleteFlg = getEmployeeDeleteFlg(employeeId[0]);

		assertEquals(1, result);
		assertTrue(deleteFlg);
	}

	/**
	 * deleteEmployee（指定された社員IDのデータを取得できる場合）
	 * ※指定された社員IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した削除件数が2件であること
	 **/
	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case02/init-delete-data")
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case02/after-delete-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void deleteEmployee02() {

		String[] employeeId = { "1", "2" };
		String updateUser = "sysdate";

		int result = target.deleteEmployee(employeeId, updateUser);
		boolean deleteFlg1 = getEmployeeDeleteFlg(employeeId[0]);
		boolean deleteFlg2 = getEmployeeDeleteFlg(employeeId[1]);

		assertEquals(2, result);
		assertTrue(deleteFlg1);
		assertTrue(deleteFlg2);
	}

	/**
	 * deleteEmployee（指定された社員IDのデータを取得できない場合）
	 * ※指定された社員IDの削除フラグ=trueのデータが存在すること
	 * 異常系テストケース
	 * 期待値
	 * ・取得した削除件数が0件であること
	 **/
	@Test
	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case03/init-delete-data")
	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case03/init-delete-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void deleteEmployee03() {

		String[] employeeId = { "1" };
		String updateUser = "sysdate";

		int result = target.deleteEmployee(employeeId, updateUser);

		assertEquals(0, result);
	}

	/**
	 * deleteEmployee（指定された社員IDのデータを取得できる場合）
	 * ※指定された社員IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した削除件数が1件であること
	 **/
//	@Test
//	@DatabaseSetup("/testdata/EmployeeRepositoryTest/case01/init-deletePaidVacation-data")
//	@ExpectedDatabase(value = "/testdata/EmployeeRepositoryTest/case01/after-deletePaidVacation-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
//	public void deletePaidVacation01() {
//
//		String[] employeeId = { "1" };
//		String updateUser = "sysdate";
//		String paidHoliday = "2023-07-01";
//
//		int result = target.deletePaidVacation(employeeId, updateUser, paidHoliday);
//		boolean deleteFlg = getPaidVacationDeleteFlg(employeeId[0],paidHoliday);
//
//		assertEquals(1, result);
//		assertTrue(deleteFlg);
//	}

	public boolean getEmployeeDeleteFlg(String empId) {

		String sql = "SELECT delete_flg FROM m_employee WHERE employee_id=?";

		Object[] param = { empId };

		boolean result = jdbcTemplate.queryForObject(sql, param, boolean.class);

		return result;

	}

//	public boolean getPaidVacationDeleteFlg(String empId, String paidHoliday) {
//
//		String sql = "SELECT delete_flg FROM m_employee_paid_vacation\n"
//				+ "WHERE employee_id=? AND YEAR=?";
//
//		Object[] param = { empId, paidHoliday };
//
//		boolean result = jdbcTemplate.queryForObject(sql, param, boolean.class);
//
//		return result;
//
//	}

}
