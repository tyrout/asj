package com.kotlin.board.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import lombok.extern.java.Log;

@Log
public class SessionInterCepter extends HandlerInterceptorAdapter {

	@Autowired
	HttpSession session;
	
	//컨트롤러 들어가기전에 처리하게 만들어진 메소드가 있구요,
	//컨트롤러 나왔다가 그 다음에 처리하는 메소드가 있어요. 
	
	//컨트롤러로 요청이 전달되기 전에 처리하는 메소드 
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.info("prehandle() intercept");
		
		//session에 mb가 없으면 첫 번째 page 로 이동하게 한다. 
		if(session.getAttribute("mb") == null) {
			response.sendRedirect("../");
			return false;
			//문제는 첫 페이지는 세션에 mb 가 없잖아요. 그래서 실행이 안 돼요. 
		}
		
		// treu = 정상적으로 콘트롤러로 가라. false=컨트롤러로 가지마라
		return true;
	}
	
	//로그아웃 후 뒤로가기 막기 
	//posthandle메소드를 재정의하여, 브라우저 버퍼에 임시로 저장된 녀석을 cache data 라고 하는데 그 캐쉬를 제거한다 (그래서 bakcbutton 비활성화되게 )
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		//프로토콜 1.1 과 1.0이 캐쉬를 지우는 방법이 좀 달라요... 두 경우를 다 고려해서  처리되게 만들어주겠습니다. 
		if(request.getProtocol().equals("HTTP/1.1")) {
			response.setHeader("Cache-Control","no-cache, no-store, must-revalidate");
			//이걸하면 브라우저에서 캐쉬를 지웁니다. 
			
		}else {
			//프로토콜이 http/1.0일 때, 
			response.setHeader("Pragma", "no-cache");
		}
		response.setDateHeader("Expires", 0);//즉시 하라는 의미. 캐쉬 폐기 : 0을 받게 되면 즉시 지워라. 
	}
}
