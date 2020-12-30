<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>

<html>
<head>
<title>Home</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
	
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/bxslider/4.2.12/jquery.bxslider.min.js"></script>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/bxslider/4.2.12/jquery.bxslider.css">
<link rel="stylesheet" href="resources/css/style.css">
<script type="text/javascript">
$(function(){
	var chk = "${msg}";
	
	if(chk != ""){
		alert(chk);
		location.reload(true);
	}
	
	$('.slider').bxSlider({
		auto: true,
		slideWidth: 600,
	});
	
	var mql = window.matchMedia("screen and (max-width: 768px)");
	mql.addListener(function(e){
		if(!e.matches){
			slider.reloadSlider();
		}
	});
});

</script>
</head>
<body>
	<div class="wrap">
		<header>
			<jsp:include page="header.jsp"/>
			<!-- 인클루드는 원래 옆에있는거 꺼내와서 가져와요 인클루드는 유일하게, 그냥 경로 무시하고 같은 폴더에있는녀석가지고옵니다.
				원래는 경로따라 resource이런거 설정을 해주고 그래야 하는데. -->
			<!-- 소스보기를 하면 쏙 들어오는 상태로 처리가 된다는 걸 알 수 있습니다. 그래서 불필요한 head 나 body 태그를 넣지 않은 거죠. hearder 에 있는 코드가 그.대.로 여기에 들어오는 겁니다.  -->
		</header>
		<section>
		<div class="content-home">
		<div class="slider">
			<div><img src="resources/image/Chrysanthemum.jpg"></div>
			<div><img src="resources/image/Desert.jpg"></div>
			<div><img src="resources/image/Lighthouse.jpg"></div>
			<div><img src="resources/image/Tulips.jpg"></div>
		</div>
	</div>
		</section>
		<footer>
		<jsp:include page="footer.jsp"/>
		</footer>
	</div>
</body>
</html>
