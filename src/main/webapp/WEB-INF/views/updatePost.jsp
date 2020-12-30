<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>New Post</title>
<link rel="stylesheet" href="resources/css/style.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script>

	$(function(){
		
		var alarm = '${msg}';
		if(alarm != ""){
			alert(alarm);
		}
	
		//지금session 에 mb가 있죠?
		var identity = "${mb.m_id}";
		$("#mname").html(identity);
		$(".suc").css("display", "block");
		$(".bef").css("display", "none");

		//컨트롤러에서 전달하는 메세지 출력. 
	});
</script>
</head>
<body>
	<div class="wrap">
		<header>
			<jsp:include page="header.jsp"/>
		</header>
		<section>
			<div class="content">
				<!--  boardDto 에 담아서 글을 db에 넘길 거예요. 아이디를 잘못 입력하면 글을 썼는데도 db 에 저장이 안 될 수 있잖아요 
				그러니까 id는 미리 hidden 으로 value를 받아온 걸 너헝서 진행을 합니다.  -->
				<form action="./updateDone" class="write-form" method="post"
					enctype="multipart/form-data">
					<!--  이렇게하면 martipart request 객체가 만들어져서 해당 데이터가 담겨 전송됩니다.  -->
					<div class="user-info">
						<div class="user-info-sub">
							<p class="grade">Grade [${mb.g_name }]</p>
							<p class="point">POINT [${mb.m_point}]</p>
						</div>
					</div>
					<h2 class="login-header">new post</h2>
					<input type="hidden" name="bid" value="${mb.m_id}">
					<input type="hidden" name="bnum" value="${board.bnum }">
					<input type="text" class="write-input" name="btitle" autofocus placeholder="Title" value="${board.btitle }" required>
					<textarea rows="15" name="bcontents" placeholder="contents area" class="write-input ta"
					>${board.bcontents }</textarea><!-- 여기선 공간을 띄우면 안 됩니다. 왜냐면그게 그대로 브라우저에 적용이 돼요.  -->
					<div class="filebox">
						<div class="loadDiskFile">
							<c:if test="${!empty bfList}">
								<label style="width:100%" style="margin-bottom: 10px";>Click file to delete</label>
							</c:if> 
							<c:if test="${!empty bfList }">
								<c:forEach var="diskfile" items="${bfList }">
									<label style="width:100%;" onclick="del('${diskfile.bf_sysname}')">
										${diskfile.bf_oriname}
									</label>
								</c:forEach>
							</c:if>
						</div>
						<label for="file">upload</label>
						<input type="file" name="files" id="file" multiple>
						<!--  실제로 파일을 가지고 오는 애들이에요. 파일을 끌고와서 페이지에 잠시 저장하는 녀석입니다.  -->
						<input class="upload-name" value="select file" readonly>
						<input type="hidden" id="filecheck" value="0" name="fileCheck">
						<!-- 업로드를 하는 경우가 있고 아닌 경우가 있죠 모든 파일을 업로드 하지 않잖아요. 파일이 있으면 value 가 1이면 파일을 꺼내서 저장하겠따., 파일체크가 0 이면 파일을 보내지 않겠다. -->
					</div>
					<div class="btn-area">
						<input class="btn-write" type="submit" value="up">
						<input class="btn-write" type="reset" value="rs">
						<input class="btn-write" type="button" value="bc" onclick="location.href='./list?pageNum=${pageNum}'">
					</div>
				</form>
			</div>
		</section>
		<footer>
			<jsp:include page="footer.jsp"/>
		</footer>
	</div>
</body>


<script type="text/javascript">
$("#file").on('change',function(){
	var fileName = $("#file").val();
	console.log(fileName);
	$(".upload-name").val(fileName);
	
	if(fileName == ""){
		console.log("empty");
		$("#filecheck").val(0);
		$(".upload-name").val("select file");
		
	}else{
		console.log("not empty");
		$("#filecheck").val(1);
	}
});

function del(sysname){
	
	var check = confirm("are you sure removing this file away?");
	
	if(check){
		var object = {"sysname": sysname};
		object.bnum = ${board.bnum};
		
		$.ajax({
			
			url:"deleteFileFromDisk",
			type:"get",
			data:object,
			dataType: "json",
			success : function(data){
				
				var display;
				var list = data.fList;
				console.log(data);//{fList:Array(1)}
				console.log(list);//[{...}]
				
				if(list.length == 0){
					display = '<label style="width:100%" style="margin-bottom: 10px";>Click file to delete</label>';
				}
				else{
					for(var i = 0; i < list.length; i++){
						display += '<label style="width:100%;" onclick="del(\''
								+ list[i].bf_sysname
								+'\')">'+ list[i].bf_oriname+'</label>';
					}
				}
				
				$(".loadDiskFile").html(display);
				
			},
			error: function(error){
				alert("delete failure");
			}
			
		});

	}
}


/*파일 여러개 선택하면 이걸 다 보이게 하고 싶을 때. 
$("#file").on('change', function(){
	   var files = $("#file")[0].files;
	   console.log(files);
	   
	   var fileName = "";
	   
	   for(var i = 0; i < files.length; i++){
	      fileName += files[i].name + " ";
	   }
	   console.log(fileName);
	   
	   $(".upload-name").val(fileName);
	   
	   if(fileName == ""){
	      console.log("empty");
	      $("#filecheck").val(0);
	      $(".upload-name").val("파일선택");
	   }
	   else {
	      console.log("not empty");
	      $("#filecheck").val(1);
	   }
	});
	*/
</script>
</html>