//package jp.co.nexus;
//
//import static org.junit.Assert.*;
//import static org.mockito.Mockito.*;
//
//import java.util.ArrayList;
//import java.util.Collections;
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
//import com.nexus.whc.repository.EmployeeRepository;
//import com.nexus.whc.services.EmployeeService;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ContextConfiguration(classes = EmployeeService.class)
//@TestPropertySource(locations = "classpath:application.properties")
//@SpringBootApplication
//public class EmployeeServiceTest {
//
//	@TestConfiguration
//	static class EmployeeRepositoryTestMock {
//
//		@Bean
//		public EmployeeRepository employeeRepository() {
//			return mock(EmployeeRepository.class);
//		}
//	}
//
//	@MockBean
//	private EmployeeRepository mockEmployeeRepository;
//
//	@Autowired
//	private EmployeeService target;
//
//	/**
//	 * searchActive(Listを取得できる場合)
//	 * 正常系テストケース
//	 * mockEmployeeRepository.searchActiveはmockListの値を返す
//	 * 期待値
//	 * ・一覧に表示するための情報を返し、List0番目のemployee_idが1であること
//	 */
//	@Test
//	public void searchActive01() {
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
//		int offset = 0;
//		int pageSize = 20;
//		int page = 1;
//
//		Mockito.when(mockEmployeeRepository.searchActive(offset, pageSize)).thenReturn(mockList);
//
//		List<Map<String, Object>> result = target.searchActive(page - 1, pageSize);
//		String empId = result.get(0).get("employee_id").toString();
//
//		assertEquals("1", empId);
//	}
//
//	/**
//	 * searchActive(Listを取得できる場合)
//	 * 正常系テストケース
//	 * mockEmployeeRepository.searchActiveはmockListの値を返す
//	 * 期待値
//	 * ・一覧に表示するための情報を返し、List1番目のemployee_idが2であること
//	 */
//	@Test
//	public void searchActive02() {
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
//		map2.put("remaind_this_year", null);
//		map2.put("holiday_date", null);
//		map2.put("application_class", null);
//
//		mockList.add(map1);
//		mockList.add(map2);
//
//		int offset = 0;
//		int pageSize = 20;
//		int page = 1;
//
//		Mockito.when(mockEmployeeRepository.searchActive(offset, pageSize)).thenReturn(mockList);
//
//		List<Map<String, Object>> result = target.searchActive(page - 1, pageSize);
//		String empId = result.get(1).get("employee_id").toString();
//
//		assertEquals("2", empId);
//	}
//
//	/**
//	 * searchActive(Listが0件の場合)
//	 * 正常系テストケース
//	 * mockEmployeeRepository.searchActiveはmockListの値を返す
//	 * 期待値
//	 * ・空のListを返すこと
//	 */
//	@Test
//	public void searchActive03() {
//
//		int offset = 0;
//		int pageSize = 20;
//		int page = 0;
//
//		Mockito.when(mockEmployeeRepository.searchActive(offset, pageSize)).thenReturn(Collections.emptyList());
//
//		List<Map<String, Object>> result = target.searchActive(page, pageSize);
//
//		assertTrue(result.isEmpty());
//	}
//
//	/**
//	 * searchActive(Listを取得できる場合)
//	 * 正常系テストケース
//	 * mockEmployeeRepository.searchActiveはmockListの値を返す
//	 * 期待値
//	 * ・一覧に表示するための情報を返し、List1番目のemployee_idが21であること
//	 */
//	@Test
//	public void searchActive04() {
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
//		int page = 2;
//		int offset = 20;
//		int pageSize = 20;
//
//		Mockito.when(mockEmployeeRepository.searchActive(offset, pageSize)).thenReturn(mockList);
//
//		List<Map<String, Object>> result = target.searchActive(page - 1, pageSize);
//		String empId = result.get(0).get("employee_id").toString();
//
//		assertEquals("21", empId);
//	}
//
//	/**
//	 * countEmployee（指定された社員IDのデータを取得できる場合）
//	 * ※data.sqlに指定された社員IDの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得した件数が1件であること
//	 **/
//	@Test
//	public void countEmployee01() {
//
//		Mockito.when(mockEmployeeRepository.countEmployee()).thenReturn(1);
//
//		int result = target.countEmployee();
//
//		assertEquals(1, result);
//
//	}
//
//	/**
//	 * countEmployee（指定された社員IDのデータを取得できる場合）
//	 * ※data.sqlに指定された社員IDの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得した件数が2件であること
//	 **/
//	@Test
//	public void countEmployee02() {
//
//		Mockito.when(mockEmployeeRepository.countEmployee()).thenReturn(2);
//
//		int result = target.countEmployee();
//
//		assertEquals(2, result);
//
//	}
//
//	/**
//	 * countEmployee（指定された社員IDのデータを取得できない場合）
//	 * ※data.sqlに指定された社員IDの削除フラグ=falseのデータが存在しないこと
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得した件数が0件であること
//	 **/
//	@Test
//	public void countEmployee03() {
//
//		Mockito.when(mockEmployeeRepository.countEmployee()).thenReturn(0);
//
//		int result = target.countEmployee();
//
//		assertEquals(0, result);
//
//	}
//	
//	/**
//	 * countEmployee（指定された社員IDのデータを取得できる場合）
//	 * ※data.sqlに指定された社員IDの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得した件数が21件であること
//	 **/
//	@Test
//	public void countEmployee04() {
//		
//		Mockito.when(mockEmployeeRepository.countEmployee()).thenReturn(21);
//		
//		int result = target.countEmployee();
//		
//		assertEquals(21, result);
//		
//	}
//
//	/**
//	 * deleteEmployee（指定された社員IDのデータを取得できる場合）
//	 * ※data.sqlに指定された社員IDの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得した削除件数が1件であること
//	 **/
//	@Test
//	public void deleteEmployee01() {
//
//		String[] employeeId = { "1" };
//		String updateUser = "sysdate";
//
//		Mockito.when(mockEmployeeRepository.deleteEmployee(employeeId, updateUser)).thenReturn(1);
//
//		int result = target.deleteEmployee(employeeId,  updateUser);
//
//		assertEquals(1, result);
//	}
//
//	/**
//	 * deleteEmployee（指定された社員IDのデータを取得できる場合）
//	 * ※data.sqlに指定された社員IDの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得した削除件数が1件であること
//	 **/
//	@Test
//	public void deleteEmployee02() {
//
//		String[] employeeId = { "1", "2" };
//		String updateUser = "sysdate";
//
//	
//		Mockito.when(mockEmployeeRepository.deleteEmployee(employeeId,  updateUser)).thenReturn(2);
//
//		int result = target.deleteEmployee(employeeId,  updateUser);
//
//		assertEquals(2, result);
//	}
//
//	/**
//	 * deleteEmployee（指定された社員IDのデータを取得できる場合）
//	 * ※data.sqlに指定された社員IDの削除フラグ=falseのデータが存在すること
//	 * 正常系テストケース
//	 * 期待値
//	 * ・取得した削除件数が1件であること
//	 **/
//	@Test
//	public void deleteEmployee03() {
//
//		String[] employeeId = { "2" };
//		String updateUser = "sysdate";
//
//		Mockito.when(mockEmployeeRepository.deleteEmployee(employeeId, updateUser)).thenReturn(0);
//
//		int result = target.deleteEmployee(employeeId, updateUser);
//
//		assertEquals(0, result);
//	}
//
//}
