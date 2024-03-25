package jp.co.nexus;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.nexus.whc.repository.UserRepository;
import com.nexus.whc.services.UserServices;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = UserServices.class)
@TestPropertySource(locations = "classpath:application.properties")
@SpringBootApplication
public class UserServicesTest {
	

	@MockBean
	private UserRepository mockUserRepository;
	
	@Autowired
	private UserServices target;
	
	@BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
	/**
	 * ★一覧表示機能
	 * ・searchActive
	 * ・serachCount
	 * *削除機能
	 * ・daleteUser
	 */
	
	/**
	 * searchActive(Listを取得できる場合)
	 * 正常系テストケース
	 * mockUserRepository.searchActive()はmockUserListを返す
	 * 期待値
	 * ・一覧に表示するための情報を返し、List1番目のseq_idが101であること
	 */
	@Test
	public void searchActive01() {
		
		int pageNumber = 0;
		int pageSize = 20;
		int offset = pageNumber * pageSize;
		
		List<Map<String, Object>> mockUserList = new ArrayList<>();
		Map<String, Object> userMap1 = new HashMap<>();
		userMap1.put("m_user.seq_id",101);
		userMap1.put("m_user.user_id","201");
		userMap1.put("m_user.user_name","A");
		userMap1.put("m_user.mail_address","test01@nexus-nt.co.jp");
		userMap1.put("m_user.auth_id","301");
		userMap1.put("m_authority.auth_status","AAA");
		userMap1.put("m_user.delete_flg",0);
		userMap1.put("m_user.created_at","2023-12-04 11:58:00");
		userMap1.put("m_user.created_user","nexus101");
		userMap1.put("m_user.updated_at","2023-12-04 11:58:00");
		userMap1.put("m_user.updated_user","nexus101");
		mockUserList.add(userMap1);
		
		Mockito.when(mockUserRepository.searchActive(offset,pageSize)).thenReturn(mockUserList);
		List<Map<String, Object>> userList = target.searchActive(pageNumber,pageSize);
		Object result = userList.get(0).get("m_user.seq_id");
		assertEquals(101,result );
	}
	
	/**
	 * searchActive(Listを取得できる場合)
	 * 正常系テストケース
	 * mockUserRepository.searchActive()はmockUserListを返す
	 * 期待値
	 * ・一覧に表示するための情報を返し、List1番目のseq_idが102であること
	 */
	@Test
	public void searchActive02() {
		
		int pageNumber = 0;
		int pageSize = 20;
		int offset = pageNumber * pageSize;
		
		List<Map<String, Object>> mockUserList = new ArrayList<>();
		
		Map<String, Object> userMap1 = new HashMap<>();
		userMap1.put("m_user.seq_id",101);
		userMap1.put("m_user.user_id","201");
		userMap1.put("m_user.user_name","A");
		userMap1.put("m_user.mail_address","test01@nexus-nt.co.jp");
		userMap1.put("m_user.auth_id","301");
		userMap1.put("m_authority.auth_status","AAA");
		userMap1.put("m_user.delete_flg",0);
		userMap1.put("m_user.created_at","2023-12-04 11:58:00");
		userMap1.put("m_user.created_user","nexus101");
		userMap1.put("m_user.updated_at","2023-12-04 11:58:00");
		userMap1.put("m_user.updated_user","nexus101");
		Map<String, Object> userMap2 = new HashMap<>();
		userMap2.put("m_user.seq_id",102);
		userMap2.put("m_user.user_id","202");
		userMap2.put("m_user.user_name","B");
		userMap2.put("m_user.mail_address","test02@nexus-nt.co.jp");
		userMap2.put("m_user.auth_id","302");
		userMap2.put("m_authority.auth_status","AAA");
		userMap2.put("m_user.delete_flg",0);
		userMap2.put("m_user.created_at","2023-12-04 11:58:00");
		userMap2.put("m_user.created_user","nexus102");
		userMap2.put("m_user.updated_at","2023-12-04 11:58:00");
		userMap2.put("m_user.updated_user","nexus102");
		
		mockUserList.add(userMap1);
		mockUserList.add(userMap2);
		
		Mockito.when(mockUserRepository.searchActive(offset,pageSize)).thenReturn(mockUserList);
		List<Map<String, Object>> userList = target.searchActive(pageNumber,pageSize);
		Object result = userList.get(1).get("m_user.seq_id");
		assertEquals(102,result );
	}
	/**
	 * searchActive(Listを取得できる場合)
	 * 正常系テストケース
	 * mockUserRepository.searchActive()はmockUserListを返す
	 * 期待値
	 * ・空のListを返すこと
	 */
	@Test
	public void searchActive03() {
		
		int pageNumber = 0;
		int pageSize = 20;
		int offset = pageNumber * pageSize;
		
		Mockito.when(mockUserRepository.searchActive(offset,pageSize)).thenReturn(Collections.emptyList());
		List<Map<String, Object>> userList = target.searchActive(pageNumber,pageSize);
		assertTrue(userList.isEmpty());
	}
	
