package com.kotlin.board;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kotlin.board.dto.MemberDto;
import com.kotlin.board.service.MemberService;

import lombok.extern.java.Log;

@RestController
@Log
public class RestHomeController {
	
	String msg;
	
	@Autowired
	private MemberService mServ;

	@GetMapping(value="idCheck" , produces="application/text; charset=utf-8")
	public String idCheck(String mid) {
		log.info("idcheck()");
		
		String msg = mServ.countId(mid);
		
		return msg;
		
	}
	
	
	 @PostMapping(value="idCheckajax", produces="application/json; charset=utf-8")
	 public Map<String, String> idCheckajax(MemberDto member) {
		 log.info("idcheckajax()");
		 
		 Map<String, String> map = new HashMap<String, String>();
		 //map.put("anything", "value");
		 //System.out.println(map);
		 
		 log.info(member.getM_id());
		 String msg = mServ.countId(member.getM_id());
		 log.info(msg);
		 if(msg == "success") {
			 log.info("i'm in msg = success section");
			 map.put("result", "success");
			 map.put("m_id", member.getM_id());
		 }else {
			 map.put("result", "fail");
			 map.put("m_id", member.getM_id());
		 }
		 System.out.println("This is systemout "+map);
		 
		 return map;
	 }
	  
	  
	  
	 
	
}
