package com.kotlin.board;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kotlin.board.dto.BfileDto;
import com.kotlin.board.dto.ReplyDto;
import com.kotlin.board.service.BoardService;

import lombok.extern.java.Log;

@Controller
@Log
public class BoardController {
	//service object variable
	
	@Autowired
	private BoardService bServ;
	
	// ModelAndView object for data transfer
	private ModelAndView mv;
	
	//Integer 은 객체이고 int 는 하나의 변수입니다. Integer 을 사용하면 null 처리가 가능해지죠. 
	@GetMapping("list")
	public ModelAndView boardList(Integer pageNum) {
		//int 가 아니라 integer 입니다. 
		log.info("boardList()");
		
		
		mv = bServ.getBoardList(pageNum);
				// mv = bServ.getBoardList(pageNum);
				// 페이지, 보드리스트, 파일이름까지 다  service에서 받아서 return 을 할 것입니다. 
				//mv = new ModelAndView();
				//mv.setViewName("boardList");
		
		return mv;
	}
	
	@GetMapping("writeFrm")
	public String writeFrm() {
		log.info("writeFrm()");
		
		return "writeFrm";
	}
	
	
	@PostMapping("boardWrite")
	public String boardWrite(MultipartHttpServletRequest multi, RedirectAttributes rttr) {
		log.info("boardWrite()");
		
		String view = bServ.boardInsert(multi, rttr);
		
		return view;
	}
	
	@GetMapping("contents")
	public ModelAndView boardContents(Integer bnum) {
		log.info("boardContents() - bnum : " +bnum);
		
		mv = bServ.getContents(bnum);
		
		return mv;
	}
	
	@PostMapping(value="replyIns", produces="application/json; charset=utf-8")
	@ResponseBody
	public Map<String, List<ReplyDto>> replyInsert(ReplyDto reply){
		Map<String, List<ReplyDto>> rMap = bServ.replyInsert(reply);
		
		return rMap;
	}
	
	//첨부파일 다운로드 처리. 페이지 return 값이 없음 다운로드를 바로 처리하는거지 다운로드 페이지로 이동 x
	@GetMapping("download")
	public void fileDownLoad(String sysName, HttpServletRequest request, HttpServletResponse response) {
		//서브릿 doget dopost할때 자동으로 들어가는 녀석이죠
		//request를 통해 다운로드 요청이 들어옵니다. file을 다운로드 해달라는 요청이request 로 들어오고, response도 딸려옵니다. 
		//리퀘스트리스폰스로 파일다운
		log.info("fileDownLoad() file : " + sysName);
		
		bServ.fileDown(sysName, request, response);
		
	}
	
	@GetMapping("updatePost")
	public ModelAndView updatePost(Integer postNum) {
		
		mv = bServ.upPostProc(postNum);
		
		return mv;
	}
	
	@PostMapping("updateDone")
	public String updateDone(MultipartHttpServletRequest multi, RedirectAttributes rttr) {
		
		String view = bServ.updateDone(multi, rttr);
		
		return view;
	}
	
	@GetMapping("deletePost")
	public String deletePost(Integer postNum) {
		
		String view = bServ.deletePostProc(postNum);
		
		return view;
	}
	
	@GetMapping(value = "deleteFileFromDisk", produces = "application/json; charset=utf-8")
	@ResponseBody
	public Map<String, List<BfileDto>> deleteFileFromDisk(String sysname, int bnum) {
		
		Map<String, List<BfileDto>> fmap = bServ.deleteFileFromDisk(sysname,bnum);

		return fmap;
	}
}