	/**
	 * searchActive(Listを取得できる場合)
	 * 正常系テストケース
	 * mockUserRepository.searchActive()はmockUserListを返す
	 * 期待値
	 * ・一覧に表示するための情報を返し、List1番目のseq_idが121であること
	 */
	@Test
	public void searchActive04() {
		
		int pageNumber = 20;
		int pageSize = 20;
		int offset = pageNumber * pageSize;
		
		List<Map<String, Object>> mockUserList = new ArrayList<>();
		Map<String, Object> userMap1 = new HashMap<>();
		userMap1.put("m_user.seq_id",121);
		userMap1.put("m_user.user_id","221");
		userMap1.put("m_user.user_name","A");
		userMap1.put("m_user.mail_address","test21@nexus-nt.co.jp");
		userMap1.put("m_user.auth_id","321");
		userMap1.put("m_authority.auth_status","ZZZ");
		userMap1.put("m_user.delete_flg",0);
		userMap1.put("m_user.created_at","2023-12-04 11:58:00");
		userMap1.put("m_user.created_user","nexus121");
		userMap1.put("m_user.updated_at","2023-12-04 11:58:00");
		userMap1.put("m_user.updated_user","nexus121");
		mockUserList.add(userMap1);
		
		Mockito.when(mockUserRepository.searchActive(offset,pageSize)).thenReturn(mockUserList);
		List<Map<String, Object>> userList = target.searchActive(pageNumber,pageSize);
		Object result = userList.get(0).get("m_user.seq_id");
		assertEquals(121,result );
	}
	
	/**
	 * serachCount（指定されたユーザIDのデータを取得できる場合）
	 * ※data.sqlに指定されたユーザIDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した件数が1件であること
	 **/
	@Test
	public void serachCount01() {
		Mockito.when(mockUserRepository.searchCount()).thenReturn(1);
		int result = target.searchCount();
		assertEquals(1,result);
	}
	/**
	 * serachCount（指定されたユーザIDのデータを取得できる場合）
	 * ※data.sqlに指定されたユーザIDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した件数が2件であること
	 **/
	@Test
	public void serachCount02() {
		Mockito.when(mockUserRepository.searchCount()).thenReturn(2);
		int result = target.searchCount();
		assertEquals(2,result);
	}
	/**
	 * serachCount（指定されたユーザIDのデータを取得できる場合）
	 * ※data.sqlに指定されたユーザIDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した件数が0件であること
	 **/
	@Test
	public void serachCount03() {
		Mockito.when(mockUserRepository.searchCount()).thenReturn(0);
		int result = target.searchCount();
		assertEquals(0,result);
	}
	/**
	 * serachCount（指定されたユーザIDのデータを取得できる場合）
	 * ※data.sqlに指定されたユーザIDの削除フラグ=falseのデータが存在すること
	 * 正常系テストケース
	 * 期待値
	 * ・取得した件数が21件であること
	 **/
	@Test
	public void serachCount04() {
		Mockito.when(mockUserRepository.searchCount()).thenReturn(21);
		int result = target.searchCount();
		assertEquals(21,result);
	}
	
    
	/**
	 * daleteUser（指定されたシーケンスIDのデータを取得できる場合）
	 * 正常系テストケース
	 * mockUserRepository.daleteUser()は1の値を返す
	 * target.daleteUserはvoidの為、戻り値無し
	 * 期待値
	 * mockUserRepository.daleteUser()が1回実行されているか
	 **/
    @Test
	public void daleteUser01() {
		
		String seqId = "101";
		
		Mockito.when(mockUserRepository.daleteUser(seqId)).thenReturn(1);
		target.daleteUser(seqId);
		
		verify(mockUserRepository,times(1)).daleteUser(seqId);
	}
	/**
	 * daleteUser（指定されたシーケンスIDのデータを取得できる場合）
	 * 正常系テストケース
	 * mockUserRepository.daleteUser()は1の値を返す
	 * target.daleteUserはvoidの為、戻り値無し
	 * 期待値
	 * mockUserRepository.daleteUser()が1回実行されているか
	 **/
    @Test
	public void daleteUser02() {
		
		String seqId1 = "101";
		String seqId2 = "102";
		
		Mockito.when(mockUserRepository.daleteUser(seqId1)).thenReturn(1);
		Mockito.when(mockUserRepository.daleteUser(seqId2)).thenReturn(1);
		target.daleteUser(seqId1);
		target.daleteUser(seqId2);
		
		verify(mockUserRepository,times(1)).daleteUser(seqId1);
		verify(mockUserRepository,times(1)).daleteUser(seqId2);
	}
	/**
	 * daleteUser（指定されたシーケンスIDのデータを取得できる場合）
	 * 正常系テストケース
	 * mockUserRepository.daleteUser()は0の値を返す
	 * target.daleteUserはvoidの為、戻り値無し
	 * 期待値
	 * mockUserRepository.daleteUser()が実行されていないか
	 **/
    @Test
	public void daleteUser03() {
		
		String seqId = "101";
		
		Mockito.when(mockUserRepository.daleteUser(seqId)).thenReturn(0);
		target.daleteUser(seqId);
		
		verify(mockUserRepository,times(1)).daleteUser(seqId);
	}  
}
