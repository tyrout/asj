package com.kotlin.board.util;

public class Paging {
	//궁극적으로 전달되는 것은 문자열입니다 ${paging}에 전달되는 것은 문자열이라는 말임. 
	// a태그가 걸린 문자열입니다. 

	private int maxNum; // 전체 글 개수 
	private int pageNum; // 현재 페이지 번호 
	private int listCnt; // 페이지 당 글 개수
	private int pageCnt; // 한 페이지 내에 몇 개의 페이지 목록을 보여 줄 것인가. 이번 프로젝트에서는 2 로 설정  
	// 한 페이지에서 보여질 페이지 페이징 번호 개수, ex 1,2,~10 . + 총 100 페이지가 있으면 10개씩 페이지를 끊겠다 : paging 
	// 아무 사이트 가서 마우스를 대 보면 아래에 링크가 떠요, 즉 다들 a태그를 쓴다는 말이죠?
	private String listName; // 목록 페이지의 이름(종류) ex) 배구 목록, 농구 목록 등 게시판 목록 자체가 여러개가 있다고 하면 그 목록 자체가 구분이 돼야하죠. 

	public Paging(int maxNum, int pageNum, int listCnt, int pageCnt, String listName) {
		this.maxNum = maxNum;
		this.pageNum = pageNum;
		this.listCnt = listCnt;
		this.pageCnt = pageCnt;
		this.listName = listName;
		 
	}

	public String makePaging() {

		// 1 전체 페이지 개수 구하기 
		// ex 글 개수 9개 이하 - 페이지 개수 1개.
		// ex 글 개수 11 이상 - 페이지 개수 2 이상 
		// %로 하면 딱 10개까지는 나머지가 0 이겠죠?
		//maxNum  이 1~9까지는 전부다 0보다 큰 값이 나머지로 나오죠? ( 왜냐면 여기서는 음수 취급 안 함 ) 
		// % 랑 / 는 다름. 첫번째 조건은 음수가 나오니까 0 + 1 이 되고요.
		int totalPage = (maxNum % listCnt > 0) ? maxNum/listCnt + 1 : maxNum/listCnt;

		//현재 페이지가 속해 있는 그룹 번호 
		int curGroup = (pageNum % pageCnt > 0)? pageNum/pageCnt +1 : pageNum/pageCnt;

		StringBuffer sb = new StringBuffer();

		// 현재 페이징 그룹의 시작 페이지 번호
		int start = (curGroup * pageCnt) - (pageCnt-1);

		//현재 그룹의 끝 페이지 번호 
		int end = (curGroup * pageCnt >= totalPage) ? totalPage:curGroup * pageCnt;

		// 스타트페이지가 1이 아닌 경우. 
		if(start != 1) {
			sb.append("<a class='pno' href = '"+listName+"?pageNum="+(start-1)+"'>");
			sb.append("&nbsp;previous&nbsp;");
			sb.append("</a>");
			// == <a class='pno' href= 'list? pageNum=5'> previous </a>
		}

		//이전과 다음 버튼 사이의 페이지 번호 처리 
		for (int i = start; i<= end; i++) {
			if (pageNum != i) {
				// 현재 있는 페이지 제외한 페이지에 이동 링크를 건다.
				sb.append("<a class='pno' href='"+listName+"?pageNum="+i+"'>");
				sb.append("&nbsp;"+ i + "&nbsp;</a>");
				//sstringbutter는 스트링을 만들기 위한 메모리 버퍼를 쓴다. 스트링은 이미 만들어진 애라서, 
				// 콜스택에서 스트링을 하나 만들면 heap 에 실제로 만ㄷ르어지죠. 메소드에서 string + string 을 하면 두 개가 합쳐진 것을 
				// 새로 만ㄷ르어요  각각 따로 있던 두개를 버리고 새로운 통합적 인스턴스를 만드는 거죠. 이렇게 하면 메모리 사용에 좀  그렇죠 
				// 그래서 스트링버퍼라는, 문자길이 제한이 없는, 임의의 공간ㅇ르 만들어서 계속 문자를 추가해요. append는 그냥 여태까지 쓰인거 뒤에 계속계속 문장을 추가해주는 역할을 해요. 
				// 어펜들르 사용하면 새로인스턴스를 만들지 않고 기존의 문자열을 확장하는 식으로 해요. 
			}else {
				//현재 페이지라서 링크를 걸지 않는다. 
				sb.append("<font class='pno' style='color:red;'>");
				sb.append("&nbsp;"+ i + "&nbsp;</font>");
			}
			//ex 현재 보고있는 페이지 번호 : 7p 
			// < a class='pno' href="list?pageNum=6'> 6 </a>
			// 그럼 7 페이지에는 font 태그가 들어간다. 
			// <font class='pno' style='color:red;'> 7 </font>
			// < a class='pno' href="list?pageNum=8'> 8 </a>
			// < a class='pno' href="list?pageNum=9'> 9 </a>
			// < a class='pno' href="list?pageNum=10'> 10 </a>
			// listName 은 현재로서는 게시판이 하나라 쓸 모없지만... 

		}//for end 


		// 'next' button 처리 - 첫 번째 paging 에서는 이전 버튼 없음.
		// 마지막 paging 에서는 다음 버튼 없음. 즉 스타트 번호가 1일때 혹은 마지막번호에 있을 때.
		// 빼기는 문자열로 변환 할 일이 없죠 실수로라도? 
		// 암튼 + 할 때는 ()를 꼭 붙여줘야 합니다. 
		if(end != totalPage) {
			sb.append("<a class='pno' href='"+listName+"?pageNum="+(end + 1)+"'>");
			sb.append("&nbsp;next&nbsp;</a>");
		}
		//<a class='pno' href='list?pageNum=6'>next</a>
		
		return sb.toString(); // 문자열로 완성
		
	}//making page end 
}//class end
