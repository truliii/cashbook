package cash.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import cash.dao.CashbookDao;
import cash.dao.HashtagDao;
import cash.vo.Cashbook;
import cash.vo.Hashtag;

public class CashbookService {
	//가계부 입력
	public String addCashbook(Cashbook cashbook) {
		String msg = null;
		Connection conn = null;

		try {
			String dbUrl = "jdbc:mariadb://127.0.0.1:3306/cash";
			String dbUser = "root";
			String dbPw = "java1234";
			Class.forName("org.mariadb.jdbc.Driver");
			conn = DriverManager.getConnection(dbUrl, dbUser, dbPw);
			
			//자동커밋 끄기
			conn.setAutoCommit(false);
			
			CashbookDao cashbookDao = new CashbookDao();
			int cashbookNo = cashbookDao.insertCashbook(conn, cashbook);
			
			//입력 성공 시 -> 해쉬태그가 존재한다면 -> 해쉬태그 추출 -> 해쉬태그 입력(반복)
			//해쉬태그 추출 알고리즘
			//# #구디 #구디 #자바
			HashtagDao hashtagDao = new HashtagDao();
			String rplMemo = cashbook.getMemo().replace("#", " #"); //#을 공백+#으로 바꿔줌 (#자바##파이썬 -> #자바 # #파이썬)
			
			Set<String> set = new HashSet<String>(); //중복된 해쉬태그를 방지하기 위해 set자료구조 사용
			
			for(String ht1 : rplMemo.split(" ")) { //공백으로 문자열을 나누어 배열로 저장한다.
				//#이 붙지 않는 단어도 해쉬태그로 저장되므로 #이 붙은 문자열만 ht2에 저장
				if(ht1.contains("#")) { //if(ht1.startsWith("#")
					String ht2 = ht1.replace("#", ""); //#을 없앤다
					if(ht2.length() > 0) { //#제거 후 남은 문자열이 있는 경우
						set.add(ht2); //set자료구조는 중복된 값이 add되지 않는다
					}
				}
			}
			
			for(String s : set) {
				Hashtag hashtag = new Hashtag();
				hashtag.setCashbookNo(cashbookNo);
				hashtag.setWord(s);
				hashtagDao.insertHashtag(conn, hashtag);
			}
			
			conn.commit();
		} catch(Exception e) {
			System.out.println("addCashbook 예외발생");
			msg = "addCashbook 예외발생";
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return msg;
	}
	
	//가계부 수정
	public String modifyCashbook(Cashbook cashbook) {
		String msg = null;
		Connection conn = null;
		
		try {
			String dbUrl = "jdbc:mariadb://127.0.0.1:3306/cash";
			String dbUser = "root";
			String dbPw = "java1234";
			Class.forName("org.mariadb.jdbc.Driver");
			conn = DriverManager.getConnection(dbUrl, dbUser, dbPw);
			conn.setAutoCommit(false);
			
			CashbookDao cashbookDao = new CashbookDao();
			cashbookDao.updateCashbook(conn, cashbook);

			//수정 성공 시 -> 기존 해쉬태그 삭제 -> 해쉬태그가 존재한다면 -> 해쉬태그 추출 -> 해쉬태그 입력(반복)
			//해쉬태그 추출 알고리즘
			//# #구디 #구디 #자바
			HashtagDao hashtagDao = new HashtagDao();
			hashtagDao.deleteHashtag(conn, cashbook.getCashbookNo()); //기존 해쉬태그삭제
			
			String rplMemo = cashbook.getMemo().replace("#", " #"); //#을 공백+#으로 바꿔줌 (#자바##파이썬 -> #자바 # #파이썬)
			Set<String> set = new HashSet<String>(); //중복된 해쉬태그를 방지하기 위해 set자료구조 사용
			for(String ht1 : rplMemo.split(" ")) { //공백으로 문자열을 나누어 배열로 저장한다.
				//#이 붙지 않는 단어도 해쉬태그로 저장되므로 #이 붙은 문자열만 ht2에 저장
				if(ht1.contains("#")) { //if(ht1.startsWith("#")
					String ht2 = ht1.replace("#", ""); //#을 없앤다
					if(ht2.length() > 0) { //#제거 후 남은 문자열이 있는 경우
						set.add(ht2); //set자료구조는 중복된 값이 add되지 않는다
					}
				}
			}
			
			for(String s : set) {
				Hashtag hashtag = new Hashtag();
				hashtag.setCashbookNo(cashbook.getCashbookNo());
				hashtag.setWord(s);
				hashtagDao.insertHashtag(conn, hashtag);
			}
			
			conn.commit();
		} catch(Exception e) {
			System.out.println("modifyCashbook 예외 발생");
			e.printStackTrace();
			try {
				conn.rollback(); //예외 발생 시 롤백
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				conn.close(); //conn 자원 반납
			} catch (SQLException e) {
				e.printStackTrace();
			} 
		}
		
		return msg;
	}
	
	//가계부 삭제
	public String removeCashbook(int cashbookNo) {
		String msg = null;
		Connection conn = null;
		
		try {
			String dbUrl = "jdbc:mariadb://127.0.0.1:3306/cash";
			String dbUser = "root";
			String dbPw = "java1234";
			Class.forName("org.mariadb.jdbc.Driver");
			conn = DriverManager.getConnection(dbUrl, dbUser, dbPw);
			conn.setAutoCommit(false);
			
			//삭제 메서드 실행 순서는 자식테이블인 hashtag먼저한다
			//해쉬태그 삭제
			HashtagDao hashtagDao = new HashtagDao();
			hashtagDao.deleteHashtag(conn, cashbookNo);

			//삭제 메서드 실행
			CashbookDao cashbookDao = new CashbookDao();
			cashbookDao.deleteCashbook(conn, cashbookNo);
			
			conn.commit();
		} catch(Exception e) {
			System.out.println("removeCashbook 예외 발생");
			e.printStackTrace();
			try {
				conn.rollback(); //예외 발생 시 롤백
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				conn.close(); //conn 자원 반납
			} catch (SQLException e) {
				e.printStackTrace();
			} 
		}
		
		return msg;
	}
}