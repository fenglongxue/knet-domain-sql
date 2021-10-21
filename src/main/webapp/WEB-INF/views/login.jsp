<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-cn">
<head>
	<meta charset="utf-8">
	<title>数据库查询系统</title>
	<meta name="renderer" content="webkit">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
	<meta name="keywords" content="">
	<meta name="description" content="">
	<link rel="stylesheet" href="${ctx }/ui/css/login.css"/>
</head>
<body>
	<div class="header">
		<h2>DB MANAGER</h2>
	</div>
	<div class="loginContent">
		<div class="row">
			<div class="panel panel-primary">
				<div class="panel-heading">
					<span class="glyphicon glyphicon-user"></span>
					<h3 class="panel-title">
						用户名密码登录
					</h3>
				</div>
				<div class="panel-body">
					<form role="form" method="post" id="form">
						<input type="hidden" name="service" id="service" value="${currentServiceUrl}" />
						<input type="hidden" name="serviceToken" value="knet" />

						<div class="form-group">
							<label for="username">用户名</label>
							<input type="text" value="" class="form-control" id="j_username" name="username" placeholder="英文字符,不能有中文">
						</div>
						<div class="form-group">
							<label for="password">密码</label>
							<input type="password" value="" class="form-control" id="j_password" name="password" placeholder="6-10位英文数字">
						</div>
						<div class="alert alert-danger">
							<div class="error" id="error" style="display:none"></div>
						</div>
						<div class="btn-group">
							<button type="button" id="go" class="btn btn-primary">登 录</button>
						</div>
					</form>
				</div>


			</div>
		</div>
	</div>

	<script src="${ctx }/ui/js/jquery-3.4.1.min.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(function() {
			if (window != top)
				top.location.href = location.href;
			document.onkeydown = function(e) {
				var ev = document.all ? window.event : e;
				if (ev.keyCode == 13) {
					$("#go").click();
				}
			}
			$("#go").click(function() {
				if (!$("#j_username").val()) {
					$("#error").html("请输入用户名").show();
					return false;
				}
				if (!$("#j_password").val()) {
					$("#error").html("请输入密码").show();;
					return false;
				}
				$.post("${ctx}/login", $("form").serialize(), function(data) {
					if (data.code != "0") {
						$("#error").html(data.msg).show();
					} else {
						window.location.replace("${ctx}/index");
					}
				})
			});
		})
	</script>
</body>
</html>