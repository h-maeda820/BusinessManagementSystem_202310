//package jp.co.nexus;
//
//import static org.junit.Assert.assertTrue;
//import static org.junit.jupiter.api.Assertions.assertEquals;
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
//import com.nexus.whc.controller.EmployeeController;
//import com.nexus.whc.models.EmployeeDate;
//import com.nexus.whc.services.EmployeeService;
//import com.nexus.whc.services.ExclusiveCheckService;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ContextConfiguration(classes = EmployeeController.class)
//@TestPropertySource(locations = "classpath:application.properties")
//@SpringBootApplication
//public class EmployeeControllerTest {
//
//	@MockBean
//	private EmployeeService mockEmployeeService;
//
//	@MockBean
//	private ExclusiveCheckService mockExclusiveCheckService;
//
//	@MockBean
//	private EmployeeDate mockEmployeeDate;
//
//	@InjectMocks
//	private EmployeeController target;
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
//	 * employeeList(Listを取得できる場合)
//	 * ※削除フラグ=falseのデータが1件以上存在すること
//	 * 正常系テストケース
//	 * mockEmployeeService.searchActiveはmockListの値を返す
//	 * mockEmployeeService.countEmployee()は1を返す
//	 * 期待値
//	 * model「list」List0番目のemployee_idが1であること
//	 * model「currentPage」2が保存されていること
//	 * model「totalPages」1が保存されていること
//	 * employeeList	"SMSEM001"を返す
//	 */
//	@Test
//	public void listGet01() {
//
//		int page = 1;
//		int pageSize = 20;
//
//		List<Map<String, Object>> mockList = new ArrayList<>();
//
//		Map<String, Object> map = new HashMap<>();
//		map.put("employee_id", 1);
//		map.put("employee_name", "田中零郎1");
//		map.put("client_id", 101);
//		map.put("client_name", "株式会社アクサス");
//		map.put("hourly_wage", 1);
//		map.put("paid_holiday_std", "2023-07-01");
//		map.put("remaind_this_year", null);
//		map.put("holiday_date", null);
//		map.put("application_class", null);
//
//		mockList.add(map);
//
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		Mockito.when(mockEmployeeService.searchActive(page - 1, pageSize)).thenReturn(mockList);
//		Mockito.when(mockEmployeeService.countEmployee()).thenReturn(1);
//
//		String result = target.employeeList(page, model);
//
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("list");
//		String empId = list.get(0).get("employee_id").toString();
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//
//		assertEquals("1", empId);
//		assertEquals(2, currentPage);
//		assertEquals(1, totalPages);
//
//		assertEquals("SMSEM001", result);
//	}
//
//	/**
//	 * employeeList(Listを取得できる場合)
//	 * ※削除フラグ=falseのデータが2件以上存在すること
//	 * 正常系テストケース
//	 * mockEmployeeService.searchActiveはmockListの値を返す
//	 * mockEmployeeService.countEmployee()は1を返す
//	 * 期待値
//	 * model「list」List1番目のemployee_idが2であること
//	 * model「currentPage」2が保存されていること
//	 * model「totalPages」1が保存されていること
//	 * employeeList	"SMSEM001"を返す
//	 */
//	@Test
//	public void listGet02() {
//
//		int page = 1;
//		int pageSize = 20;
//
//		List<Map<String, Object>> mockList = new ArrayList<>();
//
//		Map<String, Object> map1 = new HashMap<>();
//		map1.put("employee_id", 1);
//		map1.put("employee_name", "田中零郎1");
//		map1.put("client_id", 101);
//		map1.put("client_name", "株式会社アクサス");
//		map1.put("hourly_wage", 1);
//		map1.put("paid_holiday_std", "2023-07-01");
//		map1.put("remaind_this_year", null);
//		map1.put("holiday_date", null);
//		map1.put("application_class", null);
//
//		Map<String, Object> map2 = new HashMap<>();
//		map2.put("employee_id", 2);
//		map2.put("employee_name", "田中零郎2");
//		map2.put("client_id", 102);
//		map2.put("client_name", "株式会社イクサス");
//		map2.put("hourly_wage", 1);
//		map2.put("paid_holiday_std", "2023-07-01");
//		map2.put("remaind_te", null);
//		map2.put("application_class", null);
//
//		mockList.add(map1);
//		mockList.add(map2);
//
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		Mockito.when(mockEmployeeService.searchActive(page - 1, pageSize)).thenReturn(mockList);
//		Mockito.when(mockEmployeeService.countEmployee()).thenReturn(2);
//
//		String result = target.employeeList(page, model);
//
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("list");
//		String empId = list.get(1).get("employee_id").toString();
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//
//		assertEquals("2", empId);
//		assertEquals(2, currentPage);
//		assertEquals(1, totalPages);
//
//		assertEquals("SMSEM001", result);
//	}
//
//	/**
//	 * employeeList(Listを取得できない場合)
//	 * ※削除フラグ=falseのデータが存在しないこと
//	 * 正常系テストケース
//	 * mockEmployeeService.searchActiveはmockListの値を返す
//	 * mockEmployeeService.countEmployee()は1を返す
//	 * 期待値
//	 * model「list」空のListを返すこと
//	 * model「currentPage」2が保存されていること
//	 * model「totalPages」0が保存されていること
//	 * employeeList	"SMSEM001"を返す
//	 */
//	@Test
//	public void listGet03() {
//
//		int page = 1;
//		int pageSize = 20;
//
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		Mockito.when(mockEmployeeService.searchActive(page - 1, pageSize)).thenReturn(Collections.emptyList());
//		Mockito.when(mockEmployeeService.countEmployee()).thenReturn(0);
//
//		String result = target.employeeList(page, model);
//
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("list");
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//
//		assertTrue(list.isEmpty());
//		assertEquals(2, currentPage);
//		assertEquals(0, totalPages);
//
//		assertEquals("SMSEM001", result);
//	}
//
//	/**
//	 * employeeList(Listを取得できる場合)
//	 * ※削除フラグ=falseのデータが21件以上存在すること
//	 * 正常系テストケース
//	 * mockEmployeeService.searchActiveはmockListの値を返す
//	 * mockEmployeeService.countEmployee()は21を返す
//	 * 期待値
//	 * model「list」List0番目のemployee_idが21であること
//	 * model「currentPage」3が保存されていること
//	 * model「totalPages」2が保存されていること
//	 * employeeList	"SMSEM001"を返す
//	 */
//	@Test
//	public void listGet04() {
//
//		int page = 2;
//		int pageSize = 20;
//
//		List<Map<String, Object>> mockList = new ArrayList<>();
//
//		Map<String, Object> map = new HashMap<>();
//		map.put("employee_id", 21);
//		map.put("employee_name", "田中零郎21");
//		map.put("client_id", 121);
//		map.put("client_name", "株式会社ナクサス");
//		map.put("hourly_wage", 1);
//		map.put("paid_holiday_std", "2023-07-01");
//		map.put("remaind_this_year", null);
//		map.put("holiday_date", null);
//		map.put("application_class", null);
//
//		mockList.add(map);
//
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		Mockito.when(mockEmployeeService.searchActive(page - 1, pageSize)).thenReturn(mockList);
//		Mockito.when(mockEmployeeService.countEmployee()).thenReturn(21);
//
//		String result = target.employeeList(page, model);
//
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("list");
//		String empId = list.get(0).get("employee_id").toString();
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//
//		assertEquals("21", empId);
//		assertEquals(3, currentPage);
//		assertEquals(2, totalPages);
//
//		assertEquals("SMSEM001", result);
//	}
//
//	/**
//	 * employeePostList(Listを取得できる場合)
//	 * 正常系テストケース
//	 * mockEmployeeService.searchActiveはmockListの値を返す
//	 * mockEmployeeService.countEmployee()は1を返す
//	 * 期待値
//	 * model「list」List0番目のemployee_idが1であること
//	 * model「currentPage」2が保存されていること
//	 * model「totalPages」1が保存されていること
//	 * employeeList	"SMSEM001"を返す
//	 */
//	@Test
//	public void listPost01() {
//
//		int page = 1;
//		int pageSize = 20;
//
//		//登録画面でキャンセルボタン押下時に排他チェック編集済を呼び出す為
//		String action = "cancel";
//		String empId = "1";
//
//		List<Map<String, Object>> mockList = new ArrayList<>();
//
//		Map<String, Object> map = new HashMap<>();
//		map.put("employee_id", 1);
//		map.put("employee_name", "田中零郎1");
//		map.put("client_id", 101);
//		map.put("client_name", "株式会社アクサス");
//		map.put("hourly_wage", 1);
//		map.put("paid_holiday_std", "2023-07-01");
//		map.put("remaind_this_year", null);
//		map.put("holiday_date", null);
//		map.put("application_class", null);
//
//		mockList.add(map);
//
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		Mockito.when(mockEmployeeService.searchActive(page - 1, pageSize)).thenReturn(mockList);
//		Mockito.when(mockEmployeeService.countEmployee()).thenReturn(1);
//
//		String result = target.employeePostList(page, model, action, empId);
//
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("list");
//		String employeeId = list.get(0).get("employee_id").toString();
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//
//		assertEquals("1", employeeId);
//		assertEquals(2, currentPage);
//		assertEquals(1, totalPages);
//
//		assertEquals("SMSEM001", result);
//	}
//
//	/**
//	 * employeePostList(Listを取得できる場合)
//	 * 正常系テストケース
//	 * mockEmployeeService.searchActiveはmockListの値を返す
//	 * mockEmployeeService.countEmployee()は2を返す
//	 * 期待値
//	 * model「list」List1番目のemployee_idが2であること
//	 * model「currentPage」2が保存されていること
//	 * model「totalPages」1が保存されていること
//	 * employeeList	"SMSEM001"を返す
//	 */
//	@Test
//	public void listPost02() {
//
//		int page = 1;
//		int pageSize = 20;
//
//		String action = "cancel";
//		String empId = "1";
//
//		List<Map<String, Object>> mockList = new ArrayList<>();
//
//		Map<String, Object> map1 = new HashMap<>();
//		map1.put("employee_id", 1);
//		map1.put("employee_name", "田中零郎1");
//		map1.put("client_id", 101);
//		map1.put("client_name", "株式会社アクサス");
//		map1.put("hourly_wage", 1);
//		map1.put("paid_holiday_std", "2023-07-01");
//		map1.put("remaind_this_year", null);
//		map1.put("holiday_date", null);
//		map1.put("application_class", null);
//
//		Map<String, Object> map2 = new HashMap<>();
//		map2.put("employee_id", 2);
//		map2.put("employee_name", "田中零郎2");
//		map2.put("client_id", 102);
//		map2.put("client_name", "株式会社イクサス");
//		map2.put("hourly_wage", 1);
//		map2.put("paid_holiday_std", "2023-07-01");
//		map2.put("remaind_te", null);
//		map2.put("application_class", null);
//
//		mockList.add(map1);
//		mockList.add(map2);
//
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		Mockito.when(mockEmployeeService.searchActive(page - 1, pageSize)).thenReturn(mockList);
//		Mockito.when(mockEmployeeService.countEmployee()).thenReturn(2);
//
//		String result = target.employeePostList(page, model, action, empId);
//
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("list");
//		String employeeId = list.get(1).get("employee_id").toString();
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//
//		assertEquals("2", employeeId);
//		assertEquals(2, currentPage);
//		assertEquals(1, totalPages);
//
//		assertEquals("SMSEM001", result);
//	}
//
//	/**
//	 * employeePostList(Listを取得できない場合)
//	 * 正常系テストケース
//	 * mockEmployeeService.searchActiveは空のリストを返す
//	 * mockEmployeeService.countEmployee()は0を返す
//	 * 期待値
//	 * model「list」が空であること
//	 * model「currentPage」2が保存されていること
//	 * model「totalPages」0が保存されていること
//	 * employeeList	"SMSEM001"を返す
//	 */
//	@Test
//	public void listPost03() {
//
//		int page = 1;
//		int pageSize = 20;
//
//		String action = "cancel";
//		String empId = "1";
//
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		Mockito.when(mockEmployeeService.searchActive(page - 1, pageSize)).thenReturn(Collections.emptyList());
//		Mockito.when(mockEmployeeService.countEmployee()).thenReturn(0);
//
//		String result = target.employeePostList(page, model, action, empId);
//
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("list");
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//
//		assertTrue(list.isEmpty());
//		assertEquals(2, currentPage);
//		assertEquals(0, totalPages);
//
//		assertEquals("SMSEM001", result);
//	}
//
//	/**
//	 * employeePostList(Listを取得できる場合)
//	 * 正常系テストケース
//	 * mockEmployeeService.searchActiveはmockListの値を返す
//	 * mockEmployeeService.countEmployee()は21を返す
//	 * 期待値
//	 * model「list」List0番目のemployee_idが21であること
//	 * model「currentPage」3が保存されていること
//	 * model「totalPages」2が保存されていること
//	 * employeeList	"SMSEM001"を返す
//	 */
//	@Test
//	public void listPost04() {
//
//		int page = 2;
//		int pageSize = 20;
//
//		//登録画面でキャンセルボタン押下時に排他チェック編集済を呼び出す為
//		String action = "cancel";
//		String empId = "1";
//
//		List<Map<String, Object>> mockList = new ArrayList<>();
//
//		Map<String, Object> map = new HashMap<>();
//		map.put("employee_id", 21);
//		map.put("employee_name", "田中零郎21");
//		map.put("client_id", 121);
//		map.put("client_name", "株式会社ナクサス");
//		map.put("hourly_wage", 1);
//		map.put("paid_holiday_std", "2023-07-01");
//		map.put("remaind_this_year", null);
//		map.put("holiday_date", null);
//		map.put("application_class", null);
//
//		mockList.add(map);
//
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		Mockito.when(mockEmployeeService.searchActive(page - 1, pageSize)).thenReturn(mockList);
//		Mockito.when(mockEmployeeService.countEmployee()).thenReturn(21);
//
//		String result = target.employeePostList(page, model, action, empId);
//
//		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("list");
//		String employeeId = list.get(0).get("employee_id").toString();
//		int currentPage = (int) model.get("currentPage");
//		int totalPages = (int) model.get("totalPages");
//
//		assertEquals("21", employeeId);
//		assertEquals(3, currentPage);
//		assertEquals(2, totalPages);
//
//		assertEquals("SMSEM001", result);
//	}
//
//	/**
//	 * deleteEmployee（指定された社員IDのデータを取得できる場合）
//	 * ※指定された社員IDの削除フラグ=falseのデータが1件存在すること
//	 * mockEmployeeDate.getPaidHolidayStd()は"2023-07-01"を返す
//	 * mockEmployeeDate.getUpdatedUser()は"sysdate"を返す
//	 * 正常系テストケース
//	 * 期待値
//	 * deletePostEmployee "redirect:/employee/list"を返す
//	 **/
//	@Test
//	public void deletePost01() {
//
//		String[] employeeId = { "1" };
//		int tableNumber = 2;
//		String loginUserId = "nexus001";
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		//EmployeeDateを格納
//		Mockito.when(mockEmployeeDate.getPaidHolidayStd()).thenReturn("2023-07-01");
//		Mockito.when(mockEmployeeDate.getUpdatedUser()).thenReturn("sysdate");
//
//		//排他チェック(削除)の結果:false
//		Mockito.when(mockExclusiveCheckService.ExclusiveCheckDalete(Mockito.anyString(), Mockito.eq(tableNumber)))
//				.thenReturn(false);
//		//排他チェック(編集中)の結果:false
//		Mockito.when(mockExclusiveCheckService.ExclusiveCheckEdited(Mockito.anyString(), Mockito.eq(loginUserId),
//				Mockito.eq(tableNumber))).thenReturn(false);
//
//		String result = target.deletePostEmployee(mockEmployeeDate, employeeId, attr, model);
//
//		verify(mockEmployeeService, times(1)).deleteEmployee(employeeId,"sysdate");
//		assertEquals("redirect:/employee/list", result);
//	}
//
//	/**
//	 * deleteEmployee（指定された社員IDのデータを取得できる場合）
//	 * ※指定された社員IDの削除フラグ=falseのデータが2件存在すること
//	 * mockEmployeeDate.getPaidHolidayStd()は"2023-07-01"を返す
//	 * mockEmployeeDate.getUpdatedUser()は"sysdate"を返す
//	 * 正常系テストケース
//	 * 期待値
//	 * deletePostEmployee "redirect:/employee/list"を返す
//	 **/
//	@Test
//	public void deletePost02() {
//
//		String[] employeeId = { "1", "2" };
//		int tableNumber = 2;
//		String loginUserId = "nexus001";
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		//EmployeeDateを格納
//		Mockito.when(mockEmployeeDate.getPaidHolidayStd()).thenReturn("2023-07-01");
//		Mockito.when(mockEmployeeDate.getUpdatedUser()).thenReturn("sysdate");
//
//		//排他チェック(削除)の結果:false
//		Mockito.when(mockExclusiveCheckService.ExclusiveCheckDalete(Mockito.anyString(), Mockito.eq(tableNumber)))
//				.thenReturn(false);
//		//排他チェック(編集中)の結果:false
//		Mockito.when(mockExclusiveCheckService.ExclusiveCheckEdited(Mockito.anyString(), Mockito.eq(loginUserId),
//				Mockito.eq(tableNumber))).thenReturn(false);
//
//		String result = target.deletePostEmployee(mockEmployeeDate, employeeId, attr, model);
//		
//		verify(mockEmployeeService, times(2)).deleteEmployee(employeeId,"sysdate");
//		assertEquals("redirect:/employee/list", result);
//	}
//
//	/**
//	 * deleteEmployee（指定された社員IDのデータを取得できない場合）
//	 * ※指定された社員IDの削除フラグ=falseのデータが存在しないこと
//	 * mockEmployeeDate.getPaidHolidayStd()は"2023-07-01"を返す
//	 * mockEmployeeDate.getUpdatedUser()は"sysdate"を返す
//	 * 正常系テストケース
//	 * 期待値
//	 * deletePostEmployee "redirect:/employee/list"を返す
//	 **/
//	@Test
//	public void deletePost03() {
//
//		String[] employeeId = {};
//		int tableNumber = 2;
//		String loginUserId = "nexus001";
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		//EmployeeDateを格納
//		Mockito.when(mockEmployeeDate.getPaidHolidayStd()).thenReturn("2023-07-01");
//		Mockito.when(mockEmployeeDate.getUpdatedUser()).thenReturn("sysdate");
//
//		//排他チェック(削除)の結果:false
//		Mockito.when(mockExclusiveCheckService.ExclusiveCheckDalete(Mockito.anyString(), Mockito.eq(tableNumber)))
//				.thenReturn(false);
//		//排他チェック(編集中)の結果:false
//		Mockito.when(mockExclusiveCheckService.ExclusiveCheckEdited(Mockito.anyString(), Mockito.eq(loginUserId),
//				Mockito.eq(tableNumber))).thenReturn(false);
//
//		String result = target.deletePostEmployee(mockEmployeeDate, employeeId, attr, model);
//
//		verify(mockEmployeeService, times(0)).deleteEmployee(employeeId,"sysdate");
//		assertEquals("redirect:/employee/list", result);
//	}
//
//	/**
//	 * deleteEmployee（指定された社員IDのデータを取得できる場合）
//	 * ※排他チェック(削除)の結果がtrueのとき
//	 * mockEmployeeDate.getPaidHolidayStd()は"2023-07-01"を返す
//	 * mockEmployeeDate.getUpdatedUser()は"sysdate"を返す
//	 * 異常系テストケース
//	 * 期待値
//	 * attr「result」該当データはすでに削除されています。が保存されていること。
//	 * deletePostEmployee "redirect:/employee/list"を返す
//	 **/
//	@Test
//	public void deletePost04() {
//
//		String[] employeeId = { "1" };
//		int tableNumber = 2;
//		String loginUserId = "nexus001";
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		//EmployeeDateを格納
//		Mockito.when(mockEmployeeDate.getPaidHolidayStd()).thenReturn("2023-07-01");
//		Mockito.when(mockEmployeeDate.getUpdatedUser()).thenReturn("sysdate");
//
//		//排他チェック(削除)の結果:true
//		Mockito.when(mockExclusiveCheckService.ExclusiveCheckDalete(Mockito.anyString(), Mockito.eq(tableNumber)))
//				.thenReturn(true);
//		//排他チェック(編集中)の結果:false
//		Mockito.when(mockExclusiveCheckService.ExclusiveCheckEdited(Mockito.anyString(), Mockito.eq(loginUserId),
//				Mockito.eq(tableNumber))).thenReturn(false);
//
//		String result = target.deletePostEmployee(mockEmployeeDate, employeeId, attr, model);
//
//		//フラッシュスコープに保存されたエラー文を取得
//		String error = attr.getFlashAttributes().get("result").toString();
//
//		verify(mockEmployeeService, times(0)).deleteEmployee(employeeId,"sysdate");
//		assertEquals("該当データはすでに削除されています。", error);
//		assertEquals("redirect:/employee/list", result);
//	}
//
//	/**
//	 * deleteEmployee（指定された社員IDのデータを取得できる場合）
//	 * ※排他チェック(編集中)の結果がtrueのとき
//	 * mockEmployeeDate.getPaidHolidayStd()は"2023-07-01"を返す
//	 * mockEmployeeDate.getUpdatedUser()は"sysdate"を返す
//	 * 異常系テストケース
//	 * 期待値
//	 * attr「result」該当データは他のユーザ「」が編集中です。が保存されていること。
//	 * deletePostEmployee "redirect:/employee/list"を返す
//	 **/
//	@Test
//	public void deletePost05() {
//
//		String[] employeeId = { "1" };
//		int tableNumber = 2;
//		String loginUserId = "nexus001";
//		RedirectAttributes attr = new RedirectAttributesModelMap();
//		ExtendedModelMap model = new ExtendedModelMap();
//
//		//EmployeeDateを格納
//		Mockito.when(mockEmployeeDate.getPaidHolidayStd()).thenReturn("2023-07-01");
//		Mockito.when(mockEmployeeDate.getUpdatedUser()).thenReturn("sysdate");
//
//		//排他チェック(削除)の結果:true
//		Mockito.when(mockExclusiveCheckService.ExclusiveCheckDalete(Mockito.anyString(), Mockito.eq(tableNumber)))
//				.thenReturn(false);
//		//排他チェック(編集中)の結果:false
//		Mockito.when(mockExclusiveCheckService.ExclusiveCheckEdited(Mockito.anyString(), Mockito.eq(loginUserId),
//				Mockito.eq(tableNumber))).thenReturn(true);
//
//		String result = target.deletePostEmployee(mockEmployeeDate, employeeId, attr, model);
//
//		//フラッシュスコープに保存されたエラー文を取得
//		String error = attr.getFlashAttributes().get("result").toString();
//
//		verify(mockEmployeeService, times(0)).deleteEmployee(employeeId,"sysdate");
//		assertEquals("該当データは他のユーザ「" + ExclusiveCheckService.loockingUserId + "」が編集中です。", error);
//		assertEquals("redirect:/employee/list", result);
//	}
//
//}
