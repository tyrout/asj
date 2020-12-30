package com.kotlin.board.service;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kotlin.board.dao.MemberDao;
import com.kotlin.board.dto.MemberDto;

import lombok.extern.java.Log;

//아직 root context 에 서비스 package 를 등록을 안 해놨죠?? 
@Service
@Log
public class MemberService {
	//Dao객체. 기본적으로 mybatis가 만든다고 했죠? 
	
	@Autowired
	private MemberDao mDao;

	//session 객체
	@Autowired
	private HttpSession session;
	
	//modelandview 
	// 이 세 가지는 기본적으로 service 에서 쓸 거라고 보면 됩니ㅏㄷ.
	private ModelAndView mv;
	
	//insert, updata, delete 는 무조건 transactional 을 넣어줘야합니다. 
	@Transactional
	public String memberInsert(MemberDto member, RedirectAttributes rttr) {
		String view = null; 
		//비밀번호 암호화 처리를 여기서 해야해요. 
		// 암호화를 하면 복구도 할수있어야해요
		// encript/ decript 
		//예를들어 db에 암호화된 걸 넣어야하죠. 사용자는 암호화된걸 모르니까 정상적인 패스워드를 넣고,
		//근데 디비에서는 암호화된 문자이 들어가있는거죠. 
		// 디비를 복구해서 입력된 유저인풋과 맞는지 아닌지 확인을 해야하는데 그 작업을 안 하는 거죠. 
		// 그래서 대신 암호화된 비번과 유저입력 비번을 입력하면 이게 같은지 아닌지를 알려주는 메소드를 제공합니다.
		// 디코드를 해버리면 원래 비번이 뭔지 알아버릴 수 있으니까요. 
		
		//스프링 프레임워크에서 제공하는 암호화 인코더 사용 
		BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();
	
		String encPwd = pwdEncoder.encode(member.getM_pwd());
		member.setM_pwd(encPwd);
		
		try {
			mDao.memberInsert(member);
			view = "redirect:/";
			rttr.addFlashAttribute("msg","success!");
		}catch(Exception e) {
			view = "redirect:joinFrm";
			rttr.addFlashAttribute("msg","fail");
		}
		
		
		return view; 
	}
	
	//data 가 jsp 쪽으로 넘어가야한다 즉 화면이 새로고침을하든안하든 계속 보여줘야하는 data 면 model 을 써야합니다.
	//그냥 redirect 만 할 경우는 modelandview or model을 쓸 필요가 없겠죠. 
	//session은 페이지가 바뀌든간에 session삭제가 되기 전까지는 계속 유지가 돼요 
	
	public String loginProc(MemberDto member, RedirectAttributes rttr) {
		String view = null;
		
		String encPwd = mDao.getEncPwd(member.getM_id());
		
		//암호화된 비번과 입력한 비번 비교처리를 위한 인코더 생성
		BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();
		
		//null이면 db에 없는 녀석인거죠 
		if(encPwd != null) {
			//matching id 
			if(pwdEncoder.matches(member.getM_pwd(), encPwd)) {
				//암호화 and rowpwd 같으면 true if not false
				
				//화면에 출력할 사용자 정보 가져오기
				member = mDao.getMemeberInfo(member.getM_id());
				session.setAttribute("mb", member);
				log.info("m_point is " + member.getM_point());
				log.info("m_name is " + member.getM_name());
				
				//session 에 mb가 없으면 게시판에서 정보를 출력하면 안 됩니다.
				//이 처리를 해줘야해요 jsp에서 뭐 query를 쓰든지. 
				
				view = "redirect:list?pageNum";
				//list는 boardcontroller 로 넘어갑니다 즉 board 쪽으로 넘어갑니다 왜냐하면 
				//로그인이 된 후에는 board 쪽 게시판으로 넘어가기 때문입니다. 
			}else {
				//worng password
				view = "redirect:loginFrm";
				rttr.addFlashAttribute("msg", "wrong password entered");
			}
		}else {
			// no id exist
			view = "redirect:loginFrm";
			rttr.addFlashAttribute("msg", "no matching id");
		}
		
		return view;
	}
	
	
	
	public String countId (String id) {
		
		String view = null; 
		
		int result = mDao.countId(id);
		
		if(result == 0 ) {
			view = "success";
		}else {
			view = "fail";
		}
		
		return view;
	}
	
	public String logout() {
		session.invalidate();
		
		return "home";
	}
	
}
