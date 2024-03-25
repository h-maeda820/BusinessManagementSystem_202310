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
//import javax.servlet.http.HttpSession;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.ui.ExtendedModelMap;
//
//import com.nexus.whc.controller.UserController;
//import com.nexus.whc.models.Filelink;
//import com.nexus.whc.services.ExclusiveCheckService;
//import com.nexus.whc.services.UserServices;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ContextConfiguration(classes = UserController.class)
//@TestPropertySource(locations = "classpath:application.properties")
//@SpringBootApplication
//public class UserControllerTest {
//
//	@MockBean
//	private UserServices mockUserServices;
//
//	@InjectMocks
//	private UserController target;
//
//	@MockBean
//	private Filelink mockUser;
//
//	@MockBean
//	private ExclusiveCheckService mockExclusiveCheckService;
//
//	@Mock
//	private HttpSession session;
//
//	@BeforeEach
//	public void initMocks() {
//		MockitoAnnotations.initMocks(this);
//	}
//
//	/**
//	 * ★一覧表示機能
//	 * ・userListGet
//	 * *削除機能
//	 * ・deleteUserPost
//	 * ※削除機能に関しては排他チェックテスト実施
//	 */
//
//	/**
//	 * userList(Listを取得できる場合)
//	 * ※削除フラグ=falseのデータが1件以上存在すること
//	 * 正常系テストケース
//	 * mockUserServices.searchActive()はmockActinveListを返す
//	 * mockUserServices.userList()はmockUserListを返す
//	 * 期待値
//	 * model「list」List0番目のseq_idが101であること
//	 * model「currentPage」2が保存されていること
//	 * model「totalPages」1が保存されていること
//	 * model「mode」1が保存されていること
//	 * userList	LIST_PAGE("SMSUS001")を返す
//	 */
//	@Test
//	public void userListGet01() {
//
//		int page = 1;
//		int pageSize = 20;
//		String LIST_PAGE = "SMSUS001";
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		List<Map<String, Object>> mockActinveList = new ArrayList<>();
//		Map<String, Object> map = new HashMap<>();
//		map.put("user_id", "201");
//		map.put("user_name", "A");
//		map.put("seq_id", 101);
//		map.put("mail_address", "test01@nexus-nt.co.jp");
//		map.put("auth_id", 301);
//		map.put("delete_flg", 0);
//		mockActinveList.add(map);
//
//		// Mapを使ったリストの作成
//		List<Map<String, Object>> mockUserList = new ArrayList<>();
//		// Mapを作成し、整数値1を持つ要素を追加
//		Map<String, Object> userMap = new HashMap<>();
//		userMap.put("id", 101); // "id"というキーに1という値をセット
//		mockUserList.add(userMap); // リストにMapを追加
//
//		Mockito.when(mockUserServices.searchActive(page - 1, pageSize)).thenReturn(mockActinveList);
//		Mockito.when(mockUserServices.userList()).thenReturn(mockUserList);
//
//		String result = target.userList(page, model);
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("list");
//
//		int seqId = (int) list.get(0).get("seq_id");
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//		int mode = (int) model.get("mode");
//
//		assertEquals(101, seqId);
//		assertEquals(2, currentPage);
//		assertEquals(1, totalPages);
//		assertEquals(1, mode);
//		assertEquals(LIST_PAGE, result);
//	}
//
//	/**
//	 * userList(Listを取得できる場合)
//	 * ※削除フラグ=falseのデータが2件以上存在すること
//	 * 正常系テストケース
//	 * mockUserServices.searchActive()はmockActinveListを返す
//	 * mockUserServices.userList()はmockUserListを返す
//	 * 期待値
//	 * model「list」List1番目のseq_idが102であること
//	 * model「currentPage」2が保存されていること
//	 * model「totalPages」1が保存されていること
//	 * model「mode」1が保存されていること
//	 * userList	LIST_PAGE("SMSUS001")を返す
//	 */
//	@Test
//	public void userListGet02() {
//
//		int page = 1;
//		int pageSize = 20;
//		String LIST_PAGE = "SMSUS001";
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		List<Map<String, Object>> mockActinveList = new ArrayList<>();
//		Map<String, Object> map1 = new HashMap<>();
//		map1.put("user_id", "201");
//		map1.put("user_name", "A");
//		map1.put("seq_id", 101);
//		map1.put("mail_address", "test01@nexus-nt.co.jp");
//		map1.put("auth_id", 301);
//		map1.put("delete_flg", 0);
//		mockActinveList.add(map1);
//
//		Map<String, Object> map2 = new HashMap<>();
//		map2.put("user_id", "202");
//		map2.put("user_name", "B");
//		map2.put("seq_id", 102);
//		map2.put("mail_address", "test02@nexus-nt.co.jp");
//		map2.put("auth_id", 302);
//		map2.put("delete_flg", 0);
//		mockActinveList.add(map2);
//
//		List<Map<String, Object>> mockUserList = new ArrayList<>();
//		Map<String, Object> userMap = new HashMap<>();
//		userMap.put("id", 101);
//		userMap.put("id", 102);
//		mockUserList.add(userMap);
//
//		Mockito.when(mockUserServices.searchActive(page - 1, pageSize)).thenReturn(mockActinveList);
//		Mockito.when(mockUserServices.userList()).thenReturn(mockUserList);
//
//		String result = target.userList(page, model);
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("list");
//
//		int seqId = (int) list.get(1).get("seq_id");
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//		int mode = (int) model.get("mode");
//
//		assertEquals(102, seqId);
//		assertEquals(2, currentPage);
//		assertEquals(1, totalPages);
//		assertEquals(1, mode);
//		assertEquals(LIST_PAGE, result);
//	}
//
//	/**
//	 * userList(Listを取得できる場合)
//	 * ※削除フラグ=falseのデータが存在しないこと
//	 * 正常系テストケース
//	 * mockUserServices.searchActive()はmockActinveListを返す
//	 * mockUserServices.userList()はmockUserListを返す
//	 * 期待値
//	 * model「list」空のListを返すこと
//	 * model「currentPage」2が保存されていること
//	 * model「totalPages」0が保存されていること
//	 * model「mode」1が保存されていること
//	 * userList	LIST_PAGE("SMSUS001")を返す
//	 */
//	@Test
//	public void userListGet03() {
//
//		int page = 1;
//		int pageSize = 20;
//		String LIST_PAGE = "SMSUS001";
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		// Mapを使ったリストの作成
//		List<Map<String, Object>> mockUserList = new ArrayList<>();
//
//		Mockito.when(mockUserServices.searchActive(page - 1, pageSize)).thenReturn(Collections.emptyList());
//		Mockito.when(mockUserServices.userList()).thenReturn(mockUserList);
//
//		String result = target.userList(page, model);
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("list");
//
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//		int mode = (int) model.get("mode");
//
//		assertTrue(list.isEmpty());
//		assertEquals(2, currentPage);
//		assertEquals(0, totalPages);
//		assertEquals(1, mode);
//		assertEquals(LIST_PAGE, result);
//	}
//
//	/**
//	 * UserList(Listを取得できる場合)
//	 * 削除フラグ=falseのデータが21件以上存在すること
//	 * 正常系テストケース
//	 * mockUserServices.searchActive()はmockActinveListを返す
//	 * mockUserServices.userList()はmockUserListを返す
//	 * 期待値
//	 * model「list」List20番目のseq_idが121であること
//	 * model「currentPage」2が保存されていること
//	 * model「totalPages」0が保存されていること
//	 * model「mode」1が保存されていること
//	 * userList LIST_PAGE("SMSUS001")を返す
//	 */
//	@Test
//	public void userListGet04() {
//
//		int page = 2;
//		int pageSize = 20;
//		String LIST_PAGE = "SMSUS001";
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		List<Map<String, Object>> mockActinveList = new ArrayList<>();
//		Map<String, Object> map1 = new HashMap<>();
//		map1.put("user_id", "221");
//		map1.put("user_name", "Z");
//		map1.put("seq_id", 121);
//		map1.put("mail_address", "test21@nexus-nt.co.jp");
//		map1.put("auth_id", 321);
//		map1.put("delete_flg", 0);
//		mockActinveList.add(map1);
//
//		List<Map<String, Object>> mockUserList = new ArrayList<>();
//		for (int i = 0; i < 21; i++) {
//			Map<String, Object> userMap = new HashMap<>();
//			userMap.put("id", i + 101);
//			mockUserList.add(userMap);
//		}
//
//		Mockito.when(mockUserServices.searchActive(page - 1, pageSize)).thenReturn(mockActinveList);
//		Mockito.when(mockUserServices.userList()).thenReturn(mockUserList);
//
//		String result = target.userList(page, model);
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("list");
//
//		int seqId = (int) list.get(0).get("seq_id");
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//		int mode = (int) model.get("mode");
//
//		assertEquals(121, seqId);
//		assertEquals(3, currentPage);
//		assertEquals(2, totalPages);
//		assertEquals(1, mode);
//		assertEquals(LIST_PAGE, result);
//	}
//
//	/**
//	 * deleteUser（指定されたユーザIDのデータを取得できる場合）
//	 * ※指定されたユーザIDの削除フラグ=falseのデータが1件存在すること
//	 * mockExclusiveCheckService.ExclusiveCheckDalete()はfalseを返す
//	 * mockExclusiveCheckService.ExclusiveCheckEdited()はfalseを返す
//	 * mockUserServices.daleteUser()はvoidの為、戻り値無し
//	 * 正常系テストケース
//	 * 期待値
//	 * mockUserServices.daleteUser()のメソッドが1回実行されているか
//	 * daleteUser REDIRECT_LIST_PAGE("redirect:/user/list")を返す
//	 **/
//	/*@Test
//	public void deleteUserPost01() {
//		String[] selectCheck = { "101" };
//		int tableNumber = 1;
//		String loginUserId = "11";
//		String REDIRECT_LIST_PAGE = "redirect:/user/list";
//	
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//	
//		//排他チェック(削除)の結果：false
//		Mockito.when(mockExclusiveCheckService.ExclusiveCheckDalete(Mockito.anyString(), Mockito.eq(tableNumber)))
//				.thenReturn(false);
//		//排他チェック(編集中)の結果：false
//		Mockito.when(mockExclusiveCheckService.ExclusiveCheckEdited(Mockito.anyString(), Mockito.eq(loginUserId),
//				Mockito.eq(tableNumber))).thenReturn(false);
//	
//		String result = target.daleteUser(selectCheck, attr);
//		verify(mockUserServices, times(1)).daleteUser(Mockito.anyString());
//		assertEquals(REDIRECT_LIST_PAGE, result);
//	
//	}*/
//
//	/**
//	 * deleteUser（指定されたユーザIDのデータを取得できる場合）
//	 * ※指定されたユーザIDの削除フラグ=falseのデータが2件存在すること
//	 * mockExclusiveCheckService.ExclusiveCheckDalete()はfalseを返す
//	 * mockExclusiveCheckService.ExclusiveCheckEdited()はfalseを返す
//	 * mockUserServices.daleteUser()はvoidの為、戻り値無し
//	 * 正常系テストケース
//	 * 期待値
//	 * mockUserServices.daleteUser()のメソッドが2回実行されているか
//	 * daleteUser REDIRECT_LIST_PAGE("redirect:/user/list")を返す
//	 **/
//	/*@Test
//		public void deleteUserPost02() {
//			String[] selectCheck = { "101", "102" };
//			int tableNumber = 1;
//			String loginUserId = "11";
//			String REDIRECT_LIST_PAGE = "redirect:/user/list";
//		
//			RedirectAttributes attr = new RedirectAttributesModelMap();
//			//排他チェック(削除)：false
//			Mockito.when(mockExclusiveCheckService.ExclusiveCheckDalete(Mockito.anyString(), Mockito.eq(tableNumber)))
//					.thenReturn(false);
//			//排他チェック(編集中)：false
//			Mockito.when(mockExclusiveCheckService.ExclusiveCheckEdited(Mockito.anyString(), Mockito.eq(loginUserId),
//					Mockito.eq(tableNumber))).thenReturn(false);
//		
//			String result = target.daleteUser(selectCheck, attr);
//			verify(mockUserServices, times(2)).daleteUser(Mockito.anyString());
//			assertEquals(REDIRECT_LIST_PAGE, result);
//	
//		}*/
//
//	/**
//	 * deleteUser（指定されたユーザIDのデータを取得でない場合）
//	 * ※指定されたユーザIDの削除フラグ=falseのデータが存在しないこと
//	 * mockExclusiveCheckService.ExclusiveCheckDalete()はfalseを返す
//	 * mockExclusiveCheckService.ExclusiveCheckEdited()はfalseを返す
//	 * mockUserServices.daleteUser()はvoidの為、戻り値無し
//	 * 正常系テストケース
//	 * 期待値
//	 * mockUserServices.daleteUser()のメソッドが実行がされてないか
//	 * daleteUser REDIRECT_LIST_PAGE("redirect:/user/list")を返す
//	 **/
//	/*@Test
//	public void deleteUserPost03() {
//		String[] selectCheck = null;
//		String REDIRECT_LIST_PAGE = "redirect:/user/list";
//	
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//	
//		String result = target.daleteUser(selectCheck, attr);
//		//フラッシュスコープに保存されたエラー文を取得
//		String error = attr.getFlashAttributes().get("result").toString();
//		verify(mockUserServices, times(0)).daleteUser(Mockito.anyString());
//		assertEquals("対象が選択されていません。対象を選択してください。", error);
//		assertEquals(REDIRECT_LIST_PAGE, result);
//	}*/
//	/**
//	 * deleteUser（指定されたユーザIDが取得できる場合）
//	 * ※指定されたユーザIDの削除フラグ=falseのデータが存在しないこと
//	 * ※排他チェック(削除)の結果がtrueのとき
//	 * mockExclusiveCheckService.ExclusiveCheckDalete()はtrueを返す
//	 * mockExclusiveCheckService.ExclusiveCheckEdited()はfalseを返す
//	 * mockUserServices.daleteUser()はvoidの為、戻り値無し
//	 * 正常系テストケース
//	 * 期待値
//	 * mockUserServices.daleteUser()のメソッドが実行がされてないか
//	 * 「error」該当データはすでに削除されています。が保存されていること。
//	 * daleteUser REDIRECT_LIST_PAGE("redirect:/user/list")を返す
//	 **/
//
//	/*@Test
//	public void deleteUserPost04() {
//		String[] selectCheck = {"101"};
//		int tableNumber = 1;
//		String loginUserId = "11";
//		String REDIRECT_LIST_PAGE = "redirect:/user/list";
//		
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//		//排他チェック(削除)：true
//		Mockito.when(mockExclusiveCheckService.ExclusiveCheckDalete(Mockito.anyString(), Mockito.eq(tableNumber)))
//		.thenReturn(true);
//		//排他チェック(編集中)：false
//		Mockito.when(mockExclusiveCheckService.ExclusiveCheckEdited(Mockito.anyString(), Mockito.eq(loginUserId),
//				Mockito.eq(tableNumber))).thenReturn(false);
//		
//		String result = target.daleteUser(selectCheck, attr);
//		//フラッシュスコープに保存されたエラー文を取得
//		String error = attr.getFlashAttributes().get("result").toString();
//		
//		verify(mockUserServices, times(0)).daleteUser(Mockito.anyString());
//		assertEquals("該当データはすでに削除されています。", error);
//		assertEquals(REDIRECT_LIST_PAGE, result);
//	}*/
//	/**
//	 * deleteUser（指定されたユーザIDのデータを取得できる場合）
//	 * ※指定されたユーザIDの削除フラグ=falseのデータが存在しないこと
//	 * mockExclusiveCheckService.ExclusiveCheckDalete()はfalseを返す
//	 * mockExclusiveCheckService.ExclusiveCheckEdited()はtrueを返す
//	 * mockUserServices.daleteUser()はvoidの為、戻り値無し
//	 * 正常系テストケース
//	 * 期待値
//	 * mockUserServices.daleteUser()のメソッドが実行がされてないか
//	 * mockExclusiveCheckService.ExclusiveLockDalete()が1回実行されているか
//	 * 「error」"該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。"が保存されていること。
//	 * daleteUser 「result」REDIRECT_LIST_PAGE("redirect:/user/list")を返す
//	 **/
//	/*@Test
//	public void deleteUserPost05() {
//		String[] selectCheck = {"101"};
//		int tableNumber = 1;
//		String loginUserId = "11";
//		String REDIRECT_LIST_PAGE = "redirect:/user/list";
//		
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//		//排他チェック(削除)：false
//		Mockito.when(mockExclusiveCheckService.ExclusiveCheckDalete(Mockito.anyString(), Mockito.eq(tableNumber)))
//		.thenReturn(false);
//		//排他チェック(編集中)：true
//		Mockito.when(mockExclusiveCheckService.ExclusiveCheckEdited(Mockito.anyString(), Mockito.eq(loginUserId),
//				Mockito.eq(tableNumber))).thenReturn(true);
//		
//		テスト終了後に削除機能の引数を変更しました
//		String result = target.daleteUser(selectCheck, attr);
//		//フラッシュスコープに保存されたエラー文を取得
//		String error = attr.getFlashAttributes().get("result").toString();
//		
//		verify(mockUserServices, times(0)).daleteUser(Mockito.anyString());
//		verify(mockExclusiveCheckService, times(1)).ExclusiveLockDalete(Mockito.anyString(), Mockito.eq(loginUserId),
//				Mockito.eq(tableNumber));
//		assertEquals( "該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。", error);
//		assertEquals(REDIRECT_LIST_PAGE, result);
//	}*/
//
//}
