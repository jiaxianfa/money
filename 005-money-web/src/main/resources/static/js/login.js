var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
	try {
		if (window.opener) {                
			// IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性              
			referrer = window.opener.location.href;
		}  
	} catch (e) {
	}
}

//按键盘Enter键即可登录
$(document).keyup(function(event){
	if(event.keyCode == 13){
		login();
	}
});
//登录点击事件	登录成功以后跳转到当前页
function Login() {
	//获得手机号和密码
	var phone = $.trim($("#phone").val());
	var loginPassword = $.trim($("#loginPassword").val());
	// 验证是否为空
	if (phone==null||phone=="") {
		//为空，所以进行聚焦事件
		$("#phone").focus();
		return;
	}
	if (loginPassword==null||loginPassword=="") {
		$("#loginPassword").focus();
		return;
	}
	//登录
	$.post("/005-money-web/loan/page/loginSubmit", {"phone": phone, "loginPassword": $.md5(loginPassword)}, function (data) {
		if (data.code==1) {
			alert($("#cookie").val())
			//登录成功以后进入主页
			// window.location.href = "/005-money-web/index";
			//登录成功以后进入当前页
			window.location.href=$("#ReturnUrl").val()
		}
		if (data.code == 0) {
			alert(data.message);
		}
	});
}
