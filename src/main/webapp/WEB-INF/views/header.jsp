<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
	<div class="top-bar">
		<div class="content">
			<img alt="logo" src="resources/image/ajaxicon.png"
				class="top-left logo" onclick="gohome();">
				<!-- 로그인 한 후 와 전의 home 이 다름. 분기해야함.  -->
			<h2 class="top-left">Kotlin Board</h2>
			<nav class="top-right">
				<ul>
					<li class="suc" id="mname">test</li>
					<li class="suc"><a href="./logout">Logout</a></li>
					<li class="bef"><a href="./loginFrm">Login</a></li>
					<li class="bef"><a href="./joinFrm">Join Us</a></li>
				</ul>
			</nav>
		</div>
	</div>
	
	<!--  인클루드 할 애가 script src를 가지고 있으면 이런 inner page 에서는 굳이 import 를 안 해도 됩니다.  -->
	<script>
	
	function gohome(){
		var id = "${mb.m_id}";
		//나중에 mb 라는 이름으로 사용자 정보를 저장할거에요
		if(id == ""){
			location.href = "./";
		}else if(id != ""){
			location.href="./list";
		}
	}
	
	</script>