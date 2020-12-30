<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원 가입</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="resources/css/style.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		var msg = "${msg}";
		console.log(msg);
		if (msg != "") {
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
				<form name="joinFrm" class="login-form" action="./memInsert"
					method="post" onsubmit="return check()" id="joinFrm">
					<!-- check 함수가 실행되고 그 값이 true 일 때만 'return'을 한다는 의미입니ㅏㄷ. 
		javascript로 전송을 제어하는 부분을 살펴볼겁니다.  -->
					<h2 class="login-header">회원 가입</h2>
					<input type="text" class="login-input" id="mid" title="아이디"
						name="m_id" autofocus placeholder="아이디">
					<!-- 같은 아이디를 입력하면 contraint violation이라는 exception 이 뜬다. 
				이걸 처리하기 위해서 ajax 라는 함수를 사용한다고 합니다.  -->
					<input type="button" class="idcheck-btn" value="중복확인"
						onclick="idcheck()"> <input type="password"
						class="login-input" title="비밀번호" name="m_pwd" placeholder="비밀번호">
					<input type="text" name="m_name" class="login-input" title="이름"
						placeholder="이름"> <input type="text" name="m_birth"
						class="login-input" title="생일" placeholder="생일"> <input
						type="text" name="m_addr" class="login-input" title="주소"
						placeholder="주소"> <input type="text" name="m_phone"
						class="login-input" title="연락처" placeholder="연락처">
						 <input type="button"
						class="idcheck-btn" value="duplication check"
						onclick="idcheckajax()">
						 <input
						type="submit" class="login-btn" value="가입">
						
				</form>
			</div>
		</section>
		<footer>
			<jsp:include page="footer.jsp"></jsp:include>
		</footer>
	</div>
</body>
<script src="resources/js/jquery.serializeObject.js"></script>
<script type="text/javascript">
	function check() {
		//form 태그의 내용을 전부 가져오기
		var frm = document.joinFrm;
		console.log(frm);
		//joinform 은 폼 태그의 이름입니다. 이걸 통째로 가져와서 frm 이라는 변수에 전체 내용을 넣구요. 

		//submit 버튼을 뺀 나머지 input태그의 개수
		var length = frm.length - 1;
		// submit button은 포함하면 안 되니까요. 
		// 이거하면 inputtag의 개수값이 나오죠. 

		//input 태그 중에 입력이 안된 요소를 확인
		for (var i = 0; i < length; i++) {
			if (frm[i].value == "" || frm[i].value == null) {
				alert(frm[i].title + " 입력!");
				frm[i].focus();
				return false;//action이 실행 안됨.
			}
		}
		//값이 비어있으면 입력이 되어있지 않다는 말이겠죠?
		//그리고 focus를 주고 false 를 보내서 action이 실행안되게합니다. 

		//모든 input에 입력이 다 되었을 경우.
		return true;//action이 실행됨.
	}

	//아이디 중복 여부 확인 함수
	//이 함수의 ajax를 실행하기 위해서 
	//인터셉터 대상에서 제외해야 함.
	//servlet-context.xml에 exclude-mapping을 처리.
	function idcheck() {
		var id = $('#mid').val();
		if (id == "") {
			alert("아이디를 입력하세요.");
			$('#mid').focus();
			return;
		}
		var ckObj = {
			"mid" : id
		};//아이디를 보내면 server 에서 id 를 검색을 해서 check 함. 
		console.log(ckObj);

		$.ajax({
			url : "idCheck",
			type : "get",
			data : ckObj, //페이지쪽에서 보내는 data
			success : function(data) {//성공시 server 에서 return 해서 들어오는 data 
				if (data == "success") {
					alert('ID 사용 가능');
				} else {
					alert('사용할 수 없는 ID');
					$('#mid').val('');//입력 초기화
					$('#mid').focus();//ID 부분에 포커스 주기
				}
			},
			error : function(error) {
				console.log(error);
			}
		});
	}

	function idcheckajax() {

		var joinFrm = $('#joinFrm').serializeObject();
		console.log(joinFrm+" I'm checking it!");

		$.ajax({
			url : "idCheckajax",
			type : "post",
			data : joinFrm,
			dataType:"json",
			success : function(data) {
				console.log(data);
				var str = data.result;
				console.log("i moved to joinFrm with " + str);
				if (data.result == "success") {
					console.log("the id is "+ data.m_id);
					alert("usable id");
					//alert(data.m_id + "is available");
				} else {
					alert(data.m_id + " is already existing id");
					$('#mid').val('');
					$('#mid').focus;
				}
			},
			error : function(error) {
				alert("error occured");
				console.log(error)
			}
		});

	}
</script>
</html>





