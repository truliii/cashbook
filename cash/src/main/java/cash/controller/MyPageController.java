package cash.controller;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cash.vo.Member;

@WebServlet("/on/myPage")
public class MyPageController extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//session 유효성 검사 : 로그인이 되어있지 않으면 로그인컨트롤러로 리다이렉션 -> Filter로 처리
		Object o = request.getSession().getAttribute("loginMember");
		String memberId = "";
		if(o instanceof Member) {
			memberId = ((Member)o).getMemberId();
		}
		
		Calendar today = Calendar.getInstance();
		int targetYear = today.get(Calendar.YEAR);
		int targetMonth = today.get(Calendar.MONTH);
		if(request.getParameter("targetYear") != null) {
			targetYear = Integer.parseInt(request.getParameter("targetYear"));
			targetMonth = Integer.parseInt(request.getParameter("targetMonth"));
		}
		
		request.setAttribute("memberId", memberId);
		request.setAttribute("targetYear", targetYear);
		request.setAttribute("targetMonth", targetMonth);
		
		//myPage로 forward
		request.getRequestDispatcher("/WEB-INF/view/on/myPage.jsp").forward(request, response);
	}
}
