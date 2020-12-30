<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page session="false"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="resources/css/style.css">
<title>로그인</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	var msg = "${msg}";
	console.log(msg);
	if(msg != ""){
		alert(msg);
		location.reload(true);
	}
});
</script>
</head>
<body>
<div class="wrap">
	<header>
	<jsp:include page="header.jsp"></jsp:include>
	</header>
	<section>
	<div class="content">
		<form class="login-form" action="./login" method="post">
			<h2 class="login-header">로그인</h2>
			<input type="text" class="login-input" 
				name="m_id" required autofocus placeholder="아이디">
			<input type="password" class="login-input" 
				name="m_pwd" required placeholder="비밀번호">
			<input type="submit" class="login-btn" value="로그인">
		</form>
	</div>
	</section>
	<footer>
	<jsp:include page="footer.jsp"></jsp:include>
	</footer>
</div>
</body>
</html>