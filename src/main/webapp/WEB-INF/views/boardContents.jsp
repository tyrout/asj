<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Insert title here</title>
<link rel="stylesheet" href="resources/css/style.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script>
	$(function() {

		var alarm = "${msg}";
		if (alarm != "") {
			alert(alarm);
		}

		//지금session 에 mb가 있죠?
		var identity = "${mb.m_id}";
		$("#mname").html(identity);
		$(".suc").css("display", "block");
		$(".bef").css("display", "none");

		//컨트롤러에서 전달하는 메세지 출력. 
		
		
		//업데이트, 하이드를 숨겨놓고 시작. 
		$('#upbtn').hide();
		$('#delbtn').hide();
		var mid = "${mb.m_id}";//로그인한 아이디
		var bid = "${board.bid}";//작성자 아이디 
		
		if(mid == bid){
			$('#upbtn').show();
			$('#delbtn').show();
		}
	});
</script>
</head>
<body>
	<div class="wrap">
		<header>
			<jsp:include page="header.jsp" />
		</header>
		<section>
			<div class="content">
				<div class="user-info">
					<div class="user-info-sub">
						<p class="grade">grade[${mb.g_name }]</p>
						<p class="point">Point[${mb.m_point }]</p>
					</div>
				</div>
				<h2 class="login-header">Detail</h2>
				<table>
					<tr height="30">
						<td width="100" bgcolor="#efd4cf" align="center">NUM</td>
						<td colspan="5">${board.bnum }</td>
					</tr>
					<tr height="30">
						<td bgcolor="#efd4cf" align="center">Writer</td>
						<td width="150">${board.mname }</td>
						<!-- boardDto 에 있는 내용 가져오는 거예요.  -->
						<td bgcolor="#efd4cf" align="center">Date</td>
						<td width="200">${board.bdate }</td>
						<td bgcolor="#efd4cf" align="center">Views</td>
						<td width="200">${board.bviews }</td>
					</tr>
					<tr height="30">
						<td bgcolor="#efd4cf" align="center">Title</td>
						<td colspan="5">${board.btitle }</td>
					</tr>
					<tr height="200">
						<td bgcolor="#efd4cf" align="center">Contents</td>
						<td colspan="5">${board.bcontents }</td>
					</tr>
					<tr>
						<th>attachments</th>
						<td colspan="5">
							<!--  service 의 bfList임.무슨 목록 가져오기. --> 
							<c:if test="${empty bfList }">
								No attachments
							</c:if> 
							<c:if test="${!empty bfList }">
								<c:forEach var="file" items="${bfList }">
									<a href="./download?sysName=${file.bf_sysname }"> 
									<span class="file-title">${file.bf_oriname }</span>
									</a>&nbsp;&nbsp;	
								</c:forEach>
							</c:if>
						</td>
					</tr>
					<c:if test="${!empty bfList}">
						<tr>
							<td colspan="5">
							<!--fn:contains 메소드 :  뒤에있는 문자열이 앞에있는 문자열에 포함돼있으면 true -->
							<c:forEach var="f" items="${bfList }">
									<c:if test="${fn:contains(f.bf_sysname, '.jpg') }">
										<img src="resources/upload/${f.bf_sysname }" width="100">
									</c:if>
									<c:if test="${fn:contains(f.bf_sysname, '.png') }">
										<img src="resources/upload/${f.bf_sysname }" width="100">
									</c:if>
									<c:if test="${fn:contains(f.bf_sysname, '.gifs') }">
										<img src="resources/upload/${f.bf_sysname }" width="100">
									</c:if>
							</c:forEach>
							</td>
						</tr>
					</c:if>
					<tr>
						<td colspan="6" align="center">
							<button class="btn-write" id="upbtn" onclick="location.href='./updatePost?postNum=${board.bnum}'">Ed</button>
							<button class="btn-write" id="delbtn" onclick="location.href='./deletePost?postNum=${board.bnum}'">De</button>
							<button class="btn-sub" onclick="location.href='./list?pageNum=${pageNum}'">Bc</button>
						</td>
					</tr>
				</table>
				<!-- 댓글 작성 양식 /  id로 가져와야하죠? (name은 왜 넣었는지 기억안나신다함  -->
				<form id="rFrm">
					<textarea rows="3" class="write-input ta" name="r_contents" id="comment" placeholder="leave comments" style="width:100%; margin-top:10px; margin-bottom:0;"></textarea>
					<input type="button" value="Send Comments" class="btn-write" onclick="replyInsert(${board.bnum})" style="width:100%; margin-bottom:30px;">
					<!-- 함수 안에 게시글 번호가 같이 저장이 돼야 한다.  -->
				</form>
				<!-- 댓글 목록 출력 부분 -->
				<table style="width:100%">
					<!--  모든 tr 에 다 적용 -->
					<tr bgcolor="pink" align="center" height="30">
						<td width="20%">Writer</td>
						<td width="50%">Contents</td>
						<td width="30%">Date</td>
					</tr>
				</table>
				<table id="rtable" style="width:100%">
					<c:forEach var="r" items="${rList}">
					<tr height="25" align="center">
						<td width="20%">${r.r_id }</td>
						<td width="50%">${r.r_contents }</td>
						<td width="30%">${r.r_date }</td>
					</c:forEach>
				</table>
			</div>
		</section>
		<footer>
			<jsp:include page="footer.jsp" />
		</footer>
	</div>
</body>

<script src="resources/js/jquery.serializeObject.js"></script>
<script>
function replyInsert(bnum){
	//form data 를 json 으로 변환을 해 줘야 한다!
	var replyFrm = $('#rFrm').serializeObject();
	//들어와서 jquery 로 바뀌는 것은, 두 가지 - text area 와 inputtag 가지. 결국 버튼은 빼고 
	//텍스트에어리어만 들어오겠쬬?
	replyFrm.r_bnum = bnum;//글번호 
	replyFrm.r_id = "${mb.m_id}";//작성자 아이디 (로그인한 아이디)

	console.log(replyFrm);
	
	//ajax로 전송 
	$.ajax({
		url:"replyIns",
		type:"post",
	data:replyFrm,
	dataType:"json",
	success:function(data){
		//return Data로 rMap 이 들어왔죠. 
		var rlist = "";
		//list가 하나면 하나만 rlist에 저장될 것임. 
		
		var dlist = data.rList;// data == rMap;
		
		//기존 출력부분을 싹다 지워버리고 새로 tr 부분을 만들 거예요 .
		
		for(var i=0; i<dlist.length;i++){
			rlist += '<tr height="25px" align="center">'
			+ '<td width="20%">' + dlist[i].r_id + '</td>'
			+ '<td width="50%">' + dlist[i].r_contents + '</td>'
			+ '<td width="30%">' + dlist[i].r_date + '</td>'
		}
		
		$('#rtable').html(rlist);
		$("#comment").val("");
	},
	error: function(error){
		console.log(error);
		alert(error);
	}
	
	});

	
}

</script>
</html>