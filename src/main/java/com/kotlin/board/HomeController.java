package com.kotlin.board;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kotlin.board.dto.MemberDto;
import com.kotlin.board.service.MemberService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private MemberService mServ;
	
	private ModelAndView mv;
	//얘는 autowired 안 해요 받아 쓰기 때문에...?
	
	@GetMapping("/")
	public String home() {
		logger.info("home()");
	
		return "home";
	}
	
	@GetMapping("joinFrm")
	public String joinFrm() {
		logger.info("joinFrm()");
		
		return "joinFrm";
	}
	
	@PostMapping("memInsert")
	public String memInsert(MemberDto member, RedirectAttributes rttr) {
		logger.info("memberInsert()");
		String view = mServ.memberInsert(member, rttr);
				
		return view;
	}
	
	@GetMapping("loginFrm")
	public String loginFrm() {
		logger.info("loginFrm");
		
		return "loginFrm";
	}
	
	@PostMapping("login")
	public String loginProc(MemberDto member, RedirectAttributes rttr) {
		logger.info("login()");
		
		return mServ.loginProc(member, rttr);
	}
	//서비스가 String을 return 하면 ...얘도 맞춰서해야죱.
	
	@GetMapping("logout")
	public String logout() {
		//session을 쓰는건 서비스니까 service에서 로그아웃을 처리하게 만들어줄거예요. 
		
		return mServ.logout();
	}
}
