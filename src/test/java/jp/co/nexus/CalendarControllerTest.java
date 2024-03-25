//package jp.co.nexus;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.http.HttpSession;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.ui.ExtendedModelMap;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
//
//import com.nexus.whc.controller.CalendarController;
//import com.nexus.whc.models.CalendarData;
//import com.nexus.whc.services.CalendarService;
//import com.nexus.whc.services.ExclusiveCheckService;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ContextConfiguration(classes = CalendarController.class)
//@TestPropertySource(locations = "classpath:application.properties")
//@SpringBootApplication
//
//public class CalendarControllerTest {
//
//	@MockBean
//	private CalendarController mockCalendarcController;
//
//	@MockBean
//	private CalendarService mockCalendarService;
//
//	@MockBean
//	private ExclusiveCheckService lockService;
//
//	@MockBean
//	private CalendarData mockCalendar;
//
//	@InjectMocks
//	private CalendarController target;
//
//	@Mock
//	private HttpSession session;
//
//	@Before
//	public void initMocks() {
//		MockitoAnnotations.initMocks(this);
//	}
//
//	/**
//	 * calendarList(Listを取得できる場合)
//	 * ※削除フラグ=falseのデータが1件以上存在すること
//	 * 正常系テストケース
//	 * mockCalendarService.searchCalendarAllはmockListの値を返す
//	 * mockCalendarService.countCalendarは1を返す
//	 * mockCalendarcController.holidayCountは100を返す
//	 * 期待値
//	 * model「list」List0番目のseq_idが1であること
//	 * model「currentPage」2が保存されていること
//	 * model「totalPages」1が保存されていること
//	 * model「listMode」1が保存されていること
//	 * calendarList	"SMSCD001"を返す
//	 * 
//	 * テストソース作成者 長澤
//	 */
//	@Test
//	public void calendarListTest01() {
//
//		int page = 1;
//		int pageSize = 20;
//
//		/*モック用listの作成*/
//		List<Map<String, Object>> mockList = new ArrayList<>();
//
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
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		/*calendarList内で使用されるメソッドのモックを作成*/
//		Mockito.when(mockCalendarService.searchCalendarAll(page - 1, pageSize)).thenReturn(mockList);
//		Mockito.when(mockCalendarService.countCalendar()).thenReturn(2);
//		Mockito.when(mockCalendarcController.holidayCount(map)).thenReturn(100);
//
//		/*テスト対象メソッドを実行*/
//		String result = target.calendarList(page, model);
//
//		/*実行結果の各値を取得*/
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("calendar_list");
//		String id = list.get(0).get("seq_id").toString();
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//		int listMode = (int) model.get("mode");
//
//		/*比較を行いテスト結果を確認*/
//		assertEquals("1", id);
//		assertEquals(2, currentPage);
//		assertEquals(1, totalPages);
//		assertEquals(1, listMode);
//		assertEquals("SMSCD001", result);
//	}
//
//	/**
//	 * calendarList(Listを取得できる場合)
//	 * ※削除フラグ=falseのデータが2件以上存在すること
//	 * 正常系テストケース
//	 * mockCalendarService.searchCalendarAllはmockListの値を返す
//	 * mockCalendarService.countCalendarは1を返す
//	 * mockCalendarcController.holidayCountは100を返す
//	 * 期待値
//	 * model「list」List1番目のseq_idが2であること
//	 * model「currentPage」2が保存されていること
//	 * model「totalPages」1が保存されていること
//	 * model「listMode」1が保存されていること
//	 * calendarList	"SMSCD001"を返す
//	 * 
//	 * テストソース作成者 長澤
//	 */
//	@Test
//	public void calendarListTest02() {
//
//		int page = 1;
//		int pageSize = 20;
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
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		/*calendarList内で使用されるメソッドのモックを作成*/
//		Mockito.when(mockCalendarService.searchCalendarAll(page - 1, pageSize)).thenReturn(mockList);
//		Mockito.when(mockCalendarService.countCalendar()).thenReturn(2);
//		Mockito.when(mockCalendarcController.holidayCount(map1)).thenReturn(100);
//
//		/*テスト対象メソッドを実行*/
//		String result = target.calendarList(page, model);
//
//		/*実行結果の各値を取得*/
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("calendar_list");
//		String id = list.get(1).get("seq_id").toString();
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//		int listMode = (int) model.get("mode");
//
//		/*比較を行いテスト結果を確認*/
//		assertEquals("2", id);
//		assertEquals(2, currentPage);
//		assertEquals(1, totalPages);
//		assertEquals(1, listMode);
//		assertEquals("SMSCD001", result);
//	}
//
//	/**
//	 * calendarList(Listを取得できる場合)
//	 * ※削除フラグ=falseのデータが存在しないこと
//	 * 正常系テストケース
//	 * mockCalendarService.searchCalendarAllはmockListの値を返す
//	 * mockCalendarService.countCalendarは2を返す
//	 * mockCalendarcController.holidayCountは100を返す
//	 * 期待値
//	 * model「list」空のListを返すこと
//	 * model「currentPage」2が保存されていること
//	 * model「totalPages」1が保存されていること
//	 * model「listMode」1が保存されていること
//	 * calendarList	"SMSCD001"を返す
//	 * 
//	 * テストソース作成者 長澤
//	 */
//	@Test
//	public void calendarListTest03() {
//
//		int page = 1;
//		int pageSize = 20;
//
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		/*calendarList内で使用されるメソッドのモックを作成*/
//		Mockito.when(mockCalendarService.searchCalendarAll(page - 1, pageSize)).thenReturn(Collections.emptyList());
//		Mockito.when(mockCalendarService.countCalendar()).thenReturn(0);
//		Mockito.when(mockCalendarcController.holidayCount(Collections.emptyMap())).thenReturn(0);
//
//		String result = target.calendarList(page, model);
//
//		/*実行結果の各値を取得*/
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("calendar_list");
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//		int listMode = (int) model.get("mode");
//
//		/*比較を行いテスト結果を確認*/
//		assertTrue(list.isEmpty());
//		assertEquals(2, currentPage);
//		assertEquals(0, totalPages);
//		assertEquals(1, listMode);
//		assertEquals("SMSCD001", result);
//	}
//
//	/**
//	 * calendarList(Listを取得できる場合)
//	 * ※削除フラグ=falseのデータが20件以上存在すること
//	 * 正常系テストケース
//	 * mockCalendarService.searchCalendarAllはmockListの値を返す
//	 * mockCalendarService.countCalendarは21を返す
//	 * mockCalendarcController.holidayCountは100を返す
//	 * 期待値
//	 * model「list」Lis0番目のseq_idが21であること
//	 * model「currentPage」3が保存されていること
//	 * model「totalPages」2が保存されていること
//	 * model「listMode」1が保存されていること
//	 * calendarList	"SMSCD001"を返す
//	 * 
//	 * テストソース作成者 長澤
//	 */
//	@Test
//	public void calendarListTest04() {
//
//		int page = 2;
//		int pageSize = 20;
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
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		/*calendarList内で使用されるメソッドのモックを作成*/
//		Mockito.when(mockCalendarService.searchCalendarAll(page - 1, pageSize)).thenReturn(mockList);
//		Mockito.when(mockCalendarService.countCalendar()).thenReturn(21);
//		Mockito.when(mockCalendarcController.holidayCount(map)).thenReturn(100);
//
//		/*テスト対象メソッドを実行*/
//		String result = target.calendarList(page, model);
//
//		/*実行結果の各値を取得*/
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("calendar_list");
//		String id = list.get(0).get("seq_id").toString();
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//		int listMode = (int) model.get("mode");
//
//		/*比較を行いテスト結果を確認*/
//		assertEquals("21", id);
//		assertEquals(3, currentPage);
//		assertEquals(2, totalPages);
//		assertEquals(1, listMode);
//		assertEquals("SMSCD001", result);
//	}
//
//	/**
//	 * deletCalendar (selectCheck内の要素を取得できる場合)
//	 * ※指定されたselectCheck内のseq_idの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・削除件数が1件の時
//	 * ・deleteCalendar及びdeleteCalendarDetailの実行回数が1である事。
//	 * ・resultがredirect:/calendar/listである事
//	 * 
//	 * テストソース作成者　長澤
//	 **/
//	@Test
//	public void deleteCalendarTest01() {
//
//		String[] selectCheck = { "1" };
//		String updated_user = "sysdate";
//		int tableNumber = 4;
//		String loginUserId = "11";
//		String REDIRECT_LIST_PAGE = "redirect:/calendar/list";
//
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//
//		/*排他チェック結果のモックを作成*/
//
//		//排他チェック(削除)の結果：false
//		Mockito.when(lockService.ExclusiveCheckDalete(Mockito.anyString(), Mockito.eq(tableNumber)))
//				.thenReturn(false);
//
//		//排他チェック(編集中)の結果：false
//		Mockito.when(lockService.ExclusiveCheckEdited(Mockito.anyString(), Mockito.eq(loginUserId),
//				Mockito.eq(tableNumber))).thenReturn(false);
//
//		/*テスト対象メソッドを実行*/
//		String result = target.deleteCalendarPost(selectCheck, attr);
//
//		/*比較を行いテスト結果を確認*/
//
//		/*削除メソッド実行回数の取得及び比較*/
//		verify(mockCalendarService, times(1)).deleteCalendar(selectCheck, updated_user);
//		verify(mockCalendarService, times(1)).deleteCalendarDetail(selectCheck, updated_user);
//
//		/*戻り値の取得及び比較*/
//		assertEquals(REDIRECT_LIST_PAGE, result);
//
//	}
//
//	/**
//	 * deletCalendar (selectCheck内の要素を取得できる場合)
//	 * ※指定されたselectCheck内のseq_idの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・削除件数が2件の時
//	 * ・deleteCalendar及びdeleteCalendarDetailの実行回数が1である事。
//	 * ・resultがredirect:/calendar/listである事
//	 * 
//	 * テストソース作成者　長澤
//	 **/
//	@Test
//	public void deleteCalendarTest02() {
//
//		String[] selectCheck = { "1", " 2 " };
//		String updated_user = "sysdate";
//		int tableNumber = 4;
//		String loginUserId = "11";
//		String REDIRECT_LIST_PAGE = "redirect:/calendar/list";
//
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//
//		/*排他チェック結果のモックを作成*/
//
//		//排他チェック(削除)の結果：false
//		Mockito.when(lockService.ExclusiveCheckDalete(Mockito.anyString(), Mockito.eq(tableNumber)))
//				.thenReturn(false);
//
//		//排他チェック(編集中)の結果：false
//		Mockito.when(lockService.ExclusiveCheckEdited(Mockito.anyString(), Mockito.eq(loginUserId),
//				Mockito.eq(tableNumber))).thenReturn(false);
//
//		/*テスト対象メソッドを実行*/
//		String result = target.deleteCalendarPost(selectCheck, attr);
//
//		/*比較を行いテスト結果を確認*/
//
//		/*削除メソッド実行回数の取得及び比較*/
//		verify(mockCalendarService, times(1)).deleteCalendar(selectCheck, updated_user);
//		verify(mockCalendarService, times(1)).deleteCalendarDetail(selectCheck, updated_user);
//
//		/*戻り値の取得及び比較*/
//		assertEquals(REDIRECT_LIST_PAGE, result);
//	}
//
//	/**
//	 * deletCalendar (selectCheck内の要素を取得できない場合)
//	 * ※指定されたselectCheck内のseq_idの削除フラグ=falseのデータが存在しない事
//	 * 正常系テストケース
//	 * 期待値
//	 * ・削除件数が0件の時
//	 * ・deleteCalendar及びdeleteCalendarDetailの実行回数が0である事。
//	 * ・deleteResultが「対象が選択されていません。対象を選択してください。」である事
//	 * ・resultがredirect:/calendar/listである事
//	 * 
//	 * テストソース作成者　長澤
//	 **/
//	@Test
//	public void deleteCalendarTest03() {
//
//		String[] selectCheck = null;
//		String updated_user = "sysdate";
//		int tableNumber = 4;
//		String loginUserId = "11";
//		String REDIRECT_LIST_PAGE = "redirect:/calendar/list";
//
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//
//		/*排他チェック結果のモックを作成*/
//
//		//排他チェック(削除)の結果：false
//		Mockito.when(lockService.ExclusiveCheckDalete(Mockito.anyString(), Mockito.eq(tableNumber)))
//				.thenReturn(false);
//
//		//排他チェック(編集中)の結果：false
//		Mockito.when(lockService.ExclusiveCheckEdited(Mockito.anyString(), Mockito.eq(loginUserId),
//				Mockito.eq(tableNumber))).thenReturn(false);
//
//		/*テスト対象メソッドを実行*/
//		String result = target.deleteCalendarPost(selectCheck, attr);
//
//		/*比較を行いテスト結果を確認*/
//
//		/*削除メソッド実行回数の取得及び比較*/
//		verify(mockCalendarService, times(0)).deleteCalendar(selectCheck, updated_user);
//		verify(mockCalendarService, times(0)).deleteCalendarDetail(selectCheck, updated_user);
//		
//		/*attrからエラーメッセージを取得及び比較*/
//		String deleteResult = (String) attr.getFlashAttributes().get("result");
//		assertEquals("対象が選択されていません。対象を選択してください。", deleteResult);
//
//		/*戻り値の取得及び比較*/
//		assertEquals(REDIRECT_LIST_PAGE, result);
//	}
//
//	/**
//	 * deletCalendar (selectCheck内の要素を取得できる場合)
//	 * ※指定されたselectCheck内のseq_idの削除フラグ=falseのデータが存在しない事
//	 * 異常系テストケース
//	 * 期待値
//	 * ・削除件数が0件の時
//	 * ・deleteCalendar及びdeleteCalendarDetailの実行回数が0である事。
//	 * ・deleteResultが「該当データはすでに削除されています。」である事
//	 * ・resultがredirect:/calendar/listである事
//	 * 
//	 * テストソース作成者　長澤
//	 **/
//	@Test
//	public void deleteCalendarTest04() {
//
//		String[] selectCheck = { "1" };
//		String updated_user = "sysdate";
//		int tableNumber = 4;
//		String loginUserId = "11";
//		String REDIRECT_LIST_PAGE = "redirect:/calendar/list";
//
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//
//		/*排他チェック結果のモックを作成*/
//
//		//排他チェック(削除)の結果：false
//		Mockito.when(lockService.ExclusiveCheckDalete(Mockito.anyString(), Mockito.eq(tableNumber)))
//				.thenReturn(true);
//
//		//排他チェック(編集中)の結果：false
//		Mockito.when(lockService.ExclusiveCheckEdited(Mockito.anyString(), Mockito.eq(updated_user),
//				Mockito.eq(tableNumber))).thenReturn(false);
//
//		/*テスト対象メソッドを実行*/
//		String result = target.deleteCalendarPost(selectCheck, attr);
//
//		/*比較を行いテスト結果を確認*/
//
//		/*削除メソッド実行回数の取得及び比較*/
//		verify(mockCalendarService, times(0)).deleteCalendar(selectCheck, updated_user);
//		verify(mockCalendarService, times(0)).deleteCalendarDetail(selectCheck, updated_user);
//
//		/*attrからエラーメッセージを取得及び比較*/
//		String deleteResult = (String) attr.getFlashAttributes().get("result");
//		assertEquals("該当データはすでに削除されています。", deleteResult);
//
//		/*戻り値の取得及び比較*/
//		assertEquals(REDIRECT_LIST_PAGE, result);
//	}
//
//	/**
//	 * deletCalendar (selectCheck内の要素を取得できる場合)
//	 * ※指定されたselectCheck内のseq_idの削除フラグ=falseのデータが存在する事
//	 * 異常系テストケース
//	 * 期待値
//	 * ・削除件数が0件の時
//	 * ・deleteCalendar及びdeleteCalendarDetailの実行回数が0である事。
//	 * ・deleteResultが「該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。」である事
//	 * ・resultがredirect:/calendar/listである事
//	 * 
//	 * テストソース作成者　長澤
//	 **/
//	@Test
//	public void deleteCalendarTest05() {
//
//		String[] selectCheck = { "1" };
//		String updated_user = "sysdate";
//		int tableNumber = 4;
//		String loginUserId = "11";
//		String REDIRECT_LIST_PAGE = "redirect:/calendar/list";
//
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//
//		/*排他チェック結果のモックを作成*/
//
//		//排他チェック(削除)の結果：false
//		Mockito.when(lockService.ExclusiveCheckDalete(Mockito.anyString(), Mockito.eq(tableNumber)))
//				.thenReturn(false);
//
//		//排他チェック(編集中)の結果：false
//		Mockito.when(lockService.ExclusiveCheckEdited(Mockito.anyString(), Mockito.eq(updated_user),
//				Mockito.eq(tableNumber))).thenReturn(true);
//
//		/*テスト対象メソッドを実行*/
//		String result = target.deleteCalendarPost(selectCheck, attr);
//
//		/*比較を行いテスト結果を確認*/
//
//		/*削除メソッド実行回数の取得及び比較*/
//		verify(mockCalendarService, times(0)).deleteCalendar(selectCheck, updated_user);
//		verify(mockCalendarService, times(0)).deleteCalendarDetail(selectCheck, updated_user);
//
//		/*attrからエラーメッセージを取得及び比較*/
//		String deleteResult = (String) attr.getFlashAttributes().get("result");
//		assertEquals("該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。", deleteResult);
//
//		/*戻り値の取得及び比較*/
//		assertEquals(REDIRECT_LIST_PAGE, result);
//	}
//
//}