//package jp.co.nexus;
//
//import static org.junit.Assert.assertTrue;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.MessageSource;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.ui.ExtendedModelMap;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
//
//import com.nexus.whc.controller.ClientController;
//import com.nexus.whc.models.ClientData;
//import com.nexus.whc.repository.ClientRepository;
//import com.nexus.whc.services.ClientService;
//import com.nexus.whc.services.ExclusiveCheckService;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ContextConfiguration(classes = ClientController.class)
//@TestPropertySource(locations = "classpath:application.properties")
//@SpringBootApplication
//public class ClientControllerTest {
//
//	@MockBean
//	private ClientService mockClientService;
//
//	@MockBean
//	private ExclusiveCheckService lockService;
//
//	@MockBean
//	private ClientData mockClient;
//
//	@MockBean
//	private ClientRepository mockClientRepositpry;
//
//	@InjectMocks
//	private ClientController target;
//
////	@Mock
////	private HttpSession session;
////	
////	@Autowired
////	MessageSource messages;
//	
//	@MockBean
//	MessageSource messages;
//
//	@Before
//	public void initMocks() {
//		MockitoAnnotations.initMocks(this);
//	}
//
//	/**
//	 * clientList(Listを取得できる場合)
//	 * ※削除フラグ=falseのデータが1件以上存在すること
//	 * 正常系テストケース
//	 * mockClientService.searchActiveはmockListの値を返す
//	 * mockClientService.countClient()は1を返す
//	 * 期待値
//	 * model「list」List0番目のclientIdが101であること
//	 * model「currentPage」2が保存されていること
//	 * model「totalPages」1が保存されていること
//	 * model「mode」1が保存されていること
//	 * result "SMSCL001"を返す
//	 */
//	@Test
//	public void clientList01() {
//
//		/*ページサイズとオフセットを定義*/
//		int page = 1;
//		int pageSize = 20;
//
//		/*mockListの作成*/
//		List<Map<String, Object>> mockList = new ArrayList<>();
//
//		/*mapの作成*/
//		Map<String, Object> map = new HashMap<>();
//		map.put("client_id", 101);
//		map.put("client_name", "株式会社エクサス");
//		map.put("open_time", "09:00:00");
//		map.put("close_time", "18:00:00");
//		map.put("rest1_start", "12:00:00");
//		map.put("rest1_end", "13:00:00");
//		map.put("comment", "コメント4");
//		map.put("delete_flg", 0);
//		map.put("created_at", "2023-01-01 00:00:00");
//		map.put("reated_user", "nexus001");
//		map.put("updated_at", "2023-01-01 00:00:00");
//		map.put("updated_user", "nexus001");
//
//		/*mockListにmapを追加*/
//		mockList.add(map);
//
//		/*modelオブジェクトを作成*/
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		/*mockListと2を返すように設定*/
//		Mockito.when(mockClientService.searchActive(page - 1, pageSize)).thenReturn(mockList);
//		Mockito.when(mockClientService.countClient()).thenReturn(2);
//
//		/*メソッドを実行*/
//		String result = target.clientList(page, mockClient, model);
//
//		/*モデルからクライアントリストを取得*/
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("client_list");
//
//		/*取得したリストの0番目の顧客IDを取得*/
//		String clientId = list.get(0).get("client_id").toString();
//
//		/*modelから情報を取得*/
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//		int mode = (int) model.get("mode");
//
//		/*期待値と実際の値が一致することを確認*/
//		assertEquals("101", clientId);
//		assertEquals(2, currentPage);
//		assertEquals(1, totalPages);
//		assertEquals(1, mode);
//		assertEquals("SMSCL001", result);
//	}
//
//	/**
//	 * clientList(Listを取得できる場合)
//	 * ※削除フラグ=falseのデータが2件以上存在すること
//	 * 正常系テストケース
//	 * mockClientService.searchActiveはmockListの値を返す
//	 * mockClientService.countClient()は2を返す
//	 * 期待値
//	 * model「list」List1番目のclientIdが102であること
//	 * model「currentPage」2が保存されていること
//	 * model「totalPages」1が保存されていること
//	 * model「mode」1が保存されていること
//	 * result "SMSCL001"を返す
//	 */
//	@Test
//	public void clientList02() {
//
//		/*ページサイズとオフセットを定義*/
//		int page = 1;
//		int pageSize = 20;
//
//		/*mockListの作成*/
//		List<Map<String, Object>> mockList = new ArrayList<>();
//
//		/*map1の作成*/
//		Map<String, Object> map1 = new HashMap<>();
//		map1.put("client_id", 101);
//		map1.put("client_name", "株式会社エクサス");
//		map1.put("open_time", "09:00:00");
//		map1.put("close_time", "18:00:00");
//		map1.put("rest1_start", "12:00:00");
//		map1.put("rest1_end", "13:00:00");
//		map1.put("comment", "コメント4");
//		map1.put("delete_flg", 0);
//		map1.put("created_at", "2023-01-01 00:00:00");
//		map1.put("reated_user", "nexus001");
//		map1.put("updated_at", "2023-01-01 00:00:00");
//		map1.put("updated_user", "nexus001");
//
//		/*map2の作成*/
//		Map<String, Object> map2 = new HashMap<>();
//		map2.put("client_id", 102);
//		map2.put("client_name", "株式会社エクサス2");
//		map2.put("open_time", "09:00:00");
//		map2.put("close_time", "18:00:00");
//		map2.put("rest1_start", "12:00:00");
//		map2.put("rest1_end", "13:00:00");
//		map2.put("comment", "コメント4");
//		map2.put("delete_flg", 0);
//		map2.put("created_at", "2023-01-01 00:00:00");
//		map2.put("reated_user", "nexus001");
//		map2.put("updated_at", "2023-01-01 00:00:00");
//		map2.put("updated_user", "nexus001");
//
//		/*mockListにmap1とmap2を追加*/
//		mockList.add(map1);
//		mockList.add(map2);
//
//		/*modelオブジェクトを作成*/
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		/*mockListと1を返すように設定*/
//		Mockito.when(mockClientService.searchActive(page - 1, pageSize)).thenReturn(mockList);
//		Mockito.when(mockClientService.countClient()).thenReturn(1);
//
//		/*メソッドを実行*/
//		String result = target.clientList(page, mockClient, model);
//
//		/*モデルからクライアントリストを取得*/
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("client_list");
//
//		/*取得したリストの1番目の顧客IDを取得*/
//		String clientId = list.get(1).get("client_id").toString();
//
//		/*modelから情報を取得*/
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//		int mode = (int) model.get("mode");
//
//		/*期待値と実際の値が一致することを確認*/
//		assertEquals("102", clientId);
//		assertEquals(2, currentPage);
//		assertEquals(1, totalPages);
//		assertEquals(1, mode);
//		assertEquals("SMSCL001", result);
//	}
//
//	/**
//	 * clientList(Listを取得できる場合)
//	 * ※削除フラグ=falseのデータが存在しないこと
//	 * 正常系テストケース
//	 * mockClientService.searchActiveはmockListの値を返す
//	 * mockClientService.countClient()は0を返す
//	 * 期待値
//	 * model「list」空のlistを返すこと
//	 * model「currentPage」2が保存されていること
//	 * model「totalPages」0が保存されていること
//	 * model「mode」1が保存されていること
//	 * result "SMSCL001"を返す
//	 */
//	@Test
//	public void clientList03() {
//
//		/*ページサイズとオフセットを定義*/
//		int page = 1;
//		int pageSize = 20;
//
//		/*modelオブジェクトを作成*/
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		/*mockListと0を返すように設定*/
//		Mockito.when(mockClientService.searchActive(page - 1, pageSize)).thenReturn(Collections.emptyList());
//		Mockito.when(mockClientService.countClient()).thenReturn(0);
//
//		/*メソッドを実行*/
//		String result = target.clientList(page, mockClient, model);
//
//		/*モデルからクライアントリストを取得*/
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("client_list");
//
//		/*modelから情報を取得*/
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//		int mode = (int) model.get("mode");
//
//		/*期待値と実際の値が一致することを確認*/
//		assertTrue(list.isEmpty());
//		assertEquals(2, currentPage);
//		assertEquals(0, totalPages);
//		assertEquals(1, mode);
//		assertEquals("SMSCL001", result);
//	}
//
//	/**
//	 * clientList(Listを取得できる場合)
//	 * ※削除フラグ=falseのデータが存在しないこと
//	 * 正常系テストケース
//	 * mockClientService.searchActiveはmockListの値を返す
//	 * mockClientService.countClient()は21を返す
//	 * 期待値
//	 * model「list」List0番目(20番目)のclientIdが121であること
//	 * model「currentPage」3が保存されていること
//	 * model「totalPages」2が保存されていること
//	 * model「mode」1が保存されていること
//	 * result "SMSCL001"を返す
//	 */
//	@Test
//	public void clientList04() {
//
//		/*ページサイズとオフセットを定義*/
//		int page = 2;
//		int pageSize = 20;
//
//		/*mockListの作成*/
//		List<Map<String, Object>> mockList = new ArrayList<>();
//
//		/*mapの作成*/
//		Map<String, Object> map = new HashMap<>();
//		map.put("client_id", 121);
//		map.put("client_name", "株式会社エクサス21");
//		map.put("open_time", "09:00:00");
//		map.put("close_time", "18:00:00");
//		map.put("rest1_start", "12:00:00");
//		map.put("rest1_end", "13:00:00");
//		map.put("comment", "コメント4");
//		map.put("delete_flg", 0);
//		map.put("created_at", "2023-01-01 00:00:00");
//		map.put("reated_user", "nexus001");
//		map.put("updated_at", "2023-01-01 00:00:00");
//		map.put("updated_user", "nexus001");
//
//		/*mockListにmapを追加*/
//		mockList.add(map);
//
//		/*modelオブジェクトを作成*/
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		/*mockListと21を返すように設定*/
//		Mockito.when(mockClientService.searchActive(page - 1, pageSize)).thenReturn(mockList);
//		Mockito.when(mockClientService.countClient()).thenReturn(21);
//
//		/*メソッドを実行*/
//		String result = target.clientList(page, mockClient, model);
//
//		/*モデルからクライアントリストを取得*/
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("client_list");
//
//		/*取得したリストの0番目(20番目)の顧客IDを取得*/
//		String clientId = list.get(0).get("client_id").toString();
//
//		/*modelから情報を取得*/
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//		int mode = (int) model.get("mode");
//
//		/*期待値と実際の値が一致することを確認*/
//		assertEquals("121", clientId);
//		assertEquals(3, currentPage);
//		assertEquals(2, totalPages);
//		assertEquals(1, mode);
//		assertEquals("SMSCL001", result);
//	}
//
//	/**
//	 * clientList(PostMapping)(Listを1件取得できる場合)
//	 * 正常系テストケース
//	 * mockClientService.searchActiveはmockListの値を返す
//	 * mockClientService.countClient()は1を返す
//	 * 期待値
//	 * model「list」List0番目のclientIdが101であること
//	 * model「currentPage」2が保存されていること
//	 * model「totalPages」1が保存されていること
//	 * model「mode」1が保存されていること
//	 * result "SMSCL001"を返す
//	 */
//	@Test
//	public void clientPostList01() {
//
//		/*ページサイズとオフセットを定義*/
//		int page = 1;
//		int pageSize = 20;
//
//		/*変数を定義*/
//		String buttonType = "exclusiveCheck";
//		String cliId = "101";
//
//		/*mockListの作成*/
//		List<Map<String, Object>> mockList = new ArrayList<>();
//
//		/*mapの作成*/
//		Map<String, Object> map = new HashMap<>();
//		map.put("client_id", 101);
//		map.put("client_name", "株式会社エクサス");
//		map.put("open_time", "09:00:00");
//		map.put("close_time", "18:00:00");
//		map.put("rest1_start", "12:00:00");
//		map.put("rest1_end", "13:00:00");
//		map.put("comment", "コメント4");
//		map.put("delete_flg", 0);
//		map.put("created_at", "2023-01-01 00:00:00");
//		map.put("reated_user", "nexus001");
//		map.put("updated_at", "2023-01-01 00:00:00");
//		map.put("updated_user", "nexus001");
//
//		/*mockListにmapを追加*/
//		mockList.add(map);
//
//		/*modelオブジェクトを作成*/
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		/*mockListと1を返すように設定*/
//		Mockito.when(mockClientService.searchActive(page - 1, pageSize)).thenReturn(mockList);
//		Mockito.when(mockClientService.countClient()).thenReturn(1);
//
//		/*メソッドを実行*/
//		String result = target.clientList(page, mockClient, buttonType, cliId, model);
//
//		/*モデルからクライアントリストを取得*/
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("client_list");
//
//		/*取得したリストの0番目の顧客IDを取得*/
//		String clientId = list.get(0).get("client_id").toString();
//
//		/*modelから情報を取得*/
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//		int mode = (int) model.get("mode");
//
//		/*期待値と実際の値が一致することを確認*/
//		assertEquals("101", clientId);
//		assertEquals(2, currentPage);
//		assertEquals(1, totalPages);
//		assertEquals(1, mode);
//		assertEquals("SMSCL001", result);
//	}
//
//	/**
//	 * clientList(PostMapping)(Listを2件取得できる場合)
//	 * 正常系テストケース
//	 * mockClientService.searchActiveはmockListの値を返す
//	 * mockClientService.countClient()は2を返す
//	 * 期待値
//	 * model「list」List1番目のclientIdが102であること
//	 * model「currentPage」2が保存されていること
//	 * model「totalPages」1が保存されていること
//	 * model「mode」1が保存されていること
//	 * result "SMSCL001"を返す
//	 */
//	@Test
//	public void clientPostList02() {
//
//		/*ページサイズとオフセットを定義*/
//		int page = 1;
//		int pageSize = 20;
//
//		/*変数を定義*/
//		String buttonType = "exclusiveCheck";
//		String cliId = "101";
//
//		/*mockListの作成*/
//		List<Map<String, Object>> mockList = new ArrayList<>();
//
//		/*mapの作成*/
//		Map<String, Object> map1 = new HashMap<>();
//		map1.put("client_id", 101);
//		map1.put("client_name", "株式会社エクサス");
//		map1.put("open_time", "09:00:00");
//		map1.put("close_time", "18:00:00");
//		map1.put("rest1_start", "12:00:00");
//		map1.put("rest1_end", "13:00:00");
//		map1.put("comment", "コメント4");
//		map1.put("delete_flg", 0);
//		map1.put("created_at", "2023-01-01 00:00:00");
//		map1.put("reated_user", "nexus001");
//		map1.put("updated_at", "2023-01-01 00:00:00");
//		map1.put("updated_user", "nexus001");
//
//		/*map2の作成*/
//		Map<String, Object> map2 = new HashMap<>();
//		map2.put("client_id", 102);
//		map2.put("client_name", "株式会社エクサス2");
//		map2.put("open_time", "09:00:00");
//		map2.put("close_time", "18:00:00");
//		map2.put("rest1_start", "12:00:00");
//		map2.put("rest1_end", "13:00:00");
//		map2.put("comment", "コメント4");
//		map2.put("delete_flg", 0);
//		map2.put("created_at", "2023-01-01 00:00:00");
//		map2.put("reated_user", "nexus001");
//		map2.put("updated_at", "2023-01-01 00:00:00");
//		map2.put("updated_user", "nexus001");
//
//		/*mockListにmapを追加*/
//		mockList.add(map1);
//		mockList.add(map2);
//
//		/*modelオブジェクトを作成*/
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		/*mockListと1を返すように設定*/
//		Mockito.when(mockClientService.searchActive(page - 1, pageSize)).thenReturn(mockList);
//		Mockito.when(mockClientService.countClient()).thenReturn(2);
//
//		/*メソッドを実行*/
//		String result = target.clientList(page, mockClient, buttonType, cliId, model);
//
//		/*モデルからクライアントリストを取得*/
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("client_list");
//
//		/*取得したリストの0番目の顧客IDを取得*/
//		String clientId = list.get(1).get("client_id").toString();
//
//		/*modelから情報を取得*/
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//		int mode = (int) model.get("mode");
//
//		/*期待値と実際の値が一致することを確認*/
//		assertEquals("102", clientId);
//		assertEquals(2, currentPage);
//		assertEquals(1, totalPages);
//		assertEquals(1, mode);
//		assertEquals("SMSCL001", result);
//	}
//
//	/**
//	 * clientList(PostMapping)(Listが0件の場合)
//	 * 正常系テストケース
//	 * mockClientService.searchActiveは空のリストを返す
//	 * mockClientService.countEmployee()は0を返す
//	 * 期待値
//	 * model「list」が空であること
//	 * model「currentPage」2が保存されていること
//	 * model「totalPages」0が保存されていること
//	 * model「mode」1が保存されていること
//	 * result "SMSCL001"を返す
//	 */
//	@Test
//	public void clientPostList03() {
//
//		/*ページサイズとオフセットを定義*/
//		int page = 1;
//		int pageSize = 20;
//
//		/*変数を定義*/
//		String buttonType = "exclusiveCheck";
//		String cliId = "101";
//
//		/*modelオブジェクトを作成*/
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		/*mockListと1を返すように設定*/
//		Mockito.when(mockClientService.searchActive(page - 1, pageSize)).thenReturn(Collections.emptyList());
//		Mockito.when(mockClientService.countClient()).thenReturn(0);
//
//		/*メソッドを実行*/
//		String result = target.clientList(page, mockClient, buttonType, cliId, model);
//
//		/*モデルからクライアントリストを取得*/
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("client_list");
//
//		/*modelから情報を取得*/
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//		int mode = (int) model.get("mode");
//
//		/*期待値と実際の値が一致することを確認*/
//		assertTrue(list.isEmpty());
//		assertEquals(2, currentPage);
//		assertEquals(0, totalPages);
//		assertEquals(1, mode);
//		assertEquals("SMSCL001", result);
//	}
//
//	/**
//	 * clientList(PostMapping)(Listを21件取得できる場合)
//	 * 正常系テストケース
//	 * mockClientService.searchActiveはmockListの値を返す
//	 * mockClientService.countClient()は21を返す
//	 * 期待値
//	 * model「list」List0番目のclientIdが121であること
//	 * model「currentPage」3が保存されていること
//	 * model「totalPages」2が保存されていること
//	 * model「mode」1が保存されていること
//	 * result "SMSCL001"を返す
//	 */
//	@Test
//	public void clientPostList04() {
//
//		/*ページサイズとオフセットを定義*/
//		int page = 2;
//		int pageSize = 20;
//
//		/*変数を定義*/
//		String buttonType = "exclusiveCheck";
//		String cliId = "101";
//
//		/*mockListの作成*/
//		List<Map<String, Object>> mockList = new ArrayList<>();
//
//		/*mapの作成*/
//		Map<String, Object> map = new HashMap<>();
//		map.put("client_id", 121);
//		map.put("client_name", "株式会社エクサス21");
//		map.put("open_time", "09:00:00");
//		map.put("close_time", "18:00:00");
//		map.put("rest1_start", "12:00:00");
//		map.put("rest1_end", "13:00:00");
//		map.put("comment", "コメント4");
//		map.put("delete_flg", 0);
//		map.put("created_at", "2023-01-01 00:00:00");
//		map.put("reated_user", "nexus001");
//		map.put("updated_at", "2023-01-01 00:00:00");
//		map.put("updated_user", "nexus001");
//
//		/*mockListにmapを追加*/
//		mockList.add(map);
//
//		/*modelオブジェクトを作成*/
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		/*mockListと1を返すように設定*/
//		Mockito.when(mockClientService.searchActive(page - 1, pageSize)).thenReturn(mockList);
//		Mockito.when(mockClientService.countClient()).thenReturn(21);
//
//		/*メソッドを実行*/
//		String result = target.clientList(page, mockClient, buttonType, cliId, model);
//
//		/*モデルからクライアントリストを取得*/
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("client_list");
//
//		/*取得したリストの0番目の顧客IDを取得*/
//		String clientId = list.get(0).get("client_id").toString();
//
//		/*modelから情報を取得*/
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//		int mode = (int) model.get("mode");
//
//		/*期待値と実際の値が一致することを確認*/
//		assertEquals("121", clientId);
//		assertEquals(3, currentPage);
//		assertEquals(2, totalPages);
//		assertEquals(1, mode);
//		assertEquals("SMSCL001", result);
//	}
//
//	/**
//	 * deleteClient(選択行削除)（指定された顧客IDのデータを取得できる場合）
//	 * ※指定された顧客IDの削除フラグ=falseのデータが1件存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * result "redirect:/client/list"を返す
//	 * deleteResult "1件削除しました"を返す
//	 **/
//	@Test
//	public void deleteClient01() {
//
//		/*変数を定義*/
//		String[] selectCheck = { "1" };
//		String clientId = "1";
//		int tableNumber = 3;
//		int editTableNumber = 3;
//		String loginUserId = "ttts";
//
//		/*attrオブジェクトを作成*/
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//
//		/*1とfalse(排他チェック削除)とfalse(排他チェック編集)を返すように設定*/
//		Mockito.when(mockClientService.deleteClient(clientId)).thenReturn(1);
//		Mockito.when(lockService.ExclusiveCheckDalete(clientId, tableNumber)).thenReturn(false);
//		Mockito.when(lockService.ExclusiveCheckEdited(clientId, loginUserId, editTableNumber)).thenReturn(false);
//
//		/*メソッドを実行*/
//		String result = target.deleteClient(selectCheck, attr);
//
//		/*attrからエラーメッセージを取得*/
//		String deleteResult = (String) attr.getFlashAttributes().get("result");
//
//		/*期待値と実際の値が一致することを確認*/
//		assertEquals("1件削除しました。", deleteResult);
//		assertEquals("redirect:/client/list", result);
//	}
//
//	/**
//	 * deleteClient(選択行削除)（指定された顧客IDのデータを取得できる場合）
//	 * ※指定された顧客IDの削除フラグ=falseのデータが2件存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * result "redirect:/client/list"を返す
//	 * deleteResult "2件削除しました"を返す
//	 **/
//	@Test
//	public void deleteClient02() {
//
//		/*変数を定義*/
//		String[] selectCheck = { "1", "2" }; 
//		String clientId = "1"; 
//		int tableNumber = 3;
//		int editTableNumber = 3;
//		String loginUserId = "ttts";
//
//		/*attrオブジェクトを作成*/
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//
//		/*2とfalse(排他チェック削除)とfalse(排他チェック編集)を返すように設定*/
//		Mockito.when(mockClientService.deleteClient(clientId)).thenReturn(2); 
//		Mockito.when(lockService.ExclusiveCheckDalete(clientId, tableNumber)).thenReturn(false);
//		Mockito.when(lockService.ExclusiveCheckEdited(clientId, loginUserId, editTableNumber)).thenReturn(false);
//
//		/*メソッドを実行*/
//		String result = target.deleteClient(selectCheck, attr);
//
//		/*attrからエラーメッセージを取得*/
//		String deleteResult = (String) attr.getFlashAttributes().get("result");
//
//		/*期待値と実際の値が一致することを確認*/
//		assertEquals("2件削除しました。", deleteResult);
//		assertEquals("redirect:/client/list", result);
//	}
//
////	//みかんみかんみかんできないいいい
////	/**
////	 * deleteClient(PostMapping)（指定された顧客IDのデータ（false）を取得できる場合）
////	 * ※指定された顧客IDの削除フラグ=falseのデータが存在すること
////	 * 正常系テストケース
////	 * 期待値
////	 * ・削除件数が0件の時
////	 **/
////	@Test
////	public void deleteClient03() {
////
////		/*変数を定義*/
////		String[] selectCheck = null;
////		String expectedMessage = "YourMockedMessage";
////		
////		/*attrオブジェクトとmessagesオブジェクトを作成*/
////		RedirectAttributes attr = new RedirectAttributesModelMap();
//////		MessageSource messages = mock(MessageSource.class);
////		MessageSource messages = new MessageSource();
////
////		
////		/*エラーメッセージを返すように設定*/
////	    when(messages.getMessage("COM01W003", null, Locale.JAPAN)).thenReturn(expectedMessage);
////		
////		/*テスト対象のメソッド呼び出し*/
////	    String attributeValue = messages.getMessage("COM01W003", null, Locale.JAPAN);
////
////		/*メソッドを実行*/
////		String result = target.deleteClient(selectCheck, attr);
////		
////		/*期待値と実際の値が一致することを確認*/
////	    assertEquals(expectedMessage, attributeValue);
////		assertEquals("redirect:/client/list", result);
////	}
//	
//	/**
//	 * deleteClient(選択行削除)（指定された顧客IDのデータを取得できる場合）
//	 * ※排他チェック(削除)の結果がtrueのとき
//	 * 異常系テストケース
//	 * 期待値
//	 * exclusiveResultD "該当データはデータはすでに削除されています。"を返す
//	 * result "redirect:/client/list"を返す
//	 **/
//	@Test
//	public void deleteClient04() {
//
//		/*変数を定義*/
//		String[] selectCheck = { "1" };
//		String clientId = "1";
//		int tableNumber = 3;
//		int editTableNumber = 3;
//		String loginUserId = "ttts";
//
//		/*attrオブジェクトを作成*/
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//
//		/*true(排他チェック削除)とfalse(排他チェック編集)を返すように設定*/
//		Mockito.when(lockService.ExclusiveCheckDalete(clientId, tableNumber)).thenReturn(true);
//		Mockito.when(lockService.ExclusiveCheckEdited(clientId, loginUserId, editTableNumber)).thenReturn(false);
//
//		/*メソッドを実行*/
//		String result = target.deleteClient(selectCheck, attr);
//
//		/*attrからエラーメッセージを取得*/
//		String exclusiveResultD = (String) attr.getFlashAttributes().get("result");
//
//		/*期待値と実際の値が一致することを確認*/
//		assertEquals("redirect:/client/list", result);
//		assertEquals("該当データはすでに削除されています。", exclusiveResultD);
//	}
//
//	/**
//	 * deleteClient(選択行削除)（指定された顧客IDのデータを取得できる場合）
//	 * ※排他チェック(編集)の結果がtrueのとき
//	 * 異常系テストケース
//	 * 期待値
//	 * exclusiveResultE "該当データは他のユーザ『』が編集中です。"を返す
//	 * result "redirect:/client/list"を返す
//	 **/
//	@Test
//	public void deleteClient05() {
//
//		/*変数を定義*/
//		String[] selectCheck = { "1" };
//		String clientId = "1";
//		int tableNumber = 3;
//		int editTableNumber = 3;
//		String loginUserId = "ttts";
//
//		/*attrオブジェクトを作成*/
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//
//		/*false(排他チェック削除)とtrue(排他チェック編集)を返すように設定*/
//		Mockito.when(lockService.ExclusiveCheckDalete(clientId, tableNumber)).thenReturn(false);
//		Mockito.when(lockService.ExclusiveCheckEdited(clientId, loginUserId, editTableNumber)).thenReturn(true);
//
//		/*メソッドを実行*/
//		String result = target.deleteClient(selectCheck, attr);
//
//		/*attrからエラーメッセージを取得*/
//		String exclusiveResultE = attr.getFlashAttributes().get("result").toString();
//
//		/*期待値と実際の値が一致することを確認*/
//		assertEquals("redirect:/client/list", result);
//		assertEquals("該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。", exclusiveResultE);
//	}
//
//	/**
//	 * deleteEdit(編集画面からの削除)（指定された顧客IDのデータを取得できる場合）
//	 * ※指定された顧客IDの削除フラグ=falseのデータが1件存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * result "redirect:/client/list"を返す
//	 * deleteResult 1を返す
//	 **/
//	@Test
//	public void deleteEdit01() {
//
//		/*変数を定義*/
//		String clientId = "1";
//		int tableNumber = 3;
//		int editTableNumber = 3;
//		String loginUserId = "ttts";
//
//		/*model,attrオブジェクトを作成*/
//		ExtendedModelMap model = new ExtendedModelMap();
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//
//		/*1とfalse(排他チェック削除)とfalse(排他チェック編集)を返すように設定*/
//		Mockito.when(mockClientService.deleteClient(clientId)).thenReturn(1); // 仮
//		Mockito.when(lockService.ExclusiveCheckDalete(clientId, tableNumber)).thenReturn(false);
//		Mockito.when(lockService.ExclusiveCheckEdited(clientId, loginUserId, editTableNumber)).thenReturn(false);
//
//		/*sqlを実行、件数ををdeleteResultに代入*/
//		int deleteResult = mockClientService.deleteClient(clientId);
//
//		/*メソッドを実行*/
//		String result = target.deleteEdit(clientId, model, attr);
//
//		/*期待値と実際の値が一致することを確認*/
//		assertEquals("redirect:/client/list", result);
//		assertEquals(1, deleteResult);
//	}
//
//	/**
//	 * deleteEdit(編集画面からの削除)（指定された顧客IDのデータを取得できる場合）
//	 * ※指定された顧客IDの削除フラグ=falseのデータが2件存在すること
//	 * 異常系テストケース
//	 * 期待値
//	 * result "redirect:/client/list"を返す
//	 * deleteResult 2を返す
//	 **/
//	@Test
//	public void deleteEdit02() {
//
//		/*変数を定義*/
//		String clientId = "1";
//		int tableNumber = 3;
//		int editTableNumber = 3;
//		String loginUserId = "ttts";
//
//		/*model,attrオブジェクトを作成*/
//		ExtendedModelMap model = new ExtendedModelMap();
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//
//		/*2とfalse(排他チェック削除)とfalse(排他チェック編集)を返すように設定*/
//		Mockito.when(mockClientService.deleteClient(clientId)).thenReturn(2); // 仮
//		Mockito.when(lockService.ExclusiveCheckDalete(clientId, tableNumber)).thenReturn(false);
//		Mockito.when(lockService.ExclusiveCheckEdited(clientId, loginUserId, editTableNumber)).thenReturn(false);
//
//		/*sqlを実行、件数ををdeleteResultに代入*/
//		int deleteResult = mockClientService.deleteClient(clientId);
//
//		/*メソッドを実行*/
//		String result = target.deleteEdit(clientId, model, attr);
//
//		/*期待値と実際の値が一致することを確認*/
//		assertEquals("redirect:/client/list", result);
//		assertEquals(2, deleteResult);
//	}
//
//	/**
//	 * deleteEdit(編集画面からの削除)（指定された顧客IDのデータを取得できる場合）
//	 * ※指定された顧客IDの削除フラグ=falseのデータが存在しないこと
//	 * 異常系テストケース
//	 * 期待値
//	 * result "redirect:/client/list"を返す
//	 * deleteResult 0を返す
//	 **/
//	@Test
//	public void deleteEdit03() {
//
//		/*変数を定義*/
//		String clientId = "1";
//		int tableNumber = 3;
//		int editTableNumber = 3;
//		String loginUserId = "ttts";
//
//		/*model,attrオブジェクトを作成*/
//		ExtendedModelMap model = new ExtendedModelMap();
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//
//		/*2とfalse(排他チェック削除)とfalse(排他チェック編集)を返すように設定*/
//		Mockito.when(mockClientService.deleteClient(clientId)).thenReturn(0); // 仮
//		Mockito.when(lockService.ExclusiveCheckDalete(clientId, tableNumber)).thenReturn(false);
//		Mockito.when(lockService.ExclusiveCheckEdited(clientId, loginUserId, editTableNumber)).thenReturn(false);
//
//		/*sqlを実行、件数ををdeleteResultに代入*/
//		int deleteResult = mockClientService.deleteClient(clientId);
//
//		/*メソッドを実行*/
//		String result = target.deleteEdit(clientId, model, attr);
//
//		/*期待値と実際の値が一致することを確認*/
//		assertEquals("redirect:/client/list", result);
//		assertEquals(0, deleteResult);
//	}
//
//	/**
//	 * deleteEdit(編集画面からの削除)（指定された顧客IDのデータを取得できる場合）
//	 * ※排他チェック(削除)の結果がtrueのとき
//	 * 異常系テストケース
//	 * 期待値
//	 * exclusiveResultD "該当データはデータはすでに削除されています。"を返す
//	 * result "redirect:/client/list"を返す
//	 **/
//	@Test
//	public void deleteEdit04() {
//
//		/*変数を定義*/
//		String clientId = "1";
//		int tableNumber = 3;
//		int editTableNumber = 3;
//		String loginUserId = "ttts";
//
//		/*model,attrオブジェクトを作成*/
//		ExtendedModelMap model = new ExtendedModelMap();
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//
//		/*true(排他チェック削除)とfalse(排他チェック編集)を返すように設定*/
//		Mockito.when(lockService.ExclusiveCheckDalete(clientId, tableNumber)).thenReturn(true);
//		Mockito.when(lockService.ExclusiveCheckEdited(clientId, loginUserId, editTableNumber)).thenReturn(false);
//
//		/*メソッドを実行*/
//		String result = target.deleteEdit(clientId, model, attr);
//
//		/*attrからエラーメッセージを取得*/
//		String exclusiveResultD = (String) attr.getFlashAttributes().get("result");
//
//		/*期待値と実際の値が一致することを確認*/
//		assertEquals("redirect:/client/list", result);
//		assertEquals("該当データはすでに削除されています。", exclusiveResultD);
//	}
//
//	/**
//	 * deleteEdit(編集画面からの削除)（指定された顧客IDのデータを取得できる場合）
//	 * ※排他チェック(編集)の結果がtrueのとき
//	 * 異常系テストケース
//	 * 期待値
//	 * exclusiveResultD "該当データは他のユーザ『』が編集中です。"を返す
//	 * result "redirect:/client/list"を返す
//	 **/
//	@Test
//	public void deleteEdit05() {
//
//		/*変数を定義*/
//		String clientId = "1";
//		int tableNumber = 3;
//		int editTableNumber = 3;
//		String loginUserId = "ttts";
//
//		/*model,attrオブジェクトを作成*/
//		ExtendedModelMap model = new ExtendedModelMap();
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//
//		/*false(排他チェック削除)とtrue(排他チェック編集)を返すように設定*/
//		Mockito.when(lockService.ExclusiveCheckDalete(clientId, tableNumber)).thenReturn(false);
//		Mockito.when(lockService.ExclusiveCheckEdited(clientId, loginUserId, editTableNumber)).thenReturn(true);
//
//		/*メソッドを実行*/
//		String result = target.deleteEdit(clientId, model, attr);
//
//		/*attrからエラーメッセージを取得*/
//		String exclusiveResultE = (String) attr.getFlashAttributes().get("result");
//
//		/*期待値と実際の値が一致することを確認*/
//		assertEquals("redirect:/client/list", result);
//		assertEquals("該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。", exclusiveResultE);
//	}
//
//}
