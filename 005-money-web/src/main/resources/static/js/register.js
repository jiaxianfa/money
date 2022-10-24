//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}


//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}

//注册协议确认
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});
	//手机号失焦事件
	$("#phone").blur(function () {
		//去除空格
		var phone = $.trim($("#phone").val());
		//1、不能为空
		if (phone == null || phone == "") {
			showError("phone", "请输入手机号");
			return;
		}
		//2、长度
		if (phone.length!=11) {
			showError("phone","您输入的手机号码长度不正确")
			return;
		}
		//3、正则表达式	正文要在//之间	满足后面的条件就好执行
		if (!/^1[1-9]\d{9}$/.test(phone)) {
			showError("phone","您输入的手机号码格式有误")
			return;
		}
		//4、是否已经注册		服务端验证	手机号可以显示，所以用get请求
		$.get("/005-money-web/loan/page/checkPhone",{phone:phone},function (data) {
			if (data.code==1) {
				showSuccess("phone");
			}
			if (data.code == 0) {
				showError("phone", data.message);
			}
		});
		//给调用这个方法的对象返回值  目前用不到
		//return true;
	});

	//密码失焦事件
	$("#loginPassword").blur(function () {
		//去除空格
		//this是当前的dom对象
		var loginPassword = $.trim($(this).val());
		//每个条件不满足后必须return，结束方法，不然后面程序还是会执行成功
		//1、不能为空
		if (loginPassword==null||loginPassword=="") {
			showError("loginPassword","密码不能为空")
			return;
		}
		//2、密码长度
		if (loginPassword.length < 6 || loginPassword.length > 20) {
			showError("loginPassword","密码长度必须是6-20位")
			return;
		}
		//3、密码字符只可使用数字和大小写英文字母
		if (!/^[0-9a-zA-Z]+$/.test(loginPassword)) {
			showError("loginPassword","密码只可使用数字和大小写英文字母");
			return;
		}
		//4、密码应同时包含英文和数字
		if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)) {
			showError("loginPassword","密码应同时包含英文字母和数字");
			return;
		}
		showSuccess("loginPassword");
		//给调用这个方法的对象返回值	目前用不到
		//return true;
	});
	//获取验证码点击事件
	$("#messageCodeBtn").click(function () {
		//也需要确保手机号和密码已经输入
		$("#phone").blur();
		$("#loginPassword").blur();
		//要求手机号和密码已经填写		主方法里面不能执行ajax方法，但是可以判断页面标签是否满足要求
		if ($("#phoneErr").is(":hidden")&&$("#loginPasswordErr").is(":hidden")) {
			//验证没问题，获取手机号
			var phone =$.trim($("#phone").val());
			//因为要服务器判断验证码是否正确，需要后端发送请求
			//获得倒计时需要的当前对象
			var _this=$(this);
			if(!$(this).hasClass("on")) {
				$.get("/005-money-web/loan/page/messageCode", {"phone": phone}, function (data) {
					//这里面的当前对象是ajax对象
					//轻量级倒计时	需要倒计时插件，放到js包里面
					if (data.message == null || data.message == "") {
						alert("验证码发送失败，请重新操作")
						return;
					}
					alert(data.message)
					//验证码发送成功，开始倒计时
					$.leftTime(60, function (d) {
						if (d.status) {
							_this.addClass("on");
							_this.html((d.s == "00" ? "60" : d.s) + "秒后重新获取");
						} else {
							_this.removeClass("on");
							_this.html("获取验证码");
						}
					});
				});
			}
		}
	});
	//验证码失焦事件
	$("#messageCode").blur(function () {
		var messageCode = $.trim($(this).val());
		//1、不能为空
		if (messageCode == null || messageCode == "") {
			showError("messageCode","验证码不能为空")
			return;
		}
		//2、长度
		if (messageCode.length != 6) {
			showError("messageCode","验证码长度不正确")
			return;
		}
		//3、数字	全部用!是为了提高客户的体验
		if (!/^[0-9]*$/.test(messageCode)) {
			showError("messageCode","请输入正确的验证码")
			return;
		}
		//验证没问题
		showSuccess("messageCode");
	});

	//注册按钮点击事件
	$("#btnRegist").click(function () {
		//模拟事件触发	相当于调用其他方法		但是并不会收到我们的期望的返回值(true或者false)
	/*	//让手机号码失去焦点		返回的是phone当前的jquery对象  	jquery内置对象调用的失焦事件方法，才可以得到此方法的返回值
		var phone_ret = $("#phone").blur();
		//让密码失去焦点		返回的是loginPassword当前的jquery对象
		var loginPassword_ret = $("#loginPassword").blur();
		//页面正上方弹出对话框
		/!*alert(phone_ret);
		alert(loginPassword_ret);*!/
		//页面开发工具栏(console)弹出信息
		console.log(phone_ret);
		console.log(loginPassword_ret); */
		//需要得到返回值才能判断是否执行下一步,但是直接得不到返回值，可以定义追踪变量
		//但是执行主程序函数(方法)的时候，不会轮询调用ajax去操作变量，避免高并发，执行结束以后才调用ajax，所以目前这个方法不能判断手机号是否被注册过
		//模拟事件触发	返回的是当前的jquery对象	得不到返回值，就用追踪变量获取执行结果
		$("#phone").blur();
		$("#loginPassword").blur();
		$("#messageCode").blur();
		//手机号没有注册过，可以进行注册提交
		//判断号码、密码、验证码是否满足要求
		if ($("#phoneErr").is(":hidden")&&$("#loginPasswordErr").is(":hidden")&&$("#messageCodeErr").is(":hidden")) {
			//获取手机号
			var phone = $.trim($("#phone").val());
			//获取密码
			var loginPassword = $.trim($("#loginPassword").val());
			//获取验证码
			var messageCode = $.trim($("#messageCode").val());
			$.post("/005-money-web/loan/page/registerSubmit", {
				"phone": phone,
				"loginPassword": $.md5(loginPassword),"messageCode":messageCode
			}, function (data) {
				if (data.code == 1) {
					//验证成功，跳转到首页
					window.location.href = "/005-money-web/loan/page/realName"
				}
				if (data.code == 0) {
					alert(data.message);
				}
				//后面的json不加也可以
			}, "json");
		}
	})
});
