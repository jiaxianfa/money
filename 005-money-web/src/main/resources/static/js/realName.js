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

//同意实名认证协议
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
		//获取手机号
		var phone = $.trim($("#phone").val());
		//1、不能为空
		if (phone==null||phone=="") {
			showError("phone","请输入手机号")
			return;
		}
		//2、长度==11位
		if (phone.length != 11) {
			showError("phone","手机号长度有误");
			return;
		}
		//3、正则表达式 必须为数字
		if (!/^1[1-9]\d{9}$/.test(phone)) {
			showError("phone","您输入的手机号格式有误");
			return;
		}
		//实名认证的手机号码只是用来验证的，所以不需要查询手机号是否有重复
		showSuccess("phone");//不用显示提示信息
	});


	//真实姓名失焦事件
	$("#realName").blur(function () {
		//获得真实姓名
		var realName = $.trim($("#realName").val());
		//1、不能为空
		if (realName==null||realName=="") {
			showError("realName","请输入您的真实姓名")
			return;
		}
		//2、验证是否2-4位中文
		if (!/^[\u4e00-\u9fa5]{2,4}$/.test(realName)) {
			showError("realName","请输入您的真实姓名")
			return;
		}
		showSuccess("realName");
	});


	//身份证失焦事件
	$("#idCard").blur(function () {
		//获得身份证号码
		var idCard = $.trim($("#idCard").val());
		//1、不能为空
		if (idCard==null||idCard=="") {
			showError("idCard","请输入您的身份证号码")
			return;
		}
		//2、长度	身份证号码为15位或18位
		//满足其中一个就要执行
		if (idCard.length != 15 && idCard.length != 18) {
			showError("idCard","身份证号码长度不正确")
			return;
		}
		//3、正则表达式
		if (!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)) {
			showError("idCard","身份证号码格式有误")
			return;
		}
		//4、是否是真实身份证号码
		showSuccess("idCard")
	})
	//获取验证码点击事件
	$("#messageCodeBtn").click(function () {
		//模拟事件触发
		$("#phone").blur();
		$("#realName").blur();
		$("#idCard").blur();
		if ($("#phoneErr").is(":hidden")&&$("#realNameErr").is(":hidden")&&$("#idCardErr").is(":hidden")) {
			//获取手机号
			var phone = $.trim($("#phone").val());
			//获得倒计时的jquery对象
			var _this = $(this);
			if(!$(this).hasClass("on")) {
				//获取验证码
				$.get("/005-money-web/loan/page/messageCode", {"phone": phone}, function (data) {
					//得到的验证码
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
				})
			}
		}
	});
	var memessageCode_tag=0;
	//验证码失焦事件
	$("#messageCode").blur(function () {
		//获得身份证号码
		var messageCode = $.trim($("#messageCode").val());
		//1、不能为空
		if (messageCode==null||messageCode=="") {
			showError("messageCode","请输入验证码")
			return;
		}
		//2、长度	身份证号码为15位或18位
		if (messageCode.length != 6) {
			showError("messageCode","验证码长度不正确")
			return;
		}
		//3、数字
		if (!/^[0-9]*$/.test(messageCode)) {
			showError("messageCode","请输入正确的验证码")
			return;
		}
		showSuccess("messageCode");
		memessageCode_tag=1;
	});
	//认证客户信息
	$("#btnRegist").click(function () {
		//模拟事件触发
		$("#phone").blur();
		$("#realName").blur();
		$("#idCard").blur();
		$("#messageCode").blur();
		if ($("#phoneErr").is(":hidden")&&$("#realNameErr").is(":hidden")&&$("#idCardErr").is(":hidden")&&$("#messageCodeErr").is(":hidden")) {
			//获取身份证号、姓名、验证码、手机号
			var realName = $.trim($("#realName").val());
			var idCard = $.trim($("#idCard").val());
			var messageCode = $.trim($("#messageCode").val());
			var phone = $.trim($("#phone").val());
			//验证成功，发起异步认证的请求
			$.get("/005-money-web/loan/page/authentication",{"realName":realName,"idCard":idCard,"messageCode":messageCode,"phone":phone},function (data) {
				if (data.code==1) {
					alert()
					window.location.href=$("#ReturnUrlA").val();
				}
				if (data.code == 0) {
					alert(data.message);
				}
			})
		}
	});
});