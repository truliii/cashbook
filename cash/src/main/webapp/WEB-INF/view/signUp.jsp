<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>sign up</title>
<!-- jQuery -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.0/jquery.min.js"></script>
<script>
	$(document).ready(function(){
		//중복검사
		$("#idCkBtn").click(function(){
			$.ajax({
				url : "./idCk",
				data : {memberId: $("#memberId").val()},
				type : "post",
				success : function(param){
					console.log(param);
					if(param == 0){ //true인 경우 (아이디 사용 가능한 경우)
						$("#idValMsg").text("사용 가능한 아이디입니다");
					} else { //false인 경우
						$("#idValMsg").text("이미 사용 중인 아이디입니다");
						$("#memberId").val("");
					} 
				},
			})
		})
		
		//입력값 유효성 검사
		${}
	})
</script>
</head>
<body>
	<h1>회원가입</h1>
	<form method="post" action="${pageContext.request.contextPath}/signUp">
		<div>
			<label for="memberId">아이디</label>
			<input type="text" id="memberId" name="memberId" required>
			<button type="button" id="idCkBtn">중복검사</button>
			<span id="idValMsg"></span>
		</div>
		<div>
			<label for="memberPw">비밀번호</label>
			<input type="password" id="memberPw" name="memberPw" required>
			<span id="pwValMsg"></span>
		</div>
		<div>
			<button type="submit">회원가입</button>
		</div>
	</form>
</body>
</html>