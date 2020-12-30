<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Insert title here</title>
<link rel="stylesheet" href="resources/css/style.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script>

$(function(){
	
	var alarm = "${msg}";
	if(alarm != ""){
		alert(alarm);
	}
	
	//지금session 에 mb가 있죠?
	var identity = "${mb.m_id}";
	$("#mname").html(identity);
	$(".suc").css("display","block");
	$(".bef").css("display","none");
	
	//컨트롤러에서 전달하는 메세지 출력. 
});
</script>
</head>
<body>
<div class="wrap">
	<header>
		<div>
			<jsp:include page="header.jsp"/>
		</div>
	</header>
	<section>
		<div class="content">
			<div class="board-form">
				<div class="user-info">
					<div class="user-info-sub">
						<p class="grade">Grade [${mb.g_name }]</p>
						<p class="point">POINT [${mb.m_point}]</p>
					</div>
				</div>
				<h2 class="login-header">BOARD</h2>
				<div class="data-area">
					<div class="title-row">
						<div class="t-no p-10">Num</div>
						<div class="t-title p-30">Title</div>
						<div class="t-name p-15">User</div>
						<div class="t-date p-30">UplodeT</div>
						<div class="t-view p-15">View</div>
						<!--  이제 div 에서 꺼내와야 하는데, 몇 개가 들어갈지 모르죠. 한 페이지에 10 개, 인데 마지막 페이지는 몇 개가 찍힐 줄 모르잖아요. 그러니 jstl 의 foreach 를 사용합니다.  -->
					</div>
					<c:forEach var="bitem" items="${bList}"><!-- bList에 들어갈 애들은 게시글 목록. 목록을 한 줄씩 꺼내서 bitem 에 집어넣고 꺼내는 겁니다.  -->
					<div class="data-row">
						<div class="t-no p-10">${bitem.bnum }</div>
						<div class="t-title p-30"><a href="contents?bnum=${bitem.bnum}">${bitem.btitle}</a></div>
						<!-- 게시글 마다 해당되는 리크가 걸린다.  -->
						<div class="t-name p-15">${bitem.bid }</div>
						<div class="t-data p-30"><fmt:formatDate pattern ="yyyy-MM-dd hh:mm:ss" value="${bitem.bdate}"/>
						</div>
						<div class="t-view p-15">${bitem.bviews }</div>
					</div>
					</c:forEach>
				</div>
				<div class="btn-area">
					<div class="paging">${paging }</div> 
					<!--  button for writing -->
					<button class="wr-btn" onclick="location.href='./writeFrm'">
					new post
					</button>
				</div>
			</div><!--  board-form -->
		</div><!-- content  -->
	</section>
	<footer>
		<jsp:include page="footer.jsp"/>
	</footer>
</div>
</body>
</html>