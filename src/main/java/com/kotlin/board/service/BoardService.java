package com.kotlin.board.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kotlin.board.dao.BoardDao;
import com.kotlin.board.dao.MemberDao;
import com.kotlin.board.dto.BfileDto;
import com.kotlin.board.dto.BoardDto;
import com.kotlin.board.dto.MemberDto;
import com.kotlin.board.dto.ReplyDto;
import com.kotlin.board.util.Paging;

import lombok.extern.java.Log;

@Service// 어노테이션이 이 ㅆ어야 루트 콘텍스트를 통해, context 스캔을 통해 bean 이 만들어지고 그래야 controller 에서 autowired 를 통해 빈을 가져올 수 있씁니다. 
@Log
public class BoardService {
	//Dao
	@Autowired
	private BoardDao bDao;
	
	@Autowired
	private MemberDao mDao;
	
	private ModelAndView mv;
	
	@Autowired
	private HttpSession session;
	//session 에 페이지 번호를 저장해야하낟.
	// 그 이유 : 글 보고 백 버튼으로 다시 이전페이지인 목록을 ㅗ돌아가는데
	//첫번째 페이지로 가면 안 되겠죠? 원래 보던 page 로 돌아가야겠죠?
	
	//게시글 목록 가져오는 메소드
	public ModelAndView getBoardList(Integer pageNum) {
		
		log.info("getBoardList() - pageNum : "+pageNum);
		
		mv  = new ModelAndView();
		//page num == 1 이면 null이란 거겠죠. 
		int num = (pageNum == null) ? 1: pageNum;
		bDao.getList(num); //보시면, list 로 바로 넘어올 때는 pgaeNum 에 넘어오는 게 없어요.
		
		List<BoardDto> bList = bDao.getList(num);
		
		// db에서 조회환 데이터 목록을 모델에 추가
		mv.addObject("bList",bList);
		
		//페이징 처리 (나중에 ) 
		mv.addObject("paging",getPaging(num));
		
		
		session.setAttribute("pageNum", num);
		//이걸 모델에 집어넣어도 돼요 굳이 세션에 넣는 이유는요, 모델은 페이지가 변경이 되면 다 날아가요
		// 리스트에서 상세보기로 넘어가면 페이지번호가 사라져버린다는 겁니다. 
		// 목록에ㅓㅅ 상세보기로 들어갈때 글번호를 컨트롤러 통해 다시 db검색을 가져와야하는데, ..암튼 상세보기로 넘어가면 blist 에 있는 내용은
		// 싹다 없어진다는 거죠. 그래서 다시 목록으로 되돌아오게 되면 무조건 목록으로 돌아오게 된다는 거죠. 
		
		mv.setViewName("boardList");
		
		return mv;
	}//method end 
	
	
	private String getPaging(int num) {
		//전체 글 개수 구하기 ( from db ) 
		int maxNum = bDao.getBoardCnt();
		//설정값 (페이지 당 글 개수, 그룹 당 페이지 개수, 게시판 이름 ) 
		
		int listCnt = 10; 
		int pageCnt = 2;// 한 페이지 당 몇
		String listName="list";
		
		Paging paging = new Paging(maxNum, num, listCnt, pageCnt, listName);
		
		String pagingHtml = paging.makePaging();
		
		return pagingHtml;
	}
	
	
	// method="post" enctype="multipart/form-data"> 로 처리하기로 했죠?
	//게시글 등록 서비스 메소드 . insert update delete 는 transactional 을 써야한다. 
	@Transactional
	public String boardInsert(MultipartHttpServletRequest multi, RedirectAttributes rttr) {
		log.info("boardInsert()");
		
		String view = null; 
		
		String id = multi.getParameter("bid");
		String title = multi.getParameter("btitle");
		String contents = multi.getParameter("bcontents");
		String check = multi.getParameter("fileCheck");
		
		//textarea 는 입력한 문자열 앞 뒤로 공백이 발생한다. 
		//문자열 앞 뒤 공백을 제거해야한다. trim()
		contents = contents.trim();
		
		BoardDto board = new BoardDto();
		board.setBid(id);
		board.setBtitle(title);
		board.setBcontents(contents);
		
		try {
			//이 안에서 selectKey 가 먼저 실행이 됩니다. 
			bDao.boardInsert(board);
			
			view = "redirect:list";
			rttr.addFlashAttribute("msg","upload successes!");
			log.info("this is oardInsert section");
			//파일 업로드 메소드 호출 + file 있을 때만 해야해요. 
			//그걸 위해 처음부터 받아놓은 게 check
			if(check.equals("1")) {
				fileUP(multi,board.getBnum());
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			view = "redirect:writeFrm";
			rttr.addFlashAttribute("msg","failed connecting db or dealing with file");
		}

		//파일 업로드가 실패해도 글이 저장이 되게 하려면 여기에 파일 업로드 메소드를 호출하면 된다. 
		// try catch 에 들어가면, transactional 이라했잖아요 얘는 뭐냐면, 일단 우리가 게시글 저장/ 파일용 정보도 다른 테이블에 저장하잖아요
		// 게시글은 저장됐는데 파일 저장이 실패하면 그냥 다 rollback 을 한다는게 트랙젝션의 의미구요... <? 
		
		
		return view; 
	}
	
	//file upload method 
	//중요한건, 글번호를 받아와야해요. 
	public boolean fileUP(MultipartHttpServletRequest multi, int bnum) throws Exception{
		
		//file은 항상 '절대경로'를 사용합니다. 
		//저장공간에 대한 물리경로를 잡아야 합니다.
		//경로는 다 resources 에 넣어야해요. 
		String path = multi.getSession().getServletContext().getRealPath("/");
		
		path += "resources/upload/";
		
		log.info(path);
		
		//폴더가 없으면, 폴더를 만들어서 저장하라고 해야합니다. 
		File dir = new File(path);
		
		if(dir.isDirectory() == false) {
			dir.mkdir(); // 폴더가 없는 경우 폴더 생성 
		}
		
		//실제 파일명과 저장 파일명을 함께 관리한다. ( origianl name 은 이건데 이 이름으로 저장돼있다 sysname)
		Map<String, String> fmap = new HashMap<String, String>();
		
		fmap.put("bnum",String.valueOf(bnum));
		
		//multiple 로 해놓으면 list 로 꺼내옵니다.
		//file 전송 시 기본값을 html에서 multiple 로 해 놓고, 
		//multipart 요청 객체에서 꺼내올 때는 List 를 사용한다. 
		List<MultipartFile> fList = multi.getFiles("files");
		
		for(int i = 0; i < fList.size(); i++) {
			MultipartFile mf = fList.get(i);
			String on = mf.getOriginalFilename();//수국 이라고 되있으면 hydrangeas라고 저장이 되잖아요. 그 파일업로드하려면 뜨는 이름. 
			fmap.put("oriName",on);
			
		//변경된 파일 이름 저장
			String sn = System.currentTimeMillis() + "."//확장자 구분 점 
					+on.substring(on.lastIndexOf(".")+1);// 마시막부터 시작해서, . 을 찾아서 그 . 다음 거부터 찾아와라 (+1)
			//즉 여기서 +"." 을 안 해 줄 거면 +1을 안 해줘도 돼요. 
			
			fmap.put("sysName",sn);
			
			//해당 폴더에 파일 저장하기
			mf.transferTo(new File(path + sn));
			// 파일이 통신을 거쳐서 돌아오면 그냥 data 예요 그래서 들어오는 data 를 다시 file 로 만들어서 저장한다고 보시면 돼요. 
			//그래서 save 가 아니라 , 파일로 만든다는 transfer 이라고 보시면 돼요. 
			
			bDao.fileInsert(fmap);
		}
		
		return false;
		
		
	}
	
	
	public ModelAndView getContents(Integer bnum) {
		mv = new ModelAndView();
	
		//조회수 1 증가하는 작업 
		bDao.viewUpdate(bnum);
		
		// 글 내용 가져오기 
		BoardDto board = bDao.getContents(bnum);
		
		// 파일 목록 가져오기 
		List<BfileDto> bfList = bDao.getBfList(bnum); 
				
		// 댓글 목록 가져오기 
		List<ReplyDto> rList = bDao.getReplyList(bnum);
		
		// 모델에 데이터 담기 
		mv.addObject("board", board);
		mv.addObject("bfList", bfList);
		mv.addObject("rList", rList);
		
		
		//뷰 이름 지정하기 
		mv.setViewName("boardContents");
		
		return mv; 
	}
	
	@Transactional
	public Map<String, List<ReplyDto>> replyInsert(ReplyDto reply){
		Map<String, List<ReplyDto>> rMap = null;
		
		try {
			//동시에 두 가지 작업을 같이 합니다 
			// 1. 댓글을 db에 집어넣는다. 
			bDao.replyInsert(reply);
			
			// 2. 댓글 목록을 다시 가져온다. 
			List<ReplyDto> rList = bDao.getReplyList(reply.getR_bnum());
			
			rMap = new HashMap<String,List<ReplyDto>>();
			rMap.put("rList",rList);
			
		}catch(Exception e) {
			//e.printStackTrace();
			rMap = null; 
		}
		
		return rMap;
	}
	//ajax는 두 가지 콘트롤러 사용가능 
		// 일반 vs restcontroller 
		// 일반콘트롤러에서는 annotation 을 사용해야한다. 
		// responseBody 


	public void fileDown(String sysName, HttpServletRequest request, HttpServletResponse response) {
		// 리퀘스트를 통해 파일의 절대 경로를 찾아줄거예요
		
		//리소스 전까지의 경로
		String path = request.getSession().getServletContext().getRealPath("/");

		path += "resources/upload/";
		log.info(path);
		
		String oriName = bDao.getOriName(sysName);
		path += sysName;// 다운로드 파일 경로 + 파일명
		
		// 이 파일은 실질적으로 서버 디스크에 있기때문에 
		// 이걸 꺼내와서 다시 사용자 브라우저에 보내야해요 그래서 
		// 서버 저장장치 (디스크) 에서 저장된 파일을 읽어오는 통로가 필요합니다. 
		// 이런 통로를 스트림이라고 합니다 
		// 읽어오는 스트림을 inputStream 이라 합니다.
		InputStream is = null;
		
		//사용자 컴퓨터에 파일을 보내는 통로. 통로는 무조건 통로예요 보내는 통로 받는 통로 다 달라요
		OutputStream os = null;
		
		//1. request 받은 이유 : 경로를 받기 위해서.  
		//2. response에 파일을 담아서 클라이언트 브라우저에 보냅니다.
		// response가 사용자 브라우저에 가는 녀석입니다. 
		//3. oriname은 파일이 다운로드가 될 때 원래이름으로 복원하기 위해서 가져오는 녀석입니다. 
		// 파일은 실질적으로 디스크에 저장이 됐다가 그걸 꺼내와야 하기 때문ㅇ ㅔinputStream이 꺼내오는 녀석
		// 파일을 타 컴퓨터에 보내기 위해 사용하는 객체가 outputStream입니다 .
		// 뭐 파일을 가져오려했는데 파일이 없을 수도 있고 등등의 문제로 얘네들은 트라이캐치문 내에서 사용합니다. 
		
		try {
			//먼저 파일명을 인코딩 해야한다 ( 파일명이 한글인 부분이 깨지지 않도록 )
			//서블릿할때는 리퀘스트에 인코딩하고 레스폰스에서도 하고 그랬잖아요 그런데 스프링에서는 xml에 했잔하요 
			// 이건 경우가 달라요 이건 response에 직접적으로 집어넣기때문에 스프링 범위 바깥으로 나가요
			// 그래서 이 안에 넣어서 보내는 경우 인코딩을 따로 해 줘야 해요 안 해주면 한글파일일 경우에는 문자가 깨져요
			
			String dFileName = URLEncoder.encode(oriName, "UTF-8");
			//최종적으로 들어가는 이름은 dFileName입니다.
			
			// 파일 객체 생성
			// 메모리 상에 파일을 가져와야 (디스크에 저장된 파일을 메모리에 끌어와야 아웃스트림으로 보낼수있는거죠 작업은 메모리에서 처리가 되는거예요)
			//경로 찾아가서 해당파일 열어서 file 에 저장합니다 File은 메모리상의 파일객체입니다. 디스크의 파일을 읽어서 메모리의 file로 가져옵니다.
			File file = new File(path);
			is = new FileInputStream(file); //inputStream은 상위녀석이에요. 큰 빨대입니다. 
			//inpuststeramd은 읽어오는 빨때, outStream은 보내는 빨때. 
			//fileinputStram은 파일을 처리하기 위해 만들어진 애. 
			
			//응답 객체 (response) 의 헤더 설정 
			// output 통로에, 전체 덩어리로 못보내고  쪼개서 패킷이라는 덩어리들을 하나씩 보내주게 되는데요.
			// 라우터가 있잖아요, 첫번째 패킷은 이쪽 다른 패킷은 다른 라우터 쓰고, 경로가 그ㄸ그때 다르게 설정이 돼요/
			//그래서 하나하나마다 어디로 가야한다 라는 내용이 명시가 돼야해요 
			// 그런 부분드를 header라고 합니다. 
			
			//파일전송용 컨텐츠 타입 설정 
			response.setContentType("application/octet-stream");
			//octet 은 8진(1바이트), 운영프로그램에서 바이트 단위로 만든 연속된 프로그램 형태다 라는 말이구요 
			// 어플리케이션 텍스트/ 제이슨 써봤죠? json 오브젝트를 보낸다, 텍스트를 보낸다 처럼 보내는 데이터마다 다른 콘텐츠 타입이 지정이 됩니다. 
			// 바이트 단위로 이루어진 이진데이터. 비트를 8개씩 잘라서 하나의 바이트로 묶어서 원래 상태로 복원해야한다. 고 알려주는 겁니다.
			//특수문자 출력하려면 역슬래시 쓴다고 했어요. 
			response.setHeader("content-Disposition", "attachment; filename=\""+dFileName+"\"");
			//attachment의 이름이 dfilename 입니다. 
			// 위 코드는 attachment; filename="gkgkgk.jpg"
			//httmp 가 다운할 때ㅁ는 콘텐츠 타임은 contentpytpe그거고, 헤터는 저렇게 한다고 정해논거예요. 
			
			//응답 객체와 보내는 통로 연결 . 파일을보내려면 이웃풋스트림을 써야합니다.
			os = response.getOutputStream();
			
			//파일전송(byte단위로 전송) .1024 : 1키로 바이트
			//이 키로바이트 단위가요 우리가 탐색기에서 봤ㅇ르때 1kb보다적으면 그냥 1kb로 표시를 해요 
			// 기본적으로 컴터상에서 최소단위는 바이트이지만바이트는 너무 작으니까 바이트로 처리안하고 표현단위로 키로바이트씁니다. 
			//파일전송시에는 ㅣ로바이트로 배열을 만들어서 처리하는게 일반적이에요.
			//더 이상 읽어올 게 없으면, 데이터를 읽어오는 메소드가 -1을 리턴을 해요
			byte[] buffer = new byte[1024];
			int length;
			//인풋스트림이 파일에서 데이터를 읽어와요. 
			//파일이 인풋스트림에 들어갔는데 이걸 꺼내서 아웃풋스트림에 넘어갈때 1키바씩 끊어옵니다. 
			//이 끊어주는 작업을하는게 read인데, 그냥 끊어오면안되니 이걸 buffer에 담아요, 
			// 버터가 1키바씩 담당하니까, 그리고 그걸 최종적으로 os넣습니다. 암튼읽어서 버터에 집어넣구요 
			// 자 이렇게 1024씩 저장하다가 더 이상 읽어올 게 없으면 -1이 됩니다. 
			// read 가 나 얼마큼씩읽어왔다고 알려줘요 1키바만큼 읽어왔으면 lo24,500읽어오면 500. 더 이상 없으면 -1. 
			while((length = is.read(buffer)) != -1) {
				os.write(buffer, 0, length);
				//버터의 시작지점의 길이만큼 해라..<? 
			}
			//더 이상 꺼낼게 없으면 lentg 에 -1ㅇ들어가구요. != -1 인데 들어온 값이 -1이면 false가 되니까 꺼집니다. 

		}catch(Exception e){
			e.printStackTrace();
		}finally {
			try {
				os.flush();
				//통신이 끝나면 반드시 끝내줘야해요
				// 브라우저에 전달되기전까지는 os가 남아있는 경ㅇ구가 있어요 그래서 쭉 다시 한 번 더 짜줘야 합니다. 
				
				os.close();
				is.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
			
		}
	}

	
	public ModelAndView upPostProc(Integer postNum) {
		log.info("upPostProc()");
		
		mv = new ModelAndView();
	
		// 글 내용 가져오기 
		BoardDto board = bDao.getContents(postNum);
		
		// 파일 목록 가져오기 
		List<BfileDto> bfList = bDao.getBfList(postNum); 
				
		// 댓글 목록 가져오기 
		//List<ReplyDto> rList = bDao.getReplyList(postNum);
		
		// 모델에 데이터 담기 
		mv.addObject("board", board);
		mv.addObject("bfList", bfList);
		//mv.addObject("rList", rList);
		
		//뷰 이름 지정하기 
		mv.setViewName("updatePost");
		
		return mv; 
		
	}
	
	@Transactional
	public String updateDone(MultipartHttpServletRequest multi, RedirectAttributes rttr) {
		log.info("updateDone()");
		
		String view = null; 
		
		String id = multi.getParameter("bid");
		String title = multi.getParameter("btitle");
		String contents = multi.getParameter("bcontents");
		int bnum = Integer.parseInt(multi.getParameter("bnum"));
		String check = multi.getParameter("fileCheck");
		
		//textarea 는 입력한 문자열 앞 뒤로 공백이 발생한다. 
		//문자열 앞 뒤 공백을 제거해야한다. trim()
		contents = contents.trim();
		
		BoardDto board = new BoardDto();
		board.setBid(id);
		board.setBtitle(title);
		board.setBcontents(contents);
		board.setBnum(bnum);
		
		try {
			bDao.updatePostProc(board);
			view = "redirect: contents?bnum="+board.getBnum();
			rttr.addFlashAttribute("msg","upload successes!");
			
			if(check.equals("1")) {
				fileUP(multi,board.getBnum());
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			view = "redirect:updatePost?postNum="+board.getBnum();
			rttr.addFlashAttribute("msg","error uploading file onto db or updating statementss");
		}

		//파일 업로드가 실패해도 글이 저장이 되게 하려면 여기에 파일 업로드 메소드를 호출하면 된다. 
		// try catch 에 들어가면, transactional 이라했잖아요 얘는 뭐냐면, 일단 우리가 게시글 저장/ 파일용 정보도 다른 테이블에 저장하잖아요
		// 게시글은 저장됐는데 파일 저장이 실패하면 그냥 다 rollback 을 한다는게 트랙젝션의 의미구요... <? 
		
		
		return view; 
	}
		

	@Transactional
	public String deletePostProc(Integer postNum) {
		
		String view;
		
		try {
			
			bDao.deleteAllComments(postNum);
			bDao.deleteFileUploaded(postNum);
			bDao.deleteBoardPost(postNum);
			view = "redirect:list";
		}catch(Exception e) {
			//e.printStackTrace();
			log.info("!! error occured at deletePostProces (Service Class)" );
			view="redirect:contents?bnum="+postNum;
			
		}

		return view;
	}


	@Transactional
	public Map<String, List<BfileDto>> deleteFileFromDisk(String sysname, int bnum) {
		log.info("deleteFileFromDisk()");
		String str;
		Map<String, List<BfileDto>> fmap = null;
		//String falsecheck = "true";
		
		String absPath = session.getServletContext().getRealPath("/");
		
		absPath += "resources/upload/"+sysname;
		log.info(absPath);
		
		try {
			bDao.deleteFileBySysName(sysname);	
			List<BfileDto> fList = bDao.getBfList(bnum);
			System.out.println(fList); // 이렇게 하면 [] 이 뜸. 
			if(fList.isEmpty()) {
				//falsecheck = "false";
				log.info("no file detected so falsecheck is initialized as false");
				// && falsecheck != "false" inject to if sentence with && operator
			}
			
			log.info("successfully delete file from db, brought leftovers List, it might not have any leftovers because system just deleted a File from your db");
			File ondisk = new File(absPath);
			
			if(ondisk.exists()) {
				fmap = new HashMap<String, List<BfileDto>>();
				fmap.put("fList",fList);
				log.info("you are not allowed to appear");		
				
			}else{
				fmap = null; // 만약에 json 이 null되면..?
				log.info("no matching file exists in Server disk or no filename leave on db");
			}
			
		}catch(Exception e){
			e.printStackTrace();
			fmap = null;
		}

		return fmap;
	}

	
}//class end 
