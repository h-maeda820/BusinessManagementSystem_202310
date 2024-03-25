package jp.co.nexus;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
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
import com.nexus.whc.repository.ClientRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class) // DBUnitでCSVファイルを使えるよう指定。＊CsvDataSetLoaderクラスは自作します（後述）
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class, // このテストクラスでDIを使えるように指定
		TransactionDbUnitTestExecutionListener.class // @DatabaseSetupや＠ExpectedDatabaseなどを使えるように指定
})
@ContextConfiguration(classes = ClientRepository.class)
@TestPropertySource(locations = "classpath:application.properties")
@SpringBootApplication
@Transactional // @DatabaseSetupで投入するデータをテスト処理と同じトランザクション制御とする。（テスト後に投入データもロールバックできるように）
public class ClientRepositoryTest {

	@Autowired
	private ClientRepository target;

	/**
	 * serchActive（指定された顧客IDのデータ（false）を取得できる場合）
	 * ※指定された顧客IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した顧客Idが105であること(データが1件の時)
	 **/
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case01/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case01/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void searchActive01() {

		/*ページサイズとオフセットを定義*/
		int offSet = 0;
		int pageSize = 20;

		/*sqlを実行*/
		List<Map<String, Object>> list = target.searchActive(offSet, pageSize);

		/*取得したリストの0番目の顧客IDを取得*/
		Object clientId = list.get(0).get("client_id");

		/*期待値と実際の値が一致することを確認*/
		assertEquals(105, clientId);
	}

	/**
	 * ClientSerchActive（指定された顧客IDのデータ（false）を取得できる場合）
	 * ※指定された顧客IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した顧客Idが104であること(データが2件の時)
	 **/
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case02/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case02/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void searchActive02() {

		/*ページサイズとオフセットを定義*/
		int offSet = 0;
		int pageSize = 20;

		/*sqlを実行*/
		List<Map<String, Object>> list = target.searchActive(offSet, pageSize);

		/*取得したリストの1番目の顧客IDを取得*/
		Object clientId = list.get(1).get("client_id");

		/*期待値と実際の値が一致することを確認*/
		assertEquals(105, clientId);
	}

	/**
	 * ClientSerchActive（指定された顧客IDのデータ（false）を取得できる場合）
	 * ※指定された顧客IDの削除フラグ=falseのデータが存在しない
	 * 異常系テストケース
	 * 期待値
	 * ・取得した顧客Idがnullであること
	 **/
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case03/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case03/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void SearchActive03() {

		/*ページサイズとオフセットを定義*/
		int offSet = 0;
		int pageSize = 20;

		/*sqlを実行*/
		List<Map<String, Object>> list = target.searchActive(offSet, pageSize);

		/*変数を宣言*/
		Object clientId;

		/*空だったらclientIdにnullを代入*/
		if (list.isEmpty()) {
			clientId = null;
		} else {
			clientId = list.get(0).get("client_id");
		}

		/*期待値と実際の値が一致することを確認*/
		assertEquals(null, clientId);
	}

	/**
	 * ClientSerchActive（指定された顧客IDのデータ（false）を取得できる場合）
	 * ※指定された顧客IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した顧客Idが121であること(データが21件の時)
	 **/
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case04/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case04/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void searchActive04() {

		/*ページサイズとオフセットを定義*/
		int offSet = 20;
		int pageSize = 20;

		/*sqlを実行*/
		List<Map<String, Object>> list = target.searchActive(offSet, pageSize);

		/*取得したリストの0番目(20番目)の顧客IDを取得*/
		Object clientId = list.get(0).get("client_id");

		/*期待値と実際の値が一致することを確認*/
		assertEquals(121, clientId);
	}

	/**
	 * countClient（指定された顧客IDのデータ（false）を取得できる場合）
	 * ※指定された顧客IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した件数が1件であること
	 **/
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case01/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case01/after-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void countClient01() {

		/*sqlを実行、件数をresultに代入*/
		int result = target.countClient();

		/*期待値と実際の値が一致することを確認*/
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
	@DatabaseSetup("/testdata/ClientRepositoryTest/case02/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case02/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void countClient02() {

		/*sqlを実行、件数をresultに代入*/
		int result = target.countClient();

		/*期待値と実際の値が一致することを確認*/
		assertEquals(2, result);
	}

	/**
	 * countClient（指定された顧客IDのデータ（false）を取得できない場合）
	 * ※指定された顧客IDのデータが存在しないこと
	 * 異常系テストケース
	 * 期待値
	 * ・取得した件数が0件であること
	 **/
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case03/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case03/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void countClient03() {

		/*sqlを実行、件数をresultに代入*/
		int result = target.countClient();

		/*期待値と実際の値が一致することを確認*/
		assertEquals(0, result);
	}

	/**
	 * countClient（指定された顧客IDのデータ（false）を取得できる場合）
	 * ※指定された顧客IDのデータが存在しないこと
	 * 正常系テストケース
	 * 期待値
	 * ・取得した件数が21件であること
	 **/
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case04/init-list-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case04/init-list-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void countClient04() {

		/*sqlを実行、件数をresultに代入*/
		int result = target.countClient();

		/*期待値と実際の値が一致することを確認*/
		assertEquals(21, result);
	}

	/**
	 * deleteClient（指定された顧客IDのデータを取得できる場合）
	 * ※指定された顧客IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した削除件数が1件であること
	 **/
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case01/init-delete-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case01/after-delete-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void deleteClient01() {

		/*clientIdを定義*/
		String clientId = "104";

		/*sqlを実行、件数をresultに代入*/
		int result = target.deleteClient(clientId);

		/*期待値と実際の値が一致することを確認*/
		assertEquals(1, result);
	}

	/**
	 * deleteClient（指定された顧客IDのデータを取得できる場合）
	 * ※指定された顧客IDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した削除件数が2件であること
	 **/
	/*削除件数2件の時*/
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case02/init-delete-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case02/after-delete-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void deleteClient02() {

		/*clientIdを定義*/
		String clientId = "104";
		String clientId2 = "105";

		/*sqlを実行、件数をresultに代入*/
		int result = target.deleteClient(clientId);
		result += target.deleteClient(clientId2);

		/*期待値と実際の値が一致することを確認*/
		assertEquals(2, result);
	}

	/**
	 * deleteClient（指定された顧客IDのデータを取得できない場合）
	 * ※指定された顧客IDのデータが存在しないこと
	 * 異常系テストケース
	 * 期待値
	 * ・取得した削除件数が0件であること
	 **/
	@Test
	@DatabaseSetup("/testdata/ClientRepositoryTest/case03/init-delete-data") // テスト実行前に初期データを投入
	@ExpectedDatabase(value = "/testdata/ClientRepositoryTest/case03/after-delete-data", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED) // テスト実行後のデータ検証（初期データのままであること）
	public void deleteClient03() {

		/*clientIdを定義*/
		String clientId = "999";

		/*sqlを実行、件数をresultに代入*/
		int result = target.deleteClient(clientId);

		/*期待値と実際の値が一致することを確認*/
		assertEquals(0, result);
	}

}
